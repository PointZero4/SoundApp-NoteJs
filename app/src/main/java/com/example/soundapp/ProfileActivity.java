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

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;
import app.akexorcist.bluetotohspp.library.DeviceList;


public class ProfileActivity extends AppCompatActivity {

    TextView username, msoundLevel, email, deviceId;


    final String data1 = "1";
    final String data2 = "0";
    BluetoothSPP bluetooth;
    Button connect;

    // Socket implementation
    private Socket mSocket;

    {
        try {
            String serverUrl = "http://smaster-backend.herokuapp.com";
            mSocket = IO.socket(serverUrl);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private final Emitter.Listener onNewMessage = args -> runOnUiThread(new Runnable() {
        @Override
        public void run() {
            try {
                JSONObject data = (JSONObject) args[0];

                Intent intent = getIntent();
                if (intent.getExtras() != null) {

                    String soundLevel;
                    soundLevel = data.getString("soundLevel");
                    String sound = intent.getStringExtra("soundLevel");
                    msoundLevel.setText(soundLevel);
                    System.out.println(soundLevel);


                    float number = Float.parseFloat(sound);
                    if (number == 0.0) {
                        Toast.makeText(ProfileActivity.this, "Please connect your sound detector device", Toast.LENGTH_LONG).show();
                    } else if (number > 55.0) {
                        bluetooth.send(data1, true);
                    } else {
                        bluetooth.send(data2, true);
                    }


                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    });


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        username = findViewById(R.id.name);
        msoundLevel = findViewById(R.id.textHome);
        email = findViewById(R.id.mail);
        deviceId = findViewById(R.id.id);
        connect = (Button) findViewById(R.id.connect);
        bluetooth = new BluetoothSPP(this);

        mSocket.connect();
        mSocket.on("receive_data", onNewMessage);

        // Getting the User details
        Intent intent = getIntent();

        if (intent.getExtras() != null) {
            String passedUsername = intent.getStringExtra("name");
            username.setText("Welcome,\n" + passedUsername + "!");

            String passedEmail = intent.getStringExtra("email");
            email.setText("Email:\n" + passedEmail);
            String passedId = intent.getStringExtra("deviceId");
            deviceId.setText("DeviceId:\n" + passedId);


        }

        //Sound Level Animation
        msoundLevel.setOnClickListener(v -> msoundLevel.animate().rotationY(360f).start());


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


        ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {


                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // There are no request codes
                        Intent data = result.getData();
//                            doSomeOperations();
                        bluetooth.connect(data);
                    }
                });


        connect.setOnClickListener(v -> {
            if (bluetooth.getServiceState() == BluetoothState.STATE_CONNECTED) {
                bluetooth.disconnect();
            } else {
                Intent intent1 = new Intent(getApplicationContext(), DeviceList.class);
                someActivityResultLauncher.launch(intent1);


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

        mSocket.disconnect();
        mSocket.off("receive_data", onNewMessage);
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