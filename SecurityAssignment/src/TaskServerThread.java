import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class TaskServerThread extends Thread {
	private Socket socket;
	private Crypt crypt;
	private String keyFileName = "session-key";

	public TaskServerThread(Socket s) throws NoSuchAlgorithmException, ClassNotFoundException, IOException {
		this.socket = s;
		this.crypt = new Crypt(keyFileName);
	}

	@Override
	public void run() {
		try {
			System.out.println("A client request received at " + socket);

			// open output stream to send response. gah we need to encrypt this, too
			// let's just send a string for now. get the encryption working in one direction first
			PrintWriter writeToSocket = new PrintWriter(socket.getOutputStream(), true);
			
			DataInputStream dataIn = new DataInputStream(socket.getInputStream());
			// read length of incoming message
			int length = dataIn.readInt();
			byte[] message;
			String decrypted;
			if(length > 0) {
			    message = new byte[length];
//			    dataIn.readFully(message, 0, message.length); // this didn't work
			    // read the message
			    dataIn.read(message);
			    decrypted = crypt.decrypt(message);
			} else {
				writeToSocket.println("Data error; no data received");
				return;
			}
	        
	        // read client id and parse incoming command
	        String[] codedArray = decrypted.split("; ");
//	        int clientId = Integer.parseInt(codedArray[0]);
	        String username = codedArray[0];

	        if (codedArray[1].equals("CREATE")) {
	        	
	        	// parse task and create timestamp
	        	String task = codedArray[2];
	        	String timeStamp = new java.util.Date().toString();
	        	
	        	// write task to db
	        	writeTaskToDB(username, task, timeStamp);
	        	
	        	// send message back to client
	        	writeToSocket.println("Client " + username + " entered task: " + task + " at " + timeStamp);
	        	
	        } else if (codedArray[1].equals("READ")) {
	        	
	        	// query database for task list
	        	String taskList = readTasksFromDB(username);
//	        	System.out.println("Tasks: " + taskList);
	        	
	        	// encrypt task list
	        	byte[] toSend = crypt.encrypt(taskList);
	        	
	        	// send encrypted object to client
	        	DataOutputStream dataOut = new DataOutputStream(socket.getOutputStream());
	    		
	    		dataOut.writeInt(toSend.length); // write length of the message
	    		dataOut.write(toSend);           // write the message
	        	
//	        	// send back to the client
//	        	writeToSocket.println("Client " + clientId + " tasks: " + taskList);
	        } else if (codedArray[1].equals("REGISTER")) {
//	        	// encrypt the password
//	        	byte[] encryptedPassword = passwordCrypt.encrypt(codedArray[2]);
//	        	
//	        	// add client to database
//	        	clientId = Integer.parseInt(registerUser(encryptedPassword));
	        	String password = codedArray[2];
	        	registerUser(username, password);
	        	
	        	String toSendString = "1: Client " + username + " successfully registered.";
	        	byte[] toSend = crypt.encrypt(toSendString);
	        	
	        	// send encrypted object to client
	        	DataOutputStream dataOut = new DataOutputStream(socket.getOutputStream());
	    		
	    		dataOut.writeInt(toSend.length); // write length of the message
	    		dataOut.write(toSend);           // write the message
	        	

	        } else if (codedArray[1].equals("LOGIN")) {
	        	
	        	String password = codedArray[2];
	        	boolean loginTrue = loginUser(username, password);
	        	String toSendString;
	        	if (loginTrue) {
	        		toSendString = "1: Client " + username + " successfully authenticated.";
	        	} else {
	        		toSendString = "0: Authentication failure";
	        	}
	        	
	        	byte[] toSend = crypt.encrypt(toSendString);
	        	
	        	// send encrypted object to client
	        	DataOutputStream dataOut = new DataOutputStream(socket.getOutputStream());
	    		
	    		dataOut.writeInt(toSend.length); // write length of the message
	    		dataOut.write(toSend);           // write the message
	        	

	        } else {
	        	// send back to client faulty command message
	        	writeToSocket.println("Command error; please provide CREATE or READ");
	        }
	        
	        
			socket.close();
		} catch (Exception e) {
			System.out.println("Error: " + e);
			e.printStackTrace();
		}
	}
	
	// establish and return a psql database connection 
	private Connection connectToDB() throws ClassNotFoundException, SQLException {
		
		// db user details
		String databaseUser = "dizach";
		String databaseUserPass = "123";
		
		// import postgresql driver
		Class.forName("org.postgresql.Driver");
		
		// establish and return connection
		Connection connection = null;
		String url = "jdbc:postgresql://localhost/testdb";
		connection = DriverManager.getConnection(url, databaseUser, databaseUserPass);
		
		return connection;
	}
	
	public String readTasksFromDB(String username) {
		try {
			Connection connection = connectToDB();
			
			// prepare sql query
			Statement s = connection.createStatement();
			String tableName = "tasks";
			String sqlCommand = "SELECT task FROM " + tableName + " WHERE username = '" + username + "'";
			// don't forget to add single quotation marks around the username!
			
			// execute query
			ResultSet results = s.executeQuery(sqlCommand);
			
			// loop over result set and add to string to return
			String taskList = "";
			while (results.next()) {
				taskList += results.getString("task") + "; ";
			}
			
//			System.out.println("Tasks: " + taskList);
			// close db connection and return tasklist
			connection.close();
			return taskList;
			
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Login Error: " + e.toString());
			return "";
		}
		
	}
	
	public boolean loginUser(String username, String password) {
		try {
			Connection connection = connectToDB();
			
			// prepare sql command
			Statement s = connection.createStatement();
			String tableName = "clients";
			String sqlCommand = "select password from " + tableName + " where username = '" + username + "'";
//			s.executeUpdate(sqlCommand);
//			s.execute(sqlCommand);
			
//			ResultSet results = s.getResultSet();
//			System.out.println("results: " + results);

			// execute query
			ResultSet results = s.executeQuery(sqlCommand);
			
			// loop over result set and add to string to return
			String dbPass = "";
			while (results.next()) {
				dbPass += results.getString("password");
			}
			if (dbPass.equals(password)) {
				return true;
			} else {
				return false;
			}
			
//			return results.toString();
//			connection.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error: " + e.toString());
//			return "";
			return false;
		}
	}
	
	public void registerUser(String username, String password) {
		try {
			Connection connection = connectToDB();
			
			// prepare sql command
			Statement s = connection.createStatement();
			String tableName = "clients";
			String sqlCommand = "insert into " + tableName + " values ('" + username + "', '" + password + "')";
			s.executeUpdate(sqlCommand);

//			ResultSet results = s.executeQuery(sqlCommand);
//			
//			// loop over result set and add to string to return
//			String dbPass = "";
//			while (results.next()) {
//				dbPass += results.getString("password");
//			}
//			if (dbPass.equals(password)) {
//				return true;
//			} else {
//				return false;
//			}
			
//			ResultSet results = ps.getResultSet();
//			System.out.println("results: " + results);
//			return results.toString();
//			connection.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error: " + e.toString());
//			return "";
		}

	}

	public void writeTaskToDB(String username, String task, String timeStamp) {
		try {
			Connection connection = connectToDB();
			
			// prepare sql command
			Statement s = connection.createStatement();
			String tableName = "tasks";
			String sqlCommand = "insert into " + tableName + " values ('" + username + "', '" + task + "', '" + timeStamp + "')";
			
			// execute command
			s.executeUpdate(sqlCommand);
			
			// don't wait for response?
			connection.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Login Error: " + e.toString());
		}

	}

}
