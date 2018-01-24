package com.starbrunch.couple.photo.frame.main.common;

/**
 * Created by 정재현 on 2017-12-13.
 */

public class Common
{


    public static final String PACKAGE_NAME ="com.starbrunch.couple.photo.frame.main";
    public static final String PATH_APP_ROOT	 = "/data/data/" + PACKAGE_NAME + "/files/";
    public static final String PATH_IMAGE_ROOT = PATH_APP_ROOT+"images/";

    public static final String SHARED_PHOTO_IMAGE = "shared_photo_image";

    public static final String INTENT_PHOTO_KEY_ID = "intent_photo_key_id";
    public static final String INTENT_PHOTO_TRANSITION_NAME = "intent_photo_transition_name";

    public static final String INTENT_MODIFIED_ITEM_OBJECT = "intent_modified_item_object";

    public static final String INTENT_MONTH_POSITION = "intent_month_position";
    public static final String INTENT_MONTH_PHOTO_LIST = "intent_month_photo_list";

    public static final String[] MONTH_TEXT_LIST =  {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "November"};

    public static final String INTENT_WIDGET_UPDATE = "intent_widget_update";

    /** 개발자 이메일 */
    public static final String DEVELOPER_EMAIL 		= "only340@gmail.com";

    public static final String PARAMS_DISPLAY_METRICS	= "display_metrics";
    public static final int TYPE_PARAMS_BOOLEAN 	= 0;
    public static final int TYPE_PARAMS_INTEGER 	= 1;
    public static final int TYPE_PARAMS_STRING		= 2;


    public static final int LOADING_DIALOG_SIZE = 100;

    public static final int DURATION_SHORT = 300;
    public static final int DURATION_DEFAULT = 500;
    public static final int DURATION_TITLE_BACKGROUND_ANIMATION = 700;

    public static final int SCREEN_MAIN         = 0;
    public static final int SCREEN_MONTH_LIST   = 1;
}
