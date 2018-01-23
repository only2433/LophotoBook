package com.starbrunch.couple.photo.frame.main.bluetooth.common;

import java.util.UUID;

/**
 * Created by 정재현 on 2018-01-19.
 */

public class Constants
{
    public static final String SERVICE_NAME = "lophoco_book_service";
    public static final UUID RFCCMM_UUID = UUID.fromString("00000003-0000-1000-8000-00805F9B34FB");

    // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;

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
}
