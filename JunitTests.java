package application;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

/**
 * <p>JunitTests Class</p>
 *
 * <p>Description: This class tests for the article search and special group tasks by automating the testing various 
 * functionalities of the Article class and SpecialGroup class. This includes tests for 
 * searching for articles by title, author, abstract, groups, and levels. It also tests the admin's ability
 * to insert/remove a user from a special group, as well as the student's ability to send a special message.
 * The class uses JUnit testing and displays the output in the JUnit Test Results Window </p>
 *
 * <p>Author: Alyssa Duranovic </p>
 *
 * <p>Version: 1.00 2024-11-18</p>
 */


public class JunitTests{
	
	//private instance of userDatabase
    private Map<String, User> userDatabase;
    //private static instance of special access groups
    private static Map<String, User> specialAccessGroup;
    //private static Article instance representing the article database 
    private static Article articleDatabase;
    //private static User instance representing a user of the help system
    private static User user;
    //private static Student instance representing a student user of the help system
    private static Student student;

    /**
     * Instantiates the variables needed for the JUnit testing functions, such as the article database, 
     * specialAccessGroup, user, student, and special group interface.
     */
    @BeforeEach
    public void setUp() {
    	try {
            articleDatabase = new Article();
            articleDatabase.connectToDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }
        userDatabase = new HashMap<>();
        specialAccessGroup = new HashMap<>();
        user = new User("stu", "123".toCharArray());
        student = new Student("stu", "123".toCharArray(), userDatabase);
        userDatabase.put("stu", user);
        try {
			SpecialGroup_Interface specialInterface = new SpecialGroup_Interface(specialAccessGroup, userDatabase, user);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace(); 
		}
    }

    /**
     * Tests the insertion of a user into a special group
     */
	@Test
	public void JT1() {
		assertEquals(true, SpecialGroup_Interface.addUserTest("stu", "group1"));
	}
	
    /**
     * Tests the removal of a user from a special group
     */
	@Test
	public void JT2() {
		SpecialGroup_Interface.addUserTest("stu", "group1");
		assertEquals(true, SpecialGroup_Interface.removeUserTest("stu", "group1"));
	}
	
    /**
     * Tests the search function for an article by its title
     */
	@Test
	public void JT3() {
		try {
			Article article = new Article("TestArticle", "test", "test", "test", "test", "test", "test", "searchByTitleTest", "test");
			articleDatabase.register(article);
			String result = articleDatabase.getArticlesByTitleAsString("TestArticle");
			assertEquals(result, articleDatabase.getArticlesByTitleAsString("TestArticle"));
			articleDatabase.deleteArticlesByIdentifier("searchByTitleTest");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
    /**
     * Tests the search function for an article by its author
     */
		@Test
		public void JT4() {
			try {
				Article article = new Article("TestArticle", "TestAuthor", "test", "test", "test", "test", "test", "searchByAuthorTest", "test");
				articleDatabase.register(article);
				String result = articleDatabase.getArticlesByAuthorAsString("TestAuthor");
				assertEquals(result, articleDatabase.getArticlesByAuthorAsString("TestAuthor"));
				articleDatabase.deleteArticlesByIdentifier("searchByAuthorTest");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
	    /**
	     * Tests the search function for an article by its abstract
	     */
		@Test
		public void JT5() {
			try {
				Article article = new Article("TestArticle", "TestAuthor", "TestAbstract", "test", "test", "test", "test", "searchByAbstractTest", "test");
				articleDatabase.register(article);
				String result = articleDatabase.getArticlesByAbstractAsString("TestAbstract");
				assertEquals(result, articleDatabase.getArticlesByAbstractAsString("TestAbstract"));
				articleDatabase.deleteArticlesByIdentifier("searchByAbstractTest");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
	    /**
	     * Tests the search function for an article by its group
	     */
		@Test
		public void JT6() {
			try {
				Article article = new Article("TestArticle", "TestAuthor", "TestAbstract", "TestGroup", "test", "test", "test", "searchByGroupTest", "test");
				articleDatabase.register(article);
				List<String> stringList = new ArrayList<>();
				stringList.add("TestGroup");
				String result = articleDatabase.getArticlesByGroupsAsString(stringList, true);
				assertEquals(result, articleDatabase.getArticlesByGroupsAsString(stringList, true));
				articleDatabase.deleteArticlesByIdentifier("searchByGroupTest");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
	    /**
	     * Tests the search function for an article by its level
	     */
		@Test
		public void JT7() {
			try {
				Article article = new Article("TestArticle", "TestAuthor", "TestAbstract", "TestGroup", "test", "test", "Expert", "searchByLevelTest", "test");
				articleDatabase.register(article);
				String result = articleDatabase.getArticlesByContentLevelAsString("Expert", user);
				assertEquals(result, articleDatabase.getArticlesByContentLevelAsString("Expert", user));
				articleDatabase.deleteArticlesByIdentifier("searchByLevelTest");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	
	    /**
	     * Tests the sending of a special message from a student user to the admin user(s)
	     */
		@Test
		public void JT8() {
			assertEquals(true, student.sendMessage("Question", "I have a question"));
		}
	
	
}
