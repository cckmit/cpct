@echo off
setlocal
set "TASKTRACKER_HOME=%cd%"
set JVM_OPTS="-server -Xms512m -Xmx512m -DTASKTRACKER_HOME=%TASKTRACKER_HOME%"
echo JVM_OPTS=%JVM_OPTS%
IF NOT EXIST "..\logs" MD "..\logs"
:start
java -showversion  "%JVM_OPTS%" -cp "..\lib\*;..\conf"  com.ctg.dtts.tasktracker.TaskTrackerStartUp
goto exit
:exit
endlocal
@echo on
pause