import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class HTTPWebServer {
	int requestCount = 0;
	int requestId = 0;

	public static void main(String[] args) {
		try {
			showWelcomeMessage();
			new HTTPWebServer();
		} catch (IOException e) {
			HTTPWebServer.logMessage("Error in starting the WebServer : "  + e);
		}
	}

	public HTTPWebServer() throws IOException{
		final ServerSocket server;

		server = new ServerSocket(54322);
		logMessage("WebServer Initiated. Listening to Port 54322");

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

					logMessage("New Request Initiated with Id : " + (++requestId));
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
