import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Scanner;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class ClientInterface extends Application {

	// gui window dimensions
	private static int width = 400;
	private static int height = 600;
	
	// session
//	private Session session;
	private static int portDefault = 9090;
	private static String serverAddressDefault = "127.0.0.1";
	private Client client;
	
	// server address and port
//	private String serverAddressDefault = "127.0.0.1";
//	private int portDefault = 9090;
	
	// cryptographic 
	private Crypt crypt;
	private String sessionKeyDefaultFileName = "session-key";
//	private String passwordsKeyDefaultFileName = "passwords-key";
	
	// ---------- javafx nodes ---------------- //
	
	// register
	private VBox registerPane;
	private Button registerButton;
	private String registerInstructionString = "Please enter a username and password and hit register.";
	private Text registerText;
	private TextField registerUsernameField;
	private PasswordField registerPasswordField;

	// login
	private Pane loginPane;
	private Button loginButton;
	private TextField loginClientIdField;
	private TextField loginPasswordField;
	
	// client dashboard
	private TextField newTaskField;
	private Button submitTaskButton;
	private Button retrieveTasksButton;
	private Text taskList;
	
	// main window
	private VBox mainPane;
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		// i think, present register and login at the start
		// and then create a client instance from this. good
		
		// buttons and texts
		registerText = new Text(registerInstructionString);
		registerUsernameField = new TextField();
		registerPasswordField = new PasswordField();
		registerButton = new Button("Register");
		registerPane = new VBox();
		registerPane.getChildren().addAll(registerText, registerUsernameField, registerPasswordField, registerButton);
		
		loginButton = new Button("Login");
		loginClientIdField = new TextField();
		loginPasswordField = new TextField();
		
		mainPane = new VBox();
		mainPane.getChildren().addAll();
		mainPane.setAlignment(Pos.CENTER);
//		pane.setPadding(new Insets(20, 20, 20, 20));
		
		// -------------------------- set button event listeners --------------------------------- //
		registerButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				try {
					registerUser();
				} catch (IOException | InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException | ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		loginButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				loginUser();
			}
		});
		
//		mainPane.getChildren().add(e)
		Scene registerScene = new Scene(registerPane, width, height);
		
		Scene scene = new Scene(mainPane, width, height);
		primaryStage.setTitle("Task Client");
//		primaryStage.setScene(scene);
		primaryStage.setScene(registerScene);
		primaryStage.show();
	}

	private void registerUser() throws IOException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, ClassNotFoundException {
		// read username and password
		String username = registerUsernameField.getText();
		String password = registerPasswordField.getText();
		
		// check it's valid (greater than say 6 characters)
		if (password.length() < 3) {
			// display some kind of error message

			return;
		} else if (username.length() < 1) {
			// display some kind of error message
			
			return;
		}
		
		// do we create an instance of a client here, and then run like client.register() or some shit?
		client = new Client(username, serverAddressDefault, portDefault);
		
		client.registerUser(password);
		
		// change the scene to the client dashboard
	}
	
	private void loginUser() {
		// read client id and password
		
		// check password is correct. send encrypted password to server
		
		
		
//		client = new Client(clientId, session.getSocket());
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
