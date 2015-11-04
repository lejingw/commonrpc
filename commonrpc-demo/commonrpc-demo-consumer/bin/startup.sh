#!/bin/bash

cd `dirname $0`/..
BASE=`pwd`
PROJECT_PATH=$BASE

. "$BASE/bin/setenv.sh"

if [ "$ISRUN" != "1" ]; then

    if [ ! -d "$BASE/logs/pids.log" ]; then
        mkdir -p $BASE/logs
    fi

    echo "classpath:"
    echo "$CLASSPATH"
    echo "starting project ..."

    nohup $JAVA_HOME/bin/java -classpath $CLASSPATH com.commonrpc.demo.ConsumerStartup > $BASE/logs/stdout.log 2>&1 &
    echo $!> $BASE/logs/project.pid
    echo '' > $BASE/logs/stdout.log
    tail -f $BASE/logs/stdout.log

else

    echo "project is running."

fi
