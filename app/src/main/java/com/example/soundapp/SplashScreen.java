package com.example.soundapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import java.io.IOException;

public class SplashScreen extends Activity {
    Thread timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        timer = new Thread(){
            @Override
            public void run() {
                try{
                    synchronized (this){
                        wait(5000);
                    }
                }catch (InterruptedException e){
                    e.printStackTrace();
                }finally {
                    Intent intent = new Intent(SplashScreen.this,Welcome.class);
                    startActivity(intent);
                    finish();
                }
            }
        };
        timer.start();
    }
}