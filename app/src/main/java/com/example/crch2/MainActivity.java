package com.example.crch2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.QuickContactBadge;

import com.google.android.material.color.DynamicColors;
import com.google.android.material.textfield.TextInputEditText;

public class MainActivity extends AppCompatActivity {

SharedPreferences sharedPreferences;
SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DynamicColors.applyToActivityIfAvailable(this);
        setContentView(R.layout.activity_main);
sharedPreferences = getSharedPreferences("settings",MODE_PRIVATE);
editor = sharedPreferences.edit();
if(!sharedPreferences.getString("name","").equals("")){
    Intent intent = new Intent(MainActivity.this, ChatActivity.class);
    String name = sharedPreferences.getString("name","");
    intent.putExtra("name",name);
    startActivity(intent);
    finish();
}
        Button button = findViewById(R.id.connectButton);
        TextInputEditText usernameText = findViewById(R.id.usernameEdit);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, ChatActivity.class);
                String name = usernameText.getText().toString();
                editor.putString("name",name);
                editor.apply();
                intent.putExtra("name",name);
                startActivity(intent);
                finish();
            }
        });
    }



}