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
            
            System.out.println( loginMessage);
        }

        scanner.close();
    }
}