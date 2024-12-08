package application;

import java.util.*;
/**
 * <p>Student Class</p>
 *
 * <p>Description: The Student class includes functionality for sending messages, managing user-related data, and handling a message.
 * Students can compose and send messages with specific word limits, and these messages are stored.
 * @author: Group Tu64
 *
 * @version: 1.00 2024-10-02
 * @version: 2.00 2024-11-19 - message functionality 
 */
public class Student extends User {

    private Map<String, User> userDatabase = new HashMap<>();
    private static List<String> messageDataset = new ArrayList<>();  // To store the messages

    public Student(String username, char[] password, Map<String, User> userDatabase) {
        super(username, password);
        this.userDatabase = userDatabase;
    }  
    /**
     * Sends a specific message with a subject and description.
     *
     * @param subject The subject of the message (max 100 words).
     * @param description The description of the message (max 100 words).
     * @return True if the message is successfully sent, false otherwise.
     **/
    public boolean sendMessage(String subject, String description) {
        // Check for null or empty inputs
        if (subject == null || subject.isEmpty() || description == null || description.isEmpty()) {
            System.out.println("Subject and description cannot be empty.");
            return false;
        }

        // Split the subject and description into words and count them
        int subjectWordCount = countWords(subject);
        int descriptionWordCount = countWords(description);

        // Check if either exceeds 100 words
        if (subjectWordCount > 100) {
            System.out.println("Subject exceeds the 100-word limit.");
            subject = trimToMaxWords(subject, 100); // Truncate subject to 100 words
        }

        if (descriptionWordCount > 100) {
            System.out.println("Description exceeds the 100-word limit.");
            description = trimToMaxWords(description, 100); // Truncate description to 100 words
        }

        // Format the message
        String message = "Subject: " + subject + "\nDescription: " + description + "\nFrom: " + getUserName();
        
        // Add the message to the dataset
        messageDataset.add(message);
        return true;
    }

    // Helper method to count words
    private int countWords(String text) {
        String[] words = text.trim().split("\\s+");
        return words.length;
    }

    // Helper method to truncate text to the max number of words
    private String trimToMaxWords(String text, int maxWords) {
        String[] words = text.trim().split("\\s+");
        if (words.length <= maxWords) {
            return text;
        }
        StringBuilder trimmedText = new StringBuilder();
        for (int i = 0; i < maxWords; i++) {
            trimmedText.append(words[i]).append(" ");
        }
        return trimmedText.toString().trim();
    }
    /**
     * Retrieves the dataset of messages sent by students.
     *
     * @return A list of messages, where each message is a formatted string containing the subject, description, and sender details.
     */
    public List<String> getMessageDataset() {
        return messageDataset;
    }
}
