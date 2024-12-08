package application;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
/**
 * <p>Search_Interface Class</p>
 *
 * <p>Description:The Search_Interface class is a JavaFX application that provides a graphical user interface (GUI)
 * for searching articles in a database. It allows users to search articles by various ways such as title,
 * author, abstract, identifiers, levels, and groups Users can also view more details about specific articles.
 * The class adapts functionality based on the user's role, ensuring appropriate access levels.</p>
 *

 *
 * @author: Group Tu64
 * @version: 1.00 2024-11-19
 */
public class Search_Interface extends Application {
	

    private static Article articleDatabase; // Object of Article class for managing articles
    private Map<String, User> specialAccessGroup = new HashMap<>(); // A map that stores the special access group memebers where the key is the username and the value is a User object.
    private Map<String, User> userDatabase = new HashMap<>(); // Database to store user information
//    private Map<String, User> specialAccessGroup = new HashMap<>(); // A map that stores the special access group memebers where the key is the username and the value is a User object.
    private User user; // The current user

    
    /**
     * Constructor for Article_Interface
     *
     * @param userDatabase The database of users
     * @param user The current user
     * @throws Exception 
     */
    public Search_Interface(Map<String, User> specialAccessGroup,Map<String, User> userDatabase, User user) throws Exception {
//        this.specialAccessGroup = specialAccessGroup;  // Initialize the special access group
        this.userDatabase = userDatabase; // Initialize the user database
        this.user = user; // Initialize the current user
        articleDatabase = new Article();
        articleDatabase.connectToDatabase();
    }
    

    @Override
    public void start(Stage primaryStage) throws Exception {

        primaryStage.setTitle("Search Articles");

        // Create the main layout
        GridPane grid = new GridPane();
        grid.setId("grid-pane");  // Apply custom CSS class

        // Create buttons only for search operations
        Button searchByTitlesButton = new Button("Search by Titles");
        Button searchByAuthorButton = new Button("Search by Author");
        Button searchByAbstractButton = new Button("Search by Abstract");
        Button searchByIdentifiersButton = new Button("Search by Identifiers");
        Button searchByLevelsButton = new Button("Search by Levels");
        Button searchByGroupsButton = new Button("Search by Groups");
        Button backButton = new Button("Back");

        // Adding buttons to the layout
        grid.add(searchByTitlesButton, 0, 0);
        grid.add(searchByAuthorButton, 1, 0);
        grid.add(searchByAbstractButton, 0, 1);
        grid.add(searchByIdentifiersButton, 1, 1);
        grid.add(searchByLevelsButton, 0, 2);
        if (user.hasRole("Instructor") || user.hasRole("Admin")) {
            grid.add(searchByGroupsButton, 1, 2);
        }
        grid.add(backButton, 0, 3);

        // Create Scene and display the main stage
        Scene scene = new Scene(grid, 600, 500);
        scene.getStylesheets().add(getClass().getResource("articlePanelStyle.css").toExternalForm());  // Link the CSS file
        primaryStage.setScene(scene);
        primaryStage.show();

        // Button Event Handlers for Search and Back
        searchByTitlesButton.setOnAction(e -> searchByTitlesWindow());
        searchByAuthorButton.setOnAction(e -> searchByAuthorWindow());
        searchByAbstractButton.setOnAction(e -> searchByAbstractWindow());
        searchByIdentifiersButton.setOnAction(e -> searchByIdentifiersWindow());
        searchByLevelsButton.setOnAction(e -> searchByLevelsWindow());
        searchByGroupsButton.setOnAction(e -> searchByGroupsWindow());
        backButton.setOnAction(e -> {
			try {
				returnToOriginal(user, primaryStage);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});
    }

     /**
     * Searches for articles by title.
     */
	void searchByTitlesWindow() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Search by Titles");
        dialog.setHeaderText("Enter phrases OR words for the Title ");

        dialog.showAndWait().ifPresent(input -> {
            String results = null;
            try {
            	results = articleDatabase.getArticlesByTitleAsString(input);
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace(); 
            }
            showArticleDetailsWindow(results);
        });
    }
	
     /**
     * Searches for articles by author
     */
    void searchByAuthorWindow() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Search by Author");
        dialog.setHeaderText("Enter phrases OR words: ");

        dialog.showAndWait().ifPresent(input -> {
            String results = null;
            try {
            	results = articleDatabase.getArticlesByAuthorAsString(input);
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            showArticleDetailsWindow(results);
        });
    }
    /**
     * Searches for articles by abstract
     */
    void searchByAbstractWindow() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Search by Abstract");
        dialog.setHeaderText("Enter phrases OR words: ");

        dialog.showAndWait().ifPresent(input -> {
            String results = null;
            try {
            	results = articleDatabase.getArticlesByAbstractAsString(input);
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            showArticleDetailsWindow(results);
        });
    }
     /**
     * Searches for articles by identifiers.
     */
    void searchByIdentifiersWindow() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Search by Identifiers");
        dialog.setHeaderText("Enter Identifiers (comma-separated) OR 'All'");

