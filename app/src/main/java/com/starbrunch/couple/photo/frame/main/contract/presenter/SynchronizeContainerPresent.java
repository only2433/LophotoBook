package com.starbrunch.couple.photo.frame.main.contract.presenter;

import android.content.Context;
import android.content.Intent;
import android.os.Message;

import com.starbrunch.couple.photo.frame.main.contract.SynchronizeContainerContract;

import java.util.TimerTask;

/**
 * Created by only340 on 2018-03-09.
 */

public class SynchronizeContainerPresent implements SynchronizeContainerContract.Presenter
{
    class SynchronizeTimerTask extends TimerTask
    {

        @Override
        public void run()
        {

        }
    }

    private static final int MESSAGE_SYNCHRONIZE_TIMER_CHECK = 0;

    private Context mContext = null;
    private SynchronizeContainerContract.View mSynchronizeContainerContractView = null;
    public SynchronizeContainerPresent(Context context)
    {
        mContext = context;
        mSynchronizeContainerContractView = (SynchronizeContainerContract.View)mContext;
        mSynchronizeContainerContractView.initView();
        mSynchronizeContainerContractView.initFont();

    }



    @Override
    public void resume()
    {

    }

    @Override
    public void pause()
    {

    }

    @Override
    public void stop()
    {

    }

    @Override
    public void destroy() {

    }

    @Override
    public void acvitityResult(int requestCode, int resultCode, Intent data)
    {

    }

    @Override
    public void sendMessageEvent(Message msg)
    {

    }

    @Override
    public void synchronizeNow()
    {

    }

    @Override
    public void synchronizeCancel()
    {

    }


}
