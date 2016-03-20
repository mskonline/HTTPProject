# HTTPProject

################################################################
README

Simple HTTP Web Server and HTTP Client
UTA FALL 15 - CSE 5334 003 Project 1


Author(s) :-
	Sai Kumar Manakan
	Email : <saikumar.manakan@mavs.uta.edu>
	UTA ID : 1001236131

Submitted On: 10/31/2015

################################################################

Contents :-

1. Project Description [Ln 27]
2. Installation [Ln 34]
3. Run [Ln 40]
4. Files [Ln 62]
5. References [Ln 82]

##################################################

1. Project Description

This project implements a simple multi threaded HTTP Server and a simple HTTP client using
the TCP/IP protocol provided by Java's Socket Implementation

##################################################

2. Installation

Unzip the 1001236131_Sai_Kumar_Manakan.zip to your desired location.

##################################################

3. Run

Prerequisite : Java 1.6 (or higher) needs to be installed in your system. The shell scripts
	which are run below will build the source files.

Server:
Navigate to <Project_Folder>/src/ and type the following command :-

	startHTTPWebServer.bat <port_number> // On Windows
	or
	sh startHTTPWebServer.sh <port_number> // On *nix

Client:

Navigate to <Project_Folder>/src/ and type the following command :-

	startHTTPWebClient.bat <host_name> <host_port> <resource> // On Windows
		or
	sh startHTTPWebClient.sh <host_name> <host_port> <resource> // On *nix

##################################################

4. Files

	The Implementation of the Server can be found in the following Java Files

		<Project_Folder>/src/HTTPWebServer.java
		<Project_Folder>/src/HTTPRequest.java

	The Implementation of the Client can be found in the following Java File

		<Project_Folder>/src/HTTPWebClient.java

	Other files :

	<Project_Folder>/src/ReadHeaders.html : Displays the HTTP Header information, when the request is made from a 		        browser
		<Project_Folder>/src/404.html : Displays a 404 error detail
		<Project_Folder>/src/Index.html : Displays a sample index page

##################################################

5. References :

	a) https://docs.oracle.com/javase/tutorial/networking/sockets/readingWriting.html
	b) Programming Assignment 1_reference_Java.pdf :-
		http://crystal.uta.edu/~datta/teaching/cse5344-3/Programming%20Assignment%201_reference_Java.pdf

##################################################

END OF README

##################################################
