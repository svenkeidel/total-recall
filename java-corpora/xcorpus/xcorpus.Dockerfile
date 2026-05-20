FROM maven:3.8.5-openjdk-8

# Clone xcorpus repository
RUN git clone https://bitbucket.org/jensdietrich/xcorpus/ /xcorpus-src

# Setup ant
RUN mkdir -p /xcorpus-src/ant && \
    unzip -q /xcorpus-src/misc/docker/ant.zip -d /xcorpus-src/ant
ENV ANT_HOME=/xcorpus-src/ant/apache-ant-1.10.1
ENV PATH=${PATH}:${ANT_HOME}/bin

# Install build utilities
RUN apt update && apt -y install zip

# Download missing dependencies
ARG AXION_PATH=/xcorpus-src/data/qualitas_corpus_20130901/axion-1.0-M2/project
RUN mvn dependency:copy -Dartifact=axion:axion:1.0-M2-dev -DoutputDirectory=${AXION_PATH} && \
    mv ${AXION_PATH}/axion-1.0-M2-dev.jar ${AXION_PATH}/bin.zip && \
    mvn dependency:copy -Dartifact=axion:axion:1.0-M2-dev:pom -DoutputDirectory=${AXION_PATH} && \
    mvn dependency:copy-dependencies -f ${AXION_PATH}/axion-1.0-M2-dev.pom -DoutputDirectory=${AXION_PATH}/default-lib/

ARG BATIK_PATH=/xcorpus-src/data/qualitas_corpus_20130901/batik-1.7/project
RUN wget https://repo1.maven.org/maven2/xmlbeans/xbean/2.2.0/xbean-2.2.0.jar -P${BATIK_PATH}/default-lib/
RUN --mount=type=bind,source=batik/,target=/xcorpus-src/data/qualitas_corpus_20130901/batik-1.7/project/patch/ \
    for JAR in $(ls ${BATIK_PATH}/patch); \
    do  \
      zip -d ${BATIK_PATH}/default-lib/${JAR} META-INF/MANIFEST.MF; \
      jar -uvfm ${BATIK_PATH}/default-lib/${JAR} ${BATIK_PATH}/patch/${JAR}/META-INF/MANIFEST.MF;  \
    done


ARG FINDBUGS_PATH=/xcorpus-src/data/qualitas_corpus_20130901/findbugs-1.3.9/project
RUN wget https://github.com/JavaQualitasCorpus/findbugs-1.3.9/raw/master/findbugs-1.3.9.jar -O${FINDBUGS_PATH}/bin.zip && \
    rm -rf default-lib/ && \
    wget https://github.com/JavaQualitasCorpus/findbugs-1.3.9/raw/master/lib/AppleJavaExtensions.jar -P${FINDBUGS_PATH}/default-lib/ && \
    wget https://github.com/JavaQualitasCorpus/findbugs-1.3.9/raw/master/lib/ant.jar -P${FINDBUGS_PATH}/default-lib/ && \
    wget https://github.com/JavaQualitasCorpus/findbugs-1.3.9/raw/master/lib/asm-3.1.jar -P${FINDBUGS_PATH}/default-lib/ && \
    wget https://github.com/JavaQualitasCorpus/findbugs-1.3.9/raw/master/lib/asm-analysis-3.1.jar -P${FINDBUGS_PATH}/default-lib/ && \
    wget https://github.com/JavaQualitasCorpus/findbugs-1.3.9/raw/master/lib/asm-commons-3.1.jar -P${FINDBUGS_PATH}/default-lib/ && \
    wget https://github.com/JavaQualitasCorpus/findbugs-1.3.9/raw/master/lib/asm-tree-3.1.jar -P${FINDBUGS_PATH}/default-lib/ && \
    wget https://github.com/JavaQualitasCorpus/findbugs-1.3.9/raw/master/lib/asm-util-3.1.jar -P${FINDBUGS_PATH}/default-lib/ && \
    wget https://github.com/JavaQualitasCorpus/findbugs-1.3.9/raw/master/lib/asm-xml-3.1.jar -P${FINDBUGS_PATH}/default-lib/ && \
    wget https://repo1.maven.org/maven2/asm/asm-attrs/2.2.3/asm-attrs-2.2.3.jar -P${FINDBUGS_PATH}/default-lib/ && \
    wget https://github.com/JavaQualitasCorpus/findbugs-1.3.9/raw/master/lib/bcel.jar -P${FINDBUGS_PATH}/default-lib/ && \
    wget https://github.com/JavaQualitasCorpus/findbugs-1.3.9/raw/master/lib/commons-lang-2.4.jar -P${FINDBUGS_PATH}/default-lib/ && \
    wget https://github.com/JavaQualitasCorpus/findbugs-1.3.9/raw/master/lib/dom4j-1.6.1.jar -P${FINDBUGS_PATH}/default-lib/ && \
    wget https://github.com/JavaQualitasCorpus/findbugs-1.3.9/raw/master/lib/jFormatString.jar -P${FINDBUGS_PATH}/default-lib/ && \
    wget https://github.com/JavaQualitasCorpus/findbugs-1.3.9/raw/master/lib/jaxen-1.1.1.jar -P${FINDBUGS_PATH}/default-lib/ && \
    wget https://github.com/JavaQualitasCorpus/findbugs-1.3.9/raw/master/lib/jdepend-2.9.jar -P${FINDBUGS_PATH}/default-lib/ && \
    wget https://github.com/JavaQualitasCorpus/findbugs-1.3.9/raw/master/lib/jsr305.jar -P${FINDBUGS_PATH}/default-lib/ && \
    wget https://github.com/JavaQualitasCorpus/findbugs-1.3.9/raw/master/lib/junit.jar -P${FINDBUGS_PATH}/default-lib/ && \
    wget https://github.com/JavaQualitasCorpus/findbugs-1.3.9/raw/master/lib/mysql-connector-java-5.1.7-bin.jar -P${FINDBUGS_PATH}/default-lib/

