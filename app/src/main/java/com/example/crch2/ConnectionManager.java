package com.example.crch2;


import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.progressindicator.CircularProgressIndicator;

import java.util.ArrayList;

public class ConnectionManager {
   private SocketThread thread;
   private RecyclerView recyclerView;
   private ArrayList<Message> messages;

private ImageView imageView;
    public ConnectionManager(ImageView imageView){

this.imageView = imageView;
    }

    public void startThread(RecyclerView recyclerView, ArrayList<Message> messages){
        this.recyclerView = recyclerView;
        this.messages = messages;
        this.thread = new SocketThread(recyclerView,messages, this);
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
        this.thread = new SocketThread(recyclerView,messages, this);
        this.thread.start();
    }

    public void setOnlineStatus(Boolean status){
if(status == true){

    imageView.setImageResource(R.drawable.ok);
}else{

    imageView.setImageResource(R.drawable.error);
}
    }
}
