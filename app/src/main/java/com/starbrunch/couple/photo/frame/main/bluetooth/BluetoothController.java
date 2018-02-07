package com.starbrunch.couple.photo.frame.main.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;

import com.littlefox.logmonitor.Log;
import com.starbrunch.couple.photo.frame.main.R;
import com.starbrunch.couple.photo.frame.main.bluetooth.listener.BluetoothThreadCallback;
import com.starbrunch.couple.photo.frame.main.bluetooth.thread.AcceptThread;
import com.starbrunch.couple.photo.frame.main.bluetooth.thread.ConnectedFileTransferThread;
import com.starbrunch.couple.photo.frame.main.bluetooth.thread.ConnectedInformationThread;
import com.starbrunch.couple.photo.frame.main.bluetooth.thread.ConnectingThread;
import com.starbrunch.couple.photo.frame.main.common.Common;
import com.starbrunch.couple.photo.frame.main.common.CommonUtils;
import com.starbrunch.couple.photo.frame.main.contract.presenter.MainContainerPresent;
import com.starbrunch.couple.photo.frame.main.handler.WeakReferenceHandler;
import com.starbrunch.couple.photo.frame.main.object.MessageObject;

import java.util.Set;
import java.util.UUID;

/**
 * Created by 정재현 on 2018-01-18.
 */

public class BluetoothController implements BluetoothThreadCallback
{

    public static final String SERVICE_NAME = "lophoco_book_service";
    public static final UUID RFCCMM_UUID = UUID.fromString("00000003-0000-1000-8000-00805F9B34FB");


    public static final int MESSAGE_INFORMATION_READ    = 0;
    public static final int MESSAGE_DATA_READ_PERCENT   = 101;
    public static final int MESSAGE_DATA_WRITE_PERCENT  = 102;

    public static final int MAX_PERCENT = 100;


    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

    // Constants that indicate the current connection state
    public static final int STATE_NONE                      = 0; // we're doing nothing
    public static final int STATE_LISTEN                    = 1; // now listening for incoming connections
    public static final int STATE_CONNECTING                = 2; // now initiating an outgoing connection
    public static final int STATE_CONNECTED                 = 3; // now connected to a remote device
    public static final int STATE_CONNECTED_FILE_TRANSFER   = 4; // now ready to send client file

    public static final int STATE_CONNECTION_FAILED = 101;
    public static final int STATE_CONNECTION_LOST   = 102;


    private BluetoothAdapter mBluetoothAdapter = null;
    private int mConnectStatus = STATE_NONE;
    private int mNewConnectStatus = STATE_NONE;

    private AcceptThread mAcceptThread                                  = null;
    private ConnectingThread mConnectingThread                          = null;
    private ConnectedInformationThread mConnectedInformationThread      = null;
    private ConnectedFileTransferThread mConnectedFileTransferThread    = null;
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
            case STATE_CONNECTED_FILE_TRANSFER:
                Log.f("Ready to send File or receive File "+ mConnectDeviceName);
                break;
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
            mAcceptThread = new AcceptThread(mBluetoothAdapter,this);
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

    private void startConnectedInformationThread(BluetoothSocket socket)
    {
        if(mConnectedInformationThread == null)
        {
            mConnectedInformationThread = new ConnectedInformationThread(socket, this);
            mConnectedInformationThread.start();
        }
    }

    private void cancelConnectedInformationThread()
    {
        if(mConnectedInformationThread != null)
        {
            mConnectedInformationThread.cancel();
            mConnectedInformationThread = null;
        }
    }

    private void startConnectedFileTransferThread(BluetoothSocket socket)
    {
        if(mConnectedFileTransferThread == null)
        {
            long fileSize = (long) CommonUtils.getInstance(mContext).getSharedPreference(Common.PREFERENCE_SEND_FILE_SIZE, Common.TYPE_PARAMS_LONG);

            mConnectedFileTransferThread = new ConnectedFileTransferThread(socket,fileSize, this);
            mConnectedFileTransferThread.start();
        }
    }

    private void cancelConnectedFileTransferThread()
    {
        if(mConnectedFileTransferThread != null)
        {
            mConnectedFileTransferThread.cancel();
            mConnectedFileTransferThread = null;
        }
    }

