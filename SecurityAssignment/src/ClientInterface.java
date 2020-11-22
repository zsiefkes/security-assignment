import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class ClientInterface extends Application {

	// gui window dimensions
	private static int width = 400;
	private static int height = 600;
	
	// server address and port
	private static int portDefault = 9090;
	private static String serverAddressDefault = "127.0.0.1";
	private static int portNumber = portDefault;
	private static String serverAddress = serverAddressDefault;
	private Client client;
	
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
	private Button logoutButton;
	
	// client dashboard
	private TextField serverAddressField;
	private TextField portNumberField;
	private Button connectToServerButton;
	private TextField newTaskField;
	private Button submitTaskButton;
	private Button retrieveTasksButton;
	private Text taskList;
	private VBox clientDashboard;
	private Scene dashboardScene;
	private Scene welcomeScene;
	
	// main window
	private VBox welcomePane;
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		
		// buttons and texts
		registerText = new Text(registerInstructionString);
		registerUsernameField = new TextField();
		registerUsernameField.setPromptText("Username");
		registerPasswordField = new PasswordField();
		registerPasswordField.setPromptText("Password");
		registerButton = new Button("Register");
		registerPane = new HBox();
		registerPane.getChildren().addAll(registerUsernameField, registerPasswordField, registerButton);
		
		loginButton = new Button("Login");
		loginUsernameField = new TextField();
		loginUsernameField.setPromptText("Username");
		loginPasswordField = new PasswordField();
		loginPasswordField.setPromptText("Password");
		loginPane = new HBox();
		loginPane.getChildren().addAll(loginUsernameField, loginPasswordField, loginButton);
		
		logoutButton = new Button("Logout");
		
		welcomePane = new VBox();
		welcomePane.getChildren().addAll(registerText, registerPane, loginPane);
		welcomePane.setAlignment(Pos.CENTER);
		
		// client dashboard
		Text serverInfoText = new Text("Currently connected to " + serverAddressDefault + ":" + portDefault);
		serverAddressField = new TextField();
		serverAddressField.setPromptText("Server IP address");
		portNumberField = new TextField();
		portNumberField.setPromptText("Local port number");
		connectToServerButton = new Button("Connect");
		
		HBox serverControls = new HBox();
		serverControls.getChildren().addAll(serverAddressField, portNumberField, connectToServerButton);
		
		newTaskField = new TextField();
		newTaskField.setPromptText("Enter new task");
		submitTaskButton = new Button("Add task");
		retrieveTasksButton = new Button("View all tasks");
		
		HBox taskButtons = new HBox();
		taskButtons.getChildren().addAll(submitTaskButton, retrieveTasksButton);
		taskButtons.setAlignment(Pos.CENTER);
		
		taskList = new Text("Task List:");
		clientDashboard = new VBox();
		clientDashboard.getChildren().addAll(logoutButton, serverControls, serverInfoText, newTaskField, taskButtons, taskList);
		clientDashboard.setAlignment(Pos.CENTER);
		dashboardScene = new Scene(clientDashboard, width, height);
		welcomeScene = new Scene(welcomePane, width, height);
		
		
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
					e.printStackTrace();
				}
			}
		});
		logoutButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				primaryStage.setScene(welcomeScene);
			}
		});
		submitTaskButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				try {
					submitTask();
				} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException
						| InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException
						| IOException e) {
					e.printStackTrace();
				}
			}
		});
		retrieveTasksButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				try {
					retrieveTasks();
				} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException
						| InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException
						| IOException e) {
					e.printStackTrace();
				}
			}
		});
		connectToServerButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				String customServerAddress = serverAddressField.getText();
				int customPortNumber = Integer.parseInt(portNumberField.getText());
				String username = client.getUsername();
				try {
					client = new Client(username, customServerAddress, customPortNumber);
					// update server details
					serverAddress = customServerAddress;
					portNumber = customPortNumber;
				} catch (NoSuchAlgorithmException | ClassNotFoundException | IOException e) {
					e.printStackTrace();
				}
			}
		});
		
		

		primaryStage.setTitle("Task Client");
		primaryStage.setScene(welcomeScene);
		primaryStage.show();
	}

	private void submitTask() throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, IOException {
		String task = newTaskField.getText();
		client.createTask(task);
	}
	
	private void retrieveTasks() throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, IOException {
		String[] taskList = client.readTasks();
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
		
		// create an instance of a client, and then run client.register()
		client = new Client(username, serverAddress, portNumber);
		
		boolean registerTrue = client.registerUser(password);
		
		if (registerTrue) {
			// change the scene to the client dashboard
			System.out.println("User registration for " + username + " successful.");
			registerUsernameField.clear();
			registerPasswordField.clear();
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
		
		client = new Client(username, serverAddress, portNumber);
		
		boolean loginTrue = client.loginUser(username, password);
		
		if (loginTrue) {
			System.out.println("Login successful");
			// change scene to dashboard
			loginUsernameField.clear();
			loginPasswordField.clear();
			return true;
		} else {
			System.out.println("Login error...");
			return false;
		}
		
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
