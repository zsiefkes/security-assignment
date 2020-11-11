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
	        	
	        	// query database for task list
	        	String taskList = readTasksFromDB(clientId);
//	        	System.out.println("Tasks: " + taskList);
	        	
	        	// encrypt task list
	        	byte[] toSend = crypt.encrypt(taskList);
	        	
	        	// send encrypted object to client
	        	DataOutputStream dataOut = new DataOutputStream(socket.getOutputStream());
	    		
	    		dataOut.writeInt(toSend.length); // write length of the message
	    		dataOut.write(toSend);           // write the message
	        	
//	        	// send back to the client
//	        	writeToSocket.println("Client " + clientId + " tasks: " + taskList);
	        	
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
	
	public String readTasksFromDB(int clientId) {
		try {
			Connection connection = connectToDB();
			
			// prepare sql query
			Statement s = connection.createStatement();
			String tableName = "tasks";
			String sqlCommand = "SELECT task FROM " + tableName + " WHERE client_id = " + clientId;
			
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

	public void writeTaskToDB(int clientId, String task, String timeStamp) {
		try {
			Connection connection = connectToDB();
			
			// prepare sql command
			Statement s = connection.createStatement();
			String tableName = "tasks";
			String sqlCommand = "insert into " + tableName + " values (" + clientId + ", '" + task + "', '" + timeStamp + "')";
			
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
