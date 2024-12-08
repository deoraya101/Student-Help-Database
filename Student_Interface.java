package application;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.animation.PauseTransition;
import javafx.util.Duration;

import java.util.HashMap;
import java.util.Map; 
/**
 * <p>Student_Interface Class</p>
 *
 * <p>Description: Student_Interface is a JavaFX application designed for students to interact with the system.
 * It provides a graphical user interface (GUI) for students to perform actions such as searching for articles, sending messages, and logging out.
 *
 * @author Group Tu64
 *
 * @version: 1.00 2024-10-02
 * @version: 2.00 2024-11-19 the addition of searching articles and sending messages. 
 */
public class Student_Interface extends Application {
    private Map<String, User> userDatabase = new HashMap<>();
    private Map<String, User> specialAccessGroup = new HashMap<>(); // A map that stores the special access group memebers where the key is the username and the value is a User object.
    private Student student;
    /**
     * Constructor for the Student_Interface class.
     *
     * @param specialAccessGroup A map of special access group members where the key is the username and the value is a User object.
     * @param userDatabase A map containing all users in the system.
     * @param user The current user object interacting with the interface.
     */

    public Student_Interface(Map<String, User> specialAccessGroup, Map<String, User> userDatabase, User user) {
        this.userDatabase = userDatabase;
        this.specialAccessGroup = specialAccessGroup;
        if (!user.getIsStudent()) {
        	Student student = new Student(user.getUserName(),user.getPassword(),userDatabase);
        	this.student = student;
        } else {
        	this.student = (Student)user;
        }
    }

