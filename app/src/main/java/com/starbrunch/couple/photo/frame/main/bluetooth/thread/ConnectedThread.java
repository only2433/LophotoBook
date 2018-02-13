package com.starbrunch.couple.photo.frame.main.bluetooth.thread;

import android.bluetooth.BluetoothSocket;

import com.littlefox.logmonitor.Log;
import com.starbrunch.couple.photo.frame.main.bluetooth.BluetoothController;
import com.starbrunch.couple.photo.frame.main.bluetooth.listener.BluetoothThreadCallback;
import com.starbrunch.couple.photo.frame.main.object.MessageObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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
    private long mMaxFileSize = 0;

    private int mReadType = BluetoothController.READ_TYPE_MESSAGE;
    private File mDownloadFile = null;
    private OutputStream mFileOutputStream = null;
    private long mFileMaxSize = 0L;

    public ConnectedThread(BluetoothSocket socket, BluetoothThreadCallback bluetoothThreadCallback)
    {
        mReadType = BluetoothController.READ_TYPE_MESSAGE;
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
        int percent = 0;
        long currentFileSize = 0;
        long fileMaxSize = 0;

        while ((mBluetoothThreadCallback.getConnectStatus() == BluetoothController.STATE_CONNECTED))
        {

            try {
                bytes = mInputStream.read(buffer);

                if(mReadType == BluetoothController.READ_TYPE_MESSAGE)
                {
                    MessageObject object = new MessageObject();
                    object.code = BluetoothController.MESSAGE_INFORMATION_READ;
                    object.argument1 = bytes;
                    object.data = buffer;

                    mBluetoothThreadCallback.sendMessage(object);
                }
                else if(mReadType == BluetoothController.READ_TYPE_FILE)
                {
                    currentFileSize += bytes;
                    percent = (int) (currentFileSize * 100 / mFileMaxSize);
                    mFileOutputStream.write(buffer, 0, bytes);

                    Log.i("currentFileSize : "+ currentFileSize+", mTargetFileSize : "+mFileMaxSize+", fileLength : "+bytes);

                    MessageObject object = new MessageObject();
                    object.code = BluetoothController.MESSAGE_READ_PERCENT_UI;
                    object.argument1 = percent;
                    mBluetoothThreadCallback.sendMessage(object);


                }

            } catch (Exception e) {
                Log.f("Exception : " + e.getMessage());
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

    public void writeFile(String path)
    {
        int fileLength = 0;
        int currentFileSize = 0;
        int percent = 0;
        File file = new File(path);

        long totalFileSize = file.length();
        byte[] buffer = new byte[1024];

        try {
            InputStream inputStream = new FileInputStream(file);

            while((fileLength = inputStream.read(buffer, 0, buffer.length)) >= 0)
            {
                currentFileSize += fileLength;
                Log.i("currentFileSize : "+ currentFileSize+", mTargetFileSize : "+totalFileSize+", fileLength : "+fileLength);

                percent = (int) (currentFileSize * 100 / totalFileSize);
                mOutputStream.write(buffer, 0 , fileLength);

                MessageObject object = new MessageObject();
                object.code = BluetoothController.MESSAGE_SEND_PERCENT_UI;
                object.argument1 = percent;
                mBluetoothThreadCallback.sendMessage(object);

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

    public void setType(int type)
    {
        mReadType = type;
    }

    public void makeFileInformation(String path, long fileSize)
    {
        mFileMaxSize = fileSize;
        mDownloadFile = new File(path);
        File parentPath = new File(mDownloadFile.getParent());
        if(parentPath.exists() == false)
        {
            parentPath.mkdirs();
        }

        if(mDownloadFile.exists())
        {
            mDownloadFile.delete();
        }
        try
        {
            mDownloadFile.createNewFile();
            mFileOutputStream = new FileOutputStream(mDownloadFile);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
