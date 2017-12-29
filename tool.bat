@echo off

set baseDir=%~dp0

rem Check the JAVA_HOME directory
if not "%JAVA_HOME%" == "" goto gotJavaHome
set EXEC=java
goto noJavaHome
:gotJavaHome
set EXEC=%JAVA_HOME%\bin\java
:noJavaHome

set CP=%CP%;%baseDir%/third-party/commons-codec-1.4.jar
set CP=%CP%;%baseDir%/third-party/commons-logging-1.1.1.jar
set CP=%CP%;%baseDir%/third-party/httpclient-4.1.1.jar
set CP=%CP%;%baseDir%/third-party/httpcore-4.1.jar
set CP=%CP%;%baseDir%/third-party/jackson-core-asl-1.8.7.jar
set CP=%CP%;%baseDir%/third-party/joda-time-2.1.jar
set CP=%CP%;%baseDir%/third-party/log4j-1.2.17.jar
set CP=%CP%;%baseDir%/third-party/nos-sdk-java-publiccloud-0.0.3.jar

"%EXEC%" -classpath "%CP%" com.netease.cloud.services.nos.tools.noscmd %*

:END