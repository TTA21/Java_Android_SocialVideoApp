package com.example.trabalhom3.hub.file_sender;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.trabalhom3.R;
import com.example.trabalhom3.data_structures.Selected_video;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class Video_Viewer_Activity extends AppCompatActivity {

    private FirebaseDatabase database;
    private String chosen_user = "";
    private String chosen_video = "";
    private Context cont = this;

    private Selected_video selected_video;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_viewer_activity);

        database = FirebaseDatabase.getInstance();

        Bundle extras = getIntent().getExtras();

        chosen_user = extras.getString( "username" ).trim();
        chosen_video = extras.getString( "videoname" ).trim();

        selected_video = new Selected_video();

        update_params();

    }

    private long viewcount_oncreate = 0;
    private boolean increased_once = false;

    public void update_params(){

        DatabaseReference myRef = database.getReference("Video_URL").child( chosen_user ).child( chosen_video ).child( "URL" );

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Log.d("Found URL: ", dataSnapshot.getValue().toString() );
                selected_video.setURL( dataSnapshot.getValue().toString() );
                Video_Player( selected_video.getURL() );

            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.d("ERROR", "onCancelled: " + "Failure to load database");
                Toast.makeText( cont , "Falha ao carregar o banco de dados" , Toast.LENGTH_LONG ).show();
            }
        });

        myRef = database.getReference("Video_URL").child( chosen_user ).child( chosen_video ).child( "Dislike_Num" );

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Log.d("Found Dislike: ", String.valueOf(dataSnapshot.getValue()) );
                selected_video.setDislike_Num( Long.valueOf( String.valueOf(dataSnapshot.getValue()) ) );
                update_dislike_count();

            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.d("ERROR", "onCancelled: " + "Failure to load database");
                Toast.makeText( cont , "Falha ao carregar o banco de dados" , Toast.LENGTH_LONG ).show();
            }
        });

        myRef = database.getReference("Video_URL").child( chosen_user ).child( chosen_video ).child( "Like_Num" );

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Log.d("Found Like: ", String.valueOf(dataSnapshot.getValue()) );
                selected_video.setLike_Num( Long.valueOf( String.valueOf(dataSnapshot.getValue()) ) );
                update_like_count();

            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.d("ERROR", "onCancelled: " + "Failure to load database");
                Toast.makeText( cont , "Falha ao carregar o banco de dados" , Toast.LENGTH_LONG ).show();
            }
        });

        myRef = database.getReference("Video_URL").child( chosen_user ).child( chosen_video ).child( "ViewCount" );

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.

                Log.d("Found ViewCount: ", String.valueOf(dataSnapshot.getValue()) );
                selected_video.setViewCount( Long.valueOf( String.valueOf(dataSnapshot.getValue()) ) );
                update_viewCount();

                if( !increased_once ){
                    viewcount_oncreate = Long.valueOf( String.valueOf(dataSnapshot.getValue()) );
                    increase_viewcount();
                }


            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.d("ERROR", "onCancelled: " + "Failure to load database");
                Toast.makeText( cont , "Falha ao carregar o banco de dados" , Toast.LENGTH_LONG ).show();
            }
        });

        selected_video.setVideoName( chosen_video );
        selected_video.setThis_username( chosen_user );

    }

    public void update_viewCount(){
        TextView viewcount = findViewById( R.id.views_view );
        viewcount.setText( String.valueOf(selected_video.getViewCount()) + " Visualizações" );
    }

    public void update_dislike_count(){
        TextView like_view = findViewById( R.id.like_num );
        like_view.setText( String.valueOf( selected_video.getLike_Num() ) + " Dislikes" );
    }

    public void update_like_count(){
        TextView dislike_view = findViewById( R.id.dislike_num );
        dislike_view.setText( String.valueOf( selected_video.getDislike_Num() ) + " Likes" );
    }

    public void Video_Player( String URI ){

        VideoView videoView = findViewById( R.id.videoView );
        String vidAddress = URI;
        Uri vidUri = Uri.parse(vidAddress);
        videoView.setVideoURI(vidUri);

        MediaController vidControl = new MediaController(this);
        vidControl.setAnchorView(videoView);
        videoView.setMediaController(vidControl);

    }


    private void increase_viewcount(){

        if( !increased_once ){
            DatabaseReference myRef = database.getReference("Video_URL").child( chosen_user ).child( chosen_video ).child( "ViewCount" );
            myRef.setValue( viewcount_oncreate + 1 );
            increased_once = true;
        }

    }


    public void like_pressed(View view) {
    }

    public void dislike_pressed(View view) {
    }

    public void exit_func(View view) {
        finish();
    }
}