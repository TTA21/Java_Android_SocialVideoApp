package com.example.trabalhom3;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.trabalhom3.data_structures.Device_user;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.example.trabalhom3.utils.Hash_Util.getSha256Hash;

public class Register_Activity extends AppCompatActivity {

    private FirebaseDatabase database;
    private boolean non_duplicate_user = false;

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

                        Device_user user = new Device_user();

                        user.setUsername( this_usrnm );
                        user.setHashed_password( getSha256Hash( this_pass1 ) );

                        register_user( user );

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

    protected void register_user( Device_user user ){

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


    public void register_status( boolean status ){

        if( status ){
            Toast.makeText( this , "Usuario cadastrado com sucesso" , Toast.LENGTH_LONG ).show();
            finish();
        }else{
            Toast.makeText( this , "Falha no cadastro" , Toast.LENGTH_LONG ).show();
        }

    }
}