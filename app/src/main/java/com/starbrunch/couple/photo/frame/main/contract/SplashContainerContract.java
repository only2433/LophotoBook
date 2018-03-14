package com.starbrunch.couple.photo.frame.main.contract;

/**
 * Created by only340 on 2018-03-12.
 */

public class SplashContainerContract
{
    public interface View extends BaseContract.View
    {
        void showSplashTitleText();
        void showSplashMessageText();
        void showToast(String message);
    }

    public interface Presenter extends BaseContract.Presenter
    {
        void requestPermissionResult(int requestCode, String[] permissions, int[] grantResults);
    }
}
