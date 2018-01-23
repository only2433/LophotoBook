package com.starbrunch.couple.photo.frame.main.bluetooth.thread;

import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;

import com.littlefox.logmonitor.Log;
import com.starbrunch.couple.photo.frame.main.bluetooth.common.Constants;
import com.starbrunch.couple.photo.frame.main.bluetooth.listener.BluetoothThreadCallback;


/**
 * This thread runs while listening for incoming connections. It behaves
 * like a server-side client. It runs until a connection is accepted
 * (or until cancelled).
 * Created by 정재현 on 2018-01-19.
 */
public class AcceptThread extends Thread
{
    private final BluetoothServerSocket mBluetoothServerSocket;
    private String mBluetoothServerSocketType = "";
    private BluetoothThreadCallback mBluetoothThreadCallback = null;

    public AcceptThread(BluetoothThreadCallback bluetoothThreadCallback)
    {
        BluetoothServerSocket bluetoothServerSocket = null;
        mBluetoothThreadCallback = bluetoothThreadCallback;

        try
        {
            bluetoothServerSocket = mBluetoothThreadCallback.getBluetoothAdapter().listenUsingInsecureRfcommWithServiceRecord(
                    Constants.SERVICE_NAME, Constants.RFCCMM_UUID);

        }catch(Exception e)
        {
            Log.f("Message : "+ e.getMessage());
        }
        mBluetoothServerSocket = bluetoothServerSocket;
        mBluetoothThreadCallback.sendConnectStatus(Constants.STATE_LISTEN);
    }

    @Override
    public void run()
    {
        super.run();
        Log.f("BEGIN AcceptThread : "+ this);
        setName("AcceptThread");

        BluetoothSocket socket = null;

        while(mBluetoothThreadCallback.getConnectStatus() != Constants.STATE_CONNECTED)
        {
            try
            {
                socket = mBluetoothServerSocket.accept();
            }catch(Exception e)
            {
                Log.f("Message : "+ e.getMessage());
            }

            if(socket != null)
            {
                synchronized (this)
                {
                    switch (mBluetoothThreadCallback.getConnectStatus())
                    {
                        case Constants.STATE_LISTEN:
                        case Constants.STATE_CONNECTING:
                            mBluetoothThreadCallback.connected(socket, socket.getRemoteDevice());
                            break;
                        case Constants.STATE_NONE:
                        case Constants.STATE_CONNECTED:
                            try
                            {
                                socket.close();
                            }catch (Exception e)
                            {
                                Log.f("Message : "+ e.getMessage());
                            }
                            break;
                    }
                }
            }
        }

        Log.f("END AcceptThread : "+ this);
    }

    public void cancel() {
        Log.f("CANCEL AcceptThread : " + this);

        try
        {
            mBluetoothServerSocket.close();
        } catch (Exception e)
        {
            Log.f("Message : "+ e.getMessage());
        }
    }
}
