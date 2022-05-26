package com.example.crch2;



public class Message {
    private final String userName;
    private final String message;

    public Message(String name, String message){
        this.userName = name;
        this.message = message;
    }

    public String getUserName() {
        return userName;
    }

    public String getMessage() {
        return message;
    }
}
