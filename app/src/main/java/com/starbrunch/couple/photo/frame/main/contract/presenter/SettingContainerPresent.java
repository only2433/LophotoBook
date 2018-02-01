package com.starbrunch.couple.photo.frame.main.contract.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;

import com.starbrunch.couple.photo.frame.main.common.Common;
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
        mSettingContainerContractView.initFont();
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
    public void sendMessageEvent(Message msg)
    {

    }

    @Override
    public void sendBook()
    {
        Intent intent = new Intent();
        intent.putExtra(Common.INTENT_SETTING_SELECT_INDEX, Common.RESULT_SETTING_BLUETOOTH_SEND);
        ((AppCompatActivity)mContext).setResult(Activity.RESULT_OK, intent);
        ((AppCompatActivity)mContext).onBackPressed();
    }

    @Override
    public void receiveBook()
    {
        Intent intent = new Intent();
        intent.putExtra(Common.INTENT_SETTING_SELECT_INDEX, Common.RESULT_SETTING_BLUETOOTH_RECEIVE);
        ((AppCompatActivity)mContext).setResult(Activity.RESULT_OK, intent);
        ((AppCompatActivity)mContext).onBackPressed();
    }
}
