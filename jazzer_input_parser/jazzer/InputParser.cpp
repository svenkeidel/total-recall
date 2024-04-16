// Copyright 2021 Code Intelligence GmbH
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//
// Modified from
// https://raw.githubusercontent.com/google/atheris/034284dc4bb1ad4f4ab6ba5d34fb4dca7c633660/fuzzed_data_provider.cc
//
// Original license and copyright notices:
//
// Copyright 2020 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//
// Modified from
// https://github.com/llvm/llvm-project/blob/70de7e0d9a95b7fcd7c105b06bd90fdf4e01f563/compiler-rt/include/fuzzer/FuzzedDataProvider.h
//
// Original license and copyright notices:
//
//===- FuzzedDataProvider.h - Utility header for fuzz targets ---*- C++ -* ===//
//
// Part of the LLVM Project, under the Apache License v2.0 with LLVM Exceptions.
// See https://llvm.org/LICENSE.txt for license information.
// SPDX-License-Identifier: Apache-2.0 WITH LLVM-exception
//

#include "jazzer_InputParser.h"

#include <jni.h>
#include <algorithm>
#include <cstdint>
#include <limits>
#include <string>
#include <tuple>
#include <iostream>

// Polyfill for C++20 std::countl_one, which counts the number of leading ones
// in an unsigned integer.
inline __attribute__((always_inline)) uint8_t countl_one(uint8_t byte) {
  // The result of __builtin_clz is undefined for 0.
  if (byte == 0xFF) return 8;
  return __builtin_clz(static_cast<uint8_t>(~byte)) - 24;
}

// Forces a byte to be a valid UTF-8 continuation byte.
inline __attribute__((always_inline)) void ForceContinuationByte(
    uint8_t &byte) {
  byte = (byte | (1u << 7u)) & ~(1u << 6u);
}

constexpr uint8_t kTwoByteZeroLeadingByte = 0b11000000;
constexpr uint8_t kTwoByteZeroContinuationByte = 0b10000000;
constexpr uint8_t kThreeByteLowLeadingByte = 0b11100000;
constexpr uint8_t kSurrogateLeadingByte = 0b11101101;

enum class Utf8GenerationState {
  LeadingByte_Generic,
  LeadingByte_AfterBackslash,
  ContinuationByte_Generic,
  ContinuationByte_LowLeadingByte,
  FirstContinuationByte_LowLeadingByte,
  FirstContinuationByte_SurrogateLeadingByte,
  FirstContinuationByte_Generic,
  SecondContinuationByte_Generic,
  LeadingByte_LowSurrogate,
  FirstContinuationByte_LowSurrogate,
  SecondContinuationByte_HighSurrogate,
  SecondContinuationByte_LowSurrogate,
};

