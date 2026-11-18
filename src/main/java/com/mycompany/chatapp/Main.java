/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.chatapp;
import java.util.Scanner;
/**
 *
 * @author USER
 */


public class Main {
    public static void main(String[] args) {
        // One Scanner for the whole program so I can read what the user types
        Scanner scanner = new Scanner(System.in);

        // Part 1: registration. I start by asking for the user's names.
        System.out.println("ACCOUNT REGISTRATION");
        System.out.print("Enter First Name: ");
        String firstName = scanner.nextLine();

        System.out.print("Enter Last Name: ");
        String lastName = scanner.nextLine();

        // Make a Login object using the names, because the registration and login methods belong to it
        Login userLogin = new Login(firstName, lastName);

        System.out.print("Enter Username: ");
        String username = scanner.nextLine();

        System.out.print("Enter Password: ");
        String password = scanner.nextLine();

        System.out.print("Enter Cell Phone Number (with international code, e.g., +27838968976): ");
        String cellPhone = scanner.nextLine();

        // registerUser checks the username, password and cell number and returns a message
        String registrationResult = userLogin.registerUser(username, password, cellPhone);
        System.out.println(registrationResult);

        // Only let the user log in if registration worked
        if (registrationResult.contains("successfully captured")) {
            System.out.println("USER LOGIN");

            System.out.print("Enter Username: ");
            String loginUsername = scanner.nextLine();

            System.out.print("Enter Password: ");
            String loginPassword = scanner.nextLine();

            // loginUser gives true or false, and returnLoginStatus turns that into a message
            boolean isAuthenticated = userLogin.loginUser(loginUsername, loginPassword);
            String loginMessage = userLogin.returnLoginStatus(isAuthenticated);

            System.out.println(loginMessage);

            // The chat part only starts if the login was correct
            if (isAuthenticated) {
                // Part 3: the logged in user is the sender of the messages
                Message.setSender(firstName + " " + lastName);
                runQuickChat(scanner);
            }
        }

        // Done with the Scanner, so I close it
        scanner.close();
    }

    // Runs the QuickChat main menu
    private static void runQuickChat(Scanner in) {
        System.out.println("\nWelcome to QuickChat.");

        // Part 3: read the JSON file into the stored messages array when the chat starts,
        // so messages that were stored in an earlier run are still available
        int loaded = Message.loadStoredMessages();
        if (loaded > 0) {
            System.out.println(loaded + " stored message(s) loaded from the file.");
        }

        // Ask how many messages the user wants to enter (minimum 1, no upper limit)
        int maxMessages = readInt(in, "How many messages do you wish to enter? ", 1, Integer.MAX_VALUE);
        int entered = 0; // counts how many messages the user has entered so far

        // The menu keeps showing until the user chooses to quit
        boolean running = true;
        while (running) {
            System.out.println("\n1) Send Messages");
            System.out.println("2) Show recently sent messages");
            System.out.println("3) Quit");
            System.out.println("4) Stored Messages"); // the new fourth option for Part 3
            int choice = readInt(in, "Choose an option: ", 1, 4);

            switch (choice) {
                case 1:
                    // Stop the user from entering more messages than they said they wanted
                    if (entered >= maxMessages) {
                        System.out.println("You have reached your limit of " + maxMessages + " messages.");
                        break;
                    }
                    processMessage(in);
                    entered++;
                    // When they reach their limit, show the total number of messages sent
                    if (entered == maxMessages) {
                        System.out.println("\nAll messages entered. Total messages sent: "
                                + Message.returnTotalMessages());
                    }
                    break;
                case 2:
                    // In Part 3 this feature is finished: it shows a report of every sent message
                    System.out.println("\n" + Message.reportSentMessages());
                    break;
                case 3:
                    // Show the final total, say goodbye and end the loop
                    System.out.println("\nTotal messages sent: " + Message.returnTotalMessages());
                    System.out.println("Goodbye!");
                    running = false;
                    break;
                default:
                    // The only choice left is 4, which opens the stored messages menu
                    runStoredMessagesMenu(in);
            }
        }
    }

