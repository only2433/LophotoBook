package com.starbrunch.couple.photo.frame.main.handler;

import android.os.Handler;
import android.os.Message;

import com.starbrunch.couple.photo.frame.main.handler.callback.MessageHandlerCallback;

import java.lang.ref.WeakReference;

/**
 * Created by 정재현 on 2017-12-28.
 */

public class WeakReferenceHandler extends Handler
{
    private WeakReference<MessageHandlerCallback> mHandlerActivity = null;

    public WeakReferenceHandler(MessageHandlerCallback activity)
    {
        mHandlerActivity = new WeakReference<MessageHandlerCallback>(activity);
    }

    @Override
    public void handleMessage(Message msg)
    {
        super.handleMessage(msg);

        MessageHandlerCallback result = (MessageHandlerCallback)mHandlerActivity.get();

        if(result == null)
        {
            return;
        }

        result.handlerMessage(msg);
    }
}
