package com.mycompany.chatapp;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class MessageTest {

    private static final String MSG1 = "Hi Mike, can you join us for dinner tonight?";
    private static final String MSG2 = "Hi Keegan, did you receive the payment?";

    @TempDir
    Path tmp;

    @BeforeEach
    void setUp() {
        Message.reset();
        Message.setStoragePath(tmp.resolve("messages.json").toString());
    }

    // ---- Message length ----

    @Test
    void messageLengthSuccess() {
        assertEquals("Message ready to send.", Message.checkMessageLength(MSG1));
    }

    @Test
    void messageLengthFailure() {
        String tooLong = "a".repeat(260);
        assertEquals("Message exceeds 250 characters by 10; please reduce the size.",
                Message.checkMessageLength(tooLong));
    }

    // ---- Recipient ----

    @Test
    void recipientSuccess() {
        Message m = new Message("0012345678", "+27718693002", MSG1);
        assertEquals("Cell phone number successfully captured.", m.checkRecipientCell());
    }

    @Test
    void recipientFailure() {
        Message m = new Message("0012345678", "08575975889", MSG2);
        assertEquals("Cell phone number is incorrectly formatted or does not contain an "
                + "international code. Please correct the number and try again.",
                m.checkRecipientCell());
    }

    // ---- Hash ----

    @Test
    void messageHashTestCase1() {
        Message m = new Message("0012345678", "+27718693002", MSG1);
        assertEquals("00:0:HITONIGHT", m.getMessageHash());
    }

    @Test
    void messageHashesInLoop() {
        String[][] data = {
            {"0012345678", "+27718693002", MSG1,  "00:0:HITONIGHT"},
            {"1234567890", "+27835551234", MSG2,  "12:1:HIPAYMENT"},
            {"9876543210", "+27721112222", "Hello there friend", "98:2:HELLOFRIEND"}
        };
        for (String[] row : data) {
            Message m = new Message(row[0], row[1], row[2]);
            assertEquals(row[3], m.getMessageHash());
        }
    }

    // ---- Message ID ----

    @Test
    void messageIDCreated() {
        Message m = new Message("+27718693002", MSG1);
        assertTrue(m.checkMessageID());
        assertEquals(10, m.getMessageID().length());
        assertEquals("Message ID generated: " + m.getMessageID(), m.messageIDGenerated());
    }

    @Test
    void messageIDTooLongFails() {
        Message m = new Message("12345678901", "+27718693002", MSG1);
        assertFalse(m.checkMessageID());
    }

    // ---- Send / Disregard / Store ----

    @Test
    void sendMessage() {
        Message m = new Message("+27718693002", MSG1);
        assertEquals("Message successfully sent.", m.sentMessage(1));
    }

    @Test
    void disregardMessage() {
        Message m = new Message("08575975889", MSG2);
        assertEquals("Press 0 to delete the message.", m.sentMessage(2));
    }

    @Test
    void storeMessage() throws Exception {
        Message m = new Message("0012345678", "+27718693002", MSG1);
        assertEquals("Message successfully stored.", m.sentMessage(3));
        String json = Files.readString(tmp.resolve("messages.json"));
        assertTrue(json.contains("\"messageHash\": \"00:0:HITONIGHT\""));
        assertTrue(json.contains("+27718693002"));
    }

    // ---- Totals ----

    @Test
    void totalMessagesSent() {
        Message m1 = new Message("+27718693002", MSG1);
        Message m2 = new Message("08575975889", MSG2);
        m1.sentMessage(1); // send
        m2.sentMessage(2); // discard
        assertEquals(1, Message.returnTotalMessages());
        assertTrue(Message.printMessages().contains(m1.getMessageID()));
        assertFalse(Message.printMessages().contains(m2.getMessageID()));
    }
}
