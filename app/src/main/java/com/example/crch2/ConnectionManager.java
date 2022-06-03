package com.example.crch2;


import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ConnectionManager {
   private SocketThread thread;
   private RecyclerView recyclerView;
   private ArrayList<Message> messages;

    public ConnectionManager(){

    }

    public void startThread(RecyclerView recyclerView, ArrayList<Message> messages){
        this.recyclerView = recyclerView;
        this.messages = messages;
        this.thread = new SocketThread(recyclerView,messages);
        this.thread.start();
    }

    public SocketThread getThread() {
        return this.thread;
    }

    public void disconnect(){
        this.thread.close();
    }

    public void reconnect(){
        disconnect();
        this.thread = new SocketThread(recyclerView,messages);
        this.thread.start();
    }
}
