package com.starbrunch.couple.photo.frame.main.common;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;

/**
 * Created by 정재현 on 2017-12-13.
 */

public class FontManager
{
    private static FontManager self;
    private Typeface mMainTitleFont = null;
    private Typeface mMainMonthTextFont = null;
    private Typeface mMainImageCountFont = null;
    private Typeface mDefaultBoldTextFont = null;
    private Typeface mDefaultRegularTextFont = null;
    private Typeface mDefaultLightTextFont = null;


    public static FontManager getInstance(Context context)
    {
        if(self == null)
        {
            self = new FontManager(context);
        }

        return self;
    }

    private FontManager(Context context)
    {
        AssetManager assetManager = context.getAssets();
        mMainTitleFont = Typeface.createFromAsset(assetManager, "fonts/mexcellentrg.ttf");
        mMainMonthTextFont = Typeface.createFromAsset(assetManager, "fonts/quigleywiggly.ttf");
        mMainImageCountFont = Typeface.createFromAsset(assetManager, "fonts/johnny_fever.ttf");
        mDefaultBoldTextFont = Typeface.createFromAsset(assetManager, "fonts/Roboto-Bold.ttf");
        mDefaultRegularTextFont = Typeface.createFromAsset(assetManager, "fonts/Roboto-Medium.ttf");
        mDefaultLightTextFont = Typeface.createFromAsset(assetManager, "fonts/Roboto-Light.ttf");

        if(mMainTitleFont == null)
        {
            mMainTitleFont = Typeface.DEFAULT;
        }

        if(mMainMonthTextFont == null)
        {
            mMainMonthTextFont = Typeface.DEFAULT;
        }

        if(mMainImageCountFont == null)
        {
            mMainImageCountFont = Typeface.DEFAULT;
        }

        if(mDefaultBoldTextFont == null)
        {
            mDefaultBoldTextFont = Typeface.DEFAULT;
        }

        if(mDefaultRegularTextFont == null)
        {
            mDefaultRegularTextFont = Typeface.DEFAULT;
        }

        if(mDefaultLightTextFont == null)
        {
            mDefaultLightTextFont = Typeface.DEFAULT;
        }
    }

    public Typeface getMainTitleFont()
    {
        return mMainTitleFont;
    }
    public Typeface getMainMonthTextFont()
    {
        return  mMainMonthTextFont;
    }

    public Typeface getmMainImageCountFont()
    {
        return mMainImageCountFont;
    }
    public Typeface getDefaultBoldTextFont()
    {
        return mDefaultBoldTextFont;
    }

    public Typeface getDefaultLightTextFont()
    {
        return mDefaultLightTextFont;
    }

    public Typeface getmDefaultRegularTextFont()
    {
        return mDefaultRegularTextFont;
    }
}
