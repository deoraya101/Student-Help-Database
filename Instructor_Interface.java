package application;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.animation.PauseTransition;
import javafx.util.Duration;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>Instructor_Interface Class</p>
 *
  * <p>Description: The Instructor_Interface class represents the graphical user interface (GUI) for instructors in the system. 
 * This interface allows instructors to access and manage articles, and logging out.
 * It also supports determining the instructor's special access rights and provides role-based navigation options.</p>
 *
 * @author: Group Tu64
 *
 * @version: 1.00 2024-10-02
 * @version: 2.00 2024-11-19 Added search and opens to article management 
 */

public class Instructor_Interface extends Application {
    private Map<String, User> userDatabase = new HashMap<>(); // A map to store all users
    private Map<String, User> specialAccessGroup = new HashMap<>(); // A map that stores the special access group memebers where the key is the username and the value is a User object.
    private Instructor instructor; // The current instructor using the interface
    
    /**
     * Constructor for the Instructor_Interface class.
     * It initializes the instructor by checking if the user is already an instructor or needs promotion.
     *
     * @param userDatabase The map containing all user data.
     * @param user         The current user who logged in.
     */
    public Instructor_Interface(Map<String, User> userDatabase, User user, Map<String, User> specialAccessGroup) {
        this.userDatabase = userDatabase;
        this.specialAccessGroup = specialAccessGroup;
        if (!user.getIsInstructor()) { 
            // Promote user to instructor if they are not one already
            Instructor instructor = new Instructor(user.getUserName(), user.getPassword(), userDatabase, specialAccessGroup);
            this.instructor = instructor;
        } else { 
            // User is already an instructor, just assign them
            this.instructor = (Instructor) user;
        }
    }

    public Instructor_Interface(Map<String, User> specialAccessGroup, Map<String, User> userDatabase, User user) {
    	this.userDatabase = userDatabase;
        if (!user.getIsInstructor()) { 
            // Promote user to instructor if they are not one already
            Instructor instructor = new Instructor(user.getUserName(), user.getPassword(), userDatabase, specialAccessGroup);
            this.instructor = instructor;
            this.specialAccessGroup = specialAccessGroup;
        } else { 
            // User is already an instructor, just assign them
            this.instructor = (Instructor) user;
        }
	}

	/**
     * The JavaFX start method to initiate the GUI.
     * It shows the instructor panel.
     *
     * @param primaryStage The main window of the application.
     */
    @Override
    public void start(Stage primaryStage) {
        showInstructorPanel(primaryStage); // Show the panel for instructor interactions
    }

    /**
     * Displays the instructor panel where actions like logging out are provided.
     * 
     * @param primaryStage The main window where the panel is shown.
     */
    private void showInstructorPanel(Stage primaryStage) {
    	GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
    	Label instructorLabel = new Label("Instructor Panel");
        Button openArticleManagerButton = new Button("Open Article Manager");
        Button logoutButton = new Button("Logout");
        ComboBox<String> searchByComboBox = new ComboBox<>();
        searchByComboBox.getItems().addAll("Title", "Author", "Abstract", "Groups", "Levels");
        searchByComboBox.setPromptText("Search By");
        Button searchButton = new Button("Search");
        Label messageLabel = new Label();


        grid.add(instructorLabel, 1, 0);
        grid.add(openArticleManagerButton, 1, 2);
        grid.add(searchByComboBox, 1, 4);
        grid.add(searchButton, 2, 4);
        grid.add(logoutButton, 1, 6);

        logoutButton.setOnAction(event -> logout(primaryStage));
        openArticleManagerButton.setOnAction(event -> {
			try {
				openArticleManager(primaryStage, messageLabel);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});        
        /*manageSpecialGroupButton.setOnAction(event -> {
			try {
				openSpecialGroupManager(primaryStage, messageLabel);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});*/
        searchButton.setOnAction(event -> {
            try {
                String selectedOption = searchByComboBox.getValue();
                if (selectedOption != null) {
                    Search_Interface articles = new Search_Interface(specialAccessGroup, userDatabase, instructor);
                    articles.start(primaryStage);
                    switch (selectedOption) {
                        case "Title":
                            articles.searchByTitlesWindow();
                            break;
                        case "Author":
                            articles.searchByAuthorWindow();
                            break;
                        case "Abstract":
                            articles.searchByAbstractWindow();
                            break;
                        case "Groups":
                            articles.searchByIdentifiersWindow();
                            break;
                        case "Levels":
                            articles.searchByLevelsWindow();
                            break;
                        default:
                            showAlert("Error", "Please select a valid search option.");
                            break;
                    }
                } else {
                    showAlert("Error", "Please select a search option from the dropdown.");
                }
            } catch (Exception e) {
                System.err.println("An error occurred: " + e.getMessage());
                showAlert("Error", "An unexpected error occurred. Please try again.");
            }
        });

        Scene scene = new Scene(grid, 400, 300);
        primaryStage.setTitle("Instructor Panel");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    /**
     * Handles the logout process. Displays a message and waits for 2 seconds before redirecting.
     * 
     * @param primaryStage The main window to display the logout message.
     */
    private void logout(Stage primaryStage) {
        System.out.println("Logging out."); // Log the action in the console

        // Display a logout message
        Label loggingOutLabel = new Label("Logging out...");
        GridPane grid = new GridPane();
        grid.add(loggingOutLabel, 0, 0);

        Scene scene = new Scene(grid, 400, 300);
        primaryStage.setScene(scene);

        // Pause for 2 seconds before moving to the login screen
        PauseTransition pause = new PauseTransition(Duration.seconds(2));
        pause.setOnFinished(event -> redirectToLogin(primaryStage)); // Redirect after the pause
        pause.play();
    }

    /**
     * Redirects the user to the login screen after logout.
     * 
     * @param primaryStage The main window where the login screen will be displayed.
     */
    private void redirectToLogin(Stage primaryStage) {
        LoginScreen loginPage = new LoginScreen(userDatabase); // Create a new login screen
        loginPage.start(primaryStage); // Start the login screen
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
        Article_Interface articlePage = new Article_Interface(userDatabase, userDatabase, instructor);
        articlePage.start(primaryStage);
        messageLabel.setText("Article Manager opened.");
    }
}
