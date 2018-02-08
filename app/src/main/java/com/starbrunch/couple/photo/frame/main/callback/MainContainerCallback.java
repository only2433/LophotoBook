package com.starbrunch.couple.photo.frame.main.callback;

import android.support.v4.util.Pair;
import android.view.View;

/**
 * Fragement 에서의 행동을 MainContainerPresent 에게 전달 하는 역할
 * Created by 정재현 on 2017-12-14.
 */

public interface MainContainerCallback
{
    public void onSelectMonth(int position);


    public void onChangeMainViewSetting();
    public void onChangeMonthListViewSetting();

    public void onDeletePhoto(String keyID);
    public void onModifiedPhoto(int position, Pair<View, String> item);
    public void onModifiedEnd();
    public void onDataTransferEnd();
    public void onModifiedItemSave();
    public void onModifiedItemCancel();
    public void onSelectPhotoModified();
    public void onSelectDateModified();
    public void onSelectCommentModifed();

    public void setMainScene(int scene);
    public void startFileTransfer();
    public void cancelFileTransfer();
}
