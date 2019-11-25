#!/bin/bash
#define
export BUILD_ID=dontKillMe
cd `dirname "$0"`/..
TASKTRACKER_HOME=`pwd`
echo "TASKTRACKER_HOME="$TASKTRACKER_HOME
pid=`ps -ef|grep -E $TASKTRACKER_HOME|grep -v grep|awk '{print $2}'`
if [ -n "$pid" ]; then
        kill -9 $pid
        echo "kill old TaskTracker processor "$pid", path="$TASKTRACKER_HOME
fi
