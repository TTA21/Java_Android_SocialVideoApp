package com.example.trabalhom3.hub.category;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
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

public class Search_User_Activity extends AppCompatActivity {

    private String this_user = "";
    private FirebaseDatabase database;
    private Context cont = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_user_activity);

        Bundle extras = getIntent().getExtras();
        database = FirebaseDatabase.getInstance();

        this_user = extras.getString( "this_usernm" );

        update_params_spinner();

    }

    private ArrayList<String> usernames = null;

    private void update_params_spinner(){

        DatabaseReference myRef = database.getReference("Users");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                usernames = new ArrayList<String>();

                for(DataSnapshot snap:dataSnapshot.getChildren()){
                    usernames.add( snap.getKey() );
                }

                update_overlay_spinner();

            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.d("ERROR", "onCancelled: " + "Failure to load database");
                Toast.makeText( cont , "Falha ao carregar o banco de dados" , Toast.LENGTH_LONG ).show();
            }
        });

    }


    private void update_overlay_spinner(){

        if( !usernames.isEmpty() ){

            Spinner list_cat = findViewById( R.id.usernames_spinner);
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, usernames);
            list_cat.setAdapter(dataAdapter);

        }

    }

    private String user_to_find_field;

    public void search_database_forUser_func(View view) {

        EditText user_to_find_field_obj = findViewById( R.id.user_to_find_field );
        user_to_find_field = user_to_find_field_obj.getText().toString();

        search_user();

    }

    public void search_by_spinner_func(View view) {

        Spinner usernames_spinner_obj = findViewById( R.id.usernames_spinner );
        user_to_find_field = usernames_spinner_obj.getSelectedItem().toString();

        search_user();

    }

    private void search_user(){

        Log.d("ALERT", "Searching for " + user_to_find_field);

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        Query query = rootRef.child("Users");
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.child( user_to_find_field ).exists()){
                    Log.d("PRIORITY ALERT", "USER " + user_to_find_field + " FOUND");
                    search_user_videos();
                }else{
                    Log.d("PRIORITY ALERT", "USER " + user_to_find_field + " NOT FOUND");
                    Toast.makeText( cont , "Usuario não existe" , Toast.LENGTH_LONG ).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("ERROR", databaseError.getMessage());
            }
        };
        query.addListenerForSingleValueEvent(valueEventListener);

    }

    private ArrayList<String> videonames = null;

    private void search_user_videos(){

        DatabaseReference rootRef;
        rootRef = database.getReference("Video_URL").child( user_to_find_field );

        Log.d("ALERT", "Searching for " + user_to_find_field + " videos");

        rootRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                videonames = new ArrayList<String>();

                for(DataSnapshot ds : dataSnapshot.getChildren()) {

                    Log.d("ADD", ds.getKey());
                    videonames.add( ds.getKey()  );

                }
                update_videonames_list();

            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.d("ERROR", "onCancelled: " + "Failure to load database");
                Toast.makeText( cont , "Falha ao carregar o banco de dados" , Toast.LENGTH_LONG ).show();
            }
        });

    }

    private void update_videonames_list(){

        if( !videonames.isEmpty() ){

            Log.d("ALERT", "updating list");

            ListView list_videos = findViewById( R.id.user_videos_list );
            ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, videonames);
            list_videos.setAdapter(arrayAdapter);
            list_videos.setOnItemClickListener( listClick );

        }

    }

    private AdapterView.OnItemClickListener listClick = new AdapterView.OnItemClickListener(){

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            String itemAtPosition = String.valueOf( parent.getItemAtPosition(position) );

            Intent video_viewer = new Intent( cont , Video_Viewer_Activity.class);
            video_viewer.putExtra( "username" , user_to_find_field );
            video_viewer.putExtra( "videoname" , itemAtPosition );
            video_viewer.putExtra( "this_username" , this_user );

            startActivity( video_viewer );

        }
    };

    public void search_by_videoname_func(View view) {

        Intent search_videoname = new Intent( cont , Search_VideoName_Activity.class);
        search_videoname.putExtra( "this_username" , this_user );

        startActivity( search_videoname );

    }
}