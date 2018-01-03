package com.starbrunch.couple.photo.frame.main.contract.presenter;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.littlefox.library.system.common.FileUtils;
import com.littlefox.logmonitor.Log;
import com.starbrunch.couple.photo.frame.main.MainContainerActivity;
import com.starbrunch.couple.photo.frame.main.R;
import com.starbrunch.couple.photo.frame.main.callback.MainContainerCallback;
import com.starbrunch.couple.photo.frame.main.common.Common;
import com.starbrunch.couple.photo.frame.main.common.CommonUtils;
import com.starbrunch.couple.photo.frame.main.contract.MainContainerContract;
import com.starbrunch.couple.photo.frame.main.database.PhotoInformationDBHelper;
import com.starbrunch.couple.photo.frame.main.fragment.MainViewFragment;
import com.starbrunch.couple.photo.frame.main.fragment.MonthListViewFragment;
import com.starbrunch.couple.photo.frame.main.handler.WeakReferenceHandler;
import com.starbrunch.couple.photo.frame.main.object.PhotoInformationObject;

import java.io.File;

/**
 * Created by 정재현 on 2017-12-21.
 */

public class MainContainerPresent implements MainContainerCallback, MainContainerContract.Presenter
{

    private static final int PERMISSION_REQUEST = 100;

    private static final int PICK_FROM_ALBUM    = 0;
    private static final int CROP_FROM_ALBUM    = 1;

    private static final int MESSAGE_TITLE_INIT_COLOR = 0;
    private static final int MESSAGE_IMAGE_INFORMATION_SAVE = 1;

    private Context mContext = null;
    private MainViewFragment mMainViewFragment = null;
    private MonthListViewFragment mMonthListViewFragment = null;
    private FragmentManager mFragmentManager = null;
    private MainContainerContract.View mMainContainerContractView = null;
    private int mMonthPosition = 0;
    private int mSelectMonthColor = 0;
    private Uri mImageCaptureUri = null;
    private PhotoInformationDBHelper mPhotoInformationDBHelper = null;
    private WeakReferenceHandler mWeakReferenceHandler = null;
    private PhotoInformationObject mCurrentPhotoInformationObject = null;
    private File mCropImageFile = null;

