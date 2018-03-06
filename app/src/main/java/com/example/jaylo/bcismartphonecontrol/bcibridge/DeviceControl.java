package com.example.jaylo.bcismartphonecontrol.bcibridge;

/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
* This file is a modified version of https://github.com/googlesamples/android-BluetoothLeGatt
* This Activity is started by the DeviceScanActivity and is passed a device name and address to start with
* Clicking on the listed device, opens up a page which allows connection and for scanning of GATT Services available for the device
* */
//package com.example.android.openbciBLE;

        import android.app.Activity;
        import android.bluetooth.BluetoothGattCharacteristic;
        import android.bluetooth.BluetoothGattService;
        import android.content.BroadcastReceiver;
        import android.content.ComponentName;
        import android.content.Context;
        import android.content.Intent;
        import android.content.IntentFilter;
        import android.content.ServiceConnection;
        import android.os.Bundle;
        import android.os.Environment;
        import android.os.IBinder;
        import android.util.Log;
        import android.view.View;
        import android.widget.Button;
        import android.widget.TextView;
        import android.widget.Toast;

        import com.example.jaylo.bcismartphonecontrol.R;

        import java.io.BufferedWriter;
        import java.io.File;
        import java.io.FileWriter;
        import java.io.IOException;
        import java.util.ArrayList;
        import java.util.HashMap;
        import java.util.List;
        import java.util.Objects;

/**
 * For a given BLE device, this Activity provides the user interface to connect, display data,
 * and display GATT services and characteristics supported by the device.  The Activity
 * communicates with {@code BluetoothLeService}, which in turn interacts with the
 * Bluetooth LE API.
 */
public class DeviceControl extends Activity {
    private final static String TAG = "OpenBCIBLE/"+ DeviceControl.class.getSimpleName();

    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";
    private static final byte[] mCommands = {'b','s'};
    private static int mCommandIdx = 0;
    private TextView mConnectionState;
    private String mDeviceName;
    private String mDeviceAddress;
    private BluetoothLeService mBluetoothLeService;
    private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics = new ArrayList<>();
    private boolean mConnected = false;
    private BluetoothGattCharacteristic mNotifyOnRead;
    private BluetoothGattCharacteristic mGanglionSend;

    private boolean mIsDeviceGanglion= false;;

    private ArrayList<HashMap<String, String>> gattServiceData = new ArrayList<HashMap<String, String>>();
    private  ArrayList<ArrayList<HashMap<String, String>>> gattCharacteristicData= new ArrayList<ArrayList<HashMap<String, String>>>();

    private final String LIST_NAME = "NAME";
    private final String LIST_UUID = "UUID";

    private Button bStream;

    //CSV File  will get saved in Documents/test/filename
    private final File path = getStorageDir("test");
    private final String fileName= "test3.csv";



