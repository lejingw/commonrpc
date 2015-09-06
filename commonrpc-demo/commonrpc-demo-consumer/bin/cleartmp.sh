#!/bin/bash

cd `dirname $0`/..
BASE=`pwd`

. "$BASE/bin/setenv.sh"

if [ "$ISRUN" != "1"]; then
    rm -rf $BASE/logs
    rm -rf $BASE/temp
else
    echo "本环境还在运行，请结束后再执行本命令！"
fi