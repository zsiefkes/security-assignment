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
import javafx.scene.layout.HBox;
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
	private HBox registerPane;
	private Button registerButton;
	private String registerInstructionString = "Please enter a username and password to login or register.";
	private Text registerText;
	private TextField registerUsernameField;
	private PasswordField registerPasswordField;

	// login
	private HBox loginPane;
	private Button loginButton;
	private TextField loginUsernameField;
	private PasswordField loginPasswordField;
	
	// client dashboard
	private TextField newTaskField;
	private Button submitTaskButton;
	private Button retrieveTasksButton;
	private Text taskList;
	private VBox clientDashboard;
	private Scene dashboardScene;
	
	// main window
	private VBox welcomePane;
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		// i think, present register and login at the start
		// and then create a client instance from this. good
		
		// buttons and texts
		registerText = new Text(registerInstructionString);
		registerUsernameField = new TextField();
		registerPasswordField = new PasswordField();
		registerButton = new Button("Register");
		registerPane = new HBox();
		registerPane.getChildren().addAll(registerUsernameField, registerPasswordField, registerButton);
		
		loginButton = new Button("Login");
		loginUsernameField = new TextField();
		loginPasswordField = new PasswordField();
		loginPane = new HBox();
		loginPane.getChildren().addAll(loginUsernameField, loginPasswordField, loginButton);
		
		welcomePane = new VBox();
		welcomePane.getChildren().addAll(registerText, registerPane, loginPane);
		welcomePane.setAlignment(Pos.CENTER);
//		pane.setPadding(new Insets(20, 20, 20, 20));
		
		// client dashboard
		newTaskField = new TextField();
		submitTaskButton = new Button("Add task");
		retrieveTasksButton = new Button("View all tasks");
		taskList = new Text("Task List:");
		clientDashboard = new VBox();
		clientDashboard.getChildren().addAll(newTaskField, submitTaskButton, retrieveTasksButton, taskList);
		dashboardScene = new Scene(clientDashboard, width, height);
		
		// -------------------------- set button event listeners --------------------------------- //
		registerButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				try {
					boolean registerTrue = registerUser();
					if (registerTrue) {
						primaryStage.setScene(dashboardScene);
					}
				} catch (IOException | InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException | ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		loginButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				try {
					boolean loginTrue = loginUser();
					if (loginTrue) {
						primaryStage.setScene(dashboardScene);
					}
				} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException
						| InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException
						| IOException | ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		
//		welcomePane.getChildren().add(e)
		Scene welcomeScene = new Scene(welcomePane, width, height);
		
//		Scene scene = new Scene(welcomePane, width, height);
		primaryStage.setTitle("Task Client");
//		primaryStage.setScene(scene);
		primaryStage.setScene(welcomeScene);
		primaryStage.show();
	}

	private boolean registerUser() throws IOException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, ClassNotFoundException {
		// read username and password
		String username = registerUsernameField.getText();
		String password = registerPasswordField.getText();
		
		// check it's valid (greater than say 6 characters)
		if (password.length() < 3) {
			// display some kind of error message

			return false;
		} else if (username.length() < 1) {
			// display some kind of error message
			
			return false;
		}
		
		// do we create an instance of a client here, and then run like client.register() or some shit?
		client = new Client(username, serverAddressDefault, portDefault);
		
		boolean registerTrue = client.registerUser(password);
		
		if (registerTrue) {
			// change the scene to the client dashboard
			System.out.println("User registration for " + username + " successful.");
			return true;
		} else {
			// error message
			return false;
		}
		
	}
	
	private boolean loginUser() throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, IOException, ClassNotFoundException {
		// read client id and password
		String username = loginUsernameField.getText();
		String password = loginPasswordField.getText();
		
		client = new Client(username, serverAddressDefault, portDefault);
		
		boolean loginTrue = client.loginUser(username, password);
		
		if (loginTrue) {
			System.out.println("Login successful");
			// change scene to dashboard
			return true;
		} else {
			System.out.println("Login error...");
			return false;
		}
		
		
//		client = new Client(clientId, session.getSocket());
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
