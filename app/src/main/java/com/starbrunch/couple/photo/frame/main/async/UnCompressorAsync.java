package com.starbrunch.couple.photo.frame.main.async;

import android.content.Context;

import com.littlefox.library.system.async.BaseAsync;
import com.littlefox.logmonitor.Log;
import com.starbrunch.couple.photo.frame.main.common.Common;
import com.starbrunch.couple.photo.frame.main.common.Compressor;


/**
 * Created by only340 on 2018-01-31.
 */

public class UnCompressorAsync extends BaseAsync
{
    private String mTargetZipFilePath = "";
    private String mDestinationFolderPath = "";
    public UnCompressorAsync(Context context)
    {
        super(context, Common.ASYNC_UNCOMPRESSOR);
    }

    @Override
    public void setData(Object... objects)
    {
        mTargetZipFilePath     = (String) objects[0];
        mDestinationFolderPath = (String) objects[1];
    }

    @Override
    protected Object doInBackground(Void... voids)
    {
        if(isRunning == true)
        {
            return false;
        }
        boolean result = false;

        synchronized (mSync)
        {
            isRunning = true;

            result = Compressor.unzip(mTargetZipFilePath, mDestinationFolderPath);
        }
        Log.i("result : "+result);
        return result;
    }
}
