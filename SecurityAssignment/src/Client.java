import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {

//	private int clientId; // for later
	private Socket socket;
	
	public Client(String serverAddress, int port) throws UnknownHostException, IOException {
		this.socket = new Socket(serverAddress, port);
	}
	
	public void writeTaskToDB(String task) throws IOException {
		// okay so, we just use the code the task server was using to send the date to the client, but in the opposite directino now! yeah?
		// um where's our socket coming from
		// okay prob want to start a client with a socket, too. yeah?
		// or do we open one each time we want to send a task ... :( ? 
		// nah, seems to make more sense to open it and keep it open. okay
		PrintWriter writeToSocket = new PrintWriter(socket.getOutputStream(), true);
//		System.out.println("A client request received at " + socket);
//		String ts = new java.util.Date().toString();
		writeToSocket.println(task);
//		insertTimeStampIntoDB(ts); // client does not write to db
//		socket.close(); // don't close the socket.... right?
		
		// and now wait for a response??? I dunno...
		BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		String answer = input.readLine();
//      //JOptionPane.showMessageDialog(null, answer);
		System.out.println(answer);
		System.exit(0);
//		socket.close();
		// where do we open the socket? in the um. constructor?
	}
	
	// this here is basically the constructor.
//	private void connectToServer(String serverAddress, int port) throws UnknownHostException, IOException {
////        String serverAddress = "127.0.0.1";
////        int port = 9090;
//		this.socket = new Socket(serverAddress, port);
//	}
	
    public static void main(String[] args) throws IOException {
//        String serverAddress = "127.0.0.1";
//        int port = 9090;
//        Socket s = new Socket(serverAddress, port);
//        BufferedReader input = new BufferedReader(new InputStreamReader(s.getInputStream()));
//        String answer = input.readLine();
//        //JOptionPane.showMessageDialog(null, answer);
//        System.out.println(answer);
//        System.exit(0);
//        s.close();
    	// i want to see something from the client be read by the server
    	Client client = new Client("127.0.0.1", 9090);
    	client.writeTaskToDB("Hello world!");
    }
}