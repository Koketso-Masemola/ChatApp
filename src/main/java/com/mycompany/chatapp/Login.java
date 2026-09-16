/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.chatapp;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 *
 * @author USER
 */
public class Login {
    //variables to save user info
    private String registeredUsername;
    private String registeredPassword;
    private String registeredCellPhoneNumber;
    private String firstName;
    private String lastName;
    //empty constructor
    public Login() {
    }
   //constructor to set the user names
    public Login(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    // make sure username has an underscore and is 5 chars or less
    public boolean checkUserName(String username) {
        return username != null && username.contains("_") && username.length() <= 5;
    }

    //check if password has a capital letter, number and special character
    public boolean checkPasswordComplexity(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }
        
        boolean hasCapital = false;
        boolean hasNumber = false;
        boolean hasSpecial = false;
        
//loop through each character in the password
        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) {
                hasCapital = true;
            } else if (Character.isDigit(c)) {
                hasNumber = true;
            } else if (!Character.isLetterOrDigit(c)) {
                hasSpecial = true;
            }
        }

        return hasCapital && hasNumber && hasSpecial;
    }

    //check if the phone number has the country code and the right length
    public boolean checkCellPhoneNumber(String cellPhoneNumber) {
        if (cellPhoneNumber == null) {
            return false;
        }
        //pattern to check the international number format
        String regex = "^\\+27\\d{1,9}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(cellPhoneNumber);
        
        // Also enforce the total length requirement (no more than 10 digits excluding country code, or <= 13 total chars)
        return matcher.matches();
    }

    //tests all inputs and register if everything is valid
    public String registerUser(String username, String password, String cellPhoneNumber) {
        if (!checkUserName(username)) {
            return "Username is not correctly formatted; please ensure that your username contains an underscore and is no more than five characters in length.";
        }

        if (!checkPasswordComplexity(password)) {
            return "Password is not correctly formatted; please ensure that the password contains at least eight characters, a capital letter, a number, and a special character.";
        }

        if (!checkCellPhoneNumber(cellPhoneNumber)) {
            return "Cell number is incorrectly formatted or does not contain an international code; please correct the number and try again.";
        }

        //save the user details
        this.registeredUsername = username;
        this.registeredPassword = password;
        this.registeredCellPhoneNumber = cellPhoneNumber;

       //all three fields are valid, so report each success message and welcome the user
        return """
               Username successfully captured.
               Password successfully captured.
               Cell phone number successfully added.
               Welcome """ + firstName + "," + lastName + " it is great to see you.";
    }

    //compare given details with saved credentials
    public boolean loginUser(String username, String password) {
        if (this.registeredUsername == null || this.registeredPassword == null) {
            return false;
        }
        return this.registeredUsername.equals(username) && this.registeredPassword.equals(password);
    }

    //show welcome message if login is true,else show error 
    public String returnLoginStatus(boolean isLoggedIn) {
        if (isLoggedIn) {
            return "Welcome " + firstName + ", " + lastName + " it is great to see you again.";
        } else {
            return "Username or password incorrect, please try again.";
        }
    }

    // Getters and Setters methods
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
}