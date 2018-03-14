package com.starbrunch.couple.photo.frame.main.contract;

/**
 * Created by only340 on 2018-03-09.
 */

public class SynchronizeContainerContract
{
    public interface View extends BaseContract.View
    {
        void setRemainingTimer(int hour, int minute, int second, int percent);

    }

    public interface Presenter extends BaseContract.Presenter
    {
        void synchronizeNow();
        void synchronizeCancel();
    }
}
