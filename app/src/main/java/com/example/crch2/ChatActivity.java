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
import android.widget.ImageView;

import com.google.android.material.color.DynamicColors;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    ArrayList<Message> messages;
    ConnectionManager connectionManager;

    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DynamicColors.applyToActivityIfAvailable(this);
        setContentView(R.layout.activity_chat);

imageView = findViewById(R.id.imageView);
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
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiverReconnect,
                new IntentFilter("reconnect"));
        connectionManager = new ConnectionManager(imageView);
        connectionManager.startThread(recyclerView,messages);

        TextInputEditText inputEditText = findViewById(R.id.messageEdit);
        FloatingActionButton floatingActionButton = findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectionManager.getThread().send(name,inputEditText.getText().toString());
                inputEditText.setText("");
                //recyclerView.smoothScrollToPosition(messages.size());
            }
        });
    }
    private final BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String message = intent.getStringExtra("message");
           // Log.d("receiver", "Got message: " + message);
            recyclerView.getAdapter().notifyDataSetChanged();
            recyclerView.smoothScrollToPosition(messages.size());

        }
    };
    private final BroadcastReceiver mMessageReceiverReconnect = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String message = intent.getStringExtra("message");
            // Log.d("receiver", "Got message: " + message);
          //  thread = new SocketThread(recyclerView,messages);
           // thread.start();
            connectionManager.reconnect();
        }
    };

    @Override
    protected void onDestroy() {
        // Unregister since the activity is about to be closed.
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiverReconnect);
        connectionManager.disconnect();
        super.onDestroy();
    }
}