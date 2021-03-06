package com.starbrunch.couple.photo.frame.main;

import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.littlefox.logmonitor.Log;
import com.starbrunch.couple.photo.frame.main.base.BaseActivity;
import com.starbrunch.couple.photo.frame.main.common.CommonUtils;
import com.starbrunch.couple.photo.frame.main.common.FontManager;
import com.starbrunch.couple.photo.frame.main.contract.SettingContainerContract;
import com.starbrunch.couple.photo.frame.main.contract.presenter.SettingContainerPresent;
import com.starbrunch.couple.photo.frame.main.handler.callback.MessageHandlerCallback;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by 정재현 on 2018-01-24.
 */

public class SettingContainerActivity extends BaseActivity implements SettingContainerContract.View, MessageHandlerCallback
{
    @BindView(R.id._coordinatorMainLayout)
    CoordinatorLayout _CoordinatorMainLayout;

    @BindView(R.id._settingBackButton)
    ImageView _SetttingBackButton;

    @BindView(R.id._settingTitleText)
    TextView _SettingTitleText;

    @BindView(R.id._settingBluetoothSubTitleText)
    TextView _SettingBluetoothSubtitleText;

    @BindView(R.id._settingSendBookText)
    TextView _SettingSendBookText;

    @BindView(R.id._settingReceiveBookText)
    TextView _SettingReceiveBookText;

    private SettingContainerContract.Presenter mSettingContainerContractPresenter = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);

        init();
    }

    private void init()
    {
        mSettingContainerContractPresenter = new SettingContainerPresent(this);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        mSettingContainerContractPresenter.resume();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        mSettingContainerContractPresenter.pause();
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        mSettingContainerContractPresenter.stop();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        mSettingContainerContractPresenter.destroy();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
    }

    @Override
    public void handlerMessage(Message message)
    {
        mSettingContainerContractPresenter.sendMessageEvent(message);
    }

    @Override
    public void initView()
    {

    }

    @Override
    public void initFont()
    {
        _SettingTitleText.setTypeface(FontManager.getInstance(this).getRampungRagularFont());
        _SettingBluetoothSubtitleText.setTypeface(FontManager.getInstance(this).getPhenomenaLightFont());
        _SettingSendBookText.setTypeface(FontManager.getInstance(this).getPhenomenaLightFont());
        _SettingReceiveBookText.setTypeface(FontManager.getInstance(this).getPhenomenaLightFont());
    }

    @Override
    public void showMessage(String message, int color)
    {
        Log.i("message : "+message);
        CommonUtils.getInstance(this).showSnackMessage(_CoordinatorMainLayout, message, color, Gravity.CENTER);
    }


    @OnClick({R.id._settingSendButton, R.id._settingReceiveButton})
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id._settingSendButton:
                mSettingContainerContractPresenter.sendBook();
                break;
            case R.id._settingReceiveButton:
                mSettingContainerContractPresenter.receiveBook();
                break;
        }
    }

}
