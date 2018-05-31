package com.starbrunch.couple.photo.frame.main.billing;

import android.app.Activity;
import android.content.Context;

import com.android.vending.billing.util.IabHelper;
import com.android.vending.billing.util.IabHelper.OnConsumeFinishedListener;
import com.android.vending.billing.util.IabHelper.OnIabPurchaseFinishedListener;
import com.android.vending.billing.util.IabHelper.OnIabSetupFinishedListener;
import com.android.vending.billing.util.IabHelper.QueryInventoryFinishedListener;
import com.android.vending.billing.util.IabResult;
import com.android.vending.billing.util.Inventory;
import com.android.vending.billing.util.Purchase;
import com.littlefox.logmonitor.Log;
import com.starbrunch.couple.photo.frame.main.R;
import com.starbrunch.couple.photo.frame.main.billing.listener.IBillingStatusListener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 *  앱결제를 위한 Helper 클래스
 * @author 정재현
 *
 */
public class InAppPurchase {
    public static final String IN_APP_FREE_USER = "free_user";
    public static final String IN_APP_CONSUMABLE_ITEM = "consumable_1_item";

    public static final int STATUS_SET_UP_FINISHED = 0;
    public static final int STATUS_QUERY_INVENTORY_FINISHED = 1;
    public static final int STATUS_PURCHASE_FINISHED = 2;
    public static final int STATUS_CONSUME_FINISHED = 3;

    private static final String ERROR_MESSAGE_DEVELOPER_PAYLOAD = "Current Payload is Developer.";

	/*
	private final static String REDIRECT_URL ="urn:ietf:wg:oauth:2.0:oob";

	private static final String BASE_REFRESH_URL 	= "https://accounts.google.com/o/oauth2/";
	private static final String REFRESH_TOKEN_URL 	= BASE_REFRESH_URL+"token";
	private static final String REFRESH_AUTH_URL	= BASE_REFRESH_URL +"auth";

	private static final String GOOGLE_PUBLISH_URL ="https://www.googleapis.com/auth/androidpublisher";
	private static final String AUTH_CODE = "4/rj9Yt1pz-X-pDOjG-_RnAQ5DO2GzBkzeEUNLdCIN_18";
	private static String REFRESH_TOKEN = "";
	*/

    /**
     * 결제 관련 IN APP BILLING KEY
     */
    private static final String IN_APP_BILLING_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAi3m641qzN11CKdwDHkWb+akiNX0/0EhL228SfJfib6ABQ2arL0J9migio4nDkiUwgu2sikZh1l5GpeJpB4Vecq79v+NrIMZyI3FxCoO4SZLxOQlRpW21WpW7TDNzIQBv/30d4TImflTRCROxvicVQYbQs83KTzHVgob7f21rK4AH6z85NrHb1PBxWy+EHKCZxV2hA+Emt2VV8JXJ/CwBd+CmF4NZo01pAgDvQeVCr9zBw1ZEgrPf8X6YX7mcy2+VPILxaNFnto+8kanS8afiukMk96MmqlpHlhVibPIA5Gecm50K8fGDhkS/ANXSnvvCldzmoV+v36PueIBV722F0wIDAQAB";

	/*
	private static final String IN_APP_CLIENT_ID = "670368007911-0msm29glhl6dik40lqbrhddapt9b732v.apps.googleusercontent.com";

	private static final String IN_APP_CLIENT_SECRET_KEY = "wpHPOfB9ID8RNTh0UVKvkHnq";
	*/


    /**
     * 구매 흐름에 관한 Request Code
     */
    public static final int REQUEST_CODE = 10001;

    private static InAppPurchase sInAppPurchase = null;

    /**
     * 결제를 도와주는 Helper Class
     */
    private IabHelper mIabHelper = null;


    /**
     * 현재 서버에 등록된 상품 코드 리스트
     */
    private ArrayList<String> mServerProductCodeList = new ArrayList<String>();

    /**
     * 결제 관련 정보를 리턴하는 Listener
     */
    private IBillingStatusListener mIBillingStatusListener;

    /**
     * 현재 Google에서 받아온 Inventory 정보
     */
    private Inventory mInventory = null;

    /**
     * Developer 결제 테스트 계정인지 확인 하기 위해 사용
     */
    private String mPayLoad = null;

    private Context mContext;

    /**
     * 현재 결제 하는 Sku
     */
    private String mCurrentPaySku = "";


    public static InAppPurchase getInstance() {
        if (sInAppPurchase == null) {
            sInAppPurchase = new InAppPurchase();
        }

        return sInAppPurchase;
    }


    public IabHelper getInAppHelper() {
        return mIabHelper;
    }

