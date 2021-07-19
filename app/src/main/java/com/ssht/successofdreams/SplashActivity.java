package com.ssht.successofdreams;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class SplashActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Thread() {
            public void run() {
                try {
                    sleep(2000);
                        SplashActivity.this.startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                        SplashActivity.this.finish();


                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
}