    @Override
    public void start(Stage primaryStage) {
        showStudentPanel(primaryStage);
    }
    /**
     * Displays the main student panel with options to search articles, send messages, or log out.
     *
     * @param primaryStage The primary stage for the JavaFX application.
     */
    private void showStudentPanel(Stage primaryStage) {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        
        Label studentLabel = new Label("Student Panel");
        Button logoutButton = new Button("Logout");
        Button specialMessageButton = new Button("Send Message");
        ComboBox<String> searchByComboBox = new ComboBox<>();
        searchByComboBox.getItems().addAll("Title", "Author", "Abstract", "Groups", "Levels");
        searchByComboBox.setPromptText("Search By");
        ComboBox<String> genericMessageComboBox = new ComboBox<>();
        genericMessageComboBox.getItems().addAll("Searching for Articles", "Installing Software", "Run Configuration", "What I want is not listed...");
        genericMessageComboBox.setPromptText("Generic Message");
        Button enterButton = new Button("Enter");
        Button enter2Button = new Button ("Enter");
        
		
        grid.add(studentLabel, 1, 0);
        grid.add(searchByComboBox, 1, 2);
        grid.add(enterButton, 2, 2);
        grid.add(logoutButton, 15, 10);
        grid.add(genericMessageComboBox, 1, 4);
        grid.add(enter2Button, 2, 4);
        grid.add(specialMessageButton, 1, 6);
       

        
        logoutButton.setOnAction(event -> logout(primaryStage));
        specialMessageButton.setOnAction(event -> sendSpecialMessage(primaryStage));
        enter2Button.setOnAction(event -> {
            String selectedOption = genericMessageComboBox.getValue();
            if (selectedOption != null) {
                switch (selectedOption) {
                    case "Searching For Articles":
                    	String search = "There are multiple ways you can find the article that you want to view! "
                    					+ "By clicking on “Search By…”, this will allow you to find an article by "
                    					+ "its title, author, abstract, identifier, content level, or group.";
                    	showAlert("Searching For Articles", search);
                        break;
                    case "Installing Software":
                    	String install = "Please refer to the articles regarding all software installation, e.g. “How to install Eclipse”, “JavaFX Installation”, …";
                        showAlert("Installing Software", install);
                        break;
                    case "Run Configuration":
                    	String runconfig = "Open the application, the “src” folder, the “package” folder, and select the “.java” file that contains the mainline to highlight it "
                    			+ "-> Use the “Run” menu at the top of the window and select “Run Configurations...” -> Click on the “Java Application” item "
                    			+ "-> Click on the empty page with a gold plus sign icon -> In the white text box below “VM arguments”, specify the “--module-path” directive "
                    			+ "-> Click on “Apply” button and then click on the blue “Run” button ";
                    	showAlert("Run Configuration", runconfig);
                        break;
                        
                    case "What I want is not listed...":
                        sendSpecialMessage(primaryStage);
                        break;
                    default:
                        showAlert("Error", "Please select a valid search option.");
                        break;
                }
            } else {
                showAlert("Error", "Please select a search option from the dropdown.");
            }
        });
       
        enterButton.setOnAction(event -> {
            try {
                String selectedOption = searchByComboBox.getValue();
                if (selectedOption != null) {
                    Search_Interface articles = new Search_Interface(specialAccessGroup, userDatabase, student);
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
                // Log the exception if necessary
                System.err.println("An error occurred: " + e.getMessage());
                // Optionally, show an alert to the user
                showAlert("Error", "An unexpected error occurred. Please try again.");
            }
        });

        Scene scene = new Scene(grid, 400, 300);
        primaryStage.setTitle("Student Panel");
        primaryStage.setScene(scene);
        primaryStage.show();

    }
    /**
     * Displays an alert with a given title and message.
     *
     * @param title The title of the alert.
     * @param message The content of the alert message.
     */
    private void showAlert(String title, String message) {
    	 Alert alert = new Alert(Alert.AlertType.INFORMATION);
         alert.setTitle(title);
         alert.setHeaderText(null);
         alert.setContentText(message);
         alert.showAndWait();
		
	}
    /**
     * Displays a form for the student to send a custom message.
     *
     * @param primaryStage The primary stage for the message form.
     */
    private void sendSpecialMessage(Stage primaryStage) {
    	Stage messageStage = new Stage();
        messageStage.setTitle("Send Special Message");

        // Create Labels
        Label subjectLabel = new Label("Subject:");
        Label descriptionLabel = new Label("Description:");
        Label confirmationLabel = new Label();  // To display the success message

        // Create Text Fields for subject and description
        TextField subjectField = new TextField();
        subjectField.setPromptText("Enter the subject of your message");

        TextArea descriptionArea = new TextArea();
        descriptionArea.setPromptText("Enter your question or description here");
        descriptionArea.setWrapText(true);

        // Create a Button to submit the message
        Button sendButton = new Button("Send");
        sendButton.setOnAction(e -> {
            String subject = subjectField.getText();
            String description = descriptionArea.getText();

            // Send the message using the Student class's sendMessage method
            Boolean sent = student.sendMessage(subject, description);

            // Display confirmation message
            if(sent) {
            	Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Message Sent");
                alert.setHeaderText(null);
                alert.setContentText("Your message has been sent successfully!");
                alert.showAndWait();
            }

            // Clear the fields after sending the message
            subjectField.clear();
            descriptionArea.clear();
        });

        // Layout for the form
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(20));
        vbox.getChildren().addAll(subjectLabel, subjectField, descriptionLabel, descriptionArea, sendButton);

        // Create the scene and set it on the new stage
        Scene scene = new Scene(vbox, 400, 300);
        messageStage.setScene(scene);
        messageStage.show();
    }
       //logout
	private void logout(Stage primaryStage) {
    	System.out.println("Logging out.");
    	Label loggingOutLabel = new Label("Logging out...");
        GridPane grid = new GridPane();
        grid.add(loggingOutLabel, 0, 0);
        
        Scene scene = new Scene(grid, 400, 300);
        primaryStage.setScene(scene);
        
        // Pause for 2 seconds before redirecting to login screen
        PauseTransition pause = new PauseTransition(Duration.seconds(2));
        pause.setOnFinished(event -> redirectToLogin(primaryStage));
        pause.play();
    }
    /**
     * Redirects the user to the login screen.
     *
     * @param primaryStage The primary stage for the application.
     */
    private void redirectToLogin(Stage primaryStage) {
    	LoginScreen loginPage = new LoginScreen(userDatabase);
        loginPage.start(primaryStage);
    }
}
