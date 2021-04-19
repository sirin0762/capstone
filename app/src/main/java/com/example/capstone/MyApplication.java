package com.example.capstone;

import android.app.Application;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

public class MyApplication extends Application {
    public static BluetoothDevice device;

    public static BluetoothSocket mBluetoothSocket;


    public synchronized BluetoothDevice getBtConnection() {
        return device;
    }
}