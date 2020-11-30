package com.example.trabalhom3.hub.category;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.trabalhom3.R;
import com.example.trabalhom3.hub.file_sender.Video_Viewer_Activity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Search_VideoName_Activity extends AppCompatActivity {

    private FirebaseDatabase database;
    private Context cont = this;
    private String this_username = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_videoname_activity);

        database = FirebaseDatabase.getInstance();

        Bundle extras = getIntent().getExtras();

        this_username = extras.getString( "this_username" );


    }

    private ArrayList<String> usernames = null;
    private String videoname_searh_field = "";

    public void search_videoname_func(View view) {

        EditText videoname_searh_field_obj = findViewById( R.id.videoname_searh_field );
        videoname_searh_field = videoname_searh_field_obj.getText().toString();

        DatabaseReference myRef = database.getReference("Users");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                usernames = new ArrayList<String>();

                for(DataSnapshot snap:dataSnapshot.getChildren()){
                    usernames.add( snap.getKey() );
                }

                search_users_videonames();

            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.d("ERROR", "onCancelled: " + "Failure to load database");
                Toast.makeText( cont , "Falha ao carregar o banco de dados" , Toast.LENGTH_LONG ).show();
            }
        });


    }

    private String chosen_user = "";

    private void search_users_videonames(){

        for( int I = 0 ; I < usernames.size() ; I++ ){

            final DatabaseReference myRef = database.getReference("Video_URL").child( usernames.get(I) );
            Query query = myRef;
            ValueEventListener valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if( dataSnapshot.hasChild( videoname_searh_field ) ){

                        chosen_user = dataSnapshot.getKey();
                        play_video();

                    }

                }
                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Log.d("ERROR", "onCancelled: " + "Failure to load database");
                    Toast.makeText( cont , "Falha ao carregar o banco de dados" , Toast.LENGTH_LONG ).show();
                }
            };
            query.addListenerForSingleValueEvent(valueEventListener);

        }

    }

    private void play_video(){

        Intent video_viewer = new Intent( cont , Video_Viewer_Activity.class);
        video_viewer.putExtra( "this_username" , this_username );
        video_viewer.putExtra( "videoname" , videoname_searh_field );
        video_viewer.putExtra( "username" , chosen_user );

        startActivity( video_viewer );

    }

}