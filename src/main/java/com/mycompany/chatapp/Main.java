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
        //scanner to get input from the user
        Scanner scanner = new Scanner(System.in);
          //ask the user for their names
        System.out.println("ACCOUNT REGISTRATION");
        System.out.print("Enter First Name: ");
        String firstName = scanner.nextLine();

        System.out.print("Enter Last Name: ");
        String lastName = scanner.nextLine();
        //created a new login object with the names
        Login userLogin = new Login(firstName, lastName);

        System.out.print("Enter Username: ");
        String username = scanner.nextLine();

        System.out.print("Enter Password: ");
        String password = scanner.nextLine();

        System.out.print("Enter Cell Phone Number (with international code, e.g., +27838968976): ");
        String cellPhone = scanner.nextLine();
        
         //register the user and print the outcome
        String registrationResult = userLogin.registerUser(username, password, cellPhone);
        System.out.println(registrationResult);

        // check if the registration worked before trying to log in
        if (registrationResult.contains("successfully captured")) {
            System.out.println("USER LOGIN");
            //get login credentials from the user
            System.out.print("Enter Username: ");
            String loginUsername = scanner.nextLine();

            System.out.print("Enter Password: ");
            String loginPassword = scanner.nextLine();
            
           //verify login details and print status message
            boolean isAuthenticated = userLogin.loginUser(loginUsername, loginPassword);
            String loginMessage = userLogin.returnLoginStatus(isAuthenticated);
            
            System.out.println(loginMessage);

            //messaging is only available after a successful login
            if (isAuthenticated) {
                runQuickChat(scanner);
            }
        }

        scanner.close();
    }

    private static void runQuickChat(Scanner in) {
        System.out.println("\nWelcome to QuickChat.");
        int maxMessages = readInt(in, "How many messages do you wish to enter? ", 1, Integer.MAX_VALUE);
        int entered = 0;

        boolean running = true;
        while (running) {
            System.out.println("\n1) Send Messages");
            System.out.println("2) Show recently sent messages");
            System.out.println("3) Quit");
            int choice = readInt(in, "Choose an option: ", 1, 3);

            switch (choice) {
                case 1:
                    if (entered >= maxMessages) {
                        System.out.println("You have reached your limit of " + maxMessages + " messages.");
                        break;
                    }
                    processMessage(in);
                    entered++;
                    if (entered == maxMessages) {
                        System.out.println("\nAll messages entered. Total messages sent: "
                                + Message.returnTotalMessages());
                    }
                    break;
                case 2:
                    System.out.println("Coming Soon.");
                    break;
                default:
                    System.out.println("\nTotal messages sent: " + Message.returnTotalMessages());
                    System.out.println("Goodbye!");
                    running = false;
            }
        }
    }

    private static void processMessage(Scanner in) {
        String recipient;
        while (true) {
            System.out.print("Recipient cell number (e.g. +27718693002): ");
            recipient = in.nextLine().trim();
            String result = Message.checkRecipientCell(recipient);
            System.out.println(result);
            if (result.startsWith("Cell phone number successfully")) break;
        }

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

        Message msg = new Message(recipient, text);
        System.out.println(msg.messageIDGenerated());
        System.out.println("Message Hash: " + msg.getMessageHash());

        System.out.println("\n1) Send Message");
        System.out.println("2) Disregard Message");
        System.out.println("3) Store Message to send later");
        int option = readInt(in, "Choose an option: ", 1, 3);
        System.out.println(msg.sentMessage(option));

        if (option == 1) {
            System.out.println("\n" + msg.details());
        } else if (option == 2) {
            String key;
            do {
                System.out.print("> ");
                key = in.nextLine().trim();
            } while (!key.equals("0"));
            System.out.println("Message deleted.");
        }
    }

    private static int readInt(Scanner in, String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            try {
                int value = Integer.parseInt(in.nextLine().trim());
                if (value >= min && value <= max) return value;
            } catch (NumberFormatException ignored) { }
            System.out.println("Please enter a number between " + min
                    + (max == Integer.MAX_VALUE ? " and above." : " and " + max + "."));
        }
    }
}