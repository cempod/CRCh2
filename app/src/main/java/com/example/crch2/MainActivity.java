package com.example.crch2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.QuickContactBadge;

import com.google.android.material.textfield.TextInputEditText;

public class MainActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button button = findViewById(R.id.connectButton);
        TextInputEditText usernameText = findViewById(R.id.usernameEdit);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ChatActivity.class);
                String name = usernameText.getText().toString();
                intent.putExtra("name",name);
                startActivity(intent);
            }
        });
    }



}