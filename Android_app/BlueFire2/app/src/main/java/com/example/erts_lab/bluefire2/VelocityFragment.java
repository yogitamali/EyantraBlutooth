/*
 *
 * Project Name: Fire Bird V operation using Android app
 * Author List: Archie Mittal, Kanupriya Sharma
 * Filename: VelocityFragment.java
 * Functions: VelocityFragment(), onStart(), onStop(), onResume(), onServiceConnected(ComponentName, IBinder), onServiceDisconnected(ComponentName),
 *            onCreateView(LayoutInflater, ViewGroup,Bundle), onViewCreated(View,Bundle), onProgressChanged(SeekBar, int, boolean), onStartTrackingTouch(SeekBar),
 *            onStopTrackingTouch(SeekBar), beforeTextChanged(CharSequence, int, int,int), onTextChanged(CharSequence, int, int, int), afterTextChanged(Editable)
 * Global Variables: TAG, mBtConnection, mBound, leftMotorVelocitySeekBar, rightMotorVelocitySeekBar, leftMotorVelocityText, rightMotorVelocityText, setButton,
 *                   leftMotorVelocity, rightMotorVelocity,
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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;


/**
 *
 * Class Name: VelocityFragment
 * Logic: controls the velocity of bot by taking the velocity from the TextFields and then sending those values to the output stream
 * Example Call: new VelocityFragment()
 *
 */
public class VelocityFragment extends Fragment {

    //for debugging
    final String TAG = "BLUEFIRE";

    //to store object of BtConnection
    BtConnection mBtConnection;

    //stores whether activity is bound to service or not
    boolean mBound = false;

    // select the left and right motor velocity with the help of seekbar
    SeekBar leftMotorVelocitySeekBar, rightMotorVelocitySeekBar;

    // Text box used to enter the velocity of both the wheels
    EditText leftMotorVelocityText, rightMotorVelocityText;

    // sets the velocity of robot
    Button setButton;

    // stores the left and right motor velocity
    int leftMotorVelocity, rightMotorVelocity;

    /**
     *
     * Function Name: VelocityFragment
     * Input: None
     * Output: None
     * Logic: Required empty public constructor
     * Example Call: called automatically when its class object is created
     *
     */
    public VelocityFragment() {
    }


