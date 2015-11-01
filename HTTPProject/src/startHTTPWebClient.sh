#!/bin/bash
if [ ! -f HTTPWebClient.class ]; then
	echo Compiling HTTPWebClient...
    javac HTTPWebClient.java
fi
echo Starting HTTPWebClient...
java -cp . HTTPWebClient $1 $2 $3