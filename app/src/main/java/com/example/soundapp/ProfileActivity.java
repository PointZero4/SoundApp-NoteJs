package com.example.soundapp;


import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;
import app.akexorcist.bluetotohspp.library.DeviceList;


public class ProfileActivity extends AppCompatActivity {

    TextView username, soundLevel, email, deviceId;


    final String data = "1";
    final String data1 = "0";
    BluetoothSPP bluetooth;
    Button connect;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        username = findViewById(R.id.name);
        soundLevel = findViewById(R.id.textHome);
        email = findViewById(R.id.mail);
        deviceId = findViewById(R.id.id);
        connect = (Button) findViewById(R.id.connect);
        bluetooth = new BluetoothSPP(this);

        // Getting the User details
        Intent intent = getIntent();

        if (intent.getExtras() != null) {
            String passedUsername = intent.getStringExtra("name");
            username.setText("Welcome,\n" + passedUsername + "!");

            String passedEmail = intent.getStringExtra("email");
            email.setText("Email:\n" + passedEmail);
            String passedSoundLevel = intent.getStringExtra("sound");
            soundLevel.setText(passedSoundLevel);
            String passedId = intent.getStringExtra("deviceId");
            deviceId.setText("DeviceId:" + passedId);

            float number = Float.parseFloat(passedSoundLevel);

            if (number == 0.0) {
                Toast.makeText(ProfileActivity.this, "Please connect your sound detector device", Toast.LENGTH_LONG).show();
            } else if (number > 55.0) {
                bluetooth.send(data, true);
            } else {
                bluetooth.send(data1, true);
            }

        }

        //Sound Level Animation
        soundLevel.setOnClickListener(v -> soundLevel.animate().rotationY(360f).start());


        //Bluetooth Connection
        if (!bluetooth.isBluetoothAvailable()) {
            Toast.makeText(getApplicationContext(), "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
        }
        bluetooth.setBluetoothConnectionListener(new BluetoothSPP.BluetoothConnectionListener() {
            @Override
            public void onDeviceConnected(String name, String address) {
                connect.setText("Connected to" + name);
            }

            @Override
            public void onDeviceDisconnected() {

                connect.setText("Connection lost");
            }

            @Override
            public void onDeviceConnectionFailed() {

                connect.setText("Unable to connect");
            }
        });

        connect.setOnClickListener(v -> {
            if (bluetooth.getServiceState() == BluetoothState.STATE_CONNECTED) {
                bluetooth.disconnect();
            } else {
                Intent intent1 = new Intent(getApplicationContext(), DeviceList.class);
                startActivityForResult(intent1, BluetoothState.REQUEST_CONNECT_DEVICE);
//                ActivityResultLauncher<Intent> resultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
//                    @Override
//                    public void onActivityResult(ActivityResult result) {
//                        if(result.getResultCode() == Activity.RESULT_OK){
//                            Intent intent1 = new Intent(getApplicationContext(), DeviceList.class);
//                           // startActivity(intent1,BluetoothState.REQUEST_CONNECT_DEVICE);
//                        }
//                    }
//
//                });
            }
        });
    }

    public void onStart() {


        super.onStart();

        if (!bluetooth.isBluetoothEnabled()) {
            bluetooth.enable();
        } else {
            if (!bluetooth.isServiceAvailable()) {
                bluetooth.setupService();
                bluetooth.startService(BluetoothState.DEVICE_OTHER);
            }
        }
    }

    public void onDestroy() {

        super.onDestroy();
        bluetooth.stopService();
    }


    @SuppressLint("SetTextI18n")
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == BluetoothState.REQUEST_CONNECT_DEVICE) {
            if (requestCode == Activity.RESULT_OK)
                bluetooth.connect(data);
        } else if (requestCode == BluetoothState.REQUEST_ENABLE_BT) {
            if (requestCode == Activity.RESULT_OK) {
                bluetooth.setupService();
            } else {
                Toast.makeText(getApplicationContext(), "Bluetooth was not enabled", Toast.LENGTH_LONG).show();
                finish();
            }
        }

    }

}