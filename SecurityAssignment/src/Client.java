import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class Client {

	private int clientId; // for later
	private Socket socket;

	private static ArrayList<Client> clientList = new ArrayList<Client>(); 
	
	public Client(String serverAddress, int port) throws UnknownHostException, IOException {
		// open socket
		this.socket = new Socket(serverAddress, port);
		// assign unique client id and add Client to list of Clients
		this.clientId = clientList.size() + 1;
		clientList.add(this);
	}
	
	public void createTask(String task) throws IOException {
		// open socket for writing to
		PrintWriter writeToSocket = new PrintWriter(socket.getOutputStream(), true);
		
		/* 
		 * format the string in the agreed format: "[client ID]; [command string]; [task string]"
		 * command strings are: CREATE to create a task; READ to retrieve client's list of tasks
		 * eg. "1; CREATE; File tax return";
		 * eg. "4; READ; "
		 */
		String toSend = clientId + "; CREATE; " + task;
		writeToSocket.println(toSend);
		
		// and now wait for a response...
		BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		String answer = input.readLine();
		System.out.println(answer);
		System.exit(0);
//		socket.close();
	}
	
	public void readTasks() {
		
	}
	
	
    public static void main(String[] args) throws IOException {
    	// i want to see something from the client be read by the server
    	Client client = new Client("127.0.0.1", 9090);
    	client.createTask("Hello world!");
    }
}