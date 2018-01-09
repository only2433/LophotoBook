package com.starbrunch.couple.photo.frame.main.common;

/**
 * Created by 정재현 on 2017-12-13.
 */

public class Common
{
    public static final String PACKAGE_NAME ="com.starbrunch.couple.photo.frame.main";
    public static final String PATH_APP_ROOT	 = "/data/data/" + PACKAGE_NAME + "/files/";
    public static final String PATH_IMAGE_ROOT = PATH_APP_ROOT+"images/";


    public static final String SHARED_ELEMENT_MONTH_TITLE ="shared_element_month_title";
    public static final String SHARED_ELEMENT_MONTH_IMAGE ="shared_element_month_image";
    public static final String SHARED_ELEMENT_MONTH_POSITION = "shared_element_month_position";
    public static final String SHARED_ELEMENT_MONTH_PHOTO_LIST = "shared_element_month_photo_list";

    public static final String[] MONTH_TEXT_LIST =  {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "November"};

    public static final String INTENT_WIDGET_UPDATE = "intent_widget_update";

    /** 개발자 이메일 */
    public static final String DEVELOPER_EMAIL 		= "only340@gmail.com";

    public static final String PARAMS_DISPLAY_METRICS					= "display_metrics";
    public static final int TYPE_PARAMS_BOOLEAN 	= 0;
    public static final int TYPE_PARAMS_INTEGER 	= 1;
    public static final int TYPE_PARAMS_STRING		= 2;

    public static final int DURATION_SHORT = 300;
    public static final int DURATION_DEFAULT = 500;
    public static final int DURATION_TITLE_BACKGROUND_ANIMATION = 700;
}
