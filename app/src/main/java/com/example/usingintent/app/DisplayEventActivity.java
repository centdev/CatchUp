package com.example.usingintent.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;



/**
 * Created by timfong224 on 3/14/14.
 */
public class DisplayEventActivity extends Activity {



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.display_event);


        // getIntent() is a method from the started activity
        Intent myIntent = getIntent(); // gets the previously created intent
        String title = myIntent.getStringExtra("firstKeyName"); // will return "FirstKeyValue"
        String venue = myIntent.getStringExtra("secondKeyName"); // will return "SecondKeyValue"



    }
}
