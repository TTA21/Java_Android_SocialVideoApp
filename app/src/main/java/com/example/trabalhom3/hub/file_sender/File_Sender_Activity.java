package com.example.trabalhom3.hub.file_sender;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.trabalhom3.R;
import com.example.trabalhom3.data_structures.Selected_video;
import com.example.trabalhom3.utils.UriUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.net.URI;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class File_Sender_Activity extends AppCompatActivity {

    private ArrayList<String> category_names=new ArrayList<String>();
    private Selected_video selected_video = new Selected_video();
    private Uri full_uri = null;
    private File selected_file = null;
    private String this_username = "";
    private TextView progrssview = null;
    private FirebaseDatabase database;

    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.file_sender_activity);

        Bundle extras = getIntent().getExtras();

        category_names = extras.getStringArrayList( "cat_names" );
        this_username = extras.getString( "this_usernm" );
        progrssview = findViewById( R.id.progress_field );

        user = FirebaseAuth.getInstance().getCurrentUser();

        database = FirebaseDatabase.getInstance();

        Log.d("Num of categ", "" + category_names.size() );

        update_cat_spinner();

    }

    public void update_cat_spinner(){

        Spinner category_spinner = findViewById( R.id.spinner_category );
        ArrayAdapter<String> spinner_Adapter;

        spinner_Adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, category_names);
        spinner_Adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        category_spinner.setAdapter(spinner_Adapter);

    }

    public void exit_func(View view) {
        finish();
    }

    public void find_file_func(View view) {

        Intent intent = new Intent()
                .setType("*/*")
                .setAction(Intent.ACTION_GET_CONTENT);

        startActivityForResult(Intent.createChooser(intent, "Select a file"), 123);

    }

    Context cont = this;

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

            Uri selectedURI = data.getData(); //The uri with the location of the file

            String fullFilePath = UriUtils.getPathFromUri(this, selectedURI);
            File selectedFile = new File(fullFilePath);

            String filetype = MimeTypeMap.getFileExtensionFromUrl(fullFilePath);

            //if( filetype.contains( "mp4" ) || filetype.contains( "mp3" ) || filetype.contains( "ogg" ) ){

                    detail_file( fullFilePath , selectedURI , selectedFile );

            //}else{
            //    Toast.makeText( cont , "Arquivo escolhido inválido" , Toast.LENGTH_LONG ).show();
            //}

    }

    public void detail_file(String full_file_path , Uri uri , File file ){

        TextView full_filename_field_obj = findViewById( R.id.file_name_field );
        TextView file_size_field_obj = findViewById( R.id.filesize_field );

        full_filename_field_obj.setText( file.getName() );

        double filesize = file.length() / 1024;     ///Kb
        filesize = filesize / 1024;                 ///Mb
        file_size_field_obj.setText( Double.toString( filesize ) + " MBytes" );

        full_uri = uri;
        selected_file = file;

    }

    public void upload_video_func(View view) {

        EditText chosen_file_name_obj = findViewById( R.id.file_name_editText );
        Spinner category_spinner = findViewById(R.id.spinner_category);

        String chosen_file_name = chosen_file_name_obj.getText().toString();

        if( selected_file == null ){
            Toast.makeText( this , "Selecione um video primeiro" , Toast.LENGTH_LONG ).show();
            return;
        }

        if( chosen_file_name.isEmpty() ){
            chosen_file_name = selected_file.getName();
            chosen_file_name_obj.setText( chosen_file_name );
        }

        if(     chosen_file_name.contains( "." ) ||
                chosen_file_name.contains( "#" ) ||
                chosen_file_name.contains( "$" ) ||
                chosen_file_name.contains( "[" ) ||
                chosen_file_name.contains( "]" ) ){
            Toast.makeText( cont , "Nome do video não pode conter '.' '#' '$' '[' ']'" , Toast.LENGTH_LONG ).show();
            return;
        }

        selected_video.setVideoName( chosen_file_name );

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        String currentTime = sdf.format(new Date());
        selected_video.setTime( currentTime );

        sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String currentDate = sdf.format(new Date());
        selected_video.setDate( currentDate );

        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        //use one of overloaded setDataSource() functions to set your data source
        retriever.setDataSource(this, Uri.fromFile(selected_file));
        String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        long lenghtInMillisec = Long.parseLong(time );
        retriever.release();

        long lenghtInMinutes = lenghtInMillisec / 60000;
        selected_video.setVideoLenght( lenghtInMinutes );

        selected_video.setCategory( category_spinner.getSelectedItem().toString() );

        selected_video.setThis_username( this_username );

        selected_video.setUri( full_uri );

        Log.d("Videoname", selected_video.getVideoName());
        Log.d("VideoLenght", "" + selected_video.getVideoLenght());
        Log.d("ViewCount", "" + selected_video.getViewCount());
        Log.d("Date", selected_video.getDate());
        Log.d("Time", selected_video.getTime());
        Log.d("Dislikes", "" + selected_video.getDislike_Num());
        Log.d("Likes", "" + selected_video.getLike_Num());
        Log.d("Category", "" + selected_video.getCategory());
        Log.d("ThisUsername", selected_video.getThis_username());
        Log.d("URL", selected_video.getURL());

        progrssview.setText( "Metadata peparado" );

        upload_to_specific_directory();

    }

    private void upload_to_specific_directory(){

        ///First send the video

        StorageReference mStorageRef;
        mStorageRef = FirebaseStorage.getInstance().getReference();

        //if (user != null) {

            progrssview.setText("Enviando, isso pode demorar bastante tempo");

            if( selected_video.getUri() != null ){

                Uri file = selected_video.getUri();
                StorageReference riversRef = mStorageRef.child("uploads/" + selected_video.getThis_username() + "/" + selected_video.getVideoName());

                riversRef.putFile(file)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                // Get a URL to the uploaded content
                                Log.d("ALERT", "onSuccess: " + "Video Sent");
                                update_progress_bar( "Enviado" );
                                get_URL_from_video();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Handle unsuccessful uploads
                                Log.d("ERROR", "could not send video");
                                update_progress_bar( "Erro, não pode enviar o video" );
                                // ...
                            }
                        });

            }else{
                Toast.makeText( this , "Erro ao enviar video, ERRO DE URI" , Toast.LENGTH_LONG ).show();
                progrssview.setText("Erro ao enviar video, ERRO DE URI");
            }

        //}else{
        //    Toast.makeText( this , "Não pode validar usuario" , Toast.LENGTH_LONG ).show();
        //    progrssview.setText("Falha, Usuario não autenticado");
        //}



    }

    private void get_URL_from_video(){

        progrssview.setText( "Recuperando URL" );

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage
                //.getReferenceFromUrl("gs://trabalhom3-89e3f.appspot.com/uploads/" + selected_video.getThis_username() + "/" )
                .getReferenceFromUrl("gs://trabalhom3-89e3f.appspot.com/")
                .child( "uploads/" )
                .child( selected_video.getThis_username() + "/" )
                .child( selected_video.getVideoName() );

        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Got the download URL for 'users/me/profile.png'
                Log.d("FOUND URI:", "onSuccess: " + uri.toString());
                selected_video.setURL( uri.toString() );
                update_progress_bar("URL recuperado" );
                update_database();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                Log.d("URI NOT FOUND", "");
            }
        });

    }

    public void update_progress_bar( String message ){
        progrssview.setText( message );
    }

    private boolean change_cat_val = true;

    private void update_database(){

        progrssview.setText( "Atualizando banco de dados" );
        Log.d("URL", selected_video.getURL() );

        String subs_name;
        if( selected_video.getVideoName().contains(".") ){
            subs_name = selected_video.getVideoName().substring( 0 , selected_video.getVideoName().indexOf(".") );
        }else{
            subs_name = selected_video.getVideoName();
        }

        Log.d("sub video name", subs_name );

        DatabaseReference myRef = database.getReference("Video_URL")
                .child( selected_video.getThis_username() ) ///User1
                .child( subs_name );    ///videoplayback

        myRef.child( "Category" ).setValue( selected_video.getCategory() );
        myRef.child( "Date" ).setValue( selected_video.getDate() );
        myRef.child( "Dislike_Num" ).setValue( selected_video.getDislike_Num() );
        myRef.child( "Like_Num" ).setValue( selected_video.getLike_Num() );
        myRef.child( "ViewCount" ).setValue( selected_video.getViewCount() );
        myRef.child( "Time" ).setValue( selected_video.getTime() );
        myRef.child( "URL" ).setValue( selected_video.getURL() );
        myRef.child( "VideoLenght" ).setValue( selected_video.getVideoLenght() );

        myRef = database.getReference("Categories").child( selected_video.getCategory() ).child("Num_of_Videos");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.

                if( change_cat_val ){

                    long value = dataSnapshot.getValue(long.class);
                    update_corresponding_category( value );
                    Log.d("", "Value is: " + value);

                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("", "Failed to read value.", error.toException());
            }
        });

    }

    private void update_corresponding_category( long databse_cat_val ){

        DatabaseReference newRef = database.getReference("Categories")
                .child( selected_video.getCategory() ).child( "Num_of_Videos" );
        newRef.setValue( databse_cat_val + 1 );

        change_cat_val = false;

        progrssview.setText( "Banco de dados atualizado" );
        Toast.makeText( this , "Video enviado" , Toast.LENGTH_LONG ).show();

        finish();

    }

}