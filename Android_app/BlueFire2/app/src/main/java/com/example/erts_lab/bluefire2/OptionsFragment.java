/**
 *
 * Project Name: Fire Bird V operation using Android app
 * Author List: Archie Mittal, Kanupriya Sharma
 * Filename: OptionsFragment
 * Functions: OptionsFragment(), onCreate(Bundle), onCreateView(LayoutInflater, ViewGroup, Bundle), onViewCreated(View,Bundle)
 * Global Variables: TAG, customMode, gestureMode, sensors, velocity
 *
 */

package com.example.erts_lab.bluefire2;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


/**
 *
 * Class Name: OptionsFragment
 * Logic: fragment which displays the four options available to the user, i.e., normal mode, gesture mode, sensors and velocity and allows the user to
 *        make choice among those options.
 * Example Call: new OptionsFragment()
 *
 */
public class OptionsFragment extends Fragment {

    String TAG = "BLUEFIRE";

    //Buttons for various options available
    Button customMode, gestureMode, sensors, velocity;


    /**
     *
     * Function Name: OptionsFragment
     * Input: None
     * Output: None
     * Logic: Required empty public constructor
     * Example Call: called automatically when its class object is created
     *
     */
    public OptionsFragment() {
    }


    /**
     *
     * Function Name: onCreate
     * Input: savedInstanceState --> If the activity is being re-initialized after previously being shut down then this Bundle contains the data it most recently
     *        supplied in onSaveInstanceState(Bundle). Note: Otherwise it is null.
     * Output: sets up the OptionsFragment
     * Logic: Called when the activity is starting. This is where most initialization should go: calling setContentView(int) to inflate the activity's UI,
     *        using findViewById(int) to programmatically interact with widgets in the UI
     * Example Call: Called automatically when the activity is created
     *
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_options, container, false);
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
        customMode = (Button)view.findViewById(R.id.custom_mode_button);
        gestureMode = (Button)view.findViewById(R.id.gesture_mode_button);
        sensors = (Button)view.findViewById(R.id.sensor_button);
        velocity = (Button)view.findViewById(R.id.velocity_button);
    }




}
