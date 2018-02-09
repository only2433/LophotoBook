package com.starbrunch.couple.photo.frame.main.bluetooth.thread;

import android.bluetooth.BluetoothSocket;

import com.littlefox.logmonitor.Log;
import com.starbrunch.couple.photo.frame.main.bluetooth.BluetoothController;
import com.starbrunch.couple.photo.frame.main.bluetooth.listener.BluetoothThreadCallback;
import com.starbrunch.couple.photo.frame.main.object.MessageObject;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by 정재현 on 2018-01-18.
 */

public class ConnectedThread extends Thread
{
    private BluetoothThreadCallback mBluetoothThreadCallback = null;
    private final BluetoothSocket mBluetoothSocket;
    private final InputStream mInputStream;
    private final OutputStream mOutputStream;

    private boolean isCancel = false;

    public ConnectedThread(BluetoothSocket socket, BluetoothThreadCallback bluetoothThreadCallback)
    {
        mBluetoothThreadCallback = bluetoothThreadCallback;
        mBluetoothSocket = socket;
        InputStream inputStream = null;
        OutputStream outputStream = null;

        try
        {
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();
        }catch(Exception e)
        {
            Log.f("Exception : "+ e.getMessage());
        }
        mInputStream = inputStream;
        mOutputStream = outputStream;
        mBluetoothThreadCallback.sendConnectStatus(BluetoothController.STATE_CONNECTED);
    }

    @Override
    public void run()
    {
        super.run();
        Log.f("BEGIN ConnectedThread");
        byte[] buffer = new byte[1024];
        int bytes;

        while((mBluetoothThreadCallback.getConnectStatus() == BluetoothController.STATE_CONNECTED)
                && !isCancel)
        {
            try
            {
                bytes = mInputStream.read(buffer);
                MessageObject object = new MessageObject();
                object.argument1 = bytes;
                object.data = buffer;

                mBluetoothThreadCallback.sendMessage(BluetoothController.MESSAGE_INFORMATION_READ, object);
            }catch(Exception e)
            {
                Log.f("Exception : "+ e.getMessage());
                mBluetoothThreadCallback.sendConnectStatus(BluetoothController.STATE_CONNECTION_LOST);
            }
        }
    }

    public void write(byte[] buffer)
    {
        Log.i("");
        try
        {
            mOutputStream.write(buffer);

        }catch(Exception e)
        {
            Log.f("Exception : "+ e.getMessage());
        }
    }

    public void cancel()
    {
        isCancel = true;
        try
        {
            mBluetoothSocket.close();
        }catch(Exception e)
        {
            Log.f("Exception : "+ e.getMessage());
        }
    }
}
