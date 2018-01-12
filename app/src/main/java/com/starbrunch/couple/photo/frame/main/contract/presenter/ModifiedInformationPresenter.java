package com.starbrunch.couple.photo.frame.main.contract.presenter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;

import com.littlefox.library.system.common.FileUtils;
import com.littlefox.logmonitor.Log;
import com.starbrunch.couple.photo.frame.main.ModifiedInformationActivity;
import com.starbrunch.couple.photo.frame.main.common.Common;
import com.starbrunch.couple.photo.frame.main.common.CommonUtils;
import com.starbrunch.couple.photo.frame.main.contract.ModifiedInformationContract;
import com.starbrunch.couple.photo.frame.main.database.PhotoInformationDBHelper;
import com.starbrunch.couple.photo.frame.main.handler.WeakReferenceHandler;
import com.starbrunch.couple.photo.frame.main.object.PhotoInformationObject;

import java.io.File;
import java.util.HashMap;

/**
 * Created by 정재현 on 2018-01-10.
 */

public class ModifiedInformationPresenter implements ModifiedInformationContract.Presenter
{
    private enum Check
    {
        PHOTO, DATE, COMMENT
    }

    private static final int REQUEST_PICK_FROM_ALBUM    = 0;
    private static final int REQUEST_CROP_FROM_ALBUM    = 1;

    private Context mContext = null;
    private ModifiedInformationContract.View mModifiedInformationContractView = null;
    private PhotoInformationDBHelper mPhotoInformationDBHelper = null;
    private WeakReferenceHandler mWeakReferenceHandler = null;

    private PhotoInformationObject mCurrentModifiedObject = null;
    private String mConvertItemKeyID = "";
    private long mModifiedDateTime = 0L;
    private Uri mImageCaptureUri = null;
    private File mCropImageFile = null;


    private HashMap<Enum, Boolean> mModifiedCheckList = null;

    public ModifiedInformationPresenter(Context context)
    {
        mContext = context;
        mModifiedCheckList = new HashMap<>();
        mModifiedInformationContractView = (ModifiedInformationContract.View)context;
        mPhotoInformationDBHelper = PhotoInformationDBHelper.getInstance(context);
        mWeakReferenceHandler = new WeakReferenceHandler((ModifiedInformationActivity)context);
        settingInformation();
    }

    private void settingInformation()
    {
        Intent intent = ((AppCompatActivity)mContext).getIntent();
        mConvertItemKeyID = intent.getStringExtra(Common.INTENT_PHOTO_KEY_ID);
        mCurrentModifiedObject = mPhotoInformationDBHelper.getPhotoInformationObject(mConvertItemKeyID);
        mModifiedInformationContractView.initView(mCurrentModifiedObject);
    }

    private void  copyChooseImageFile(Intent data)
    {
        File originalImageFile = CommonUtils.getInstance(mContext).getImageFile(data.getData());
        mImageCaptureUri = CommonUtils.getInstance(mContext).createImageFileUri(mConvertItemKeyID);
        mCropImageFile = new File(mImageCaptureUri.getPath());
        FileUtils.copyFile(originalImageFile, mCropImageFile);
    }

    private long getPhotoDateTime(Uri uri)
    {
        final int INDEX_DATE_TIME   = 0;
        String[] projection = {MediaStore.Images.Media.DATE_TAKEN};
        Cursor cursor = ((AppCompatActivity)mContext).getContentResolver().query(uri, projection, null, null, null);
        cursor.moveToFirst();
        if(cursor != null && cursor.getCount() != 0)
        {
            return Long.valueOf(cursor.getString(INDEX_DATE_TIME));
        }
        return System.currentTimeMillis();
    }