    // Part 3: the menu for working with the stored messages array
    private static void runStoredMessagesMenu(Scanner in) {
        boolean back = false;
        while (!back) {
            System.out.println("\nSTORED MESSAGES");
            System.out.println("1) Display the sender and recipient of all stored messages");
            System.out.println("2) Display the longest stored message");
            System.out.println("3) Search for a message ID");
            System.out.println("4) Search for all messages for a particular recipient");
            System.out.println("5) Delete a message using the message hash");
            System.out.println("6) Display a report of all stored messages");
            System.out.println("7) Back to the main menu");
            int choice = readInt(in, "Choose an option: ", 1, 7);

            switch (choice) {
                case 1:
                    System.out.println("\n" + Message.displaySenderAndRecipient());
                    break;
                case 2:
                    // longestStoredMessage gives an empty String when nothing is stored,
                    // so I check the count first to show a proper message
                    if (Message.returnTotalStoredMessages() == 0) {
                        System.out.println("\nNo stored messages.");
                    } else {
                        System.out.println("\nLongest stored message: " + Message.longestStoredMessage());
                    }
                    break;
                case 3:
                    System.out.print("Enter the message ID: ");
                    String id = in.nextLine();
                    System.out.println("\n" + Message.searchByMessageID(id));
                    break;
                case 4:
                    System.out.print("Enter the recipient's cell number: ");
                    String recipient = in.nextLine();
                    System.out.println("\n" + Message.searchByRecipient(recipient));
                    break;
                case 5:
                    System.out.print("Enter the message hash: ");
                    String hash = in.nextLine();
                    System.out.println("\n" + Message.deleteByHash(hash));
                    break;
                case 6:
                    System.out.println("\n" + Message.reportStoredMessages());
                    break;
                default:
                    back = true; // option 7: leave this menu and go back to the main menu
            }
        }
    }

    // Handles one whole message: get the recipient, get the text, then ask what to do with it
    private static void processMessage(Scanner in) {
        // Keep asking for the recipient's number until it is valid
        String recipient;
        while (true) {
            System.out.print("Recipient cell number (e.g. +27718693002): ");
            recipient = in.nextLine().trim(); // trim() removes extra spaces at the start and end
            String result = Message.checkRecipientCell(recipient);
            System.out.println(result);
            if (result.startsWith("Cell phone number successfully")) break;
        }

        // Keep asking for the message text until it is within the 250 character limit
        String text;
        while (true) {
            System.out.print("Enter your message: ");
            text = in.nextLine();
            String result = Message.checkMessageLength(text);
            if (result.equals("Message ready to send.")) {
                System.out.println("Message sent");
                break;
            }
            System.out.println("Please enter a message of less than 250 characters.");
            System.out.println(result);
        }

        // Both the recipient and the text are valid, so I can create the Message object
        Message msg = new Message(recipient, text);
        System.out.println(msg.messageIDGenerated());
        System.out.println("Message Hash: " + msg.getMessageHash());

        // Let the user decide what happens to the message
        System.out.println("\n1) Send Message");
        System.out.println("2) Disregard Message");
        System.out.println("3) Store Message to send later");
        int option = readInt(in, "Choose an option: ", 1, 3);
        System.out.println(msg.sentMessage(option));

        if (option == 1) {
            // If it was sent, show the full details of the message
            System.out.println("\n" + msg.details());
        } else if (option == 2) {
            // To disregard the message the user must type 0 to confirm
            String key;
            do {
                System.out.print("> ");
                key = in.nextLine().trim();
            } while (!key.equals("0"));
            System.out.println("Message deleted.");
        }
    }

    // Helper method to read a whole number between min and max. It keeps asking until the input is valid.
    private static int readInt(Scanner in, String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            try {
                // If the user types something that is not a number, parseInt throws an exception
                int value = Integer.parseInt(in.nextLine().trim());
                if (value >= min && value <= max) return value;
            } catch (NumberFormatException ignored) { } // not a number, so ask again
            System.out.println("Please enter a number between " + min
                    + (max == Integer.MAX_VALUE ? " and above." : " and " + max + "."));
        }
    }
}