    /**
     * Start the chat service. Specifically start AcceptThread to begin a
     * session in listening (server) mode. Called by the Activity onResume()
     */
    public synchronized void start()
    {
        cancelConnectingThread();
        cancelConnectedInformationThread();
        cancelConnectedFileTransferThread();
        startAcceptThread();

        updateConnectStatus();
    }

    public synchronized void stop()
    {
        cancelConnectingThread();
        cancelConnectedInformationThread();
        cancelConnectedFileTransferThread();
        cancelAcceptThread();

        mConnectStatus = STATE_NONE;
        updateConnectStatus();
    }

    public void writeInformation(byte[] out)
    {
        ConnectedInformationThread connectedInformationThread = null;

        synchronized (this)
        {
            if(getConnectStatus() == STATE_CONNECTED)
            {
                return;
            }
            connectedInformationThread = mConnectedInformationThread;
        }
        connectedInformationThread.write(out);
    }

    private void connectionFailed()
    {
        Message message = Message.obtain();
        message.what = MainContainerPresent.MESSAGE_BLUETOOTH_TOAST;
        message.obj = mContext.getResources().getString(R.string.message_connection_failed);
        mWeakReferenceHandler.sendMessage(message);
        mConnectStatus = STATE_NONE;
        updateConnectStatus();

        start();
    }

    private void connectionLost()
    {
        Message message = Message.obtain();
        message.what = MainContainerPresent.MESSAGE_BLUETOOTH_TOAST;
        message.obj = mContext.getResources().getString(R.string.message_connection_lost);
        mWeakReferenceHandler.sendMessage(message);
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

    public BluetoothDevice getRemoteDevice(String address)
    {
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        return device;
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
        cancelConnectedInformationThread();
        startConnectingThread(device);
        updateConnectStatus();
    }

    @Override
    public void connectedInformation(BluetoothSocket socket, BluetoothDevice device)
    {
        Log.i("");
        cancelConnectingThread();
        cancelConnectedInformationThread();
        cancelConnectedFileTransferThread();
        cancelAcceptThread();

        startConnectedInformationThread(socket);

        mConnectDeviceName = device.getName();

        Message message = Message.obtain();
        message.what = MainContainerPresent.MESSAGE_BLUETOOTH_TOAST;
        message.obj = mContext.getResources().getString(R.string.message_connect_to_device)+" "+device.getName();

        mWeakReferenceHandler.sendMessage(message);
        updateConnectStatus();
    }

    @Override
    public void connectedFileTransfer(BluetoothSocket socket, BluetoothDevice device)
    {
        Log.i("");
        cancelConnectingThread();
        cancelConnectedInformationThread();
        cancelConnectedFileTransferThread();
        cancelAcceptThread();

        startConnectedFileTransferThread(socket);

        mConnectDeviceName = device.getName();

        Message message = Message.obtain();
        message.what = MainContainerPresent.MESSAGE_BLUETOOTH_TOAST;
        message.obj = mContext.getResources().getString(R.string.message_ready_to_transfer_file)+" "+device.getName();

        mWeakReferenceHandler.sendMessage(message);
        updateConnectStatus();
    }

    @Override
    public void sendMessage(int messageType, MessageObject object)
    {
        Message message = null;
        switch(messageType)
        {
            case MESSAGE_INFORMATION_READ:
                break;
            case MESSAGE_DATA_WRITE_PERCENT:
                if(object.argument1 == MAX_PERCENT)
                {
                    mWeakReferenceHandler.sendEmptyMessage(MainContainerPresent.MESSAGE_BLUETOOTH_DATA_WRITE_COMPLETE);
                }
                else
                {
                    message= Message.obtain();
                    message.what = MainContainerPresent.MESSAGE_BLUETOOTH_DATA_WRITE;
                    message.arg1 = object.argument1;
                    mWeakReferenceHandler.sendMessage(message);
                }
                break;
            case MESSAGE_DATA_READ_PERCENT:


                if(object.argument1 == MAX_PERCENT)
                {
                    mWeakReferenceHandler.sendEmptyMessage(MainContainerPresent.MESSAGE_BLUETOOTH_DATA_READ_COMPLETE);
                }
                else
                {
                    message = Message.obtain();
                    message.what = MainContainerPresent.MESSAGE_BLUETOOTH_DATA_WRITE;
                    message.arg1 = object.argument1;
                    mWeakReferenceHandler.sendMessage(message);
                }
                break;
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
