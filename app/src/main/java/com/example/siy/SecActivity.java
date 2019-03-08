package com.example.siy;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

public class SecActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sec);

        final TextView tv = findViewById(R.id.show_tx);
        tv.setText("我是一段很长的文字我是一段很长的文字我是一段很长的文字我是一段很长的文字我是一段很长的文字我是一段很长的文字我是一段很长的文字我是一段很长的文字我是一段很长的文字我是一段很长的文字我是一段很长的文字我是一段很长的文字");
    }

}
