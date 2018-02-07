package com.starbrunch.couple.photo.frame.main.common;

import android.os.Environment;

/**
 * Created by 정재현 on 2017-12-13.
 */

public class Common
{


    public static final String PACKAGE_NAME ="com.starbrunch.couple.photo.frame.main";
    public static final String PATH_APP_ROOT	 = "/data/data/" + PACKAGE_NAME + "/files/";
    public static final String PATH_IMAGE_ROOT = PATH_APP_ROOT+"images/";

    public static final String PATH_EXTERNAL_ZIP_ROOT = Environment.getExternalStorageDirectory()+"/CouplePhotoFrame/";
    public static final String PATH_EXTERNAL_PHOTO_INFORMATION_ROOT = PATH_EXTERNAL_ZIP_ROOT +"files/";

    public static final String ZIP_FILE_NAME = "couplePhotoFrame.zip";
    public static final String PHOTO_INFORMATION_FILE_NAME ="photo_information.txt";


    public static final String ASYNC_UNCOMPRESSOR   ="async_uncompressor";
    public static final String ASYNC_COMPRESSOR     ="async_compressonr";

    public static final String SHARED_PHOTO_IMAGE = "shared_photo_image";

    public static final String INTENT_PHOTO_TRANSITION_NAME = "intent_photo_transition_name";
    public static final String INTENT_MODIFIED_ITEM_OBJECT = "intent_modified_item_object";
    public static final String INTENT_MONTH_PHOTO_LIST = "intent_month_photo_list";
    public static final String INTENT_WIDGET_UPDATE = "intent_widget_update";
    public static final String INTENT_SETTING_SELECT_INDEX = "intent_setting_select_index";

    public static final String[] MONTH_TEXT_LIST =  {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "November"};



    /** 개발자 이메일 */
    public static final String DEVELOPER_EMAIL 		= "only340@gmail.com";

    public static final String PARAMS_DISPLAY_METRICS	= "display_metrics";
    public static final int TYPE_PARAMS_BOOLEAN 	= 0;
    public static final int TYPE_PARAMS_INTEGER 	= 1;
    public static final int TYPE_PARAMS_LONG        = 2;
    public static final int TYPE_PARAMS_STRING		= 3;


    public static final int LOADING_DIALOG_SIZE = 100;

    public static final int DURATION_SHORT = 300;
    public static final int DURATION_DEFAULT = 500;
    public static final int DURATION_TITLE_BACKGROUND_ANIMATION = 700;

    public static final int SCREEN_MAIN         = 0;
    public static final int SCREEN_MONTH_LIST   = 1;

    public static final int RESULT_SETTING_BLUETOOTH_SEND       = 1001;
    public static final int RESULT_SETTING_BLUETOOTH_RECEIVE    = 1002;

    public static final String PREFERENCE_SEND_FILE_SIZE = "preference_file_size";
}
