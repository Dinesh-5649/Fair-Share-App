package com.example.demo;

public class Message {
    private String senderName;
    private String messageText;
    private String timestamp;

    public Message(String senderName, String messageText, String timestamp) {
        this.senderName = senderName;
        this.messageText = messageText;
        this.timestamp = timestamp;
    }

    public String getSenderName() {
        return senderName;
    }

    public String getMessageText() {
        return messageText;
    }

    public String getTimestamp() {
        return timestamp;
    }
}
