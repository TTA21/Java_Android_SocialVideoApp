package com.example.trabalhom3.hub.category;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.trabalhom3.R;
import com.example.trabalhom3.hub.file_sender.Video_Viewer_Activity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class Category_Activity extends AppCompatActivity {

    private String chosen_category = "";
    private String this_username = "";

    private FirebaseDatabase database;
    private Context cont = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.category_activity);

        database = FirebaseDatabase.getInstance();

        Bundle extras = getIntent().getExtras();
        chosen_category = extras.getString( "chosen_category" );
        this_username = extras.getString( "this_usrnm" );

        Log.d("Chosen Category", chosen_category);

        update_list_videos();
        startTimer();

    }

    private ArrayList<String> all_usernames = new ArrayList<String>();
    private ArrayList< ArrayList<String> > all_videonames = new ArrayList< ArrayList<String> >();
    private ArrayList<String> list_strings = new ArrayList<String>();

    private void update_list_videos(){

        get_all_usernames();
        make_list_Strings();

        if( !list_strings.isEmpty() ){

            ListView list_videos = findViewById( R.id.list_videos );
            ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, list_strings);
            list_videos.setAdapter(arrayAdapter);
            list_videos.setOnItemClickListener( listClick );
            list_strings = new ArrayList<String>();

        }

        all_usernames = new ArrayList<String>();
        all_videonames = new ArrayList< ArrayList<String> >();

    }

    private void get_all_usernames(){

        DatabaseReference myRef = database.getReference("Video_URL");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                for(DataSnapshot snap:dataSnapshot.getChildren()){
                    all_usernames.add(snap.getKey());
                }

                get_all_video_names();

            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.d("ERROR", "onCancelled: " + "Failure to load database");
                Toast.makeText( cont , "Falha ao carregar o banco de dados" , Toast.LENGTH_LONG ).show();
            }
        });

    }

    private String curr_usr = "";

    private void get_all_video_names(){

        ///Cicle through all usernames and get their child

        DatabaseReference myRef;

        for( int I = 0 ; I < all_usernames.size() ; I++ ){

            curr_usr = all_usernames.get(I);
            myRef = database.getReference("Video_URL").child( all_usernames.get(I) );

            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.

                    ArrayList<String> temp_user = new ArrayList<String>();
                    //temp_user.add( curr_usr );

                    for(DataSnapshot snap:dataSnapshot.getChildren()){
                        temp_user.add(snap.getKey());
                    }

                    all_videonames.add( temp_user );    ////User1   :   video1 , video 2 , video 3
                                                        ////User2   :   video1 , video 2

                }
                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Log.d("ERROR", "onCancelled: " + "Failure to load database");
                    Toast.makeText( cont , "Falha ao carregar o banco de dados" , Toast.LENGTH_LONG ).show();
                }
            });

        }

    }

    private void make_list_Strings(){

        if( !all_videonames.isEmpty() && !all_usernames.isEmpty() ){

            list_strings = new ArrayList<String>();
            for( int I = 0 ; I < all_videonames.size() ; I++ ){

                //String username = all_videonames.get( I ).get( 0 );
                String username = all_usernames.get(I);

                for( int J = 0 ; J < all_videonames.get( I ).size() ; J++ ){

                    String videoname = all_videonames.get(I).get( J );

                    list_strings.add( username + " : " + videoname );

                }

            }

        }

    }

    private Timer timer;
    private TimerTask timerTask;
    private Handler handler = new Handler();

    //To stop timer
    private void stopTimer(){
        if(timer != null){
            timer.cancel();
            timer.purge();
        }
    }

    //To start timer
    private void startTimer(){
        timer = new Timer();
        timerTask = new TimerTask() {
            public void run() {
                handler.post(new Runnable() {
                    public void run(){
                        update_list_videos();
                    }
                });
            }
        };
        timer.schedule(timerTask, 3000, 3000);
    }


    private AdapterView.OnItemClickListener listClick = new AdapterView.OnItemClickListener(){

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            String itemAtPosition = String.valueOf( parent.getItemAtPosition(position) );
            int end_pos = itemAtPosition.indexOf( ":" );
            String videoname = itemAtPosition.substring( end_pos + 2 , String.valueOf( parent.getItemAtPosition(position) ).length() );
            String username =  itemAtPosition.substring( 0 , end_pos -1 );
            Log.d("username", username);
            Log.d("videoname", videoname);

            Intent video_viewer = new Intent( cont , Video_Viewer_Activity.class);
            video_viewer.putExtra( "username" , username );
            video_viewer.putExtra( "videoname" , videoname );

            startActivity( video_viewer );

        }
    };


    public void exit_btn(View view) {

        stopTimer();
        finish();

    }
}