    /**
     *
     * Function Name: onStart
     * Input: None
     * Output: binds the velocity fragment with the local bluetooth service
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
            mBound = false;
        }
    }

    /**
     *
     * Function Name: onResumne
     * Input: None
     * Output: sets the screen orientation to Portrait mode
     * Logic: calls the setRequestedOrientation(Orientation) method to set the screen orientation
     * Example Call: called automatically when the fragment becomes visible and user can interact with it
     *
     */
    @Override
    public void onResume() {
        super.onResume();
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_velocity, container, false);
    }

    /**
     *
     * Function Name: onViewCreated
     * Input: view --> The View returned by onCreateView(LayoutInflater, ViewGroup, Bundle).
     *        savedInstanceState --> If non-null, this fragment is being re-constructed from a previous saved state as given here.
     * Output: sets the left and right motor velocity and updates the left and right motor velocity text field respectively
     * Logic: Called immediately after onCreateView(LayoutInflater, ViewGroup, Bundle) has returned, but before any saved state has been restored in to the view.
     *        This gives subclasses a chance to initialize themselves once they know their view hierarchy has been completely created. The fragment's view hierarchy
     *        is not however attached to its parent at this point.
     * Example Call: Called immediately after onCreateView(LayoutInflater, ViewGroup, Bundle) has returned, but before any saved state has been restored in to the view.
     *
     */
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        Log.d(TAG, "velocity onViewCreated()");
        // finds a view that was identified by the id left_velocity_seekbar from the XML that was processed in onCreate(Bundle)
        leftMotorVelocitySeekBar = (SeekBar) view.findViewById(R.id.left_velocity_seekbar);
        // assigns OnSeekBarChangeListener to leftMotorVelocitySeekBar
        leftMotorVelocitySeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            // stores the current value of seek bar
            int progress = 0;

            /**
             *
             * Function Name: onProgressChanged
             * Input: seekBar --> The SeekBar whose progress has changed
             *        progress --> The current progress level. This will be in the range 0..max where max was set by setMax(int). (The default value for max is 100.)
             *        fromUser -->  True if the progress change was initiated by the user.
             * Output: sets the leftMotorVelocityText value according to the seekbar's value
             * Logic: gets the seekbar's current value stored in the variable progressValue and sets the text of leftMotorVelocityText accordingly
             * Example Call: called automatically when the seekbar's value changes
             *
             */
            @Override
            public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
                progress = progresValue;
                leftMotorVelocityText.setText("" + progress);
            }

            /**
             *
             * Function Name: onStartTrackingTouch
             * Input: seekBar --> The SeekBar in which the touch gesture began
             * Example Call: called automatically when the seekbar is touched
             *
             */
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            /**
             *
             * Function Name: onStopTrackingTouch
             * Input: seekBar --> The SeekBar in which the touch gesture began
             * Example Call: called automatically when the change in seekbar's value stops
             *
             */
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        // finds a view that was identified by the id right_velocity_seekbar from the XML that was processed in onCreate(Bundle)
        rightMotorVelocitySeekBar = (SeekBar) view.findViewById(R.id.right_velocity_seekbar);
        // assigns OnSeekBarChangeListener to rightMotorVelocitySeekBar
        rightMotorVelocitySeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            // stores the current value of seek bar
            int progress = 0;

            /*
             *
             * Function Name: onProgressChanged
             * Input: seekBar --> The SeekBar whose progress has changed
             *        progress --> The current progress level. This will be in the range 0..max where max was set by setMax(int). (The default value for max is 100.)
             *        fromUser -->  True if the progress change was initiated by the user.
             * Output: sets the rightMotorVelocityText value according to the seekbar's value
             * Logic: gets the seekbar's current value stored in the variable progressValue and sets the text of rightMotorVelocityText accordingly
             * Example Call: called automatically when the seekbar's value changes
             *
             */
            @Override
            public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
                progress = progresValue;
                rightMotorVelocityText.setText("" + progress);
            }

            /*
             *
             * Function Name: onStartTrackingTouch
             * Input: seekBar --> The SeekBar in which the touch gesture began
             * Example Call: called automatically when the seekbar is touched
             *
             */
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            /**
             *
             * Function Name: onStopTrackingTouch
             * Input: seekBar --> The SeekBar in which the touch gesture began
             * Example Call: called automatically when the change in seekbar's value stops
             *
             */
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        // finds a view that was identified by the id left_velocity_seekbar from the XML that was processed in onCreate(Bundle)
        leftMotorVelocityText = (EditText) view.findViewById(R.id.left_velocity_text);

        // assigns TextChangedListener to leftMotorVelocityText
        leftMotorVelocityText.addTextChangedListener(new TextWatcher() {

            /**
             *
             * Function Name: beforeTextChanged
             * Example Call: This method is called to notify you that, within s, the count characters beginning at start are about to be replaced by new text with length after.
             *               It is an error to attempt to make changes to s from this callback.
             *
             */
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            /**
             *
             * Function Name: onTextChanged
             * Input: s --> char sequence which stores the characters
             *        start --> integer which stores the starting location from where the characters have been changed
             *        before --> integer which contains the length of the text that has been replaced
             *        count --> integer which stores the length of new characters
             * Output: sets the seek bars value according to the text entered
             * Logic: This method is called to notify you that, within s, the count characters beginning at start have just replaced old text that had length before.
             * Example Call: called automatically when the text is changing
             *
             */
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (leftMotorVelocityText.getText().toString() != "" && !(leftMotorVelocityText.getText().toString()).isEmpty() && leftMotorVelocityText.getText().toString().length() == 3) {
                    int value = Integer.parseInt(leftMotorVelocityText.getText().toString());

                    if (value <= 255)
                    {
                        leftMotorVelocitySeekBar.setProgress(value);
                    }
                    else
                    {
                        leftMotorVelocitySeekBar.setProgress(0);
                        leftMotorVelocityText.setText("0");
                    }
                }

            }

            /**
             *
             * Function Name: afterTextChanged
             * Input: the editable text s
             * Logic: This method is called to notify you that, somewhere within s, the text has been changed.
             * Example Call: called automatically after the text has been changed
             *
             */
            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // finds a view that was identified by the id right_velocity_seekbar from the XML that was processed in onCreate(Bundle)
        rightMotorVelocityText = (EditText) view.findViewById(R.id.right_velocity_text);

        // assigns TextChangedListener to rightMotorVelocityText
        rightMotorVelocityText.addTextChangedListener(new TextWatcher() {

            /**
             *
             * Function Name: beforeTextChanged
             * Example Call: This method is called to notify you that, within s, the count characters beginning at start are about to be replaced by new text with length after.
             *               It is an error to attempt to make changes to s from this callback.
             *
             */
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            /**
             *
             * Function Name: onTextChanged
             * Input: s --> char sequence which stores the characters
             *        start --> integer which stores the starting location from where the characters have been changed
             *        before --> integer which contains the length of the text that has been replaced
             *        count --> integer which stores the length of new characters
             * Output: sets the seek bars value according to the text entered
             * Logic: This method is called to notify you that, within s, the count characters beginning at start have just replaced old text that had length before.
             * Example Call: called automatically when the text is changing
             *
             */
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (rightMotorVelocityText.getText().toString() != "" && !(rightMotorVelocityText.getText().toString()).isEmpty() && rightMotorVelocityText.getText().toString().length() == 3) {
                    int value = Integer.parseInt(rightMotorVelocityText.getText().toString());

                    if (value <= 255) {
                        rightMotorVelocitySeekBar.setProgress(value);
                    }
                    else
                    {
                        rightMotorVelocitySeekBar.setProgress(0);
                        rightMotorVelocityText.setText("0");
                    }
                }
            }

            /**
             *
             * Function Name: afterTextChanged
             * Input: the editable text s
             * Logic: This method is called to notify you that, somewhere within s, the text has been changed.
             * Example Call: called automatically after the text has been changed
             *
             */
            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        // finds a view that was identified by the id set_button from the XML that was processed in onCreate(Bundle)
        setButton = (Button) view.findViewById(R.id.set_button);
        // assigns the OnClickListener with the setButton
        setButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                leftMotorVelocity = Integer.parseInt(leftMotorVelocityText.getText().toString());
                rightMotorVelocity = Integer.parseInt(rightMotorVelocityText.getText().toString());
                // if entered left velocity is less than 127 then it sends "y" to the output stream
                if (leftMotorVelocity <= 127 ) {
                    mBtConnection.sendData("y");
                }
                // if entered left velocity is greater than 127 then it sends "z" to the output stream
                else if (leftMotorVelocity <= 255) {
                    mBtConnection.sendData("z");
                    leftMotorVelocity = leftMotorVelocity/2;
                }
                // sends the calculated left motor velocity to the output stream in the form of string
                mBtConnection.sendData(String.valueOf((char) leftMotorVelocity));
                // if entered right velocity is less than 127 then it sends "A" to the output stream
                if (rightMotorVelocity <= 127) {
                    mBtConnection.sendData("A");
                }
                // if entered right velocity is greater than 127 then it sends "B" to the output stream
                else if (rightMotorVelocity <= 255) {
                    mBtConnection.sendData("B");
                    rightMotorVelocity = rightMotorVelocity/2;
                }
                // sends the calculated right motor velocity to the output stream in the form of string
                mBtConnection.sendData(String.valueOf((char) rightMotorVelocity));
                // makes an object of the class NormalFragment
                MotionControlFragment mCustomFragment = new MotionControlFragment();
                // to interact with the fragment, fragment manager is obtained and the transaction is started
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                // replaces the current fragment with the NormalFragment
                transaction.replace(R.id.fragment_container, mCustomFragment, "CustomFragment");
                // adds the fragment to the back stack and when the back button is pressed then the fragment at the top of the stack is popped off
                transaction.addToBackStack(null);
                // saves all the changes made to the transaction
                transaction.commit();
            }
        });
    }
}


