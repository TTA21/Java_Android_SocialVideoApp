package com.example.trabalhom3.hub.this_channel;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.trabalhom3.R;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class This_Channel_Activity extends AppCompatActivity {

    private ArrayList<String> category_names = new ArrayList<String>();
    private String this_username = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.this_channel_activity);

        Bundle extras = getIntent().getExtras();

        category_names = extras.getStringArrayList( "cat_names" );
        this_username = extras.getString( "this_usernm" );

    }
}