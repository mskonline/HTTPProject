import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.StringTokenizer;

public class HTTPRequest implements Runnable{

	private final Socket socket;
	private final int requestId;

	private String requestLine;
	private String requestMethod, requestResource, httpVersion;

	private enum HTTPRequestMethod {
		GET, POST, PUT, DELETE
	};

	private final String DELIM = " ";

	public HTTPRequest(Socket s, int id) {
		this.socket = s;
		this.requestId = id;
	}

	@Override
	public void run() {
		BufferedReader bReader = null;

		// Read the HTTP request header
		try {
			String requestHeaderLines = null;
			StringBuffer headerBuffer = new StringBuffer();
			bReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

			requestLine = bReader.readLine();

			if(requestLine != null){
				logMessage("Processing request '" + requestLine + "' with Id : " + requestId);

				headerBuffer.append("\t" + requestLine + "\n");

				while((requestHeaderLines = bReader.readLine()).length() != 0)
					headerBuffer.append("\t" + requestHeaderLines + "\n");

				System.out.println(headerBuffer.toString());
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
		} finally {
			try {
				bReader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

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
			default:
		}
	}

	private void getResourse(){
		DataOutputStream output = null;

		File f = new File(requestResource);

		if(!f.exists()){
			// 404 - Requested resource not found
			f = new File("404.html");
		}

		try {
			output = new DataOutputStream(socket.getOutputStream());
			FileInputStream fs = new FileInputStream(f);


		} catch (Exception e) {
			logMessage("Error in sending response : " + e);
		} finally{
			try {
				output.flush();
				output.close();
				socket.close();
				logMessage("Closing request with Id : " + requestId);
			} catch (Exception e) {
				logMessage("Error in closing socket : " + e);
			}
		}
	}

	private void parseRequestLine(){
		StringTokenizer st = new StringTokenizer(requestLine, DELIM);

		if(st.countTokens() == 3){
			requestMethod = st.nextToken();
			requestResource = st.nextToken();
			httpVersion = st.nextToken();
		}

		logMessage(requestMethod + " " + requestResource + " " + httpVersion);
	}


	static SimpleDateFormat sdt = new SimpleDateFormat("[yyyy/MM/dd HH:mm:ss] : ");

	static void logMessage(String msg){
		Calendar c = Calendar.getInstance();
		System.out.println(sdt.format(c.getTime()) + msg);
	}
}
