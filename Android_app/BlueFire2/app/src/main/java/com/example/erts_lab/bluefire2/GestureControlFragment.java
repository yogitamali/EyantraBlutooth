/*
 *
 * Project Name: Fire Bird V operation using Android app
 * Author List: Archie Mittal, Kanupriya Sharma
 * Filename: GestureControlFragment.java
 * Functions: onCreate(Bundle), onStart(), onResume(), onPause(), onStop(), unregisterListener(), onServiceConnected(ComponentName, IBinder),
 *            onServiceDisconnected(ComponentName), isBound(), onCreateView(LayoutInflater, ViewGroup, Bundle), onViewCreated(View, Bundle),
 *            onSensorChanged(int, float[]), onAccuracyChanged(int , int), updateImage(int)
 * Global Variables: TAG, mBtConnection, mBound, gestureImage, mAccelerometer, mSensorManager, forwardThread, backwardThread, leftThread, rightThread,
 *                   forward, back, right, left
 *
 */

package com.example.erts_lab.bluefire2;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 *
 * Class Name: GestureControlFragment
 * Logic: controls the motion of robot with the help of phone's accelerometer
 * Example Call: new GestureControlFragment()
 *
 */
public class GestureControlFragment extends Fragment {

    //for debugging
    final String TAG = "BLUEFIRE";

    //to store object of BtConnection
    BtConnection mBtConnection;

    //stores whether activity is bound to service or not
    boolean mBound = false;

    //displays the image of corresponding motion
    ImageView gestureImage;

    Sensor mAccelerometer;
    SensorManager mSensorManager;

    private Thread forwardThread, backwardThread, leftThread, rightThread;
    private boolean forward, back, right, left ;

    /**
     *
     * Function Name: GestureControlFragment
     * Input: None
     * Output: None
     * Logic: Required empty public constructor
     * Example Call: called automatically when its class object is created
     *
     */
    public GestureControlFragment() {
    }

