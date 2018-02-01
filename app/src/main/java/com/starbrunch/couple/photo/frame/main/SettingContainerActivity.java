package com.starbrunch.couple.photo.frame.main;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.starbrunch.couple.photo.frame.main.base.BaseActivity;
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
        overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);
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
        _SettingTitleText.setTypeface(FontManager.getInstance(this).getMainTitleFont());
        _SettingBluetoothSubtitleText.setTypeface(FontManager.getInstance(this).getDefaultLightTextFont());
        _SettingSendBookText.setTypeface(FontManager.getInstance(this).getMainTitleFont());
        _SettingReceiveBookText.setTypeface(FontManager.getInstance(this).getMainTitleFont());
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        mSettingContainerContractPresenter.acvitityResult(requestCode, resultCode, data);
    }
}
