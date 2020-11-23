package com.example.trabalhom3;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.trabalhom3.hub.Hub_Activity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import static com.example.trabalhom3.utils.Hash_Util.getSha256Hash;


public class MainActivity extends AppCompatActivity {

    private FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        database = FirebaseDatabase.getInstance();

    }

    public void login_func(View view) {

        EditText this_usrnm_obj = findViewById( R.id.username_field );
        EditText this_pswrd_obj = findViewById( R.id.password_field );

        final String this_usrnm = this_usrnm_obj.getText().toString();
        final String this_pswrd_hash = getSha256Hash(this_pswrd_obj.getText().toString());

        Log.d("This username: ", this_usrnm );
        Log.d("This password unhashed:", this_pswrd_obj.getText().toString() );
        Log.d("This password hashed: ", this_pswrd_hash );

        DatabaseReference myRef = database.getReference("Users").child( this_usrnm );

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.getValue(String.class);

                if( value != null ){

                    if( value.equals( this_pswrd_hash ) ){

                        Log.d("Login report: ", "Succesful login");
                        succesful_login( this_usrnm );

                    }else{

                        Log.d("Login report: ", "Incorrect password");
                        failed_login( "Senha Incorreta" );

                    }

                }else{
                    failed_login( "Usuario não encontrado" );
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("Login report: ", "Invalid username", error.toException());
                failed_login( "Erro de servidor, cheque conexão com internet" );
            }
        });

    }

    public void register_func(View view) {

        Intent registry = new Intent( this , Register_Activity.class );
        startActivity( registry );

    }

    protected void succesful_login( String this_usrnm ){

        Toast.makeText( this , "Acessando " + this_usrnm + "...", Toast.LENGTH_LONG ).show();

        Intent hub = new Intent( this , Hub_Activity.class );
        hub.putExtra( "this_usrnm" , this_usrnm );
        startActivity( hub );

    }

    public void failed_login( String err_msg ){

        Toast.makeText( this , err_msg , Toast.LENGTH_LONG ).show();

    }

}