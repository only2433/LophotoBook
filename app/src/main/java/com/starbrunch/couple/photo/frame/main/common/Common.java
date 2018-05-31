package com.starbrunch.couple.photo.frame.main.common;

import android.os.Environment;

/**
 * Created by 정재현 on 2017-12-13.
 */

public class Common
{


    public static final String PACKAGE_NAME ="com.starbrunch.couple.photo.frame.main";
    public static final String PATH_BASE_APP_ROOT   = "/data/data/" + PACKAGE_NAME;
    public static final String PATH_APP_ROOT	 = "/data/data/" + PACKAGE_NAME + "/files/";
    public static final String PATH_IMAGE_ROOT = PATH_APP_ROOT+"images/";

    public static final String PATH_EXTERNAL_ZIP_ROOT = Environment.getExternalStorageDirectory()+"/CouplePhotoFrame/";
    public static final String PATH_EXTERNAL_PHOTO_INFORMATION_ROOT = PATH_EXTERNAL_ZIP_ROOT +"files/";

    public static final String ZIP_FILE_NAME = "couplePhotoFrame.zip";
    public static final String PHOTO_INFORMATION_FILE_NAME ="photo_information.txt";


    public static final String ASYNC_UNCOMPRESSOR   ="async_uncompressor";
    public static final String ASYNC_COMPRESSOR     ="async_compressonr";
    public static final String ASYNC_BLUETOOTH_SEND_FILE = "async_bluetooth_send_file";
    public static final String ASYNC_BLUETOOTH_READ_FILE = "async_bluetooth_read_file";


    public static final String SHARED_PHOTO_IMAGE = "shared_photo_image";

    public static final String INTENT_PHOTO_TRANSITION_NAME = "intent_photo_transition_name";
    public static final String INTENT_MODIFIED_ITEM_OBJECT  = "intent_modified_item_object";
    public static final String INTENT_MONTH_PHOTO_LIST      = "intent_month_photo_list";
    public static final String INTENT_WIDGET_UPDATE         = "intent_widget_update";
    public static final String INTENT_SETTING_SELECT_INDEX  = "intent_setting_select_index";
    public static final String INTENT_SYNCHRONIZE           = "intent_synchronize";


    public static final String[] MONTH_TEXT_LIST =  {"JANUARY", "FEBRUARY", "MARCH", "APRIL", "MAY", "JUNE", "JULY", "AUGUST", "SEPTEMBER", "OCTOBER", "NOVEMBER", "DECEMBER"};

    public static final int END_SYNCHRONIZE_TIME = 12 * 60 * 60;

    /** 개발자 이메일 */
    public static final String DEVELOPER_EMAIL 		= "only340@gmail.com";

    public static final String PARAMS_IS_SYNCHRONIZING          = "params_synchronizing";
    public static final String PARAMS_CURRENT_SYNCHRONIZE_TIME  = "params_current_synchronize_time";
    public static final String PARAMS_DISPLAY_METRICS	        = "display_metrics";
    public static final int TYPE_PARAMS_BOOLEAN 	= 0;
    public static final int TYPE_PARAMS_INTEGER 	= 1;
    public static final int TYPE_PARAMS_LONG        = 2;
    public static final int TYPE_PARAMS_STRING		= 3;


    public static final int LOADING_DIALOG_SIZE = 100;

    public static final int DURATION_SHORT                      = 300;
    public static final int DURATION_DEFAULT                    = 500;
    public static final int DURATION_TITLE_BACKGROUND_ANIMATION = 700;
    public static final int DURATION_LONG                       = 1000;
    public static final int DURATION_LONGER                     = 2000;

    public static final int RESULT_SETTING_BLUETOOTH_SEND       = 1001;
    public static final int RESULT_SETTING_BLUETOOTH_RECEIVE    = 1002;
    public static final int RESULT_SYNCRONIZE_SUCCESS           = 1003;
    public static final int RESULT_SYNCRONIZE_FAIL              = 1004;


    /**
     * 각 달마다 사진등록 최고 개수는 7개로 정의 한다. ( 추후 더 늘릴계획 있음 )
     */
    public static final int MAX_PHOTO_ITEM = 7;

    // Send 기기가 Receive 기기에게 받을 파일 사이즈를 전달한다. ( BLUETOOTH로 주고 받는 메세지 의 코드 )
    public static final int BLUETOOTH_CODE_RECEIVE_FILE_SIZE        = 700;

    // Receive 기기가 Send 기기에게 파일 받을 준비가 됬다고 알린다. ( BLUETOOTH로 주고 받는 메세지 의 코드 )
    public static final int BLUETOOTH_CODE_READY_TO_RECEIVE_FILE    = 701;

    // Receive 기기가 Send 기기에게 데이터가 다 전달 되었다고 알린다. ( BLUETOOTH로 주고 받는 메세지 의 코드 )
    public static final int BLUETOOTH_CODE_RECEIVE_DATA_COMPLETE    = 702;


    public static final String PREFERENCE_SEND_FILE_SIZE = "preference_file_size";
}
