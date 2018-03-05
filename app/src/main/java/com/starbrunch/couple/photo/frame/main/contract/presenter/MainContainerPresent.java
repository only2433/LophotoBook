package com.starbrunch.couple.photo.frame.main.contract.presenter;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.littlefox.library.system.async.listener.AsyncListener;
import com.littlefox.library.system.common.FileUtils;
import com.littlefox.logmonitor.Log;
import com.starbrunch.couple.photo.frame.main.MainContainerActivity;
import com.starbrunch.couple.photo.frame.main.R;
import com.starbrunch.couple.photo.frame.main.SettingContainerActivity;
import com.starbrunch.couple.photo.frame.main.async.CompressorAsync;
import com.starbrunch.couple.photo.frame.main.async.UnCompressorAsync;
import com.starbrunch.couple.photo.frame.main.bluetooth.BluetoothController;
import com.starbrunch.couple.photo.frame.main.callback.MainContainerCallback;
import com.starbrunch.couple.photo.frame.main.common.Common;
import com.starbrunch.couple.photo.frame.main.common.CommonUtils;
import com.starbrunch.couple.photo.frame.main.contract.MainContainerContract;
import com.starbrunch.couple.photo.frame.main.database.PhotoInformationDBHelper;
import com.starbrunch.couple.photo.frame.main.dialog.BluetoothScanDialog;
import com.starbrunch.couple.photo.frame.main.fragment.DataCommunicateFragment;
import com.starbrunch.couple.photo.frame.main.fragment.MainViewFragment;
import com.starbrunch.couple.photo.frame.main.fragment.ModifiedInformationFragment;
import com.starbrunch.couple.photo.frame.main.fragment.MonthListViewFragment;
import com.starbrunch.couple.photo.frame.main.handler.WeakReferenceHandler;
import com.starbrunch.couple.photo.frame.main.object.MessageObject;
import com.starbrunch.couple.photo.frame.main.object.PhotoInformationListObject;
import com.starbrunch.couple.photo.frame.main.object.PhotoInformationObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Set;

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

    private static final int REQUEST_PICK_FROM_ADD      = 0;
    private static final int REQUEST_CROP_FROM_ADD      = 1;
    private static final int REQUEST_PICK_FROM_MODIFY   = 2;
    private static final int REQUEST_CROP_FROM_MODIFY   = 3;

    private static final int REQUEST_SETTING                = 101;
    private static final int REQUEST_BLUETOOTH_ENABLE       = 102;
    private static final int REQUEST_BLUETOOTH_DISCOVERY    = 103;

    private static final int MESSAGE_TITLE_INIT_COLOR           = 0;
    private static final int MESSAGE_IMAGE_INFORMATION_SAVE     = 1;
    private static final int MESSAGE_SAVE_COMPLETE              = 2;
    private static final int MESSAGE_FILE_COMPRESSOR            = 3;
    private static final int MESSAGE_FILE_UNCOMPRESSOR          = 4;

    // Receive 기기가 데이터를 받고 세팅이 전부 끝났을때 사용
    private static final int MESSAGE_RECEIVE_SETTING_COMPLETE   = 5;

    // Send 기기와 Receive 기기가 연결이 완료 되었을 때
    public static final int MESSAGE_BLUETOOTH_LINK_COMPLETE         = 200;

    // Send 기기와 Receive 기기 끼리 메세지를 주고 받을때 사용
    public static final int MESSAGE_BLUETOOTH_INFORMATION_READ      = 201;

    // Receive 기기의 데이터를 받을때 UI를 갱신 시키기 위해 사용
    public static final int MESSAGE_BLUETOOTH_DATA_RECEIVE_UI       = 202;

    // Send 기기의 데이터를 받을때 UI를 갱신 시키기 위해 사용
    public static final int MESSAGE_BLUETOOTH_DATA_SEND_UI          = 203;

    // Receive 기기가 데이터를 전부 받았을 때의 처리
    public static final int MESSAGE_BLUETOOTH_DATA_RECEIVE_COMPLETE = 204;

    // Send 기기가 데이터를 전부 보냈을 때의 처리
    public static final int MESSAGE_BLUETOOTH_DATA_SEND_COMPLETE    = 205;

    // Send 기기에서 파일을 보내기 시작 하기 위한 메세지
    public static final int MESSAGE_BLUETOOTH_FILE_SEND_SIGNAL      = 206;

    // 두 기기의 연결이 안되었을 때 의 처리
    public static final int MESSAGE_BLUETOOTH_CONNECTION_FAIL       = 1001;

    // 두 기기가 연결 도중 끊어졌을 때의 처리
    public static final int MESSAGE_BLUETOOTH_CONNECTION_LOST       = 1002;

    // 블루투스 통신 도중 발생 상황에 대한 메세지를 보내기 위해 사용
    public static final int MESSAGE_BLUETOOTH_TOAST                 = 10001;

    public static final int SCENE_MAIN_VIEW             = 0;
    public static final int SCENE_MONTH_LIST_VIEW       = 1;
    public static final int SCENE_MODIFIED_INFORMATION  = 2;
    public static final int SCENE_DATA_COMMUNICATE      = 3;

    private Context mContext = null;
    private MainViewFragment mMainViewFragment = null;
    private MonthListViewFragment mMonthListViewFragment = null;
    private ModifiedInformationFragment mModifiedInformationFragment = null;
    private DataCommunicateFragment mDataCommunicateFragment = null;

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

    private HashMap<Enum, Boolean> mModifiedCheckList = null;
    private Calendar mRequestCalendar = null;
    private long mModifiedDateTime = 0L;

    private int mCurrentViewState = SCENE_MAIN_VIEW;

    private int mCurrentSettingType = -1;
    private boolean isTransferFileComplete = false;
    /**
     * 연결할 Bluetooth Address
     */
    private String mCurrentRemoteAddress = "";

    private BluetoothController mBluetoothController = null;
    private BluetoothScanDialog mBluetoothScanDialog = null;

    public MainContainerPresent(Context context)
    {
        mContext = context;

        mMainContainerContractView = (MainContainerContract.View)mContext;
        mPhotoInformationDBHelper = PhotoInformationDBHelper.getInstance(mContext);
        mWeakReferenceHandler = new WeakReferenceHandler((MainContainerActivity)mContext);
        mBluetoothController = new BluetoothController(mContext, mWeakReferenceHandler);

        mModifiedCheckList = new HashMap<>();

        settingInformation();

        initReceiver();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            CommonUtils.getInstance(mContext).requestPermission(new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST);
        }

    }

    @Override
    public void resume(){}

    @Override
    public void pause(){}

    @Override
    public void stop(){}

    @Override
    public void destroy()
    {
        Log.i("");
        if (mBluetoothController != null)
        {
            mBluetoothController.stop();
        }
        ((AppCompatActivity)mContext).unregisterReceiver(mBluetoothScanReceiver);
    }

    private void initReceiver()
    {
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        ((AppCompatActivity)mContext).registerReceiver(mBluetoothScanReceiver, filter);

        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        ((AppCompatActivity)mContext).registerReceiver(mBluetoothScanReceiver, filter);
    }

    private void settingInformation()
    {
        mMainContainerContractView.initFont();
        mMainContainerContractView.initView();

        mFragmentManager = ((AppCompatActivity)mContext).getSupportFragmentManager();
        mMainViewFragment = new MainViewFragment();
        mMainViewFragment.setMainContainerCallback(this);

        mFragmentManager.beginTransaction()
                .replace(R.id._mainContainer, mMainViewFragment)
                .commit();
        mMainContainerContractView.showSettingButton(Common.DURATION_SHORT);
    }

    private void startDataCommunicateFragment()
    {
        mMainContainerContractView.initFont();
        mMainContainerContractView.initView();

        mMainContainerContractView.hideMainTitleLayout();
        mMainContainerContractView.invisibleFloatButton();

        mMainViewFragment.setExitTransition(CommonUtils.getInstance(mContext).getSlideTransition(Gravity.LEFT, 0,Common.DURATION_DEFAULT));
        mMainViewFragment.setReenterTransition(CommonUtils.getInstance(mContext).getSlideTransition(Gravity.LEFT, 300,Common.DURATION_DEFAULT));

        Bundle bundle = new Bundle();
        bundle.putInt(Common.INTENT_SETTING_SELECT_INDEX, mCurrentSettingType);

        mDataCommunicateFragment = new DataCommunicateFragment();
        mDataCommunicateFragment.setArguments(bundle);
        mDataCommunicateFragment.setMainContainerCallback(this);

        mDataCommunicateFragment.setEnterTransition(CommonUtils.getInstance(mContext).getSlideTransition(Gravity.RIGHT, 300,Common.DURATION_DEFAULT));
        mDataCommunicateFragment.setReturnTransition(CommonUtils.getInstance(mContext).getSlideTransition(Gravity.RIGHT, 0, Common.DURATION_DEFAULT));

        mFragmentManager.beginTransaction()


                .replace(R.id._mainContainer, mDataCommunicateFragment)
                .addToBackStack(null)
                .commit();
    }

    private void startMonthListViewFragment(int position)
    {

        ArrayList<PhotoInformationObject> photoInformationList = getPhotoInformationMonthList(mMonthPosition);

        Log.i("position : "+position+", list size : "+ photoInformationList.size());
        mMonthListViewFragment = new MonthListViewFragment();
        mMonthListViewFragment.setMainContainerCallback(this);

        Bundle bundle = new Bundle();

        mMainViewFragment.setExitTransition(CommonUtils.getInstance(mContext).getSlideTransition(Gravity.LEFT, 0,Common.DURATION_DEFAULT));
        mMainViewFragment.setReenterTransition(CommonUtils.getInstance(mContext).getSlideTransition(Gravity.LEFT, 0,Common.DURATION_DEFAULT));


        bundle.putParcelableArrayList(Common.INTENT_MONTH_PHOTO_LIST, photoInformationList);

        mMonthListViewFragment.setArguments(bundle);

        mFragmentManager.beginTransaction().replace(R.id._mainContainer, mMonthListViewFragment)
                .addToBackStack(null)
                .commit();

        mMainContainerContractView.setMonthNumberText(mSelectMonthColor, photoInformationList.size());
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

        intent.putExtra("outputX", 640);
        intent.putExtra("outputY", 400);
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

    private void startSettingActivity()
    {
        Intent intent = new Intent(mContext, SettingContainerActivity.class);
        ((AppCompatActivity)mContext).startActivityForResult(intent, REQUEST_SETTING);
        ((AppCompatActivity) mContext).overridePendingTransition(R.anim.slide_left_in, R.anim.slide_left_out);
    }

    private void showBluetoothScanDialog()
    {
        if(mBluetoothScanDialog == null)
        {
            mBluetoothScanDialog = new BluetoothScanDialog(mContext);
            mBluetoothScanDialog.setBluetoothScanListener(mBluetoothScanListener);
        }
        mBluetoothScanDialog.show();
        mBluetoothScanDialog.showLoading();
    }

    private void hideBluetoothScanDialog()
    {
        if(mBluetoothScanDialog != null)
        {
            mBluetoothScanDialog.dismiss();
            mBluetoothScanDialog = null;
        }
    }

    private void startDiscovery()
    {
        setPairBlutooth();
        if(mBluetoothController.isStartDiscovering())
        {
            mBluetoothController.cancelDiscovery();
        }

        mBluetoothController.startDiscovery();
    }

    private void setPairBlutooth()
    {
        if(mBluetoothScanDialog != null)
        {
            Set<BluetoothDevice> pairedDevices = mBluetoothController.getBondedDevices();

            if(pairedDevices.size() > 0)
            {
                for(BluetoothDevice device : pairedDevices)
                {
                    Log.i("device.getName() : "+device.getName()+", device.getAddress() : "+device.getAddress() +", type : " +device.getBluetoothClass().getDeviceClass());

                    if(device.getBluetoothClass().getDeviceClass() == BluetoothClass.Device.PHONE_SMART)
                    {
                        mBluetoothScanDialog.addData(device.getName(), device.getAddress());
                    }

                }
            }
        }
    }


    /**
     * Makes this device discoverable for 300 seconds (5 minutes).
     */
    private void enableBluetoothDiscoverable()
    {
        Log.i("mBluetoothController.isConnectDiscoverable() : "+mBluetoothController.isConnectDiscoverable());


        mBluetoothController.startConnectDiscoverable(REQUEST_BLUETOOTH_DISCOVERY);
    }

    private void enableBluetooth()
    {
        mBluetoothController.startBluetoothEnable(REQUEST_BLUETOOTH_ENABLE);
    }

    private void startCompressorAsync()
    {
        //Toast.makeText(mContext, "압축을 시도중 입니다.", Toast.LENGTH_SHORT).show();

        String targetPath = Common.PATH_APP_ROOT;
        String destinationFilePath = Common.PATH_EXTERNAL_ZIP_ROOT + Common.ZIP_FILE_NAME;

        CompressorAsync async = new CompressorAsync(mContext);
        async.setData(targetPath,destinationFilePath);
        async.setAsyncListener(mOnAsyncListener);
        async.execute();
    }

    private void startUnCompressorAsync()
    {
        String targetZipFile = Common.PATH_EXTERNAL_ZIP_ROOT+ Common.ZIP_FILE_NAME;
        String destinationPath = Common.PATH_BASE_APP_ROOT;

        UnCompressorAsync async = new UnCompressorAsync(mContext);
        async.setData(targetZipFile, destinationPath);
        async.setAsyncListener(mOnAsyncListener);
        async.execute();
    }

    private void makePhotoInformationFile()
    {
        ArrayList<PhotoInformationObject> list = mPhotoInformationDBHelper.getPhotoInformationList();
        PhotoInformationListObject object = new PhotoInformationListObject(list);
        FileUtils.writeFile(object, Common.PATH_APP_ROOT+Common.PHOTO_INFORMATION_FILE_NAME);
    }

    private ArrayList<PhotoInformationObject> getPhotoInformationFromFile()
    {
        String fileInformation = FileUtils.getStringFromFile(Common.PATH_APP_ROOT+Common.PHOTO_INFORMATION_FILE_NAME);
        PhotoInformationListObject object = new Gson().fromJson(fileInformation, PhotoInformationListObject.class);

        Log.i("list size : "+ object.getPhotoInformationListObjectList().size());
        return object.getPhotoInformationListObjectList();
    }

    private void initSetPhotoInformaionList(ArrayList<PhotoInformationObject> list)
    {
        for(int i = 0; i < list.size() ; i++)
        {
            mPhotoInformationDBHelper.addPhotoInformationObject(list.get(i));
        }
    }

    private void sendMessage(MessageObject object)
    {
        String message = new Gson().toJson(object);
        byte[] readyMessageByte = message.getBytes();
        mBluetoothController.writeInformation(readyMessageByte);
    }

    /**
     * 해당 월의 DB정보에서 Photo Information 의 list 를 넘겨준다.
     * @param monthPosition 해당 월
     * @return
     */
    private ArrayList<PhotoInformationObject> getPhotoInformationMonthList(int monthPosition)
    {
        return mPhotoInformationDBHelper.getPhotoInformationListByMonth(Common.MONTH_TEXT_LIST[monthPosition]);
    }

    @Override
    public void acvitityResult(int requestCode, int resultCode, Intent data) {
        Log.f("requestCode : " + requestCode + ", resultCode : " + resultCode);

        if(requestCode == REQUEST_BLUETOOTH_ENABLE && resultCode == ((AppCompatActivity) mContext).RESULT_CANCELED)
        {
            Log.i("Bluetooth enable fail");
            mMainContainerContractView.showMessage(mContext.getResources().getString(R.string.message_not_enable_bluetooth),
                    mContext.getResources().getColor(R.color.color_white));
        }
        else if(requestCode == REQUEST_BLUETOOTH_DISCOVERY && resultCode == ((AppCompatActivity) mContext).RESULT_CANCELED)
        {
            Log.i("Bluetooth discovery fail");
            mMainContainerContractView.showMessage(mContext.getResources().getString(R.string.message_not_discovery_enable),
                    mContext.getResources().getColor(R.color.color_white));
        }
        else if (resultCode == ((AppCompatActivity)mContext).RESULT_CANCELED)
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
            case REQUEST_SETTING:
                //TODO : BLUETOOTH 선택에 따른 행동
                mCurrentSettingType = data.getIntExtra(Common.INTENT_SETTING_SELECT_INDEX, 0);
                Log.i("isBluetoothEnable() : " + mBluetoothController.isBluetoothEnable());
                switch (mCurrentSettingType)
                {
                    case Common.RESULT_SETTING_BLUETOOTH_SEND:
                    case Common.RESULT_SETTING_BLUETOOTH_RECEIVE:

                       if (mBluetoothController.isBluetoothEnable() == false)
                        {
                            enableBluetooth();
                        }
                        else
                        {
                            if (mCurrentSettingType == Common.RESULT_SETTING_BLUETOOTH_SEND)
                            {
                                Log.i("Send");
                                showBluetoothScanDialog();
                                startDiscovery();
                            }
                            else if(mCurrentSettingType == Common.RESULT_SETTING_BLUETOOTH_RECEIVE)
                            {
                                mBluetoothController.start();
                                Log.i("Receive");
                                enableBluetoothDiscoverable();
                            }
                        }
                        break;
                }
                break;
            case REQUEST_BLUETOOTH_ENABLE:

                Log.i("Bluetooth enable success : "+mCurrentSettingType);
                if (mCurrentSettingType == Common.RESULT_SETTING_BLUETOOTH_SEND)
                {

                    showBluetoothScanDialog();
                    startDiscovery();

                }
                else
                {
                    mBluetoothController.start();
                    Log.i("Receive");
                    enableBluetoothDiscoverable();
                }

                break;
            case REQUEST_BLUETOOTH_DISCOVERY:
                Log.i("Bluetooth discovery success : "+mCurrentSettingType);


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
                    mMainContainerContractView.setMonthNumberText(mSelectMonthColor, getPhotoInformationMonthList(mMonthPosition).size());
                    CommonUtils.getInstance(mContext).updateWidget();


                    if(getPhotoInformationMonthList(mMonthPosition).size() >= Common.MAX_PHOTO_ITEM)
                    {
                        mMainContainerContractView.invisibleFloatButton();
                    }
                }
                break;

            case MESSAGE_SAVE_COMPLETE:
                mMainContainerContractView.hideLoading();
                CommonUtils.getInstance(mContext).updateWidget();
                ((AppCompatActivity)mContext).onBackPressed();
                PhotoInformationObject updatePhotoInformationObject = mPhotoInformationDBHelper.getPhotoInformationObject(mModifiedInformationObject.getKeyID());
                mMonthListViewFragment.notifyChanged(mModifiedItemPosition,updatePhotoInformationObject);
                break;

            case MESSAGE_FILE_COMPRESSOR:
                makePhotoInformationFile();
                startCompressorAsync();
                break;

            case MESSAGE_FILE_UNCOMPRESSOR:
                mMainContainerContractView.showLoading();
                startUnCompressorAsync();
                break;

            case MESSAGE_RECEIVE_SETTING_COMPLETE:
                mMainContainerContractView.hideLoading();
                FileUtils.deleteAllFileInPath(Common.PATH_EXTERNAL_ZIP_ROOT);
                mMainContainerContractView.showMessage(mContext.getResources().getString(R.string.message_receive_complete),
                        mContext.getResources().getColor(R.color.color_37b09b));
                break;

            case MESSAGE_BLUETOOTH_LINK_COMPLETE:
                //TODO:
                Log.i("MESSAGE_BLUETOOTH_READY_TO_SEND_INFORMATION");
                startDataCommunicateFragment();
                break;

            case MESSAGE_BLUETOOTH_INFORMATION_READ:
                byte[] readBuf = (byte[]) msg.obj;
                String readMessage = new String(readBuf, 0, msg.arg1);
                Log.i("readMessage : "+readMessage);
                try
                {
                    MessageObject object = new Gson().fromJson(readMessage, MessageObject.class);

                    if(mCurrentSettingType == Common.RESULT_SETTING_BLUETOOTH_SEND)
                    {
                        Log.i("SEND object.code : "+object.code);

                        if(object.code == Common.BLUETOOTH_CODE_READY_TO_RECEIVE_FILE)
                        {
                            mWeakReferenceHandler.sendEmptyMessageDelayed(MESSAGE_BLUETOOTH_FILE_SEND_SIGNAL , Common.DURATION_LONG);
                        }
                        else if(object.code == Common.BLUETOOTH_CODE_RECEIVE_DATA_COMPLETE)
                        {
                            FileUtils.deleteAllFileInPath(Common.PATH_EXTERNAL_ZIP_ROOT);
                            mDataCommunicateFragment.closeFragment();

                        }
                    }
                    else if(mCurrentSettingType == Common.RESULT_SETTING_BLUETOOTH_RECEIVE)
                    {
                        Log.i("RECEIVE object.code : "+object.code);
                        if(object.code == Common.BLUETOOTH_CODE_RECEIVE_FILE_SIZE)
                        {
                            mDataCommunicateFragment.startTransferData();
                            Log.i("send message : "+readMessage);
                            CommonUtils.getInstance(mContext).setSharedPreference(Common.PREFERENCE_SEND_FILE_SIZE, Long.valueOf((String) object.data));
                            Log.i("File Size : "+ Long.valueOf((String) object.data));

                            MessageObject messageObject = new MessageObject();
                            messageObject.code = Common.BLUETOOTH_CODE_READY_TO_RECEIVE_FILE;
                            sendMessage(messageObject);

                            long fileSize = (long) CommonUtils.getInstance(mContext).getSharedPreference(Common.PREFERENCE_SEND_FILE_SIZE, Common.TYPE_PARAMS_LONG);
                            mBluetoothController.setConnectedFile();
                            mBluetoothController.setReadFileInformation(Common.PATH_EXTERNAL_ZIP_ROOT+Common.ZIP_FILE_NAME, fileSize);
                        }

                    }
                }catch(Exception e)
                {
                    Log.i("Exception : "+e.getMessage());
                }
                break;

            case MESSAGE_BLUETOOTH_DATA_RECEIVE_UI:
                //RECEIVE 화면 갱신
                mDataCommunicateFragment.setTransferPercent(msg.arg1);
                break;

            case MESSAGE_BLUETOOTH_DATA_SEND_UI:
                //SEND 화면 갱신
                mDataCommunicateFragment.setTransferPercent(msg.arg1);
                break;

            case MESSAGE_BLUETOOTH_DATA_RECEIVE_COMPLETE:
                Log.f("MESSAGE_BLUETOOTH_DATA_RECEIVE_COMPLETE");
                isTransferFileComplete = true;
                mDataCommunicateFragment.endTransferData();
                mBluetoothController.setConnectedMessage();
                MessageObject messageObject = new MessageObject();
                messageObject.code = Common.BLUETOOTH_CODE_RECEIVE_DATA_COMPLETE;
                sendMessage(messageObject);
                mDataCommunicateFragment.closeFragment();
                break;

            case MESSAGE_BLUETOOTH_DATA_SEND_COMPLETE:
                Log.f("MESSAGE_BLUETOOTH_DATA_SEND_COMPLETE");
                isTransferFileComplete = true;
                mDataCommunicateFragment.endTransferData();
                break;

            case MESSAGE_BLUETOOTH_FILE_SEND_SIGNAL:
                Log.i("");
                mBluetoothController.sendFile(Common.PATH_EXTERNAL_ZIP_ROOT+Common.ZIP_FILE_NAME);
                break;

            case MESSAGE_BLUETOOTH_CONNECTION_FAIL:
                //TODO: 연결 실패했을때
                break;

            case MESSAGE_BLUETOOTH_CONNECTION_LOST:
                //TODO: 연결을 잃엇을때
                if(mCurrentViewState == SCENE_DATA_COMMUNICATE)
                {
                    ((AppCompatActivity)mContext).onBackPressed();
                }
                break;

            case MESSAGE_BLUETOOTH_TOAST:
                mMainContainerContractView.showMessage((String)msg.obj, mContext.getResources().getColor(R.color.color_white));
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
    public void selectFloatButton()
    {
        if(mCurrentViewState == SCENE_MONTH_LIST_VIEW)
        {
            Log.f("");
            startPickActivity(REQUEST_PICK_FROM_ADD);
        }
        else
        {
            //SETTING 화면으로
            Log.f("");
            startSettingActivity();
        }
    }


    @Override
    public void onSelectMonth(int position)
    {
        mMonthPosition = position;
        mSelectMonthColor = mContext.getResources().getIdentifier("color_month_"+(position+1), "color", Common.PACKAGE_NAME);

        if(getPhotoInformationMonthList(mMonthPosition).size() >= Common.MAX_PHOTO_ITEM)
        {
            mMainContainerContractView.hideModeButton();
        }
        else
        {
            mMainContainerContractView.changePhotoButton(mSelectMonthColor);
        }

        startMonthListViewFragment(position);

    }


    @Override
    public void onChangeMainViewSetting()
    {
        Log.i("mCurrentState : "+ mCurrentViewState);

        if(mSelectMonthColor != 0)
        {
            mWeakReferenceHandler.sendEmptyMessageDelayed(MESSAGE_TITLE_INIT_COLOR, Common.DURATION_DEFAULT);
            mMainContainerContractView.hideMonthNumberAnimation();
            mMainContainerContractView.changeTitleAnimationText(mContext.getResources().getString(R.string.app_name));
            mMainContainerContractView.hideTitleViewBackgroundAnimation(mSelectMonthColor);

            if(getPhotoInformationMonthList(mMonthPosition).size() >= Common.MAX_PHOTO_ITEM)
            {
                mMainContainerContractView.showSettingButton(Common.DURATION_DEFAULT);
            }
            else
            {
                mMainContainerContractView.changeSettingButton();
            }


        }

        mSelectMonthColor = 0;
    }

    @Override
    public void onChangeMonthListViewSetting()
    {
        Log.i("mCurrentState : "+ mCurrentViewState+", mSelectMonthColor : "+mSelectMonthColor);

        if(mSelectMonthColor != 0)
        {
            mMainContainerContractView.changeTitleViewColor(R.color.color_white);
            mMainContainerContractView.showMonthNumberAnimation();
            mMainContainerContractView.showTitleViewBackgroundAnimation(mSelectMonthColor);
        }

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
        mMonthListViewFragment.deleteItem();
        CommonUtils.getInstance(mContext).updateWidget();

        ArrayList<PhotoInformationObject> photoInformationList = getPhotoInformationMonthList(mMonthPosition);

        mMainContainerContractView.setMonthNumberText(mSelectMonthColor, photoInformationList.size());

        if(photoInformationList.size() == Common.MAX_PHOTO_ITEM -1)
        {
            mMainContainerContractView.showPhotoButton(Common.DURATION_DEFAULT, mSelectMonthColor);
        }
    }

    @Override
    public void onModifiedPhoto(int position, Pair<View, String> item)
    {
        Log.f("");

        mModifiedItemPosition = position;
        mModifiedInformationObject = getPhotoInformationMonthList(mMonthPosition).get(mModifiedItemPosition);
        startModifiedInformationFragment(item);
        mMainContainerContractView.hideMainTitleLayout();
        mMainContainerContractView.invisibleFloatButton();
    }

    @Override
    public void onModifiedEnd()
    {
        mMainContainerContractView.showMainTitleLayout();
        mMainContainerContractView.showPhotoButton(Common.DURATION_SHORT, mSelectMonthColor);
    }

    @Override
    public void onDataTransferEnd()
    {
        Log.i("");
        mMainContainerContractView.showMainTitleLayout();
        mMainContainerContractView.showSettingButton(Common.DURATION_SHORT);

        if (mBluetoothController != null)
        {
            mBluetoothController.stop();
        }

        if(isTransferFileComplete)
        {
            if(mCurrentSettingType == Common.RESULT_SETTING_BLUETOOTH_RECEIVE)
            {
                FileUtils.deleteAllFileInPath(Common.PATH_APP_ROOT);
                mPhotoInformationDBHelper.deletePhotoInformationAll();

                mWeakReferenceHandler.sendEmptyMessageDelayed(MESSAGE_FILE_UNCOMPRESSOR, Common.DURATION_LONG);
            }
            else if(mCurrentSettingType == Common.RESULT_SETTING_BLUETOOTH_SEND)
            {
                mMainContainerContractView.showMessage(mContext.getResources().getString(R.string.message_send_complete),
                        mContext.getResources().getColor(R.color.color_37b09b));
            }
        }
        else
        {
            //TODO: 파일 전송이 중간에 끊어졌을때의 처리

            if(mCurrentSettingType == Common.RESULT_SETTING_BLUETOOTH_RECEIVE)
            {
                mMainContainerContractView.showMessage(mContext.getResources().getString(R.string.message_receive_fail),
                        mContext.getResources().getColor(R.color.color_ed433e));
            }
            else
            {
                mMainContainerContractView.showMessage(mContext.getResources().getString(R.string.message_send_fail),
                        mContext.getResources().getColor(R.color.color_ed433e));
            }
        }

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

    @Override
    public void setMainScene(int scene)
    {
        mCurrentViewState = scene;
    }

    @Override
    public void startFileTransfer()
    {
        Log.i("");
        mDataCommunicateFragment.startTransferData();
        mWeakReferenceHandler.sendEmptyMessage(MESSAGE_FILE_COMPRESSOR);
    }

    @Override
    public void cancelFileTransfer()
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
            }
        }

        if(mModifiedCheckList.containsKey(Check.DATE) && mModifiedCheckList.get(Check.DATE) == true)
        {
            Log.f("DATE CHANGE");
            mPhotoInformationDBHelper.updatePhotoInformationObject(mModifiedInformationObject.getKeyID(), PhotoInformationDBHelper.KEY_DATE_MILLISECOND, String.valueOf(mModifiedDateTime));
        }

        mWeakReferenceHandler.sendEmptyMessageDelayed(MESSAGE_SAVE_COMPLETE, 2000);
    }

    private final BroadcastReceiver mBluetoothScanReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();

            if(action.equals(BluetoothDevice.ACTION_FOUND))
            {
                if(mBluetoothScanDialog != null)
                {
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                    Log.i("Name : "+device.getName()+", Address : "+device.getAddress()+", type : " +device.getBluetoothClass().getDeviceClass());
                    if(device.getBondState() != BluetoothDevice.BOND_BONDED &&
                            device.getBluetoothClass().getDeviceClass() == BluetoothClass.Device.PHONE_SMART)
                    {
                        mBluetoothScanDialog.addData(device.getName(), device.getAddress());
                    }
                }
            }
            else if(action.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED))
            {
                Log.i("ACTION_DISCOVERY_FINISHED ");
                if(mBluetoothScanDialog != null)
                {
                    mBluetoothScanDialog.showData();
                }
            }
        }
    };

    private BluetoothScanDialog.BluetoothScanListener mBluetoothScanListener = new BluetoothScanDialog.BluetoothScanListener() {

        @Override
        public void onSelectDevice(String deviceAddress)
        {
            Log.i("deviceAddress : "+deviceAddress);
            mCurrentRemoteAddress = deviceAddress;
            hideBluetoothScanDialog();
            BluetoothDevice device = mBluetoothController.getRemoteDevice(mCurrentRemoteAddress);
            mBluetoothController.connecting(device);
        }

        @Override
        public void onClickScan()
        {
            Log.i("onClickScan ");
            mBluetoothScanDialog.showLoading();
            startDiscovery();
        }

        @Override
        public void onClickCancel()
        {
            Log.i("onClickCancel ");
            hideBluetoothScanDialog();
        }

        @Override
        public void onDismiss()
        {
            Log.i("onDismiss ");
            mBluetoothController.cancelDiscovery();


        }
    };

    private AsyncListener mOnAsyncListener = new AsyncListener()
    {
        @Override
        public void onRunningStart(String code)
        {

        }

        @Override
        public void onRunningEnd(String code, Object object)
        {
            if(code.equals(Common.ASYNC_COMPRESSOR))
            {
                boolean result = (boolean) object;
                Log.f("Compressor result : "+ result);
                if(result)
                {
                    File file = new File(Common.PATH_EXTERNAL_ZIP_ROOT+Common.ZIP_FILE_NAME);
                    Log.i("zip file exist : "+ file.exists());

                    if(file.exists())
                    {
                        if(file.length() > 0)
                        {
                            String fileSize = String.valueOf(file.length());
                            Log.i("zip file size : "+ fileSize);

                            MessageObject messageObject = new MessageObject();
                            messageObject.code = Common.BLUETOOTH_CODE_RECEIVE_FILE_SIZE;
                            messageObject.data = fileSize;
                            sendMessage(messageObject);
                        }
                        else
                        {
                            Log.f("File size error");
                        }

                    }
                }
                else
                {
                    //TODO: 압축 실패했을때 seqence
                }
            }
            else if(code.equals(Common.ASYNC_UNCOMPRESSOR))
            {
                boolean result = (boolean) object;

                if(result)
                {
                    initSetPhotoInformaionList(getPhotoInformationFromFile());
                    mWeakReferenceHandler.sendEmptyMessageDelayed(MESSAGE_RECEIVE_SETTING_COMPLETE, Common.DURATION_LONGER);
                }
                else
                {

                }
            }
        }

        @Override
        public void onRunningCanceled(String code) {}

        @Override
        public void onRunningProgress(String code,Integer integer) {}

        @Override
        public void onRunningAdvanceInformation(String code, Object object) {}

        @Override
        public void onErrorListener(String type, String message)
        {
            Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
        }
    };


}
