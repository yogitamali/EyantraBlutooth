/*
 *
 * Project Name: Fire Bird V operation using Android app
 * Author List: Archie Mittal, Kanupriya Sharma
 * Filename: SensorReadingFragment.java
 * Functions: SensorReadingFragment(), onStart(), onResume(), onPause(), Sharp_GP2D12_estimation (int), onStop(), onServiceConnected(ComponentName, IBinder),
 *            onServiceDisconnected(ComponentName), isBound(), onCreateView(LayoutInflater, ViewGroup,Bundle), onViewCreated(View,Bundle)
 * Global Variables: TAG, mBtConnection, mBound, readSensors, ir1, ir2, ir3, ir4, ir5, ir6, ir7, ir8, wlL, wlM, wlR, sharp1, sharp2, sharp3, sharp4, sharp5,
 *                   battVoltage
 *
 */

package com.example.erts_lab.bluefire2;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

/**
 *
 * Class Name: SensorReadingFragment
 * Logic: gets the values of different sensors and battery voltage from the inputstream, sent by the robot and displays them on the screen
 * Example Call: new SensorReadingFragment()
 *
 */
public class SensorReadingFragment extends Fragment
{

    //for debugging
    final String TAG = "BLUEFIRE";

    //to store object of BtConnection
    BtConnection mBtConnection;

    //stores whether activity is bound to service or not
    boolean mBound = false, readSensors = false;

    //TextViews for displaying values of sensors
    TextView ir1, ir2, ir3, ir4, ir5, ir6, ir7, ir8, wlL, wlM, wlR, sharp1, sharp2, sharp3, sharp4, sharp5, battVoltage;

    /**
     *
     * Function Name: SensorReadingFragment
     * Input: None
     * Output: None
     * Logic: Required empty public constructor
     * Example Call: called automatically when its class object is created
     *
     */
    public SensorReadingFragment() {
    }

