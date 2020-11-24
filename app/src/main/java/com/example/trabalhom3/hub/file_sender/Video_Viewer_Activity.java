package com.example.trabalhom3.hub.file_sender;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.trabalhom3.R;
import com.google.firebase.database.FirebaseDatabase;

public class Video_Viewer_Activity extends AppCompatActivity {

    private FirebaseDatabase database;
    private String chosen_user = "";
    private String chosen_video = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_viewer_activity);

        database = FirebaseDatabase.getInstance();

        Bundle extras = getIntent().getExtras();

        chosen_user = extras.getString( "username" );
        chosen_video = extras.getString( "videoname" );

    }
}