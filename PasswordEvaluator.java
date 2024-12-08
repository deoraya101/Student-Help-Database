package application;

/**
 * <p>PasswordEvaluator Class</p>
 *
 * <p>Description: A Java-based password evaluator that checks if passwords meet specific criteria.</p>
 *
 * <p>Author: Group Tu64</p>
 *
 * <p>Version: 1.00 2024-10-02</p>
 */
public class PasswordEvaluator { 

    /**********************************************************************************************
     * Attributes
     **********************************************************************************************/
    
    // Static variables to store the evaluation results and error messages
    public static String passwordErrorMessage = ""; // Stores error messages related to password validation
    public static String passwordInput = ""; // Stores the password input for evaluation
    public static int passwordIndexofError = -1; // Index of the character where an error occurred
    public static boolean foundUpperCase = false; // Indicates if an uppercase letter is found
    public static boolean foundLowerCase = false; // Indicates if a lowercase letter is found
    public static boolean foundNumericDigit = false; // Indicates if a numeric digit is found
    public static boolean foundSpecialChar = false; // Indicates if a special character is found
    public static boolean foundLongEnough = false; // Indicates if the password is long enough
    private static String inputLine = ""; // Holds the password input
    private static char currentChar; // The current character being evaluated
    private static int currentCharNdx; // The index of the current character
    private static boolean running; // Flag to control the evaluation loop

    /**********************************************************************************************
     * Password Evaluator Actions
     **********************************************************************************************/

    /**
     * Displays the current state of the input password, including the size, current index,
     * and the character being evaluated.
     */
    private static void displayInputState() {
        System.out.println(inputLine);
        System.out.println(inputLine.substring(0, currentCharNdx) + "?"); // Display the current password with a marker
        System.out.println("The password size: " + inputLine.length() + "  |  The currentCharNdx: " + 
                           currentCharNdx + "  |  The currentChar: \"" + currentChar + "\""); // Display characteristics of the password
    }

    /**
     * Public static method that evaluates the password based on predefined criteria.
     * 
     * @param input The password string to be evaluated.
     * @return A message indicating the result of the evaluation, including any errors found.
     */
    public static String evaluatePassword(String input) {
        // Initialize variables for evaluation
        passwordErrorMessage = ""; // Clear any previous error messages
        passwordIndexofError = 0; // Reset error index
        inputLine = input; // Set the input line to the current password
        currentCharNdx = 0; // Start evaluation at the first character
        
        // Check for an empty password
        if (input.length() <= 0) return "*** Error *** The password is empty!";

        currentChar = input.charAt(0); // Get the first character of the password

        // Initialize flags to check password requirements
        passwordInput = input; // Store the input for later use
        foundUpperCase = false;
        foundLowerCase = false;    
        foundNumericDigit = false;
        foundSpecialChar = false;
        foundLongEnough = false; // Initialize long enough check
        running = true; // Start the evaluation loop

        // Evaluate each character of the password
        while (running) {
            displayInputState(); // Display current state for debugging

            // Check for uppercase letters
            if (currentChar >= 'A' && currentChar <= 'Z') {
                System.out.println("Upper case letter found");
                foundUpperCase = true;
            // Check for lowercase letters
            } else if (currentChar >= 'a' && currentChar <= 'z') {
                System.out.println("Lower case letter found");
                foundLowerCase = true;
            // Check for numeric digits
            } else if (currentChar >= '0' && currentChar <= '9') {
                System.out.println("Digit found");
                foundNumericDigit = true;
            // Check for special characters
            } else if ("~`!@#$%^&*()_-+={}[]|\\:;\"'<>,.?/".indexOf(currentChar) >= 0) {
                System.out.println("Special character found");
                foundSpecialChar = true;
            // Handle invalid characters
            } else {
                passwordIndexofError = currentCharNdx; // Store index of the invalid character
                return "*** Error *** An invalid character has been found!";
            }
            
            // Check password length (minimum 8 characters)
            if (currentCharNdx >= 7) {
                System.out.println("At least 8 characters found");
                foundLongEnough = true;
            }
            
            // Move to the next character or end if at the end of the password
            currentCharNdx++; // Increment to the next character
            if (currentCharNdx >= inputLine.length()) // Check if at the end of the password
                running = false; // Stop the evaluation
            else
                currentChar = input.charAt(currentCharNdx); // Move to the next character
        }
        
        // Construct the error message based on unmet criteria
        String errMessage = "";
        // Check each requirement and append to error message if not met
        if (!foundUpperCase) errMessage += "Upper case; ";
        if (!foundLowerCase) errMessage += "Lower case; ";
        if (!foundNumericDigit) errMessage += "Numeric digits; ";
        if (!foundSpecialChar) errMessage += "Special character; ";
        if (!foundLongEnough) errMessage += "Long Enough; ";
        
        // Return error messages if there were unmet criteria
        if (!errMessage.isEmpty()) {
            passwordIndexofError = currentCharNdx; // Update the index of error for feedback
            return errMessage + "conditions were not satisfied";
        }

        // If all criteria are met, return an empty string indicating success
        return "";
    }
}
