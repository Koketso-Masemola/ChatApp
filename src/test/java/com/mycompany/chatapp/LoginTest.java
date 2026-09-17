package com.mycompany.chatapp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class LoginTest {
    private Login login;

@BeforeEach
public void setUp() {
 login = new Login("Kyle", "User");
 }

// --- USERNAME TESTS ---
@Test
public void testCheckUserNameSuccess() {
assertTrue(login.checkUserName("kyl_1"));
}

@Test
public void testCheckUserNameFailure() {
assertFalse(login.checkUserName("kyle!!!!!!"));
}

 // --- PASSWORD TESTS ---
@Test
public void testCheckPasswordComplexitySuccess() {
 assertTrue(login.checkPasswordComplexity("Ch&&sec@ke99!"));
}

 @Test
public void testCheckPasswordComplexityFailure() {
assertFalse(login.checkPasswordComplexity("password"));
 }

 // --- CELL PHONE TESTS ---
@Test
public void testCheckCellPhoneNumberSuccess() {
 assertTrue(login.checkCellPhoneNumber("+27838968976"));
}

@Test
public void testCheckCellPhoneNumberFailure() {
assertFalse(login.checkCellPhoneNumber("08966553"));
}

// --- REGISTRATION & LOGIN MESSAGING TESTS ---
@Test
public void testRegisterUserUsernameFailureMessage() {
 String expectedMessage = "Username is not correctly formatted; please ensure that your username contains an underscore and is no more than five characters in length.";
String actualMessage = login.registerUser("kyle!!!!!!", "Ch&&sec@ke99!", "+27838968976");
 assertEquals(expectedMessage, actualMessage);
}

@Test
public void testRegisterUserPasswordFailureMessage() {
String expectedMessage = "Password is not correctly formatted; please ensure that the password contains at least eight characters, a capital letter, a number, and a special character.";
 String actualMessage = login.registerUser("kyl_1", "password", "+27838968976");
assertEquals(expectedMessage, actualMessage);
}

@Test
public void testRegisterUserCellPhoneFailureMessage() {
String expectedMessage = "Cell number is incorrectly formatted or does not contain an international code; please correct the number and try again.";
String actualMessage = login.registerUser("kyl_1", "Ch&&sec@ke99!", "08966553");
assertEquals(expectedMessage, actualMessage);
}

 @Test
public void testLoginUserSuccessAndMessage() {
login.registerUser("kyl_1", "Ch&&sec@ke99!", "+27838968976");

 boolean loginSuccess = login.loginUser("kyl_1", "Ch&&sec@ke99!");
assertTrue(loginSuccess);

String expectedMessage = "Welcome Kyle, User it is great to see you again.";
String actualMessage = login.returnLoginStatus(loginSuccess);
 assertEquals(expectedMessage, actualMessage);
 }

@Test
public void testLoginUserFailureAndMessage() {
login.registerUser("kyl_1", "Ch&&sec@ke99!", "+27838968976");

boolean loginSuccess = login.loginUser("kyl_1", "wrongPassword");
assertFalse(loginSuccess);

 String expectedMessage = "Username or password incorrect, please try again.";
String actualMessage = login.returnLoginStatus(loginSuccess);
assertEquals(expectedMessage, actualMessage);
 }
}