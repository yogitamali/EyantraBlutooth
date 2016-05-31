/**
 *
 * Project Name: Fire Bird V operation using Android app
 * Author List: Archie Mittal, Kanupriya Sharma
 * Filename: MainActivity.java
 * Functions: onCreate(Bundle), onStart(), onStop(), onServiceConnected(ComponentName, IBinder), onServiceDisconnected(ComponentName), onActivityResult(int, int, Intent)
 *            connectDevice(Intent), setStatus(String), onCreateOptionsMenu(Menu), onOptionsItemSelected(MenuItem), customMode(View), gestureMode(View),
 *            sensor(View), velocity(View)
 * Global Variables: TAG, REQUEST_CONNECT_DEVICE, REQUEST_ENABLE_BT, mAdapter, mBtConnection, mBound
 *
 */

package com.example.erts_lab.bluefire2;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import com.example.erts_lab.bluefire2.BtConnection.LocalBinder;

/*
 *
 * Class Name: MainActivity
 * Logic: It is teh starting activity which gets displayed when the application is launched. It contains two fragment. One fragment is static and have the four
 *        options available to the user and the other fragment is dynamic, i.e. it gets replaced according to the option selected by the user
 * Example Call: new MainActivity()
 *
 */
public class MainActivity extends AppCompatActivity
{

    //for debugging
    final String TAG = "BLUEFIRE";

    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;

    //Bluetooth related objects
    BluetoothAdapter mAdapter = null;
    BtConnection mBtConnection;

    boolean mBound = false;

