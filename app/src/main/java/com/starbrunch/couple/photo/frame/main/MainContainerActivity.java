package com.starbrunch.couple.photo.frame.main;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.ColorStateList;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
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
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by 정재현 on 2017-12-13.
 */

public class MainContainerActivity extends BaseActivity implements MainContainerContract.View, MessageHandlerCallback
{
    private static final int HEIGHT_FLOTING_BUTTON_DP = 56;

    @BindView(R.id._coordinatorMainLayout)
    CoordinatorLayout _CoordinatorMainLayout;

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

    @BindView(R.id._photoFloatingButton)
    FloatingActionButton _PhotoFloatingButton;

    private static final int MODE_HIDE_DEFAULT      = 0;
    private static final int MODE_SHOW_SETTING      = 1;
    private static final int MODE_SHOW_PHOTO        = 2;

    private MainContainerPresent mMainContainerPresent = null;
    private MaterialLoadingDialog mMaterialLoadingDialog = null;
    private CoordinatorLayout.LayoutParams mCoordinatorLayoutParams = null;
    private int mFlotingButtonHeight = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_base);
        ButterKnife.bind(this);
        CommonUtils.getInstance(this).getWindowInfo();

        init();
    }

    private void init()
    {
        mMainContainerPresent = new MainContainerPresent(this);
    }



    @Override
    public void initView()
    {

        mCoordinatorLayoutParams = (CoordinatorLayout.LayoutParams) _PhotoFloatingButton.getLayoutParams();
        mFlotingButtonHeight = (int) CommonUtils.getInstance(this).convertDpToPixel(HEIGHT_FLOTING_BUTTON_DP);
        changeTitleAnimationText(getResources().getString(R.string.app_name).toUpperCase());
    }

    @Override
    public void initFont()
    {
        _MainBaseTitleText.setTypeface(FontManager.getInstance(this).getTypolinoRegularFont());
        _TitleMonthSubTitle.setTypeface(FontManager.getInstance(this).getDefaultLightTextFont());
    }


    @Override
    protected void onResume()
    {
        super.onResume();
        mMainContainerPresent.resume();
        Log.i("");
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        mMainContainerPresent.pause();
        Log.i("");
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        mMainContainerPresent.stop();
        Log.i("");
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        mMainContainerPresent.destroy();
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
        final int backgroundColor = color;
        Rect rect = CommonUtils.getInstance(this).getGlobalVisibleRect(_MainBaseTitleText);

        CommonUtils.getInstance(MainContainerActivity.this).showAnimateReveal(
                _MainBaseBackgroundLayout,
                backgroundColor,
                rect.centerX(),
                rect.centerY(),
                Common.DURATION_TITLE_BACKGROUND_ANIMATION);
    }

    @Override
    public void hideTitleViewBackgroundAnimation(int color)
    {
        final int backgroundColor = color;
        Rect rect = CommonUtils.getInstance(this).getGlobalVisibleRect(_MainBaseTitleText);

        CommonUtils.getInstance(MainContainerActivity.this).hideAnimateReveal(
                _MainBaseBackgroundLayout,
                backgroundColor,
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

    @Override
    public void showSettingButton(int delay)
    {
        _PhotoFloatingButton.setVisibility(View.VISIBLE);
        _PhotoFloatingButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.color_999999)));
        _PhotoFloatingButton.setImageResource(R.drawable.ic_menu_white_24dp);
        Animation animation = CommonUtils.getInstance(this).getTranslateYAnimation(CommonUtils.getInstance(this).getPixel(mFlotingButtonHeight) + mCoordinatorLayoutParams.bottomMargin, 0, Common.DURATION_DEFAULT, delay, new AccelerateInterpolator());
        _PhotoFloatingButton.startAnimation(animation);
    }

    @Override
    public void showPhotoButton(int delay, int color)
    {
        _PhotoFloatingButton.setVisibility(View.VISIBLE);
        _PhotoFloatingButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(color)));
        _PhotoFloatingButton.setImageResource(R.drawable.ic_wallpaper_white);
        Animation animation = CommonUtils.getInstance(this).getTranslateYAnimation(CommonUtils.getInstance(this).getPixel(mFlotingButtonHeight) + mCoordinatorLayoutParams.bottomMargin, 0, Common.DURATION_DEFAULT, delay, new AccelerateInterpolator());
        _PhotoFloatingButton.startAnimation(animation);
    }

    @Override
    public void hideModeButton()
    {
        changeModeButton(MODE_HIDE_DEFAULT);
    }

    private void changeModeButton(final int mode, final int ... color)
    {
        Animation animation = CommonUtils.getInstance(this).getTranslateYAnimation(0, CommonUtils.getInstance(this).getPixel(mFlotingButtonHeight) + mCoordinatorLayoutParams.bottomMargin, Common.DURATION_DEFAULT, 0, new AccelerateInterpolator());
        animation.setFillAfter(true);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation)
            {
                switch(mode)
                {
                    case MODE_HIDE_DEFAULT:
                        _PhotoFloatingButton.setVisibility(View.INVISIBLE);
                        break;
                    case MODE_SHOW_SETTING:
                        showSettingButton(0);
                        break;
                    case MODE_SHOW_PHOTO:
                        showPhotoButton(0, color[0]);
                        break;
                }

            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
        _PhotoFloatingButton.startAnimation(animation);
    }


    @Override
    public void changeSettingButton()
    {
        changeModeButton(MODE_SHOW_SETTING);
    }

    @Override
    public void changePhotoButton(int color)
    {
        changeModeButton(MODE_SHOW_PHOTO, color);
    }



    @Override
    public void invisibleFloatButton()
    {
        Log.i("");
        _PhotoFloatingButton.clearAnimation();
        _PhotoFloatingButton.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showMessage(String message, int color)
    {
        Log.i("message : "+message);
        CommonUtils.getInstance(this).showSnackMessage(_CoordinatorMainLayout, message, color, Gravity.CENTER);
    }

    @OnClick(R.id._photoFloatingButton)
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id._photoFloatingButton:
                mMainContainerPresent.selectFloatButton();
                break;
        }
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
    public void handlerMessage(Message message)
    {
        mMainContainerPresent.sendMessageEvent(message);
    }


}
