# syntax = docker/dockerfile:latest
FROM jcg AS builder

COPY --chown=JCG:users repo/ /repo
COPY --from=jazzer-input-parser /build/JazzerInputParser.jar /repo/
COPY --from=jazzer-input-parser /build/libJazzerInputParser.so /repo/

ENV JDK=/opt/jdk-25.0.3+9/

# Compile entrypoint
RUN --mount=type=bind,src=src/,target=src/ \
    $JDK/bin/javac -cp "/repo/*" -d target src/Entrypoint.java && \
    $JDK/bin/jar cf /repo/entrypoint.jar -C target . && \
    rm -rf target/*

# Compile fuzzer and package in a jar
RUN --mount=type=bind,src=src/,target=src/ \
    --mount=type=bind,src=target/jazzer-api.jar,target=lib/jazzer-api.jar \
    $JDK/bin/javac -cp "/repo/*:lib/jazzer-api.jar" -d target src/*Fuzzer.java && \
    $JDK/bin/jar cf /repo/fuzzer.jar -C target . && \
    rm -rf target/*

RUN --mount=type=bind,src=target/adjust_line_numbers_to_pc.jar,target=lib/adjust_line_numbers_to_pc.jar \
    find /repo -name '*.jar' -exec java -jar lib/adjust_line_numbers_to_pc.jar {} \;


FROM scratch
COPY --from=builder /repo/ /