package com.starbrunch.couple.photo.frame.main.contract.presenter;

import android.content.Context;
import android.content.Intent;
import android.os.Message;

import com.android.vending.billing.util.IabResult;
import com.android.vending.billing.util.Purchase;
import com.littlefox.logmonitor.Log;
import com.starbrunch.couple.photo.frame.main.R;
import com.starbrunch.couple.photo.frame.main.SynchronizeContainerActivity;
import com.starbrunch.couple.photo.frame.main.billing.InAppPurchase;
import com.starbrunch.couple.photo.frame.main.billing.listener.IBillingStatusListener;
import com.starbrunch.couple.photo.frame.main.common.Common;
import com.starbrunch.couple.photo.frame.main.common.CommonUtils;
import com.starbrunch.couple.photo.frame.main.contract.SynchronizeContainerContract;
import com.starbrunch.couple.photo.frame.main.handler.WeakReferenceHandler;

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

    private static final int MESSAGE_SYNCHRONIZE_TIMER_CHECK = 0;

    private Context mContext = null;
    private SynchronizeContainerContract.View mSynchronizeContainerContractView = null;
    private WeakReferenceHandler mWeakReferenceHandler = null;
    private int mCurrentSynchronizeTime = 0;
    private InAppPurchase mInAppPurchase = null;
    public SynchronizeContainerPresent(Context context)
    {
        mContext = context;
        mSynchronizeContainerContractView = (SynchronizeContainerContract.View)mContext;
        mSynchronizeContainerContractView.initView();
        mSynchronizeContainerContractView.initFont();

        mWeakReferenceHandler = new WeakReferenceHandler((SynchronizeContainerActivity)mContext);

        mCurrentSynchronizeTime = (int) CommonUtils.getInstance(mContext).getSharedPreference(Common.PARAMS_CURRENT_SYNCHRONIZE_TIME, Common.TYPE_PARAMS_INTEGER);
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
            public void onQueryInventoryFinished(IabResult result) {

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
                //TODO: 메인 액티비티로 이동
            }
        });
    }

    private void startMainActivity()
    {

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
                }
                else
                {
                    int percent = (int)(mCurrentSynchronizeTime * 100 / Common.END_SYNCHRONIZE_TIME);
                    String[] synchronizeTimer = CommonUtils.getInstance(mContext).getSyncronizeTime(mCurrentSynchronizeTime);
                    mSynchronizeContainerContractView.setRemainingTimer(synchronizeTimer[0], synchronizeTimer[1], synchronizeTimer[2], percent);
                }
                break;
        }
    }

    @Override
    public void synchronizeNow()
    {
        //TODO: 결제 진행 한다. 결제가 완료 되면 압축을 풀고 데이터를 세팅한다.
    }

    @Override
    public void synchronizeCancel()
    {
        //TODO: 동기화 취소하고 메인 화면으로 이동하면된다.
    }
}
