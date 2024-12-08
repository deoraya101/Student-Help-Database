package application;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.*;
/**
 * 
 * <p>Article_Interface Class </p>
 *
 * <p>Description:The `Article_Interface` class provides a JavaFX graphical user interface (GUI) for managing articles in the system. 
 * This interface enables users to create, display, update, delete, back up, restore, and search articles. </p>
 *
 * @author: Group Tu64
 * 
 * @version: 1.00 2024-10-02
 * @version: 2.00 2024-11-19 - Added improved search functions and group-based operations
 *
 */
public class Article_Interface extends Application {
		

    private static Article articleDatabase; // Object of Article class for managing articles
    private Map<String, User> userDatabase = new HashMap<>(); // Database to store user information
    private Map<String, User> specialAccessGroup = new HashMap<>(); // A map that stores the special access group memebers where the key is the username and the value is a User object.
    private User user; // The current user

   
    /**
     * Constructor for Article_Interface
     *
     * @param userDatabase The database of users
     * @param user The current user
     * @throws Exception 
     */
    public Article_Interface(Map<String, User> specialAccessGroup,Map<String, User> userDatabase, User user) throws Exception {
//        this.specialAccessGroup = specialAccessGroup;  // Initialize the special access group
        this.userDatabase = userDatabase; // Initialize the user database
        this.user = user; // Initialize the current user
        articleDatabase = new Article();
        articleDatabase.connectToDatabase();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
    	 //start test
        Article article1 = new Article(
                "Using Bouncy Castle for Encryption",
                "Robert Lynn Carter",
                "Bouncy Castle provides a lightweight cryptography API in Java for developers.",
                "Bouncy Castle, Encryption",
                "This article explains how to use Bouncy Castle for implementing encryption in Java applications.",
                "Introduction to Encryption APIs",
                "Intermediate", 
                "JavaFX",
                null
            );
	    //articles
            articleDatabase.register(article1);

//            Article article2 = new Article(
//                "Mastering JavaFX: A Comprehensive Guide",
//                "Emily Johnson",
//                "A beginner to intermediate guide for creating rich user interfaces in Java with JavaFX.",
//                "JavaFX, UI, Design Patterns",
//                "JavaFX is a powerful framework for building graphical user interfaces in Java. This article covers design patterns and tips for creating robust JavaFX applications.",
//                "JavaFX for Beginners",
//                "Beginner",
//                "JavaFX, GUI",
//                "Instructor, Student"
//            );
//            articleDatabase.register(article2);

            Article article3 = new Article(
                "Building Web Applications with Spring Boot",
                "James Smith",
                "Spring Boot is a popular framework for building web applications in Java.",
                "Spring Boot, Web Development, Java",
                "In this article, we explore how to build robust web applications using Spring Boot, including configuration, security, and deployment.",
                "Building Web Apps with Spring Boot",
                "Advanced",
                "Java, Spring",
                "Mirabella"
            );
            articleDatabase.register(article3);

//            Article article4 = new Article(
//                "Data Science with Python: An Overview",
//                "Alice Cooper",
//                "Data science has become a vital part of many industries, and Python is a leading language in this field.",
//                "Python, Data Science, Machine Learning",
//                "This article introduces the basics of data science with Python, including libraries such as Pandas, NumPy, and Matplotlib.",
//                "Introduction to Data Science",
//                "Beginner",
//                "Data Science, Python",
//                "Admin, Instructor, Student"
//            );
//            articleDatabase.register(article4);
//
//            Article article5 = new Article(
//                "Artificial Intelligence in Healthcare",
//                "Sarah Lee",
//                "AI applications in healthcare are rapidly growing, with applications ranging from diagnostics to treatment optimization.",
//                "AI, Healthcare, Machine Learning",
//                "This article discusses the role of artificial intelligence in healthcare, including examples of AI-powered diagnostic tools and predictive algorithms.",
//                "AI in Healthcare",
//                "Advanced",
//                "AI, Healthcare",
//                "Instructor, Student"
//            );
//            articleDatabase.register(article5);
//
//            Article article6 = new Article(
//                "Understanding Blockchain Technology",
//                "David Brown",
//                "Blockchain is revolutionizing industries with its ability to securely store and share data.",
//                "Blockchain, Cryptocurrency, Security",
//                "This article provides a deep dive into how blockchain works, its uses in cryptocurrency, and its potential applications in other industries.",
//                "Introduction to Blockchain",
//                "Intermediate",
//                "Blockchain, Cryptocurrency",
//                "Admin, Instructor, Student"
//            );
//            articleDatabase.register(article6);
            //end test code
        primaryStage.setTitle("Article Management");

        // Create the main layout
        GridPane grid = new GridPane();
        grid.setId("grid-pane");  // Apply custom CSS class

        // Create buttons for each operation
        Button createButton = new Button("Create Article");
        Button displayButton = new Button("Display Articles");
        Button updateButton = new Button("Update Article"); 
        Button deleteButton = new Button("Delete Article");
        Button backupButton = new Button("Backup Articles");
        Button restoreButton = new Button("Restore Articles");
        Button clearButton = new Button("Clear Database");
        Button searchByGroupsButton = new Button("Search for Specific Articles");
        Button deleteGroupButton = new Button("Delete by Group");
        Button backupGroupButton = new Button("Backup by Group");
        Button backButton = new Button("Back");
        Label messageLabel = new Label();
        messageLabel.setWrapText(true);
        messageLabel.getStyleClass().add("message-label");

        // Adding buttons to the layout
        grid.add(createButton, 0, 0);
        grid.add(updateButton, 1, 0);

        grid.add(deleteButton, 0, 1);   
        grid.add(deleteGroupButton, 1, 1);


        grid.add(displayButton, 0, 5);
        grid.add(searchByGroupsButton, 1, 5);
        

        grid.add(backupButton, 0, 9);
        grid.add(restoreButton, 1, 9);
        grid.add(backupGroupButton, 2, 9);
        
        grid.add(clearButton, 0, 10);
        grid.add(backButton, 0, 14);
        
        // Create Scene and display the main stage
        Scene scene = new Scene(grid, 600, 500);
        scene.getStylesheets().add(getClass().getResource("articlePanelStyle.css").toExternalForm());  // Link the CSS file
        primaryStage.setScene(scene);
        primaryStage.show();

        // Button Event Handlers
        createButton.setOnAction(e -> openCreateArticleWindow());
        displayButton.setOnAction(e -> {
            try {
                displayAllArticles();
            } catch (SQLException e1) {
                e1.printStackTrace();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        });
        updateButton.setOnAction(e -> openUpdateArticleWindow());
        deleteButton.setOnAction(e -> openDeleteArticleWindow());
        backupButton.setOnAction(e -> backupArticles());
        restoreButton.setOnAction(e -> restoreArticles());
        clearButton.setOnAction(e -> {
            try {
                clearDatabase();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        });
        searchByGroupsButton.setOnAction(e -> {
			try {
				openSearchArticlesWindow(primaryStage, messageLabel);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});
        deleteGroupButton.setOnAction(e -> openDeleteGroupArticlesWindow());
        backupGroupButton.setOnAction(e -> backupArticlesByGroup());
        backButton.setOnAction(e -> returnToOriginal(user, primaryStage));

    }

    private void openCreateArticleWindow() {
        Stage createStage = new Stage();
        createStage.setTitle("Create New Article");

        GridPane createGrid = new GridPane();
        createGrid.setPadding(new Insets(10, 10, 10, 10));
        createGrid.setVgap(8);
        createGrid.setHgap(10);

        // Text fields for article attributes
        TextField titleField = new TextField();
        titleField.setPromptText("Title");
        TextField authorsField = new TextField();
        authorsField.setPromptText("Authors");
        TextArea abstractField = new TextArea();
        abstractField.setPromptText("Abstract Text");
        TextField keywordsField = new TextField();
        keywordsField.setPromptText("Keywords (comma-separated)");
        TextArea bodyField = new TextArea();
        bodyField.setPromptText("Body Text");
        TextField referencesField = new TextField();
        referencesField.setPromptText("References (comma-separated)");
        TextField privateLevelField = new TextField();
        privateLevelField.setPromptText("Private Level");
        TextField identifierField = new TextField();
        identifierField.setPromptText("Identifier (comma-separated)");
        TextField systemInfoField = new TextField();
        systemInfoField.setPromptText("System Info");

        Button submitButton = new Button("Submit");

        // Adding fields and button to the layout
        createGrid.add(new Label("Title:"), 0, 0);
        createGrid.add(titleField, 1, 0);
        createGrid.add(new Label("Authors:"), 0, 1);
        createGrid.add(authorsField, 1, 1);
        createGrid.add(new Label("Abstract:"), 0, 2);
        createGrid.add(abstractField, 1, 2);
        createGrid.add(new Label("Keywords: (comma-separated)"), 0, 3);
        createGrid.add(keywordsField, 1, 3);
        createGrid.add(new Label("Body:"), 0, 4);
        createGrid.add(bodyField, 1, 4);
        createGrid.add(new Label("References: (comma-separated)"), 0, 5);
        createGrid.add(referencesField, 1, 5);
        createGrid.add(new Label("Level:"), 0, 6);
        createGrid.add(privateLevelField, 1, 6);
        createGrid.add(new Label("Identifier:"), 0, 7);
        createGrid.add(identifierField, 1, 7);
        createGrid.add(new Label("System Info:"), 0, 8);
        createGrid.add(systemInfoField, 1, 8);

        createGrid.add(submitButton, 1, 9);

        submitButton.setOnAction(e -> {
            String title = titleField.getText();
            String authors = authorsField.getText();
            String abstractText = abstractField.getText();
            String keywords = keywordsField.getText();
            String body = bodyField.getText();
            String references = referencesField.getText();
            String privateLevel = privateLevelField.getText();
            String identifier = identifierField.getText();
            String systemInfo = systemInfoField.getText();
            
            //if systemInfo empty its auto to general group
            if (systemInfo == null || systemInfo.trim().isEmpty()) {
                systemInfo = "General";
            }

            Article article = null;
            try {
                // Assuming the Article constructor is updated to accept the new parameters
                article = new Article(title, authors, abstractText, keywords, body, references, privateLevel, identifier, systemInfo);
            } catch (Exception e1) {
                e1.printStackTrace();
            }

            if (!articleDatabase.doesArticleExist(title)) {
                try {
                    articleDatabase.register(article);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                showAlert("Success", "Article created successfully!");
            } else {
                showAlert("Error", "Article already exists.");
            }
            createStage.close();
        });

        createStage.setScene(new Scene(createGrid, 400, 500)); // Adjusted height to fit new fields
        createStage.show();
    }


    private void displayAllArticles() throws Exception {
    	Set<String> thisUserGroups = user.getGroups();
    	List<String> thisUserGroupsList = new ArrayList<>(thisUserGroups);
    	// Print the list
    	System.out.println(thisUserGroupsList);
    	
    	
    	String articles;
    	if (user.getIsSpecial()) {
            articles = articleDatabase.displayAllArticles(user);
            showAlert("All Articles", articles);
    	}
    	else {
            articles = articleDatabase.displayAllArticles(user);
            showAlert("All Articles", articles);

    	}
    }

    private void openDeleteArticleWindow() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Delete Article");
        dialog.setHeaderText("Enter Article ID to Delete");

        dialog.showAndWait().ifPresent(id -> {
            if (articleDatabase.doesArticleExist(Integer.parseInt(id))) {
                try {
					articleDatabase.deleteArticle(Integer.parseInt(id));
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                showAlert("Success", "Article deleted.");
            } else {
                showAlert("Error", "Article does not exist.");
            }
        });
    }

    private void backupArticles() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Backup Articles");
        dialog.setHeaderText("Enter Backup File Name");

        dialog.showAndWait().ifPresent(fileName -> {
            try {
				articleDatabase.backupArticles(fileName);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            showAlert("Success", "Articles backed up to " + fileName);
        });
    }
    
    private void backupArticlesByGroup() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Backup Articles");
        dialog.setHeaderText("Enter Group Name");

        dialog.showAndWait().ifPresent(groupName -> {
            dialog.setHeaderText("Enter Backup File Name");
            dialog.showAndWait().ifPresent(fileName -> {
                try {
    				articleDatabase.backupArticlesByGroup(fileName, groupName);
    			} catch (Exception e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();           	
            }
            showAlert("Success", "Articles backed up to " + fileName);
            });
        });
    }


    private void restoreArticles() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Restore Articles");
        dialog.setHeaderText("Enter Restore File Name");

        dialog.showAndWait().ifPresent(fileName -> {
            try {
				articleDatabase.restoreArticles(fileName);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            showAlert("Success", "Articles restored from " + fileName);
        });
    }

    private void clearDatabase() throws Exception {
        articleDatabase.clearDatabase();
        showAlert("Database Cleared", "All articles have been deleted.");
    }
    
    private void openDeleteGroupArticlesWindow() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Delete Group of Articles");
        dialog.setHeaderText("Enter Article Identifiers (comma-separated)");

        dialog.showAndWait().ifPresent(ids -> {
            String[] idArray = ids.split(",");
            List<String> identifiersList = new ArrayList<>();
            for (String id : idArray) {
                identifiersList.add(id.trim());
            }

            // Delete each article by identifier
            for (String identifier : identifiersList) {
                try {
                    if (articleDatabase.doesIdentifierExist(identifier)) {
                        articleDatabase.deleteArticlesByIdentifier(identifier);
                        showAlert("Success", "Article with Identifier '" + identifier + "' deleted.");
                    } else {
                        showAlert("Error", "Article with Identifier '" + identifier + "' does not exist.");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    showAlert("Error", "An error occurred while deleting article with Identifier '" + identifier + "'.");
                }
            }
        });
    }
    
    private void openUpdateArticleWindow() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Update Article");
        dialog.setHeaderText("Enter Article ID to Update");
        
        dialog.showAndWait().ifPresent(id -> {
            int articleId = Integer.parseInt(id);
            if (articleDatabase.doesArticleExist(articleId)) {
                openUpdateArticleForm(articleId);
            } else {
                showAlert("Error", "Article does not exist.");
            }
        });
    }

    private void openUpdateArticleForm(int articleId) {
        Stage createStage = new Stage();
        createStage.setTitle("Update New Article");

        GridPane createGrid = new GridPane();
        createGrid.setPadding(new Insets(10, 10, 10, 10));
        createGrid.setVgap(8);
        createGrid.setHgap(10);

        // Text fields for article attributes
        TextField titleField = new TextField();
        titleField.setPromptText("Title");
        TextField authorsField = new TextField();
        authorsField.setPromptText("Authors");
        TextArea abstractField = new TextArea();
        abstractField.setPromptText("Abstract Text");
        TextField keywordsField = new TextField();
        keywordsField.setPromptText("Keywords (comma-separated)");
        TextField referencesField = new TextField();
        referencesField.setPromptText("References (comma-separated)");
        TextField privateLevelField = new TextField();
        privateLevelField.setPromptText("Level");
        TextField identifierField = new TextField();
        identifierField.setPromptText("Identifier");
        TextField systemInfoField = new TextField();
        systemInfoField.setPromptText("System Info");

        Button submitButton = new Button("Submit");

        // Adding fields and button to the layout
        createGrid.add(new Label("Title:"), 0, 1);
        createGrid.add(titleField, 1, 1);
        createGrid.add(new Label("Authors:"), 0, 2);
        createGrid.add(authorsField, 1, 2);
        createGrid.add(new Label("Abstract:"), 0, 3);
        createGrid.add(abstractField, 1, 3);
        createGrid.add(new Label("Keywords: (comma-separated)"), 0, 4);
        createGrid.add(keywordsField, 1, 4);
        createGrid.add(new Label("References: (comma-separated)"), 0, 6);
        createGrid.add(referencesField, 1, 6);
        createGrid.add(new Label("Level:"), 0, 7);
        createGrid.add(privateLevelField, 1, 7);
        createGrid.add(new Label("Identifier:"), 0, 8);
        createGrid.add(identifierField, 1, 8);
        createGrid.add(new Label("System Info:"), 0, 9);
        createGrid.add(systemInfoField, 1, 9);

        // Adding the submit button to the layout
        createGrid.add(submitButton, 1, 10);

        submitButton.setOnAction(e -> {
            String title = titleField.getText();
            String authors = authorsField.getText();
            String abstractText = abstractField.getText();
            String keywords = keywordsField.getText();
            String body = null;
			try {
				body = articleDatabase.getBody(articleId);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
            String references = referencesField.getText();
            String privateLevel = privateLevelField.getText();
            String identifier = identifierField.getText();
            String systemInfo = systemInfoField.getText();

            Article updateArticle = null;
            try {
                // Assuming the Article constructor is updated to accept the new parameters
            	updateArticle = new Article(title, authors, abstractText, keywords, body, references, privateLevel, identifier, systemInfo);
            } catch (Exception e1) {
                e1.printStackTrace();
            }

            try {
				articleDatabase.updateArticle(articleId, updateArticle);
                showAlert("Success", "Article updated successfully!");
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        createStage.close();
        });
            
        createStage.setScene(new Scene(createGrid, 400, 500)); // Adjusted height to fit new fields
        createStage.show();
    }
    
    private void openSearchArticlesWindow(Stage primaryStage, Label messageLabel) throws Exception {
        // Proceed to the role interface and display the next screen
        Search_Interface articlePage = new Search_Interface(specialAccessGroup, userDatabase, user);
        articlePage.start(primaryStage);
        messageLabel.setText("Search Window opened.");
    }

    private static void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void returnToOriginal(User user, Stage primaryStage) {
        if (user.hasRole("Admin")) {
            Admin_Interface adminPage = new Admin_Interface(specialAccessGroup, userDatabase, user);
            adminPage.start(primaryStage);
        } else if (user.hasRole("Instructor")) {
            Instructor_Interface instructorPage = new Instructor_Interface(specialAccessGroup, userDatabase, user);
            instructorPage.start(primaryStage);
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
