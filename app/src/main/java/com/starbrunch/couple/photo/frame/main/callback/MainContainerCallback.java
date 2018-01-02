package com.starbrunch.couple.photo.frame.main.callback;

/**
 * Created by 정재현 on 2017-12-14.
 */

public interface MainContainerCallback
{
    public void onSelectMonth(int position);

    public void onGotoMainView();
    public void onGotoMonthListView();

    public void onAddPicture();

    public void onDeletePicture();


}
