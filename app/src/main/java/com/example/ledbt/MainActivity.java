package com.example.ledbt;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.hardware.ConsumerIrManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    public static Vibrator mVibrator = null;
    BluetoothDevice ledBT = null;
    BluetoothSocket mBluetoothSocket = null;
    OutputStream output = null;
    private BluetoothAdapter BA;
    private Set<BluetoothDevice> pairedDevices;
    //A00101A2
    final byte[] On = {(byte) 0xA0, 0x01, 0x01, (byte) 0xA2};
    //A00100A1
    final byte[] Off = {(byte) 0xA0, 0x01, 0x0, (byte) 0xA1};

    @SuppressLint("InlinedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initIR();
        initBT();
        initAlarm();
        initVibrator();
    }

    public void initVibrator() {
        mVibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
    }

    public void vibratorIt()
    {
        mVibrator.vibrate(50);
    }

    public void initAlarm() {
        Thread alarmTD = new Thread(runnable);
        alarmTD.start();
    }

    public void initBT() {
        Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(turnOn, 0);
        BA = BluetoothAdapter.getDefaultAdapter();

        pairedDevices = BA.getBondedDevices();

        ArrayList list = new ArrayList();
        for (BluetoothDevice bt : pairedDevices) {
            if (bt.getName().compareTo("BT04-A") == 0) {
                ledBT = bt;
            }
        }
        if (ledBT != null) {
            try {
                if (mBluetoothSocket == null) {
                    mBluetoothSocket = ledBT.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
                }
                if (mBluetoothSocket.isConnected() == false) {
                    mBluetoothSocket.connect();//start connection
                    output = mBluetoothSocket.getOutputStream();
                }
                Log.e("PCC", "connected");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void myConnect() {
        try {
            if (mBluetoothSocket != null) {
                if (mBluetoothSocket.isConnected() == false) {
                    mBluetoothSocket.connect();//start connection
                    output = mBluetoothSocket.getOutputStream();
                    Log.e("PCC", "connected2");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void lightOnClick(View v) {
        vibratorIt();
        if (mBluetoothSocket.isConnected()) {
            try {
                if (output != null) {
                    output.write(On);
                    output.flush();
                    Log.e("PCC", "on");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void lightOffClick(View v) {
        vibratorIt();
        if (mBluetoothSocket.isConnected()) {
            try {
                if (output != null) {
                    output.write(Off);
                    output.flush();
                    Log.e("PCC", "off");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        myConnect();
    }

    @Override
    protected void onResume() {
        super.onResume();
        myConnect();
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        myConnect();
    }

    private static final String TAG = "PCC";
    public static ConsumerIrManager mCIR = null;

    private static final int keyPower[] = {228, 892, 224, 2248, 228, 752, 224, 2792, 228, 1296, 232, 1288, 228, 1432, 232, 1832, 228, 14268, 232, 880, 224, 756, 308, 668, 312, 668, 228, 748, 228, 2792, 228, 748, 232, 748, 232, 15420, 224, 896, 232, 2240, 232, 748, 232, 2784, 224, 1300, 228, 1292, 232, 1428, 224, 1840, 224, 14268, 232, 884, 232, 1832, 232, 1836, 224, 752, 228, 752, 228, 2788, 232, 748, 228, 748, 232};
    private static final int keyChannelUp[] = {224, 892, 224, 2252, 224, 752, 224, 2796, 228, 1292, 232, 1292, 224, 1432, 232, 1836, 228, 14264, 228, 888, 228, 1024, 228, 748, 232, 744, 232, 748, 232, 2512, 236, 744, 224, 752, 228, 15468, 280, 840, 232, 2244, 232, 744, 224, 2792, 228, 1296, 232, 1288, 228, 1432, 228, 1836, 228, 14268, 224, 892, 224, 2112, 224, 1844, 220, 756, 224, 752, 228, 2520, 228, 748, 228, 752, 228};
    private static final int keyChannelDown[] = {224, 892, 232, 2244, 232, 744, 232, 2788, 224, 1296, 228, 1296, 232, 1424, 228, 1840, 224, 14268, 232, 884, 232, 880, 224, 756, 224, 752, 228, 752, 224, 2656, 228, 752, 228, 748, 232, 15464, 232, 884, 232, 2244, 228, 748, 232, 2788, 232, 1288, 228, 1296, 228, 1428, 224, 1844, 232, 14260, 228, 888, 228, 1972, 228, 1840, 224, 752, 228, 748, 228, 2656, 228, 748, 232, 748, 232};
    private static final int keyVolumnUp[] = {228, 900, 192, 2276, 224, 764, 192, 2828, 228, 1292, 228, 1296, 224, 1432, 228, 1840, 228, 13532, 224, 904, 192, 1444, 228, 764, 192, 780, 192, 780, 192, 2132, 232, 760, 236, 728, 236, 16660, 228, 900, 188, 2276, 228, 764, 188, 2828, 228, 1292, 228, 1292, 232, 1428, 228, 1840, 228, 13524, 228, 904, 188, 2552, 228, 1844, 228, 764, 188, 780, 192, 2136, 228, 764, 188, 776, 192};
    private static final int keyVolumnDown[] = {224, 904, 188, 2276, 228, 764, 188, 2828, 228, 1316, 188, 1332, 188, 1448, 228, 1840, 228, 13532, 224, 904, 188, 1308, 232, 764, 188, 784, 188, 780, 188, 2276, 228, 764, 192, 772, 188, 16724, 228, 900, 188, 2276, 228, 764, 192, 2828, 224, 1296, 228, 1292, 276, 1384, 228, 1840, 228, 13528, 228, 900, 236, 2368, 228, 1844, 228, 764, 236, 736, 236, 2228, 228, 764, 236, 728, 236};
    private static final int key1[] = {232, 888, 232, 2244, 220, 756, 228, 2788, 232, 1292, 224, 1300, 224, 1432, 232, 1836, 224, 14268, 228, 888, 284, 2596, 224, 756, 228, 748, 232, 748, 224, 888, 232, 748, 224, 752, 232, 15412, 228, 888, 232, 2244, 224, 752, 232, 2788, 232, 1288, 228, 1296, 228, 1428, 232, 1836, 224, 14268, 232, 884, 220, 1572, 228, 1840, 228, 752, 220, 756, 228, 884, 224, 756, 228, 748, 232};
    private static final int key2[] = {232, 888, 232, 2240, 228, 752, 228, 2788, 224, 1300, 228, 1292, 232, 1428, 224, 1840, 232, 14264, 224, 892, 224, 2520, 232, 748, 224, 752, 232, 748, 224, 1024, 228, 752, 220, 756, 228, 15452, 224, 892, 228, 2248, 228, 748, 228, 2792, 228, 1292, 224, 1300, 224, 1432, 232, 1836, 224, 14268, 232, 884, 224, 1436, 224, 1840, 232, 744, 228, 752, 228, 1020, 224, 756, 228, 748, 224};
    private static final int key3[] = {232, 888, 232, 2240, 224, 756, 228, 2788, 232, 1292, 224, 1296, 232, 1428, 224, 1844, 224, 14268, 232, 884, 224, 2384, 232, 748, 224, 752, 228, 752, 304, 1080, 228, 752, 232, 744, 228, 15444, 224, 896, 224, 2248, 232, 748, 224, 2792, 228, 1296, 232, 1288, 224, 1436, 228, 1836, 224, 14272, 228, 884, 224, 1300, 224, 1840, 232, 748, 224, 752, 232, 1156, 224, 752, 312, 664, 224};
    private static final int key4[] = {224, 896, 224, 2248, 228, 752, 316, 2700, 224, 1300, 228, 1292, 224, 1436, 228, 1840, 228, 14264, 308, 808, 228, 2244, 224, 756, 228, 748, 232, 744, 228, 1296, 232, 748, 224, 752, 316, 15356, 228, 888, 232, 2244, 224, 752, 312, 2708, 232, 1288, 228, 1296, 228, 1432, 224, 1840, 228, 14268, 232, 880, 228, 1160, 228, 1836, 224, 752, 316, 664, 308, 1212, 232, 748, 224, 752, 228};
    private static final int key5[] = {224, 896, 220, 2252, 228, 752, 228, 2788, 224, 1300, 228, 1292, 224, 1436, 224, 1840, 232, 14264, 224, 892, 228, 2108, 224, 752, 232, 748, 224, 752, 312, 1348, 232, 748, 308, 668, 316, 15360, 228, 892, 228, 2244, 232, 748, 224, 2792, 228, 1296, 232, 1292, 224, 1432, 232, 1836, 224, 14268, 228, 888, 224, 1024, 228, 1840, 232, 744, 228, 752, 232, 1424, 224, 756, 228, 748, 224};
    private static final int key6[] = {224, 892, 224, 2252, 228, 748, 232, 2788, 224, 1296, 232, 1292, 224, 1432, 228, 1840, 232, 14260, 228, 888, 232, 1972, 224, 752, 232, 748, 224, 752, 228, 1568, 220, 756, 228, 752, 220, 15420, 232, 888, 232, 2240, 228, 752, 228, 2788, 224, 1300, 228, 1292, 232, 1428, 224, 1840, 232, 14264, 224, 888, 232, 884, 224, 1844, 228, 748, 232, 744, 228, 1568, 232, 748, 224, 752, 232};
    private static final int key7[] = {224, 892, 224, 2252, 228, 748, 224, 2796, 224, 1296, 232, 1292, 228, 1428, 228, 1840, 232, 14264, 224, 888, 232, 1836, 224, 752, 312, 668, 304, 672, 312, 1616, 236, 744, 312, 664, 316, 15344, 224, 892, 224, 2252, 228, 748, 236, 2784, 224, 1296, 232, 1292, 224, 1432, 228, 1840, 220, 14272, 232, 884, 232, 748, 224, 1840, 232, 748, 224, 752, 312, 1616, 224, 756, 228, 752, 220};
    private static final int key8[] = {232, 884, 224, 2252, 228, 752, 228, 2788, 224, 1296, 232, 1292, 224, 1432, 228, 1840, 232, 14264, 224, 888, 232, 1700, 224, 752, 228, 752, 304, 672, 312, 1756, 228, 748, 228, 752, 228, 15428, 224, 896, 220, 2252, 228, 752, 228, 2788, 224, 1300, 228, 1292, 224, 1436, 224, 1840, 232, 14264, 224, 888, 232, 2788, 232, 1832, 228, 752, 232, 744, 228, 1840, 228, 748, 308, 620, 344};
    private static final int key9[] = {232, 888, 232, 2240, 224, 756, 228, 2788, 224, 1300, 224, 1296, 232, 1428, 224, 1840, 228, 14268, 224, 888, 228, 1568, 232, 744, 312, 668, 312, 664, 312, 1892, 228, 748, 224, 756, 312, 15384, 232, 888, 228, 2248, 220, 756, 228, 2788, 232, 1292, 224, 1300, 228, 1428, 224, 1844, 224, 14268, 232, 884, 224, 2660, 228, 1836, 232, 744, 228, 752, 316, 1884, 228, 752, 228, 748, 224};
    private static final int key0[] = {224, 896, 224, 2248, 228, 752, 232, 2784, 228, 1296, 228, 1292, 224, 1436, 228, 1836, 232, 14264, 224, 892, 228, 2788, 236, 744, 228, 748, 232, 748, 224, 752, 232, 748, 224, 752, 232, 15424, 232, 888, 224, 2252, 224, 752, 232, 2784, 228, 1296, 228, 1292, 232, 1428, 224, 1840, 232, 14264, 224, 892, 228, 1700, 224, 1844, 224, 752, 316, 664, 224, 752, 232, 744, 228, 752, 228};
    private static final int keyOK[] = {228, 888, 232, 2244, 220, 756, 312, 2708, 228, 1292, 224, 1300, 228, 1428, 224, 1844, 228, 14264, 232, 884, 224, 1844, 228, 748, 224, 756, 228, 1020, 232, 1428, 224, 752, 232, 748, 224, 65180, 232, 888, 232, 2244, 220, 756, 228, 2788, 232, 1292, 228, 1292, 232, 1428, 224, 1844, 228, 14264, 232, 884, 224, 752, 232, 1836, 224, 752, 232, 1020, 224, 1436, 224, 752, 232, 744, 228, 16028, 228, 888, 232, 2244, 220, 756, 312, 2704, 224, 1300, 228, 1292, 232, 1428, 224, 1844, 228, 14264, 224, 892, 228, 748, 308, 1760, 224, 752, 232, 1020, 224, 1436, 228, 748, 224, 752, 228};
    private static final int keyRETURN[] = {224, 896, 220, 2252, 228, 752, 232, 2784, 224, 1300, 228, 1292, 224, 1436, 224, 1840, 232, 14264, 224, 888, 232, 1292, 224, 752, 232, 748, 224, 1028, 224, 1976, 232, 748, 224, 752, 228, 15520, 224, 892, 228, 2248, 228, 748, 228, 2792, 228, 1292, 224, 1300, 228, 1428, 232, 1836, 232, 14260, 232, 884, 224, 2384, 228, 1840, 232, 744, 228, 1024, 232, 1972, 220, 756, 228, 748, 224};
    private static final int keyINFO[] = {224, 892, 228, 2248, 228, 748, 224, 2796, 224, 1296, 232, 1292, 224, 1432, 228, 1840, 228, 14268, 228, 884, 224, 1164, 228, 748, 224, 756, 224, 1024, 232, 2108, 224, 752, 228, 748, 228, 15464, 224, 896, 224, 2248, 228, 752, 232, 2784, 224, 1300, 280, 1240, 232, 1428, 224, 1840, 228, 14268, 228, 884, 224, 2252, 224, 1840, 232, 748, 224, 1028, 224, 2112, 232, 744, 228, 752, 228};
    private static final int airOn[] = {796, 1340, 800, 1344, 796, 1340, 800, 1340, 796, 1344, 800, 296, 800, 1340, 800, 296, 796, 1344, 796, 1344, 796, 1344, 800, 1344, 764, 1372, 800, 292, 800, 300, 768, 324, 800, 1344, 796, 292, 804, 296, 800, 296, 800, 296, 800, 296, 800, 296, 800, 296, 800, 296, 804, 1340, 796, 1344, 800, 296, 796, 296, 800, 1344, 800, 292, 800, 296, 800, 296, 800, 296, 800, 296, 800, 300, 796, 296, 800, 296, 800, 296, 800, 296, 800, 296, 800, 296, 804, 292, 800, 296, 800, 300, 796, 296, 800, 296, 804, 296, 796, 296, 800, 300, 768, 324, 800, 296, 804, 296, 796, 296, 800, 296, 800, 300, 796, 300, 764, 332, 796, 300, 796, 300, 792, 300, 800, 300, 796, 300, 796, 300, 796, 304, 792, 300, 796, 296, 800, 304, 792, 300, 800, 296, 800, 296, 796, 300, 800, 296, 800, 296, 800, 296, 796, 300, 796, 300, 796, 436, 792, 300, 796, 296, 800, 296, 800, 296, 800, 300, 796, 296, 800, 300, 796, 300, 796, 300, 796, 300, 796, 300, 796, 300, 796, 300, 796, 300, 796, 300, 796, 300, 796, 300, 796, 304, 792, 296, 804, 296, 796, 300, 796, 300, 800, 296, 796, 300, 800, 296, 796, 300, 796, 300, 800, 296, 800, 296, 800, 296, 796, 300, 800, 296, 768, 328, 800, 296, 796, 300, 800, 296, 844, 332, 800, 296, 800, 296, 800, 296, 800, 300, 764, 328, 800, 1344, 792, 300, 796, 1348, 792, 1348, 792, 296, 800, 1348, 768, 324, 796, 276, 736};
    private static final int airOff[] = {792, 1344, 796, 1368, 776, 1340, 796, 1368, 772, 1368, 772, 296, 800, 1348, 760, 328, 796, 300, 800, 296, 796, 300, 800, 296, 800, 1344, 796, 296, 796, 300, 800, 296, 796, 1372, 768, 300, 800, 296, 796, 300, 780, 320, 776, 316, 780, 316, 796, 300, 796, 300, 796, 1348, 776, 1364, 792, 304, 792, 300, 800, 1344, 796, 300, 796, 300, 764, 328, 800, 296, 796, 300, 800, 296, 796, 300, 796, 304, 792, 304, 796, 296, 796, 300, 800, 300, 796, 296, 796, 300, 800, 296, 1000, 412, 796, 300, 796, 300, 796, 300, 796, 300, 796, 300, 796, 300, 796, 300, 768, 328, 796, 300, 796, 300, 796, 300, 796, 300, 796, 300, 796, 300, 796, 300, 796, 300, 800, 296, 792, 308, 792, 300, 796, 300, 796, 300, 796, 300, 796, 300, 900, 304, 764, 328, 796, 300, 796, 304, 792, 304, 792, 300, 796, 300, 796, 304, 792, 300, 800, 300, 796, 296, 800, 300, 792, 304, 792, 304, 796, 300, 796, 300, 796, 300, 792, 304, 764, 332, 792, 304, 796, 300, 792, 300, 800, 300, 792, 304, 796, 300, 796, 300, 796, 300, 796, 300, 792, 304, 796, 300, 796, 300, 796, 300, 796, 300, 796, 304, 792, 304, 764, 328, 796, 432, 796, 300, 792, 300, 796, 304, 796, 304, 788, 304, 796, 300, 796, 300, 792, 304, 796, 300, 796, 300, 796, 300, 796, 300, 796, 300, 796, 300, 796, 300, 792, 1352, 792, 1348, 792, 1372, 768, 1344, 796, 300, 868, 304, 796, 280, 732};
    private static final int warm25[] = {788, 1348, 792, 1348, 792, 1348, 792, 1348, 792, 1352, 792, 296, 800, 1344, 792, 300, 796, 1348, 792, 1348, 792, 1348, 792, 1348, 792, 1344, 796, 300, 796, 300, 792, 300, 796, 1352, 792, 296, 796, 300, 800, 300, 796, 428, 768, 328, 796, 300, 796, 304, 792, 1348, 788, 304, 796, 1348, 792, 304, 792, 300, 796, 1348, 792, 300, 796, 300, 796, 300, 796, 304, 792, 300, 796, 300, 796, 300, 796, 300, 796, 300, 796, 300, 796, 300, 796, 300, 820, 304, 796, 300, 796, 300, 796, 300, 796, 300, 792, 300, 796, 304, 796, 300, 796, 300, 792, 304, 792, 304, 792, 304, 796, 300, 764, 332, 792, 308, 792, 300, 796, 300, 796, 300, 796, 300, 796, 304, 792, 300, 796, 304, 764, 328, 796, 300, 796, 300, 796, 300, 796, 300, 796, 300, 796, 300, 796, 300, 796, 300, 792, 304, 792, 304, 796, 300, 796, 300, 796, 300, 796, 428, 800, 300, 796, 300, 792, 304, 796, 300, 796, 300, 796, 300, 796, 300, 796, 304, 792, 304, 760, 332, 796, 300, 796, 300, 796, 300, 796, 300, 796, 300, 796, 300, 796, 300, 792, 304, 768, 328, 796, 300, 796, 300, 796, 300, 800, 296, 796, 300, 800, 296, 796, 300, 796, 300, 796, 300, 796, 300, 796, 300, 796, 300, 800, 296, 796, 300, 796, 300, 800, 300, 764, 328, 876, 300, 796, 300, 796, 300, 796, 300, 768, 328, 796, 300, 796, 300, 796, 1368, 768, 1352, 788, 300, 796, 1352, 792, 296, 800, 276, 732};
    private static final int warm26[] = {792, 1344, 796, 1344, 796, 1344, 796, 1344, 792, 1352, 788, 300, 796, 1348, 792, 300, 796, 1348, 792, 1348, 792, 1348, 792, 1348, 792, 1348, 792, 300, 796, 300, 796, 300, 796, 1348, 792, 300, 796, 296, 800, 300, 792, 432, 768, 332, 792, 300, 796, 300, 796, 300, 796, 1348, 796, 1344, 792, 304, 796, 296, 800, 1344, 792, 300, 796, 300, 796, 300, 800, 300, 792, 300, 796, 300, 796, 300, 796, 300, 796, 300, 796, 300, 796, 300, 796, 304, 820, 300, 796, 300, 792, 308, 792, 300, 796, 300, 796, 300, 796, 300, 796, 300, 796, 300, 796, 300, 792, 304, 792, 304, 796, 304, 764, 328, 792, 308, 788, 304, 796, 300, 796, 300, 796, 300, 796, 304, 792, 300, 796, 304, 764, 328, 796, 300, 796, 300, 796, 300, 796, 300, 800, 296, 796, 300, 800, 296, 796, 300, 796, 300, 796, 300, 796, 300, 796, 300, 796, 300, 800, 428, 796, 300, 796, 300, 796, 300, 796, 300, 796, 300, 796, 300, 796, 300, 796, 304, 792, 300, 768, 328, 796, 300, 796, 300, 796, 300, 796, 300, 800, 296, 796, 300, 796, 300, 796, 304, 764, 328, 796, 300, 800, 296, 796, 300, 800, 296, 800, 296, 800, 296, 800, 296, 796, 300, 800, 296, 796, 300, 800, 296, 800, 296, 796, 300, 796, 300, 800, 296, 796, 304, 764, 328, 876, 300, 796, 300, 796, 300, 800, 300, 764, 328, 796, 1344, 796, 300, 800, 1344, 788, 1352, 792, 300, 796, 1348, 792, 300, 796, 280, 728};
    private static final int warm27[] = {792, 1344, 796, 1344, 796, 1344, 796, 1340, 800, 1340, 800, 292, 804, 1344, 796, 296, 800, 1340, 800, 1344, 792, 1344, 800, 1340, 800, 1340, 800, 292, 804, 296, 796, 296, 800, 1348, 792, 296, 800, 296, 800, 296, 804, 424, 768, 328, 796, 300, 800, 296, 796, 1348, 796, 1344, 796, 1344, 796, 292, 804, 296, 796, 1348, 796, 296, 796, 300, 796, 300, 800, 296, 800, 296, 796, 300, 796, 300, 796, 300, 768, 328, 796, 300, 796, 300, 872, 304, 800, 296, 768, 328, 796, 296, 800, 296, 800, 300, 796, 296, 800, 296, 800, 300, 796, 296, 800, 300, 796, 300, 796, 300, 796, 300, 796, 296, 800, 300, 796, 300, 796, 300, 796, 296, 800, 300, 796, 300, 796, 300, 796, 300, 796, 300, 796, 300, 796, 300, 800, 296, 796, 300, 796, 300, 796, 304, 792, 300, 768, 328, 796, 300, 796, 300, 796, 300, 796, 300, 796, 432, 796, 296, 800, 300, 796, 300, 796, 300, 796, 300, 800, 296, 796, 300, 796, 300, 796, 300, 796, 300, 796, 296, 800, 296, 800, 300, 796, 296, 800, 300, 796, 300, 796, 300, 796, 300, 796, 300, 796, 300, 796, 300, 796, 300, 800, 296, 796, 300, 796, 300, 796, 300, 796, 300, 768, 328, 796, 300, 800, 296, 796, 300, 800, 296, 800, 296, 796, 304, 796, 296, 800, 296, 876, 296, 800, 296, 800, 304, 796, 296, 796, 300, 796, 296, 800, 300, 796, 1348, 792, 1348, 792, 1348, 768, 324, 796, 1344, 796, 300, 768, 304, 684};
    private static final int warm28[] = {792, 1344, 776, 1364, 796, 1344, 796, 1344, 792, 1348, 792, 300, 796, 1348, 792, 300, 796, 1348, 792, 1348, 792, 1348, 788, 1352, 792, 1348, 792, 300, 796, 300, 792, 304, 796, 1348, 792, 300, 796, 300, 792, 300, 796, 436, 760, 336, 792, 300, 796, 304, 792, 300, 796, 300, 796, 300, 796, 1348, 796, 296, 796, 1352, 788, 300, 796, 300, 796, 300, 796, 300, 796, 300, 796, 300, 796, 304, 792, 304, 764, 332, 792, 300, 796, 300, 800, 296, 796, 300, 796, 304, 792, 300, 800, 296, 800, 296, 800, 300, 796, 296, 796, 304, 792, 300, 796, 304, 792, 304, 792, 304, 792, 304, 764, 332, 792, 304, 792, 304, 764, 332, 792, 304, 772, 324, 776, 320, 792, 300, 800, 300, 796, 300, 796, 300, 796, 300, 792, 304, 792, 308, 792, 300, 796, 300, 792, 304, 796, 300, 796, 300, 796, 300, 796, 300, 792, 304, 796, 300, 792, 304, 796, 300, 792, 304, 792, 304, 792, 304, 796, 300, 796, 300, 796, 304, 792, 300, 792, 304, 796, 300, 796, 300, 792, 304, 796, 300, 796, 300, 796, 300, 796, 304, 792, 304, 764, 328, 796, 300, 796, 300, 796, 300, 796, 300, 796, 300, 796, 300, 796, 300, 872, 304, 796, 300, 792, 304, 796, 304, 792, 300, 796, 300, 796, 300, 796, 300, 796, 300, 796, 300, 796, 300, 768, 328, 796, 300, 792, 308, 760, 332, 796, 300, 796, 1348, 792, 1344, 796, 1348, 792, 1344, 796, 428, 768, 1376, 792, 300, 796, 280, 732};
    @SuppressLint("InlinedApi")
    public void initIR() {
        mCIR = (ConsumerIrManager) getSystemService(Context.CONSUMER_IR_SERVICE);
        if (!mCIR.hasIrEmitter()) {
            Log.e(TAG, "未找到紅外發射器！");
        } else {
            Log.e(TAG, "找到紅外發射器！");
        }
    }

    @SuppressLint("InlinedApi")
    public void tvPowerClick(View v) {
        vibratorIt();
        if (mCIR != null) {
            if (mCIR.hasIrEmitter()) {
                mCIR.transmit(38400, keyPower);
            }
        }
    }

    @SuppressLint("InlinedApi")
    public void key0Click(View v) {
        vibratorIt();
        if (mCIR != null) {
            if (mCIR.hasIrEmitter()) {
                mCIR.transmit(38400, key0);
            }
        }
    }

    @SuppressLint("InlinedApi")
    public void key1Click(View v) {
        vibratorIt();
        if (mCIR != null) {
            if (mCIR.hasIrEmitter()) {
                mCIR.transmit(38400, key1);
            }
        }
    }

    @SuppressLint("InlinedApi")
    public void key2Click(View v) {
        vibratorIt();
        if (mCIR != null) {
            if (mCIR.hasIrEmitter()) {
                mCIR.transmit(38400, key2);
            }
        }
    }

    @SuppressLint("InlinedApi")
    public void key3Click(View v) {
        vibratorIt();
        if (mCIR != null) {
            if (mCIR.hasIrEmitter()) {
                mCIR.transmit(38400, key3);
            }
        }
    }

    @SuppressLint("InlinedApi")
    public void key4Click(View v) {
        vibratorIt();
        if (mCIR != null) {
            if (mCIR.hasIrEmitter()) {
                mCIR.transmit(38400, key4);
            }
        }
    }

    @SuppressLint("InlinedApi")
    public void key5Click(View v) {
        vibratorIt();
        if (mCIR != null) {
            if (mCIR.hasIrEmitter()) {
                mCIR.transmit(38400, key5);
            }
        }
    }

    @SuppressLint("InlinedApi")
    public void key6Click(View v) {
        vibratorIt();
        if (mCIR != null) {
            if (mCIR.hasIrEmitter()) {
                mCIR.transmit(38400, key6);
            }
        }
    }

    @SuppressLint("InlinedApi")
    public void key7Click(View v) {
        vibratorIt();
        if (mCIR != null) {
            if (mCIR.hasIrEmitter()) {
                mCIR.transmit(38400, key7);
            }
        }
    }

    @SuppressLint("InlinedApi")
    public void key8Click(View v) {
        vibratorIt();
        if (mCIR != null) {
            if (mCIR.hasIrEmitter()) {
                mCIR.transmit(38400, key8);
            }
        }
    }

    @SuppressLint("InlinedApi")
    public void key9Click(View v) {
        vibratorIt();
        if (mCIR != null) {
            if (mCIR.hasIrEmitter()) {
                mCIR.transmit(38400, key9);
            }
        }
    }

    @SuppressLint("InlinedApi")
    public void channelUpClick(View v) {
        vibratorIt();
        if (mCIR != null) {
            if (mCIR.hasIrEmitter()) {
                mCIR.transmit(38400, keyChannelUp);
            }
        }
    }

    @SuppressLint("InlinedApi")
    public void channelDownClick(View v) {
        vibratorIt();
        if (mCIR != null) {
            if (mCIR.hasIrEmitter()) {
                mCIR.transmit(38400, keyChannelDown);
            }
        }
    }

    @SuppressLint("InlinedApi")
    public void volumnUpClick(View v) {
        vibratorIt();
        if (mCIR != null) {
            if (mCIR.hasIrEmitter()) {
                mCIR.transmit(38400, keyVolumnUp);
            }
        }
    }

    @SuppressLint("InlinedApi")
    public void volumnDownClick(View v) {
        vibratorIt();
        if (mCIR != null) {
            if (mCIR.hasIrEmitter()) {
                mCIR.transmit(38400, keyVolumnDown);
            }
        }
    }


    @SuppressLint("InlinedApi")
    public void air25Click(View v) {
        vibratorIt();
        if (mCIR != null) {
            if (mCIR.hasIrEmitter()) {
                //mCIR.transmit(38400, airOn);
                mCIR.transmit(38400, warm25);
                Log.e(TAG, "warm25!!!");
            }
        }
    }

    @SuppressLint("InlinedApi")
    public void air26Click(View v) {
        vibratorIt();
        if (mCIR != null) {
            if (mCIR.hasIrEmitter()) {
                //mCIR.transmit(38400, airOn);
                mCIR.transmit(38400, warm26);
                Log.e(TAG, "warm26!!!");
            }
        }
    }

    @SuppressLint("InlinedApi")
    public void air27Click(View v) {
        vibratorIt();
        if (mCIR != null) {
            if (mCIR.hasIrEmitter()) {
                //mCIR.transmit(38400, airOn);
                mCIR.transmit(38400, warm27);
                Log.e(TAG, "warm27!!!");
            }
        }
    }

    @SuppressLint("InlinedApi")
    public void air28Click(View v) {
        vibratorIt();
        if (mCIR != null) {
            if (mCIR.hasIrEmitter()) {
                //mCIR.transmit(38400, airOn);
                mCIR.transmit(38400, warm28);
                Log.e(TAG, "warm28!!!");
            }
        }
    }

    @SuppressLint("InlinedApi")
    public void airOffClick(View v) {
        vibratorIt();
        if (mCIR != null) {
            if (mCIR.hasIrEmitter()) {
                mCIR.transmit(38400, airOff);
                Log.e(TAG, "AirOff!!!");
            }
        }
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            boolean flag = false;
            while (true) {
                Date currentTime = Calendar.getInstance().getTime();
                if (currentTime.getHours() == 00 && currentTime.getMinutes() == 01 && flag == false) {
                    flag = true;
                    try {
                        myConnect();
                        lightOffClick(null);
                        Thread.sleep(5000);

                        for (int i = 0; i < 2; i++) {
                            lightOnClick(null);
                            Thread.sleep(1000);
                            lightOffClick(null);
                            Thread.sleep(1000);
                        }
                        lightOnClick(null);

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                if (currentTime.getHours() == 00 && currentTime.getMinutes() == 02) {
                    flag = false;
                }

                if (currentTime.getHours() == 04 && currentTime.getMinutes() == 30 && flag == false) {
                    flag = true;
                    try {
                        myConnect();
                        lightOffClick(null);
                        Thread.sleep(5000);
                        lightOnClick(null);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
                if (currentTime.getHours() == 04 && currentTime.getMinutes() == 31) {
                    flag = false;
                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };
}