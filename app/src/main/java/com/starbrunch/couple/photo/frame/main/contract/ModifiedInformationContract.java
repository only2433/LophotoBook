package com.starbrunch.couple.photo.frame.main.contract;

import android.graphics.Bitmap;

import com.starbrunch.couple.photo.frame.main.object.PhotoInformationObject;

/**
 * Created by 정재현 on 2018-01-10.
 */

public class ModifiedInformationContract
{
    public interface View
    {
        void initView(PhotoInformationObject object);
        void changePhoto(Bitmap bitmap);
        void changeDateInformation(String date);
        void changeComment(String comment);
        void showDatePickerDialog();

        void showLoading();
        void hideLoading();
    }

    public interface Presenter extends BaseContract.Presenter
    {
        void onClickSaveButton();
        void onClickCancelButton();
        void onClickPhotoModifiedButton();
        void onClickDateModifiedButton();
        void onClickCommentModifedButton();
        void onDateSetChanged(long dateTime);
    }
}