    /**
     * 인앱 결제 초기화
     */
    public void init(Context context) {
        mContext = context;
        mPayLoad = UUID.randomUUID().toString();
        mIabHelper = new IabHelper(mContext, IN_APP_BILLING_KEY);

        mIabHelper.enableDebugLogging(false);
        mServerProductCodeList = new ArrayList<String>();

    }


    /**
     * 구매햇던 내역이 소모성아이템인데 구글에 정보를 못줬을 때를 대비하여 예비상으로 앱실행시 다시한번 체크하여 구글에 전달
     */
    private void sendConsumableDataToGoogle() {
        List<Purchase> allPurchaseList = new ArrayList<Purchase>();

        allPurchaseList = mInventory.getAllPurchases();

        for (Purchase purchaseItem : allPurchaseList)
        {
            mIabHelper.consumeAsync(purchaseItem, mOnConsumeFinishedListener);
        }
    }

    /**
     * 구글에 등록되어 있는 결제 정보를 받기위해 호출
     */
    public void settingPurchaseInforamtionToGoogle() {
        try {
            if (mIabHelper != null) {
                mIabHelper.startSetup(mOnIabSetupFinishedListener);
            }

        } catch (IllegalStateException e) {

        }

    }

    public void release() {
        if (mIabHelper != null) {
            mIabHelper.dispose();
            mIabHelper = null;
        }
    }

    public List<String> getOwnedPurchaseItemList() {
        if (mInventory != null) {
            return mInventory.getAllOwnedSkus();
        }

        return null;
    }

    /**
     * 등록된 상품코드 리스트를 세팅한다.
     *
     * @param productList
     */
    public void setServerProductList(ArrayList<String> productList) {
        mServerProductCodeList = productList;
    }

    /**
     * 등록된 상품가격을 알아오기 위해 사용
     *
     * @param productCode
     */
    public void setServerProduct(String productCode) {
        Log.i("productCode : " + productCode.trim());
        mServerProductCodeList.add(productCode.trim());
    }


    /**
     * 아이템 구매를 요청한다. Consumable , Non-Consumable
     *
     * @param activity 구매 요청할 ACTIVITY
     * @param skuCode  구매할 상품 코드
     */
    public void purchaseItem(Activity activity, String skuCode) {
        mCurrentPaySku = skuCode;

        Log.i("skuCode : " + skuCode);
        if (mIabHelper == null) {
            mIBillingStatusListener.inFailure(STATUS_QUERY_INVENTORY_FINISHED, mContext.getResources().getString(R.string.product_is_null));
            return;
        }

        if (mInventory == null) {
            mIBillingStatusListener.inFailure(STATUS_QUERY_INVENTORY_FINISHED, mContext.getResources().getString(R.string.product_is_null));
            return;
        }

        if (mInventory.hasPurchase(skuCode)) {
            mIBillingStatusListener.inFailure(STATUS_QUERY_INVENTORY_FINISHED, mContext.getResources().getString(R.string.message_iab_aleady_buy_product));
            return;
        }

        if (mIabHelper != null) {
            try {
                mIabHelper.launchPurchaseFlow(activity, skuCode, IabHelper.ITEM_TYPE_INAPP, REQUEST_CODE, mOnIabPurchaseFinishedListener, mPayLoad);


            } catch (Exception e) {
                mIabHelper.flagEndAsync();
                mIabHelper.launchPurchaseFlow(activity, skuCode, IabHelper.ITEM_TYPE_INAPP, REQUEST_CODE, mOnIabPurchaseFinishedListener, mPayLoad);

            }
        }
    }

    public String getCurrentPaySku() {
        return mCurrentPaySku;
    }

    public Inventory getInventory() {
        return mInventory;
    }

    public void setOnBillingStatusListener(IBillingStatusListener IBillingStatusListener) {
        mIBillingStatusListener = IBillingStatusListener;
    }

    public void consumePurchase(Purchase purchase) {
        Log.i("consumPurchase : " + purchase.getSku());
        mIabHelper.consumeAsync(purchase, mOnConsumeFinishedListener);
        mInventory.erasePurchase(purchase.getSku());
    }



