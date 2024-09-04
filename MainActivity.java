package com.example.heartmonitor;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "HeartMonitor";
    private static final String DEVICE_NAME = "HC-05";
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private TextView bpmTextView;
    private Button connectButton;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothSocket bluetoothSocket;
    private Handler handler;
    private boolean isConnected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bpmTextView = findViewById(R.id.bpmTextView);
        connectButton = findViewById(R.id.connectButton);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        handler = new Handler();

        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isConnected) {
                    connectToDevice();
                } else {
                    disconnectFromDevice();
                }
            }
        });
    }

    private void connectToDevice() {
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        BluetoothDevice device = null;

        for (BluetoothDevice d : pairedDevices) {
            if (DEVICE_NAME.equals(d.getName())) {
                device = d;
                break;
            }
        }

        if (device != null) {
            try {
                bluetoothSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
                bluetoothSocket.connect();
                isConnected = true;
                connectButton.setText("Disconnect");
                startListeningForData();
                Toast.makeText(this, "Connected to HC-05", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                Log.e(TAG, "Error connecting to device", e);
                Toast.makeText(this, "Connection failed", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "HC-05 not found", Toast.LENGTH_SHORT).show();
        }
    }

    private void disconnectFromDevice() {
        try {
            bluetoothSocket.close();
            isConnected = false;
            connectButton.setText("Connect");
            Toast.makeText(this, "Disconnected", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Log.e(TAG, "Error disconnecting from device", e);
            Toast.makeText(this, "Disconnection failed", Toast.LENGTH_SHORT).show();
        }
    }

    private void startListeningForData() {
        final InputStream inputStream;

        try {
            inputStream = bluetoothSocket.getInputStream();
        } catch (IOException e) {
            Log.e(TAG, "Error getting input stream", e);
            return;
        }

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                byte[] buffer = new byte[1024];
                int bytes;

                while (isConnected) {
                    try {
                        bytes = inputStream.read(buffer);
                        final String receivedData = new String(buffer, 0, bytes);
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                bpmTextView.setText("BPM: " + receivedData);
                            }
                        });
                    } catch (IOException e) {
                        Log.e(TAG, "Error reading input stream", e);
                        break;
                    }
                }
            }
        });

        thread.start();
    }
}
