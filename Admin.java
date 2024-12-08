package application;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * <p>Admin Class</p>
 *
 * <p>Description: The Admin class represents an administrator who has special privileges in the system.
 * Admins can manage the user database by performing tasks such as deleting users, managing roles, resetting passwords,listing users,viewing student messages 
 * and backing up or restoring user data. The Admin class inherits properties and methods from the User class.</p>
 *
 * <p>Author: Group Tu64</p>
 *
 * @version: 1.00 2024-10-02
 * @version: 2.00 2024-11-19 Added functionality for viewing student messages 
 */
public class Admin extends User {


    private Map<String, User> userDatabase = new HashMap<>();     // A map that stores the user database where the key is the username and the value is a User object.
    private Map<String, User> specialAccessGroup = new HashMap<>(); // A map that stores the special access group memebers where the key is the username and the value is a User object.
    
    
    /**
     * Constructor for the Admin class.
     * Initializes the Admin with a username, password, and a reference to the user database.
     *
     * @param username     The admin's username.
     * @param password     The admin's password as a character array.
     * @param userDatabase The user database the admin will manage.
     */
    public Admin(String username, char[] password, Map<String, User> userDatabase, Map<String, User> specialAccessGroup) {
        super(username, password);  // Calls the parent class constructor to initialize the User attributes
        this.userDatabase = userDatabase;
        this.specialAccessGroup = specialAccessGroup;
    }
	   
    /**
     * Deletes a user from the user database.
     *
     * @param userName      The username of the user to be deleted.
     * @param userDatabase  The map containing the users.
     */
    public void deleteUser(String userName, Map<String, User> userDatabase) {
        if (userDatabase.remove(userName) != null) {
            System.out.println("User " + userName + " has been deleted.");
        } else {
            System.out.println("User " + userName + " not found.");
        }
    }

    /**
     * Adds a role to a specified user and returns a message indicating the result.
     * Valid roles are "Admin", "Student", and "Instructor".
     *
     * @param user  The User object to which the role should be added.
     * @param role  The role to be added.
     * @return A message indicating whether the role was added or an error occurred.
     */
    public String addRoleToUser(User user, String role) {
        String messageLabel = "";
        if (user != null) {
            if (role.equals("Admin") || role.equals("Student") || role.equals("Instructor")) {
                user.addRole(role);
                messageLabel = "Role " + role + " added to " + user.getUserName();
            } else {
                messageLabel = "Invalid role.";
            }
        } else {
            messageLabel = "User not found.";
        }
        return messageLabel;
    }

    /**
     * Removes a specified role from a user.
     *
     * @param user  The User object from which the role should be removed.
     * @param role  The role to be removed.
     */
    public void removeRoleFromUser(User user, String role) {
        if (user != null) {
            user.removeRole(role);
            System.out.println("Removed role " + role);
        } else {
            System.out.println("User not found.");
        }
    }

    /**
     * Generates a random one-time password (OTP) for resetting a user's password.
     * The OTP will consist of uppercase letters, lowercase letters, numbers, and symbols.
     *
     * @return A character array containing the generated OTP.
     */
    static char[] generate_OTP() {
        String capitalChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String smallChars = "abcdefghijklmnopqrstuvwxyz";
        String numbers = "0123456789";
        String symbols = "!@#$%^&*=+-/.?<>)";
        String values = capitalChars + smallChars + numbers + symbols;

        Random rand_method = new Random();

        // Generate a fixed length of 8
        int length = (int) ((Math.random() * (-4)) + 8);
        char[] oneTimePassword = new char[length];

        for (int i = 0; i < length; i++) {
            oneTimePassword[i] = values.charAt(rand_method.nextInt(values.length()));
        }

        return oneTimePassword;
    }

    /**
     * Resets a user's password using a one-time password (OTP).
     * The new password is set to "temporary", and the deadline for password reset is set for 24 hours.
     *
     * @param userName      The username of the user whose password is being reset.
     * @param userDatabase  The map containing the users.
     */
    @SuppressWarnings("unused")
	public void resetUserPassword(String userName, Map<String, User> userDatabase) {
        User user = userDatabase.get(userName);
        user.doReset();  // Added step to reset user state.
        if (user != null) {
            char[] oneTimePassword = generate_OTP();
            user.setPassword("temporary".toCharArray());  // Temporarily set password to "temporary"
            user.deadLine = LocalDateTime.now().plusDays(1);  // Directly modifying deadline.
            System.out.println("Password reset for " + userName + " with one-time password: " + String.valueOf(oneTimePassword));
        } else {
            System.out.println("User " + userName + " not found.");
        }
    }

    /**
     * Lists all users in the database, displaying their usernames and roles without revealing private information.
     */
    public void listUsers() {
        for (User user : userDatabase.values()) {
            System.out.println("Username: " + user.getUserName() + ", Roles: " + user.getRoles());
        }
    }

    /**
     * Backs up the current user data to a provided backup database.
     * A new copy of each user is made to avoid reference issues.
     *
     * @param userDatabaseBackup The map where user data will be backed up.
     */
    public void backupUserData(Map<String, User> userDatabaseBackup) {
        userDatabaseBackup.clear();  // Clear the backup first
        for (Map.Entry<String, User> entry : userDatabase.entrySet()) {
            User backupUser = new User(entry.getValue().getUserName(), entry.getValue().getPassword());
            backupUser.setEmail(entry.getValue().getEmail());
            backupUser.setFirstName(entry.getValue().getFirstName());
            backupUser.setLastName(entry.getValue().getLastName());
            backupUser.setRoles(entry.getValue().getRoles());
            userDatabaseBackup.put(entry.getKey(), backupUser);
        }
        System.out.println("User data has been backed up.");
    }

    /**
     * Restores user data from a backup database, replacing the current database.
     *
     * @param userDatabaseBackup The backup map from where user data will be restored.
     */
    public void restoreUserData(Map<String, User> userDatabaseBackup) {
        userDatabase.clear();  // Clear the current database
        userDatabase.putAll(userDatabaseBackup);
        System.out.println("User data has been restored.");
    }
    
    public List<String> viewStudentMessage() {
        List<String> messages = new ArrayList<String>();
        for (User student : userDatabase.values()) {
            if(student.getIsStudent()) {
                List<String> messageDataset = ((Student) student).getMessageDataset(); // Static call to getMessageDataset
                for (String message : messageDataset) {
                    messages.add(message);
                }
            }
        }
        return messages;
    } 
    
}
