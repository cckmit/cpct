#!/bin/bash
#define
export BUILD_ID=dontKillMe
cd `dirname "$0"`/..
TASKTRACKER_HOME=`pwd`
BACKEND="&";
#==============================================================================
pid=`ps -ef|grep -E $TASKTRACKER_HOME|grep -v grep|awk '{print $2}'`
if [ -n "$pid" ]; then
        kill -9 $pid
        echo "kill old TaskTracker processor "$pid", path="$TASKTRACKER_HOME
fi

if [ ! -d "./logs" ]; then
        mkdir ./logs
fi
#==============================================================================
#check JAVA_HOME & java
noJavaHome=false
if [ -z "$JAVA_HOME" ] ; then
    noJavaHome=true
fi
if [ ! -e "$JAVA_HOME/bin/java" ] ; then
    noJavaHome=true
fi
if $noJavaHome ; then
    echo
    echo "Error: JAVA_HOME environment variable is not set."
    echo
    exit 1
fi
#==============================================================================
#GC Log Options
JAVA_OPTS="$JAVA_OPTS -XX:+PrintGCApplicationStoppedTime"
JAVA_OPTS="$JAVA_OPTS -XX:+PrintGCTimeStamps"
JAVA_OPTS="$JAVA_OPTS -XX:+PrintGCDetails"
JAVA_OPTS="$JAVA_OPTS -Xloggc:${TASKTRACKER_HOME}/logs/gc.log"
#OOM dump
JAVA_OPTS="$JAVA_OPTS -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=${TASKTRACKER_HOME}/"
#debug Options
#JAVA_OPTS="$JAVA_OPTS -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=8805"
#==============================================================================
#set CLASSPATH
TASKTRACKER_CLASSPATH=".:$TASKTRACKER_HOME/conf:$TASKTRACKER_HOME/lib/*"
#==============================================================================
#startup
RUN_CMD="\"$JAVA_HOME/bin/java\""
RUN_CMD="$RUN_CMD -DappName=TaskTracker -DTASKTRACKER_HOME=\"$TASKTRACKER_HOME\""
RUN_CMD="$RUN_CMD -classpath \"$TASKTRACKER_CLASSPATH\""
RUN_CMD="$RUN_CMD $JAVA_OPTS"
RUN_CMD="$RUN_CMD com.ctg.dtts.tasktracker.TaskTrackerStartUp  $@"
RUN_CMD="$RUN_CMD >> \"$TASKTRACKER_HOME/logs/console.log\" 2>&1 $BACKEND "
echo $RUN_CMD
eval $RUN_CMD
echo $! > $TASKTRACKER_HOME/bin/tasktracker.pid
#==============================================================================
