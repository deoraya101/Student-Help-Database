package application;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * <p>Role_Interface Class</p>
 *
 * <p>Description: A JavaFX application that allows users to select roles from a list.</p>
 *
 * @author: Group Tu64 
 *
 * @version: 1.00 2024-10-02
 * @version: 1.01 2024-11-19 updated to included specialAccessGroup
 */
public class Role_Interface extends Application {

    private Map<String, User> userDatabase = new HashMap<>(); // Database to store user information
    private Map<String, User> specialAccessGroup = new HashMap<>(); // A map that stores the special access group memebers where the key is the username and the value is a User object.
    private User user; // The current user

    /**
     * Constructor for Role_Interface
     *
     * @param userDatabase The database of users
     * @param user The current user
     */
    public Role_Interface(Map<String, User> specialAccessGroup, Map<String, User> userDatabase, User user) {
        this.userDatabase = userDatabase; // Initialize the user database
        this.user = user; // Initialize the current user
        this.specialAccessGroup = specialAccessGroup;
    }

    /**
     * Method to print user data to the console, primarily for debugging.
     */
    public void printData() {
        for (Map.Entry<String, User> entry : userDatabase.entrySet()) {
            String username = entry.getKey(); // Get the username
            User user = entry.getValue(); // Get the User object
            System.out.print("Username: " + username);
            Set<String> roles = user.getRoles(); // Get the roles for the user
            Iterator<String> iterator = roles.iterator();
            if (iterator.hasNext()) {
                String firstElement = iterator.next(); // Get the first role
                System.out.println(" User's First Role: " + firstElement); // Print the first role
            }
        }
    }

    /**
     * The start method is the main entry point for JavaFX applications.
     *
     * @param primaryStage The primary stage for the application.
     */
    @Override
    public void start(Stage primaryStage) {
        showRolePanel(primaryStage); // Show the role selection panel
    } 

    /**
     * Method to display the role selection panel.
     *
     * @param primaryStage The primary stage for the application.
     */
    public void showRolePanel(Stage primaryStage) {
        GridPane gridPane = new GridPane(); // Create a grid layout

        ListView<String> roleListView = new ListView<>(); // ListView to display roles

        // Check if user is not null and populate the roleListView with user roles
        if (user != null) {
            roleListView.getItems().addAll(user.getRoles());
        } else {
            System.out.println("User not found.");
        }

        Label selectedRoleLabel = new Label("Selected Role: None"); // Label to show selected role

        // Button to select the role from the list
        Button selectButton = new Button("Select Role");
        selectButton.setOnAction(event -> {
            String selectedRole = roleListView.getSelectionModel().getSelectedItem(); // Get the selected role
            if (selectedRole != null) {
                selectedRoleLabel.setText("Selected Role: " + selectedRole); // Update label with selected role
                // Navigate to the appropriate interface based on the selected role
                if (selectedRole.equals("Admin")) {
                    Admin_Interface adminPage = new Admin_Interface(specialAccessGroup, userDatabase, user);
                    adminPage.start(primaryStage); // Start the Admin interface
                } else if (selectedRole.equals("Student")) {
                    Student_Interface studentPage = new Student_Interface(specialAccessGroup, userDatabase, user);
                    studentPage.start(primaryStage); // Start the Student interface
                } else if (selectedRole.equals("Instructor")) {
                    Instructor_Interface instructorPage = new Instructor_Interface(specialAccessGroup, userDatabase, user);
                    instructorPage.start(primaryStage); // Start the Instructor interface
                }
            } else {
                selectedRoleLabel.setText("Please select a role."); // Prompt for selection if none is made
            }
        });

        // Add components to the grid
        gridPane.add(new Label("Available Roles:"), 0, 0);
        gridPane.add(roleListView, 0, 1);
        gridPane.add(selectButton, 0, 2);
        gridPane.add(selectedRoleLabel, 0, 3);

        // Set up the scene and stage
        Scene scene = new Scene(gridPane, 300, 250);
        primaryStage.setTitle("Role Selection"); // Set the title of the stage
        primaryStage.setScene(scene); // Set the scene for the primary stage
        primaryStage.show(); // Show the stage
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