RUN --mount=type=bind,source=findbugs/,target=/xcorpus-src/data/qualitas_corpus_20130901/findbugs-1.3.9/project/patch/ \
    for JAR in $(ls ${FINDBUGS_PATH}/patch); \
    do \
       zip -d ${FINDBUGS_PATH}/default-lib/${JAR} META-INF/MANIFEST.MF; \
       jar -uvfm ${FINDBUGS_PATH}/default-lib/${JAR} ${FINDBUGS_PATH}/patch/${JAR}/META-INF/MANIFEST.MF;  \
    done

ARG XERCES_PATH=/xcorpus-src/data/qualitas_corpus_20130901/xerces-2.10.0/project
RUN unzip -q ${XERCES_PATH}/bin.zip -d ${XERCES_PATH}/jar/ && \
    rm -rf ${XERCES_PATH}/jar/xercesSamples && \
    mv ${XERCES_PATH}/jar/xercesImpl/* ${XERCES_PATH}/jar/ && \
    rmdir ${XERCES_PATH}/jar/xercesImpl/ && \
    jar cf ${XERCES_PATH}/bin.zip -C ${XERCES_PATH}/jar/ .


ARG JAVACC_PATH=/xcorpus-src/data/qualitas_corpus_20130901/javacc-5.0/project
RUN wget https://github.com/JavaQualitasCorpus/javacc-5.0/raw/master/lib/junit3.8.1/junit.jar \
    -P${JAVACC_PATH}/default-lib/
RUN wget https://github.com/JavaQualitasCorpus/javacc-5.0/raw/master/javacc-5.0.jar \ 
-O${JAVACC_PATH}/bin.zip 


ARG HSQL_PATH=/xcorpus-src/data/qualitas_corpus_20130901/hsqldb-2.0.0/project
RUN mkdir -p ${HSQL_PATH}/default-lib 

RUN wget https://github.com/JavaQualitasCorpus/hsqldb-2.0.0/raw/refs/heads/master/hsqldb-2.0.0.jar -O${HSQL_PATH}/bin.zip && \
    wget https://github.com/JavaQualitasCorpus/hsqldb-2.0.0/raw/refs/heads/master/hsqldb/lib/sqltool.jar -P${HSQL_PATH}/default-lib/ && \
    wget https://github.com/JavaQualitasCorpus/hsqldb-2.0.0/raw/refs/heads/master/lib/com.springsource.org.apache.tools.ant-1.8.1.jar -P${HSQL_PATH}/default-lib/ && \
    wget https://github.com/JavaQualitasCorpus/hsqldb-2.0.0/raw/refs/heads/master/lib/hibernate-3.6.6.jar -P${HSQL_PATH}/default-lib/ && \
    wget https://github.com/JavaQualitasCorpus/hsqldb-2.0.0/raw/refs/heads/master/lib/javax.servlet.jar -P${HSQL_PATH}/default-lib/ && \
    wget https://github.com/JavaQualitasCorpus/hsqldb-2.0.0/raw/refs/heads/master/lib/slf4j-api.jar -P${HSQL_PATH}/default-lib/



# Ensure project/default-lib directories are present
RUN find /xcorpus-src/data -name 'project' -type d -exec mkdir -p {}/default-lib \;

# Setup xcorpus
RUN cd /xcorpus-src/tools && \
    ant release && \
    unzip -q /xcorpus-src/xcorpus-1.0.0.zip -d /xcorpus

# Clean up
# RUN rm -rf /xcorpus-src

WORKDIR /xcorpus


#ARG JAVACC_PATH=/xcorpus-src/data/qualitas_corpus_20130901/javacc-5.0/project
#RUN wget https://repo1.maven.org/maven2/javacc/javacc/4.0/javacc-4.0.jar -P${JAVACC_PATH}/default-lib/ && \
#    wget https://repo1.maven.org/maven2/javacc/javacc/4.0/javacc-4.0-sources.jar -P${JAVACC_PATH}/default-lib/ && \
#    wget https://repo1.maven.org/maven2/javacc/javacc/4.0/javacc-4.0.pom -P${JAVACC_PATH}/default-lib/
#RUN --mount=type=bind,source=javacc/,target=/xcorpus-src/data/qualitas_corpus_20130901/javacc-5.0/project/patch/ \
#    for JAR in $(ls ${JAVACC_PATH}/patch); \
#    do \
#       zip -d ${JAVACC_PATH}/default-lib/${JAR} META-INF/MANIFEST.MF; \
#       jar -uvfm ${JAVACC_PATH}/default-lib/${JAR} ${JAVACC_PATH}/patch/${JAR}/META-INF/MANIFEST.MF;  \
#    done

#ARG JAVACC_PATH=/xcorpus-src/data/qualitas_corpus_20130901/javacc-5.0/project
#RUN mvn dependency:copy -Dartifact=javacc:javacc:4.0 -DoutputDirectory=${JAVACC_PATH} && \
#    mv ${JAVACC_PATH}/javacc-4.0.jar ${JAVACC_PATH}/bin.zip && \
#    mvn dependency:copy -Dartifact=javacc:javacc:4.0:pom -DoutputDirectory=${JAVACC_PATH} && \
#    mvn dependency:copy-dependencies -f ${JAVACC_PATH}/javacc-4.0.pom -DoutputDirectory=${JAVACC_PATH}/default-lib/