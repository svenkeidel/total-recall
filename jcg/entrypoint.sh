#!/bin/sh

TIMEOUT=$1
MX=$2
shift 2

timeout --foreground $TIMEOUT \
  sbt -java-home /opt/jdk-25.0.3+9 -J-Xmx${MX} \
    "; project jcg_evaluation; $*" \
  | tee /log.txt