    /**
     *
     * Function Name: onStart
     * Input: None
     * Output: binds the SensorReading fragment with the local bluetooth service
     * Logic: Calls the activity BtConnection and binds this activity with the BtConnection by calling bindService method
     * Example Call: Called automatically when the fragment becomes visible
     *
     */
    @Override
    public void onStart()
    {
        super.onStart();

        // Bind to LocalService
        Intent intent = new Intent(getActivity(), BtConnection.class);
        getActivity().getApplicationContext().bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    /**
     *
     * Function Name: onResumne
     * Input: None
     * Output: sets the screen orientation in Portrait mode and set the variable readSensors to true to start reading the value of sensors
     * Logic: calls the setRequestedOrientation(Orientation) method to set the screen orientation
     * Example Call: called automatically when the fragment becomes visible and user can interact with it
     *
     */
    @Override
    public void onResume()
    {
        super.onResume();
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        readSensors = true;
    }

    /**
     *
     * Function Name: onPause
     * Input: None
     * Output: sensor readings are stopped
     * Logic: sets the variable readSensors to false
     * Example Call: called automatically when the fragment goes in  inactive mode
     *
     */
    @Override
    public void onPause()
    {
        super.onPause();
        readSensors = false;
    }

    /**
     *
     * Function Name: Sharp_GP2D12_estimation
     * Input: analog value of sharp ir sensor
     * Output: distance of object from robot in unit mm
     * Logic: distance = (int)(10.00*(2799.6*(1.00/(Math.pow(value,1.1546)))))
     * Example Call: Sharp_GP2D12_estimation (int value)
     *
     */
    int Sharp_GP2D12_estimation (int value){
        float distance;
        int distanceInt;
        distance = (int)(10.00*(2799.6*(1.00/(Math.pow(value,1.1546)))));
        distanceInt = (int)distance;
        if(distanceInt>800)
        {
            distanceInt=800;
        }
        return distanceInt;

    }

    // Thread to read the value of sensors
    private class ReadThread implements Runnable
    {
        // stores the value read from the input stream
        int m;
        // delay between two sensor values
        long t= 60;
        public void run()
        {
            // gets the sensor values till the time fragment is visible and is connected to the bluetooth service
            while (isBound() && readSensors)
            {
                try
                {
                    Thread.sleep(t);
                    mBtConnection.sendData("h");
                    // reads the proximity sensor 1 value
                    m = mBtConnection.readData();
                    Log.d(TAG, "Sensors readTghread ir1" + m);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // This code will always run on the UI thread, therefore is safe to modify UI elements.
                            ir1.setText("" + m);
                            Log.d(TAG, "Sensors readTghread ir1 update");
                        }
                    });
                    Thread.sleep(t);
                    mBtConnection.sendData("i");
                    // reads the proximity sensor 2 value
                    m = mBtConnection.readData();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // This code will always run on the UI thread, therefore is safe to modify UI elements.
                            ir2.setText("" + m);
                        }
                    });
                    Thread.sleep(t);
                    mBtConnection.sendData("j");
                    // reads the proximity sensor 3 value
                    m = mBtConnection.readData();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // This code will always run on the UI thread, therefore is safe to modify UI elements.
                            ir3.setText("" + m);
                        }
                    });
                    Thread.sleep(t);
                    mBtConnection.sendData("k");
                    // reads the proximity sensor 4 value
                    m = mBtConnection.readData();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // This code will always run on the UI thread, therefore is safe to modify UI elements.
                            ir4.setText("" + m);
                        }
                    });
                    Thread.sleep(t);
                    mBtConnection.sendData("l");
                    // reads the proximity sensor 5 value
                    m = mBtConnection.readData();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // This code will always run on the UI thread, therefore is safe to modify UI elements.
                            ir5.setText("" + m);
                        }
                    });

                    Thread.sleep(t);
                    mBtConnection.sendData("m");
                    // reads the proximity sensor 6 value
                    m = mBtConnection.readData();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // This code will always run on the UI thread, therefore is safe to modify UI elements.
                            ir6.setText("" + m);
                        }
                    });
                    Thread.sleep(t);
                    mBtConnection.sendData("n");
                    // reads the proximity sensor 7 value
                    m = mBtConnection.readData();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // This code will always run on the UI thread, therefore is safe to modify UI elements.
                            ir7.setText("" + m);
                        }
                    });
                    Thread.sleep(t);
                    mBtConnection.sendData("o");
                    // reads the proximity sensor 8 value
                    m = mBtConnection.readData();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // This code will always run on the UI thread, therefore is safe to modify UI elements.
                            ir8.setText("" + m);
                        }
                    });
                    Thread.sleep(t);
                    mBtConnection.sendData("p");
                    // reads the left white line sensor value
                    m = mBtConnection.readData();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // This code will always run on the UI thread, therefore is safe to modify UI elements.
                            wlL.setText("" + m);
                        }
                    });
                    Thread.sleep(t);
                    mBtConnection.sendData("q");
                    // reads the middle white line sensor value
                    m = mBtConnection.readData();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // This code will always run on the UI thread, therefore is safe to modify UI elements.
                            wlM.setText("" + m);
                        }
                    });
                    Thread.sleep(t);
                    mBtConnection.sendData("r");
                    // reads the right white line sensor value
                    m = mBtConnection.readData();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // This code will always run on the UI thread, therefore is safe to modify UI elements.
                            wlR.setText("" + m);
                        }
                    });
                    Thread.sleep(t);
                    mBtConnection.sendData("s");
                    // reads the sharp ir sensor 1 value
                    m = mBtConnection.readData();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // This code will always run on the UI thread, therefore is safe to modify UI elements.
                            sharp1.setText("" + Sharp_GP2D12_estimation(m) + " mm");
                        }
                    });
                    Thread.sleep(t);
                    mBtConnection.sendData("t");
                    // reads the sharp ir sensor 2 value
                    m = mBtConnection.readData();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // This code will always run on the UI thread, therefore is safe to modify UI elements.
                            sharp2.setText("" + Sharp_GP2D12_estimation(m) + " mm");
                        }
                    });
                    Thread.sleep(t);
                    mBtConnection.sendData("u");
                    // reads the sharp ir sensor 3 value
                    m = mBtConnection.readData();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // This code will always run on the UI thread, therefore is safe to modify UI elements.
                            sharp3.setText("" + Sharp_GP2D12_estimation(m) + " mm");
                        }
                    });
                    Thread.sleep(t);
                    mBtConnection.sendData("v");
                    // reads the sharp ir sensor 4 value
                    m = mBtConnection.readData();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // This code will always run on the UI thread, therefore is safe to modify UI elements.
                            sharp4.setText("" + Sharp_GP2D12_estimation(m) + " mm");
                        }
                    });
                    Thread.sleep(t);
                    mBtConnection.sendData("w");
                    // reads the sharp ir sensor 5 value
                    m = mBtConnection.readData();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // This code will always run on the UI thread, therefore is safe to modify UI elements.
                            sharp5.setText("" + Sharp_GP2D12_estimation(m) + " mm");
                        }
                    });
                    Thread.sleep(t);
                    mBtConnection.sendData("x");
                    // reads the battery voltage value
                    m = mBtConnection.readData();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // This code will always run on the UI thread, therefore is safe to modify UI elements.
                            battVoltage.setText("" + (float) (((((m) * 100) * 0.07902) + 0.7)/100));
                        }
                    });
                    Thread.sleep(t);
                } catch (InterruptedException ex) {
                    Log.e(TAG, "Exception ", ex);
                }
                catch (Exception ex)
                { Log.e(TAG, "Exception ", ex);
                }
            }
        }
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
    public void onStop()
    {
        super.onStop();

        // Unbind from the service
        if (mBound)
        {
            getActivity().getApplicationContext().unbindService(mConnection);
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
         * Output: bounds to the local service and starts reading the sensors value
         * Logic:  bound to LocalService, cast the IBinder and get LocalService instance by calling getservice method. Also starts reading
         *         sensors by calling (new ReadThread()).start() method
         * Example Call: called automatically when a connection to the Service has been established, with the IBinder of the communication channel to the Service.
         *
         */
        @Override
        public void onServiceConnected(ComponentName className, IBinder service)
        {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            BtConnection.LocalBinder binder = (BtConnection.LocalBinder) service;
            mBtConnection = binder.getService();
            mBound = true;
            new Thread(new ReadThread()).start();

        }

        /*
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
     * Function Name: isBound
     * Input: None
     * Output: boolean value which represents whether teh device is connected to robot or not
     * Logic: Checks whether a bluetooth connection has been established or not by checking the value of mBtConnection object. If it is connected then mBound
     *        is set to true else false
     * Example Call: isBound()
     *
     */
    public boolean isBound()
    {
        if (mBtConnection != null) {
            if (mBtConnection.getStream() != null)
                mBound = true;
            else
                mBound = false;

        }
        return mBound;
    }

    /**
     *
     * Function Name: onCreateView
     * Input: inflater --> The LayoutInflater object that can be used to inflate any views in the fragment,
     *        container	--> If non-null, this is the parent view that the fragment's UI should be attached to. The fragment should not add the view itself, but this can be used to generate the LayoutParams of the view.
     *        savedInstanceState --> If non-null, this fragment is being re-constructed from a previous saved state as given here.
     * Output: creates and returns the view hierarchy associated with the fragment.
     * Logic: Called to have the fragment instantiate its user interface view. This is optional, and non-graphical fragments can return null (which is the default
     *        implementation).
     * Example Call: This will be called between onCreate(Bundle) and onActivityCreated(Bundle).
     *
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sensor_reading, container, false);
    }

    /**
     *
     * Function Name: onViewCreated
     * Input: view --> The View returned by onCreateView(LayoutInflater, ViewGroup, Bundle).
     *        savedInstanceState --> If non-null, this fragment is being re-constructed from a previous saved state as given here.
     * Output: finds a view that was identified by the id attribute from the XML that was processed in onCreate(Bundle)
     * Logic: calls the function findViewById to find the corresponding view is the xml file and maps it to the java objects.
     * Example Call: Called immediately after onCreateView(LayoutInflater, ViewGroup, Bundle) has returned, but before any saved state has been restored in to the view.
     *
     */
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {

        ir1 = (TextView)view.findViewById(R.id.ir1_value);
        ir2 = (TextView)view.findViewById(R.id.ir2_value);
        ir3 = (TextView)view.findViewById(R.id.ir3_value);
        ir4 = (TextView)view.findViewById(R.id.ir4_value);
        ir5 = (TextView)view.findViewById(R.id.ir5_value);
        ir6 = (TextView)view.findViewById(R.id.ir6_value);
        ir7 = (TextView)view.findViewById(R.id.ir7_value);
        ir8 = (TextView)view.findViewById(R.id.ir8_value);
        wlL = (TextView)view.findViewById(R.id.w_l_left_value);
        wlM = (TextView)view.findViewById(R.id.w_l_middle_value);
        wlR = (TextView)view.findViewById(R.id.w_l_right_value);
        sharp1 = (TextView)view.findViewById(R.id.sharp_1_value);
        sharp2 = (TextView)view.findViewById(R.id.sharp_2_value);
        sharp3 = (TextView)view.findViewById(R.id.sharp_3_value);
        sharp4 = (TextView)view.findViewById(R.id.sharp_4_value);
        sharp5 = (TextView)view.findViewById(R.id.sharp_5_value);
        battVoltage = (TextView)view.findViewById(R.id.batt_voltage_value);

    }
}