    /**
     *
     * Function Name: onCreate
     * Input: savedInstanceState --> If the activity is being re-initialized after previously being shut down then this Bundle contains the data it most recently
     *        supplied in onSaveInstanceState(Bundle). Note: Otherwise it is null.
     * Output: sets up the GestureControlFragment an gets the phone's accelerometer sensor
     * Logic: Called when the activity is starting. This is where most initialization should go: calling setContentView(int) to inflate the activity's UI,
     *        using findViewById(int) to programmatically interact with widgets in the UI
     * Example Call: Called automatically when the activity is created
     *
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mSensorManager = (SensorManager) getActivity().getSystemService(getActivity().SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    /**
     *
     * Function Name: onStart
     * Input: None
     * Output: binds the  GestureControlFragment with the local bluetooth service
     * Logic: Calls the activity BtConnection and binds this activity with the BtConnection by calling bindService method
     * Example Call: Called automatically when the fragment becomes visible
     *
     */
    @Override
    public void onStart() {
        super.onStart();
        // Bind to LocalService
        Intent intent = new Intent(getActivity(), BtConnection.class);
        getActivity().getApplicationContext().bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    /**
     *
     * Function Name: onResume
     * Input: None
     * Output: sets the screen orientation to Landscape mode and assigns listener to the accelerometer's value
     * Logic: calls the setRequestedOrientation(Orientation) method to set the screen orientation and calls the registerListener method to register the accelerometer
     * Example Call: called automatically when the fragment becomes visible and user can interact with it
     *
     */
    @Override
    public void onResume() {
        super.onResume();
        mSensorManager.registerListener(mSensorAccelerometer, SensorManager.SENSOR_ACCELEROMETER, SensorManager.SENSOR_DELAY_GAME);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        Log.d(TAG, "GestureMode onResume()");
    }


    /**
     *
     * Function Name: onPause
     * Input: None
     * Output: stops all the motions
     * Logic: sets all the variables defining the motion equals to false
     * Example Call: called automatically when the fragment goes in  inactive mode
     *
     */
    @Override
    public void onPause()
    {
        super.onPause();
        Log.d(TAG, "GestureMode onPause()");
        forward = false;
        back = false;
        left = false;
        right = false;
        mBtConnection.sendData("e");
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
    public void onStop() {
        super.onStop();

        // Unbind from the service
        if (mBound) {
            getActivity().getApplicationContext().unbindService(mConnection);
            unregisterListener();
            mBound = false;
        }
    }

    /**
     *
     * Function Name: unregisterListener
     * Input: None
     * Output: unregisters the listener
     * Logic: unregister the listener by calling unregisterListener method
     * Example Call: unregisterListener()
     *
     */
    public void unregisterListener()
    {
        mSensorManager.unregisterListener(mSensorAccelerometer);
    }



    /**
     * Defines callbacks for service binding, passed to bindService()
     */
    private ServiceConnection mConnection = new ServiceConnection() {

        /**
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
        public void onServiceConnected(ComponentName className, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            BtConnection.LocalBinder binder = (BtConnection.LocalBinder) service;
            mBtConnection = binder.getService();
            mBound = true;

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
    public boolean isBound() {
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
        return inflater.inflate(R.layout.fragment_gesture_control, container, false);
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
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        gestureImage = (ImageView)view.findViewById(R.id.gesture_image);
    }


    private final SensorListener mSensorAccelerometer = new SensorListener() {

        /**
         *
         * Function Name: onSensorChanged
         * Input: sensor --> id of the sensor whose value has been changed
         *        values[] --> array whichstors the changed sensor values
         * Output: adjusts the motion of the robot according to sensor values
         * Logic: With phone held in upright condition, +ve X-axis goes to right,
         *         and +ve Y-axis goes front
         *
         *  Hence 	+ve x-value--> Right
         *  		-ve x-value--> Left
         *  		+ve y-value--> Front
         *  		-ve y-value--> Back
         * Example Call: called automatically after the sensor has been registered and its vaues is changed
         *
         */
        @Override
        public void onSensorChanged(int sensor, float values[]) {
            float mAccelX = 0;
            float mAccelY = 0;

            /** Copy the values of acceleration in 3 directions. */
            mAccelX = values[0];
            mAccelY = values[1];
            if (isBound()) {
                if (mAccelY > 1)
                {
                    forward = true;
                    back = false;
                    left = false;
                    right = false;
                    forwardThread = new Thread(new ForwardThread());
                    forwardThread.setPriority(1);
                    forwardThread.start();
                    updateImage(R.drawable.forward);

                } else if (mAccelY < -1 )
                {
                    forward = false;
                    back = true;
                    left = false;
                    right = false;
                    backwardThread = new Thread(new BackwardThread());
                    backwardThread.setPriority(1);
                    backwardThread.start();
                    updateImage(R.drawable.backward);

                } else if (mAccelX < -1 )
                {
                    forward = false;
                    back = false;
                    left = true;
                    right = false;
                    leftThread = new Thread(new LeftThread());
                    leftThread.setPriority(1);
                    leftThread.start();
                    updateImage(R.drawable.left);

                } else if (mAccelX > 1 )
                {
                    forward = false;
                    back = false;
                    left = false;
                    right = true;
                    rightThread = new Thread(new RightThread());
                    rightThread.setPriority(1);
                    rightThread.start();
                    updateImage(R.drawable.right);
                } else {
                    forward = false;
                    back = false;
                    left = false;
                    right = false;
                    Log.d(TAG, "Stop of Gesture");
                    mBtConnection.sendData("e");
                    updateImage(R.drawable.pause);
                }
            }
        }

        /**
         *
         * Function Name: onAccuracyChanged
         * Input: accuracy --> The new accuracy of this sensor,
         * Example Call: Called when the accuracy of the registered sensor has changed.
         *
         */
        @Override
        public void onAccuracyChanged(int sensor, int accuracy) {
        }
    };

    /**
     *
     * Function Name: updateImage
     * Input: id --> integer which stores the id of the image to be loaded
     * Output: sets the image
     * Logic: calls the function setImageResource(id) to set the image
     * Example Call: updateImage(R.id.forward)
     *
     */
    public void updateImage(int id)
    {
        gestureImage.setImageResource(id);
    }

    // Thread to make the bot move forward
    private class ForwardThread implements Runnable
    {
        public void run()
        {
            while(forward == true)
            {
                try {
                    mBtConnection.sendData("a");

                } catch (Exception ex) {
                    Log.e(TAG, "Exception ", ex);
                }
            }
        }
    }

    // Thread to make the bot move backward
    private class BackwardThread implements Runnable
    {
        public void run()
        {

            while(back == true)
            {
                try {
                    mBtConnection.sendData("b");

                } catch (Exception ex) {
                    Log.e(TAG, "Exception ", ex);
                }
            }
        }
    }

    // Thread to make the bot move right
    private class RightThread implements Runnable
    {
        public void run()
        {
            while(right == true)
            {
                try {
                    mBtConnection.sendData("d");

                } catch (Exception ex) {
                    Log.e(TAG, "Exception ", ex);
                }
            }
        }
    }

    // Thread to make the bot move left
    private class LeftThread implements Runnable
    {
        public void run()
        {
            while(left == true)
            {
                try {
                    mBtConnection.sendData("c");

                } catch (Exception ex) {
                    Log.e(TAG, "Exception ", ex);
                }
            }
        }
    }
}
