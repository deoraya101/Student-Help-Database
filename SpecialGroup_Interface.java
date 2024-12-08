package application;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;

import java.util.*;
/**
 * <p>SpecialGroup_Interface Class</p>
 *
 * <p>Description: The SpecialGroup_Interface class is a JavaFX application that provides a graphical user interface (GUI) 
 * for managing a special access group within the system. This includes operations like adding users to the group, 
 * viewing group members, and removing users from the group. The class ensures that certain constraints, such as 
 * maintaining at least one admin in the group.</p>

 * @author: Group Tu64
 * @version 1.00 2024-11-19
 */
public class SpecialGroup_Interface extends Application {
    
    private static Map<String, User> userDatabase = new HashMap<>(); // Database to store user information
    private static Map<String, User> specialAccessGroup = new HashMap<>(); // A map that stores the special access group members where the key is the username and the value is a User object.
    private User user; // The current user
    
    /**
     * Constructor for SpecialGroup_Interface
     *
     * @param userDatabase The database of users
     * @param specialAccessGroup The special access group
     * @param user The current user
     * @throws Exception 
     */
    public SpecialGroup_Interface(Map<String, User> specialAccessGroup, Map<String, User>  userDatabase, User user) throws Exception {
        this.userDatabase = userDatabase; // Initialize the user database
        this.specialAccessGroup = specialAccessGroup;  // Initialize the special access group
        this.user = user; // Initialize the current user
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Special Group Management"); 

        // Create the main layout
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(8);
        grid.setHgap(10);
        
        // Set grid column constraints to allow buttons to expand
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setHgrow(Priority.ALWAYS);
        grid.getColumnConstraints().addAll(col1, col1, col1);

        // Create buttons for each operation
        Button addButton = new Button("Add User to Special Group");
        Button viewButton = new Button("View Group Members");
        Button removeButton = new Button("Remove User from Special Group");
        Button backButton = new Button("Back");
        
        // Allow buttons to expand and fit content
        addButton.setMaxWidth(Double.MAX_VALUE);
        viewButton.setMaxWidth(Double.MAX_VALUE);
        removeButton.setMaxWidth(Double.MAX_VALUE);
        backButton.setMaxWidth(Double.MAX_VALUE);

        // Adding buttons to the layout
        grid.add(addButton, 0, 0);
        grid.add(viewButton, 1, 0);
        grid.add(removeButton, 2, 0);
        grid.add(backButton, 2, 1); // Add the Back button

        // Create Scene and display the main stage
        Scene scene = new Scene(grid, 600, 200);
        scene.getStylesheets().add(getClass().getResource("specialGroupStyle.css").toExternalForm()); // External CSS stylesheet for styling
        primaryStage.setScene(scene);
        primaryStage.show();

        // Button Event Handlers
        addButton.setOnAction(e -> addUserToSpecialGroup());
        viewButton.setOnAction(e -> viewGroupMembers());
        removeButton.setOnAction(e -> removeUserFromSpecialGroup());
        backButton.setOnAction(e -> returnToOriginal(user, primaryStage));
    }

    
    /**
     * Method to print user data to the console, primarily for debugging.
     */
    public void printData() {
        for (Map.Entry<String, User> entry : specialAccessGroup.entrySet()) {
            String group = entry.getKey(); // Get the username
            User user = entry.getValue(); // Get the User object
//            System.out.println(user.getUserName() + "'s group that is being added: " + group);
            System.out.println("group that is being added: " + group);
            if (user != null) {
                Set<String> groups = user.getGroups(); // Get the roles for the user
                Iterator<String> iterator = groups.iterator();
                if (iterator.hasNext()) {
                    String firstElement = iterator.next(); // Get the first role
                    System.out.println("   User " + user.getUserName() + "'s First Group: " + firstElement); // Print the first role
                }           	
            }
        }
    }
    
    //returns true if successfully added user to special group
    //Alyssa
    public static boolean addUserTest(String username, String group) {
    	User user = userDatabase.get(username);
    	if (user != null) {
            // Check if this is the first user being added to the special group
            boolean isFirstInstructor = specialAccessGroup.isEmpty();
            specialAccessGroup.put(group, user);
            user.addGroup(group);

            // Grant admin rights only to the first instructor
            if (isFirstInstructor && user.hasRole("Instructor")) {
                user.addRole("Admin");
                user.setIsAdmin();
            } else { 
                // Ensure the instructor does not have admin rights by default
                user.removeRole("Admin"); // Assuming `removeRole` method exists
            }

            return true;
        } else {
            return false;
        }
    	
    }
    /**
     * Adds a user to the special access group.
     */
    private void addUserToSpecialGroup() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add User");

