import java.io.BufferedReader;
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

	private int clientId;
	private Socket socket;
	private Crypt crypt;
	private String keyFileName = "session-key"; 

	private static ArrayList<Client> clientList = new ArrayList<Client>(); 
	
	public Client(String serverAddress, int port) throws UnknownHostException, IOException, NoSuchAlgorithmException, ClassNotFoundException {
		// set client socket and crypt with hardcoded symmetric key filename
		this.socket = new Socket(serverAddress, port);
		this.crypt = new Crypt(keyFileName);
		// assign unique client id and add Client to list of Clients
		this.clientId = clientList.size() + 1;
		clientList.add(this);
	}
	
	public void createTask(String task) throws IOException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
		/* 
		 * format the string in the agreed format: "[client ID]; [command string]; [task string]"
		 * command strings are: CREATE to create a task; READ to retrieve client's list of tasks
		 * eg. "1; CREATE; File tax return";
		 * eg. "4; READ; "
		 */
		String toSend = clientId + "; CREATE; " + task;
		
		
		// encrypt the thing
		byte[] encText = crypt.encrypt(toSend);            
//		System.out.println("The DES encrypted message 64: "+ (Base64.getEncoder().encodeToString(encText)));
//		System.out.println("Using String.valueOf: " + String.valueOf(encText)); // yeah, these be different.
		
		// open socket for writing to
//		PrintWriter writeToSocket = new PrintWriter(socket.getOutputStream(), true);
		
		// we actually need to treat the socket differently so that we can send um a byte array...
		DataOutputStream dataOut = new DataOutputStream(socket.getOutputStream());
		
		dataOut.writeInt(encText.length); // write length of the message
		dataOut.write(encText);           // write the message
		
		// whoa you can "print" a byte array? according to the docs it first calls String.valueOf(Object)  
//		writeToSocket.println(encText);
//		writeToSocket.println
		
		// and now wait for a response...
		BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		String answer = input.readLine();
		System.out.println(answer);
		System.exit(0);
//		socket.close();
	}
	
	public void readTasks() throws IOException {
		// print read command to socket
		PrintWriter writeToSocket = new PrintWriter(socket.getOutputStream(), true);
		String toSend = clientId + "; READ; ";
		writeToSocket.println(toSend);
		
		// read response
		BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		String answer = input.readLine();
		
		// decrypt the thing
		
		System.out.println(answer);
		System.exit(0);
		
	}
	
	
    public static void main(String[] args) {
    	try {
    		Client client = new Client("127.0.0.1", 9090);
    		client.createTask("Test task 3");
//    		client.readTasks();
		} catch (Exception e) {
			System.out.println("Error: " + e);
		}
    }
}