// Consumes up to `max_bytes` arbitrary bytes pointed to by `ptr` and returns a
// valid "modified UTF-8" string of length at most `max_length` that resembles
// the input bytes as closely as possible as well as the number of consumed
// bytes. If `stop_on_slash` is true, then the string will end on the first
// single consumed '\'.
//
// "Modified UTF-8" is the string encoding used by the JNI. It is the same as
// the legacy encoding CESU-8, but with `\0` coded on two bytes. In these
// encodings, code points requiring 4 bytes in modern UTF-8 are represented as
// two surrogates, each of which is coded on 3 bytes.
//
// This function has been designed with the following goals in mind:
// 1. The generated string should be biased towards containing ASCII characters
//    as these are often the ones that affect control flow directly.
// 2. Correctly encoded data (e.g. taken from the table of recent compares)
//    should be emitted unchanged.
// 3. The raw fuzzer input should be preserved as far as possible, but the
//    output must always be correctly encoded.
//
// The JVM accepts string in two encodings: UTF-16 and modified UTF-8.
// Generating UTF-16 would make it harder to fulfill the first design goal and
// would potentially hinder compatibility with corpora using the much more
// widely used UTF-8 encoding, which is reasonably similar to modified UTF-8. As
// a result, this function uses modified UTF-8.
//
// See Algorithm 1 of https://arxiv.org/pdf/2010.03090.pdf for more details on
// the individual cases involved in determining the validity of a UTF-8 string.
template <bool ascii_only, bool stop_on_backslash>
std::pair<std::string, jint> FixUpModifiedUtf8(const uint8_t *data,
                                               jint max_bytes,
                                               jint max_length) {
  std::string str;
  // Every character in modified UTF-8 is coded on at most six bytes. Every
  // consumed byte is transformed into at most one code unit, except for the
  // case of a zero byte which requires two bytes.
  if (ascii_only) {
    str.reserve(std::min(2 * static_cast<std::size_t>(max_length),
                         2 * static_cast<std::size_t>(max_bytes)));
  } else {
    str.reserve(std::min(6 * static_cast<std::size_t>(max_length),
                         2 * static_cast<std::size_t>(max_bytes)));
  }

  Utf8GenerationState state = Utf8GenerationState::LeadingByte_Generic;
  const uint8_t *pos = data;
  const auto data_end = data + max_bytes;
  for (jint length = 0; length < max_length && pos != data_end; ++pos) {
    uint8_t c = *pos;
    if (ascii_only) {
      // Clamp to 7-bit ASCII range.
      c &= 0x7Fu;
    }
    // Fix up c or previously read bytes according to the value of c and the
    // current state. In the end, add the fixed up code unit c to the string.
    // Exception: The zero character has to be coded on two bytes and is the
    // only case in which an iteration of the loop adds two code units.
    switch (state) {
      case Utf8GenerationState::LeadingByte_Generic: {
        switch (ascii_only ? 0 : countl_one(c)) {
          case 0: {
            // valid - 1-byte code point (ASCII)
            // The zero character has to be coded on two bytes in modified
            // UTF-8.
            if (c == 0) {
              str += static_cast<char>(kTwoByteZeroLeadingByte);
              c = kTwoByteZeroContinuationByte;
            } else if (stop_on_backslash && c == '\\') {
              state = Utf8GenerationState::LeadingByte_AfterBackslash;
              // The slash either signals the end of the string or is skipped,
              // so don't append anything.
              continue;
            }
            // Remain in state LeadingByte.
            ++length;
            break;
          }
          case 1: {
            // invalid - continuation byte at leader byte position
            // Fix it up to be of the form 0b110XXXXX and fall through to the
            // case of a 2-byte sequence.
            c |= 1u << 6u;
            c &= ~(1u << 5u);
            [[fallthrough]];
          }
          case 2: {
            // (most likely) valid - start of a 2-byte sequence
            // ASCII characters must be coded on a single byte, so we must
            // ensure that the lower two bits combined with the six non-header
            // bits of the following byte do not form a 7-bit ASCII value. This
            // could only be the case if at most the lowest bit is set.
            if ((c & 0b00011110u) == 0) {
              state = Utf8GenerationState::ContinuationByte_LowLeadingByte;
            } else {
              state = Utf8GenerationState::ContinuationByte_Generic;
            }
            break;
          }
          // The default case falls through to the case of three leading ones
          // coming right after.
          default: {
            // invalid - at least four leading ones
            // In the case of exactly four leading ones, this would be valid
            // UTF-8, but is not valid in the JVM's modified UTF-8 encoding.
            // Fix it up by clearing the fourth leading one and falling through
            // to the 3-byte case.
            c &= ~(1u << 4u);
            [[fallthrough]];
          }
          case 3: {
            // valid - start of a 3-byte sequence
            if (c == kThreeByteLowLeadingByte) {
              state = Utf8GenerationState::FirstContinuationByte_LowLeadingByte;
            } else if (c == kSurrogateLeadingByte) {
              state = Utf8GenerationState::
                  FirstContinuationByte_SurrogateLeadingByte;
            } else {
              state = Utf8GenerationState::FirstContinuationByte_Generic;
            }
            break;
          }
        }
        break;
      }
      case Utf8GenerationState::LeadingByte_AfterBackslash: {
        if (c != '\\') {
          // Mark the current byte as consumed.
          ++pos;
          goto done;
        }
        // A double backslash is consumed as a single one. As we skipped the
        // first one, emit the second one as usual.
        state = Utf8GenerationState::LeadingByte_Generic;
        ++length;
        break;
      }
      case Utf8GenerationState::ContinuationByte_LowLeadingByte: {
        ForceContinuationByte(c);
        // Preserve the zero character, which is coded on two bytes in modified
        // UTF-8. In all other cases ensure that we are not incorrectly encoding
        // an ASCII character on two bytes by setting the eighth least
        // significant bit of the encoded value (second least significant bit of
        // the leading byte).
        auto previous_c = static_cast<uint8_t>(str.back());
        if (previous_c != kTwoByteZeroLeadingByte ||
            c != kTwoByteZeroContinuationByte) {
          str.back() = static_cast<char>(previous_c | (1u << 1u));
        }
        state = Utf8GenerationState::LeadingByte_Generic;
        ++length;
        break;
      }
      case Utf8GenerationState::ContinuationByte_Generic: {
        ForceContinuationByte(c);
        state = Utf8GenerationState::LeadingByte_Generic;
        ++length;
        break;
      }
      case Utf8GenerationState::FirstContinuationByte_LowLeadingByte: {
        ForceContinuationByte(c);
        // Ensure that the current code point could not have been coded on two
        // bytes. As two bytes encode up to 11 bits and three bytes encode up
        // to 16 bits, we thus have to make it such that the five highest bits
        // are not all zero. Four of these bits are the non-header bits of the
        // leader byte. Thus, set the highest non-header bit in this byte (fifth
        // highest in the encoded value).
        c |= 1u << 5u;
        state = Utf8GenerationState::SecondContinuationByte_Generic;
        break;
      }
      case Utf8GenerationState::FirstContinuationByte_SurrogateLeadingByte: {
        ForceContinuationByte(c);
        if (c & (1u << 5u)) {
          // Start with a high surrogate (0xD800-0xDBFF). c contains the second
          // byte and the first two bits of the third byte. The first two bits
          // of this second byte are fixed to 10 (in 0x8-0xB).
          c |= 1u << 5u;
          c &= ~(1u << 4u);
          // The high surrogate must be followed by a low surrogate.
          state = Utf8GenerationState::SecondContinuationByte_HighSurrogate;
        } else {
          state = Utf8GenerationState::SecondContinuationByte_Generic;
        }
        break;
      }
      case Utf8GenerationState::FirstContinuationByte_Generic: {
        ForceContinuationByte(c);
        state = Utf8GenerationState::SecondContinuationByte_Generic;
        break;
      }
      case Utf8GenerationState::SecondContinuationByte_HighSurrogate: {
        ForceContinuationByte(c);
        state = Utf8GenerationState::LeadingByte_LowSurrogate;
        ++length;
        break;
      }
      case Utf8GenerationState::SecondContinuationByte_LowSurrogate:
      case Utf8GenerationState::SecondContinuationByte_Generic: {
        ForceContinuationByte(c);
        state = Utf8GenerationState::LeadingByte_Generic;
        ++length;
        break;
      }
      case Utf8GenerationState::LeadingByte_LowSurrogate: {
        // We have to emit a low surrogate leading byte, which is a fixed value.
        // We still consume a byte from the input to make fuzzer changes more
        // stable and preserve valid surrogate pairs picked up from e.g. the
        // table of recent compares.
        c = kSurrogateLeadingByte;
        state = Utf8GenerationState::FirstContinuationByte_LowSurrogate;
        break;
      }
      case Utf8GenerationState::FirstContinuationByte_LowSurrogate: {
        ForceContinuationByte(c);
        // Low surrogates are code points in the range 0xDC00-0xDFFF. c contains
        // the second byte and the first two bits of the third byte. The first
        // two bits of this second byte are fixed to 11 (in 0xC-0xF).
        c |= (1u << 5u) | (1u << 4u);
        // The second continuation byte of a low surrogate is not restricted,
        // but we need to track it differently to allow for correct backtracking
        // if it isn't completed.
        state = Utf8GenerationState::SecondContinuationByte_LowSurrogate;
        break;
      }
    }
    str += static_cast<uint8_t>(c);
  }

  // Backtrack the current incomplete character.
  switch (state) {
    case Utf8GenerationState::SecondContinuationByte_LowSurrogate:
      str.pop_back();
      [[fallthrough]];
    case Utf8GenerationState::FirstContinuationByte_LowSurrogate:
      str.pop_back();
      [[fallthrough]];
    case Utf8GenerationState::LeadingByte_LowSurrogate:
      str.pop_back();
      [[fallthrough]];
    case Utf8GenerationState::SecondContinuationByte_Generic:
    case Utf8GenerationState::SecondContinuationByte_HighSurrogate:
      str.pop_back();
      [[fallthrough]];
    case Utf8GenerationState::ContinuationByte_Generic:
    case Utf8GenerationState::ContinuationByte_LowLeadingByte:
    case Utf8GenerationState::FirstContinuationByte_Generic:
    case Utf8GenerationState::FirstContinuationByte_LowLeadingByte:
    case Utf8GenerationState::FirstContinuationByte_SurrogateLeadingByte:
      str.pop_back();
      [[fallthrough]];
    case Utf8GenerationState::LeadingByte_Generic:
    case Utf8GenerationState::LeadingByte_AfterBackslash:
      // No backtracking required.
      break;
  }

done:
  return std::make_pair(str, pos - data);
}

JNIEXPORT jstring JNICALL Java_jazzer_InputParser_parseString(JNIEnv *env, jclass klass, jbyteArray input) {
    const jint len = env->GetArrayLength(input);
    jbyte *signedData = new jbyte[len];
    env->GetByteArrayRegion(input, 0, len, signedData);
    const uint8_t *unsignedData = reinterpret_cast<const uint8_t *>(signedData);

    std::string str;
    jint consumed_bytes;
    std::tie(str, consumed_bytes) = FixUpModifiedUtf8<false, false>(unsignedData, len, std::numeric_limits<jint>::max());

    delete signedData;

    return env->NewStringUTF(str.c_str());
};