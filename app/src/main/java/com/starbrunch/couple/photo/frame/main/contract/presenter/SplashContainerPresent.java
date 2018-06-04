package com.starbrunch.couple.photo.frame.main.contract.presenter;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Message;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;

import com.littlefox.logmonitor.Log;
import com.starbrunch.couple.photo.frame.main.MainContainerActivity;
import com.starbrunch.couple.photo.frame.main.R;
import com.starbrunch.couple.photo.frame.main.SplashContainerActivity;
import com.starbrunch.couple.photo.frame.main.SynchronizeContainerActivity;
import com.starbrunch.couple.photo.frame.main.common.Common;
import com.starbrunch.couple.photo.frame.main.common.CommonUtils;
import com.starbrunch.couple.photo.frame.main.contract.SplashContainerContract;
import com.starbrunch.couple.photo.frame.main.handler.WeakReferenceHandler;

/**
 * Created by only340 on 2018-03-12.
 */

public class SplashContainerPresent implements SplashContainerContract.Presenter
{
    private static final int PERMISSION_REQUEST = 100;

    private static final int MESSAGE_TITLE_VIEW         = 0;
    private static final int MESSAGE_CHECK_SETTING      = 1;
    private static final int MESSAGE_MESSAGE_VIEW       = 2;
    private static final int MESSAGE_START_MAIN         = 3;
    private static final int MESSAGE_START_SYNCHRONIZE  = 4;

    private Context mContext = null;
    private SplashContainerContract.View mSplashContainerContractView = null;
    private WeakReferenceHandler mWeakReferenceHandler = null;
    public SplashContainerPresent(Context context)
    {
        mContext = context;
        mSplashContainerContractView = (SplashContainerContract.View)mContext;
        mWeakReferenceHandler = new WeakReferenceHandler((SplashContainerActivity)mContext);
        mSplashContainerContractView.initFont();
        mSplashContainerContractView.initView();
        mWeakReferenceHandler.sendEmptyMessageDelayed(MESSAGE_TITLE_VIEW, Common.DURATION_SHORT);

    }

    private void checkUnusualBehavior()
    {
        if(isAutoTimeSelected())
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            {
                String[] permissionList = new String[]{ Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION};

                if(CommonUtils.getInstance(mContext).getUnAuthorizePermissionList(permissionList).size() > 0)
                {
                    CommonUtils.getInstance(mContext).requestPermission(permissionList, PERMISSION_REQUEST);
                }
                else
                {
                    mWeakReferenceHandler.sendEmptyMessageDelayed(MESSAGE_MESSAGE_VIEW, Common.DURATION_LONG);
                }
            }
            else
            {
                mWeakReferenceHandler.sendEmptyMessageDelayed(MESSAGE_MESSAGE_VIEW, Common.DURATION_LONG);
            }


        }
        else
        {
            mWeakReferenceHandler.sendEmptyMessageDelayed(MESSAGE_CHECK_SETTING, Common.DURATION_DEFAULT);
        }
    }

    /**
     * 시간대 자동 설정이 되어 있는 지 체크
     * @return
     */
    private boolean isAutoTimeSelected()
    {
        int autoTimeSelected = -1;
        //int autoTimeSelectedZone = -1;
        try
        {
            if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP)
            {
                autoTimeSelected = Settings.Global.getInt(mContext.getContentResolver(), Settings.Global.AUTO_TIME);
            }
            else
            {
                autoTimeSelected = Settings.System.getInt(mContext.getContentResolver(), Settings.System.AUTO_TIME);
            }

            if( autoTimeSelected == 1)
            {
                return true;
            }
            else
            {
                Log.f("AUTO_TIME : "+autoTimeSelected);
                return false;
            }
        }
        catch (Settings.SettingNotFoundException e)
        {
            e.printStackTrace();
        }

        return true;
    }

    private void startAutoTimeActivity()
    {
        Intent intent=new Intent();
        intent.setComponent(new ComponentName("com.android.settings",
                "com.android.settings.DateTimeSettingsSetupWizard"));
        ((AppCompatActivity)mContext).startActivity(intent);
    }

    private void checkSynchronize()
    {
        boolean isSynchronizing = (boolean) CommonUtils.getInstance(mContext).getSharedPreference(Common.PARAMS_IS_SYNCHRONIZING, Common.TYPE_PARAMS_BOOLEAN);

        isSynchronizing = true;
        if(isSynchronizing)
        {
            mWeakReferenceHandler.sendEmptyMessageDelayed(MESSAGE_START_SYNCHRONIZE, Common.DURATION_LONGER);
        }
        else
        {
            mWeakReferenceHandler.sendEmptyMessageDelayed(MESSAGE_START_MAIN, Common.DURATION_LONGER);
        }
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
    public void requestPermissionResult(int requestCode, String[] permissions, int[] grantResults)
    {
        boolean isAllCheckSuccess = true;
        switch(requestCode)
        {
            case PERMISSION_REQUEST:
                for(int i = 0; i < permissions.length; i++)
                {
                    if(grantResults[i] != PackageManager.PERMISSION_GRANTED)
                    {
                        isAllCheckSuccess = false;
                    }
                }

                if(isAllCheckSuccess == false)
                {
                    mSplashContainerContractView.showToast(mContext.getResources().getString(R.string.message_check_permission));
                    ((AppCompatActivity)mContext).finish();
                }
                else
                {
                    mWeakReferenceHandler.sendEmptyMessageDelayed(MESSAGE_MESSAGE_VIEW, Common.DURATION_LONG);
                }
        }
    }

    @Override
    public void sendMessageEvent(Message msg)
    {
        switch (msg.what)
        {
            case MESSAGE_TITLE_VIEW:
                mSplashContainerContractView.showSplashTitleText();
                checkUnusualBehavior();
                break;
            case MESSAGE_CHECK_SETTING:
                startAutoTimeActivity();
                mSplashContainerContractView.showToast(mContext.getResources().getString(R.string.message_timer_setting_warning));
                ((AppCompatActivity)mContext).finish();
                break;
            case MESSAGE_MESSAGE_VIEW:
                mSplashContainerContractView.showSplashMessageText();
                checkSynchronize();
                break;
            case MESSAGE_START_MAIN:
                Intent mainIntent = new Intent(mContext, MainContainerActivity.class);
                ((AppCompatActivity)mContext).startActivity(mainIntent);
                ((AppCompatActivity) mContext).overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
                ((AppCompatActivity)mContext).finish();
                break;
            case MESSAGE_START_SYNCHRONIZE:
                Intent syncIntent = new Intent(mContext, SynchronizeContainerActivity.class);
                ((AppCompatActivity)mContext).startActivity(syncIntent);
                ((AppCompatActivity) mContext).overridePendingTransition(R.anim.slide_down_in, R.anim.slide_up_out);
                ((AppCompatActivity)mContext).finish();
                break;
        }
    }
}
