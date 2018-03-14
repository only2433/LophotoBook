package com.starbrunch.couple.photo.frame.main;

import android.os.Bundle;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.starbrunch.couple.photo.frame.main.base.BaseActivity;
import com.starbrunch.couple.photo.frame.main.common.CommonUtils;
import com.starbrunch.couple.photo.frame.main.common.FontManager;
import com.starbrunch.couple.photo.frame.main.contract.SplashContainerContract;
import com.starbrunch.couple.photo.frame.main.contract.presenter.SplashContainerPresent;
import com.starbrunch.couple.photo.frame.main.handler.callback.MessageHandlerCallback;
import com.starbrunch.couple.photo.frame.main.hanks.htextview.HTextView;
import com.starbrunch.couple.photo.frame.main.hanks.htextview.HTextViewType;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by only340 on 2018-03-12.
 */

public class SplashContainerActivity extends BaseActivity implements SplashContainerContract.View, MessageHandlerCallback
{
    @BindView(R.id._coordinatorMainLayout)
    CoordinatorLayout _CoordinatorMainLayout;

    @BindView(R.id._splashTitleImage)
    ImageView _SplashTitleImage;

    @BindView(R.id._splashTitleText)
    HTextView _SplashTitleText;

    @BindView(R.id._splashMessageText)
    HTextView _SplashMessageText;

    private SplashContainerContract.Presenter mSplashContainerContractPresenter = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);

        mSplashContainerContractPresenter = new SplashContainerPresent(this);
    }

    @Override
    public void initView()
    {

    }

    @Override
    public void initFont()
    {
        _SplashTitleText.setTypeface(FontManager.getInstance(this).getTypolinoRegularFont());
        _SplashMessageText.setTypeface(FontManager.getInstance(this).getBodrumRegularFont());
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        mSplashContainerContractPresenter.resume();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        mSplashContainerContractPresenter.pause();
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        mSplashContainerContractPresenter.stop();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        mSplashContainerContractPresenter.destroy();
    }

    @Override
    public void showSplashTitleText()
    {
        _SplashTitleText.setAnimateType(HTextViewType.EVAPORATE);
        _SplashTitleText.animateText(getResources().getString(R.string.app_name));
    }

    @Override
    public void showSplashMessageText()
    {
        _SplashMessageText.setAnimateType(HTextViewType.TYPER);
        _SplashMessageText.animateText(getResources().getString(R.string.message_splash));
    }

    @Override
    public void showToast(String message)
    {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void showMessage(String message, int color)
    {
        CommonUtils.getInstance(this).showSnackMessage(_CoordinatorMainLayout, message, color);
    }

    @Override
    public void handlerMessage(Message message)
    {
        mSplashContainerContractPresenter.sendMessageEvent(message);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        mSplashContainerContractPresenter.requestPermissionResult(requestCode, permissions, grantResults);
    }
}
