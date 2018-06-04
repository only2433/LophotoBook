package com.starbrunch.couple.photo.frame.main.contract;

import android.content.Intent;

/**
 * Created by only340 on 2018-03-09.
 */

public class SynchronizeContainerContract
{
    public interface View extends BaseContract.View
    {
        void showLoading();
        void hideLoading();
        void setRemainingTimer(String hour, String minute, String second, int percent);
    }

    public interface Presenter extends BaseContract.Presenter
    {
        boolean acvitityResult(int requestCode, int resultCode, Intent data);

        void synchronizeNow();
        void synchronizeCancel();
    }
}