    /**
     *
     * Function Name: onCreate
     * Input: savedInstanceState --> If the activity is being re-initialized after previously being shut down then this Bundle contains the data it most recently
     *        supplied in onSaveInstanceState(Bundle). Note: Otherwise it is null.
     * Output: sets up the MainActivity
     * Logic: Called when the activity is starting. This is where most initialization should go: calling setContentView(int) to inflate the activity's UI,
     *        using findViewById(int) to programmatically interact with widgets in the UI
     * Example Call: Called automatically when the activity is created
     *
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //gets the default bluetooth adapter
        mAdapter = BluetoothAdapter.getDefaultAdapter();

        //Check if the device supports bluetooth
        if(mAdapter == null)
        {
            Toast.makeText(getApplicationContext(),"Bluetooth not supported. Leaving BlueFire",Toast.LENGTH_LONG).show();
            finish();
        }
        if (findViewById(R.id.fragment_container) != null)
        {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null)
            {
                return;
            }

            // Velocity fragment is teh default fragment which is used to set the velocity of the robot after a successful connection has been established
            //with the robot
            VelocityFragment mVelocityFragment = new VelocityFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, mVelocityFragment, "VelocityFragment");
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }

    /**
     *
     * Function Name: onStart
     * Input: None
     * Output: If bluetooth has not been enabled then it enables the bluetooth and binds the activity with the local bluetooth service
     * Logic: Calls the isEnabled() method of BluetoothAdapter class to check whether bluetooth is enabled or not. If bluetooth is not enabled then it sends a
     *        request to enable the bluetooth. After bluetooth has been enabled then binds to the local service by calling its bindService method.
     * Example Call: Called automatically when the fragment becomes visible
     *
     */
    @Override
    public void onStart()
    {
        super.onStart();

        // If BT is not on, request that it be enabled.
        if (!mAdapter.isEnabled())
        {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        }
        // Bind to LocalService
        Intent intent = new Intent(this, BtConnection.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    /**
     *
     * Function Name: onStop
     * Input: None
     * Output: Unbinds this fragment from the bluetooth service
     * Logic: calls the method unbindService(BtConnection) to unbind from the bluetooth service
     * Example Call: called automatically when the fragment is not visible
     *
     */
    @Override
    protected void onStop()
    {
        super.onStop();
        // Unbind from the service
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
    }

    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection mConnection = new ServiceConnection()
    {

        /*
         *
         * Function Name: onServiceConnected
         * Input: name -->	The concrete component name of the service that has been connected.
         *        service --> The IBinder of the Service's communication channel, which you can now make calls on.
         * Output: bounds to the local service
         * Logic:  bound to LocalService, cast the IBinder and get LocalService instance by calling getservice method
         * Example Call: called automatically when a connection to the Service has been established, with the IBinder of the communication channel to the Service.
         *
         */
        @Override
        public void onServiceConnected(ComponentName className, IBinder service)
        {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            LocalBinder binder = (LocalBinder) service;
            mBtConnection = binder.getService();
            mBound = true;
        }

        /**
         *
         * Function Name: onServiceDisconnected
         * Input: name --> The concrete component name of the service whose connection has been lost.
         * Output: sets the variable mBound to false to indicate that fragment is not connected to the service
         * Logic: Called when a connection to the Service has been lost. This typically happens when the process hosting the service has crashed or been killed.
         *        This does not remove the ServiceConnection itself -- this binding to the service will remain active, and you will receive a call to
         *        onServiceConnected(ComponentName, IBinder) when the Service is next running.
         * Example Call: called automatically
         *
         */
        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    /**
     *
     * Function Name: onActivityResult
     * Input: requestCode --> The integer request code originally supplied to startActivityForResult(), allowing you to identify who this result came from.
     *        resultCode --> The integer result code returned by the child activity through its setResult().
     *        data --> An Intent, which can return result data to the caller (various data can be attached to Intent "extras").
     * Output: Depending upon the requestCode it either enables the bluetooth of the device or calls a method connect(data) to make a bluetooth connection
     *         with the selected device
     * Logic: called when an activity you launched exits, giving you the requestCode you started it with, the resultCode it returned, and any additional data from it.
     *        The resultCode will be RESULT_CANCELED if the activity explicitly returned that, didn't return any result, or crashed during its operation.
     *        You will receive this call immediately before onResume() when your activity is re-starting.
     * Example Call: called automatically after startActivityForResult(Intent, int)
     *
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        switch (requestCode)
        {
            case REQUEST_CONNECT_DEVICE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK)
                {
                    connectDevice(data);
                }
                break;
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_CANCELED)
                {
                    // User did not enable Bluetooth or an error occurred
                    Toast.makeText(getApplicationContext(), "Bluetooth not enabled. Leaving BlueFire", Toast.LENGTH_SHORT).show();
                    finish();
                }
        }
    }


    /**
     *
     * Function Name: connectDevice
     * Input: data --> An Intent with DeviceListActivity#EXTRA_DEVICE_ADDRESS extra.
     * Output: makes a bluetooth connection with the selected device
     * Logic: gets the mac address of the bluetooth device from the intent passed as parameter, then it gets the BluetoothDevice object by calling the method getRemoteDevice(address)
     *        and finally sends that object to the BtConnection to make connection between the device and the robot.
     * Example Call: connectDevice(Intent data)
     *
     */
    private void connectDevice(Intent data) {

        // Get the device MAC address
        String address = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
        // Get the BluetoothDevice object
        BluetoothDevice device = mAdapter.getRemoteDevice(address);
        Toast.makeText(this, "Connecting...", Toast.LENGTH_LONG).show();
        mBtConnection = new BtConnection(this, device);
        if (mBound) {
            //mBtConnection.connect();
            try {
                Log.d(TAG, "Connection Started...");

                /** Bluetooth connect function returns true if connection is successful, else false. */
                if (!mBtConnection.connect()) {
                    Toast.makeText(this, " No connection established ", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    Toast.makeText(this, " Connection established ", Toast.LENGTH_SHORT).show();
                    setStatus("Connected to "+device.getName());
                }
                Log.d(TAG, "Connection Successful");
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "Connection Failed");
            }
        }
    }

    /**
     *
     * Function Name: setStatus
     * Input: status --> String which is set on teh action bar
     * Output: sets the action bar status
     * Logic: calls the method setSubtitle to set the status
     * Example Call: setStatus("Done..")
     *
     */
    public void setStatus(String status)
    {
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null)
            actionBar.setSubtitle(status);

    }

    /**
     *
     * Function Name: onCreateOptionsMenu
     * Input: menu --> The options menu in which you place your items.
     * Output: return true for the menu to be displayed; if you return false it will not be shown.
     * Logic: Initialize the contents of the Activity's standard options menu. You should place your menu items in to menu.
     * Example Call: This is only called once, the first time the options menu is displayed.
     *
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     *
     * Function Name: onOptionsItemSelected
     * Input: menu --> The options menu in which you place your items.
     * Output: boolean Return false to allow normal menu processing to proceed, true to consume it here.
     * Logic: This hook is called whenever an item in your options menu is selected. The default implementation simply returns false to have the normal
     *        processing happen (calling the item's Runnable or sending a message to its Handler as appropriate). You can use this method for any items for which you would like to do processing without those other facilities.
     * Example Call: Derived classes should call through to the base class for it to perform the default menu handling.
     *
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id)
        {
            case R.id.action_connect:
                //Launch the DeviceListActivity to see devices
                Intent serverIntent = new Intent(this, DeviceListActivity.class);
                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
                return true;

            case R.id.action_disconnect:
                //Implementation of disconnect

                break;
            case R.id.action_help:
                //Implementation of help option
                break;
        }


        return false;
    }

    /**
     *
     * Function Name: customMode
     * Input: v --> The view that was clicked.
     * Output: Loads the MotionControlFragment in the frame layout used for displaying fragments
     * Logic: gets the fragmentManager and then replaces the fragment by calling replace(Layout, Fragment) method
     * Example Call: Called when customMode button has been clicked.
     *
     */
    public void customMode(View v)
    {
        MotionControlFragment mCustomFragment = new MotionControlFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, mCustomFragment, "CustomFragment");
        transaction.addToBackStack(null);
        transaction.commit();
    }

    /**
     *
     * Function Name: gestureMode
     * Input: v --> The view that was clicked.
     * Output: Loads the GestureControlFragment in the frame layout used for displaying fragments
     * Logic: gets the fragmentManager and then replaces the fragment by calling replace(Layout, Fragment) method
     * Example Call: Called when GestureMode button has been clicked.
     *
     */
    public void gestureMode(View v)
    {

        GestureControlFragment mGestureFragment = new GestureControlFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, mGestureFragment, "GestureFragment");
        transaction.addToBackStack(null);
        transaction.commit();
    }

    /**
     *
     * Function Name: sensor
     * Input: v --> The view that was clicked.
     * Output: Loads the SensorReadingFragment in the frame layout used for displaying fragments
     * Logic: gets the fragmentManager and then replaces the fragment by calling replace(Layout, Fragment) method
     * Example Call: Called when sensor button has been clicked.
     *
     */
    public void sensor(View v)
    {
        SensorReadingFragment mSensorReadingFragment = new SensorReadingFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, mSensorReadingFragment, "SensorFragment");
        transaction.addToBackStack(null);
        transaction.commit();
    }

    /**
     *
     * Function Name: velocity
     * Input: v --> The view that was clicked.
     * Output: Loads the VelocityFragment in the frame layout used for displaying fragments
     * Logic: gets the fragmentManager and then replaces the fragment by calling replace(Layout, Fragment) method
     * Example Call: Called when velocity button has been clicked.
     *
     */
    public void velocity(View v)
    {
        VelocityFragment mVelocityFragment = new VelocityFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, mVelocityFragment, "VelocityFragment");
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
