# syntax = docker/dockerfile:latest
FROM maven:3-openjdk-17 AS builder

RUN microdnf install unzip

ARG XCORPUS_PROJECT_PATH
ARG PROJECT
ARG FUZZER
ARG FULL_PROJECT_PATH=/xcorpus/data/${XCORPUS_PROJECT_PATH}/project

# Copy project dependencies
COPY --from=xcorpus ${FULL_PROJECT_PATH}/default-lib/ /repo/
COPY --from=xcorpus ${FULL_PROJECT_PATH}/bin.zip /bin.zip
COPY --from=jazzer-input-parser /build/JazzerInputParser.jar /repo/
COPY --from=jazzer-input-parser /build/libJazzerInputParser.so /repo/

# Repackage zip as jar.
RUN unzip /bin.zip -d ${PROJECT} && \
    jar cf /repo/${PROJECT}.jar -C ${PROJECT} .

# Compile entrypoint
RUN --mount=type=bind,src=src/,target=src/ \
    javac -source 8 -target 8 -cp "/repo/*" -d target src/Entrypoint.java && \
    jar cf /repo/entrypoint.jar -C target . && \
    rm -rf target/*

# Compile fuzzer and package in a jar
RUN --mount=type=bind,src=src/,target=src/ \
    --mount=type=bind,src=target/jazzer-api.jar,target=lib/jazzer-api.jar \
    javac -source 8 -target 8 -cp "/repo/*:lib/jazzer-api.jar" -d target src/*Fuzzer.java && \
    jar cf /repo/fuzzer.jar -C target . && \
    rm -rf target/*

FROM scratch
COPY --from=builder /repo/ /repo/