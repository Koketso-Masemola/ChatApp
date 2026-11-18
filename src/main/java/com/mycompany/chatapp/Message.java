package com.mycompany.chatapp;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;


public class Message {

    // The maximum number of characters a message is allowed to have
    public static final int MAX_LENGTH = 250;

    // Part 3: these are the arrays the assignment asks for. I used ArrayLists because they
    // are arrays that grow by themselves, and I don't know how many messages the user will enter.
    // They are static so every Message object shares the same lists.
    private static final List<Message> sentMessages = new ArrayList<>();        // all messages that were sent
    private static final List<Message> disregardedMessages = new ArrayList<>(); // all messages that were disregarded
    private static final List<Message> storedMessages = new ArrayList<>();      // all messages that were stored (also read from the JSON file)
    private static final List<String> messageHashes = new ArrayList<>();        // every message hash
    private static final List<String> messageIDs = new ArrayList<>();           // every message ID

    private static int messageCounter = 0;            // gives each new message its own number
    private static String storagePath = "messages.json"; // the JSON file name
    private static String currentSender = "Developer";   // who is sending (Main sets this to the logged in user)

    // Each message keeps its own details. They are final because they should not change after creation.
    private final String messageID;
    private final int messageNumber;
    private final String sender;
    private final String recipient;
    private final String text;
    private final String messageHash;

    // Normal constructor: the ID is generated randomly, so it calls the other constructor
    public Message(String recipient, String text) {
        this(generateMessageID(), recipient, text);
    }

    // Constructor where I choose the ID myself. I use it in the unit tests so I know what the ID is.
    public Message(String messageID, String recipient, String text) {
        this.messageID = messageID;
        this.messageNumber = messageCounter++; // take the current number, then add 1 for the next message
        this.sender = currentSender;
        this.recipient = recipient;
        this.text = text;
        this.messageHash = createMessageHash(); // built last because it needs the other fields
    }

    // Private constructor that I only use when I read a message back from the JSON file.
    // It keeps the saved hash and number as they were and does NOT add 1 to the counter.
    private Message(String messageID, int messageNumber, String sender,
                    String recipient, String text, String messageHash) {
        this.messageID = messageID;
        this.messageNumber = messageNumber;
        this.sender = sender;
        this.recipient = recipient;
        this.text = text;
        this.messageHash = messageHash;
    }

    // ---------- Message ID ----------

    // Makes a random 10-digit number and returns it as a String
    private static String generateMessageID() {
        long id = ThreadLocalRandom.current().nextLong(1_000_000_000L, 10_000_000_000L);
        return String.valueOf(id);
    }

    // True only if the ID exists, is not empty and is at most 10 characters long
    public boolean checkMessageID() {
        return messageID != null && !messageID.isEmpty() && messageID.length() <= 10;
    }

    // Returns a sentence showing the ID
    public String messageIDGenerated() {
        return "Message ID generated: " + messageID;
    }

    // ---------- Recipient number ----------

    // Checks this message's own recipient
    public String checkRecipientCell() {
        return checkRecipientCell(recipient);
    }

    // I reuse the cell number check from my Login class in Part 1
    public static String checkRecipientCell(String number) {
        if (new Login().checkCellPhoneNumber(number)) {
            return "Cell phone number successfully captured.";
        }
        return "Cell phone number is incorrectly formatted or does not contain an "
                + "international code. Please correct the number and try again.";
    }

    // ---------- Message length ----------

    // Compares the length of the message to the 250 limit
    public static String checkMessageLength(String message) {
        int length = message == null ? 0 : message.length(); // treat null as empty so it doesn't crash
        if (length <= MAX_LENGTH) {
            return "Message ready to send.";
        }
        return "Message exceeds 250 characters by " + (length - MAX_LENGTH)
                + "; please reduce the size.";
    }

    // ---------- Message hash ----------

    // The hash is: first 2 digits of the ID : message number : first word + last word (all capitals)
    public String createMessageHash() {
        // split("\\s+") breaks the text at the spaces so I get an array of words
        String[] words = text == null ? new String[0] : text.trim().split("\\s+");
        String first = words.length > 0 ? clean(words[0]) : "";
        String last = words.length > 0 ? clean(words[words.length - 1]) : "";
        String prefix = messageID.length() >= 2 ? messageID.substring(0, 2) : messageID;
        return (prefix + ":" + messageNumber + ":" + first + last).toUpperCase();
    }

