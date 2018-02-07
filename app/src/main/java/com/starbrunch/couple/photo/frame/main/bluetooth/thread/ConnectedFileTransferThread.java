package com.starbrunch.couple.photo.frame.main.bluetooth.thread;

import android.bluetooth.BluetoothSocket;

import com.littlefox.logmonitor.Log;
import com.starbrunch.couple.photo.frame.main.bluetooth.BluetoothController;
import com.starbrunch.couple.photo.frame.main.bluetooth.listener.BluetoothThreadCallback;
import com.starbrunch.couple.photo.frame.main.common.Common;
import com.starbrunch.couple.photo.frame.main.object.MessageObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by only340 on 2018-02-06.
 */

public class ConnectedFileTransferThread extends Thread
{
    private BluetoothThreadCallback mBluetoothThreadCallback = null;
    private final BluetoothSocket mBluetoothSocket;
    private final InputStream mInputStream;
    private final OutputStream mOutputStream;
    private long mTotalFileSize = 0L;

    public ConnectedFileTransferThread(BluetoothSocket socket, long fileSize , BluetoothThreadCallback bluetoothThreadCallback)
    {
        mBluetoothThreadCallback = bluetoothThreadCallback;
        mTotalFileSize = fileSize;
        mBluetoothSocket = socket;
        InputStream inputStream = null;
        OutputStream outputStream = null;

        try
        {
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();
        }catch(Exception e)
        {
            Log.f("Message : "+ e.getMessage());
        }
        mInputStream = inputStream;
        mOutputStream = outputStream;
        mBluetoothThreadCallback.sendConnectStatus(BluetoothController.STATE_CONNECTED_FILE_TRANSFER);
    }

    @Override
    public void run()
    {
        byte[] buffer = new byte[1024];
        File file = new File(Common.PATH_EXTERNAL_ZIP_ROOT+Common.ZIP_FILE_NAME);
        File parentPath = new File(file.getParent());
        OutputStream outputStream = null;
        long currentFileSize = 0;
        int fileLength = 0;
        int percent = 0;

        if(parentPath.exists() == false)
        {
            parentPath.mkdirs();
        }

        try
        {
            file.createNewFile();
            outputStream = new FileOutputStream(file);

            while(currentFileSize < mTotalFileSize)
            {
                fileLength = mInputStream.read(buffer);

                if(fileLength > 0)
                {
                    currentFileSize += fileLength;
                    percent = (int) (currentFileSize * 100 / mTotalFileSize);
                    outputStream.write(buffer, 0, fileLength);

                    MessageObject object = new MessageObject();
                    object.argument1 = percent;
                    mBluetoothThreadCallback.sendMessage(BluetoothController.MESSAGE_DATA_READ_PERCENT, object);
                }
                else
                {
                    Log.f("Read received -1, break.");
                    break;
                }
            }
            outputStream.close();

        }
        catch (IOException e) {
            e.printStackTrace();
        }


    }

    /**
     * 파일을 보내는 로직. 파일을 미리 만들어 놓은 걸 보내야한다.
     * @param file
     */
    public void write(File file)
    {
        int fileLength = 0;
        int currentFileSize = 0;
        int percent = 0;
        long totalFileSize = file.length();
        byte[] buffer = new byte[1024];
        try {
            InputStream inputStream = new FileInputStream(file);

            while((fileLength = inputStream.read(buffer, 0, buffer.length)) > 0)
            {
                currentFileSize += fileLength;
                percent = (int) (currentFileSize * 100 / totalFileSize);
                mOutputStream.write(buffer, 0 , fileLength);

                MessageObject object = new MessageObject();
                object.argument1 = percent;
                mBluetoothThreadCallback.sendMessage(BluetoothController.MESSAGE_DATA_WRITE_PERCENT, object);

            }
            inputStream.close();
        } catch (FileNotFoundException e)
        {
            Log.f("Exception : "+ e.getMessage());
        }
        catch (IOException e)
        {
            Log.f("Exception : "+ e.getMessage());
        }

    }

    public void cancel()
    {
        try
        {
            mBluetoothSocket.close();
        }catch(Exception e)
        {
            Log.f("Exception : "+ e.getMessage());
        }
    }
}
