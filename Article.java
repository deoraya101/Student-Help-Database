package application;

import java.sql.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashSet;

import Encryption.EncryptionHelper;
import Encryption.EncryptionUtils;

import java.io.*;


import java.util.List;
import java.util.Set;

/**
 * <p> ArticleDatabase Class </p>
 * 
 * <p> Description: The Article class interacts with an H2 database for managing articles.
 * It supports establishing a database connection, creating necessary tables, and performing various 
 * operations on articles. This including registration, updating, retrieving, deletion, backup, and restoration of articles. 
 * The body of each article is securely encrypted before being stored in the database. </p>
 *   
 * @author Group Tu64
 * 
 * @version: 1.00 2024-10-28 
 * @version: 2.00 2024-11-19 - Enhanced with advanced search, backup by group, and group-specific access management.
 */

class Article {

	// JDBC driver and URL information
	static final String JDBC_DRIVER = "org.h2.Driver"; 
	static final String DB_URL = "jdbc:h2:~/firstDatabase";  

	// Default credentials for database access
	static final String USER = "sa"; 
	static final String PASS = ""; 

	// Article fields
	private String level;             // Level of the article (e.g., beginner, intermediate)
	private String identifier;        // Unique identifier for grouping
	private String systemInfo;        // Other system information for access control
	private String title;
	private String authors;
	private String keywords;
	private String body;
	private String abstractText;
	private String references;
	

	// Database connection and statement objects
	private Connection connection = null;
	private Statement statement = null;

	// Encryption helper for securing article content
	private EncryptionHelper encryptionHelper;

	/**
	 * Default constructor that initializes encryption helper
	 */
	public Article() throws Exception {
		encryptionHelper = new EncryptionHelper();
	} 

	/**
	 * Constructor with parameters to initialize article data
	 */
	public Article(String title, String authors, String abstractText, String keywords, String body, String references, String level, String identifier, String systemInfo) throws Exception {
		this.title = title;
		this.authors = authors;
		this.keywords = keywords;
		this.body = body;
		this.abstractText = abstractText;
		this.references = references;
	    this.level = level;               // Initialize level
	    this.identifier = identifier;     // Initialize identifier
	    this.systemInfo = (systemInfo == null) ? "general" : systemInfo;     // Initialize system information
		encryptionHelper = new EncryptionHelper();
	}

	
	 // Establishes a connection to the H2 database and creates the articles table if it does not exist
	public void connectToDatabase() throws SQLException {
		try {
			// Load JDBC driver and connect to the database
			Class.forName(JDBC_DRIVER); 
			connection = DriverManager.getConnection(DB_URL, USER, PASS);
			statement = connection.createStatement(); 
			createTables();
		} catch (ClassNotFoundException e) {
			System.err.println("JDBC Driver not found: " + e.getMessage());
		}
	}

	// Creates the 'articles' table in the database if it does not already exist
	private void createTables() throws SQLException {
	    // SQL statement to create articles table
	    String articlesTable = "CREATE TABLE IF NOT EXISTS articles (" +
	            "id INT AUTO_INCREMENT PRIMARY KEY, " +
	            "title VARCHAR(255), " +
	            "authors VARCHAR(255), " +
	            "abstract TEXT, " +
	            "keywords VARCHAR(255), " +
	            "body TEXT, " +
	            "references VARCHAR(255), " +
	            "level VARCHAR(50), " +             // New column for level
	            "identifier VARCHAR(255), " +       // New column for identifier
	            "systemInfo VARCHAR(255))";          // New column for system information
	    statement.execute(articlesTable);
	}


