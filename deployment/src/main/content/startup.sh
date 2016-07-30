#!/bin/sh

java -d64 -server -XX:+UseG1GC -XX:+AggressiveOpts -XX:+UseLargePages -Xmn1g  -Xms6g -Xmx6g  -XX:MaxGCPauseMillis=2000 %DUMP% -cp .:lib/* -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005 ru.phi.modules.demo.OAuth2Application
