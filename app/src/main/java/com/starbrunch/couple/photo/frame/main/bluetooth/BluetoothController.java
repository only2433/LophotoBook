package com.starbrunch.couple.photo.frame.main.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import com.littlefox.logmonitor.Log;
import com.starbrunch.couple.photo.frame.main.R;
import com.starbrunch.couple.photo.frame.main.bluetooth.listener.BluetoothThreadCallback;
import com.starbrunch.couple.photo.frame.main.bluetooth.thread.AcceptThread;
import com.starbrunch.couple.photo.frame.main.bluetooth.thread.ConnectedThread;
import com.starbrunch.couple.photo.frame.main.bluetooth.thread.ConnectingThread;
import com.starbrunch.couple.photo.frame.main.contract.presenter.MainContainerPresent;
import com.starbrunch.couple.photo.frame.main.handler.WeakReferenceHandler;
import com.starbrunch.couple.photo.frame.main.object.MessageObject;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

/**
 * Created by 정재현 on 2018-01-18.
 */

public class BluetoothController implements BluetoothThreadCallback
{

    public static final String SERVICE_NAME = "lophoco_book_service";
    public static final UUID RFCCMM_UUID = UUID.fromString("00000003-0000-1000-8000-00805F9B34FB");


    public static final int MESSAGE_READ    = 0;
    public static final int MESSAGE_WRITE   = 1;


    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

    // Constants that indicate the current connection state
    public static final int STATE_NONE              = 0; // we're doing nothing
    public static final int STATE_LISTEN            = 1; // now listening for incoming connections
    public static final int STATE_CONNECTING        = 2; // now initiating an outgoing connection
    public static final int STATE_CONNECTED         = 3; // now connected to a remote device
    public static final int STATE_CONNECTION_FAILED = 101;
    public static final int STATE_CONNECTION_LOST   = 102;


    private BluetoothAdapter mBluetoothAdapter = null;
    private int mConnectStatus = STATE_NONE;
    private int mNewConnectStatus = STATE_NONE;

    private AcceptThread mAcceptThread                  = null;
    private ConnectingThread mConnectingThread          = null;
    private ConnectedThread mConnectedThread            = null;
    private WeakReferenceHandler mWeakReferenceHandler  = null;
    private Context mContext = null;
    private String mConnectDeviceName = "";


    public BluetoothController(Context context, WeakReferenceHandler handler)
    {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mContext = context;
        mWeakReferenceHandler = handler;
        mConnectStatus = STATE_NONE;
        mNewConnectStatus = mConnectStatus;
    }

    private synchronized void updateConnectStatus()
    {
        mConnectStatus = getConnectStatus();
        mNewConnectStatus = mConnectStatus;

        //TODO: 스테이트가 변경될때마다 데이터를 전달할 필요가 있을때 사용
        switch (mNewConnectStatus)
        {
            case STATE_CONNECTED:
                Log.f("Connected to "+ mConnectDeviceName);
                break;
            case STATE_CONNECTING:
                Log.f("Connecting..");
                break;
            case STATE_LISTEN:
                Log.f("Listen.. not connected");
                break;
            case STATE_NONE:
                Log.f("not connected");
                break;

        }

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

        mConnectStatus = STATE_NONE;
        updateConnectStatus();
    }

    public void write(byte[] out)
    {
        ConnectedThread connectedThread = null;

        synchronized (this)
        {
            if(getConnectStatus() == STATE_CONNECTED)
            {
                return;
            }
            connectedThread = mConnectedThread;
        }
        connectedThread.write(out);
    }

    private void connectionFailed()
    {
        mWeakReferenceHandler.obtainMessage(MainContainerPresent.MESSAGE_BLUETOOTH_TOAST, -1,-1, mContext.getResources().getString(R.string.message_connection_failed));
        mConnectStatus = STATE_NONE;
        updateConnectStatus();

        start();
    }

    private void connectionLost()
    {
        mWeakReferenceHandler.obtainMessage(MainContainerPresent.MESSAGE_BLUETOOTH_TOAST, -1,-1, mContext.getResources().getString(R.string.message_connection_lost));
        mConnectStatus = STATE_NONE;
        updateConnectStatus();
        start();
    }

    public boolean isBluetoothEnable()
    {
        return mBluetoothAdapter.isEnabled();
    }

    public void startBluetoothEnable(int requestCode)
    {
        Log.i("");
        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        ((AppCompatActivity)mContext).startActivityForResult(intent, requestCode);
    }

    public boolean isConnectDiscoverable()
    {
        if(mBluetoothAdapter.getScanMode() == BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public void startConnectDiscoverable(int requestCode)
    {
        Log.i("");
        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
        ((AppCompatActivity)mContext).startActivityForResult(discoverableIntent, requestCode);
    }

    @Override
    public synchronized void sendConnectStatus(int status)
    {
        if(status == STATE_CONNECTION_FAILED)
        {
            connectionFailed();
        }
        else if(status == STATE_CONNECTION_LOST)
        {
            connectionLost();
        }
        else
        {
            mConnectStatus = status;
        }

    }

    public boolean isStartDiscovering()
    {
        return mBluetoothAdapter.isDiscovering();
    }

    public Set<BluetoothDevice> getBondedDevices()
    {
        return mBluetoothAdapter.getBondedDevices();
    }

    @Override
    public void startDiscovery()
    {
        if(mBluetoothAdapter != null)
        {
            mBluetoothAdapter.startDiscovery();
        }
    }

    @Override
    public void cancelDiscovery()
    {
        if(mBluetoothAdapter != null)
        {
            mBluetoothAdapter.cancelDiscovery();
        }
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

        mConnectDeviceName = device.getName();
        mWeakReferenceHandler.obtainMessage(MainContainerPresent.MESSAGE_BLUETOOTH_TOAST, -1,-1,
                mContext.getResources().getString(R.string.message_connect_to_device)+" "+device.getName());
        updateConnectStatus();
    }

    @Override
    public void sendMessage(int messageType, MessageObject object)
    {
        switch(messageType)
        {
            case MESSAGE_READ:
                break;
            case MESSAGE_WRITE:
                break;
        }
    }

    @Override
    public BluetoothServerSocket listenService(String serviceName, UUID serviceUUID)
    {
        try
        {
            return mBluetoothAdapter.listenUsingRfcommWithServiceRecord(serviceName, serviceUUID);
        }
        catch (IOException e)
        {
            Log.f("Exception : "+ e.getMessage());
            return null;
        }
    }

    @Override
    public synchronized int getConnectStatus()
    {
        return mConnectStatus;
    }

    @Override
    public synchronized void destroyConnectingThread()
    {
        mConnectingThread = null;
    }
}
