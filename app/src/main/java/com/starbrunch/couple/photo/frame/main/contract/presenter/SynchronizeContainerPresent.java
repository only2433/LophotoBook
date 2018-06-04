package com.starbrunch.couple.photo.frame.main.contract.presenter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

import com.android.vending.billing.util.IabResult;
import com.android.vending.billing.util.Purchase;
import com.google.gson.Gson;
import com.littlefox.library.system.async.listener.AsyncListener;
import com.littlefox.library.system.common.FileUtils;
import com.littlefox.logmonitor.Log;
import com.starbrunch.couple.photo.frame.main.MainContainerActivity;
import com.starbrunch.couple.photo.frame.main.R;
import com.starbrunch.couple.photo.frame.main.SynchronizeContainerActivity;
import com.starbrunch.couple.photo.frame.main.async.UnCompressorAsync;
import com.starbrunch.couple.photo.frame.main.billing.InAppPurchase;
import com.starbrunch.couple.photo.frame.main.billing.listener.IBillingStatusListener;
import com.starbrunch.couple.photo.frame.main.common.Common;
import com.starbrunch.couple.photo.frame.main.common.CommonUtils;
import com.starbrunch.couple.photo.frame.main.contract.SynchronizeContainerContract;
import com.starbrunch.couple.photo.frame.main.database.PhotoInformationDBHelper;
import com.starbrunch.couple.photo.frame.main.handler.WeakReferenceHandler;
import com.starbrunch.couple.photo.frame.main.object.PhotoInformationListObject;
import com.starbrunch.couple.photo.frame.main.object.PhotoInformationObject;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by only340 on 2018-03-09.
 */

public class SynchronizeContainerPresent implements SynchronizeContainerContract.Presenter
{
    class SynchronizeTimerTask extends TimerTask
    {
        @Override
        public void run()
        {
            mWeakReferenceHandler.sendEmptyMessage(MESSAGE_SYNCHRONIZE_TIMER_CHECK);
        }
    }

    private static final int MESSAGE_SYNCHRONIZE_TIMER_CHECK    = 0;
    private static final int MESSAGE_SYNCHRONIZE_COMPLETE       = 1;
    private static final int MESSAGE_EXIT_SYNCHRONIZE_VIEW      = 2;

    private Context mContext = null;
    private SynchronizeContainerContract.View mSynchronizeContainerContractView = null;
    private WeakReferenceHandler mWeakReferenceHandler = null;
    private int mCurrentSynchronizeTime = 0;
    private InAppPurchase mInAppPurchase = null;
    private PhotoInformationDBHelper mPhotoInformationDBHelper = null;
    private Timer mSynchronizeTimer = null;
    public SynchronizeContainerPresent(Context context)
    {
        mContext = context;
        mSynchronizeContainerContractView = (SynchronizeContainerContract.View)mContext;
        mSynchronizeContainerContractView.initView();
        mSynchronizeContainerContractView.initFont();

        mPhotoInformationDBHelper = PhotoInformationDBHelper.getInstance(mContext);
        mWeakReferenceHandler = new WeakReferenceHandler((SynchronizeContainerActivity)mContext);
        mCurrentSynchronizeTime = (int) CommonUtils.getInstance(mContext).getSharedPreference(Common.PARAMS_CURRENT_SYNCHRONIZE_TIME, Common.TYPE_PARAMS_INTEGER);
        initPurchase();
        enableTimer(true);
    }

    private void initPurchase()
    {
        mInAppPurchase = InAppPurchase.getInstance();
        mInAppPurchase.init(mContext);
        setupInAppPurchaseListener();
    }

    private void setupInAppPurchaseListener()
    {
        mInAppPurchase.setOnBillingStatusListener(new IBillingStatusListener()
        {
            @Override
            public void inFailure(int status, String reason) {

            }

            @Override
            public void OnIabSetupFinished(IabResult result) {

            }

            @Override
            public void onQueryInventoryFinished(IabResult result)
            {

            }

            @Override
            public void onIabPurchaseFinished(IabResult result, Purchase purchase)
            {
                Log.f("onIabPurchaseFinished : "+result.getMessage());
                Purchase consumable = mInAppPurchase.getInventory().getPurchase(InAppPurchase.IN_APP_CONSUMABLE_ITEM);

                if(consumable != null)
                {
                    mInAppPurchase.consumePurchase(consumable);
                }

            }

            @Override
            public void onConsumeFinished(IabResult result)
            {
                Log.f("onIabPurchaseFinished : "+result.getMessage());
                CommonUtils.getInstance(mContext).setSharedPreference(Common.PARAMS_CURRENT_SYNCHRONIZE_TIME, 0);
                CommonUtils.getInstance(mContext).setSharedPreference(Common.PARAMS_IS_SYNCHRONIZING, false);
                //TODO: 압축풀고 메세지 뿌리고 메인으로 진입
                mSynchronizeContainerContractView.showLoading();
                FileUtils.deleteAllFileInPath(Common.PATH_APP_ROOT);
                mPhotoInformationDBHelper.deletePhotoInformationAll();
                startUnCompressorAsync();
            }
        });
    }

