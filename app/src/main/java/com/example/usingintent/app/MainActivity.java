package com.example.usingintent.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onRegClick(View view) {
        startActivity(new Intent(this, RegistrationActivity.class));
    }

    public void onSignClick(View view) {
        startActivity(new Intent(this, SigninActivity.class));
    }


}
