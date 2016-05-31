/*
 *
 * Project Name: Fire Bird V operation using Android app
 * Author List: Archie Mittal, Kanupriya Sharma
 * Filename: MotionControlFragment.java
 * Functions: MotionControlFragment(), onCreate(Bundle), onStart(), onResume(), onPause(), onStop(),  onServiceConnected(ComponentName, IBinder), onServiceDisconnected(ComponentName),
 *            setUpMotionInterface(), isBound(), onCreateView(LayoutInflater, ViewGroup,Bundle), onViewCreated(View,Bundle)
 * Global Variables: TAG, forwardArrow, backArrow, rightArrow, leftArrow, stop, buzzerOn, buzzerOff, mBtConnection, mBound, forward, back, right, left, buzz, no_buzz,
 *                   moveForward, moveBackward, moveLeft, moveRight, moveStop, makeBuzzerOn, makeBuzzerOff
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
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


/*
 *
 * Class Name: MotionControlFragment
 * Logic: Starts different threads to make the robot move forward, backward, left, right according to the button clicked
 * Example Call: new MotionControlFragment()
 *
 */
public class MotionControlFragment extends Fragment
{

    //for debugging
    final String TAG = "BLUEFIRE";

    //Threads for starting different motions
   // private Thread forwardThread, backwardThread, leftThread, rightThread, stopThread, buzzerOnThread, buzzerOffThread;

    //Images corresponding to various motions and buzzer on/off
    private ImageView forwardArrow, backArrow, rightArrow, leftArrow, stop, buzzerOn, buzzerOff;

    //to store object of BtConnection
    BtConnection mBtConnection;

    //stores whether activity is bound to service or not
    boolean mBound = false;

    //variables for stopping the different threads
    private boolean forward, back, right, left, buzz, no_buzz ;

    //Constants for various motions
    final String moveForward = "a";
    final String moveBackward = "b";
    final String moveLeft = "c";
    final String moveRight = "d";
    final String moveStop = "e";
    final String makeBuzzerOn = "f";
    final String makeBuzzerOff = "g";

    /**
     *
     * Function Name: MotionControlFragment
     * Input: None
     * Output: None
     * Logic: Required empty public constructor
     * Example Call: called automatically when its class object is created
     *
     */
    public MotionControlFragment() {
    }