    private void startMainActivity()
    {
        Intent mainIntent = new Intent(mContext, MainContainerActivity.class);
        ((AppCompatActivity)mContext).startActivity(mainIntent);
        ((AppCompatActivity) mContext).overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
        ((AppCompatActivity)mContext).finish();
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

    private void enableTimer(boolean isStart)
    {
        if(isStart)
        {
            if(mSynchronizeTimer == null)
            {
                mSynchronizeTimer = new Timer();
                mSynchronizeTimer.schedule(new SynchronizeTimerTask(),0, Common.DURATION_LONG);
            }
            else
            {
                mSynchronizeTimer.cancel();
                mSynchronizeTimer = null;
            }
        }
    }

    @Override
    public void resume()
    {

    }

    @Override
    public void pause()
    {

    }

    @Override
    public void stop()
    {

    }

    @Override
    public void destroy() {

    }

    @Override
    public boolean acvitityResult(int requestCode, int resultCode, Intent data)
    {
        if(mInAppPurchase.getInAppHelper().handleActivityResult(requestCode, resultCode, data))
        {
            return false;
        }

        return true;
    }

    @Override
    public void sendMessageEvent(Message msg)
    {
        switch (msg.what)
        {
            case MESSAGE_SYNCHRONIZE_TIMER_CHECK:
                mCurrentSynchronizeTime++;
                CommonUtils.getInstance(mContext).setSharedPreference(Common.PARAMS_CURRENT_SYNCHRONIZE_TIME, mCurrentSynchronizeTime);

                if(mCurrentSynchronizeTime > Common.END_SYNCHRONIZE_TIME)
                {
                    mSynchronizeContainerContractView.showMessage(mContext.getResources().getString(R.string.message_synchronization_complete),
                            mContext.getResources().getColor(R.color.color_37b09b));
                    CommonUtils.getInstance(mContext).setSharedPreference(Common.PARAMS_CURRENT_SYNCHRONIZE_TIME, 0);
                    CommonUtils.getInstance(mContext).setSharedPreference(Common.PARAMS_IS_SYNCHRONIZING, false);
                    //TODO: 압축풀고 메세지 뿌리고 메인으로 진입
                    mSynchronizeContainerContractView.showLoading();
                    FileUtils.deleteAllFileInPath(Common.PATH_APP_ROOT);
                    mPhotoInformationDBHelper.deletePhotoInformationAll();
                    startUnCompressorAsync();
                }
                else
                {

                    int percent = (int) ((float)mCurrentSynchronizeTime/(float)Common.END_SYNCHRONIZE_TIME * 100);
                    String[] synchronizeTimer = CommonUtils.getInstance(mContext).getSyncronizeTime(mCurrentSynchronizeTime);
                    mSynchronizeContainerContractView.setRemainingTimer(synchronizeTimer[0], synchronizeTimer[1], synchronizeTimer[2], percent);
                }
                break;
            case MESSAGE_SYNCHRONIZE_COMPLETE:
                mSynchronizeContainerContractView.hideLoading();
                FileUtils.deleteAllFileInPath(Common.PATH_EXTERNAL_ZIP_ROOT);
                mSynchronizeContainerContractView.showMessage(mContext.getResources().getString(R.string.message_receive_complete),
                        mContext.getResources().getColor(R.color.color_37b09b));
                mWeakReferenceHandler.sendEmptyMessageDelayed(MESSAGE_EXIT_SYNCHRONIZE_VIEW, Common.DURATION_LONG);
                break;
            case MESSAGE_EXIT_SYNCHRONIZE_VIEW:
                ((AppCompatActivity)mContext).finish();
                break;
        }
    }

    @Override
    public void synchronizeNow()
    {
        //TODO: 결제 진행 한다. 결제가 완료 되면 압축을 풀고 데이터를 세팅한다.
        mInAppPurchase.purchaseItem((Activity) mContext, Common.SKU_SYNCHRONIZE_ITEM);
    }

    @Override
    public void synchronizeCancel()
    {
        //TODO: 동기화가 되지 못한다는 안내팝업 띠운다.
        AlertDialog dialog = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext)
                .setMessage(mContext.getResources().getString(R.string.message_warning_synchronize_cancel))
                .setCancelable(true)
                .setPositiveButton(mContext.getResources().getString(R.string.button_ok), new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        enableTimer(false);
                        startMainActivity();
                    }
                })
                .setNegativeButton(mContext.getResources().getString(R.string.button_cancel), new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.dismiss();
                    }
                });
        dialog = builder.show();
        TextView messageText = (TextView)dialog.findViewById(android.R.id.message);
        messageText.setGravity(Gravity.CENTER);
        dialog.show();
    }

    private AsyncListener mOnAsyncListener = new AsyncListener()
    {
        @Override
        public void onRunningStart(String code)
        {

        }

        @Override
        public void onRunningEnd(String code, Object object)
        {
            if(code.equals(Common.ASYNC_UNCOMPRESSOR))
            {
                boolean result = (boolean) object;

                if(result)
                {
                    initSetPhotoInformaionList(getPhotoInformationFromFile());
                    mWeakReferenceHandler.sendEmptyMessageDelayed(MESSAGE_SYNCHRONIZE_COMPLETE, Common.DURATION_LONGER);
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
