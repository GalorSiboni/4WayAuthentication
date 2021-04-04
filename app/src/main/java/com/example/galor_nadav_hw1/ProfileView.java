package com.example.galor_nadav_hw1;

import androidx.appcompat.app.AppCompatActivity;


import android.os.Bundle;

 import android.widget.TextView;



public class ProfileView extends AppCompatActivity {

    //textViews views
    private TextView Msg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_profile_view );

        initViews();
        Msg.setText(R.string.msg);

        }

    private void initViews() {
        // initialise views
        Msg = findViewById( R.id.Msg );

    }

    }
