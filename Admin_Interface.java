package application;

import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.animation.PauseTransition;
import javafx.util.Duration;

import java.util.HashMap;
import java.util.Map;
 
/**
 * <p>Admin_Interface Class</p>
 *
 * <p>Description: The Admin_Interface class provides a graphical user interface (GUI) for admins to manage users.
 * This interface includes functionality to invite new users, reset passwords, delete users, 
 * modify roles,view a list of users,view student messages, manage special access groups, and access the article manager.
 *
 * @author: Group Tu64</p>
 *
 * @version: 1.00 2024-10-02
 * @version: 2.00 2024-11-19 - Enhanced with student message viewing, and special group management.
 */
public class Admin_Interface extends Application {
	
    private Map<String, User> userDatabase = new HashMap<>();     // A map that stores the user database where the key is the username and the value is a User object.
    private Map<String, User> specialAccessGroup = new HashMap<>(); // A map that stores the special access group memebers where the key is the username and the value is a User object.
    private Admin admin; // The Admin object that handles admin-specific operations.

    /**
     * Constructor for the Admin_Interface class.
     * Initializes the interface with the user database and the user acting as an admin.
     *
     * @param userDatabase The map containing all user data.
     * @param user         The user who is using the admin interface (must have admin privileges).
     */
    public Admin_Interface(Map<String, User> specialAccessGroup, Map<String, User> userDatabase, User user) {
        this.userDatabase = userDatabase;
        this.specialAccessGroup = specialAccessGroup;
        // Cast the existing user to Admin.
        this.admin = (Admin) user;
        
        System.out.println("isSpecial: " + this.admin.getIsSpecial());

    }
    
    /**
     * The main entry point for the JavaFX application.
     * It shows the admin panel when the application starts.
     *
     * @param primaryStage The primary stage for this application.
     */
    @Override
    public void start(Stage primaryStage) {
    	//FOR TESTING
    	/*User i = userDatabase.get("i");
        specialAccessGroup.put("Instructor", i);
        char[] pass = {'a'};
        Instructor instructor = new Instructor("jana", pass, userDatabase);
        instructor.setIsInstructor(); // Setting the user as an Instructor
        instructor.addRole("Instructor");
        instructor.setOTC();
        userDatabase.put("jana", instructor); // Adding the new user to the database
    	*/
    	//ENDS HERE
        showAdminPanel(primaryStage); // Display the admin panel.
    }

