package com.starbrunch.couple.photo.frame.main;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.bumptech.glide.Glide;
import com.littlefox.library.view.dialog.MaterialLoadingDialog;
import com.littlefox.logmonitor.Log;
import com.starbrunch.couple.photo.frame.main.base.BaseActivity;
import com.starbrunch.couple.photo.frame.main.common.Common;
import com.starbrunch.couple.photo.frame.main.common.CommonUtils;
import com.starbrunch.couple.photo.frame.main.common.FontManager;
import com.starbrunch.couple.photo.frame.main.contract.ModifiedInformationContract;
import com.starbrunch.couple.photo.frame.main.contract.presenter.ModifiedInformationPresenter;
import com.starbrunch.couple.photo.frame.main.handler.callback.MessageHandlerCallback;
import com.starbrunch.couple.photo.frame.main.hanks.htextview.HTextView;
import com.starbrunch.couple.photo.frame.main.hanks.htextview.HTextViewType;
import com.starbrunch.couple.photo.frame.main.object.PhotoInformationObject;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by 정재현 on 2018-01-10.
 */

public class ModifiedInformationActivity extends BaseActivity implements ModifiedInformationContract.View, MessageHandlerCallback
{
    @BindView(R.id._ModifiedSaveText)
    TextView _ModifiedSaveText;

    @BindView(R.id._ModifiedCancelText)
    TextView _ModifiedCancelText;

    @BindView(R.id._ModifiedImage)
    ImageView _ModifiedImage;

    @BindView(R.id._ModifiedDateInformationText)
    HTextView _ModifiedDateInformationText;

    @BindView(R.id._ModifiedPhotoText)
    TextView _ModifiedPhotoText;

    @BindView(R.id._ModifiedDateText)
    TextView _ModifiedDateText;

    @BindView(R.id._ModifiedCommentText)
    TextView _ModifiedCommentText;

    private ModifiedInformationPresenter mModifiedInformationPresenter = null;

    private Calendar mRequestCalendar = null;
    private MaterialLoadingDialog mMaterialLoadingDialog = null;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_information_modified);
        ButterKnife.bind(this);
        mModifiedInformationPresenter = new ModifiedInformationPresenter(this);
    }

    private void initFont()
    {
        _ModifiedSaveText.setTypeface(FontManager.getInstance(this).getDefaultLightTextFont());
        _ModifiedCancelText.setTypeface(FontManager.getInstance(this).getDefaultLightTextFont());
        _ModifiedDateInformationText.setTypeface(FontManager.getInstance(this).getDefaultBoldTextFont());
        _ModifiedPhotoText.setTypeface(FontManager.getInstance(this).getDefaultLightTextFont());
        _ModifiedDateText.setTypeface(FontManager.getInstance(this).getDefaultLightTextFont());
        _ModifiedCommentText.setTypeface(FontManager.getInstance(this).getDefaultLightTextFont());
    }


    @Override
    public void handlerMessage(Message message)
    {
        mModifiedInformationPresenter.sendMessageEvent(message);
    }


    @Override
    public void initView(PhotoInformationObject object)
    {
        initFont();
        String transitionName = getIntent().getStringExtra(Common.INTENT_PHOTO_TRANSITION_NAME);
        _ModifiedImage.setTransitionName(transitionName);
        Glide.with(this).load(Common.PATH_IMAGE_ROOT+object.getFileName()).into(_ModifiedImage);
        _ModifiedDateInformationText.setAnimateType(HTextViewType.TYPER);
        _ModifiedDateInformationText.animateText(CommonUtils.getInstance(this).getDateFullText(object.getDateTime())+" "+CommonUtils.getInstance(this).getDateClock(object.getDateTime()));
    }

    @Override
    public void changePhoto(String filePath)
    {
        Log.f("filePath : "+ filePath);
        Glide.with(this).load(filePath).into(_ModifiedImage);
    }

    @Override
    public void changeDateInformation(String date)
    {
        Log.f("date : "+ date);
        _ModifiedDateInformationText.setAnimateType(HTextViewType.TYPER);
        _ModifiedDateInformationText.animateText(date);
    }

    @Override
    public void changeComment(String comment)
    {
        Log.f("comment : "+ comment);
        //TODO : 코멘트 처리
    }

    @Override
    public void showDatePickerDialog()
    {
        mRequestCalendar = Calendar.getInstance();
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, mDateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    @Override
    public void showLoading()
    {
        if(mMaterialLoadingDialog == null)
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
        }
    }

    private void showTimePickerDialog()
    {
        Calendar calendar = Calendar.getInstance();
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, mTimeSetListener,
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE), false);
        timePickerDialog.show();
    }

    @OnClick({R.id._ModifiedSaveIcon, R.id._ModifiedSaveText, R.id._ModifiedCancelIcon, R.id._ModifiedCancelText,
            R.id._ModifiedPhotoIcon, R.id._ModifiedPhotoText, R.id._ModifiedDateIcon, R.id._ModifiedDateText,
            R.id._ModifiedCommentIcon, R.id._ModifiedCommentText})
    public void onClickEvent(View view)
    {
        switch(view.getId())
        {
            case R.id._ModifiedSaveIcon:
            case R.id._ModifiedSaveText:
                mModifiedInformationPresenter.onClickSaveButton();
                break;
            case R.id._ModifiedCancelIcon:
            case R.id._ModifiedCancelText:
                mModifiedInformationPresenter.onClickCancelButton();
                break;
            case R.id._ModifiedPhotoIcon:
            case R.id._ModifiedPhotoText:
                mModifiedInformationPresenter.onClickPhotoModifiedButton();
                break;
            case R.id._ModifiedDateIcon:
            case R.id._ModifiedDateText:
                mModifiedInformationPresenter.onClickDateModifiedButton();
                break;
            case R.id._ModifiedCommentIcon:
            case R.id._ModifiedCommentText:
                mModifiedInformationPresenter.onClickCommentModifedButton();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        mModifiedInformationPresenter.onAcvitityResult(requestCode,resultCode,data);
    }

    private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener()
    {
        @Override
        public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth)
        {
            Log.i("year : "+year+", monthOfYear: "+monthOfYear+", dayOfMonth : "+dayOfMonth);
            mRequestCalendar.set(Calendar.YEAR,year);
            mRequestCalendar.set(Calendar.MONTH,monthOfYear);
            mRequestCalendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);

            showTimePickerDialog();
        }
    };

    private TimePickerDialog.OnTimeSetListener mTimeSetListener = new TimePickerDialog.OnTimeSetListener()
    {
        @Override
        public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute)
        {
            Log.i("hourOfDay : "+hourOfDay+", minute: "+minute);
            mRequestCalendar.set(Calendar.HOUR_OF_DAY,hourOfDay);
            mRequestCalendar.set(Calendar.MINUTE, minute);

            mModifiedInformationPresenter.onDateSetChanged(mRequestCalendar.getTimeInMillis());
        }
    };
}
