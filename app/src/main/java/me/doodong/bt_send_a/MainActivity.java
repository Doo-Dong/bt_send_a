package me.doodong.bt_send_a;

import android.app.AlertDialog;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import me.doodong.bt_send_a.Thread.ConnectedThread;

public class MainActivity extends Activity {
    Button btn_0, btn_1, btn_2, btn_3;


    BluetoothAdapter btAdapter;
    private final static int REQUEST_ENABLE_BT = 1;

    Set<BluetoothDevice> pairedDevices;
    ArrayAdapter<String> btArrayAdapter;
    ArrayList<String> deviceAddressArray;

    ConnectedThread connectedThread = null;

    AlertDialog.Builder builder;
    ListView pair_lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getPermission();
        onBT();
        pairing();

        // local
        btn_0 = findViewById(R.id.btn_0);
        btn_1 = findViewById(R.id.btn_1);
        btn_2 = findViewById(R.id.btn_2);
        btn_3 = findViewById(R.id.btn_3);

        btn_0.setOnClickListener(v -> {
            // 블루투스 코드 작성
            if (btAdapter != null) {
                if(connectedThread != null) {
                    connectedThread.write("0");
                }
            }
        });

        btn_1.setOnClickListener(v -> {
            // 블루투스 코드 작성
            if (btAdapter != null) {
                if(connectedThread != null) {
                    connectedThread.write("1");
                }
            }
        });

        btn_2.setOnClickListener(v -> {
            // 블루투스 코드 작성
            if (btAdapter != null) {
                if(connectedThread != null) {
                    connectedThread.write("2");
                }
            }
        });

        btn_3.setOnClickListener(v -> {
            // 블루투스 코드 작성
            if (btAdapter != null) {
                if(connectedThread != null) {
                    connectedThread.write("3");
                }
            }
        });
    }

    void pairing() {
        builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.pairing, null);
        builder.setView(dialogView);

        pair_lv = dialogView.findViewById(R.id.pair_lv);

        // show paired devices
        btArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        deviceAddressArray = new ArrayList<>();

        btArrayAdapter.clear();
        if(deviceAddressArray!=null && !deviceAddressArray.isEmpty()){ deviceAddressArray.clear(); }
        pairedDevices = btAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            // There are paired devices. Get the name and address of each paired device.
            for (BluetoothDevice device : pairedDevices) {
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
                btArrayAdapter.add(deviceName);
                deviceAddressArray.add(deviceHardwareAddress);
            }
        }

        pair_lv.setAdapter(btArrayAdapter);

        AlertDialog dialog = builder.create();
        dialog.show();

        pair_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(),"연결중입니다...",Toast.LENGTH_SHORT).show();
                dialog.cancel();

                final String name = btArrayAdapter.getItem(position); // get name
                final String address = deviceAddressArray.get(position); // get address
                boolean flag = true;

                BluetoothDevice device = btAdapter.getRemoteDevice(address);
                BluetoothSocket btSocket = null;

                UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

                // create & connect socket
                try {
                    btSocket = device.createRfcommSocketToServiceRecord(uuid);
                    btSocket.connect();
                } catch (IOException e) {
                    flag = false;
                    Toast.makeText(getApplicationContext(), "connection failed!", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }

                if(flag){
                    connectedThread = new ConnectedThread(btSocket);
                    connectedThread.start();

                    new Handler().postDelayed(() -> {
                        //딜레이 후 시작할 코드 작성
                        Toast.makeText(getApplicationContext(), "connected to " + name, Toast.LENGTH_SHORT).show();
                        //connectedThread.write("A");
//                        dialog.cancel();
                    }, 1000);
                }
            }
        });
    }

    void onBT() {
        // Enable bluetooth
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!btAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    void getPermission() {
        // Get permission
        String[] permission_list = {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
        };

        ActivityCompat.requestPermissions(MainActivity.this, permission_list,  1);
    }
}