    // Removes anything that is not a letter or number so punctuation is not in the hash
    private static String clean(String word) {
        return word.replaceAll("[^A-Za-z0-9]", "");
    }

    // ---------- Send, disregard or store ----------

    // 1 = send, 2 = disregard, 3 = store for later.
    // In Part 3 every choice also puts the message into its own array,
    // and the ID and hash go into the ID and hash arrays.
    public String sentMessage(int choice) {
        switch (choice) {
            case 1:
                sentMessages.add(this);
                trackIDAndHash();
                return "Message successfully sent.";
            case 2:
                disregardedMessages.add(this);
                trackIDAndHash();
                return "Press 0 to delete the message.";
            case 3:
                storedMessages.add(this);
                trackIDAndHash();
                storeMessage(); // save everything in storedMessages to the JSON file
                return "Message successfully stored.";
            default:
                return "Invalid option.";
        }
    }

    // Adds this message's ID and hash to the two arrays that hold all IDs and all hashes
    private void trackIDAndHash() {
        messageIDs.add(messageID);
        messageHashes.add(messageHash);
    }

    // ---------- JSON: writing ----------

    // Writes all stored messages to the JSON file. I build the text by hand with a StringBuilder.
    public static void storeMessage() {
        StringBuilder json = new StringBuilder("[\n");
        for (int i = 0; i < storedMessages.size(); i++) {
            Message m = storedMessages.get(i);
            json.append("  {\n")
                .append("    \"messageID\": \"").append(escape(m.messageID)).append("\",\n")
                .append("    \"messageNumber\": \"").append(m.messageNumber).append("\",\n")
                .append("    \"messageHash\": \"").append(escape(m.messageHash)).append("\",\n")
                .append("    \"sender\": \"").append(escape(m.sender)).append("\",\n")
                .append("    \"recipient\": \"").append(escape(m.recipient)).append("\",\n")
                .append("    \"message\": \"").append(escape(m.text)).append("\"\n")
                // a comma after every message except the last one, otherwise the JSON is invalid
                .append("  }").append(i < storedMessages.size() - 1 ? "," : "").append("\n");
        }
        json.append("]\n");
        try {
            Files.write(Paths.get(storagePath), json.toString().getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            System.out.println("Could not store message: " + e.getMessage());
        }
    }

    // Replaces characters that would break the JSON format (quotes, new lines and so on)
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

    // ---------- JSON: reading (Part 3) ----------

    // Reads the JSON file and fills the storedMessages array with what is saved in it.
    // It returns how many messages were loaded. If the file doesn't exist yet, it returns 0.
    public static int loadStoredMessages() {
        storedMessages.clear(); // start with an empty array so nothing is added twice
        Path path = Paths.get(storagePath);
        if (!Files.exists(path)) {
            return 0;
        }
        try {
            String content = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
            for (Map<String, String> item : parseJsonObjects(content)) {
                // skip anything that is not a full message
                if (!item.containsKey("messageID") || !item.containsKey("message")) {
                    continue;
                }
                String id = item.get("messageID");
                String hash = item.getOrDefault("messageHash", "");
                int number = 0;
                try {
                    number = Integer.parseInt(item.getOrDefault("messageNumber", "0"));
                } catch (NumberFormatException e) {
                    number = 0; // if the number is not valid I just use 0
                }
                Message m = new Message(id, number,
                        item.getOrDefault("sender", "Unknown"),
                        item.getOrDefault("recipient", ""),
                        item.get("message"), hash);
                storedMessages.add(m);

                // keep the ID and hash arrays up to date, without adding the same value twice
                if (!messageIDs.contains(id)) {
                    messageIDs.add(id);
                }
                if (!messageHashes.contains(hash)) {
                    messageHashes.add(hash);
                }
                // make sure new messages continue after the highest saved number
                if (number >= messageCounter) {
                    messageCounter = number + 1;
                }
            }
        } catch (IOException e) {
            System.out.println("Could not read stored messages: " + e.getMessage());
        }
        return storedMessages.size();
    }

    // A small JSON reader for the format that storeMessage() writes. I wrote it myself so the
    // project doesn't need an extra library. It goes through the text one character at a time:
    // "{" starts a message, "}" ends it, and the quoted texts inside are read as key, value, key, value...
    private static List<Map<String, String>> parseJsonObjects(String json) {
        List<Map<String, String>> result = new ArrayList<>();
        Map<String, String> current = null;
        String key = null;
        int i = 0;
        while (i < json.length()) {
            char c = json.charAt(i);
            if (c == '{') {
                current = new HashMap<>();
                key = null;
                i++;
            } else if (c == '}') {
                if (current != null) {
                    result.add(current);
                }
                current = null;
                i++;
            } else if (c == '"' && current != null) {
                // read one quoted text and turn the escaped characters back to normal
                StringBuilder sb = new StringBuilder();
                i++; // skip the opening quote
                while (i < json.length() && json.charAt(i) != '"') {
                    char ch = json.charAt(i);
                    if (ch == '\\' && i + 1 < json.length()) {
                        char next = json.charAt(i + 1);
                        switch (next) {
                            case 'n': sb.append('\n'); break;
                            case 'r': sb.append('\r'); break;
                            case 't': sb.append('\t'); break;
                            default:  sb.append(next); // handles \" and \\
                        }
                        i += 2;
                    } else {
                        sb.append(ch);
                        i++;
                    }
                }
                i++; // skip the closing quote
                if (key == null) {
                    key = sb.toString();            // the first text is the key
                } else {
                    current.put(key, sb.toString()); // the second text is its value
                    key = null;
                }
            } else {
                i++; // spaces, colons and commas are ignored
            }
        }
        return result;
    }

    // ---------- Part 3: reading the arrays ----------

    // The next methods return only the message texts of an array (used for display and unit tests)
    public static List<String> getSentMessageTexts() {
        return textsOf(sentMessages);
    }

    public static List<String> getDisregardedMessageTexts() {
        return textsOf(disregardedMessages);
    }

    public static List<String> getStoredMessageTexts() {
        return textsOf(storedMessages);
    }

    // Returns copies so other classes can't change my arrays by accident
    public static List<String> getMessageHashes() {
        return new ArrayList<>(messageHashes);
    }

    public static List<String> getMessageIDs() {
        return new ArrayList<>(messageIDs);
    }

    // Goes through a list of messages and collects only the text of each one
    private static List<String> textsOf(List<Message> messages) {
        List<String> texts = new ArrayList<>();
        for (Message m : messages) {
            texts.add(m.text);
        }
        return texts;
    }

    // ---------- Part 3: stored messages menu features ----------

    // (a) Shows the sender and the recipient of every stored message
    public static String displaySenderAndRecipient() {
        if (storedMessages.isEmpty()) {
            return "No stored messages.";
        }
        StringBuilder sb = new StringBuilder();
        for (Message m : storedMessages) {
            sb.append("Sender: ").append(m.sender)
              .append(" | Recipient: ").append(m.recipient).append("\n");
        }
        return sb.toString();
    }

    // (b) Finds the longest stored message by comparing the length of each text.
    // It returns an empty String if nothing is stored.
    public static String longestStoredMessage() {
        String longest = "";
        for (Message m : storedMessages) {
            if (m.text.length() > longest.length()) {
                longest = m.text;
            }
        }
        return longest;
    }

    // (c) Looks for a message ID in the sent, stored and disregarded arrays.
    // It returns the Message, or null if the ID is not found.
    public static Message findByMessageID(String id) {
        if (id == null) {
            return null;
        }
        String wanted = id.trim();
        for (Message m : sentMessages) {
            if (m.messageID.equals(wanted)) return m;
        }
        for (Message m : storedMessages) {
            if (m.messageID.equals(wanted)) return m;
        }
        for (Message m : disregardedMessages) {
            if (m.messageID.equals(wanted)) return m;
        }
        return null;
    }

    // (c) Shows the recipient and the message for an ID that the user searched for
    public static String searchByMessageID(String id) {
        Message m = findByMessageID(id);
        if (m == null) {
            return "No message found with ID: " + id;
        }
        return "Recipient: " + m.recipient + "\nMessage: " + m.text;
    }

    // (d) Collects the text of every sent or stored message for one recipient
    public static List<String> messagesForRecipient(String recipient) {
        List<String> found = new ArrayList<>();
        if (recipient == null) {
            return found;
        }
        String wanted = recipient.trim();
        for (Message m : sentMessages) {
            if (m.recipient.equals(wanted)) found.add(m.text);
        }
        for (Message m : storedMessages) {
            if (m.recipient.equals(wanted)) found.add(m.text);
        }
        return found;
    }

    // (d) Builds the text that is shown to the user for the recipient search
    public static String searchByRecipient(String recipient) {
        List<String> found = messagesForRecipient(recipient);
        if (found.isEmpty()) {
            return "No messages found for recipient: " + recipient;
        }
        StringBuilder sb = new StringBuilder("Messages for " + recipient.trim() + ":\n");
        for (int i = 0; i < found.size(); i++) {
            sb.append(i + 1).append(") ").append(found.get(i)).append("\n");
        }
        return sb.toString();
    }

    // (e) Deletes a stored message by its hash. It is removed from the stored array, from the
    // ID and hash arrays, and the JSON file is saved again so the file matches the array.
    public static String deleteByHash(String hash) {
        if (hash == null) {
            return "No stored message found with hash: null";
        }
        String wanted = hash.trim();
        for (int i = 0; i < storedMessages.size(); i++) {
            Message m = storedMessages.get(i);
            if (m.messageHash.equalsIgnoreCase(wanted)) {
                storedMessages.remove(i);
                messageHashes.remove(m.messageHash);
                messageIDs.remove(m.messageID);
                storeMessage();
                return "Message: \"" + m.text + "\" successfully deleted.";
            }
        }
        return "No stored message found with hash: " + wanted;
    }

    // (f) Report with the full details of all stored messages
    public static String reportStoredMessages() {
        return buildReport("STORED MESSAGES REPORT", storedMessages, "No stored messages.");
    }

    // Report with the full details of all sent messages (this is the "Display Report" test)
    public static String reportSentMessages() {
        return buildReport("SENT MESSAGES REPORT", sentMessages, "No messages have been sent yet.");
    }

    // Shared by both reports so I don't write the same loop twice
    private static String buildReport(String title, List<Message> messages, String emptyText) {
        if (messages.isEmpty()) {
            return emptyText;
        }
        StringBuilder sb = new StringBuilder(title + "\n");
        for (Message m : messages) {
            sb.append("\n").append(m.reportDetails());
        }
        return sb.toString();
    }

    // One message's full details for a report
    private String reportDetails() {
        return "Sender: " + sender + "\n"
             + "Message ID: " + messageID + "\n"
             + "Message Hash: " + messageHash + "\n"
             + "Recipient: " + recipient + "\n"
             + "Message: " + text + "\n";
    }

    // ---------- Display (from Part 2) ----------

    // All details of this one message, in the order the assignment wants
    public String details() {
        return "Message ID: " + messageID + "\n"
             + "Message Hash: " + messageHash + "\n"
             + "Recipient: " + recipient + "\n"
             + "Message: " + text + "\n";
    }

    // Joins the details of every sent message
    public static String printMessages() {
        StringBuilder sb = new StringBuilder();
        for (Message m : sentMessages) {
            sb.append(m.details()).append("\n");
        }
        return sb.toString();
    }

    // How many messages have been sent
    public static int returnTotalMessages() {
        return sentMessages.size();
    }

    // How many messages are stored
    public static int returnTotalStoredMessages() {
        return storedMessages.size();
    }

    // ---------- Getters and helpers ----------

    public String getMessageID()   { return messageID; }
    public String getMessageHash() { return messageHash; }
    public String getSender()      { return sender; }
    public String getRecipient()   { return recipient; }
    public String getText()        { return text; }

    // Lets me change the file name, e.g. to use a test file
    public static void setStoragePath(String path) { storagePath = path; }

    // Sets who the messages are from. I call this in Main with the logged in user's name.
    public static void setSender(String name) {
        if (name != null && !name.trim().isEmpty()) {
            currentSender = name.trim();
        }
    }

    // Empties every array and resets the counter and sender. I call this before each unit test
    // so one test's messages don't affect the next one. (The file path is left as it is.)
    public static void reset() {
        sentMessages.clear();
        disregardedMessages.clear();
        storedMessages.clear();
        messageHashes.clear();
        messageIDs.clear();
        messageCounter = 0;
        currentSender = "Developer";
    }
}
