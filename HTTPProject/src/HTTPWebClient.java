import java.text.SimpleDateFormat;
import java.util.Calendar;

public class HTTPWebClient {

	public static void main(String[] args) {
		showWelcomeMessage();
		new HTTPWebClient();
	}

	public HTTPWebClient() {
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
