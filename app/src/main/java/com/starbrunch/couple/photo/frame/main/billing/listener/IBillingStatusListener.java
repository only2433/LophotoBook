package com.starbrunch.couple.photo.frame.main.billing.listener;


import com.android.vending.billing.util.IabResult;
import com.android.vending.billing.util.Purchase;

public interface IBillingStatusListener
{
	void inFailure(int status, String reason);

	void OnIabSetupFinished(IabResult result);

	void onQueryInventoryFinished(IabResult result);

	void onIabPurchaseFinished(IabResult result, Purchase purchase);

	void onConsumeFinished(IabResult result);
}