    /**
     *
     * Function Name: onCreate
     * Input: savedInstanceState --> If the activity is being re-initialized after previously being shut down then this Bundle contains the data it most recently
     *        supplied in onSaveInstanceState(Bundle). Note: Otherwise it is null.
     * Output: sets up the MotionControlFragment
     * Logic: Called when the activity is starting. This is where most initialization should go: calling setContentView(int) to inflate the activity's UI,
     *        using findViewById(int) to programmatically interact with widgets in the UI
     * Example Call: Called automatically when the activity is created
     *
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    /**
     *
     * Function Name: onStart
     * Input: None
     * Output: binds the  MotionControlFragment with the local bluetooth service
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
     * Function Name: onResume
     * Input: None
     * Output: sets the screen orientation to Portrait mode and initializes the forward, back, right, left and pause variable for various motions
     * Logic: calls the setRequestedOrientation(Orientation) method to set the screen orientation and initializes forward, back, right, left and pause variables to false
     * Example Call: called automatically when the fragment becomes visible and user can interact with it
     *
     */
    @Override
    public void onResume()
    {
        super.onResume();

        //sets up the motion screen and assign listeners to various images and buttons
        setUpMotionInterface();
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        forward = back= right= left = false;
        Log.d(TAG, "CustomMode onResume()");
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
        Log.d(TAG, "CustomMode onPause()");
        forward = false;
        back = false;
        left = false;
        right = false;
        mBtConnection.sendData(moveStop);
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
        public void onServiceConnected(ComponentName className, IBinder service)
        {
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
     * Function Name: setUpMotionInterface
     * Input: void
     * Output: assigns listeners to various motion buttons and starts the motion when the button is clicked
     * Logic: when the forward, backward, left, right button is clicked then it starts teh forward, backward, left and right thread respectively to start the motion.
     * Example Call: setUpMotionInterface()
     *
     */
    private void setUpMotionInterface()
    {
        forwardArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(isBound()) {
                    forward = true;
                    back = false;
                    left = false;
                    right = false;
                    Thread forwardThread = new Thread(new ForwardThread());
                    forwardThread.setPriority(1);
                    forwardThread.start();
                }
                else
                    Toast.makeText(getActivity(), "Connect to Robot First", Toast.LENGTH_SHORT).show();

            }
        });
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isBound()) {
                    forward = false;
                    back = true;
                    left = false;
                    right = false;
                    Thread backwardThread = new Thread(new BackwardThread());
                    backwardThread.setPriority(1);
                    backwardThread.start();
                }
                else
                    Toast.makeText(getActivity(), "Connect to Robot First", Toast.LENGTH_SHORT).show();
            }
        });
        rightArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isBound()) {
                    forward = false;
                    back = false;
                    left = false;
                    right = true;
                    Thread rightThread = new Thread(new RightThread());
                    rightThread.setPriority(1);
                    rightThread.start();
                }
                else
                    Toast.makeText(getActivity(), "Connect to Robot First", Toast.LENGTH_SHORT).show();
            }
        });
        leftArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isBound()) {
                    forward = false;
                    back = false;
                    left = true;
                    right = false;
                    Thread leftThread = new Thread(new LeftThread());
                    leftThread.setPriority(1);
                    leftThread.start();
                }
                else
                    Toast.makeText(getActivity(), "Connect to Robot First", Toast.LENGTH_SHORT).show();
            }
        });
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isBound()) {
                    forward = false;
                    back = false;
                    left = false;
                    right = false;
                    mBtConnection.sendData(moveStop);
                }
                else
                    Toast.makeText(getActivity(), "Connect to Robot First", Toast.LENGTH_SHORT).show();
            }
        });

        buzzerOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isBound()) {
                    buzz = true;
                    no_buzz = false;
                    Thread buzzerOnThread = new Thread(new BuzzerOnThread());
                    buzzerOnThread.setPriority(1);
                    buzzerOnThread.start();
                }
                else
                    Toast.makeText(getActivity(), "Connect to Robot First", Toast.LENGTH_SHORT).show();
            }
        });

        buzzerOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isBound()) {
                    buzz = false;
                    no_buzz = true;
                    Thread buzzerOffThread = new Thread(new BuzzerOffThread());
                    buzzerOffThread.setPriority(1);
                    buzzerOffThread.start();
                }
                else
                    Toast.makeText(getActivity(), "Connect to Robot First", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Thread to make the bot move forward
    private class ForwardThread implements Runnable
    {
        public void run()
        {
            while(forward)
            {
                try {
                    mBtConnection.sendData(moveForward);
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
                    mBtConnection.sendData(moveBackward);

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
            while(left)
            {
                try {
                    mBtConnection.sendData(moveLeft);
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
            while(right)
            {
                try {
                    mBtConnection.sendData(moveRight);

                } catch (Exception ex) {
                    Log.e(TAG, "Exception ", ex);
                }
            }
        }
    }

    // Thread to turn On the buzzer
    private class BuzzerOnThread implements Runnable
    {
        public void run()
        {
            while(buzz)
            {
                try {
                    mBtConnection.sendData(makeBuzzerOn);
                } catch (Exception ex) {
                    Log.e(TAG, "Exception ", ex);
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // This code will always run on the UI thread, therefore is safe to modify UI elements.
                        buzzerOn.setVisibility(View.INVISIBLE);
                        buzzerOff.setVisibility(View.VISIBLE);
                    }
                });
            }
        }
    }

    // Thread to turn Off the buzzer
    private class BuzzerOffThread implements Runnable
    {
        public void run()
        {
            while(no_buzz)
            {
                try {
                    mBtConnection.sendData(makeBuzzerOff);

                } catch (Exception ex) {
                    Log.e(TAG, "Exception ", ex);
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // This code will always run on the UI thread, therefore is safe to modify UI elements.
                        buzzerOff.setVisibility(View.INVISIBLE);
                        buzzerOn.setVisibility(View.VISIBLE);
                    }
                });
            }
        }
    }


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
        if(mBtConnection.getStream() != null)
            mBound = true;
        else
            mBound = false;
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
        return inflater.inflate(R.layout.fragment_motion_control, container, false);
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
        forwardArrow = (ImageView)view.findViewById(R.id.forward_arrow);
        backArrow = (ImageView)view.findViewById(R.id.back_arrow);
        rightArrow = (ImageView)view.findViewById(R.id.right_arrow);
        leftArrow = (ImageView)view.findViewById(R.id.left_arrow);
        stop = (ImageView)view.findViewById(R.id.stop);
        buzzerOn = (ImageView)view.findViewById(R.id.soundon);
        buzzerOff = (ImageView)view.findViewById(R.id.soundoff);
        buzzerOn.setVisibility(View.VISIBLE);
        buzzerOff.setVisibility(View.INVISIBLE);
    }
}
