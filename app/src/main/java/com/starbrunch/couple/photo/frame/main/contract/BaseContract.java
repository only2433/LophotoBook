package com.starbrunch.couple.photo.frame.main.contract;

import android.os.Message;

/**
 * Contract 와 Presenter 의 기본 구조체
 * Created by 정재현 on 2017-12-21.
 */

public class BaseContract
{
    public interface View
    {
        void initView();
        void initFont();
        void showMessage(String message, int color);
    }
    public interface Presenter
    {
        void resume();
        void pause();
        void stop();
        void destroy();


        void sendMessageEvent(Message msg);
    }
}
