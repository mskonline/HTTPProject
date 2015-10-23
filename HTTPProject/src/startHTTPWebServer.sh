#!/bin/bash
if [ ! -f HTTPWebServer.class ]; then
	echo Compiling HTTPWebServer...
    javac HTTPWebServer.java HTTPRequest.java
fi
echo Starting HTTPWebServer...
java -cp . HTTPWebServer