        dialog.showAndWait().ifPresent(input -> {
            String results = null;
            try {
                if (input.trim().equalsIgnoreCase("All")) {
                    // If "All" is entered, fetch all articles
                    results = articleDatabase.displayAllArticles(user);
                } else {
                    // Otherwise, split input into identifiers and search by those
                    String[] keywordsArray = input.split(",");
                    List<String> keywordsList = new ArrayList<>();
                    for (String keyword : keywordsArray) {
                        keywordsList.add(keyword.trim());
                    }
                    results = articleDatabase.getArticlesByIdentifiersAsString(keywordsList);
                }    
            } catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            showArticleDetailsWindow(results);
        });
    }
    
     /**
     * Searches for articles by group.
     */
    void searchByGroupsWindow() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Search by Groups");
        dialog.setHeaderText("Enter Groups (comma-separated) OR 'All'");

        dialog.showAndWait().ifPresent(input -> {
            String results = null;
            try {
                if (input.trim().equalsIgnoreCase("All")) {
                    // If "All" is entered, fetch all articles
                    results = articleDatabase.displayAllArticles(user);
                } else {
                    // Otherwise, split input into identifiers and search by those
                    String[] keywordsArray = input.split(",");
                    List<String> keywordsList = new ArrayList<>();
                    for (String keyword : keywordsArray) {
                        keywordsList.add(keyword.trim());
                    }
                    
                    boolean canView = false;
                    
                    if (user.hasRole("Instructor") || user.getIsSpecial()) {
                    	canView = true;
                    }
                    
                    results = articleDatabase.getArticlesByGroupsAsString(keywordsList, canView);
                }    
            } catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            showArticleDetailsWindow(results);
        });
    }
    
     /**
     * Searches for articles by level
     */
    void searchByLevelsWindow() {
    	Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Search by Content Level");
        dialog.setHeaderText("Select Levels: ");
        
        ButtonType searchButtonType = new ButtonType("Search", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(searchButtonType, ButtonType.CANCEL);
        
        String levels[] = {"All", "Beginner", "Intermediate","Advanced", "Expert"};
        ComboBox<String> contentLevels = new ComboBox<String>(FXCollections.observableArrayList(levels));
        contentLevels.setValue("All"); // Set default to "All"
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        grid.add(new Label("Content Level:"), 0, 0);
        grid.add(contentLevels, 1, 0);
        
        dialog.getDialogPane().setContent(grid);
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == searchButtonType) {
                return contentLevels.getValue(); // Return the selected content level
            }
            return null;
        });
        
        dialog.showAndWait().ifPresent(selectedLevel -> {
            String results = null;
            try {
                results = articleDatabase.getArticlesByContentLevelAsString(selectedLevel, user);
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            showArticleDetailsWindow(results);
        });
        
    }
        
    private void returnToOriginal(User user, Stage primaryStage) throws Exception {
        Article_Interface articlePage = new Article_Interface(specialAccessGroup, userDatabase, user);
        Student_Interface studentPage = new Student_Interface(specialAccessGroup, userDatabase, user);
        if(user.getIsStudent()) {
        	studentPage.start(primaryStage);
        }else {
        	articlePage.start(primaryStage);
        }
    }
    
    private void showViewMoreWindow() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Enter Article ID");
        dialog.setHeaderText("Enter the ID to view more details:");

        dialog.showAndWait().ifPresent(input -> {
            try {            	
                int articleId = Integer.parseInt(input);
                // Fetch more details for the article with the provided ID
                boolean canView = false;
                
                if (user.hasRole("Instructor") || user.getIsSpecial()) {
                	canView = true;
                }
                 
                String articleDetails = null;
				try {				
					articleDetails = articleDatabase.getMoreInfoForArticleAsString(articleId, canView);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                showArticleDetailsWindow(articleDetails);
            } catch (NumberFormatException e) {
                showAlert("Error", "Invalid ID format.");
            }
        });
    }
     /**
     * Displays the article details in a new window.
     */
    private void showArticleDetailsWindow(String articleDetails) {
        // Create a new Stage for the article details window
        Stage detailStage = new Stage();
        detailStage.setTitle("Article Details");
        
        
	    Set<String> groups = null;
		try {
			groups = articleDatabase.getAllSystemInfo();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	String groupsAsString = String.join(", ", groups);
    	
    	String results = "Active Groups: " + groupsAsString + "\n" + articleDetails;    	
    	
        // Create a TextArea to display article details (read-only)
        TextArea detailTextArea = new TextArea();
        detailTextArea.setText(results);
        detailTextArea.setEditable(false);  // Make the TextArea read-only
        detailTextArea.setWrapText(true);   // Wrap text for better readability

        // Create a VBox to hold the TextArea and the button
        VBox vbox = new VBox(10);  // 10px space between elements
        vbox.setPadding(new Insets(10));  // Add padding around the VBox
        vbox.getChildren().add(detailTextArea);  // Add the TextArea to VBox

        // Create a button to view more details
        Button viewMoreInfoButton = new Button("View an Article in More Detail");

        // Add the button to the VBox layout (instead of the grid)
        vbox.getChildren().add(viewMoreInfoButton);  // Add button below TextArea

        // Set the scene for the new stage and show it
        detailStage.setScene(new Scene(vbox, 400, 300));
        detailStage.show();

        // Button Event Handler for the "View More Info" button
        viewMoreInfoButton.setOnAction(e -> showViewMoreWindow());
    }

    
    private static void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
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
