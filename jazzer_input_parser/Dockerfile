FROM maven:3-openjdk-8

RUN apt-get update && apt-get -y install build-essential

WORKDIR /build

COPY jazzer jazzer

RUN javac -h jazzer jazzer/*.java && \
    g++ -c -fPIC -g -I${JAVA_HOME}/include -I${JAVA_HOME}/include/linux jazzer/InputParser.cpp -o InputParser.o && \
    g++ -shared -fPIC -g -o libJazzerInputParser.so InputParser.o && \
    mkdir -p jar/ && \
    cp -R jazzer jar/ && \
    jar cf JazzerInputParser.jar -C jar .