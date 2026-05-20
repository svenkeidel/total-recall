FROM ubuntu:latest

# Install build dependencies
RUN apt update
ARG DEBIAN_FRONTEND=noninteractive
RUN apt install -yq build-essential autoconf autoconf-archive libpqxx-dev libboost-regex-dev libsqlite3-dev git-all

# Clone repository and build
RUN git clone https://github.com/anse1/sqlsmith
RUN cd sqlsmith && \
    autoreconf -i && \
    ./configure && \
    make && \
    make install