package com.starbrunch.couple.photo.frame.main.object;

/**
 * Created by only340 on 2018-01-29.
 */

public class BluetoothScanInformation
{
    private String scanDeviceName = "";
    private String scanDeviceAddress = "";

    public BluetoothScanInformation(String scanDeviceName, String scanDeviceAddress)
    {
        this.scanDeviceName = scanDeviceName;
        this.scanDeviceAddress = scanDeviceAddress;
    }

    public String getScanDeviceName()
    {
        return scanDeviceName;
    }

    public String getScanDeviceAddress()
    {
        return scanDeviceAddress;
    }
}
