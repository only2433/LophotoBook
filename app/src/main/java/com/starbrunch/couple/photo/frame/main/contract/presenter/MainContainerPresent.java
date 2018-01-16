package com.starbrunch.couple.photo.frame.main.contract.presenter;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.View;

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
import com.starbrunch.couple.photo.frame.main.fragment.ModifiedInformationFragment;
import com.starbrunch.couple.photo.frame.main.fragment.MonthListViewFragment;
import com.starbrunch.couple.photo.frame.main.handler.WeakReferenceHandler;
import com.starbrunch.couple.photo.frame.main.object.PhotoInformationObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

/**
 * Created by 정재현 on 2017-12-21.
 */

public class MainContainerPresent implements MainContainerCallback, MainContainerContract.Presenter
{
    private enum Check
    {
        PHOTO, DATE, COMMENT
    }

    private static final int PERMISSION_REQUEST = 100;

    private static final int REQUEST_PICK_FROM_ADD = 0;
    private static final int REQUEST_CROP_FROM_ADD = 1;

    private static final int REQUEST_PICK_FROM_MODIFY = 2;
    private static final int REQUEST_CROP_FROM_MODIFY = 3;

    private static final int MESSAGE_TITLE_INIT_COLOR = 0;
    private static final int MESSAGE_IMAGE_INFORMATION_SAVE = 1;
    private static final int MESSAGE_SAVE_COMPLETE = 2;

    private Context mContext = null;
    private MainViewFragment mMainViewFragment = null;
    private MonthListViewFragment mMonthListViewFragment = null;
    private ModifiedInformationFragment mModifiedInformationFragment = null;

    private FragmentManager mFragmentManager = null;
    private MainContainerContract.View mMainContainerContractView = null;
    private int mMonthPosition = 0;
    private int mSelectMonthColor = 0;
    private int mModifiedItemPosition = 0;
    private Uri mImageCaptureUri = null;
    private PhotoInformationDBHelper mPhotoInformationDBHelper = null;
    private WeakReferenceHandler mWeakReferenceHandler = null;
    private PhotoInformationObject mAddPhotoInformationObject = null;
    private PhotoInformationObject mModifiedInformationObject = null;
    private File mCropImageFile = null;
    private ArrayList<PhotoInformationObject> mCurrentPhotoInformationObjectList = null;
    private HashMap<Enum, Boolean> mModifiedCheckList = null;
    private Calendar mRequestCalendar = null;
    private long mModifiedDateTime = 0L;