    /**
     * Billing 결제를 위한 초반 setup Listener. 한번만 호출하면 된다.
     */
    private OnIabSetupFinishedListener mOnIabSetupFinishedListener = new OnIabSetupFinishedListener()
    {

        @Override
        public void onIabSetupFinished(IabResult result)
        {
            Log.i("");

            if (mIabHelper == null)
            {
                mIBillingStatusListener.inFailure(STATUS_SET_UP_FINISHED, mContext.getResources().getString(R.string.product_is_null));
            }

            if (result.isSuccess() == true)
            {

                mIBillingStatusListener.OnIabSetupFinished(result);
                List<String> list = new ArrayList<String>();
                list.add(IN_APP_CONSUMABLE_ITEM);
                mIabHelper.queryInventoryAsync(true, list, mQueryInventoryFinishedListener);
            }
            else
            {
                mIBillingStatusListener.inFailure(STATUS_SET_UP_FINISHED, result.getMessage());
            }
        }

    };

    /**
     * 등록한 상품에 대한 정보를 받아올 때 사용하는 Listener.
     */
    private QueryInventoryFinishedListener mQueryInventoryFinishedListener = new QueryInventoryFinishedListener()
    {

        @Override
        public void onQueryInventoryFinished(IabResult result, Inventory inventory)
        {
            Log.i("result.isSuccess() : "+result.isSuccess() +", mIabHelper : "+mIabHelper);

            if (mIabHelper == null)
            {
                mIBillingStatusListener.inFailure(STATUS_QUERY_INVENTORY_FINISHED, mContext.getResources().getString(R.string.product_is_null));
            }

            if (result.isSuccess() == true)
            {
                mInventory = inventory;

                //sendConsumableDataToGoogle();

                mIBillingStatusListener.onQueryInventoryFinished(result);
            }
            else
            {
                mIBillingStatusListener.inFailure(STATUS_QUERY_INVENTORY_FINISHED, result.getMessage());
            }
        }

    };

    /**
     * 구매가 완료되었을 때 호출되는 Listener.
     */
    private OnIabPurchaseFinishedListener mOnIabPurchaseFinishedListener = new OnIabPurchaseFinishedListener()
    {

        @Override
        public void onIabPurchaseFinished(IabResult result, Purchase purchase)
        {
            if (mIabHelper == null)
            {
                mIBillingStatusListener.inFailure(STATUS_PURCHASE_FINISHED, mContext.getResources().getString(R.string.product_is_null));
            }

            if (result.isSuccess() == true)
            {
                Log.i("결제 완료 개발자 결제? : " +purchase.getDeveloperPayload()+" id : "+purchase.getOrderId()+", sku : "+purchase.getSku());
                Log.i("verityDeveloperPayload(purchase) : " +verityDeveloperPayload(purchase));
                if (verityDeveloperPayload(purchase) == true)
                {
                    mIBillingStatusListener.inFailure(STATUS_PURCHASE_FINISHED, ERROR_MESSAGE_DEVELOPER_PAYLOAD);
                }
                else
                {
                    /**
                     * 소모성 아이템은 전부 소모 시켜야한다. 결제 됫을시. 단, 구독은 빼고
                     */
				/*	if(purchase.getSku().equals(Common.IN_APP_SUBSCRIPTION_1_MONTH) == false)
					{
						mIabHelper.consumeAsync(purchase, mOnConsumeFinishedListener);
					}*/

                    mIBillingStatusListener.onIabPurchaseFinished(result,purchase);
                }

            }
            else
            {
                mIBillingStatusListener.inFailure(STATUS_PURCHASE_FINISHED, result.getMessage());
            }
        }

    };

    /**
     * 소비되는 구매를 할려고 호출하는 Listener. 지속적으로 구매가능게하는 리스너
     */
    private OnConsumeFinishedListener mOnConsumeFinishedListener = new OnConsumeFinishedListener()
    {

        @Override
        public void onConsumeFinished(Purchase purchase, IabResult result)
        {
            Log.i("purchase getOrderId : "+ purchase.getOrderId());
            if (mIabHelper == null)
            {
                mIBillingStatusListener.inFailure(STATUS_CONSUME_FINISHED, mContext.getResources().getString(R.string.product_is_null));
            }

            if (result.isSuccess() == true)
            {
                mIBillingStatusListener.onConsumeFinished(result);
            }
            else
            {
                mIBillingStatusListener.inFailure(STATUS_CONSUME_FINISHED, result.getMessage());
            }
        }

    };

    /**
     * 현재 결제하는 계정이 테스트 계정인지 확인.
     *
     * @param purchase
     * @return TRUE : 테스트 계정 , FALSE : 일반 계정
     */
    private boolean verityDeveloperPayload(Purchase purchase)
    {
        if (purchase == null)
        {
            return false;
        }
        Log.i("mPayLoad : "+mPayLoad+", purchase.getDeveloperPayload() : "+purchase.getDeveloperPayload());
		/*if (mPayLoad.equals(purchase.getDeveloperPayload()) == true)
		{
			return true;
		}*/

        return false;
    }


}
