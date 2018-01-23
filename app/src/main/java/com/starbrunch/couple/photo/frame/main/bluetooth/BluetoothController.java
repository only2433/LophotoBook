package com.starbrunch.couple.photo.frame.main.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;

import com.starbrunch.couple.photo.frame.main.R;
import com.starbrunch.couple.photo.frame.main.bluetooth.common.Constants;
import com.starbrunch.couple.photo.frame.main.bluetooth.listener.BluetoothThreadCallback;
import com.starbrunch.couple.photo.frame.main.bluetooth.thread.AcceptThread;
import com.starbrunch.couple.photo.frame.main.bluetooth.thread.ConnectedThread;
import com.starbrunch.couple.photo.frame.main.bluetooth.thread.ConnectingThread;
import com.starbrunch.couple.photo.frame.main.handler.WeakReferenceHandler;
import com.starbrunch.couple.photo.frame.main.object.MessageObject;

/**
 * Created by 정재현 on 2018-01-18.
 */

public class BluetoothController implements BluetoothThreadCallback
{

    private BluetoothAdapter mBluetoothAdapter = null;
    private int mConnectStatus = Constants.STATE_NONE;
    private int mNewConnectStatus = Constants.STATE_NONE;

    private AcceptThread mAcceptThread                  = null;
    private ConnectingThread mConnectingThread          = null;
    private ConnectedThread mConnectedThread            = null;
    private WeakReferenceHandler mWeakReferenceHandler  = null;
    private Context mContext = null;



    public BluetoothController(Context context, WeakReferenceHandler handler)
    {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mContext = context;
        mWeakReferenceHandler = handler;
        mConnectStatus = Constants.STATE_NONE;
        mNewConnectStatus = mConnectStatus;
    }

    private synchronized void updateConnectStatus()
    {
        mConnectStatus = getConnectStatus();
        mNewConnectStatus = mConnectStatus;

        mWeakReferenceHandler.obtainMessage(Constants.MESSAGE_STATE_CHANGE, mNewConnectStatus, -1).sendToTarget();
    }

    private void startAcceptThread()
    {
        if(mAcceptThread == null)
        {
            mAcceptThread = new AcceptThread(this);
            mAcceptThread.start();
        }
    }

    private void cancelAcceptThread()
    {
        if(mAcceptThread != null)
        {
            mAcceptThread.cancel();
            mAcceptThread = null;
        }
    }

    private void startConnectingThread(BluetoothDevice device)
    {
        if(mConnectingThread == null)
        {
            mConnectingThread = new ConnectingThread(device, this);
            mConnectingThread.start();
        }
    }

    private void cancelConnectingThread()
    {
        if(mConnectingThread != null)
        {
            mConnectingThread.cancel();
            mConnectingThread = null;
        }
    }

    private void startConnectedThread(BluetoothSocket socket)
    {
        if(mConnectedThread == null)
        {
            mConnectedThread = new ConnectedThread(socket, this);
            mConnectedThread.start();
        }
    }

    private void cancelConnectedThread()
    {
        if(mConnectedThread != null)
        {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }
    }

    /**
     * Start the chat service. Specifically start AcceptThread to begin a
     * session in listening (server) mode. Called by the Activity onResume()
     */
    public synchronized void start()
    {
        cancelConnectingThread();
        cancelConnectedThread();
        startAcceptThread();

        updateConnectStatus();
    }

    public synchronized void stop()
    {
        cancelConnectingThread();
        cancelConnectedThread();
        cancelAcceptThread();

        mConnectStatus = Constants.STATE_NONE;
        updateConnectStatus();
    }

    public void write(byte[] out)
    {
        ConnectedThread connectedThread = null;

        synchronized (this)
        {
            if(getConnectStatus() == Constants.STATE_CONNECTED)
            {
                return;
            }
            connectedThread = mConnectedThread;
        }
        connectedThread.write(out);
    }

    private void connectionFailed()
    {
        mWeakReferenceHandler.obtainMessage(Constants.MESSAGE_TOAST, -1,-1, mContext.getResources().getString(R.string.message_connection_failed));
        mConnectStatus = Constants.STATE_NONE;
        updateConnectStatus();

        start();
    }

    private void connectionLost()
    {
        mWeakReferenceHandler.obtainMessage(Constants.MESSAGE_TOAST, -1,-1, mContext.getResources().getString(R.string.message_connection_lost));
        mConnectStatus = Constants.STATE_NONE;
        updateConnectStatus();

        start();
    }

    @Override
    public synchronized void sendConnectStatus(int status)
    {
        if(status == Constants.STATE_CONNECTION_FAILED)
        {
            connectionFailed();
        }
        else if(status == Constants.STATE_CONNECTION_LOST)
        {
            connectionLost();
        }
        else
        {
            mConnectStatus = status;
        }

    }

    @Override
    public void startDiscovery()
    {
        mBluetoothAdapter.startDiscovery();
    }

    @Override
    public void cancelDiscovery()
    {
        mBluetoothAdapter.cancelDiscovery();
    }

    @Override
    public void connecting(BluetoothDevice device)
    {
        cancelConnectingThread();
        cancelConnectedThread();
        startConnectingThread(device);
    }

    @Override
    public void connected(BluetoothSocket socket, BluetoothDevice device)
    {
        cancelConnectingThread();
        cancelConnectedThread();
        cancelAcceptThread();

        startConnectedThread(socket);

        mWeakReferenceHandler.obtainMessage(Constants.MESSAGE_DEVICE_NAME, -1,-1, device.getName());
        updateConnectStatus();
    }

    @Override
    public void sendMessage(int messageType, MessageObject object)
    {

    }

    @Override
    public BluetoothAdapter getBluetoothAdapter()
    {
        return mBluetoothAdapter;
    }

    @Override
    public synchronized int getConnectStatus()
    {
        return mConnectStatus;
    }

    @Override
    public void destroyConnectingThread()
    {

    }
}