    public MainContainerPresent(Context context)
    {
        mContext = context;

        Log.i("Common.PATH_APP_ROOT : "+ Common.PATH_APP_ROOT);
        Log.i("Common.PATH_IMAGE_ROOT : "+ Common.PATH_IMAGE_ROOT);
        mMainContainerContractView = (MainContainerContract.View)mContext;

        mPhotoInformationDBHelper = PhotoInformationDBHelper.getInstance(mContext);
        mWeakReferenceHandler = new WeakReferenceHandler((MainContainerActivity)mContext);
        mModifiedCheckList = new HashMap<>();
        settingInformation();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            CommonUtils.getInstance(mContext).requestPermission(new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST);
        }
    }

    @Override
    public void resume(){}

    @Override
    public void pause(){}

    @Override
    public void stop(){}

    @Override
    public void destroy(){}


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
        mCurrentPhotoInformationObjectList = mPhotoInformationDBHelper.getPhotoInformationListByMonth(Common.MONTH_TEXT_LIST[mMonthPosition]);

        Log.i("position : "+position+", list size : "+ mCurrentPhotoInformationObjectList.size());
        mMonthListViewFragment = new MonthListViewFragment();
        mMonthListViewFragment.setMainContainerCallback(this);

        Bundle bundle = new Bundle();

        mMainViewFragment.setExitTransition(CommonUtils.getInstance(mContext).getSlideTransition(Common.DURATION_DEFAULT));
        mMainViewFragment.setReenterTransition(CommonUtils.getInstance(mContext).getSlideTransition(Common.DURATION_DEFAULT));
        bundle.putInt(Common.INTENT_MONTH_POSITION, position);
        bundle.putParcelableArrayList(Common.INTENT_MONTH_PHOTO_LIST, mCurrentPhotoInformationObjectList);

        mMonthListViewFragment.setArguments(bundle);

        mFragmentManager.beginTransaction().replace(R.id._mainContainer, mMonthListViewFragment)
                .addToBackStack(null)
                .commit();

        mMainContainerContractView.setMonthNumberText(mSelectMonthColor, mCurrentPhotoInformationObjectList.size());
        mMainContainerContractView.changeTitleAnimationText(Common.MONTH_TEXT_LIST[mMonthPosition]);
    }


    private void startModifiedInformationFragment(Pair<View, String> item)
    {
        mModifiedInformationFragment = new ModifiedInformationFragment();
        mModifiedInformationFragment.setMainContainerCallback(this);

        Transition transitionInflater = TransitionInflater.from(mContext).inflateTransition(R.transition.change_bounds);

        String transitionName = item.second;

        Bundle bundle = new Bundle();
        bundle.putString(Common.INTENT_PHOTO_TRANSITION_NAME, transitionName);
        bundle.putParcelable(Common.INTENT_MODIFIED_ITEM_OBJECT, mModifiedInformationObject);

        mMonthListViewFragment.setSharedElementReturnTransition(transitionInflater);
        mModifiedInformationFragment.setSharedElementEnterTransition(transitionInflater);
        mModifiedInformationFragment.setArguments(bundle);

        mFragmentManager.beginTransaction().replace(R.id._mainContainer, mModifiedInformationFragment)
                .addToBackStack(null)
                .addSharedElement(item.first, transitionName)
                .commit();
    }

    private void  copyChooseImageFile(String keyID, Intent data)
    {
        File originalImageFile = CommonUtils.getInstance(mContext).getImageFile(data.getData());
        mImageCaptureUri = CommonUtils.getInstance(mContext).createImageFileUri(keyID);
        mCropImageFile = new File(mImageCaptureUri.getPath());
        FileUtils.copyFile(originalImageFile, mCropImageFile);
    }


    private void startCropActivity(int requestCode)
    {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(mImageCaptureUri, "image/*");

        intent.putExtra("outputX", 1280);
        intent.putExtra("outputY", 800);
        intent.putExtra("aspectX", 16);
        intent.putExtra("aspectY", 10);
        intent.putExtra("scale", true);
        intent.putExtra("output", mImageCaptureUri);
        ((AppCompatActivity)mContext).startActivityForResult(intent, requestCode);
    }

    private void startPickActivity(int requestCode)
    {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
        ((AppCompatActivity)mContext).startActivityForResult(intent, requestCode);
    }

    @Override
    public void acvitityResult(int requestCode, int resultCode, Intent data)
    {
        Log.f("requestCode : "+requestCode+", resultCode : "+resultCode);
        if(resultCode != ((AppCompatActivity)mContext).RESULT_OK)
        {
            return;
        }

        switch(requestCode)
        {
            case REQUEST_PICK_FROM_ADD:
                mAddPhotoInformationObject = CommonUtils.getInstance(mContext).getPhotoInformation(data.getData(),
                        Common.MONTH_TEXT_LIST[mMonthPosition]+"_"+System.currentTimeMillis(),
                        Common.MONTH_TEXT_LIST[mMonthPosition],
                        "");

                copyChooseImageFile(mAddPhotoInformationObject.getKeyID(), data);
                startCropActivity(REQUEST_CROP_FROM_ADD);
                break;
            case REQUEST_CROP_FROM_ADD:
                mWeakReferenceHandler.sendEmptyMessage(MESSAGE_IMAGE_INFORMATION_SAVE);
                break;
            case REQUEST_PICK_FROM_MODIFY:
                mModifiedDateTime = CommonUtils.getInstance(mContext).getPhotoDateTime(data.getData());
                copyChooseImageFile(mModifiedInformationObject.getKeyID(),data);
                startCropActivity(REQUEST_CROP_FROM_MODIFY);
                break;
            case REQUEST_CROP_FROM_MODIFY:
                mModifiedCheckList.put(Check.PHOTO, true);
                mModifiedCheckList.put(Check.DATE, true);
                mModifiedInformationFragment.changePhoto(BitmapFactory.decodeFile(mCropImageFile.getPath()));
                mModifiedInformationFragment.changeDateInformation(CommonUtils.getInstance(mContext).getDateFullText(mModifiedDateTime)+" "+ CommonUtils.getInstance(mContext).getDateClock(mModifiedDateTime));
                break;

        }

    }

    @Override
    public void requestPermissionResult(int requestCode, String[] permissions, int[] grantResults)
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
    public void sendMessageEvent(Message msg)
    {
        switch (msg.what)
        {

            case MESSAGE_TITLE_INIT_COLOR:
                mMainContainerContractView.changeTitleViewColor(R.color.main_title_color);
                break;
            case MESSAGE_IMAGE_INFORMATION_SAVE:

                boolean isSuccess = CommonUtils.getInstance(mContext).saveJpegFile(mImageCaptureUri,mAddPhotoInformationObject.getFileName());
                if(isSuccess)
                {
                    FileUtils.deleteFile(mCropImageFile.getPath());
                    Log.i("add KeyID : "+ mAddPhotoInformationObject.getKeyID());
                    mPhotoInformationDBHelper.addPhotoInformationObject(mAddPhotoInformationObject);

                    mMonthListViewFragment.insertItem(mAddPhotoInformationObject);
                    mMainContainerContractView.setMonthNumberText(mSelectMonthColor, mPhotoInformationDBHelper.getPhotoInformationListByMonth(Common.MONTH_TEXT_LIST[mMonthPosition]).size());
                    CommonUtils.getInstance(mContext).updateWidget();
                }
                break;
            case MESSAGE_SAVE_COMPLETE:
                mMainContainerContractView.hideLoading();
                CommonUtils.getInstance(mContext).updateWidget();
                ((AppCompatActivity)mContext).onBackPressed();
                PhotoInformationObject updatePhotoInformationObject = mPhotoInformationDBHelper.getPhotoInformationObject(mModifiedInformationObject.getKeyID());
                mMonthListViewFragment.notifyChanged(mModifiedItemPosition,updatePhotoInformationObject);
                break;

        }
    }

    @Override
    public void changeDateSetComplete(int year, int monthOfYear, int dayOfMonth)
    {
        mRequestCalendar.set(Calendar.YEAR,year);
        mRequestCalendar.set(Calendar.MONTH,monthOfYear);
        mRequestCalendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);
        mMainContainerContractView.showTimePickerDialog();
    }

    @Override
    public void changeTimeSetComplete(int hourOfDay, int minute)
    {

        mModifiedCheckList.put(Check.DATE, true);

        mRequestCalendar.set(Calendar.HOUR_OF_DAY,hourOfDay);
        mRequestCalendar.set(Calendar.MINUTE, minute);
        mModifiedDateTime = mRequestCalendar.getTimeInMillis();
        mModifiedInformationFragment.changeDateInformation(CommonUtils.getInstance(mContext).getDateFullText(mModifiedDateTime)+" "+ CommonUtils.getInstance(mContext).getDateClock(mModifiedDateTime));
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
    public void onAddPhoto()
    {
        Log.f("");
        startPickActivity(REQUEST_PICK_FROM_ADD);
    }


    @Override
    public void onDeletePhoto(String keyID)
    {
        Log.f("Delete keyID : "+keyID);
        PhotoInformationObject object = mPhotoInformationDBHelper.getPhotoInformationObject(keyID);
        Log.f("Delete FileName : "+Common.PATH_IMAGE_ROOT+object.getFileName());

        FileUtils.deleteFile(Common.PATH_IMAGE_ROOT+object.getFileName());
        Log.f("Delete CheckFile : "+FileUtils.checkFile(Common.PATH_IMAGE_ROOT+object.getFileName()));
        mPhotoInformationDBHelper.deletePhotoInformationObject(keyID);
        mMainContainerContractView.setMonthNumberText(mSelectMonthColor, mPhotoInformationDBHelper.getPhotoInformationListByMonth(Common.MONTH_TEXT_LIST[mMonthPosition]).size());
        mMonthListViewFragment.deleteItem();
        CommonUtils.getInstance(mContext).updateWidget();
    }

    @Override
    public void onModifiedPhoto(int position, Pair<View, String> item)
    {
        Log.f("");
        mModifiedItemPosition = position;
        mModifiedInformationObject = mCurrentPhotoInformationObjectList.get(mModifiedItemPosition);
        startModifiedInformationFragment(item);
        mMainContainerContractView.hideMainTitleLayout();
    }

    @Override
    public void onModifiedEnd()
    {
        mMainContainerContractView.showMainTitleLayout();
    }

    @Override
    public void onModifiedItemSave()
    {
        Log.i("");
        mMainContainerContractView.showLoading();
        saveModifiedInformation();
    }

    @Override
    public void onModifiedItemCancel()
    {
        Log.f("");
        ((AppCompatActivity)mContext).onBackPressed();
    }

    @Override
    public void onSelectPhotoModified()
    {
        Log.f("");
        startPickActivity(REQUEST_PICK_FROM_MODIFY);
    }

    @Override
    public void onSelectDateModified()
    {
        Log.f("");
        mRequestCalendar = Calendar.getInstance();
        mMainContainerContractView.showDatePickerDialog();
    }

    @Override
    public void onSelectCommentModifed()
    {

    }


    private void saveModifiedInformation()
    {
        if(mModifiedCheckList.containsKey(Check.PHOTO) && mModifiedCheckList.get(Check.PHOTO) == true)
        {
            Log.f("PHOTO CHANGE");
            boolean isSuccess = CommonUtils.getInstance(mContext).saveJpegFile(mImageCaptureUri, mModifiedInformationObject.getFileName());

            if(isSuccess)
            {
                Log.f("DB 갱신 성공");
                FileUtils.deleteFile(mCropImageFile.getPath());
                //DB에 옮긴다.
            }
        }

        if(mModifiedCheckList.containsKey(Check.DATE) && mModifiedCheckList.get(Check.DATE) == true)
        {
            Log.f("DATE CHANGE");
            mPhotoInformationDBHelper.updatePhotoInformationObject(mModifiedInformationObject.getKeyID(), PhotoInformationDBHelper.KEY_DATE_MILLISECOND, String.valueOf(mModifiedDateTime));
        }

        mWeakReferenceHandler.sendEmptyMessageDelayed(MESSAGE_SAVE_COMPLETE, 2000);

    }


}
