#!/bin/bash

TIMEOUT=$1
MX=$2
shift 2

timeout --foreground $TIMEOUT \
  sbt -java-home /opt/jdk-25.0.3+9 \
      -J-Xmx${MX} \
      -J-Xss1000m \
      -Dplay.json.parser.maxNestingDepth=10000 \
      -Dcom.fasterxml.jackson.core.StreamReadConstraints.maxDepth=10000 \
    "; project jcg_evaluation; $*" \
  |& tee /log.txt