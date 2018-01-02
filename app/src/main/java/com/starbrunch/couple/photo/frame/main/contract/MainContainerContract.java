package com.starbrunch.couple.photo.frame.main.contract;

/**
 * Created by 정재현 on 2017-12-20.
 */

public class MainContainerContract
{
    public interface View extends BaseContract.View
    {
        void showTitleViewBackgroundAnimation(int color);
        void hideTitleViewBackgroundAnimation(int color);

        void setMonthNumberText(String text);

        void showMonthNumberAnimation();
        void hideMonthNumberAnimation();

        void changeTitleAnimationText(String string);
        void changeTitleViewColor(int color);

    }

    public interface Presenter extends BaseContract.Presenter
    {
        //TODO : 추가 적으로 구현 해야할 것
    }
}
