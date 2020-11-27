package com.example.trabalhom3.hub;

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
import com.example.trabalhom3.hub.category.Category_Activity;
import com.example.trabalhom3.hub.file_sender.File_Sender_Activity;
import com.example.trabalhom3.hub.this_channel.This_Channel_Activity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class Hub_Activity extends AppCompatActivity {

    private FirebaseDatabase database;
    private String this_username = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hub_activity);

        database = FirebaseDatabase.getInstance();

        Bundle extra = getIntent().getExtras();
        this_username = extra.getString( "this_usrnm" );

        get_category_names();   ///And its values

    }

    private ArrayList<String> category_names=new ArrayList<String>();
    private ArrayList<String> category_values=new ArrayList<String>();
    private ArrayList<String> category_names_for_fileSender =new ArrayList<String>();
    private Context cont = this;

    private void change_category_list_values(){

        if( !category_names.isEmpty() && !category_values.isEmpty() ){

            ListView category_list = findViewById(R.id.category_list);
            ArrayList<String> category_display = new ArrayList<String>();

            for (int I = 0; I < category_names.size(); I++) {

                category_display.add(category_names.get(I) + " : " + category_values.get(I));

            }

            ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, category_display);
            category_list.setAdapter(arrayAdapter);
            category_list.setOnItemClickListener( listClick );

        }
        category_names_for_fileSender = category_names;
        category_names=new ArrayList<String>();
        category_values=new ArrayList<String>();
    }

    public void get_category_names(){

        DatabaseReference myRef = database.getReference("Categories");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                for(DataSnapshot snap:dataSnapshot.getChildren()){
                    category_names.add(snap.getKey());
                }

                get_category_values();

            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.d("ERROR", "onCancelled: " + "Failure to load database");
                Toast.makeText( cont , "Falha ao carregar o banco de dados" , Toast.LENGTH_LONG ).show();
            }
        });

    }

    private int this_index = 0;
    private int final_index = 0;

    public void get_category_values(){

        DatabaseReference myRef;
        Log.d("CategoryNames size", "" + category_names.size() );

        final_index = category_names.size();
        for( int I = 0 ; I < category_names.size() ; I++ ){

                myRef = database.getReference("Categories").child( category_names.get(I) ).child( "Num_of_Videos" );

                myRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // This method is called once with the initial value and again
                        // whenever data at this location is updated.
                        String value = Long.toString(dataSnapshot.getValue(Long.class));
                        this_index++;
                        category_values.add( value );

                        if( this_index == final_index ){
                            change_category_list_values();
                            this_index = 0;
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

    }

    private AdapterView.OnItemClickListener listClick = new AdapterView.OnItemClickListener(){

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            String itemAtPosition = String.valueOf( parent.getItemAtPosition(position) );
            int end_pos = itemAtPosition.indexOf( ":" );
            String subs = itemAtPosition.substring( 0 , end_pos );
            Log.d("Subtring", subs);

            Intent category = new Intent( cont , Category_Activity.class);
            category.putExtra( "chosen_category" , subs );
            category.putExtra( "this_usrnm" , this_username );

            startActivity( category );

        }
    };


    public void send_video_func(View view) {

        if( !category_names_for_fileSender.isEmpty() ){

            Intent send_video = new Intent( this , File_Sender_Activity.class );
            send_video.putStringArrayListExtra( "cat_names" , category_names_for_fileSender );
            send_video.putExtra( "this_usernm" , this_username );
            startActivity( send_video );

        }
    }

    public void see_channel_func(View view) {

        if( !category_names_for_fileSender.isEmpty() ){

            Intent view_channel = new Intent( this , This_Channel_Activity.class );
            view_channel.putStringArrayListExtra( "cat_names" , category_names_for_fileSender );
            view_channel.putExtra( "this_usernm" , this_username );
            startActivity( view_channel );

        }

    }
}