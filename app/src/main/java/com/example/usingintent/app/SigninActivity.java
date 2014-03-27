package com.example.usingintent.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

/**
 * Created by timfong224 on 14年2月28日.
 */
public class SigninActivity extends Activity{

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signin);
    }

    public void onLoginClick(View view) {

        startActivity(new Intent(this, EventActivity.class));
    }
}
