import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class TaskServerThread extends Thread {
	private Socket socket;

	public TaskServerThread(Socket s) {
		this.socket = s;
	}

	@Override
	public void run() {
		try {
			System.out.println("A client request received at " + socket);

			// read input stream
			BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	        String coded = input.readLine();

	        // open output stream
	        PrintWriter writeToSocket = new PrintWriter(socket.getOutputStream(), true);

	        // read client id and parse incoming command
	        String[] codedArray = coded.split("; ");
	        int clientId = Integer.parseInt(codedArray[0]);

	        if (codedArray[1].equals("CREATE")) {
	        	
	        	// parse task and create timestamp
	        	String task = codedArray[2];
	        	String timeStamp = new java.util.Date().toString();
	        	
	        	// write task to db
	        	writeTaskToDB(clientId, task, timeStamp);
	        	
	        	// send message back to client
	        	writeToSocket.println("Client " + clientId + " entered task: " + task + " at " + timeStamp);
	        	
	        } else if (codedArray[1].equals("READ")) {
	        	
	        	// run a query
	        	String taskList = readTasksFromDB(clientId);

	        	// send back to the client
	        	writeToSocket.println("Client " + clientId + " tasks: " + taskList);
	        	
	        } else {
	        	// send back to client faulty command message
	        	writeToSocket.println("Command error; please provide CREATE or READ");
	        }
	        
	        
	        // don't forget we're going to have to run decryption step here too
	        
	        
	        
//	        System.out.println(answer);
	        // need to parse the stuff.
	        
	        
	        // not sure what this does here ....
//	        System.exit(0);
	        // don't close the socket yet! do we?
//	        s.close();
			
			
//			PrintWriter writeToSocket = new PrintWriter(socket.getOutputStream(), true);
//			System.out.println("A client request received at " + socket);
//			String ts = new java.util.Date().toString();
//			writeToSocket.println(ts + "; " + answer);
//			writeTaskToDB(ts + "; "+ answer);
			
			socket.close();
		} catch (IOException e) {
			System.out.println("Error: " + e);
			e.printStackTrace();
		}
	}
	
	private Connection connectToDB() throws ClassNotFoundException, SQLException {
		String databaseUser = "dizach";
		String databaseUserPass = "123";
		Class.forName("org.postgresql.Driver");
		Connection connection = null;
		String url = "jdbc:postgresql://localhost/testdb";
		connection = DriverManager.getConnection(url, databaseUser, databaseUserPass);
		return connection;
	}
	
	public String readTasksFromDB(int clientId) {
		try {
			// System.out.println(Inet4Address.getLocalHost().getHostAddress());
			Connection connection = connectToDB();
			Statement s = connection.createStatement();
			String tableName = "tasks";
			
			String sqlCommand = "SELECT task FROM " + tableName + " WHERE client_id = " + clientId;
//			s.executeUpdate("insert into tasks values ('" + ts + "')"); // note no need to end sql command string with semicolon 
//			int result = s.executeUpdate(sqlCommand);
			ResultSet results = s.executeQuery(sqlCommand);
			String taskList = "";
			// loop over result set and add to string to return
			while (results.next()) {
				taskList += results.getString("task") + "; ";
			}
			connection.close();
			return taskList;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Login Error: " + e.toString());
			return "";
		}
		
	}

	public void writeTaskToDB(int clientId, String task, String timeStamp) {
		// i guess the um. parsing of what's to be wrriten should happen in here? i confused. nah happen in run. or elsewhere.
		try {
			// System.out.println(Inet4Address.getLocalHost().getHostAddress());
//			String databaseUser = "dizach";
//			String databaseUserPass = "123";
//			Class.forName("org.postgresql.Driver");
//			Connection connection = null;
//			String url = "jdbc:postgresql://localhost/testdb";
//			connection = DriverManager.getConnection(url, databaseUser, databaseUserPass);
			Connection connection = connectToDB();
			Statement s = connection.createStatement();
			String tableName = "tasks";
			
			String sqlCommand = "insert into " + tableName + " values (" + clientId + ", '" + task + "', '" + timeStamp + "')";
			s.executeUpdate(sqlCommand);
//			s.executeUpdate("insert into tasks values ('" + ts + "')"); // note no need to end sql command string with semicolon 
			connection.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Login Error: " + e.toString());
		}

	}

}
