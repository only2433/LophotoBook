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
import com.starbrunch.couple.photo.frame.main.bluetooth.thread.ConnectedThread;
import com.starbrunch.couple.photo.frame.main.bluetooth.thread.ConnectingThread;
import com.starbrunch.couple.photo.frame.main.common.Common;
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
    public static final UUID RFCCMM_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");


    public static final int MESSAGE_INFORMATION_READ    = 0;
    public static final int MESSAGE_READ_PERCENT_UI     = 1;
    public static final int MESSAGE_SEND_PERCENT_UI     = 2;

    public static final int MAX_PERCENT = 100;

    public static final int READ_TYPE_MESSAGE   = 0;
    public static final int READ_TYPE_FILE      = 1;


    // Constants that indicate the current connection state
    public static final int STATE_NONE                      = 0; // we're doing nothing
    public static final int STATE_LISTEN                    = 1; // now listening for incoming connections
    public static final int STATE_CONNECTING                = 2; // now initiating an outgoing connection
    public static final int STATE_CONNECTED                 = 3; // now connected to a remote device


    public static final int STATE_CONNECTION_FAILED = 101;
    public static final int STATE_CONNECTION_LOST   = 102;

    private BluetoothAdapter mBluetoothAdapter = null;
    private int mConnectStatus = STATE_NONE;
    private int mNewConnectStatus = STATE_NONE;

    private AcceptThread mAcceptThread                                  = null;
    private ConnectingThread mConnectingThread                          = null;
    private ConnectedThread mConnectedThread      = null;

    private WeakReferenceHandler mWeakReferenceHandler  = null;
    private Context mContext = null;
    private String mConnectDeviceName = "";
    private BluetoothSocket mCurrentBluetoothSocket = null;
    private BluetoothDevice mCurrentBluetoothDevice = null;


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
        mConnectStatus = STATE_NONE;
        updateConnectStatus();

        cancelConnectingThread();
        cancelConnectedThread();
        cancelAcceptThread();
    }

    public void writeInformation(byte[] out)
    {
        Log.i("");
        ConnectedThread  connectedThread= null;

        synchronized (this)
        {
            if(getConnectStatus() != STATE_CONNECTED)
            {
                return;
            }
            connectedThread = mConnectedThread;
        }

        try
        {
            connectedThread.sendMessage(out);
        }catch(NullPointerException e)
        {
            Log.f("Exception e : "+ e.getMessage());
        }
    }


    private void connectionFailed()
    {
        Message message = Message.obtain();
        message.what = MainContainerPresent.MESSAGE_BLUETOOTH_TOAST;
        message.obj = mContext.getResources().getString(R.string.message_connection_failed);
        mWeakReferenceHandler.sendMessage(message);
        mConnectStatus = STATE_NONE;
        updateConnectStatus();

        stop();

        /**
         * 가끔 연결이 안될때가 있다. 히밤. 연결실패시 해결 대책 필요.
         */
        mWeakReferenceHandler.sendEmptyMessageDelayed(MainContainerPresent.MESSAGE_BLUETOOTH_CONNECTION_FAIL, Common.DURATION_LONGER);
    }

    private void connectionLost()
    {
        Message message = Message.obtain();
        message.what = MainContainerPresent.MESSAGE_BLUETOOTH_TOAST;
        message.obj = mContext.getResources().getString(R.string.message_connection_lost);
        mWeakReferenceHandler.sendMessage(message);
        mConnectStatus = STATE_NONE;
        updateConnectStatus();
        stop();

        /**
         * 가끔 연결이 안될때가 있다. 히밤. 연결이 되었는데 연결을 잃어버렸을때는 데이터 관련 프레그먼트를 닫아줘야한다.
         */
        mWeakReferenceHandler.sendEmptyMessageDelayed(MainContainerPresent.MESSAGE_BLUETOOTH_CONNECTION_LOST, Common.DURATION_LONGER);
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
        cancelConnectedThread();
        startConnectingThread(device);
        updateConnectStatus();
    }

    @Override
    public void connectedInformation(BluetoothSocket socket, BluetoothDevice device)
    {
        Log.i("");
        mCurrentBluetoothSocket = socket;
        mCurrentBluetoothDevice = device;

        cancelConnectingThread();
        cancelConnectedThread();
        cancelAcceptThread();

        startConnectedThread(mCurrentBluetoothSocket);

        mConnectDeviceName = mCurrentBluetoothDevice.getName();

        Message toastMessage = Message.obtain();
        toastMessage.what = MainContainerPresent.MESSAGE_BLUETOOTH_TOAST;
        toastMessage.obj = mContext.getResources().getString(R.string.message_connect_to_device)+" "+mCurrentBluetoothDevice.getName();

        mWeakReferenceHandler.sendMessage(toastMessage);

        updateConnectStatus();
        mWeakReferenceHandler.sendEmptyMessageDelayed(MainContainerPresent.MESSAGE_BLUETOOTH_LINK_COMPLETE, Common.DURATION_LONGER);

    }


    /**
     * SEND 하는 사람이 파일을 보낼때 사용
     * @param filePath 보낼 파일 위치
     */
    public synchronized void sendFile(final String filePath)
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mConnectedThread.writeFile(filePath);
            }
        }).start();



    }

    /**
     * Connected Thread의 데이터 읽는 형태를 메세지를 보내는 형태로 변경
     */
    public synchronized void setConnectedMessage()
    {
        mConnectedThread.setType(READ_TYPE_MESSAGE);
    }

    /**
     * Connected Thread의 데이터 읽는 형태를 파일쓰는 형태로 변경
     */
    public synchronized void setConnectedFile()
    {
        mConnectedThread.setType(READ_TYPE_FILE);
    }

    /**
     * 파일을 받는 정보 및 받을 파일 사이즈를 전달한다.
     * @param path  파일 위치
     * @param fileSize  파일 사이즈
     */
    public synchronized void setReadFileInformation(String path, long fileSize)
    {
        mConnectedThread.makeFileInformation(path, fileSize);
    }




    @Override
    public void sendMessage(MessageObject object)
    {
        Message message = null;
        switch(object.code)
        {
            case MESSAGE_INFORMATION_READ:
                message = Message.obtain();
                message.what = MainContainerPresent.MESSAGE_BLUETOOTH_INFORMATION_READ;
                message.arg1 = object.argument1;
                message.obj  = object.data;

                mWeakReferenceHandler.sendMessage(message);
                break;
            case MESSAGE_READ_PERCENT_UI:

                if(object.argument1 == MAX_PERCENT)
                {
                    mWeakReferenceHandler.sendEmptyMessage(MainContainerPresent.MESSAGE_BLUETOOTH_DATA_RECEIVE_COMPLETE);
                }
                else
                {
                    message = Message.obtain();
                    message.what = MainContainerPresent.MESSAGE_BLUETOOTH_DATA_RECEIVE_UI;
                    message.arg1 = object.argument1;
                    mWeakReferenceHandler.sendMessage(message);
                }
                break;
            case MESSAGE_SEND_PERCENT_UI:
                if(object.argument1 == MAX_PERCENT)
                {
                    mWeakReferenceHandler.sendEmptyMessage(MainContainerPresent.MESSAGE_BLUETOOTH_DATA_SEND_COMPLETE);
                }
                else
                {
                    message = Message.obtain();
                    message.what = MainContainerPresent.MESSAGE_BLUETOOTH_DATA_SEND_UI;
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
