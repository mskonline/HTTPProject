import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class HTTPWebServer {

	private final ServerSocket server;

	int requestCount = 0;
	int requestId = 0;
	private int serverPort = 8080;

	public static void main(String[] args) {

		try {
			showWelcomeMessage();
			new HTTPWebServer(args);
		} catch (IOException e) {
			HTTPWebServer.logMessage("Error in starting the WebServer : "  + e);
		}
	}

	public HTTPWebServer(String[] args) throws IOException{

		if(args.length > 0){
			try{
				serverPort = Integer.parseInt(args[0].trim());
			} catch(NumberFormatException e){
				logMessage("Error in reading server port. Defaulting to 8080");
			}
		} else {
			System.out.println("Usage : startHTTPWebServer.bat <port>");
			logMessage("Defaulting to 8080");
		}

		server = new ServerSocket(serverPort);
		logMessage("WebServer Initiated. Listening to Port : " + serverPort);

		Runtime.getRuntime().addShutdownHook(new Thread()
		{
		    @Override
		    public void run()
		    {
		        try {
		        	HTTPWebServer.logMessage("Shutting down the WebServer. Total requests served : " + requestCount);
		        	server.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		    }
		});

		try {
			Socket incomingSocket = null;

			while(true){
				try {
					incomingSocket = server.accept();

					++requestId;

					HTTPRequest request = new HTTPRequest(incomingSocket,requestId);
					Thread requestThread = new Thread(request);
					requestThread.start();

					++requestCount;
				} catch(Exception e){
					logMessage("Exception : " + e);
				}
			}
		} finally {
			HTTPWebServer.logMessage("Shutting down the WebServer. Total requests served : " + requestCount);
			server.close();
		}
	}

	static SimpleDateFormat sdt = new SimpleDateFormat("[yyyy/MM/dd HH:mm:ss] : ");

	static void logMessage(String msg){

		Calendar c = Calendar.getInstance();
		System.out.println(sdt.format(c.getTime()) + msg);
	}

	static void showWelcomeMessage(){
		System.out.println("\n\n***************************************");
		System.out.println("Multi Threaded HTTP Web Server");
		System.out.println("Fall 15 - CS 5334 - Project 1 \n");
		System.out.println("by Sai Kumar Manakan <saikumar.manakan@mavs.uta.edu>");
		System.out.println("***************************************\n\n");
	}
}
