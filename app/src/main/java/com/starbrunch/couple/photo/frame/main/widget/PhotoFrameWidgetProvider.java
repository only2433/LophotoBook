package com.starbrunch.couple.photo.frame.main.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.RemoteViews;

import com.littlefox.logmonitor.Log;
import com.starbrunch.couple.photo.frame.main.R;
import com.starbrunch.couple.photo.frame.main.common.Common;
import com.starbrunch.couple.photo.frame.main.common.CommonUtils;
import com.starbrunch.couple.photo.frame.main.database.PhotoInformationDBHelper;
import com.starbrunch.couple.photo.frame.main.object.PhotoInformationObject;

import java.util.ArrayList;

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

        if(intent.getAction().equals(Common.INTENT_WIDGET_UPDATE))
        {
            Log.i("");
            AppWidgetManager manager = AppWidgetManager.getInstance(context);
            ComponentName appwidgetName = new ComponentName(context.getPackageName(), PhotoFrameWidgetProvider.class.getName());
            int[] appwidgetIds = manager.getAppWidgetIds(appwidgetName);
            onUpdate(context,manager,appwidgetIds);
        }
    }

    public static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId)
    {

        RemoteViews updateViews = new RemoteViews(context.getPackageName(), R.layout.widget_4x4_photo_framelayout);


        int currentMonth = CommonUtils.getInstance(context).getCurrentMonth(System.currentTimeMillis());

        Log.i("currentMonth : "+ currentMonth);

        ArrayList<PhotoInformationObject> resultList = PhotoInformationDBHelper.getInstance(context).getPhotoInformationListByMonth(Common.MONTH_TEXT_LIST[currentMonth]);
        Log.i("resultList size : "+ resultList.size());

        int roundPixel = CommonUtils.getInstance(context).convertDiptoPix(5);


        if(resultList.size() > 0)
        {
            updateViews.setViewVisibility(R.id._emptyLayout, View.GONE);
            int randomIndex = (int)(Math.random() * resultList.size());
            Log.i("randomIndex : "+ randomIndex);
            Log.i("fileName : "+ resultList.get(randomIndex).getFileName());
            Bitmap imageBitmap = CommonUtils.getInstance(context).getBitmapFromFile(Common.PATH_IMAGE_ROOT+resultList.get(randomIndex).getFileName());
            Bitmap alphaBitmap = CommonUtils.getInstance(context).getRoundedCornerRect(imageBitmap.getWidth(), imageBitmap.getHeight(), context.getResources().getColor(R.color.color_alpha));


            Bitmap dayTimeBitmap = CommonUtils.getInstance(context).getFontBitmap(
                    CommonUtils.getInstance(context).getDateTime(resultList.get(randomIndex).getDateTime()),
                    context.getResources().getColor(R.color.color_white),
                    10,
                    360,
                    80,
                    "fonts/Roboto-Light.ttf",
                    false);

            Bitmap dayNumberBitmap = CommonUtils.getInstance(context).getFontBitmap(
                    CommonUtils.getInstance(context).getDateDay(resultList.get(randomIndex).getDateTime()),
                    context.getResources().getColor(R.color.color_white),
                    35,
                    360,
                    80,
                    "fonts/Roboto-Bold.ttf",
                    false);

            Bitmap dayFullTextBitmap = CommonUtils.getInstance(context).getFontBitmap(
                    CommonUtils.getInstance(context).getDateFullText(resultList.get(randomIndex).getDateTime()),
                    context.getResources().getColor(R.color.color_white),
                    10,
                    360,
                    80,
                    "fonts/Roboto-Light.ttf",
                    false
                    );

            updateViews.setImageViewBitmap(R.id._widgetImage, CommonUtils.getInstance(context).getRoundedCornerBitmap(imageBitmap,roundPixel));
            updateViews.setImageViewBitmap(R.id._widgetAlphaImage, CommonUtils.getInstance(context).getRoundedCornerBitmap(alphaBitmap,roundPixel));

            updateViews.setImageViewBitmap(R.id._widgetDayTimeText, dayTimeBitmap);
            updateViews.setImageViewBitmap(R.id._widgetDayNumberText, dayNumberBitmap);
            updateViews.setImageViewBitmap(R.id._widgetFullDateText, dayFullTextBitmap);

            imageBitmap = null;
            dayTimeBitmap = null;
            dayNumberBitmap = null;
            dayFullTextBitmap = null;
            alphaBitmap = null;

        }
        else
        {
            updateViews.setViewVisibility(R.id._widgetDayTimeText, View.GONE);
            updateViews.setViewVisibility(R.id._widgetDayNumberText, View.GONE);
            updateViews.setViewVisibility(R.id._widgetFullDateText, View.GONE);


            Bitmap emptyIconBitmap = CommonUtils.getInstance(context).getBitmapFromDrawable(context.getResources().getDrawable(R.drawable.empty_image),
                    480, 367);
            Bitmap emptyMessageBitmap = CommonUtils.getInstance(context).getFontBitmap(
                    context.getResources().getString(R.string.message_empty_picture_data),
                    context.getResources().getColor(R.color.color_999999),
                    20,
                    360,
                    20,
                    "fonts/Roboto-Light.ttf",
                    true);
            updateViews.setImageViewBitmap(R.id._messageText, emptyMessageBitmap);
            updateViews.setImageViewBitmap(R.id._widgetImage, CommonUtils.getInstance(context).getRoundedCornerBitmap(emptyIconBitmap,roundPixel));

            emptyIconBitmap = null;
            emptyMessageBitmap = null;
        }


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
