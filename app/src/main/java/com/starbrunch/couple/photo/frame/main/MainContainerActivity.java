package com.starbrunch.couple.photo.frame.main;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.littlefox.logmonitor.Log;
import com.ssomai.android.scalablelayout.ScalableLayout;
import com.starbrunch.couple.photo.frame.main.base.BaseActivity;
import com.starbrunch.couple.photo.frame.main.common.Common;
import com.starbrunch.couple.photo.frame.main.common.CommonUtils;
import com.starbrunch.couple.photo.frame.main.common.FontManager;
import com.starbrunch.couple.photo.frame.main.contract.MainContainerContract;
import com.starbrunch.couple.photo.frame.main.contract.presenter.MainContainerPresent;
import com.starbrunch.couple.photo.frame.main.handler.callback.MessageHandlerCallback;
import com.starbrunch.couple.photo.frame.main.hanks.htextview.HTextView;
import com.starbrunch.couple.photo.frame.main.hanks.htextview.HTextViewType;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by 정재현 on 2017-12-13.
 */

public class MainContainerActivity extends BaseActivity implements MainContainerContract.View, MessageHandlerCallback
{
    @BindView(R.id._mainBaseBackgroundLayout)
    FrameLayout _MainBaseBackgroundLayout;

    @BindView(R.id._mainContainer)
    FrameLayout _MainContainer;

    @BindView(R.id._mainBaseTitleLayout)
    ScalableLayout _MainBaseTitleLayout;

    @BindView(R.id._mainBaseTitleText)
    HTextView _MainBaseTitleText;

    @BindView(R.id._titleMonthSubTitle)
    TextView _TitleMonthSubTitle;

    @BindView(R.id._divideLine)
    ImageView _DivideLineImage;

    private MainContainerPresent mMainContainerPresent = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_base);
        ButterKnife.bind(this);
        CommonUtils.getInstance(this).getWindowInfo();

        Log.i("Environment.getExternalStorageDirectory() : "+ Environment.getExternalStorageDirectory());
        Log.i("getFilesDir() : "+ getFilesDir());
        Log.i("getCacheDir() : "+ getCacheDir());
        Log.i("getExternalFilesDir() : "+ getExternalFilesDir(null));

        init();
    }

    private void init()
    {
        mMainContainerPresent = new MainContainerPresent(this);
    }

    @Override
    public void initView()
    {
        initFont();
        changeTitleAnimationText(getResources().getString(R.string.app_name));
    }


    private void initFont()
    {
        _MainBaseTitleText.setTypeface(FontManager.getInstance(this).getMainTitleFont());
        _TitleMonthSubTitle.setTypeface(FontManager.getInstance(this).getMainTitleFont());
    }


    private void showMonthNumberText()
    {
        AnimatorSet animatorSet = new AnimatorSet();

        ObjectAnimator transAnimator = ObjectAnimator.ofFloat(_TitleMonthSubTitle, "translationX", 100,0);
        ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(_TitleMonthSubTitle, "alpha", 0 , 1);
        animatorSet.playTogether(transAnimator,alphaAnimator);
        animatorSet.setStartDelay(Common.DURATION_SHORT);
        animatorSet.setDuration(Common.DURATION_DEFAULT);
        animatorSet.start();
        transAnimator.addListener(new Animator.AnimatorListener()
        {
            @Override
            public void onAnimationStart(Animator animator) {
                _TitleMonthSubTitle.setVisibility(View.VISIBLE);
            }
            @Override
            public void onAnimationEnd(Animator animator) {}
            @Override
            public void onAnimationCancel(Animator animator) {}
            @Override
            public void onAnimationRepeat(Animator animator) {}
        });

    }

    private void hideMonthNumberText()
    {
        AnimatorSet animatorSet = new AnimatorSet();

        ObjectAnimator transAnimator = ObjectAnimator.ofFloat(_TitleMonthSubTitle, "translationX", 0,100);
        ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(_TitleMonthSubTitle, "alpha", 1 , 0);
        animatorSet.playTogether(transAnimator, alphaAnimator);

        animatorSet.setDuration(Common.DURATION_DEFAULT);
        animatorSet.start();
        transAnimator.addListener(new Animator.AnimatorListener()
        {
            @Override
            public void onAnimationStart(Animator animator) {}
            @Override
            public void onAnimationEnd(Animator animator)
            {
                _TitleMonthSubTitle.setVisibility(View.GONE);
            }
            @Override
            public void onAnimationCancel(Animator animator) {}
            @Override
            public void onAnimationRepeat(Animator animator) {}
        });
    }

    @Override
    public void showTitleViewBackgroundAnimation(int color)
    {
        Rect rect = CommonUtils.getInstance(this).getGlobalVisibleRect(_MainBaseTitleText);

        CommonUtils.getInstance(MainContainerActivity.this).showAnimateReveal(
                _MainBaseBackgroundLayout,
                color,
                rect.centerX(),
                rect.centerY(),
                Common.DURATION_TITLE_BACKGROUND_ANIMATION);
    }

    @Override
    public void hideTitleViewBackgroundAnimation(int color)
    {
        Rect rect = CommonUtils.getInstance(this).getGlobalVisibleRect(_MainBaseTitleText);

        CommonUtils.getInstance(MainContainerActivity.this).hideAnimateReveal(
                _MainBaseBackgroundLayout,
                color,
                rect.centerX(),
                rect.centerY(),
                Common.DURATION_TITLE_BACKGROUND_ANIMATION);
    }

    @Override
    public void setMonthNumberText(String text)
    {
        _TitleMonthSubTitle.setText(text);
    }

    @Override
    public void showMonthNumberAnimation()
    {
        showMonthNumberText();
    }

    @Override
    public void hideMonthNumberAnimation()
    {

        hideMonthNumberText();
    }

    @Override
    public void changeTitleAnimationText(String string)
    {
        _MainBaseTitleText.setAnimateType(HTextViewType.SCALE);
        _MainBaseTitleText.animateText(string);
    }

    @Override
    public void changeTitleViewColor(int color)
    {
        _MainBaseTitleText.setTextColor(getResources().getColor(color));
        _DivideLineImage.setBackgroundColor(getResources().getColor(color));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        mMainContainerPresent.onAcvitityResult(requestCode,resultCode,data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        mMainContainerPresent.onRequestPermissionResult(requestCode, permissions, grantResults);
    }

    @Override
    public void handlerMessage(Message message)
    {
        mMainContainerPresent.sendMessageEvent(message);
    }
}
