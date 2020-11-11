import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class Client {

//	private int clientId;
	private String username;
	private Socket socket;
	private Crypt crypt;
	private String keyFileName = "session-key"; 

	private static ArrayList<Client> clientList = new ArrayList<Client>(); 
	
	public Client(String username, String serverAddress, int port) throws UnknownHostException, IOException, NoSuchAlgorithmException, ClassNotFoundException {
		// set client socket and crypt with hardcoded symmetric key filename
		this.socket = new Socket(serverAddress, port);
		this.crypt = new Crypt(keyFileName);
		this.username = username;
//		// assign unique client id and add Client to list of Clients
//		this.clientId = clientList.size() + 1;
//		clientList.add(this);
	}
	
	public void createTask(String task) throws IOException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
		/* 
		 * format the string in the agreed format: "[client ID]; [command string]; [task string]"
		 * command strings are: CREATE to create a task; READ to retrieve client's list of tasks
		 * eg. "1; CREATE; File tax return";
		 * eg. "4; READ; "
		 */
		
		// create and encrypt command string
		String toSend = username + "; CREATE; " + task;
		byte[] encText = crypt.encrypt(toSend);            
		
		// we actually need to treat the socket differently so that we can send um a byte array...
		DataOutputStream dataOut = new DataOutputStream(socket.getOutputStream());
		
		dataOut.writeInt(encText.length); // write length of the message
		dataOut.write(encText);           // write the message
		
		// and now wait for a response...
		BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		String answer = input.readLine();
//		System.out.println(answer);
		System.exit(0);
//		socket.close();
	}
	
	public void readTasks() throws IOException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
		// create and encrypt command string
		String message = username + "; READ; ";
		byte[] toSend = crypt.encrypt(message);
		
		// we actually need to treat the socket differently so that we can send um a byte array...
		DataOutputStream dataOut = new DataOutputStream(socket.getOutputStream());
		dataOut.writeInt(toSend.length); // write length of the message
		dataOut.write(toSend);           // write the message
		
		// handle incoming response from server
		DataInputStream dataIn = new DataInputStream(socket.getInputStream());
		
		int length = dataIn.readInt(); // read length of incoming message
//		System.out.println("length of response: " + length);
		byte[] response;
		String decrypted;
		if(length > 0) {
		    response = new byte[length];
		    // read the message
		    dataIn.read(response);
		    // decrypt the data
		    decrypted = crypt.decrypt(response);
		    
		    // parse the decrypted task list
		    String[] taskList = decrypted.split("; ");
		    
		    System.out.println("task list: ");
		    for (String s : taskList) {
		    	System.out.println(s);
		    }
		    
		} else {
			System.out.println("error no data received");
			return;
		}
		
//		System.out.println(answer);
//		System.exit(0);
		
	}
	
	
    public static void main(String[] args) {
    	try {
    		Client client = new Client("testuser", "127.0.0.1", 9090);
//    		client.createTask("Test task 1");
//    		client.readTasks();
		} catch (Exception e) {
			System.out.println("Error: " + e);
		}
    }
}