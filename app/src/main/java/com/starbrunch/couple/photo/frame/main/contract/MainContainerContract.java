package com.starbrunch.couple.photo.frame.main.contract;

/**
 * Created by 정재현 on 2017-12-20.
 */

public class MainContainerContract
{
    public interface View
    {
        void initView();
        void showTitleViewBackgroundAnimation(int color);
        void hideTitleViewBackgroundAnimation(int color);

        void setMonthNumberText(int color, int imageCount);

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