        // First dialog to enter the group name
        dialog.setHeaderText("Enter the group you want to add the user to");
        dialog.setContentText("Group:");

        dialog.showAndWait().ifPresent(group -> {
            // Clear input for next dialog
            dialog.getEditor().clear();

            // Second dialog to enter the username
            dialog.setHeaderText("Enter username of the user you want to add");
            dialog.setContentText("Username:");

            dialog.showAndWait().ifPresent(username -> {
                User user = userDatabase.get(username);
                if (user != null) {
                    // Check if this is the first user being added to the special group
                    boolean isFirstInstructor = specialAccessGroup.isEmpty();
                    specialAccessGroup.put(group, user);
                    user.addGroup(group);

                    // Grant admin rights only to the first instructor
                    if (isFirstInstructor && user.hasRole("Instructor")) {
                        user.addRole("Admin");
                        user.setIsAdmin();
                    } else {
                        // Ensure the instructor does not have admin rights by default
                        user.removeRole("Admin"); // Assuming `removeRole` method exists
                    }

                    showAlert("Success", "User " + username + " added to group " + group);
                } else {
                    showAlert("Error", "User not found.");
                }
            });
        });
    }

 /**
     * Views all members of the special access group.
     */
    private void viewGroupMembers() {
        StringBuilder members = new StringBuilder();
        for (Map.Entry<String, User> entry : specialAccessGroup.entrySet()) {
        	User user = entry.getValue();
        	if (user != null) {
                members.append(entry.getKey()).append(" - ").append(entry.getValue().getUserName()).append("\n");        		
        	}
        }
        
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Group Members");
        alert.setHeaderText("Special Access Group Members");
        alert.setContentText(members.toString());
        alert.showAndWait();
    } 
    
    //returns true if successfully removed user from group
    //Alyssa
    public static boolean removeUserTest(String username, String group) {
    	Optional<Map.Entry<String, User>> entryToRemove = specialAccessGroup.entrySet()
                .stream()
                .filter(entry -> entry.getValue().getUserName().equals(username))
                .findFirst();

        if (entryToRemove.isPresent()) {
            User user = entryToRemove.get().getValue();
            
            if (user.hasRole("Admin")) {
                long adminCount = specialAccessGroup.values()
                        .stream()
                        .filter(User::getIsAdmin)
                        .count();
                
                if (adminCount <= 1) {
                    return false;
                }
            }

            specialAccessGroup.remove(entryToRemove.get().getKey());
            return true;
        } else {
        	return false;
        }
    	
    }
    /**
     * Removes a user from the special access group.
     */
    private void removeUserFromSpecialGroup() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Remove User");

        dialog.setHeaderText("Enter username of the user you want to remove");
        dialog.setContentText("Username:");

        dialog.showAndWait().ifPresent(username -> {
            Optional<Map.Entry<String, User>> entryToRemove = specialAccessGroup.entrySet()
                    .stream()
                    .filter(entry -> entry.getValue().getUserName().equals(username))
                    .findFirst();

            if (entryToRemove.isPresent()) {
                User user = entryToRemove.get().getValue();
                
                if (user.hasRole("Admin")) {
                    long adminCount = specialAccessGroup.values()
                            .stream()
                            .filter(User::getIsAdmin)
                            .count();
                    
                    if (adminCount <= 1) {
                        showAlert("Error", "Cannot remove the last admin from the group.");
                        return;
                    }
                }

                specialAccessGroup.remove(entryToRemove.get().getKey());
                showAlert("Success", "User " + username + " removed from the special group.");
            } else {
                showAlert("Error", "User not found in the special group.");
            }
        });
    }

     /**
     * Displays an alert with a given title and message.
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Navigates back to the main interface based on the user's role.
     */
    private void returnToOriginal(User user, Stage primaryStage) {
        if (user.hasRole("Admin")) {
            new Admin_Interface(specialAccessGroup, userDatabase, user).start(primaryStage);
        } else if (user.hasRole("Instructor")) {
            new Instructor_Interface(specialAccessGroup, userDatabase, user).start(primaryStage);
        } else {
            showAlert("Error", "User does not have sufficient roles to proceed.");
        }
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
