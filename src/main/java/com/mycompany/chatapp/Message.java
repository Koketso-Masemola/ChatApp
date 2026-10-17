package com.mycompany.chatapp;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Message {
    //The maximum number of characters a message is allowed to have
    public static final int MAX_LENGTH = 250;

    // These are static so they are shared by every Message object
    //sentMessage holds what the user sent , storedMessages holds what they saved for later
    //messageCounter gives each message its own number and storagePath is the JSON file name
    private static final List<Message> sentMessages = new ArrayList<>();
    private static final List<Message> storedMessages = new ArrayList<>();
    private static int messageCounter = 0;
    private static String storagePath = "messages.json";

    //Each message keeps its own details. They are final because they should never change after creation
    private final String messageID;
    private final int messageNumber;
    private final String recipient;
    private final String text;
    private final String messageHash;

   
    // Normal constructor. I use this when I don't care what the ID is,
    // so it calls the other constructor with a randomly generated ID
    public Message(String recipient, String text) {
        this(generateMessageID(), recipient, text);
    }

    
    public Message(String messageID, String recipient, String text) {
        this.messageID = messageID;
        this.messageNumber = messageCounter++;
        this.recipient = recipient;
        this.text = text;
        this.messageHash = createMessageHash();
    }

    // ID Handling

    private static String generateMessageID() {
        long id = ThreadLocalRandom.current().nextLong(1_000_000_000L, 10_000_000_000L);
        return String.valueOf(id);
    }

    //Make sure the ID is
    public boolean checkMessageID() {
        return messageID != null && !messageID.isEmpty() && messageID.length() <= 10;
    }

    public String messageIDGenerated() {
        return "Message ID generated: " + messageID;
    }

    // ---------- Recipient Validation----------

    // Reusing the phone number rules from part 1(+27 international code + number)
    public String checkRecipientCell() {
        return checkRecipientCell(recipient);
    }

    public static String checkRecipientCell(String number) {
        if (new Login().checkCellPhoneNumber(number)) {
            return "Cell phone number successfully captured.";
        }
        return "Cell phone number is incorrectly formatted or does not contain an "
                + "international code. Please correct the number and try again.";
    }

    // ---------- Message length Check ----------

    // Verify that the message text doesn't cross our 250-character limit
    public static String checkMessageLength(String message) {
        int length = message == null ? 0 : message.length();
        if (length <= MAX_LENGTH) {
            return "Message ready to send.";
        }
        return "Message exceeds 250 characters by " + (length - MAX_LENGTH)
                + "; please reduce the size.";
    }

    // ---------- Hash Generation ----------

    // Creates a unique tag : first 2 digits of ID: message count : FIRST AND LAST
       // words uppercase
    public String createMessageHash() {
        String[] words = text == null ? new String[0] : text.trim().split("\\s+");
        String first = words.length > 0 ? clean(words[0]) : "";
        String last = words.length > 0 ? clean(words[words.length - 1]) : "";
        String prefix = messageID.length() >= 2 ? messageID.substring(0, 2) : messageID;
        return (prefix + ":" + messageNumber + ":" + first + last).toUpperCase();
    }

    private static String clean(String word) {
        return word.replaceAll("[^A-Za-z0-9]", "");
    }

    // ---------- Send / Store / Discard Actions----------

   // Handles user choice:1= send, 2=disregard/delete prompt, 3=store for later
    public String sentMessage(int choice) {
        switch (choice) {
            case 1:
                sentMessages.add(this);
                return "Message successfully sent.";
            case 2:
                return "Press 0 to delete the message.";
            case 3:
                storedMessages.add(this);
                storeMessage();
                return "Message successfully stored.";
            default:
                return "Invalid option.";
        }
    }

    // Loops through all saved messages and dumps them neatly into a local JSON file
    public static void storeMessage() {
        StringBuilder json = new StringBuilder("[\n");
        for (int i = 0; i < storedMessages.size(); i++) {
            Message m = storedMessages.get(i);
            json.append("  {\n")
                .append("    \"messageID\": \"").append(escape(m.messageID)).append("\",\n")
                .append("    \"messageHash\": \"").append(escape(m.messageHash)).append("\",\n")
                .append("    \"recipient\": \"").append(escape(m.recipient)).append("\",\n")
                .append("    \"message\": \"").append(escape(m.text)).append("\"\n")
                .append("  }").append(i < storedMessages.size() - 1 ? "," : "").append("\n");
        }
        json.append("]\n");
        try {
            Files.write(Paths.get(storagePath), json.toString().getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            System.out.println("Could not store message: " + e.getMessage());
        }
    }

    
    // Some characters (like quotes or new lines) would break the JSON format,
    // so this replaces each of them with its escaped version before hey go into the file
    private static String escape(String s) {
        StringBuilder sb = new StringBuilder();
        for (char c : s.toCharArray()) {
            switch (c) {
                case '"':  sb.append("\\\""); break;
                case '\\': sb.append("\\\\"); break;
                case '\n': sb.append("\\n");  break;
                case '\r': sb.append("\\r");  break;
                case '\t': sb.append("\\t");  break;
                default:   sb.append(c);
            }
        }
        return sb.toString();
    }

    // Puts all of this message's details into one string , each on its own line

    public String details() {
        return "Message ID: " + messageID + "\n"
             + "Message Hash: " + messageHash + "\n"
             + "Recipient: " + recipient + "\n"
             + "Message: " + text + "\n";
    }

    // Goes through every sent messages and joins their details together to print them all
    public static String printMessages() {
        StringBuilder sb = new StringBuilder();
        for (Message m : sentMessages) {
            sb.append(m.details()).append("\n");
        }
        return sb.toString();
    }

   // Gives back how many messages have been sent so far
    public static int returnTotalMessages() {
        return sentMessages.size();
    }

    //  Getters and helper methods

    public String getMessageID()   { return messageID; }
    public String getMessageHash() { return messageHash; }
    public String getRecipient()   { return recipient; }
    public String getText()        { return text; }

    public static void setStoragePath(String path) { storagePath = path; }

    // Empties both lists and sets the counter back to 0. I call this before each unit test
    // so that one test's messages don't affect the next test's results
    public static void reset() {
        sentMessages.clear();
        storedMessages.clear();
        messageCounter = 0;
    }
}
