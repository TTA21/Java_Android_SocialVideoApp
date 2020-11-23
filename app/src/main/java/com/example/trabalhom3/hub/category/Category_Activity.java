package com.example.trabalhom3.hub.category;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.trabalhom3.R;
import com.google.firebase.database.FirebaseDatabase;

public class Category_Activity extends AppCompatActivity {

    private String chosen_category = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.category_activity);

        Bundle extras = getIntent().getExtras();
        chosen_category = extras.getString( "chosen_category" );

        Log.d("Chosen Category", chosen_category);

    }

}
