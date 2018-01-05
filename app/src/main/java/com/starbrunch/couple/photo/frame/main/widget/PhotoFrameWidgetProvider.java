package com.starbrunch.couple.photo.frame.main.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.widget.RemoteViews;

import com.starbrunch.couple.photo.frame.main.R;
import com.starbrunch.couple.photo.frame.main.common.CommonUtils;

/**
 * Created by 정재현 on 2018-01-05.
 */

public class PhotoFrameWidgetProvider extends AppWidgetProvider
{
    /**
     * 브로드캐스트를 수신할때, Override된 콜백 메소드가 호출되기 직전에 호출됨
     */
    @Override
    public void onReceive(Context context, Intent intent)
    {
        super.onReceive(context, intent);
    }

    public static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId)
    {
        RemoteViews updateViews = new RemoteViews(context.getPackageName(), R.layout.widget_4x4_photo_framelayout);


        Bitmap bitmap = CommonUtils.getInstance(context).getBitmapFromDrawable(context.getResources().getDrawable(R.drawable.test_image_9),
                480, 367);

        updateViews.setImageViewBitmap(R.id._widgetImage, CommonUtils.getInstance(context).getRoundedCornerBitmap(bitmap));
        appWidgetManager.updateAppWidget(appWidgetId, updateViews);
    }

    /**
     * 위젯을 갱신할때 호출됨
     *
     * 주의 : Configure Activity를 정의했을때는 위젯 등록시 처음 한번은 호출이 되지 않습니다
     */
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds)
    {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    /**
     * 위젯이 처음 생성될때 호출됨
     *
     * 동일한 위젯이 생성되도 최초 생성때만 호출됨
     */
    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
    }

    /**
     * 위젯의 마지막 인스턴스가 제거될때 호출됨
     *
     * onEnabled()에서 정의한 리소스 정리할때
     */
    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
    }

    /**
     * 위젯이 사용자에 의해 제거될때 호출됨
     */
    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {

        super.onDeleted(context, appWidgetIds);

    }

}
