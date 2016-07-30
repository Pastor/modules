#!/bin/sh

java -cp *:lib/* -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005 ru.phi.modules.demo.OAuth2Application
