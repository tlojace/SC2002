package utils;

import java.util.Scanner;

public class Validator {
    public static int validateIntegerInput (Scanner scanner) {
        int input;
        while (true) {
            if (scanner.hasNextInt()) {
                input = scanner.nextInt();
                break;  // Input is valid, exit the loop
            } else {
                System.out.println("Invalid input. Try again.");
                scanner.next();  // Clear the invalid input
            }
        }
        return input;
    }

    // Method to validate string input (non-empty string)
    public static String validateStringInput(Scanner scanner) {
        scanner = new Scanner(System.in);
        while (true) {
            String input = scanner.nextLine().trim();
            if (!input.isEmpty()) {
                return input;
            } else {
                System.out.println("Invalid input. Please enter some text.");
            }
        }
    }

    // Validation 8 digit number
    public static boolean validateContactNo(String contactNo) {
        // Define the regex pattern: only digits, exactly 8 characters long
        String regex = "^\\d{8}$";

        // Check if the contact number matches the pattern
        if (!contactNo.matches(regex)) {
            System.out.println("Invalid Phone Number! (8 Digits)");
        }

        return contactNo.matches(regex);
    }

    // Verification Email
    public static boolean validateEmail(String email) {
        // Regex pattern for a basic email format
        String regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";

        // Check if the email matches the pattern
        if (!email.matches(regex)) {
            System.out.println("Invalid Email");
        }

        return email.matches(regex);
    }
}
