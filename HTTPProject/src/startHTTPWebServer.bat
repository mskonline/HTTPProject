@ECHO OFF

IF NOT EXIST "HTTPWebServer.class" (
	ECHO Compiling HTTPWebServer...
	javac HTTPWebServer.java HTTPRequest.java
)

ECHO Starting HTTPWebServer...
java -cp . HTTPWebServer %1