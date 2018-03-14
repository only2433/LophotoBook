package com.starbrunch.couple.photo.frame.main;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.littlefox.logmonitor.Log;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;
import com.starbrunch.couple.photo.frame.main.base.BaseActivity;
import com.starbrunch.couple.photo.frame.main.common.CommonUtils;
import com.starbrunch.couple.photo.frame.main.common.FontManager;
import com.starbrunch.couple.photo.frame.main.contract.SynchronizeContainerContract;
import com.starbrunch.couple.photo.frame.main.contract.presenter.SynchronizeContainerPresent;
import com.starbrunch.couple.photo.frame.main.handler.callback.MessageHandlerCallback;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by only340 on 2018-03-07.
 */



public class SynchronizeContainerActivity extends BaseActivity implements SynchronizeContainerContract.View, MessageHandlerCallback
{
    @BindView(R.id._coordinatorMainLayout)
    CoordinatorLayout _CoordinatorMainLayout;

    @BindView(R.id._synchronizeTimerProgressBar)
    CircularProgressBar _SynchronizeTimerProgressBar;

    @BindView(R.id._synchronizeMainTitle)
    TextView _SynchronizeMainTitle;

    @BindView(R.id._synchronizeTimerHourText)
    TextView _SynchronizeTimerHourText;

    @BindView(R.id._synchronizeHourSignText)
    TextView _SynchronizeHourSignText;

    @BindView(R.id._synchronizeTimerMinuteText)
    TextView _SynchronizeTimerMinuteText;

    @BindView(R.id._synchronizeMinuteSignText)
    TextView _SynchronizeMinuteSignText;

    @BindView(R.id._synchronizeTimerSecondText)
    TextView _SynchronizeTimerSecondText;

    @BindView(R.id._synchronizeSecondSignText)
    TextView _SynchronizeSecondSignText;

    @BindView(R.id._synchronizeWarningMessageText)
    TextView _SynchronizeWarningMessageText;

    @BindView(R.id._synchronizeInAppButton)
    Button _SynchronizeInAppButton;

    @BindView(R.id._synchronizeCancelButton)
    Button _SynchronizeCancelButton;

    private SynchronizeContainerContract.Presenter mSynchronizeContainerContractPresenter = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_synchronize);
        ButterKnife.bind(this);

        mSynchronizeContainerContractPresenter = new SynchronizeContainerPresent(this);

    }

    @Override
    public void initView()
    {

    }

    @Override
    public void initFont()
    {
        _SynchronizeMainTitle.setTypeface(FontManager.getInstance(this).getRampungRagularFont());

        _SynchronizeTimerHourText.setTypeface(FontManager.getInstance(this).getOccopiedFont());
        _SynchronizeHourSignText.setTypeface(FontManager.getInstance(this).getPhenomenaLightFont());

        _SynchronizeTimerMinuteText.setTypeface(FontManager.getInstance(this).getOccopiedFont());
        _SynchronizeMinuteSignText.setTypeface(FontManager.getInstance(this).getPhenomenaLightFont());

        _SynchronizeTimerSecondText.setTypeface(FontManager.getInstance(this).getOccopiedFont());
        _SynchronizeSecondSignText.setTypeface(FontManager.getInstance(this).getPhenomenaLightFont());

        _SynchronizeWarningMessageText.setTypeface(FontManager.getInstance(this).getPhenomenaThinFont());

        _SynchronizeInAppButton.setTypeface(FontManager.getInstance(this).getRampungRagularFont());
        _SynchronizeCancelButton.setTypeface(FontManager.getInstance(this).getRampungRagularFont());
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        mSynchronizeContainerContractPresenter.resume();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        mSynchronizeContainerContractPresenter.pause();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        mSynchronizeContainerContractPresenter.destroy();
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        mSynchronizeContainerContractPresenter.stop();
    }

    @Override
    public void setRemainingTimer(int hour, int minute, int second, int percent)
    {
        _SynchronizeTimerHourText.setText(String.valueOf(hour));
        _SynchronizeTimerMinuteText.setText(String.valueOf(minute));
        _SynchronizeTimerSecondText.setText(String.valueOf(second));
        _SynchronizeTimerProgressBar.setProgress(percent);
    }

    @Override
    public void showMessage(String message, int color)
    {
        Log.i("message : "+message);
        CommonUtils.getInstance(this).showSnackMessage(_CoordinatorMainLayout, message, color, Gravity.CENTER);
    }

    @Override
    public void handlerMessage(Message message)
    {
        mSynchronizeContainerContractPresenter.sendMessageEvent(message);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        mSynchronizeContainerContractPresenter.acvitityResult(requestCode,resultCode,data);
    }

    @OnClick({R.id._synchronizeInAppButton, R.id._synchronizeCancelButton})
    public void onClick(View view)
    {
        switch(view.getId())
        {
            case R.id._synchronizeInAppButton:
                mSynchronizeContainerContractPresenter.synchronizeNow();
                break;
            case R.id._synchronizeCancelButton:
                mSynchronizeContainerContractPresenter.synchronizeCancel();
                break;
        }
    }
}
