package com.starbrunch.couple.photo.frame.main.contract;

import android.content.Intent;
import android.os.Message;

/**
 * Contract 와 Presenter 의 기본 구조체
 * Created by 정재현 on 2017-12-21.
 */

public class BaseContract
{
    public interface Presenter
    {
        void resume();
        void pause();
        void stop();
        void destroy();

        void acvitityResult(int requestCode, int resultCode, Intent data);
        void requestPermissionResult(int requestCode, String[] permissions, int[] grantResults);
        void sendMessageEvent(Message msg);
    }
}
