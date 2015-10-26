import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class HTTPWebClient {

	private Socket httpSocket;

	private String host = "localhost";
	private int hostPort = 8080;
	private String requestedResource = "/";

	private final String DELIM = " ";
	private final String CRLF = "\r\n";

	private BufferedReader input = null;
	private DataOutputStream output = null;

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
			output.write(getRequestLine().getBytes());
			output.write(CRLF.getBytes());
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

				if(str != null)
					responseBuffer.append("\n" + str);
				else
					break;
			}

			System.out.println(responseBuffer.toString());
		} catch (IOException e) {
			logMessage("Error in reading response : " + e);
		}

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
