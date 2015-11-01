/**
 *
 * @author Sai Kumar Manakan
 * @email saikumar.manakan@mavs.uta.edu
 * @uta_id 1001236131
 *
 */

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Scanner;
import java.util.StringTokenizer;

public class HTTPRequest implements Runnable{

	private final Socket socket;
	private final int requestId;

	private String requestLine;
	private StringBuffer headerBuffer;
	private String requestMethod, requestResource, httpVersion = "HTTP/1.1";

	private boolean resourseExists = true;

	private BufferedReader bReader;
	private DataOutputStream output;

	private enum HTTPRequestMethod {
		HEAD, GET, POST, PUT, DELETE
	};

	private final String DELIM = " ";
	private final String CRLF = "\r\n";

	private String HTTP200 = httpVersion + DELIM + "200" + DELIM + "OK" + CRLF;
	private String HTTP404 = httpVersion + DELIM + "404" + DELIM + "Not Found" + CRLF;

	public HTTPRequest(Socket s, int id) {
		this.socket = s;
		this.requestId = id;

		logMessage("New Request Initiated [Id : " + id + "]");
	}

	/**
	 *
	 * Reads the HTTP request and sends appropriate HTTP response
	 *
	 */
	@Override
	public void run() {

		// Read the request headers
		boolean isRead = readRequestHeaders();

		if(isRead){
			// Parse the request line
			parseRequestLine();

			// Respond to the HTTP request
			switch(HTTPRequestMethod.valueOf(requestMethod)){
				case GET:
						logMessage("Getting requested resource : " + requestResource);
						getResourse();
					break;
				case POST:
					break;
				case PUT:
					break;
				case DELETE:
					break;
				case HEAD:
					break;
				default:
			}

			// Print the specs
			printSpecs();
		}

		// Finally, close all the streams and the socket
		try{
			bReader.close();
			socket.close();
			logMessage("Closed request [Id : " + requestId + "]");
		} catch(Exception e){
			logMessage("Error in closing socket/streams : " + e);
		}
	}

	/**
	 *
	 * Read the request headers
	 *
	 */
	private boolean readRequestHeaders(){

		bReader = null;

		// Read the HTTP request header
		try {
			String requestHeaderLines = null;
			headerBuffer = new StringBuffer();
			bReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

			requestLine = bReader.readLine();

			if(requestLine != null){
				logMessage("Processing request '" + requestLine + "' [Id : " + requestId + "]");

				headerBuffer.append(requestLine.trim() + "\n");

				while((requestHeaderLines = bReader.readLine()).length() != 0)
					headerBuffer.append(requestHeaderLines.trim() + "\n");

				logMessage("Header details : ");
				System.out.println(headerBuffer.toString());
			} else {
				logMessage("Nothing to read in this request. Closing the connection");
				return false;
			}
		} catch (Exception e) {
			logMessage("Error in reading header : " + e);
			return false;
		}

		return true;
	}

	/**
	 *
	 * Reads the requested resources and sends it to the client
	 *
	 */
	private void getResourse(){
		output = null;

		if(requestResource.equalsIgnoreCase("./")){
			getHeaderDetails();
			return;
		}

		try {
			String entityBody = getFileAsString(requestResource);
			output = new DataOutputStream(socket.getOutputStream());

			String contentType, responseLine;

			if(resourseExists){
				contentType = "Content-type : " + getContentType(requestResource) + CRLF;
				responseLine = HTTP200;
			} else {
				logMessage("Requested resource [" + requestResource + "] not found");
				contentType = "Content-type : " + getContentType("404.html") + CRLF;
				entityBody = String.format(entityBody, requestResource);
				responseLine = HTTP404;
			}

			output.write(responseLine.getBytes());
			output.write(contentType.getBytes());
			output.write(CRLF.getBytes());
			output.write(entityBody.getBytes());
		} catch (Exception e) {
			logMessage("Error in sending response : " + e);
		} finally{
			try {
				output.flush();
				output.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 *
	 *	Reads the ./ resource request and sends all the header information of the request
	 *	to the client
 	 *
	 */
	private void getHeaderDetails(){
		try {
			String outputData = getFileAsString("ReadHeaders.html");
			String entityBody = String.format(outputData, headerBuffer.toString());
			String contentType = "Content-type : " + getContentType(".html") + CRLF;

			output = new DataOutputStream(socket.getOutputStream());

			output.write(HTTP200.getBytes());
			output.write(contentType.getBytes());
			output.write(CRLF.getBytes());
			output.write(entityBody.getBytes());
		} catch (IOException e) {
		} finally{
			try {
				output.flush();
				output.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void printSpecs(){
		System.out.println("\n\n****************Client Details***********************");
		System.out.println("Request : " + requestLine);
		System.out.println("Remote Client Hostname : " + socket.getRemoteSocketAddress().toString());
		System.out.println("Protocol : TCP/IP");
		System.out.println("Socket Type : Connection Oriented");
		System.out.println("*****************************************************");
	}

	/**
	 *
	 * Parse the request line of the incoming request
	 *
	 */
	private void parseRequestLine(){
		StringTokenizer st = new StringTokenizer(requestLine, DELIM);
		String pathToken, resource;

		if(st.countTokens() == 3){
			requestMethod = st.nextToken();

			resource = st.nextToken().trim();
			pathToken = resource.startsWith("/") ? "." : "./";
			requestResource = pathToken + resource;

			httpVersion = st.nextToken();
		}
	}

	/**
	 *
	 * Read the requested file (resource) and return it as string
	 *
	 * @param fileName
	 * @return
	 */
	private String getFileAsString(String fileName){
		URL path = ClassLoader.getSystemResource(fileName);

		// Requested resource not found
		if(path == null) {
			resourseExists = false;
		    path = ClassLoader.getSystemResource("404.html");
		}

		File f = null;

		try {
			f = new File(path.toURI());
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		}

	    StringBuilder fileContents = new StringBuilder((int)f.length());
	    Scanner scanner = null;

		try {
			scanner = new Scanner(f);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	    String lineSeparator = System.getProperty("line.separator");

	    try {
	        while(scanner.hasNextLine()) {
	            fileContents.append(scanner.nextLine() + lineSeparator);
	        }
	        return fileContents.toString();
	    } finally {
	        scanner.close();
	    }
	}

	/**
	 *
	 * Return the content-type of the out going data
	 *
	 * @param fileName
	 * @return
	 */
	private String getContentType(String fileName){

		if(fileName.endsWith("html") || fileName.endsWith("htm"))
			return "text/html";

		return "application/octet-stream";
	}

	static SimpleDateFormat sdt = new SimpleDateFormat("[yyyy/MM/dd HH:mm:ss] : ");

	static void logMessage(String msg){
		Calendar c = Calendar.getInstance();
		System.out.println(sdt.format(c.getTime()) + msg);
	}
}
