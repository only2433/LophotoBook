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
    private Typeface mRampungRagularFont    = null;
    private Typeface mPhenomenaBoldFont     = null;
    private Typeface mPhenomenaLightFont    = null;
    private Typeface mPhenomenaThinFont     = null;
    private Typeface mOccopiedFont          = null;
    private Typeface mBodrumRegularFont     = null;
    private Typeface mTypolinoRegularFont   = null;


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
        mRampungRagularFont     = Typeface.createFromAsset(assetManager, "fonts/rampung.regular.ttf");
        mOccopiedFont           = Typeface.createFromAsset(assetManager,"fonts/Occupied.otf");
        mBodrumRegularFont      = Typeface.createFromAsset(assetManager, "fonts/bodrum.regular.ttf");
        mTypolinoRegularFont    = Typeface.createFromAsset(assetManager, "fonts/typolino.regular.ttf");

        mDefaultBoldTextFont    = Typeface.createFromAsset(assetManager, "fonts/Roboto-Bold.ttf");
        mDefaultRegularTextFont = Typeface.createFromAsset(assetManager, "fonts/Roboto-Medium.ttf");
        mDefaultLightTextFont   = Typeface.createFromAsset(assetManager, "fonts/Roboto-Light.ttf");


        mPhenomenaBoldFont  = Typeface.createFromAsset(assetManager, "fonts/Phenomena-Bold.otf");
        mPhenomenaLightFont = Typeface.createFromAsset(assetManager, "fonts/Phenomena-Light.otf");
        mPhenomenaThinFont  = Typeface.createFromAsset(assetManager, "fonts/Phenomena-Thin.otf");



        if(mRampungRagularFont == null)
        {
            mRampungRagularFont = Typeface.DEFAULT;
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

        if(mPhenomenaBoldFont == null)
        {
            mPhenomenaBoldFont = Typeface.DEFAULT;
        }

        if(mPhenomenaLightFont == null)
        {
            mPhenomenaLightFont = Typeface.DEFAULT;
        }

        if(mPhenomenaThinFont == null)
        {
            mPhenomenaThinFont = Typeface.DEFAULT;
        }

        if(mOccopiedFont == null)
        {
            mOccopiedFont = Typeface.DEFAULT;
        }

        if(mBodrumRegularFont == null)
        {
            mBodrumRegularFont = Typeface.DEFAULT;
        }

        if(mTypolinoRegularFont == null)
        {
            mTypolinoRegularFont = Typeface.DEFAULT;
        }



    }

    public Typeface getRampungRagularFont()
    {
        return mRampungRagularFont;
    }


    public Typeface getDefaultBoldTextFont()
    {
        return mDefaultBoldTextFont;
    }

    public Typeface getDefaultLightTextFont()
    {
        return mDefaultLightTextFont;
    }

    public Typeface getDefaultRegularTextFont()
    {
        return mDefaultRegularTextFont;
    }

    public Typeface getPhenomenaBoldFont()
    {
        return mPhenomenaBoldFont;
    }

    public Typeface getPhenomenaLightFont()
    {
        return mPhenomenaLightFont;
    }

    public Typeface getPhenomenaThinFont()
    {
        return mPhenomenaThinFont;
    }

    public Typeface getOccopiedFont()
    {
        return mOccopiedFont;
    }

    public Typeface getBodrumRegularFont()
    {
        return mBodrumRegularFont;
    }

    public Typeface getTypolinoRegularFont()
    {
        return mTypolinoRegularFont;
    }
}
