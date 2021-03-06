package com.starbrunch.couple.photo.frame.main.bluetooth.thread;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import com.littlefox.logmonitor.Log;
import com.starbrunch.couple.photo.frame.main.bluetooth.BluetoothController;
import com.starbrunch.couple.photo.frame.main.bluetooth.listener.BluetoothThreadCallback;

import java.io.IOException;

/**
 * Created by 정재현 on 2018-01-18.
 */

public class ConnectingThread extends Thread
{
    private final BluetoothSocket mBluetoothSocket;
    private final BluetoothDevice mBluetoothDevice;
    private BluetoothThreadCallback mBluetoothThreadCallback = null;

    public ConnectingThread(BluetoothDevice device, BluetoothThreadCallback bluetoothThreadCallback)
    {
        mBluetoothDevice = device;
        mBluetoothThreadCallback = bluetoothThreadCallback;
        BluetoothSocket socket = null;

        try
        {
            socket = device.createRfcommSocketToServiceRecord(BluetoothController.RFCCMM_UUID);
        }catch(Exception e)
        {
            Log.f("Message : "+ e.getMessage());
        }
        mBluetoothSocket = socket;
        mBluetoothThreadCallback.sendConnectStatus(BluetoothController.STATE_CONNECTING);
    }


    @Override
    public void run()
    {
        super.run();
        Log.f("BEGIN Connecting Thread : "+ this);
        setName("ConnectingThread");

        mBluetoothThreadCallback.cancelDiscovery();

        try
        {
            mBluetoothSocket.connect();
        }catch (Exception e)
        {
            Log.f("Message : "+ e.getMessage());
            try
            {
                mBluetoothSocket.close();
            }
            catch (IOException e1)
            {
                Log.f("IO Message : "+ e1.getMessage());
            }

            if(mBluetoothThreadCallback.getConnectStatus() == BluetoothController.STATE_CONNECTING)
            {
                mBluetoothThreadCallback.sendConnectStatus(BluetoothController.STATE_CONNECTION_FAILED);
            }

            return;

        }

        synchronized (this)
        {
            mBluetoothThreadCallback.destroyConnectingThread();
        }

        mBluetoothThreadCallback.connectedInformation(mBluetoothSocket, mBluetoothDevice);
    }

    public void cancel()
    {
        try
        {
            mBluetoothSocket.close();
        }catch (Exception e)
        {
            Log.f("Message : "+ e.getMessage());
        }
    }
}
