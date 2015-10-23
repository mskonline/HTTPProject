import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
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
	private String requestMethod, requestResource, httpVersion;

	private boolean resourseExists = true;

	private BufferedReader bReader;
	private DataOutputStream output;

	private enum HTTPRequestMethod {
		HEAD, GET, POST, PUT, DELETE
	};

	private final String DELIM = " ";
	private final String CRLF = "\r\n";

	private String HTTP200 = "HTTP/1.1 200 OK" + CRLF;
	private String HTTP404 = "HTTP/1.1 404 Not Found" + CRLF;

	public HTTPRequest(Socket s, int id) {
		this.socket = s;
		this.requestId = id;
	}

	@Override
	public void run() {
		bReader = null;

		// Read the HTTP request header
		try {
			String requestHeaderLines = null;
			headerBuffer = new StringBuffer();
			bReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

			requestLine = bReader.readLine();

			if(requestLine != null){
				logMessage("Processing request '" + requestLine + "' with Id : " + requestId);

				headerBuffer.append(requestLine.trim() + "\n");

				while((requestHeaderLines = bReader.readLine()).length() != 0)
					headerBuffer.append(requestHeaderLines.trim() + "\n");
			} else {
				return;
			}
		} catch (Exception e) {
			logMessage("Error in reading header : " + e);
			try {
				socket.close();
				return;
			} catch (Exception e1) {
				logMessage("Error in closing socket : " + e1);
			}
		}

		// Parse the request line
		parseRequestLine();

		// Respond to the HTTP request
		switch(HTTPRequestMethod.valueOf(requestMethod)){
			case GET:
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

		// Finally, close all the streams and the socket
		try{
			bReader.close();
			socket.close();
		} catch(Exception e){
			logMessage("Error in closing socket/streams : " + e);
		}
	}

	private void getResourse(){
		output = null;

		if(requestResource.equalsIgnoreCase("./")){
			outputHeaderLines();
			return;
		}

		try {
			String entityBody = getFileAsString(requestResource);
			output = new DataOutputStream(socket.getOutputStream());

			String contentType;

			if(resourseExists){
				contentType = "Content-type : " + getContentType(requestResource) + CRLF;
				output.write(HTTP200.getBytes());
			} else {
				contentType = "Content-type : " + getContentType("404.html")+ CRLF;
				output.write(HTTP404.getBytes());
			}

			output.write(contentType.getBytes());
			output.write(CRLF.getBytes());
			output.write(entityBody.getBytes());
		} catch (Exception e) {
			logMessage("Error in sending response : " + e);
		}
	}

	private void outputHeaderLines(){
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

	private void parseRequestLine(){
		StringTokenizer st = new StringTokenizer(requestLine, DELIM);

		if(st.countTokens() == 3){
			requestMethod = st.nextToken();
			requestResource = "." + st.nextToken();
			httpVersion = st.nextToken();
		}

		logMessage(requestMethod + " " + requestResource + " " + httpVersion);
	}

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
