import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
//import java.util.Date;

public class TaskServer {

    public static void main(String[] args) throws IOException {
        int port = 9090;
        ServerSocket listener = new ServerSocket(port);
        System.out.println("Server started on " + port);
        try {
            while (true) {
                Socket socket = listener.accept();
                new TaskServerThread(socket).run();               
            }
        }
        finally {
            listener.close();
        }
    }
}