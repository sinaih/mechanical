package com.example.robot.mechanical;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;

public class Splash extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        new CountDownTimer(4000, 100) {
            public void onTick(long millisUntilFinished) {

            }
            public void onFinish() {
                Intent myIntent = new Intent(Splash.this, MainActivity.class);
                Splash.this.startActivity(myIntent);
            }
        }.start();
    }
}
