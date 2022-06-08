package com.example.crch2;



public class Message {
    private final String userName;
    private final String message;
    private int type;

    public Message(String name, String message){
        this.userName = name;
        this.message = message;
        type = 0;
    }
    public Message(String name, String message, int type){
        this.userName = name;
        this.message = message;
        this.type = type;
    }

    public String getUserName() {
        return userName;
    }

    public String getMessage() {
        return message;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
