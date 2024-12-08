package application;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
/**
 * <p>Instructor Class</p>
 *
 * <p>Description: The Instructor class represents a user with instructor privileges. It extends the base User class,
 * adding functionality specific to instructors, such as managing user databases and special access groups.
 * The class also provides methods to determine if the current instructor is the first one in the system.</p>
 *
 *
 * @author: Group Tu64
 * @version 1.00 2024-11-19
 */
public class Instructor extends User {

    private Map<String, User> userDatabase;  // This will be passed as a parameter.
    private Map<String, User> specialAccessGroup = new HashMap<>(); // A map that stores the special access group members
   
    /**
     * Constructor for the Instructor class.
     *
     * @param username The username of the instructor.
     * @param password The password of the instructor.
     * @param userDatabase A map representing the user database.
     * @param specialAccessGroup A map representing the special access group members.
     */
    
    public Instructor(String username, char[] password, Map<String, User> userDatabase, Map<String, User> specialAccessGroup) {
        super(username, password);
        this.userDatabase = userDatabase;
        this.specialAccessGroup = specialAccessGroup;


    }
 /**
     * Checks if the current instructor is the first instructor in the system.
     *
     * @return true if the current instructor is the first one, false otherwise.
     */
    public boolean checkIfFirstInstructor() {
        for (Map.Entry<String, User> entry : userDatabase.entrySet()) {
            User user = entry.getValue();
            if (user.hasRole("Instructor")) {
                return false; // Found another instructor, so this is not the first one
            }
        }
        return true; // No instructor found, this is the first one
    }
}
