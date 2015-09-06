#!/bin/bash

BASE=$PROJECT_PATH

if [ -z "$PROJECT_PATH" ] ; then
	cd `dirname $0`/..
	BASE=`pwd`
fi

PROJECT=demo-provider1
#PROJECT=demo-provider2

CLASSPATH=`echo $JAVA_HOME/lib/*.jar | tr ' ' ':'`
CLASSPATH=$CLASSPATH:`echo $BASE/libs/*.jar | tr ' ' ':'`

ISRUN=$(ps ux|grep "/$PROJECT/"|grep java |wc -l)

JAVA_CONF="-Xmx256M -Dfile.encoding=UTF-8"