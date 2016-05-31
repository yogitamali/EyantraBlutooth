/*
 *
 * Project Name: Fire Bird V operation using Android app
 * Author List: Archie Mittal, Kanupriya Sharma
 * Filename:BtConnection
 * Functions:BtConnection(Activity , BluetoothDevice,BtConnection(), connect(),getStream(),readData(),sendData(byte[] write_buffer),sendData(String write_buffer),disconnect())
 * Objects:TAG, mBinder,MY_UUID,mActivity,mDevice,mmSocket,mInputStream,mOutputStream
 *
 */

package com.example.erts_lab.bluefire2;



import android.app.Activity;
import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

/**
 * This class does all the work for setting up and managing Bluetooth
 * connections with other devices. It has a thread for connecting with a device, and a
 * thread for performing data transmissions when connected.
 */
public class BtConnection extends Service
{
    // Debugging
    public final String TAG = "BLUEFIRE";

    // Binder given to clients
    private final IBinder mBinder = new LocalBinder();

    // Unique UUID for this application
    public static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    public Activity mActivity;
    public BluetoothDevice mDevice;
    public BluetoothSocket mmSocket;
    public static InputStream mInputStream = null;
    public static OutputStream mOutputStream = null;
    /*
    *
    * Function Name: <BtConnection(Activity activity, BluetoothDevice device)>
    * Input: <Activity activity, BluetoothDevice device>
    * Output: <NULL>
    * Logic: <It is a constructor which prepares a new BluetoothChat session.>
    * Example Call: <As soon as object is created>
    *
    */
    public BtConnection(Activity activity, BluetoothDevice device)
    {
        mActivity = activity;
        mDevice = device;
    }

    public BtConnection(){}

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder
    {
        BtConnection getService()
        {
            // Return this instance of LocalService so clients can call public methods
            return BtConnection.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        return mBinder;
    }
/*
*
* Function Name: <connect()>
* Input: <NULL>
* Output: <True/False>
* Logic: <this function is used to make connection with the BluetoothSocket>
* Example Call: <connect()>
*
*/

    public boolean connect()
    {
        BluetoothSocket tmp = null;
        try
        {
            tmp = mDevice.createRfcommSocketToServiceRecord(MY_UUID);
        } catch (IOException e)
        {
            Log.e(TAG, "Socket create() failed", e);
            return false;
            //return false;
        }
        mmSocket = tmp;
        // Make a connection to the BluetoothSocket
        try
        {
            // This is a blocking call and will only return on a
            // successful connection or an exception
            mmSocket.connect();
        } catch (IOException e)
        {
            // Close the socket
            try
            {
                mmSocket.close();
            } catch (IOException e2)
            {
                Log.e(TAG, "unable to close() socket during connection failure", e2);
            }
            //return;
            return false;
        }
        try {
            /** Get input and output stream handles for data transfer. */

            mInputStream = mmSocket.getInputStream();
            mOutputStream = mmSocket.getOutputStream();
            //Log.d(TAG, "InpuStream: "+ mInputStream);
            //Log.d(TAG, "OutputStream: "+ mOutputStream);
        }catch (IOException e)
        {
            Log.e(TAG, "temp sockets not created", e);
            return false;
        }
        return true;
    }

    public OutputStream getStream()
    {
        return mOutputStream;
    }
/*
*
* Function Name: <readData()>
* Input: <NULL>
* Output: <integer>
* Logic: <This function is used to read the data which is received by the bluetooth of our phone>
* Example Call: <readData()>
*
*/

    public synchronized int readData() {
        String input = null;
        int singleData = 0;
        String value = null;
        try {
            singleData = mInputStream.read();
            value =(char) singleData + "";
            if (singleData != 10) {
                //String logText = new String(new byte[] {singleData});
                //value = singleData & 0xff;
                //Log.d(TAG, "value = " + singleData);
                input = new String();
                input = String.valueOf(singleData);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return singleData;
    }

    /*
*
* Function Name: <sendData(byte[] write_buffer)>
* Input: <byte[] write_buffer>
* Output: <NULL>
* Logic: <Function to send data over BT.
*         Task: (1)To send the byte array over Bluetooth Channel.>
* Example Call: <sendData(abcd)>
*
*/
    public void sendData(byte[] write_buffer)
    {
        try
        {
            //Log.d(TAG, "stream" + mOutputStream);
            //Log.d(TAG, "Write buffer" + write_buffer);

            mOutputStream.write(write_buffer);
            mOutputStream.flush();
        }catch (IOException e)
        {
            Log.e(TAG, "Writing on command error");
        }
        catch (Exception ex)
        {
            Log.d(TAG, "Error");
        }
        //Log.d(TAG, "Writing on command successful");
    }

/*
*
* Function Name: <sendData(String write_buffer)>
* Input: <String write_buffer>
* Output: <null>
* Logic: <This function is used to send the string via bluetooth>
* Example Call: <sendData('abcd')>
*
*/

    public void sendData(String write_buffer)
    {
        try
        {
            //Log.d(TAG, "stream" + mOutputStream);
            //Log.d(TAG, "Write buffer" + write_buffer);

            //Log.d(TAG, "ASCII: "+ write_buffer);
            //Log.d(TAG, "Bytes: "+write_buffer.getBytes());
            mOutputStream.write(write_buffer.getBytes());
            mOutputStream.flush();
        }catch (IOException e)
        {
            Log.e(TAG, "Writing on command error");
        }
        catch (Exception ex)
        {
            Log.d(TAG, "Error");
        }
        //Log.d(TAG, "Writing on command successful");
    }
    /*
*
* Function Name: <disconnect()>
* Input: <Null>
* Output: <Null>
* Logic: < Function to close BT connection.
*        Task: (1)Close input and output streams
* 		       (2)Close Bluetooth socket.>
* Example Call: <disconnect()>
*
*/

    public void disconnect()
    {
        try
        {
            if (mInputStream != null)
            {
                mInputStream.close();
            }
            if (mOutputStream != null)
            {
                mOutputStream.close();
            }
            if (mmSocket != null)
            {
                mmSocket.close();
            }
            Log.d(TAG, "BT Channel free");

        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
