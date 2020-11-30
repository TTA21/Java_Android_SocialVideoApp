package com.example.trabalhom3.hub.this_channel;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.trabalhom3.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

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

        update_params();

    }

    private ArrayList<String> videonames = new ArrayList<String>();


    private Long sum_views = 0L;
    private Long sum_likes = 0L;
    private Long sum_dislikes = 0L;

    private void update_params(){

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        Query query = rootRef.child("Video_URL").child( this_username );
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot != null) {

                    for (DataSnapshot ds : dataSnapshot.getChildren()) { ///Get all videonames

                        if (ds != null) {

                            videonames.add(ds.getKey());
                            Log.d("New videoname", ds.getKey());

                            try{

                                sum_views += Long.valueOf(ds.child("ViewCount").getValue().toString());
                                sum_likes += Long.valueOf(ds.child("Like_Num").getValue().toString());
                                sum_dislikes += Long.valueOf(ds.child("Dislike_Num").getValue().toString());

                            }catch ( NullPointerException npe ){

                            }

                        }

                    }

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

        TextView totalviews_obj = findViewById( R.id.total_views );
        TextView totallikes_obj = findViewById( R.id.total_likes );
        TextView total_dislikes_obj = findViewById( R.id.total_dislikes );

        totalviews_obj.setText( String.valueOf( sum_views ) + " Views no total" );
        totallikes_obj.setText( String.valueOf( sum_likes ) + " Likes no total" );
        total_dislikes_obj.setText( String.valueOf( sum_dislikes ) + " Dislikes no total" );

        if( !videonames.isEmpty() ){

            ListView list_videos = findViewById( R.id.this_user_videolist );
            ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, videonames);
            list_videos.setAdapter(arrayAdapter);
            list_videos.setOnItemClickListener( listClick );

        }

        sum_views = 0L;
        sum_likes = 0L;
        sum_dislikes = 0L;

        videonames = new ArrayList<String>();

    }

    private Context cont = this;

    private AdapterView.OnItemClickListener listClick = new AdapterView.OnItemClickListener(){

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            String itemAtPosition = String.valueOf( parent.getItemAtPosition(position) );

            Intent video_editor = new Intent( cont , Video_Editor_Activity.class );
            video_editor.putExtra( "this_usrnm" , this_username );
            video_editor.putExtra( "chosen_video" , itemAtPosition );
            video_editor.putStringArrayListExtra( "categories" , category_names );
            startActivity( video_editor );

        }
    };

    public void exit_func(View view) {

        finish();

    }
}