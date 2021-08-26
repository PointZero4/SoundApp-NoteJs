package com.example.soundapp;


import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.example.soundapp.Responses.ApiClient;
import com.example.soundapp.Responses.RetrofitClient;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;




public class RegisterUser extends Activity implements View.OnClickListener{
    private EditText editTextFullname,editTextDeviceId,editTextEmail,editTextPassword;
//    private TextView TextViewDecibel;
    private ProgressBar progressBar;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);


        TextView banner = (TextView) findViewById(R.id.banner);
        banner.setOnClickListener(this);

        TextView registerUser = (TextView) findViewById(R.id.registerUser);
        registerUser.setOnClickListener(this);

        TextView back = (TextView) findViewById(R.id.back);
        back.setOnClickListener(this);
        //Underlining the text
        back.setPaintFlags(back.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        editTextFullname = (EditText)findViewById(R.id.fullName);
        editTextDeviceId = (EditText)findViewById(R.id.deviceID);
        editTextEmail = (EditText)findViewById(R.id.edEmail);
        editTextPassword = (EditText)findViewById(R.id.edPassword);


        progressBar = (ProgressBar)findViewById(R.id.progressBar);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.banner:
            case R.id.back:
                startActivity(new Intent(this,MainActivity.class));
                break;
            case R.id.registerUser:
                registerUser();
                break;

        }
    }

    private void registerUser() { //Taking a user data

        String deviceId = editTextDeviceId.getText().toString().trim();
        String name = editTextFullname.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();



        if (name.isEmpty()) {
            editTextFullname.setError("Username is required!");
            editTextFullname.requestFocus();
            return;
        }
        if (email.isEmpty()) {
            editTextEmail.setError("Email is required!");
            editTextEmail.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Please provide valid email!");
            editTextEmail.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            editTextPassword.setError("Password is required!");
            editTextPassword.requestFocus();
            return;
        }
        if (password.length() < 6) {
            editTextPassword.setError("Min. password should be 6 characters!");
            editTextPassword.requestFocus();
            return;
        }
        if (deviceId.isEmpty()) {
            editTextDeviceId.setError("Device ID is required!");
            editTextDeviceId.requestFocus();
            return;
        }
        progressBar.setVisibility(View.VISIBLE);




        Call<ResponseBody> call = RetrofitClient
                .getInstance()
                .getUserService()
                .createUser(name,email,password,deviceId);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.code()==200){
                    Toast.makeText(RegisterUser.this,"User registered successfully!",Toast.LENGTH_LONG).show();
                    //Redirect to Main Login Page
                    startActivity(new Intent(RegisterUser.this, MainActivity.class));
                }else if(response.code()==404){
                    Toast.makeText(RegisterUser.this,"Email already exists!",Toast.LENGTH_LONG).show();
                }
                progressBar.setVisibility(View.GONE);
            }



            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
              Toast.makeText(RegisterUser.this,t.getMessage(),Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
            }

        });

    }
}
