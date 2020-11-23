package com.example.trabalhom3;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.trabalhom3.data_structures.Device_user;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.example.trabalhom3.utils.Hash_Util.getSha256Hash;

public class Register_Activity extends AppCompatActivity {

    private FirebaseDatabase database;
    private boolean non_duplicate_user = false;
    private Device_user user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);

        database = FirebaseDatabase.getInstance();

    }

    public void new_register_func(View view) {

        TextView this_usrnm_obj = findViewById( R.id.regist_username_field );
        TextView this_pass1_obj = findViewById( R.id.regist_first_password_field );
        TextView this_pass2_obj = findViewById( R.id.regist_second_password_field );

        final String this_usrnm = this_usrnm_obj.getText().toString();
        final String this_pass1 = this_pass1_obj.getText().toString();
        final String this_pass2 = this_pass2_obj.getText().toString();

        if( this_usrnm.isEmpty() || this_pass1.isEmpty() || this_pass2.isEmpty() ){
            Toast.makeText( this , "Todos os campos tem que estar preenchidos" , Toast.LENGTH_LONG ).show();
            Log.d( "Error" , "Fields not filled");
            return;
        }

        if( this_pass1.equals( this_pass2 ) ){

            ///First check if username already exists
            DatabaseReference checkRef = database.getReference("Users").child( this_usrnm );
            checkRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.
                    String value = dataSnapshot.getValue(String.class);

                    if( value == null ){
                        Log.d("Alert , ", "non duplicate user." );

                        user = new Device_user();

                        user.setUsername( this_usrnm );
                        user.setHashed_password( getSha256Hash( this_pass1 ) );

                        register_user();

                    }else{
                        Log.d("Alert , ", "duplicate user." );
                        register_status( false );
                    }
                }
                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    register_status( false );
                }
            });

        }else{
            Toast.makeText( this , "Senhas tem que ser iguais" , Toast.LENGTH_LONG ).show();
            Log.d("Error", "Passwords are not the same");
            return;
        }

    }

    public void exit_func(View view) {

        finish();

    }

    private FirebaseUser user_storage;
    File user_metadata = null;

    protected void register_user(){

        ///Creating user metadata in file for further storage on firebase

        String FILENAME = "user_metadata";
        String string = "";                 ///It is necessary to have a file to create a folder

        try {

            user_metadata = new File(getFilesDir() + FILENAME + ".txt");
            FileOutputStream fos = new FileOutputStream(user_metadata);
            fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);

            fos.write(string.getBytes());
            fos.close();
            Uri user_metadata_uri = Uri.fromFile( user_metadata );

            user_storage = FirebaseAuth.getInstance().getCurrentUser();
            StorageReference mStorageRef;
            mStorageRef = FirebaseStorage.getInstance().getReference();

            StorageReference riversRef = mStorageRef.child("uploads/" + user.getUsername() + "/" + user_metadata.getName());

            riversRef.putFile(user_metadata_uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            user_metadata.delete();
                            create_database_data( user );
                            storage_status( "Storage atualizado" );
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle unsuccessful uploads
                            user_metadata.delete();
                            storage_status( "Falha ao criar usuario" );
                            // ...
                        }
                    });

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(this , "Não pode ser feito metadata de usuario, abortando!" , Toast.LENGTH_LONG).show();
            return;
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this , "Não pode ser feito metadata de usuario, abortando!" , Toast.LENGTH_LONG).show();
            return;
        }

    }


    public void register_status( boolean status ){

        if( status ){
            Toast.makeText( this , "Usuario cadastrado com sucesso" , Toast.LENGTH_LONG ).show();
            finish();
        }else{
            Toast.makeText( this , "Falha no cadastro" , Toast.LENGTH_LONG ).show();
        }

    }

    public void storage_status( String message ){
        Toast.makeText( this , message , Toast.LENGTH_LONG ).show();
    }

    private void create_database_data( Device_user user ){

        DatabaseReference ref = database.getReference("Users");
        DatabaseReference dataRef = ref.child( user.getUsername() );
        dataRef.setValue( user.getHashed_password() , new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    Log.d("ERROR ", "Data could not be saved " + databaseError.getMessage() );
                    register_status( false );
                } else {
                    Log.d("Success, ", "Data saved successfully." );
                    register_status( true );
                }
            }
        });

    }

}