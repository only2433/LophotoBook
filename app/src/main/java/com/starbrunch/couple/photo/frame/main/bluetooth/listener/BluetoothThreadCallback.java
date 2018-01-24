package com.starbrunch.couple.photo.frame.main.bluetooth.listener;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import com.starbrunch.couple.photo.frame.main.object.MessageObject;

/**
 * Created by 정재현 on 2018-01-18.
 */

public interface BluetoothThreadCallback
{
    public void sendConnectStatus(int status);
    public void startDiscovery();
    public void cancelDiscovery();
    public void connecting(BluetoothDevice device);
    public void connected(BluetoothSocket socket, BluetoothDevice device);
    public void sendMessage(int messageType, MessageObject object);
    public BluetoothAdapter getBluetoothAdapter();
    public int getConnectStatus();
    public void destroyConnectingThread();

}