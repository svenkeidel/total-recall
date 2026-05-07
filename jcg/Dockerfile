FROM ubuntu:24.04

# Set user and group
ARG USER=JCG
ARG GROUP=users
ARG UID=1000
ARG GID=100

# Delete user and group to ensure they do not exist
RUN OLD_USER=$(awk -F\: "\$3 == ${UID} {print \$1}" /etc/passwd); if [ ! -z "$OLD_USER" ]; then echo "delete user $OLD_USER"; userdel $OLD_USER; fi
RUN OLD_GROUP=$(awk -F\: "\$3 == ${GID} {print \$1}" /etc/group); if [ ! -z "$OLD_GROUP" ]; then echo "delete group $OLD_GROUP"; groupdel $OLD_GROUP; fi
RUN groupdel $GROUP || true
# Add user and group
RUN groupadd -g ${GID} ${GROUP}
RUN useradd -u ${UID} -g ${GID} -s /bin/sh ${USER}
RUN mkdir -p /home/${USER}
RUN chown ${USER}:${GROUP} /home/${USER}

##
# INSTALL PRELIMINARY TOOLS
RUN apt-get update && \
    DEBIAN_FRONTEND=noninteractive \
    apt-get -y install sudo apt-transport-https ca-certificates software-properties-common apt-utils  \
                       git curl less nano vim zip zlib1g-dev wget python3.5 unzip aptitude make gcc g++ \
                       libtool libncurses5-dev libopenmpi-dev openmpi-bin libsqlite3-dev libffi-dev mcpp \
                       flex bison clang build-essential cmake doxygen sqlite3

# Disable sudo password prompt for all users
RUN echo "${USER} ALL=(ALL) NOPASSWD:ALL" >> /etc/sudoers
# Add user to sudo group
RUN adduser ${USER} sudo

USER ${UID}:${GID}

# Ensure OpenJDK is not installed
RUN sudo apt-get -y autoremove openjdk* && \
    sudo apt-get -y purge openjdk*

ARG JDK_DIR="/home/${USER}/jdk"
# Install OpenJDK 8 version 342
RUN mkdir -p ${JDK_DIR} && \
    cd ${JDK_DIR} && \
    wget https://github.com/adoptium/temurin8-binaries/releases/download/jdk8u342-b07/OpenJDK8U-jdk_x64_linux_hotspot_8u342b07.tar.gz && \
    sudo tar xvf $JDK_DIR/OpenJDK8U-jdk_x64_linux_hotspot_8u342b07.tar.gz -C /opt

# Install OpenJDK 26
RUN cd ${JDK_DIR} && \
    wget https://github.com/adoptium/temurin26-binaries/releases/download/jdk-26%2B35/OpenJDK26U-jdk_x64_linux_hotspot_26_35.tar.gz && \
    sudo tar xvf $JDK_DIR/OpenJDK26U-jdk_x64_linux_hotspot_26_35.tar.gz -C /opt

# Make OpenJDK 26 the default and use it for building \
RUN sudo update-alternatives --install /usr/bin/java java /opt/jdk-26+35/bin/java 1000 && \
    sudo update-alternatives --install /usr/bin/javac javac /opt/jdk-26+35/bin/javac 1000 && \
    sudo update-alternatives --install /usr/bin/jar jar /opt/jdk-26+35/bin/jar 1000

## install sbt
RUN echo "deb https://repo.scala-sbt.org/scalasbt/debian all main" | sudo tee /etc/apt/sources.list.d/sbt.list && \
    echo "deb https://repo.scala-sbt.org/scalasbt/debian /" | sudo tee /etc/apt/sources.list.d/sbt_old.list && \
    sudo apt-key adv --keyserver hkps://keyserver.ubuntu.com:443 --recv 2EE0EA64E40A89B84B2DF73499E82A75642AC823 && \
    sudo apt-get update && \
    sudo apt-get install sbt


