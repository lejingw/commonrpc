#!/bin/bash

cd `dirname $0`/..
BASE=`pwd`
PROJECT_PATH=$BASE

. "$BASE/bin/setenv.sh"

#pushd `dirname $0`/.. > /dev/null
#BASE=`pwd`
#popd > /dev/null

if [ "$ISRUN" != "1" ]; then

    if [ ! -d "$BASE/logs/pids.log" ]; then
        mkdir -p $BASE/logs
        mkdir -p $BASE/temp
    fi

    echo "starting project ..."

    echo $CLASSPATH

    nohup $JAVA_HOME/bin/java -classpath $CLASSPATH com.commonrpc.demo.ConsumerStartup > $BASE/logs/stdout.log 2>&1 &
    echo $!> $BASE/logs/project.pid

    #$JAVA_HOME/bin/java -classpath $CLASSPATH com.commonrpc.demo.ConsumerStartup

else

    echo "project is running."

fi

#nohup java -server -Xmx6G -Xms6G -Xmn600M -XX:PermSize=50M -XX:MaxPermSize=50M -Xss256K -XX:+DisableExplicitGC -XX:SurvivorRatio=1 -XX:+UseConcMarkSweepGC -XX:+UseParNewGC -XX:+CMSParallelRemarkEnabled -XX:+UseCMSCompactAtFullCollection -XX:CMSFullGCsBeforeCompaction=0 -XX:+CMSClassUnloadingEnabled -XX:LargePageSizeInBytes=128M -XX:+UseFastAccessorMethods -XX:+UseCMSInitiatingOccupancyOnly -XX:CMSInitiatingOccupancyFraction=80 -XX:SoftRefLRUPolicyMSPerMB=0 -XX:+PrintClassHistogram -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -XX:+PrintHeapAtGC -Xloggc:gc.log -Djava.ext.dirs=lib com.test.server.HttpChunkedServer 8000 >server.out 2>&1 &