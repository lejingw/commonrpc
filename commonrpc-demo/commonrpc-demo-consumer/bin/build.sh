#!/bin/bash

cd `dirname $0`/..
BASE=`pwd`

git pull

mvn clean package -Dmaven.test.skip=true