    public MainContainerPresent(Context context)
    {
        mContext = context;

        Log.i("Common.PATH_APP_ROOT : "+ Common.PATH_APP_ROOT);
        Log.i("Common.PATH_IMAGE_ROOT : "+ Common.PATH_IMAGE_ROOT);
        mMainContainerContractView = (MainContainerContract.View)mContext;

        mPhotoInformationDBHelper = PhotoInformationDBHelper.getInstance(mContext);
        mWeakReferenceHandler = new WeakReferenceHandler((MainContainerActivity)mContext);
        settingInformation();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            CommonUtils.getInstance(mContext).requestPermission(new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST);
        }
    }

    private void settingInformation()
    {
        mMainContainerContractView.initView();

        mFragmentManager = ((AppCompatActivity)mContext).getSupportFragmentManager();
        mMainViewFragment = new MainViewFragment();
        mMainViewFragment.setMainContainerCallback(this);

        mFragmentManager.beginTransaction()
                .replace(R.id._mainContainer, mMainViewFragment)
                .commit();
    }

    private void startMonthListViewFragment(int position)
    {
        mMonthPosition = position;
        mMonthListViewFragment = new MonthListViewFragment();
        mMonthListViewFragment.setMainContainerCallback(this);

        Bundle bundle = new Bundle();

        mMainViewFragment.setExitTransition(CommonUtils.getInstance(mContext).getSlideTransition(Common.DURATION_DEFAULT));
        mMainViewFragment.setReenterTransition(CommonUtils.getInstance(mContext).getSlideTransition(Common.DURATION_DEFAULT));

        bundle.putInt(Common.SHARED_ELEMENT_MONTH_POSITION, position);

        mMonthListViewFragment.setArguments(bundle);

        mFragmentManager.beginTransaction().replace(R.id._mainContainer, mMonthListViewFragment)
                .addToBackStack(null)
                .commit();

        mMainContainerContractView.setMonthNumberText(mSelectMonthColor, mPhotoInformationDBHelper.getPhotoInformationListByMonth(Common.MONTH_TEXT_LIST[mMonthPosition]).size());
        mMainContainerContractView.changeTitleAnimationText(Common.MONTH_TEXT_LIST[mMonthPosition]);

    }

    private PhotoInformationObject getPhotoInformation(Uri uri)
    {
        final int INDEX_DATE_TIME   = 0;
        final int INDEX_LATITUDE    = 1;
        final int INDEX_LONGITUDE   = 2;
        PhotoInformationObject result = null;

        String[] projection = {MediaStore.Images.Media.DATE_TAKEN, MediaStore.Images.Media.LATITUDE, MediaStore.Images.Media.LONGITUDE};
        Cursor cursor = ((AppCompatActivity)mContext).getContentResolver().query(uri, projection, null, null, null);
        cursor.moveToFirst();

        if(cursor != null && cursor.getCount() != 0)
        {
            Log.f("cursor size : "+ cursor.getCount());
            Log.f(Common.MONTH_TEXT_LIST[mMonthPosition]+" Photo Information size : "+ mMonthListViewFragment.getPhotoInformationSize());

            long millisecond = Long.valueOf(cursor.getString(INDEX_DATE_TIME));
            Log.i("cursor DATE_TAKEN  : "+CommonUtils.getInstance(mContext).getPayInformationDate(false, millisecond));
            try
            {
                Log.i("cursor LATITUDE : "+cursor.getString(INDEX_LATITUDE));
                Log.i("cursor LONGITUDE : "+cursor.getString(INDEX_LONGITUDE));
            }catch(Exception e)
            {
                Log.i("Error Message : "+ e.getMessage());
            }

            result = new PhotoInformationObject(
                    Common.MONTH_TEXT_LIST[mMonthPosition]+"_"+mMonthListViewFragment.getPhotoInformationSize(),
                    Common.MONTH_TEXT_LIST[mMonthPosition],
                    Long.valueOf(cursor.getString(INDEX_DATE_TIME)),
                    cursor.getString(INDEX_LATITUDE) == null ? 0.0f : Float.valueOf(cursor.getString(INDEX_LATITUDE)),
                    cursor.getString(INDEX_LONGITUDE) == null ? 0.0f :Float.valueOf(cursor.getString(INDEX_LONGITUDE)),
                    ""
            );

        }
        return result;
    }

    private void copyChooseImageFile(Intent data)
    {
        File originalImageFile = CommonUtils.getInstance(mContext).getImageFile(data.getData());
        mImageCaptureUri = CommonUtils.getInstance(mContext).createImageFileUri(Common.MONTH_TEXT_LIST[mMonthPosition]+"_"+mMonthListViewFragment.getPhotoInformationSize());
        mCropImageFile = new File(mImageCaptureUri.getPath());
        Log.i("mImageCaptureUri.getPath() : "+mImageCaptureUri.getPath());
        Log.i("originalImageFile path : "+originalImageFile.getPath());
        Log.i("cropImageFile path : "+ mCropImageFile.getPath());
        FileUtils.copyFile(originalImageFile, mCropImageFile);


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
    public void init(Object object)
    {


    }


    @Override
    public void onAcvitityResult(int requestCode, int resultCode, Intent data)
    {
        if(resultCode != ((AppCompatActivity)mContext).RESULT_OK)
        {
            return;
        }

        switch(requestCode)
        {
            case PICK_FROM_ALBUM:


                mCurrentPhotoInformationObject = getPhotoInformation(data.getData());
                copyChooseImageFile(data);

                Intent intent = new Intent("com.android.camera.action.CROP");
                intent.setDataAndType(mImageCaptureUri, "image/*");

                intent.putExtra("outputX", 1280);
                intent.putExtra("outputY", 800);
                intent.putExtra("aspectX", 16);
                intent.putExtra("aspectY", 10);
                intent.putExtra("scale", true);
                intent.putExtra("output", mImageCaptureUri);
                ((AppCompatActivity)mContext).startActivityForResult(intent, CROP_FROM_ALBUM);

                break;
            case CROP_FROM_ALBUM:
                mWeakReferenceHandler.sendEmptyMessage(MESSAGE_IMAGE_INFORMATION_SAVE);

                break;

        }

    }

    @Override
    public void onRequestPermissionResult(int requestCode, String[] permissions, int[] grantResults)
    {
        boolean isAllCheckSuccess = true;
        switch(requestCode)
        {
            case PERMISSION_REQUEST:
                for(int i = 0; i < permissions.length; i++)
                {
                    if(grantResults[i] != PackageManager.PERMISSION_GRANTED)
                    {
                        isAllCheckSuccess = false;
                    }
                }

                if(isAllCheckSuccess == false)
                {
                    ((AppCompatActivity)mContext).finish();
                }
        }
    }



    @Override
    public void onSelectMonth(int position)
    {
        mSelectMonthColor = mContext.getResources().getIdentifier("color_month_"+(position+1), "color", Common.PACKAGE_NAME);

        startMonthListViewFragment(position);
    }

    @Override
    public void onGotoMainView()
    {
        mWeakReferenceHandler.sendEmptyMessageDelayed(MESSAGE_TITLE_INIT_COLOR, Common.DURATION_DEFAULT);
        mMainContainerContractView.hideMonthNumberAnimation();
        mMainContainerContractView.changeTitleAnimationText(mContext.getResources().getString(R.string.app_name));
        mMainContainerContractView.hideTitleViewBackgroundAnimation(mSelectMonthColor);
    }

    @Override
    public void onGotoMonthListView()
    {
        mMainContainerContractView.changeTitleViewColor(R.color.color_white);
        mMainContainerContractView.showMonthNumberAnimation();
        mMainContainerContractView.showTitleViewBackgroundAnimation(mSelectMonthColor);
    }


    @Override
    public void onAddPicture()
    {
        Log.i("");

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
        ((AppCompatActivity)mContext).startActivityForResult(intent, PICK_FROM_ALBUM);

    }


    @Override
    public void onDeletePicture()
    {

    }

    @Override
    public void sendMessageEvent(Message msg)
    {
        switch (msg.what)
        {

            case MESSAGE_TITLE_INIT_COLOR:
                mMainContainerContractView.changeTitleViewColor(R.color.main_title_color);
                break;
            case MESSAGE_IMAGE_INFORMATION_SAVE:

                boolean isSuccess = CommonUtils.getInstance(mContext).saveJpegFile(mImageCaptureUri,Common.MONTH_TEXT_LIST[mMonthPosition]+"_"+mMonthListViewFragment.getPhotoInformationSize());
                if(isSuccess)
                {
                    FileUtils.deleteFile(mCropImageFile.getPath());
                    mPhotoInformationDBHelper.addPhotoInformationObject(mCurrentPhotoInformationObject);
                    //TODO: 리스트 프레그먼트 갱신
                    mMonthListViewFragment.updateView();
                    mMainContainerContractView.setMonthNumberText(mSelectMonthColor, mPhotoInformationDBHelper.getPhotoInformationListByMonth(Common.MONTH_TEXT_LIST[mMonthPosition]).size());

                }

                break;


        }
    }

}
