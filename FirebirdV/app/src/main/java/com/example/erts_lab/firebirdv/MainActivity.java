package com.example.erts_lab.firebirdv;

import android.app.ActionBar;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import android.view.View.OnClickListener;


public class MainActivity extends AppCompatActivity implements OnClickListener{

    private static final String TAG = "SROBO";
    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_CONNECT_DEVICE = 2;
    private String mConnectedDeviceName = null;
    private BluetoothAdapter mBluetoothAdapter = null;
    private BtInterface mBtInterface = null;
    private ImageView forwardArrow, backArrow, rightArrow, leftArrow, stop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mBluetoothAdapter == null)
        {
            Toast.makeText(getApplicationContext(), "Bluetooth not supported", Toast.LENGTH_LONG).show();
            finish();
        }
        forwardArrow = (ImageView)findViewById(R.id.forward_arrow);
        forwardArrow.setOnClickListener(this);
        backArrow = (ImageView)findViewById(R.id.back_arrow);
        backArrow.setOnClickListener(this);
        rightArrow = (ImageView)findViewById(R.id.right_arrow);
        rightArrow.setOnClickListener(this);
        leftArrow = (ImageView)findViewById(R.id.left_arrow);
        leftArrow.setOnClickListener(this);
        stop = (ImageView)findViewById(R.id.stop);
        stop.setOnClickListener(this);
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        if(!mBluetoothAdapter.isEnabled())
        {
            Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetooth, REQUEST_ENABLE_BT);
        }
        else if (mBtInterface == null) {
            setUpInterface();
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        if (mBtInterface != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (mBtInterface.getState() == BtInterface.STATE_NONE) {
                // Start the Bluetooth services
                mBtInterface.start();
            }
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mBtInterface != null) {
            mBtInterface.stop();
        }
    }

    /**
     * Updates the status on the action bar.
     *
     * @param resId a string resource ID
     */
    private void setStatus(int resId)
    {

        if (null == null) {
            return;
        }
        final ActionBar actionBar = this.getActionBar();
        if (null == actionBar) {
            return;
        }
        actionBar.setSubtitle(resId);
    }

    /**
     * Updates the status on the action bar.
     *
     * @param subTitle status
     */
    private void setStatus(CharSequence subTitle)
    {

        if (this == null)
        {
            return;
        }
        final ActionBar actionBar = this.getActionBar();
        if (null == actionBar)
        {
            return;
        }
        actionBar.setSubtitle(subTitle);
    }

    private void setUpInterface()
    {
        // Initialize the BtInterface to perform bluetooth connections
        mBtInterface = new BtInterface(MainActivity.this, mHandler);

    }

    /**
     * Sends a message.
     *
     * @param message A string of text to send.
     */
    private void sendMessage(String message) {
        // Check that we're actually connected before trying anything
        if (mBtInterface.getState() != BtInterface.STATE_CONNECTED)
        {
            Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT).show();
            return;
        }

        // Check that there's actually something to send
        if (message.length() > 0)
        {
            // Get the message bytes and tell the BtInterface to write
            byte[] send = message.getBytes();
            mBtInterface.write(send);

           /* // Reset out string buffer to zero and clear the edit text field
            mOutStringBuffer.setLength(0);
            mOutEditText.setText(mOutStringBuffer);*/
        }
    }

    /**
     * The Handler that gets information back from the BtInterface
     */
    private final Handler mHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
                switch (msg.what)
                {
                case Constants.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BtInterface.STATE_CONNECTED:
                            setStatus(getString(R.string.title_connected_to, mConnectedDeviceName));
                            //mConversationArrayAdapter.clear();
                            break;
                        case BtInterface.STATE_CONNECTING:
                            setStatus(R.string.title_connecting);
                            break;
                        case BtInterface.STATE_LISTEN:
                        case BtInterface.STATE_NONE:
                            setStatus(R.string.title_not_connected);
                            break;
                    }
                    break;
                /*case Constants.MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    String writeMessage = new String(writeBuf);
                    mConversationArrayAdapter.add("Me:  " + writeMessage);
                    break;
                case Constants.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    mConversationArrayAdapter.add(mConnectedDeviceName + ":  " + readMessage);
                    break;*/
                case Constants.MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(Constants.DEVICE_NAME);
                    if (null != this) {
                        Toast.makeText(MainActivity.this, "Connected to "
                                + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    }
                    break;
                case Constants.MESSAGE_TOAST:
                    if (null != this) {
                        Toast.makeText(MainActivity.this, msg.getData().getString(Constants.TOAST),
                                Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    };

    //handles the clicks on various parts of the screen
    //all buttons launch a function from the BtInterface object
    @Override
    public void onClick(View v) {

        if(v == forwardArrow) {
            //addToLog("Move Forward");
            sendMessage("8");
        }
        else if(v == backArrow) {
            //addToLog("Move back");
            sendMessage("2");
        }
        else if(v == rightArrow) {
            //addToLog("Turn Right");
            sendMessage("6");
        }
        else if(v == leftArrow) {
            //addToLog("Turn left");
            sendMessage("4");
        }
        else if(v == stop) {
            //addToLog("Stopping");
            sendMessage("5");
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        switch(requestCode)
        {
            case REQUEST_ENABLE_BT:
                if(resultCode == Activity.RESULT_OK)
                    setUpInterface();
                else {
                    Log.d(TAG, "BT not enabled");
                    Toast.makeText(getApplicationContext(), "Bluetooth must be enabled", Toast.LENGTH_LONG).show();
                    finish();
                }
                break;
            case REQUEST_CONNECT_DEVICE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data);
                }
                break;
        }
    }

    private void connectDevice(Intent data) {
        // Get the device MAC address
        String address = data.getExtras()
                .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
        // Get the BluetoothDevice object
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        // Attempt to connect to the device
        mBtInterface.connect(device);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.connect_scan: {
                // Launch the DeviceListActivity to see devices and do scan
                Intent serverIntent = new Intent(this, DeviceListActivity.class);
                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
                return true;
            }

        }
        return false;
    }
}
