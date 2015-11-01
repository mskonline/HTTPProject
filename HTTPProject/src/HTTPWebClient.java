/**
 *
 * @author Sai Kumar Manakan
 * @email saikumar.manakan@mavs.uta.edu
 * @uta_id 1001236131
 *
 */

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class HTTPWebClient {

	private Socket httpSocket;

	private String host = "localhost";
	private int hostPort = 8080;
	private String requestedResource = "/";

	private final String CRLF = "\r\n";

	private BufferedReader input = null;
	private DataOutputStream output = null;

	long startTime, firstDataRecvTime = 0;

	public static void main(String[] args) {
		showWelcomeMessage();
		new HTTPWebClient(args);
	}

	/**
	 *
	 * @param args
	 */
	public HTTPWebClient(String[] args) {

		httpSocket = null;

		if(args.length >= 3){
			try{
				host = args[0].trim();
				hostPort = Integer.parseInt(args[1].trim());
				requestedResource = args[2].trim();
			} catch(NumberFormatException e){
				logMessage("Error in reading server port. Defaulting to 8080");
			}
		} else {
			System.out.println("Usage : startHTTPWebClient.bat <host> <port> <resourse>");
			logMessage("Defaulting to host:localhost. port:8080, resource:/");
		}

		// Open the socket
		try{
			httpSocket = new Socket(host, hostPort);
		} catch(Exception e){
			logMessage("Error in creating Socket : " + e);
			System.exit(0);
		}

		// Setup the streams
		setUp();

		// Send the HTTP request
		sendRequest();

		// Read the HTTP response
		readResponse();

		// Print the specifications
		printSpecs();

		// Finally close the socket and streams
		try {
			input.close();
			output.close();
			httpSocket.close();
		} catch (IOException e) {
			logMessage("Error in closing Socket : " + e);
			System.exit(0);
		}
	}

	/**
	 *
	 * Sets up the Input and Output streams
	 */
	private void setUp(){

		try{
			output = new DataOutputStream(httpSocket.getOutputStream());
			input = new BufferedReader(new InputStreamReader(httpSocket.getInputStream()));
		} catch(Exception e){
			logMessage("Error in setting up input/output streams : " + e);
		}

	}

	/**
	 *
	 * Sends a HTTP GET request to server
	 */
	private void sendRequest(){

		try {
			logMessage("Sending request : " + getRequestLine());

			output.write(getRequestLine().getBytes());
			output.write(CRLF.getBytes());
			startTime = System.currentTimeMillis();
		} catch (Exception e) {
			logMessage("Error in sending response : " + e);
		}
	}

	/**
	 *
	 * Read the HTTP response
	 *
	 */
	private void readResponse(){
		StringBuffer responseBuffer = new StringBuffer();

		try {
			while(true){
				String str = input.readLine();

				if(str != null){

					// Record the time of the first data
					if(firstDataRecvTime == 0)
						firstDataRecvTime = System.currentTimeMillis();

					responseBuffer.append("\n" + str);
				} else
					break;
			}

			logMessage("Received response :- ");
			System.out.println(responseBuffer.toString());
		} catch (IOException e) {
			logMessage("Error in reading response : " + e);
		}

	}

	DecimalFormat df = new DecimalFormat("#.0000000");

	private void printSpecs(){

		double RTT = (double)((firstDataRecvTime - startTime)/1000.0);

		System.out.println("\n\n*****************Server Details*****************");
		System.out.println("Remote Hostname : " + httpSocket.getRemoteSocketAddress().toString());
		System.out.println("RTT : " + df.format(RTT) + " secs");
		System.out.println("************************************************");
	}

	/**
	 *
	 * Constructs the request line
	 *
	 * @return
	 */
    private String getRequestLine(){
    	String rqLine = "GET " + requestedResource + " HTTP/1.0" + CRLF;
    	return rqLine;
    }


	static SimpleDateFormat sdt = new SimpleDateFormat("[yyyy/MM/dd HH:mm:ss] : ");

	static void logMessage(String msg){
		Calendar c = Calendar.getInstance();
		System.out.println(sdt.format(c.getTime()) + msg);
	}

	static void showWelcomeMessage(){
		System.out.println("\n\n***************************************");
		System.out.println("Simple HTTP Web Client");
		System.out.println("Fall 15 - CS 5334 - Project 1 \n");
		System.out.println("by Sai Kumar Manakan <saikumar.manakan@mavs.uta.edu>");
		System.out.println("***************************************\n\n");
	}
}