	/**
	 * Registers a new article in the database, encrypting the body before storing it
	 */
	public void register(Article articleToAdd) throws Exception {
		if (articleToAdd.systemInfo == null) {
	        articleToAdd.systemInfo = "general";  // Set default to "general" if null
	    }
		// Encrypt the article body using the author's name as part of the initialization vector
		String encryptedBody = Base64.getEncoder().encodeToString(
				encryptionHelper.encrypt(articleToAdd.body.getBytes(), EncryptionUtils.getInitializationVector(articleToAdd.authors.toCharArray()))
		);

		// SQL statement to insert a new article
		String insertUser = "INSERT INTO articles (title, authors, abstract, keywords, body, references, level, identifier, systemInfo) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
		try (PreparedStatement pstmt = connection.prepareStatement(insertUser)) {
			pstmt.setString(1, articleToAdd.title);
			pstmt.setString(2, articleToAdd.authors);
			pstmt.setString(3, articleToAdd.abstractText);
			pstmt.setString(4, articleToAdd.keywords);
			pstmt.setString(5, encryptedBody);
			pstmt.setString(6, articleToAdd.references);
	        pstmt.setString(7, articleToAdd.level);           // New field for level
	        pstmt.setString(8, articleToAdd.identifier);      // New field for identifier
	        pstmt.setString(9, articleToAdd.systemInfo);      // New field for system information
			pstmt.executeUpdate();
		}
	}

	/**
	 * Checks if an article with the given title already exists in the database
	 */
	public boolean doesArticleExist(String title) {
		// SQL query to count articles by title
		String query = "SELECT COUNT(*) FROM articles WHERE title = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, title);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				return rs.getInt(1) > 0;  // Returns true if the article exists
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * Checks if an article with the given sequence number already exists in the database
	 */
	public boolean doesArticleExist(int id) {
		// SQL query to count articles by id
		String query = "SELECT COUNT(*) FROM articles WHERE id = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setInt(1, id);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				return rs.getInt(1) > 0;  // Returns true if the article exists
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * Checks if an article with the given identifier already exists in the database
	 */
	public boolean doesIdentifierExist(String identifier) {
		// SQL query to count articles by title
		String query = "SELECT COUNT(*) FROM articles WHERE identifier = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, identifier);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				return rs.getInt(1) > 0;  // Returns true if the article exists
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	// displays all articles
    String displayAllArticles(User user) throws Exception {
    	Set<String> thisUserGroups = user.getGroups();
    	List<String> thisUserGroupsList = new ArrayList<>(thisUserGroups);
    	String articles;
    	if (user.getIsSpecial()) {
            articles = getAllArticlesAsString(); 
    	}
    	else {
    			
    		boolean canViewBody = false;
    		if (user.hasRole("Instructor")) {
    			canViewBody = true;
    		}
    		
    		articles = getArticlesByGroupsAsString(thisUserGroupsList, canViewBody);
    	}
    	
    	return articles;
    }
	
    public String getAllArticlesAsString() throws Exception {
        StringBuilder result = new StringBuilder("All Articles:\n");

        // SQL query to retrieve all articles
        String query = "SELECT * FROM articles";
        
        int articleCount = 0;  // Article count variable

        // Execute the query and format the results
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            
            boolean found = false;
            while (resultSet.next()) {
                found = true;

                result.append("ID: ").append(resultSet.getInt("id")).append("\n")
                .append("Title: ").append(resultSet.getString("title")).append("\n")
                .append("Authors: ").append(resultSet.getString("authors")).append("\n")
                .append("Abstract: ").append(resultSet.getString("abstract")).append("\n")
                .append("\n");
                articleCount++;
            }
            if (!found) {
                return "No articles found in the database.";
            }
            result.insert(0, "Total Articles Retrieved: " + articleCount + "\n\n");
            return result.toString();
        }
    }
    
    /**
     * Searches for articles containing the specified identifiers and returns them as a formatted string.
     * 
     * @param identifiers The list of identifiers to search for in the articles.
     * @return A formatted string listing the articles that match the identifiers, or a message if no articles were found.
     * @throws Exception 
     */
    public String getArticlesByGroupsAsString(List<String> groups, boolean canViewBody) throws Exception {
        if (groups == null || groups.isEmpty()) {
            return "No groups provided.";
        }

        // SQL query to search articles by groups in the 'group' column
        StringBuilder query = new StringBuilder("SELECT * FROM articles WHERE ");
        for (int i = 0; i < groups.size(); i++) {
            query.append("systemInfo = ?");  
            if (i < groups.size() - 1) {
                query.append(" OR ");
            }
        }
        
        
        boolean found = false;
        StringBuilder result = new StringBuilder("Articles matching groups:\n");
        int articleCount = 0;  // Article count variable
        
        // Prepare the SQL statement
        try (PreparedStatement statement = connection.prepareStatement(query.toString())) {
            for (int i = 0; i < groups.size(); i++) {
                statement.setString(i + 1, groups.get(i));  
            }
            
            if (canViewBody) {
                // Execute the query and format the results for a decrypted body
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        found = true;
                        String decryptedBody = decryptField(resultSet.getString("body"), resultSet.getString("authors"));

                        result.append("ID: ").append(resultSet.getInt("id")).append("\n")
                            .append("Title: ").append(resultSet.getString("title")).append("\n")
                            .append("Authors: ").append(resultSet.getString("authors")).append("\n")
                            .append("Abstract: ").append(resultSet.getString("abstract")).append("\n")
                            .append("Keywords: ").append(resultSet.getString("keywords")).append("\n")
                            .append("Body: ").append(decryptedBody).append("\n")
                            .append("References: ").append(resultSet.getString("references")).append("\n")
                            .append("Level: ").append(resultSet.getString("level")).append("\n")          // Add Level
                            .append("Identifier: ").append(resultSet.getString("identifier")).append("\n") // Add Identifier
                            .append("System Info: ").append(resultSet.getString("systemInfo")).append("\n") // Add System Info
                            .append("\n");
                        	articleCount++;  // Increment the count

                    }
    
                }
            } else {
                // Execute the query and format the results for an encrypted body
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        found = true;

                        result.append("ID: ").append(resultSet.getInt("id")).append("\n")
                            .append("Title: ").append(resultSet.getString("title")).append("\n")
                            .append("Authors: ").append(resultSet.getString("authors")).append("\n")
                            .append("Abstract: ").append(resultSet.getString("abstract")).append("\n")
                            .append("Keywords: ").append(resultSet.getString("keywords")).append("\n")
                            .append("Body: ").append(resultSet.getString("body")).append("\n")
                            .append("References: ").append(resultSet.getString("references")).append("\n")
                            .append("Level: ").append(resultSet.getString("level")).append("\n")          // Add Level
                            .append("Identifier: ").append(resultSet.getString("identifier")).append("\n") // Add Identifier
                            .append("System Info: ").append(resultSet.getString("systemInfo")).append("\n") // Add System Info
                            .append("\n");
                        	articleCount++;  // Increment the count
                    }
                }   
           }
            
