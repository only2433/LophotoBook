package com.starbrunch.couple.photo.frame.main.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.littlefox.library.view.dialog.MaterialLoadingDialog;
import com.littlefox.logmonitor.Log;
import com.starbrunch.couple.photo.frame.main.R;
import com.starbrunch.couple.photo.frame.main.callback.MainContainerCallback;
import com.starbrunch.couple.photo.frame.main.common.Common;
import com.starbrunch.couple.photo.frame.main.common.CommonUtils;
import com.starbrunch.couple.photo.frame.main.common.FontManager;
import com.starbrunch.couple.photo.frame.main.contract.presenter.MainContainerPresent;
import com.starbrunch.couple.photo.frame.main.hanks.htextview.HTextView;
import com.starbrunch.couple.photo.frame.main.hanks.htextview.HTextViewType;
import com.starbrunch.couple.photo.frame.main.object.PhotoInformationObject;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by 정재현 on 2018-01-15.
 */

public class ModifiedInformationFragment extends Fragment
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

    private Context mContext = null;
    private PhotoInformationObject mCurrentModifiedObject = null;
    private Calendar mRequestCalendar = null;
    private MaterialLoadingDialog mMaterialLoadingDialog = null;
    private MainContainerCallback mMainContainerCallback = null;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_photo_information_modified, container, false);
        ButterKnife.bind(this, view);

        init();

        return view;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        mMainContainerCallback.setMainScene(MainContainerPresent.SCENE_MODIFIED_INFORMATION);
    }

    @Override
    public void onPause()
    {
        super.onPause();
    }

    @Override
    public void onStop()
    {
        super.onStop();
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        mMainContainerCallback.onModifiedEnd();
    }



    private void init()
    {
        initFont();
        Bundle bundle = getArguments();
        String transitionName = bundle.getString(Common.INTENT_PHOTO_TRANSITION_NAME);

        Log.i("transitionName : "+transitionName);
        mCurrentModifiedObject = bundle.getParcelable(Common.INTENT_MODIFIED_ITEM_OBJECT);

        _ModifiedImage.setTransitionName(transitionName);
        _ModifiedImage.setImageBitmap(BitmapFactory.decodeFile(Common.PATH_IMAGE_ROOT+mCurrentModifiedObject.getFileName()));

        _ModifiedDateInformationText.setAnimateType(HTextViewType.TYPER);
        _ModifiedDateInformationText.animateText(CommonUtils.getInstance(mContext).getDateFullText(mCurrentModifiedObject.getDateTime())+" "+CommonUtils.getInstance(mContext).getDateClock(mCurrentModifiedObject.getDateTime()));
    }

    private void initFont()
    {
        _ModifiedSaveText.setTypeface(FontManager.getInstance(mContext).getDefaultLightTextFont());
        _ModifiedCancelText.setTypeface(FontManager.getInstance(mContext).getDefaultLightTextFont());
        _ModifiedDateInformationText.setTypeface(FontManager.getInstance(mContext).getDefaultBoldTextFont());
        _ModifiedPhotoText.setTypeface(FontManager.getInstance(mContext).getDefaultLightTextFont());
        _ModifiedDateText.setTypeface(FontManager.getInstance(mContext).getDefaultLightTextFont());
        _ModifiedCommentText.setTypeface(FontManager.getInstance(mContext).getDefaultLightTextFont());
    }

    public void changePhoto(Bitmap bitmap)
    {
        _ModifiedImage.setImageBitmap(bitmap);
    }

    public void changeDateInformation(String date)
    {
        Log.f("date : "+ date);
        _ModifiedDateInformationText.setAnimateType(HTextViewType.TYPER);
        _ModifiedDateInformationText.animateText(date);
    }

    public void changeComment(String comment)
    {
        Log.f("comment : "+ comment);
        //TODO : 코멘트 처리
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
                mMainContainerCallback.onModifiedItemSave();
                break;
            case R.id._ModifiedCancelIcon:
            case R.id._ModifiedCancelText:
                mMainContainerCallback.onModifiedItemCancel();
                break;
            case R.id._ModifiedPhotoIcon:
            case R.id._ModifiedPhotoText:
                mMainContainerCallback.onSelectPhotoModified();
                break;
            case R.id._ModifiedDateIcon:
            case R.id._ModifiedDateText:
                mMainContainerCallback.onSelectDateModified();
                break;
            case R.id._ModifiedCommentIcon:
            case R.id._ModifiedCommentText:
                mMainContainerCallback.onSelectCommentModifed();
                break;
        }
    }

    public void setMainContainerCallback(MainContainerCallback baseContainerCallback)
    {
        mMainContainerCallback = baseContainerCallback;
    }

}
