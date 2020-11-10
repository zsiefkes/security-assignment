import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class TaskServerThread extends Thread {
	private Socket socket;

	public TaskServerThread(Socket s) {
		this.socket = s;
	}

	@Override
	public void run() {
		try {
			// need code in here to actually um parse something incoming from client
			// just do the opposite direction to what the client was doing accepting stuff from the server. input stream
			// okay so the socket already exists, the um. the taskserver creates it and instantiates the thread with it. so don't need this line
//	        Socket s = new Socket(serverAddress, port);
			
			// we want to read the input:
	        BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	        String answer = input.readLine();
	        //JOptionPane.showMessageDialog(null, answer);
	        System.out.println(answer);
	        // need to parse the stuff.
	        // not sure what this does here ....
//	        System.exit(0);
	        // don't close the socket yet! do we?
//	        s.close();
			
			
			PrintWriter writeToSocket = new PrintWriter(socket.getOutputStream(), true);
			System.out.println("A client request received at " + socket);
			String ts = new java.util.Date().toString();
			writeToSocket.println(ts + "; " + answer);
			insertTimeStampIntoDB(ts + "; "+ answer);
			socket.close();
		} catch (IOException e) {
			System.out.println("Error: " + e);
			e.printStackTrace();
		}
	}

	public void insertTimeStampIntoDB(String ts) {
		try {
			// System.out.println(Inet4Address.getLocalHost().getHostAddress());
			String databaseUser = "dizach";
			String databaseUserPass = "123";
			Class.forName("org.postgresql.Driver");
			Connection connection = null;
			String url = "jdbc:postgresql://localhost/testdb";
			connection = DriverManager.getConnection(url, databaseUser, databaseUserPass);
			Statement s = connection.createStatement();
			s.executeUpdate("insert into timestamps values ('" + ts + "')");
			connection.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Login Error: " + e.toString());
		}

	}

}
