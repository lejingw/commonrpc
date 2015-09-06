#!/bin/bash

cd `dirname $0`/..
BASE=`pwd`
PROJECT_PATH=$BASE

. "$BASE/bin/setenv.sh"

if [ "$ISRUN" == "1" ]; then

    kill -9 `cat $BASE/logs/project.pid`

else

    echo "project is already shutdown!"

fi