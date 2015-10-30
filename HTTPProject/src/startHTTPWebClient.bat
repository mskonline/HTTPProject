@ECHO OFF

IF NOT EXIST "HTTPWebClient.class" (
	ECHO Compiling HTTPWebClient...
	javac HTTPWebClient.java
)

ECHO Starting HTTPClient...
java -cp . HTTPWebClient %1 %2 %3