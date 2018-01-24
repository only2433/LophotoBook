package com.starbrunch.couple.photo.frame.main.contract.presenter;

import android.content.Context;
import android.content.Intent;
import android.os.Message;

import com.starbrunch.couple.photo.frame.main.contract.SettingContainerContract;

/**
 * Created by 정재현 on 2018-01-24.
 */

public class SettingContainerPresent implements SettingContainerContract.Presenter
{

    private Context mContext = null;
    private SettingContainerContract.View mSettingContainerContractView = null;

    public SettingContainerPresent(Context context)
    {
        mContext = context;
        mSettingContainerContractView = (SettingContainerContract.View)mContext;
        mSettingContainerContractView.ininFont();
        mSettingContainerContractView.initView();
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
    public void destroy()
    {

    }

    @Override
    public void acvitityResult(int requestCode, int resultCode, Intent data)
    {

    }

    @Override
    public void requestPermissionResult(int requestCode, String[] permissions, int[] grantResults)
    {

    }

    @Override
    public void sendMessageEvent(Message msg)
    {

    }
}
