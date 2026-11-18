package com.mycompany.chatapp;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class StoredMessagesTest {

    // The five test messages from the assignment
    private static final String MSG1 = "Did you get the cake?";
    private static final String MSG2 = "Where are you? You are late! I have asked you to be on time.";
    private static final String MSG3 = "Yohoooo, I am at your gate.";
    private static final String MSG4 = "It is dinner time!";
    private static final String MSG5 = "Ok, I am leaving without you.";

    @TempDir
    Path tmp; // a temporary folder, so the tests never touch my real messages.json

    private Message m1, m2, m3, m4, m5;


    // ---- Arrays are populated ----

    @Test
    void sentMessagesArrayCorrectlyPopulated() {
        assertEquals(List.of(MSG1, MSG4), Message.getSentMessageTexts());
    }

    @Test
    void disregardedMessagesArrayCorrectlyPopulated() {
        assertEquals(List.of(MSG3), Message.getDisregardedMessageTexts());
    }

    @Test
    void storedMessagesArrayCorrectlyPopulated() {
        assertEquals(List.of(MSG2, MSG5), Message.getStoredMessageTexts());
    }

    @Test
    void messageHashAndIDArraysCorrectlyPopulated() {
        assertEquals(List.of(m1.getMessageHash(), m2.getMessageHash(), m3.getMessageHash(),
                m4.getMessageHash(), m5.getMessageHash()), Message.getMessageHashes());
        assertEquals(List.of("0000000001", "0000000002", "0000000003", "0838884567", "0000000005"),
                Message.getMessageIDs());
    }

    // ---- Stored messages array is read from the JSON file ----

    @Test
    void storedMessagesAreReadFromJsonFile() {
        String hash2 = m2.getMessageHash();
        Message.reset(); // empties every array, so anything loaded must come from the file
        assertEquals(0, Message.returnTotalStoredMessages());

        assertEquals(2, Message.loadStoredMessages());
        assertEquals(List.of(MSG2, MSG5), Message.getStoredMessageTexts());
        assertTrue(Message.getMessageHashes().contains(hash2));
    }

    @Test
    void jsonKeepsQuotesAndNewLines() {
        Message tricky = new Message("0000000009", "+27834557896", "He said \"hi\"\nand left");
        tricky.sentMessage(3);
        Message.reset();
        Message.loadStoredMessages();
        assertTrue(Message.getStoredMessageTexts().contains("He said \"hi\"\nand left"));
    }

    // ---- Menu features ----

    @Test
    void displaySenderAndRecipient() {
        assertEquals("""
                     Sender: Developer | Recipient: +27838884567
                     Sender: Developer | Recipient: +27838884567
                     """,
                Message.displaySenderAndRecipient());
    }

    @Test
    void displayLongestMessage() {
        assertEquals(MSG2, Message.longestStoredMessage());
    }

    @Test
    void searchForMessageID() {
        assertEquals(MSG4, Message.findByMessageID("0838884567").getText());
        assertEquals("Recipient: 0838884567\nMessage: " + MSG4,
                Message.searchByMessageID("0838884567"));
        assertEquals("No message found with ID: 123", Message.searchByMessageID("123"));
    }

    @Test
    void searchMessagesForRecipient() {
        assertEquals(List.of(MSG2, MSG5), Message.messagesForRecipient("+27838884567"));
    }

    @Test
    void deleteMessageUsingHash() throws Exception {
        assertEquals("Message: \"" + MSG2 + "\" successfully deleted.",
                Message.deleteByHash(m2.getMessageHash()));
        assertEquals(List.of(MSG5), Message.getStoredMessageTexts());
        assertFalse(Message.getMessageHashes().contains(m2.getMessageHash()));
        // the JSON file must be updated as well
        assertFalse(Files.readString(tmp.resolve("messages.json")).contains(MSG2));
    }

    @Test
    void deleteWithUnknownHash() {
        assertEquals("No stored message found with hash: 99:9:NOPE",
                Message.deleteByHash("99:9:NOPE"));
        assertEquals(2, Message.returnTotalStoredMessages());
    }

    // ---- Reports ----

    @Test
    void displayReportOfSentMessages() {
        String report = Message.reportSentMessages();
        assertTrue(report.contains(m1.getMessageHash()));
        assertTrue(report.contains("+27834557896"));
        assertTrue(report.contains(MSG1));
        assertTrue(report.contains(m4.getMessageHash()));
        assertTrue(report.contains(MSG4));
        assertFalse(report.contains(MSG2)); // stored, not sent
    }

    @Test
    void displayReportOfStoredMessages() {
        String report = Message.reportStoredMessages();
        assertTrue(report.contains(m2.getMessageHash()));
        assertTrue(report.contains(m5.getMessageHash()));
        assertTrue(report.contains("+27838884567"));
        assertTrue(report.contains(MSG2));
        assertTrue(report.contains(MSG5));
        assertFalse(report.contains(MSG1));
    }
}