           if (!found)
        	   return "No articles found for the specified groups.";
           
        }
        result.insert(0, "Total Articles Retrieved: " + articleCount + "\n\n");
        return result.toString();
    }

    /**
     * Searches for articles containing the specified identifiers and returns them as a formatted string.
     * 
     * @param identifiers The list of identifiers to search for in the articles.
     * @return A formatted string listing the articles that match the identifiers, or a message if no articles were found.
     * @throws Exception 
     */
    public String getArticlesByIdentifiersAsString(List<String> identifiers) throws Exception {
    	
        if (identifiers == null || identifiers.isEmpty()) {
            return "No identifiers provided.";
        }

        // SQL query to search articles by identifiers in the 'identifier' column
        StringBuilder query = new StringBuilder("SELECT * FROM articles WHERE ");
        for (int i = 0; i < identifiers.size(); i++) {
            query.append("identifier LIKE ?");
            if (i < identifiers.size() - 1) {
                query.append(" OR ");
            }
        }
        
        int articleCount = 0;
        // Prepare the SQL statement
        try (PreparedStatement statement = connection.prepareStatement(query.toString())) {
            for (int i = 0; i < identifiers.size(); i++) {
                statement.setString(i + 1, identifiers.get(i));
            }

            // Execute the query and format the results
            try (ResultSet resultSet = statement.executeQuery()) {
                StringBuilder result = new StringBuilder("Articles matching identifiers:\n");
                boolean found = false;
                while (resultSet.next()) {
                    found = true;

                    result.append("ID: ").append(resultSet.getInt("id")).append("\n")
                    .append("Title: ").append(resultSet.getString("title")).append("\n")
                    .append("Authors: ").append(resultSet.getString("authors")).append("\n")
                    .append("Abstract: ").append(resultSet.getString("abstract")).append("\n")
                    .append("\n");
                    articleCount++;
                }
                if (!found) {
                    return "No articles found for the specified identifiers.";
                }
                result.insert(0, "Total Articles Retrieved: " + articleCount + "\n\n");
                return result.toString();
            }
        }
    }    
     
 public String getArticlesByContentLevelAsString(String selectedLevel, User user) throws Exception {
        if (selectedLevel.equals("All")) {
            return displayAllArticles(user);
        }
        
        int articleCount = 0;  // Article count variable
        
        // SQL query to search articles by the selected level in the 'level' column
        String query = "SELECT * FROM articles WHERE level = ?";
        StringBuilder result = new StringBuilder();
        
        // Prepare the SQL statement
        try (PreparedStatement statement = connection.prepareStatement(query)) {
        	statement.setString(1, selectedLevel);
            // Execute the query and format the results
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {

                    result.append("ID: ").append(resultSet.getInt("id")).append("\n")
                    .append("Title: ").append(resultSet.getString("title")).append("\n")
                    .append("Authors: ").append(resultSet.getString("authors")).append("\n")
                    .append("Abstract: ").append(resultSet.getString("abstract")).append("\n");
                    articleCount++;  // Increment the count
                }
                result.insert(0, "Total Articles Retrieved: " + articleCount + "\n\n");
                return result.toString();
            }
        }
    }
   
    
    public String getArticlesByAuthorAsString(String author) throws Exception {
    	String query = "SELECT * FROM articles WHERE authors LIKE ?";
        StringBuilder result = new StringBuilder();
        
        int articleCount = 0;  // Article count variable
        
        // Prepare the SQL statement
        try (PreparedStatement statement = connection.prepareStatement(query.toString())) {
        	statement.setString(1, "%" + author + "%");
            // Execute the query and format the results
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {

                    result.append("ID: ").append(resultSet.getInt("id")).append("\n")
                    .append("Title: ").append(resultSet.getString("title")).append("\n")
                    .append("Authors: ").append(resultSet.getString("authors")).append("\n")
                    .append("Abstract: ").append(resultSet.getString("abstract")).append("\n")
                    .append("\n");
                    articleCount++;  // Increment the count
                }
                result.insert(0, "Total Articles Retrieved: " + articleCount + "\n\n");
                return result.toString();
            }
        }
    }

    public String getArticlesByTitleAsString(String phrases) throws Exception {
		if (phrases == null || phrases.trim().isEmpty()) {
		    return "No search phrase provided.";
		}

	    int articleCount = 0;  // Article count variable
		
		// SQL query to search for articles by matching phrases in the 'abstract' column
		String query = "SELECT * FROM articles WHERE title LIKE ?";
		
		// Prepare the SQL statement
		try (PreparedStatement statement = connection.prepareStatement(query)) {
		    statement.setString(1, "%" + phrases.trim() + "%");

            // Execute the query and format the results
            try (ResultSet resultSet = statement.executeQuery()) {
    	        StringBuilder result = new StringBuilder("Articles matching titles:\n");
    	        boolean found = false;
    	        while (resultSet.next()) {
    	            found = true;

    	            result.append("ID: ").append(resultSet.getInt("id")).append("\n")
    	            .append("Title: ").append(resultSet.getString("title")).append("\n")
    	            .append("Authors: ").append(resultSet.getString("authors")).append("\n")
    	            .append("Abstract: ").append(resultSet.getString("abstract")).append("\n")
    	            .append("\n");
    	            articleCount++;  // Increment the count
    	        }
    	        if (!found) {
    	            return "No articles found for the specified search terms.";
    	        }
    	        result.insert(0, "Total Articles Retrieved: " + articleCount + "\n\n");
    	        return result.toString();
    	    }
    	}
    }
     
    public String getArticlesByAbstractAsString(String phrases) throws Exception {
		if (phrases == null || phrases.trim().isEmpty()) {
		    return "No search phrase provided.";
		}
		
		// SQL query to search for articles by matching phrases in the 'abstract' column
		String query = "SELECT * FROM articles WHERE abstract LIKE ?";
		
	    int articleCount = 0;  // Article count variable
		
		// Prepare the SQL statement
		try (PreparedStatement statement = connection.prepareStatement(query)) {
		    statement.setString(1, "%" + phrases.trim() + "%");
		
		    try (ResultSet resultSet = statement.executeQuery()) {
		        StringBuilder result = new StringBuilder("Articles matching abstract phrase:\n");
		        boolean found = false;
		        while (resultSet.next()) {
		            found = true;
		
		            result.append("ID: ").append(resultSet.getInt("id")).append("\n")
		                .append("Title: ").append(resultSet.getString("title")).append("\n")
		                .append("Authors: ").append(resultSet.getString("authors")).append("\n")
		                .append("Abstract: ").append(resultSet.getString("abstract")).append("\n")
		                .append("\n");
		            	articleCount++;  // Increment the count
		        }
		        if (!found) {
		            return "No articles found with abstracts containing the specified phrase.";
		        }
		        result.insert(0, "Total Articles Retrieved: " + articleCount + "\n\n");
		        return result.toString();
		    }
		}
    }

    
    public String getMoreInfoForArticleAsString(int id, boolean canViewBody) throws Exception {
        StringBuilder query = new StringBuilder("SELECT * FROM articles WHERE id = ? ");
        boolean found = false;
        StringBuilder result = new StringBuilder("View More Article:\n");

        // Prepare the SQL statement
        try (PreparedStatement statement = connection.prepareStatement(query.toString())) {
		    statement.setInt(1, id);
            if (canViewBody) {
                // Execute the query and format the results for a decrypted body
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        found = true;
                        String decryptedBody = decryptField(resultSet.getString("body"), String.valueOf(resultSet.getInt("id")));


                        result.append("ID: ").append(resultSet.getInt("id")).append("\n")
                            .append("Title: ").append(resultSet.getString("title")).append("\n")
                            .append("Authors: ").append(resultSet.getString("authors")).append("\n")
                            .append("Abstract: ").append(resultSet.getString("abstract")).append("\n")
                            .append("Keywords: ").append(resultSet.getString("keywords")).append("\n")
                            .append("Body: ").append(decryptedBody).append("\n")
                            .append("References: ").append(resultSet.getString("references")).append("\n")
                            .append("Level: ").append(resultSet.getString("level")).append("\n")          // Add Level
                            .append("Identifier: ").append(resultSet.getString("identifier")).append("\n") // Add Identifier
                            .append("System Info: ").append(resultSet.getString("systemInfo")).append("\n") // Add System Info
                            .append("\n");
                    }
                }
            } else {
                // Execute the query and format the results for an encrypted body
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        found = true;

                        result.append("ID: ").append(resultSet.getInt("id")).append("\n")
                            .append("Title: ").append(resultSet.getString("title")).append("\n")
                            .append("Authors: ").append(resultSet.getString("authors")).append("\n")
                            .append("Abstract: ").append(resultSet.getString("abstract")).append("\n")
                            .append("Keywords: ").append(resultSet.getString("keywords")).append("\n")
                            .append("Body: ").append(resultSet.getString("body")).append("\n")
                            .append("References: ").append(resultSet.getString("references")).append("\n")
                            .append("Level: ").append(resultSet.getString("level")).append("\n")          // Add Level
                            .append("Identifier: ").append(resultSet.getString("identifier")).append("\n") // Add Identifier
                            .append("System Info: ").append(resultSet.getString("systemInfo")).append("\n") // Add System Info
                            .append("\n");
                    }
                }   
           }
            
           if (!found)
        	   return "No articles found for the specified groups.";
           
        }
        return result.toString();
    }


	/**********
	 * Deletes an article from the database based on its ID
	 */
	public void deleteArticle(int articleId) throws Exception {
	    // SQL query to check if the article exists
	    String query = "SELECT COUNT(*) FROM articles WHERE id = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setInt(1, articleId);
	        ResultSet rs = pstmt.executeQuery();
	        if (rs.next() && rs.getInt(1) > 0) {
	            // SQL statement to delete the article by ID
	            String deleteSQL = "DELETE FROM articles WHERE id = ?";
	            try (PreparedStatement deletePstmt = connection.prepareStatement(deleteSQL)) {
	                deletePstmt.setInt(1, articleId);
	                deletePstmt.executeUpdate();
	                System.out.println("Article with ID '" + articleId + "' has been deleted.");
	            }
	        } else {
	            System.out.println("No article found with the ID '" + articleId + "'.");
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	
	/**
	 * Deletes articles from the database based on their identifier.
	 * @param identifier The identifier of the articles to delete.
	 */
	public void deleteArticlesByIdentifier(String identifier) throws Exception {
	    // SQL query to check if any articles exist with the given identifier
	    String query = "SELECT COUNT(*) FROM articles WHERE identifier = ?";

	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, identifier);
	        ResultSet rs = pstmt.executeQuery();

	        // Check if there are articles with the specified identifier
	        if (rs.next() && rs.getInt(1) > 0) {  
	            // SQL statement to delete articles by identifier
	            String deleteSQL = "DELETE FROM articles WHERE identifier = ?";
	            try (PreparedStatement deletePstmt = connection.prepareStatement(deleteSQL)) {
	                deletePstmt.setString(1, identifier);
	                int rowsAffected = deletePstmt.executeUpdate();

	                System.out.println(rowsAffected + " article(s) with identifier '" + identifier + "' have been deleted.");
	            }
	        } else {
	            System.out.println("No articles found with the identifier '" + identifier + "'.");
	        }
	    } catch (SQLException e) {
	        // Handle any SQL exceptions
	        System.err.println("SQL error occurred: " + e.getMessage());
	        e.printStackTrace();
	    }
	}

	/**
	 * Updates an existing article in the database
	 */
	public void updateArticle(int articleId, Article articleToUpdate) throws Exception {
		if (articleToUpdate.systemInfo == null) {
	        articleToUpdate.systemInfo = "general";  // Set default to "general" if null
	    }
	    // Encrypt the article body using the author's name as part of the initialization vector
	    String encryptedBody = Base64.getEncoder().encodeToString(
	            encryptionHelper.encrypt(articleToUpdate.body.getBytes(), EncryptionUtils.getInitializationVector(articleToUpdate.authors.toCharArray()))
	    );

	    // SQL statement to update an existing article
	    
	    String updateArticle = "UPDATE articles SET title = ?, authors = ?, abstract = ?, keywords = ?, body = ?, references = ?, level = ?, identifier = ?, systemInfo = ? WHERE id = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(updateArticle)) {
	        pstmt.setString(1, articleToUpdate.title);
	        pstmt.setString(2, articleToUpdate.authors);
	        pstmt.setString(3, articleToUpdate.abstractText);
	        pstmt.setString(4, articleToUpdate.keywords);
	        pstmt.setString(5, encryptedBody);
	        pstmt.setString(6, articleToUpdate.references);
	        pstmt.setString(7, articleToUpdate.level);           // Update field for level
	        pstmt.setString(8, articleToUpdate.identifier);      // Update field for identifier
	        pstmt.setString(9, articleToUpdate.systemInfo);      // Update field for system information
	        pstmt.setInt(10, articleId); // Ensure you set this parameter
	        pstmt.executeUpdate();
	    }
	}



	/**
	 * Clears all articles from the database
	 */
	public void clearDatabase() throws Exception {
		// SQL statement to delete all articles
		String clearSQL = "DELETE FROM articles";
		try (Statement stmt = connection.createStatement()) {
			int rowsAffected = stmt.executeUpdate(clearSQL);
			System.out.println("Cleared " + rowsAffected + " articles from the database.");
		} catch (SQLException e) {
			System.err.println("Error clearing the database: " + e.getMessage());
		}
	}
	
    /** 
     * Decrypting code
     */
    private String decryptField(String encryptedField, String ivSource) throws Exception {
		return new String(encryptionHelper.decrypt(
	            Base64.getDecoder().decode(encryptedField),
	            EncryptionUtils.getInitializationVector(ivSource.toCharArray())
				));
    }
    
    /**
     * Returns the body of the article with the given ID
     */
    String getBody(int articleId) throws Exception {
        // SQL query to retrieve the body of the article by ID
        String query = "SELECT body FROM articles WHERE id = ?";
        String result = "";
        
        // Execute the query and format the results
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, articleId);
            ResultSet resultSet = pstmt.executeQuery(); 
            
            if (resultSet.next()) {
                result = resultSet.getString("body");
            } else {
                return "No articles found in the database.";
            }
        }
        return result;
    }

    /**
     * Retrieves the systemInfo of all articles in the database as a Set<String> to avoid duplicates
     */
    public Set<String> getAllSystemInfo() throws SQLException {
        Set<String> systemInfoSet = new HashSet<>();
        String query = "SELECT systemInfo FROM articles";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
            	String systemInfo = rs.getString("systemInfo");
                // Only add non-null systemInfo to the set
                if (systemInfo != null && systemInfoSet.contains(systemInfo)) {
                    systemInfoSet.add(systemInfo);
                }
            	
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving systemInfo: " + e.getMessage());
        }

        return systemInfoSet;
    }

    // Backup articles to a file
    public void backupArticles(String fileName) throws Exception {
        String sql = "SELECT * FROM articles";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql);
             BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {

            while (rs.next()) {
            	String systemInfo = rs.getString("systemInfo");
                if (systemInfo == null) {
                    systemInfo = "general";
                }
                writer.write(rs.getInt("id") + "," + rs.getString("title") + "," + rs.getString("authors") + ","
                        + rs.getString("abstract") + "," + rs.getString("keywords") + ","
                        + rs.getString("body") + "," + rs.getString("references") + "," + rs.getString("level") + "," + rs.getString("identifier") + "," + systemInfo);
                writer.newLine();
            }
        }
    }

    // Restore articles from a file
    public void restoreArticles(String fileName) throws Exception {
        // Clear the database first
        clearDatabase();

        String sql = "INSERT INTO articles (id, title, authors, abstract, keywords, body, references, level, identifier, systemInfo) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName));
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                String systemInfo = data[9];
                if (systemInfo == null || systemInfo.trim().isEmpty()) {
                    systemInfo = "general";  // Set default to "general"
                }
                
                for (String d: data) {
                	System.out.println(d);
//                	System.out.println(data[8]);
                }
                
                pstmt.setInt(1, Integer.parseInt(data[0]));
                pstmt.setString(2, data[1]);
                pstmt.setString(3, data[2]);
                pstmt.setString(4, data[3]);
                pstmt.setString(5, data[4]);
                pstmt.setString(6, data[5]);
                pstmt.setString(7, data[6]);
                pstmt.setString(8, data[7]);
                pstmt.setString(9, data[8]);
                pstmt.setString(10, systemInfo);
                pstmt.executeUpdate();
            }
        }
    }
    
    public void backupArticlesByGroup(String fileName, String groupName) throws Exception {
        String sql = "SELECT * FROM articles WHERE systemInfo = ?";
        try(PreparedStatement pstmt = connection.prepareStatement(sql);
            BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
        	
        	pstmt.setString(1, groupName);
        	try(ResultSet rs = pstmt.executeQuery()){
        		while (rs.next()) {
        			String systemInfo = rs.getString("systemInfo");
                    if (systemInfo == null) {
                        systemInfo = "general";
                    }
                    
                    writer.write(rs.getInt("id") + "," + 
                    			 rs.getString("title") + "," +
                    			 rs.getString("authors") + "," + 
                    			 rs.getString("abstract") + "," + 
                    			 rs.getString("keywords") + "," +
                    			 rs.getString("body") + "," +
                    			 rs.getString("references") + "," +
                    			 rs.getString("level") + "," + 
                    			 rs.getString("identifier") + "," + 
                    			 systemInfo);
                    writer.newLine();
                }
        	}
        }
    }

	/**
	 * Closes the database connection and statement resources
	 */
	public void closeConnection() {
		try { 
			if (statement != null) statement.close(); 
		} catch (SQLException se2) { 
			se2.printStackTrace();
		} 
		try { 
			if (connection != null) connection.close(); 
		} catch (SQLException se) { 
			se.printStackTrace(); 
		} 
	}

}
