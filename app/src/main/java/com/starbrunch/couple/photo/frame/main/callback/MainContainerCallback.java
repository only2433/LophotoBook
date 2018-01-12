package com.starbrunch.couple.photo.frame.main.callback;

import android.support.v4.util.Pair;
import android.view.View;

/**
 * Created by 정재현 on 2017-12-14.
 */

public interface MainContainerCallback
{
    public void onSelectMonth(int position);

    public void onGotoMainView();
    public void onGotoMonthListView();

    public void onAddPhoto();
    public void onDeletePhoto(String keyID);
    public void onModifiedPhoto(String keyID, Pair<View, String> item);

}
