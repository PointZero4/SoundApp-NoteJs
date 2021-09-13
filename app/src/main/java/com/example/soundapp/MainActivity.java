package com.example.soundapp;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;

import com.example.soundapp.Responses.ApiClient;
import com.example.soundapp.Responses.LoginRequest;
import com.example.soundapp.Responses.LoginResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends Activity implements View.OnClickListener {

    private EditText email, password;


    private ProgressBar progressBar;
    CheckBox checkBox;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        TextView register = (TextView) findViewById(R.id.register);
        register.setOnClickListener(this);
        register.setPaintFlags(register.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        Button logIn = (Button) findViewById(R.id.signIn);
        logIn.setOnClickListener(this);

        email = (EditText) findViewById(R.id.edEmail);
        password = (EditText) findViewById(R.id.edPassword);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        checkBox = findViewById(R.id.checkBox);

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    password.setTransformationMethod(null);
                }else {
                    password.setTransformationMethod(new PasswordTransformationMethod());
                }
            }
        });


    }



    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v){
        switch (v.getId()) {
            // redirect to RegisterUser page
            case R.id.register:
                startActivity(new Intent(this, RegisterUser.class));
                break;
            // redirect to User Profile Page
            case R.id.signIn:
                userLogin();
                break;



        }
    }

    private void userLogin() {

        if (TextUtils.isEmpty(email.getText().toString())) {
            email.setError("Email required!");
            email.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()) {
            email.setError("Please provide valid email!");
            email.requestFocus();
            return;
        } else if (TextUtils.isEmpty(password.getText().toString())) {
            password.setError("Password required!");
            password.requestFocus();
            return;
        }
        if (password.length() < 6) {
            password.setError("Min. password should be 6 characters!");
            password.requestFocus();
            return;
        }
        progressBar.setVisibility(View.VISIBLE);


        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(email.getText().toString());
        loginRequest.setPassword(password.getText().toString());
        Call<LoginResponse> loginResponseCall = ApiClient.getUserService().userLogin(loginRequest);

        loginResponseCall.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(@NonNull Call<LoginResponse> call, @NonNull Response<LoginResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "User Logged in successfully!", Toast.LENGTH_LONG).show();
                    LoginResponse loginResponse = response.body();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            assert loginResponse != null;
                            Intent iDetail = new Intent(MainActivity.this,ProfileActivity.class);
                            iDetail.putExtra("name",loginResponse.getUser().getName());
                            iDetail.putExtra("soundLevel",loginResponse.getUser().getSoundLevel());
                            iDetail.putExtra("email",loginResponse.getUser().getEmail());
                            iDetail.putExtra("deviceId",loginResponse.getUser().getDeviceId());

                            startActivity(iDetail);

//                            startActivity(new Intent(MainActivity.this, ProfileActivity.class)
//                                    .putExtra("soundLevel", loginResponse.getUser().getSoundLevel()));

                        }
                    }, 700);
                } else {
                    Toast.makeText(MainActivity.this, "Incorrect Email or Password ", Toast.LENGTH_LONG).show();
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
            }
        });

    }
}