    private void showAdminPanel(Stage primaryStage) {
        // Create a grid layout with padding and spacing
        GridPane grid = new GridPane();
        grid.getStyleClass().add("grid-layout");
        
        // Create a label for the title
        Label adminLabel = new Label("Admin Panel");
        adminLabel.getStyleClass().add("admin-label");

        // Create buttons for the admin interface
        Button inviteButton = new Button("Invite User");
        Button resetButton = new Button("Reset User Password");
        Button deleteButton = new Button("Delete User");
        Button listButton = new Button("List Users");
        Button addRoleButton = new Button("Add/Remove Role");
        Button openArticleManagerButton = new Button("Open Article Manager");
        Button manageSpecialGroupButton = new Button("Manage Special Group");
        Button viewMessagesButton = new Button("View Student Messages");
        Button logoutButton = new Button("Logout");
        Label messageLabel = new Label();
        messageLabel.setWrapText(true);
        messageLabel.getStyleClass().add("message-label");

        // Add the label and buttons to the grid layout with specific positions
        grid.add(adminLabel, 0, 0, 2, 1); // Span across two columns
        GridPane.setHalignment(adminLabel, HPos.CENTER); // Center the title

        grid.add(inviteButton, 0, 1);
        grid.add(resetButton, 1, 1);
        grid.add(deleteButton, 0, 2);
        grid.add(listButton, 1, 2);
        grid.add(addRoleButton, 0, 3);
        grid.add(openArticleManagerButton, 1, 3);
        grid.add(manageSpecialGroupButton, 0, 4);
        grid.add(viewMessagesButton, 1, 4);
        grid.add(logoutButton, 0, 5);
        grid.add(messageLabel, 0, 6, 2, 1); // Span message label across both columns

        // Center-align all buttons in their cells
        GridPane.setHalignment(inviteButton, HPos.CENTER);
        GridPane.setHalignment(resetButton, HPos.CENTER);
        GridPane.setHalignment(deleteButton, HPos.CENTER);
        GridPane.setHalignment(listButton, HPos.CENTER);
        GridPane.setHalignment(addRoleButton, HPos.CENTER);
        GridPane.setHalignment(openArticleManagerButton, HPos.CENTER);
        GridPane.setHalignment(manageSpecialGroupButton, HPos.CENTER);
        GridPane.setHalignment(logoutButton, HPos.CENTER);

        // Event listeners for each button
        inviteButton.setOnAction(event -> inviteUser(primaryStage, messageLabel));
        resetButton.setOnAction(event -> resetUserPassword(primaryStage, messageLabel));
        deleteButton.setOnAction(event -> deleteUser(primaryStage, messageLabel));
        listButton.setOnAction(event -> listUsers(primaryStage, messageLabel));
        addRoleButton.setOnAction(event -> modifyUserRoles(primaryStage, messageLabel));
        openArticleManagerButton.setOnAction(event -> {
			try {
				openArticleManager(primaryStage, messageLabel);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
        manageSpecialGroupButton.setOnAction(event -> {
			try {
				openSpecialGroupManager(primaryStage, messageLabel);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
        viewMessagesButton.setOnAction(e -> openMessageWindow());
        logoutButton.setOnAction(event -> logout(primaryStage));

        // Create the scene and apply it to the primary stage
        Scene scene = new Scene(grid, 450, 350);
        scene.getStylesheets().add(getClass().getResource("adminPanelStyle.css").toExternalForm());
        primaryStage.setTitle("Admin Panel");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    /**
     * Opens the invitation window to invite a new user to the system.
     * The admin can specify a username and a role for the new user.
     *
     * @param primaryStage   The stage for the invitation window.
     * @param messageLabel   The label used to display messages about the invitation process.
     */
    private void inviteUser(Stage primaryStage, Label messageLabel) {
        // Creating a new stage for inviting users
        Stage inviteStage = new Stage();
        GridPane grid = new GridPane(); // Grid layout for the invite window
        grid.setHgap(10);
        grid.setVgap(10);

        // Labels and input fields for the invite window
        Label usernameLabel = new Label("New Username:");
        TextField usernameField = new TextField();
        Label roleLabel = new Label("Role:");
        TextField roleField = new TextField();
        Button inviteButton = new Button("Invite");
        Label inviteMessage = new Label();

        // Adding elements to the grid
        grid.add(usernameLabel, 0, 0);
        grid.add(usernameField, 1, 0);
        grid.add(roleLabel, 0, 2);
        grid.add(roleField, 1, 2);
        grid.add(inviteButton, 1, 3);
        grid.add(inviteMessage, 1, 4);

        // Event handling for inviting a user
        inviteButton.setOnAction(event -> {
            String username = usernameField.getText();
            String role = roleField.getText();

            // Check if both username and role are filled
            if (!username.isEmpty() && !role.isEmpty()) {
                char[] password = "temporary".toCharArray(); // Temporary password
                User newUser = new User(username, password);

                // Creating a new Student or Instructor based on the role entered
                if (role.equals("Student")) {
                    Student student = new Student(username, password, userDatabase);
                    newUser = student;
                    newUser.setIsStudent(); // Setting the user as a Student
                } else if (role.equals("Instructor")) {
                    Instructor instructor = new Instructor(username, password, userDatabase, specialAccessGroup);
                    if (instructor.checkIfFirstInstructor()) {
                        instructor.setIsSpecial(true); 
                        specialAccessGroup.put(instructor.getUserName(), instructor);
                    }
                    newUser = instructor;
                    newUser.setIsInstructor(); // Setting the user as an Instructor
                }

                // Adding the role and OTC (One-Time Code) to the new user
                newUser.addRole(role);
                newUser.setOTC();
                userDatabase.put(username, newUser); // Adding the new user to the database
                inviteMessage.setText("One Time Code sent to " + username);
            } else {
                inviteMessage.setText("Please fill in all fields."); // Error message if fields are empty
            }
        });

        // Setting up the invite stage and displaying it
        Scene scene = new Scene(grid, 300, 200);
        inviteStage.setScene(scene);
        inviteStage.setTitle("Invite User");
        inviteStage.show();
    }

    /**
     * Opens a window that allows the admin to reset a user's password.
     *
     * @param primaryStage   The stage for the reset window.
     * @param messageLabel   The label used to display messages about the password reset process.
     */
    private void resetUserPassword(Stage primaryStage, Label messageLabel) {
        // Creating a new stage for resetting a user's password
        Stage resetStage = new Stage();
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        // Labels and input fields for the reset window
        Label usernameLabel = new Label("Username to Reset:");
        TextField usernameField = new TextField();
        Button resetButton = new Button("Reset Password");
        Label resetMessage = new Label();

        // Adding elements to the grid
        grid.add(usernameLabel, 0, 0);
        grid.add(usernameField, 1, 0);
        grid.add(resetButton, 1, 2);
        grid.add(resetMessage, 1, 3);

        // Event handling for resetting the password
        resetButton.setOnAction(event -> {
            String username = usernameField.getText();

            // Check if username is provided
            if (!username.isEmpty()) {
                admin.resetUserPassword(username, userDatabase); // Resetting password using the admin object
                resetMessage.setText("Email sent to user.");
            } else {
                resetMessage.setText("Please fill in all fields."); // Error message if the field is empty
            }
        });

        // Setting up the reset stage and displaying it
        Scene scene = new Scene(grid, 300, 200);
        resetStage.setScene(scene);
        resetStage.setTitle("Reset User Password");
        resetStage.show();
    }

    /**
     * Opens a window that allows the admin to delete a user from the system.
     *
     * @param primaryStage   The stage for the delete window.
     * @param messageLabel   The label used to display messages about the deletion process.
     */
    private void deleteUser(Stage primaryStage, Label messageLabel) {
        // Creating a new stage for deleting a user
        Stage deleteStage = new Stage();
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        // Labels and input fields for the delete window
        Label usernameLabel = new Label("Username to Delete:");
        TextField usernameField = new TextField();
        Button deleteButton = new Button("Delete User");
        Label deleteMessage = new Label();

        // Adding elements to the grid
        grid.add(usernameLabel, 0, 0);
        grid.add(usernameField, 1, 0);
        grid.add(deleteButton, 1, 2);
        grid.add(deleteMessage, 1, 3);

        // Event handling for deleting the user
        deleteButton.setOnAction(event -> {
            String username = usernameField.getText();

            // Check if the username is provided and exists in the database
            if (!username.isEmpty() && userDatabase.containsKey(username)) {
                admin.deleteUser(username, userDatabase); // Deleting the user
                deleteMessage.setText(username + " deleted.");
            } else {
                deleteMessage.setText("User does not exist."); // Error message if user not found
            }
        });

        // Setting up the delete stage and displaying it
        Scene scene = new Scene(grid, 300, 200);
        deleteStage.setScene(scene);
        deleteStage.setTitle("Delete User");
        deleteStage.show();
    }

    /**
     * Opens a window that shows the list of users in the system.
     *
     * @param primaryStage   The stage for displaying the user list.
     * @param messageLabel   The label used to display messages about the user list.
     */
    private void listUsers(Stage primaryStage, Label messageLabel) {
        StringBuilder userList = new StringBuilder();

        // Looping through the user database and appending usernames and roles to the list
        for (User user : userDatabase.values()) {
            userList.append("User: ").append(user.getFirstName()).append(" ").append(user.getLastName())
                    .append(", Roles: ").append(user.getRoles()).append("\n");
        }

        // Displaying the list of users in an alert box
        Alert alert = new Alert(Alert.AlertType.INFORMATION, userList.toString());
        alert.setTitle("User List");
        alert.show();
    }

    /**
     * Opens a window that allows the admin to add or remove roles from a user.
     *
     * @param primaryStage   The stage for the modify roles window.
     * @param messageLabel   The label used to display messages about modifying roles.
     */
    private void modifyUserRoles(Stage primaryStage, Label messageLabel) {
        // Creating a new stage for modifying user roles
        Stage modifyStage = new Stage();
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        // Labels and input fields for the modify roles window
        Label usernameLabel = new Label("Username:");
        TextField usernameField = new TextField();
        Label roleLabel = new Label("Role:");
        TextField roleField = new TextField();
        Button addButton = new Button("Add Role");
        Button removeButton = new Button("Remove Role");
        Label modifyMessage = new Label();

        // Adding elements to the grid
        grid.add(usernameLabel, 0, 0);
        grid.add(usernameField, 1, 0);
        grid.add(roleLabel, 0, 1);
        grid.add(roleField, 1, 1);
        grid.add(addButton, 1, 2);
        grid.add(removeButton, 2, 2);
        grid.add(modifyMessage, 1, 3);

        // Event handling for adding a role
        addButton.setOnAction(event -> {
            String username = usernameField.getText();
            String role = roleField.getText();
            User user = userDatabase.get(username);

            // Check if both username and role are provided
            if (!username.isEmpty() && !role.isEmpty()) {
                String message = admin.addRoleToUser(user, role); // Adding role using the admin object
                modifyMessage.setText(message);
            } else {
                modifyMessage.setText("Please provide username and role."); // Error message if fields are empty
            }
        });

        // Event handling for removing a role
        removeButton.setOnAction(event -> {
            String username = usernameField.getText();
            String role = roleField.getText();
            User user = userDatabase.get(username);

            // Check if both username and role are provided
            if (!username.isEmpty() && !role.isEmpty()) {
                admin.removeRoleFromUser(user, role); // Removing role using the admin object
                modifyMessage.setText("Role " + role + " removed from " + username);
            } else {
                modifyMessage.setText("Please provide username and role."); // Error message if fields are empty
            }
        });

        // Setting up the modify stage and displaying it
        Scene scene = new Scene(grid, 400, 200);
        modifyStage.setScene(scene);
        modifyStage.setTitle("Modify User Roles");
        modifyStage.show();
    }

    /**
     * Logs the admin out and redirects to the login screen.
     * Displays a brief logout message before redirecting.
     *
     * @param primaryStage   The stage used for the logout message and redirection.
     */
    private void logout(Stage primaryStage) {
        System.out.println("Logging out."); // Logging to console
        Label loggingOutLabel = new Label("Logging out..."); // Displaying logout message
        GridPane grid = new GridPane();
        grid.add(loggingOutLabel, 0, 0);

        // Creating the scene for the logout message
        Scene scene = new Scene(grid, 400, 300);
        primaryStage.setScene(scene);

        // Adding a delay of 2 seconds before redirecting to the login screen
        PauseTransition pause = new PauseTransition(Duration.seconds(2));
        pause.setOnFinished(event -> redirectToLogin(primaryStage));
        pause.play(); // Start the pause transition
    }
    
    /**
     * Redirects the user to the login screen.
     *
     * @param primaryStage The stage where the login screen will be displayed.
     */
    private void redirectToLogin(Stage primaryStage) {
        // Creating a new login screen instance and starting it
        LoginScreen loginPage = new LoginScreen(userDatabase);
        loginPage.start(primaryStage);
    }

    
    /**
     * Opens the article manager interface.
     *
     * @param primaryStage The stage where the article manager screen will be displayed.
     * @param messageLabel The label to display messages to the user.
     * @throws Exception 
     */
    private void openArticleManager(Stage primaryStage, Label messageLabel) throws Exception {
        // Proceed to the role interface and display the next screen
        Article_Interface articlePage = new Article_Interface(specialAccessGroup, userDatabase, admin);
        articlePage.start(primaryStage);
        messageLabel.setText("Article Manager opened.");
    }
     
    /**
     * Opens the special group management interface.
     *
     * @param primaryStage The stage where the special group management screen will be displayed.
     * @param messageLabel The label to display messages to the user.
     * @throws Exception If there is an issue opening the special group manager.
     */
    private void openSpecialGroupManager(Stage primaryStage, Label messageLabel) throws Exception {
        // Proceed to the role interface and display the next screen
    	SpecialGroup_Interface articlePage = new SpecialGroup_Interface(specialAccessGroup, userDatabase, admin);
        articlePage.start(primaryStage);
        messageLabel.setText("Special Group Manager opened.");
    }
     
    private void openMessageWindow() {
        Stage messageStage = new Stage();
        messageStage.setTitle("Student Messages");

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(20));
        // Call the method to get and display student messages
        for (String message : admin.viewStudentMessage()) {
            vbox.getChildren().add(new javafx.scene.control.Label(message));
        }

        // Create and set the scene for the message window
        Scene scene = new Scene(vbox, 400, 300);
        messageStage.setScene(scene);
        messageStage.show();
    }
    
    /**
     * Main method to launch the JavaFX application.
     *
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        launch(args); // Launch the JavaFX application
    }
}
