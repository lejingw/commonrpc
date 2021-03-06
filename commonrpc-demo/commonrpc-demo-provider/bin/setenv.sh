#!/bin/bash

BASE=$PROJECT_PATH

if [ -z "$PROJECT_PATH" ] ; then
	cd `dirname $0`/..
	BASE=`pwd`
fi


CLASSPATH=$JAVA_HOME/lib/*:$BASE/conf:$BASE/libs/*

PROJECT_DIR_NAME=`basename \`pwd\``

ISRUN=$(ps ux|grep "/$PROJECT_DIR_NAME/"|grep java |wc -l| sed 's/ //g')

JAVA_CONF="-Xmx256M -Dfile.encoding=UTF-8"