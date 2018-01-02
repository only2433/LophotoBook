package com.starbrunch.couple.photo.frame.main.contract;

import android.content.Intent;
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
    }

    public interface Presenter
    {
        void onResume();
        void onPause();
        void onStop();
        void onDestroy();

        void init(Object object);
        void onAcvitityResult(int requestCode, int resultCode, Intent data);
        void onRequestPermissionResult(int requestCode, String[] permissions, int[] grantResults);
        void sendMessageEvent(Message msg);
    }
}
