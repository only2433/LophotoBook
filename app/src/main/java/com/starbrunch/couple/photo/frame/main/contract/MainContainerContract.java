package com.starbrunch.couple.photo.frame.main.contract;

import android.content.Intent;

/**
 * Created by 정재현 on 2017-12-20.
 */

public class MainContainerContract
{
    public interface View extends BaseContract.View
    {
        void showTitleViewBackgroundAnimation(int color);
        void hideTitleViewBackgroundAnimation(int color);

        void setMonthNumberText(int color, int imageCount);

        void showMonthNumberAnimation();
        void hideMonthNumberAnimation();

        void changeTitleAnimationText(String string);
        void changeTitleViewColor(int color);

        void showLoading();
        void hideLoading();
        void showDatePickerDialog();
        void showTimePickerDialog();

        void showMainTitleLayout();
        void hideMainTitleLayout();

        void showSettingButton(int delay);
        void showPhotoButton(int delay, int color);
        void hideModeButton();
        void changeSettingButton();
        void changePhotoButton(int color);

        void invisibleFloatButton();

    }

    public interface Presenter extends BaseContract.Presenter
    {
        void acvitityResult(int requestCode, int resultCode, Intent data);

        void changeDateSetComplete(int year, int monthOfYear, int dayOfMonth);
        void changeTimeSetComplete(int hourOfDay, int minute);

        void selectFloatButton();

    }
}
