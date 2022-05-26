package com.example.crch2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    ArrayList<Message> messages;
    SocketThread thread;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        String name = getIntent().getStringExtra("name");
         messages = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());

        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        ChatRecyclerAdapter adapter = new ChatRecyclerAdapter(messages);
        recyclerView.setAdapter(adapter);
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("update"));
        thread = new SocketThread(recyclerView,messages);
        thread.start();

        TextInputEditText inputEditText = findViewById(R.id.messageEdit);
        FloatingActionButton floatingActionButton = findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                thread.send(name,inputEditText.getText().toString());
                inputEditText.setText("");
                recyclerView.smoothScrollToPosition(messages.size());
            }
        });
    }
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String message = intent.getStringExtra("message");
           // Log.d("receiver", "Got message: " + message);
            recyclerView.getAdapter().notifyDataSetChanged();
            recyclerView.smoothScrollToPosition(messages.size());

        }
    };

    @Override
    protected void onDestroy() {
        // Unregister since the activity is about to be closed.
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        thread.close();
        super.onDestroy();
    }
}