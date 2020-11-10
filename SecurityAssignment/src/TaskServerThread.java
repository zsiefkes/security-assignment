import java.io.IOException;
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
			PrintWriter writeToSocket = new PrintWriter(socket.getOutputStream(), true);
			System.out.println("A client request received at " + socket);
			String ts = new java.util.Date().toString();
			writeToSocket.println(ts);
			insertTimeStampIntoDB(ts);
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
