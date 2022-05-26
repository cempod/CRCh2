package com.example.crch2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ChatRecyclerAdapter extends RecyclerView.Adapter {
    ArrayList<Message> messages;

    public ChatRecyclerAdapter(ArrayList<Message> messages){
        this.messages = messages;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView messageText;
        TextView userNameText;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.messageText);
            userNameText = itemView.findViewById(R.id.userNameText);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_card,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((ViewHolder)holder).messageText.setText(messages.get(position).getMessage());
        ((ViewHolder)holder).userNameText.setText(messages.get(position).getUserName());
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }
}
