package com.starbrunch.couple.photo.frame.main.async;

import android.content.Context;

import com.littlefox.library.system.async.BaseAsync;
import com.starbrunch.couple.photo.frame.main.common.Common;
import com.starbrunch.couple.photo.frame.main.common.Compressor;


/**
 * Created by only340 on 2018-01-31.
 */

public class CompressorAsync extends BaseAsync
{
    private String mTargetPath          = "";
    private String mDestinationFilePath = "";
    public CompressorAsync(Context context)
    {
        super(context, Common.ASYNC_COMPRESSOR);
    }

    @Override
    public void setData(Object... objects)
    {
        mTargetPath             = (String) objects[0];
        mDestinationFilePath    = (String) objects[1];
    }

    @Override
    protected Object doInBackground(Void... voids)
    {
        if(isRunning == true)
        {
            return  false;
        }

        boolean result = false;

        synchronized (mSync)
        {
            isRunning = true;
            result = Compressor.zip(mTargetPath, mDestinationFilePath);
        }
        return result;
    }
}