    // Code to manage Service lifecycle.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.i(TAG,"componentName: "+ componentName);
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            // Automatically connects to the device upon successful start-up initialization.
            Log.v(TAG,"Trying to connect to GATTServer on: "+mDeviceName+" Address: "+mDeviceAddress );
            mBluetoothLeService.connect(mDeviceAddress);
            mCommandIdx = 0;

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.v(TAG,"Disconnecting from" );
            mBluetoothLeService = null;
        }
    };


    // Handles various events fired by the Service.
    // ACTION_GATT_CONNECTED: connected to a GATT server.
    // ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
    // ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
    // ACTION_DATA_AVAILABLE: received data from the device.  This can be a result of read
    //                        or notification operations.
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                Log.v(TAG,"GattServer Connected");
                mConnected = true;
                updateConnectionState(R.string.connected);

            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                Log.v(TAG,"GattServer Disconnected");
                mConnected = false;
                updateConnectionState(R.string.disconnected);

            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                Log.v(TAG,"GattServer Services Discovered");
                // Show all the supported services and characteristics on the user interface.
                displayGattServices(mBluetoothLeService.getSupportedGattServices());
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                //displayData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
                String dataType = intent.getStringExtra(BluetoothLeService.DATA_TYPE);
                if (Objects.equals(dataType, "RAW")) {
                    int[] samples=intent.getIntArrayExtra(BluetoothLeService.SAMPLE_ID);
                    int[] intentData1=intent.getIntArrayExtra(BluetoothLeService.FULL_DATA_1);
                    writetoCSV( path, fileName,
                            samples[0] + "," + intentData1[0] + "," + intentData1[1] + "," + intentData1[2] + "," + intentData1[3] +"\n");
                } else if (Objects.equals(dataType, "19BIT")){
                    int[] samples=intent.getIntArrayExtra(BluetoothLeService.SAMPLE_ID);
                    int[] intentData1=intent.getIntArrayExtra(BluetoothLeService.FULL_DATA_1);
                    int[] intentData2=intent.getIntArrayExtra(BluetoothLeService.FULL_DATA_2);
                    writetoCSV( path, fileName,
                            samples[0] + "," + intentData1[0] + "," + intentData1[1] + "," + intentData1[2] + "," + intentData1[3] +"\n" +
                                    samples[1] + "," + intentData2[0] + "," + intentData2[1] + "," + intentData2[2] + "," + intentData2[3] +"\n");
                } else{
                    //handle this
                }

            }
        }
    };


    private boolean setCharacteristicNotification(BluetoothGattCharacteristic currentNotify, BluetoothGattCharacteristic newNotify, String toastMsg){
        if(currentNotify==null){//none registered previously
            mBluetoothLeService.setCharacteristicNotification(newNotify, true);
        }
        else {//something was registered previously
            if (!currentNotify.getUuid().equals(newNotify.getUuid())) {//we are subscribed to another characteristic?
                mBluetoothLeService.setCharacteristicNotification(currentNotify, false);//unsubscribe
                mBluetoothLeService.setCharacteristicNotification(newNotify, true); //subscribe to Receive
            }
            else{
                //no change required
                return false;
            }
        }
        Toast.makeText(getApplicationContext(), "Notify: "+toastMsg, Toast.LENGTH_SHORT).show();
        return true;//indicates reassignment needed for mNotifyOnRead
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gatt_services_characteristics);

        //this activity was started by another with data stored in an intent, process it
        final Intent intent = getIntent();

        //get the device name and address
        mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
        mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);

        //set flags if CYTON or GANGLION is being used
        Log.v(TAG,"deviceName '"+mDeviceName+"'");
        if(mDeviceName!=null) {
            mIsDeviceGanglion = mDeviceName.toUpperCase().contains(SampleGattAttributes.DEVICE_NAME_GANGLION);
        }

        // Sets up UI references.
        mConnectionState = (TextView) findViewById(R.id.connection_state);

        bStream=findViewById(R.id.toggle_stream);
        bStream.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO access send characteristic here
                if(mConnected){
                    char cmd = (char) mCommands[mCommandIdx];
                    Log.v(TAG,"Sending Command : "+cmd);
                    mGanglionSend.setValue(new byte[]{(byte)cmd});
                    mBluetoothLeService.writeCharacteristic((mGanglionSend));
                    mCommandIdx = (mCommandIdx +1)% mCommands.length; //update for next run to toggle off
                    Toast.makeText(getApplicationContext(), "Sent: '"+cmd+"' to Ganglion", Toast.LENGTH_SHORT).show();

                }
            }
        });


        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);

        Log.v(TAG,"Creating Service to Handle all further BLE Interactions");
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (mBluetoothLeService != null) {

            Log.v(TAG,"Trying to connect to: "+mDeviceName+" Address: "+mDeviceAddress);
            final boolean result = mBluetoothLeService.connect(mDeviceAddress);
            Log.d(TAG, "Connect request result=" + result);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mGattUpdateReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
        mBluetoothLeService = null;
    }


    private void updateConnectionState(final int resourceId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mConnectionState.setText(resourceId);
            }
        });
    }


    // Demonstrates how to iterate through the supported GATT Services/Characteristics.
    // In this sample, we populate the data structure that is bound to the ExpandableListView
    // on the UI.
    private void displayGattServices(List<BluetoothGattService> gattServices) {
        if (gattServices == null) return;
        String uuid = null;
        String unknownServiceString = getResources().getString(R.string.unknown_service);
        String unknownCharaString = getResources().getString(R.string.unknown_characteristic);

        mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();

        // Loops through available GATT Services.
        for (BluetoothGattService gattService : gattServices) {
            Log.w(TAG,"Service Iterator:"+gattService.getUuid());

            if(mIsDeviceGanglion){////we only want the SIMBLEE SERVICE, rest, we junk...
                if(!SampleGattAttributes.UUID_GANGLION_SERVICE.equals(gattService.getUuid().toString())) continue;
            }


            //Add Service data to gattServiceData
            HashMap<String, String> currentServiceData = new HashMap<String, String>();
            uuid = gattService.getUuid().toString();
            currentServiceData.put(LIST_NAME, SampleGattAttributes.lookup(uuid, unknownServiceString));
            currentServiceData.put(LIST_UUID, uuid);
            gattServiceData.add(currentServiceData);

            ArrayList<HashMap<String, String>> gattCharacteristicGroupData = new ArrayList<HashMap<String, String>>();
            List<BluetoothGattCharacteristic> gattCharacteristics = gattService.getCharacteristics();
            ArrayList<BluetoothGattCharacteristic> charas = new ArrayList<BluetoothGattCharacteristic>();

            // Loops through available Characteristics.
            for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                charas.add(gattCharacteristic);
                HashMap<String, String> currentCharaData = new HashMap<String, String>();
                uuid = gattCharacteristic.getUuid().toString();
                currentCharaData.put(LIST_NAME, SampleGattAttributes.lookup(uuid, unknownCharaString));
                currentCharaData.put(LIST_UUID, uuid);
                gattCharacteristicGroupData.add(currentCharaData);

                //if this is the read attribute for Cyton/Ganglion, register for notify service
                if(SampleGattAttributes.UUID_GANGLION_RECEIVE.equals(uuid)){//the RECEIVE characteristic
                    Log.v(TAG,"Registering notify for: "+uuid);
                    //we set it to notify, if it isn't already on notify
                    if(mNotifyOnRead==null){
                        mBluetoothLeService.setCharacteristicNotification(gattCharacteristic, true);
                        mNotifyOnRead = gattCharacteristic;
                    }
                    else{
                        Log.v(TAG, "De-registering Notification for: "+mNotifyOnRead.getUuid().toString() +" first");
                        mBluetoothLeService.setCharacteristicNotification(mNotifyOnRead, false);
                        mBluetoothLeService.setCharacteristicNotification(gattCharacteristic, true);
                        mNotifyOnRead = gattCharacteristic;
                    }
                }

                if(SampleGattAttributes.UUID_GANGLION_SEND.equals(uuid)){//the RECEIVE characteristic
                    Log.v(TAG,"GANGLION SEND:  "+uuid);
                    mGanglionSend=gattCharacteristic;
                }
            }
            mGattCharacteristics.add(charas);
            gattCharacteristicData.add(gattCharacteristicGroupData);
        }


    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        Log.e("isExtStorageWritable", "Can´t write to Ext. Storage");
        return false;
    }

    public static void writetoCSV( File path, String Filename , String Data){

        if (isExternalStorageWritable()) {
            try {
                //Create a new file @ path/filename
                File file = new File(path, Filename);

                //1st Parameter = filepath, 2nd Paramter true=append
                FileWriter fw = new FileWriter(file, true);
                BufferedWriter out = new BufferedWriter(fw);
                out.write(Data);
                out.close();
                //toastie(context,"Entry Saved");
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        else{
            Log.e("WriteToCsv", "Cannot write to storage!");
        }
    }

    public static File getStorageDir(String folderName) {
        // Get the directory for the user's public documents directory.
        File documents = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        //path will be storage/sdcard/documents/foldername
        File path = new File(documents, folderName);
        Log.d("getStorageDir", path.toString());
        if (!path.mkdirs()) {
            Log.e("getStorageDir", "Directory not created");
        }
        return path;
    }
}
