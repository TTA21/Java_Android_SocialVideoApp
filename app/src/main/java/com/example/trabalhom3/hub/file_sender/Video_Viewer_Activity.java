package com.example.trabalhom3.hub.file_sender;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class Video_Viewer_Activity extends AppCompatActivity {

    private FirebaseDatabase database;
    private String chosen_user = "";
    private String this_user = "";
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
        this_user = extras.getString( "this_username" );

        selected_video = new Selected_video();

        update_params();
        update_comments();

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

        myRef = database.getReference("Video_URL").child( chosen_user ).child( chosen_video );

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.

                selected_video.setDislike_Num(Long.valueOf(dataSnapshot.child( "Dislike_Num" ).getValue().toString()));
                selected_video.setLike_Num( Long.valueOf( dataSnapshot.child( "Like_Num" ).getValue().toString()));
                selected_video.setViewCount( Long.valueOf( dataSnapshot.child( "ViewCount" ).getValue().toString()));
                selected_video.setURL( dataSnapshot.child( "URL" ).getValue().toString() );

                update_dislike_count();
                update_like_count();
                update_viewCount();

                if( !increased_once ){
                    viewcount_oncreate = Long.valueOf( dataSnapshot.child( "ViewCount" ).getValue().toString());
                    increase_viewcount();
                }

                Video_Player( selected_video.getURL() );

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
        like_view.setText( String.valueOf( selected_video.getDislike_Num() ) + " Dislikes" );
    }

    public void update_like_count(){
        TextView dislike_view = findViewById( R.id.dislike_num );
        dislike_view.setText( String.valueOf( selected_video.getLike_Num() ) + " Likes" );
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

    private String decision = "";
    private boolean currently_working = false;

    public void like_pressed(View view) {

        if( !currently_working ) {
            Log.d("ALERT", "Like button pressed");
            decision = "Liked";
            currently_working = true;
            single_check = false;
            update_like_dislike();
        }

    }


    public void dislike_pressed(View view) {

        if( !currently_working ) {
            Log.d("ALERT", "Dislike button pressed");
            decision = "Disliked";
            currently_working = true;
            single_check = false;
            update_like_dislike();
        }

    }

    private Selected_video selected_video_to_decide;
    private String this_user_prev_choice = "";
    private boolean single_check = false;

    private void update_like_dislike(){

        selected_video_to_decide = new Selected_video();

        ///Get all relevant values,
        ///Dislike_Num , Like_Num , Name : Like/Dislike

        DatabaseReference myRef = database.getReference("Video_URL").child( chosen_user ).child( chosen_video );

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                if( !single_check ){

                    single_check = true;
                    selected_video_to_decide.setDislike_Num( Long.valueOf(dataSnapshot.child( "Dislike_Num" ).getValue().toString()) );
                    selected_video_to_decide.setLike_Num( Long.valueOf(dataSnapshot.child( "Like_Num" ).getValue().toString()) );

                    Log.d( "Current Num of Likes: " , String.valueOf(selected_video_to_decide.getLike_Num()) );
                    Log.d( "Current Num of Dislik: " , String.valueOf(selected_video_to_decide.getDislike_Num()) );

                    if( dataSnapshot.hasChild( this_user ) ){
                        this_user_prev_choice = dataSnapshot.child( this_user ).getValue().toString();
                        Log.d( "Users prev choice: " , this_user_prev_choice);
                    }else{
                        this_user_prev_choice = "NONE";
                        Log.d( "ALERT" , "User has no previous choice");
                    }

                    make_changes();

                }

            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.d("ERROR", "onCancelled: " + "Failure to load database");
                Toast.makeText( cont , "Falha ao carregar o banco de dados" , Toast.LENGTH_LONG ).show();
            }
        });

    }

    private void make_changes(){

        if( decision.contains( "Liked" ) && this_user_prev_choice.contains( "Liked" ) ){

            Log.d( "ALERT" , "No change 1");
            Toast.makeText( this , "Você já gostou desse video" , Toast.LENGTH_LONG ).show();

        }else if( decision.contains( "Disliked" ) && this_user_prev_choice.contains( "Disliked" ) ){

            Log.d( "ALERT" , "No change 2");
            Toast.makeText( this , "Você já não gostou desse video" , Toast.LENGTH_LONG ).show();

        }else if( decision.contains( "Liked" ) && this_user_prev_choice.contains( "Disliked" ) ){

            Log.d( "ALERT" , "Changing 1");
            selected_video_to_decide.setDislike_Num( selected_video_to_decide.getDislike_Num() - 1 );
            selected_video_to_decide.setLike_Num( selected_video_to_decide.getLike_Num() + 1 );
            validate_changes();

        }else if( decision.contains( "Disliked" ) && this_user_prev_choice.contains( "Liked" ) ){

            Log.d( "ALERT" , "Changing 2");
            selected_video_to_decide.setDislike_Num( selected_video_to_decide.getDislike_Num() + 1 );
            selected_video_to_decide.setLike_Num( selected_video_to_decide.getLike_Num() - 1 );
            validate_changes();

        }else if( decision.contains( "Disliked" ) && this_user_prev_choice.contains( "NONE" ) ){

            selected_video_to_decide.setDislike_Num( selected_video_to_decide.getDislike_Num() + 1 );
            validate_changes();

        }else if( decision.contains( "Liked" ) && this_user_prev_choice.contains( "NONE" ) ){

            selected_video_to_decide.setLike_Num( selected_video_to_decide.getLike_Num() + 1 );
            validate_changes();

        }else{
            Log.d( "ALERT" , "No Change 3");
        }

    }

    private void validate_changes(){

        {
            Log.d( "ALERT" , "Changing Dislikes");
            DatabaseReference myRef = database.getReference("Video_URL")
                    .child( chosen_user )
                    .child( chosen_video )
                    .child( "Dislike_Num" );
            myRef.setValue( selected_video_to_decide.getDislike_Num() );
        }

        {
            Log.d( "ALERT" , "Changing Likes");
            DatabaseReference myRef = database.getReference("Video_URL")
                    .child( chosen_user )
                    .child( chosen_video )
                    .child( "Like_Num" );
            myRef.setValue( selected_video_to_decide.getLike_Num() );
        }

        {
            Log.d( "ALERT" , "Changing Decision");
            DatabaseReference myRef = database.getReference("Video_URL")
                    .child( chosen_user )
                    .child( chosen_video )
                    .child( this_user );
            myRef.setValue( decision );
        }

        Log.d( "ALERT" , "Done");

        Toast.makeText( this , "Atualizado" , Toast.LENGTH_LONG ).show();
        reset_params();


    }

    private void reset_params(){

        currently_working = false;
        selected_video_to_decide = new Selected_video();
        this_user_prev_choice = "";

    }

    public void exit_func(View view) {
        finish();
    }

    public void comment_func(View view) {

        EditText comment_field_obj = findViewById( R.id.comment_field );

        if( !comment_field_obj.getText().toString().isEmpty() ){

            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
            String currentTime = sdf.format(new Date());

            sdf = new SimpleDateFormat("dd:MM:yyyy", Locale.getDefault());
            String currentDate = sdf.format(new Date());

            DatabaseReference dataRef = database.getReference("Comments")
                    .child( "Videos" )
                    .child( chosen_video )
                    .child( this_user + " " + currentTime + " " + currentDate );
            dataRef.setValue( comment_field_obj.getText().toString() , new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if (databaseError != null) {
                        Toast.makeText( cont , "Comentario não pode ser enviado" , Toast.LENGTH_LONG ).show();
                    } else {
                        Toast.makeText( cont , "Comentario enviado" , Toast.LENGTH_LONG ).show();
                    }
                }
            });


        }else{
            Toast.makeText( this , "Nada foi digitado" , Toast.LENGTH_LONG ).show();
        }

    }

    private ArrayList< String > comments = new ArrayList<>();

    private void update_comments(){

        DatabaseReference rootRef;
        rootRef = database.getReference("Comments").child( "Videos" ).child( chosen_video );

        rootRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                    comments = new ArrayList<String>();

                    for(DataSnapshot ds : dataSnapshot.getChildren()) {

                        comments.add( ds.getKey() + ":\n" + ds.getValue().toString() );

                    }
                    Log.d("ALERT", "Updating comments");
                    change_comments_list();

            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.d("ERROR", "onCancelled: " + "Failure to load database");
                Toast.makeText( cont , "Falha ao carregar o banco de dados" , Toast.LENGTH_LONG ).show();
            }
        });

    }

    private void change_comments_list(){

        if( !comments.isEmpty() ){

            ListView list_videos = findViewById( R.id.comments_list );
            ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, comments);
            list_videos.setAdapter(arrayAdapter);

        }

    }

}