    private void saveModifiedInformation()
    {
        if(mModifiedCheckList.get(Check.PHOTO) == true)
        {
            Log.f("PHOTO CHANGE");
            boolean isSuccess = CommonUtils.getInstance(mContext).saveJpegFile(mImageCaptureUri, mCurrentModifiedObject.getFileName());

            if(isSuccess)
            {
                mModifiedInformationContractView.changePhoto(Common.PATH_IMAGE_ROOT + mCurrentModifiedObject.getFileName());
                FileUtils.deleteFile(mCropImageFile.getPath());
            }
        }

        if(mModifiedCheckList.get(Check.DATE) == true)
        {
            Log.f("DATE CHANGE");
            mPhotoInformationDBHelper.updatePhotoInformationObject(mConvertItemKeyID, PhotoInformationDBHelper.KEY_DATE_MILLISECOND, String.valueOf(mModifiedDateTime));
        }

        CommonUtils.getInstance(mContext).updateWidget();
        mModifiedInformationContractView.hideLoading();
        ((AppCompatActivity)mContext).setResult(((AppCompatActivity) mContext).RESULT_OK);
        ((AppCompatActivity)mContext).onBackPressed();
    }


    @Override
    public void onResume()
    {

    }

    @Override
    public void onPause()
    {

    }

    @Override
    public void onStop()
    {

    }

    @Override
    public void onDestroy()
    {

    }

    @Override
    public void onAcvitityResult(int requestCode, int resultCode, Intent data)
    {
        if (resultCode != ((AppCompatActivity) mContext).RESULT_OK)
        {
            return;
        }

        switch (requestCode)
        {
            case REQUEST_PICK_FROM_ALBUM:
                mModifiedDateTime = getPhotoDateTime(data.getData());
                copyChooseImageFile(data);
                Intent intent = new Intent("com.android.camera.action.CROP");
                intent.setDataAndType(mImageCaptureUri, "image/*");

                intent.putExtra("outputX", 1280);
                intent.putExtra("outputY", 800);
                intent.putExtra("aspectX", 16);
                intent.putExtra("aspectY", 10);
                intent.putExtra("scale", true);
                intent.putExtra("output", mImageCaptureUri);
                ((AppCompatActivity)mContext).startActivityForResult(intent, REQUEST_CROP_FROM_ALBUM);
                break;
            case REQUEST_CROP_FROM_ALBUM:
                mModifiedCheckList.put(Check.PHOTO, true);
                mModifiedCheckList.put(Check.DATE, true);

                mModifiedInformationContractView.changePhoto(mCropImageFile.getPath());
                mModifiedInformationContractView.changeDateInformation(CommonUtils.getInstance(mContext).getDateFullText(mModifiedDateTime)+" "+ CommonUtils.getInstance(mContext).getDateClock(mModifiedDateTime));
                break;
        }

    }

    @Override
    public void onRequestPermissionResult(int requestCode, String[] permissions, int[] grantResults)
    {

    }

    @Override
    public void sendMessageEvent(Message msg)
    {

    }

    @Override
    public void onClickSaveButton()
    {
        Log.f("");
        mModifiedInformationContractView.showLoading();
        saveModifiedInformation();
    }

    @Override
    public void onClickCancelButton()
    {
        Log.f("");
        ((AppCompatActivity)mContext).onBackPressed();
    }

    @Override
    public void onClickPhotoModifiedButton()
    {
        Log.f("");
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        ((AppCompatActivity)mContext).startActivityForResult(intent, REQUEST_PICK_FROM_ALBUM);
    }

    @Override
    public void onClickDateModifiedButton()
    {
        Log.f("");
        mModifiedInformationContractView.showDatePickerDialog();
    }

    @Override
    public void onClickCommentModifedButton()
    {
        Log.f("");
    }

    @Override
    public void onDateSetChanged(long dateTime)
    {
        Log.f("Full Time : "+ CommonUtils.getInstance(mContext).getDateFullText(dateTime));
        mModifiedCheckList.put(Check.DATE, true);
        mModifiedDateTime = dateTime;
        mModifiedInformationContractView.changeDateInformation(CommonUtils.getInstance(mContext).getDateFullText(mModifiedDateTime)+" "+ CommonUtils.getInstance(mContext).getDateClock(mModifiedDateTime));
    }


}