## install OPAL
#ARG OPAL_BRANCH="update-eclipse-ecj-version"
#ARG OPAL_COMMIT_ID="a43b9de23adf1bfc7aee122262fdbcbee6c8503a"
#ARG OPAL_DIR="/home/$USER/OPAL"
#RUN git clone https://github.com/stg-tud/opal.git ${OPAL_DIR} && \
#    cd ${OPAL_DIR} && \
#    git checkout ${OPAL_BRANCH} && \
#    git reset --hard ${OPAL_COMMIT_ID} && \
#    sbt publishLocal

## INSTALL SOUFFLE (04.05.2026)
ARG SOUFFLE_CC="gcc"
ARG SOUFFLE_CXX="g++"
ARG SOUFFLE_CONFIGURE_OPTIONS=""
ARG SOUFFLE_GITHUB_USER="souffle-lang"
ARG SOUFFLE_GIT_BRANCH="master"
ARG SOUFFLE_GIT_REVISION="c3861e0d3b82dcbcfce14a7bc8f434e70868bef9"
ARG SOUFFLE_MAKE_JOBS="2"
ARG SOUFFLE_DIR="/home/$USER/souffle"

ENV CC="${SOUFFLE_CC}"
ENV CXX="${SOUFFLE_CXX}"

RUN git clone https://github.com/${SOUFFLE_GITHUB_USER}/souffle $SOUFFLE_DIR && \
    cd $SOUFFLE_DIR && \
    git checkout ${SOUFFLE_GIT_BRANCH} && \
    git reset --hard ${SOUFFLE_GIT_REVISION} && \
    git clean -xdf && \
    cmake -S . -B build -DCMAKE_BUILD_TYPE=Release -DSOUFFLE_DOMAIN_64BIT=ON -DCMAKE_INSTALL_PREFIX=${SOUFFLE_DIR} && \
    cmake --build build -j8 --target install
RUN sudo ln -s ${SOUFFLE_DIR}/bin/souffle /usr/bin/souffle && \
    sudo ln -s ${SOUFFLE_DIR}/bin/souffleprof /usr/bin/souffleprof && \
    sudo ln -s ${SOUFFLE_DIR}/bin/souffle-compile.py /usr/bin/souffle-compile.py && \
    souffle -h

## INSTALL DOOP (23.04.2026)
ARG DOOP_COMMIT_ID="98f9ead0e96e41dc2a622c82a6d0e158e6631419"
ARG DOOP_BRANCH="master"
ARG DOOP_DIR="/home/$USER/doop"
ENV DOOP_HOME="$DOOP_DIR"
RUN git clone https://github.com/plast-lab/doop $DOOP_DIR && \
    cd $DOOP_DIR && \
    git checkout ${DOOP_BRANCH} && \
    git reset --hard ${DOOP_COMMIT_ID} && \
    ./doop -h

## install Coursier (dependency of JCG)
ARG COURSIER_DIR="/home/$USER/coursier"
RUN mkdir -p ${COURSIER_DIR} && \
    cd ${COURSIER_DIR} && \
    curl -fL https://github.com/coursier/coursier/releases/latest/download/cs-x86_64-pc-linux.gz | gzip -d > cs && \
    chmod +x cs && \
    sudo ln -s ${COURSIER_DIR}/cs /usr/bin/cs && \
    cs --version

## install JCG (07.05.2026)
ARG JCG_BRANCH="develop"
ARG JCG_COMMIT_ID="e0ee5e6fe292ce733290fc9b328c729665dcbad5"
ARG JCG_DIR="/home/$USER/JCG"
RUN git clone https://github.com/svenkeidel/JCG.git $JCG_DIR && \
    cd $JCG_DIR && \
    git checkout ${JCG_BRANCH} && \
    git reset --hard ${JCG_COMMIT_ID} && \
    sbt compile

# Install OpenJDK 8 version 342
COPY jre.conf $JCG_DIR/jre.conf


WORKDIR $JCG_DIR

COPY entrypoint.sh /entrypoint.sh
RUN sudo chmod a+x /entrypoint.sh
ENTRYPOINT [ "/entrypoint.sh" ]
