package com.starbrunch.couple.photo.frame.main;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.littlefox.library.view.dialog.MaterialLoadingDialog;
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

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

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

    @BindView(R.id._titleMonthSubTitleBackground)
    CircleImageView _TitleMonthSubTitleBackground;

    @BindView(R.id._divideLine)
    ImageView _DivideLineImage;

    private MainContainerPresent mMainContainerPresent = null;
    private MaterialLoadingDialog mMaterialLoadingDialog = null;

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
        _TitleMonthSubTitle.setTypeface(FontManager.getInstance(this).getDefaultLightTextFont());
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        Log.i("");
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        Log.i("");
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        Log.i("");
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        Log.i("");
    }



    private void showMonthNumberText()
    {
        final AnimatorSet animatorSet = new AnimatorSet();


        ObjectAnimator transTextAnimator = ObjectAnimator.ofFloat(_TitleMonthSubTitle, "translationY", -100,0);
        ObjectAnimator alphaTextAnimator = ObjectAnimator.ofFloat(_TitleMonthSubTitle, "alpha", 0 , 1);
        ObjectAnimator transBackgroundAnimator = ObjectAnimator.ofFloat(_TitleMonthSubTitleBackground, "translationY", 100,0);
        ObjectAnimator alphaBackgroundAnimator = ObjectAnimator.ofFloat(_TitleMonthSubTitleBackground, "alpha", 0 , 1);

        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animatorSet.playTogether(transTextAnimator,alphaTextAnimator, transBackgroundAnimator, alphaBackgroundAnimator);
        animatorSet.setStartDelay(Common.DURATION_SHORT);
        animatorSet.setDuration(Common.DURATION_SHORT);

        animatorSet.start();
        transTextAnimator.addListener(new Animator.AnimatorListener()
        {
            @Override
            public void onAnimationStart(Animator animator) {
                _TitleMonthSubTitle.setVisibility(View.VISIBLE);
                _TitleMonthSubTitleBackground.setVisibility(View.VISIBLE);
            }
            @Override
            public void onAnimationEnd(Animator animator)
            {
                Log.i("");

            }
            @Override
            public void onAnimationCancel(Animator animator)
            {
                Log.i("");
            }
            @Override
            public void onAnimationRepeat(Animator animator) {}
        });

    }

    private void hideMonthNumberText()
    {
        AnimatorSet animatorSet = new AnimatorSet();

        ObjectAnimator transTextAnimator = ObjectAnimator.ofFloat(_TitleMonthSubTitle, "translationY", 0,-100);
        ObjectAnimator alphaTextAnimator = ObjectAnimator.ofFloat(_TitleMonthSubTitle, "alpha", 1 , 0);
        ObjectAnimator transBackgroundAnimator = ObjectAnimator.ofFloat(_TitleMonthSubTitleBackground, "translationY", 0,100);
        ObjectAnimator alphaBackgroundAnimator = ObjectAnimator.ofFloat(_TitleMonthSubTitleBackground, "alpha", 1 , 0);


        animatorSet.playTogether(transTextAnimator, alphaTextAnimator, transBackgroundAnimator, alphaBackgroundAnimator);
        animatorSet.setDuration(Common.DURATION_DEFAULT);
        animatorSet.start();
        transTextAnimator.addListener(new Animator.AnimatorListener()
        {
            @Override
            public void onAnimationStart(Animator animator) {}
            @Override
            public void onAnimationEnd(Animator animator)
            {
                _TitleMonthSubTitle.setVisibility(View.GONE);
                _TitleMonthSubTitleBackground.setVisibility(View.GONE);
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
    public void setMonthNumberText(int color, int imageCount)
    {
        _TitleMonthSubTitle.setText(String.valueOf(imageCount));
        _TitleMonthSubTitleBackground.setBackgroundColor(getResources().getColor(color));
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
    public void showLoading()
    {
        if( mMaterialLoadingDialog == null)
        {
            mMaterialLoadingDialog = new MaterialLoadingDialog(this, CommonUtils.getInstance(this).getPixel(Common.LOADING_DIALOG_SIZE),
                    getResources().getColor(R.color.colorAccent));
        }
        mMaterialLoadingDialog.show();
    }

    @Override
    public void hideLoading()
    {
        if(mMaterialLoadingDialog != null)
        {
            mMaterialLoadingDialog.hide();
            mMaterialLoadingDialog = null;
        }
    }

    @Override
    public void showDatePickerDialog()
    {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, mDateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    @Override
    public void showTimePickerDialog()
    {
        Calendar calendar = Calendar.getInstance();
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, mTimeSetListener,
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE), false);
        timePickerDialog.show();
    }

    @Override
    public void showMainTitleLayout()
    {
        _MainBaseTitleLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideMainTitleLayout()
    {
        _MainBaseTitleLayout.setVisibility(View.INVISIBLE);
    }

    private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener()
    {
        @Override
        public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth)
        {
            Log.f("year : "+year+", monthOfYear: "+monthOfYear+", dayOfMonth : "+dayOfMonth);
            mMainContainerPresent.changeDateSetComplete(year, monthOfYear, dayOfMonth);
        }
    };

    private TimePickerDialog.OnTimeSetListener mTimeSetListener = new TimePickerDialog.OnTimeSetListener()
    {
        @Override
        public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute)
        {
            Log.f("hourOfDay : "+hourOfDay+", minute: "+minute);
            mMainContainerPresent.changeTimeSetComplete(hourOfDay, minute);
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        mMainContainerPresent.acvitityResult(requestCode,resultCode,data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        mMainContainerPresent.requestPermissionResult(requestCode, permissions, grantResults);
    }

    @Override
    public void handlerMessage(Message message)
    {
        mMainContainerPresent.sendMessageEvent(message);
    }


}
