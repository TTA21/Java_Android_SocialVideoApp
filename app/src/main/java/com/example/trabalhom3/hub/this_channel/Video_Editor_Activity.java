package com.example.trabalhom3.hub.this_channel;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.MediaController;
import android.widget.Spinner;
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

public class Video_Editor_Activity extends AppCompatActivity {

    private FirebaseDatabase database;
    private String this_videoname = "";
    private String this_username = "";
    private ArrayList<String> category_names;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_editor_activity);

        Bundle extras = getIntent().getExtras();

        this_username = extras.getString( "this_usrnm" );
        this_videoname = extras.getString( "chosen_video" );
        category_names = extras.getStringArrayList( "categories" );

        database = FirebaseDatabase.getInstance();

        update_params();

    }

    private Selected_video selected_video;

    Query query;
    ValueEventListener valueEventListener;

    private void update_params(){

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        query = rootRef.child("Video_URL").child( this_username ).child( this_videoname );
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if( dataSnapshot != null ) {

                    selected_video = new Selected_video();

                    selected_video.setThis_username(this_username);
                    selected_video.setVideoName(this_videoname);
                    selected_video.setURL(dataSnapshot.child("URL").getValue().toString());
                    selected_video.setDate(dataSnapshot.child("Date").getValue().toString());
                    selected_video.setDislike_Num(Long.valueOf(String.valueOf(dataSnapshot.child("Dislike_Num").getValue())));
                    selected_video.setLike_Num(Long.valueOf(String.valueOf(dataSnapshot.child("Like_Num").getValue())));
                    selected_video.setTime(dataSnapshot.child("Time").getValue().toString());
                    selected_video.setCategory(dataSnapshot.child("Category").getValue().toString());
                    selected_video.setVideoLenght(Long.valueOf(String.valueOf(dataSnapshot.child("VideoLenght").getValue())));
                    selected_video.setViewCount(Long.valueOf(String.valueOf(dataSnapshot.child("ViewCount").getValue())));

                    update_overlay();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("ERROR", databaseError.getMessage());
            }
        };
        query.addValueEventListener( valueEventListener );

    }

    private void update_overlay(){

        EditText videoname_field_obj = findViewById( R.id.videoname_field );
        videoname_field_obj.setText( selected_video.getVideoName() );

        if( !category_names.isEmpty() ){

            Spinner list_cat = findViewById( R.id.category_spinner_edit);
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, category_names);
            list_cat.setAdapter(dataAdapter);

            category_names = new ArrayList<String>();

        }

        if( !selected_video.getURL().isEmpty() ){
            Video_Player( selected_video.getURL() );
        }

    }

    void Video_Player( String URI ){

        VideoView videoView = findViewById( R.id.videoView2 );
        String vidAddress = URI;
        Uri vidUri = Uri.parse(vidAddress);
        videoView.setVideoURI(vidUri);

        MediaController vidControl = new MediaController(this);
        vidControl.setAnchorView(videoView);
        videoView.setMediaController(vidControl);

    }

    Selected_video newvideo = new Selected_video();

    public void update_database_func(View view) {

        EditText videoname_field_obj = findViewById( R.id.videoname_field );
        Spinner category_spinner_edit_obj = findViewById( R.id.category_spinner_edit );

        newvideo = selected_video;

        String videoname_field = videoname_field_obj.getText().toString();
        String chosen_category = category_spinner_edit_obj.getSelectedItem().toString();

        Log.d("Database videoname", selected_video.getVideoName());
        Log.d("This videoname", videoname_field);
        Log.d("Database Category", selected_video.getCategory());
        Log.d("This category", chosen_category);

        if( !selected_video.getCategory().equals( chosen_category ) || !selected_video.getVideoName().equals( videoname_field ) ){

            newvideo.setCategory( chosen_category );
            newvideo.setVideoName( videoname_field );
            newvideo.setViewCount( 0L );
            newvideo.setDislike_Num( 0L );
            newvideo.setLike_Num( 0L );
            change_database();

        }else{
            Log.d("ALERT", "NO CHANGE");
            Toast.makeText( this , "Não há nenhuma mudança para atualizar" , Toast.LENGTH_LONG ).show();
        }

    }

    private void change_database(){

        query.removeEventListener( valueEventListener );    ///Remove old event listener so it doesnt crash

        DatabaseReference myRef = database.getReference("Video_URL").child( this_username );

        {   //old videoname
            myRef.child( this_videoname ).removeValue();    ///delete previous video metadata, along with everything user added
        }

        {   //Comments
            DatabaseReference commRef = database.getReference("Comments").child( "Videos" ).child( this_videoname );
            commRef.removeValue();
        }

        {
                ////Make new node and fill
            myRef.child( newvideo.getVideoName() ).child( "Category" ).setValue( newvideo.getCategory() );
            myRef.child( newvideo.getVideoName() ).child( "Date" ).setValue( newvideo.getDate() );
            myRef.child( newvideo.getVideoName() ).child( "Dislike_Num" ).setValue( newvideo.getDislike_Num() );
            myRef.child( newvideo.getVideoName() ).child( "Like_Num" ).setValue( newvideo.getLike_Num() );
            myRef.child( newvideo.getVideoName() ).child( "Time" ).setValue( newvideo.getTime() );
            myRef.child( newvideo.getVideoName() ).child( "URL" ).setValue( newvideo.getURL() );
            myRef.child( newvideo.getVideoName() ).child( "VideoLenght" ).setValue( newvideo.getVideoLenght() );
            myRef.child( newvideo.getVideoName() ).child( "ViewCount" ).setValue( newvideo.getViewCount() );

        }

        finish();

    }

}
