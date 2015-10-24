import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
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

	private BufferedReader bReader;
	private DataOutputStream output;

	public static void main(String[] args) {
		showWelcomeMessage();
		new HTTPWebClient(args);
	}

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

		try{
			httpSocket = new Socket(host, hostPort);
			sendRequest();
		} catch(Exception e){
			logMessage("Error in creating Socket : " + e);
		} finally{
			try {
				httpSocket.close();
			} catch (IOException e) {
				logMessage("Error in closing Socket : " + e);
			}
		}
	}

	private void sendRequest(){
		output = null;

		try {

			output = new DataOutputStream(httpSocket.getOutputStream());

			output.write(CRLF.getBytes());
		} catch (Exception e) {
			logMessage("Error in sending response : " + e);
		}
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
