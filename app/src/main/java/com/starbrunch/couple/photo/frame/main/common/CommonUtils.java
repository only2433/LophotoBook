package com.starbrunch.couple.photo.frame.main.common;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.transition.Slide;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.gson.Gson;
import com.littlefox.library.system.common.FileUtils;
import com.littlefox.library.view.object.DisPlayMetricsObject;
import com.littlefox.logmonitor.Log;
import com.starbrunch.couple.photo.frame.main.R;
import com.starbrunch.couple.photo.frame.main.base.MainApplication;
import com.starbrunch.couple.photo.frame.main.object.PhotoInformationObject;
import com.starbrunch.couple.photo.frame.main.widget.PhotoFrameWidgetProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;
import java.util.Locale;

/**
 * Created by 정재현 on 2015-07-07.
 */
public class CommonUtils
{
	public static CommonUtils sCommonUtils = null;
	public static Context sContext = null;
	
	public static CommonUtils getInstance(Context context)
	{
		if(sCommonUtils == null)
		{
			sCommonUtils = new CommonUtils();
		}
		sContext = context;
		
		return sCommonUtils;
	}

    public String getDateFullText(long timeMs)
	{
    	Date date = new Date(timeMs);
    	String todayString  = new SimpleDateFormat("EEEE, MMMM dd, yyyy", Locale.ENGLISH).format(date);
    	return todayString;
    }

    public String getDateDay(long timeMs)
	{
		Date date = new Date(timeMs);
		String todayString  = new SimpleDateFormat("dd", Locale.ENGLISH).format(date);
		return todayString;
	}

	public String getDateClock(long timeMs)
	{
		Date date = new Date(timeMs);
		String todayString  = new SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(date);
		return todayString;
	}


    
    /**
     * 결제 관련 되서 화면에 표현해주는 형식으로 전달해준다.
     * @param isEnglish TRUE : 한국 말고 나머지 국가 , FALSE : 한국
     * @param timeMs : 시간
     * @return
     */
    public String getPayInformationDate(boolean isEnglish, long timeMs)
    {
    	String todayString = "";
    	Date date = new Date(timeMs);
    	if(isEnglish)
    	{
    		todayString = new SimpleDateFormat("dd MMM yyyy").format(date);
    	}
    	else
    	{
    		todayString  = new SimpleDateFormat("yyyy년 MM월 dd일").format(date);
    	}
    	  
    	return todayString;
    }
    

    
    /**
     * 세컨드를 시간 String  으로 리턴한다.
     * @param millisecond 밀리세컨드
     * @return 시간 String Ex) HH:MM:TT
     */
    public String getSecondTime(int millisecond)
    {
    	 StringBuilder mFormatBuilder = new StringBuilder();
         Formatter mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());

         int seconds = millisecond % 60;
         int minutes = (millisecond / 60) % 60;
         int hours   = millisecond / 3600;

         mFormatBuilder.setLength(0);
         if (hours > 0) {
             return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
         } else {
             return mFormatter.format("%02d:%02d", minutes, seconds).toString();
         }
    }

    public void showDeviceInfo()
    {
    	Log.f("BRAND : "+ Build.BRAND);
    	Log.f("DEVICE : "+ Build.DEVICE);
    	Log.f("MODEL : "+ Build.MODEL);
    	Log.f("VERSION SDK : "+ Build.VERSION.SDK_INT);
    	Log.f("APP VERSION : "+getPackageVersionName(Common.PACKAGE_NAME));
    	Log.f("Device ID : "+ CommonUtils.getInstance(sContext).getMacAddress());
    	Log.f("WIDTH PIXEL : " + MainApplication.sDisPlayMetrics.widthPixels+", HEIGHT PIXEL : " + MainApplication.sDisPlayMetrics.heightPixels);
    }

    /**
     * <pre>
     * Window 의 정보를 얻어온다.
     * </pre>
     *
     * @return
     */
    public void getWindowInfo()
    {
    	int width = 0;
    	int height = 0;
        if(MainApplication.sDisPlayMetrics == null)
        {
            MainApplication.sDisPlayMetrics  = new DisplayMetrics();
        }
        ((Activity) sContext).getWindowManager().getDefaultDisplay().getMetrics(MainApplication.sDisPlayMetrics);
       
        width = MainApplication.sDisPlayMetrics.widthPixels;
        height = MainApplication.sDisPlayMetrics.heightPixels;
        
        /**
         * 방어코드 가끔 OS 결함으로 width , height 가 잘못들어올때 방어코드 처리 
         */
        if(Feature.IS_TABLET)
        {
        	if(MainApplication.sDisPlayMetrics.widthPixels < MainApplication.sDisPlayMetrics.heightPixels)
            {
        		MainApplication.sDisPlayMetrics.widthPixels = height;
        		MainApplication.sDisPlayMetrics.heightPixels = width;
            }
        }
        else
        {
        	if(MainApplication.sDisPlayMetrics.widthPixels > MainApplication.sDisPlayMetrics.heightPixels)
            {
        		MainApplication.sDisPlayMetrics.widthPixels = height;
        		MainApplication.sDisPlayMetrics.heightPixels = width;
            }
        }
        
        DisPlayMetricsObject object = new DisPlayMetricsObject(MainApplication.sDisPlayMetrics.widthPixels, MainApplication.sDisPlayMetrics.heightPixels);
        setPreferenceObject(Common.PARAMS_DISPLAY_METRICS, object);
    }
   

    /**
     * 1080 * 1920  기준으로 멀티 해상도의 픽셀을 계산한다. Tablet 은 1920 * 1200
     * @param value 1080 * 1920  의 픽셀
     * @return
     */
    public int getPixel(int value)
    {
		try
		{
			if (MainApplication.sDisplayFactor == 0.0f)
			{
    			if(Feature.IS_TABLET)
    				MainApplication.sDisplayFactor = MainApplication.sDisPlayMetrics.widthPixels / 1920.0f;
    			else
    				MainApplication.sDisplayFactor = MainApplication.sDisPlayMetrics.widthPixels / 1080.0f;
			}
		}
		catch (NullPointerException e)
		{
			DisPlayMetricsObject object = (DisPlayMetricsObject) getPreferenceObject(Common.PARAMS_DISPLAY_METRICS, DisPlayMetricsObject.class);
			if(Feature.IS_TABLET)
				MainApplication.sDisplayFactor = MainApplication.sDisPlayMetrics.widthPixels / 1920.0f;
			else
				MainApplication.sDisplayFactor = object.widthPixel / 1080.0f;
		}
		
		
        return (int)(value * MainApplication.sDisplayFactor);
    }

    /**
     * 1080 * 1920  기준으로 멀티 해상도의 픽셀을 계산한다. Tablet 은 1920 * 1200
     * @param value 1080 * 1920  의 픽셀
     * @return
     */
	public float getPixel(float value)
	{
		try
		{
			if (MainApplication.sDisplayFactor == 0.0f)
			{
    			if(Feature.IS_TABLET)
    				MainApplication.sDisplayFactor = MainApplication.sDisPlayMetrics.widthPixels / 1920.0f;
    			else
    				MainApplication.sDisplayFactor = MainApplication.sDisPlayMetrics.widthPixels / 1080.0f;
			}
		}
		catch (NullPointerException e)
		{
			DisPlayMetricsObject object = (DisPlayMetricsObject) getPreferenceObject(Common.PARAMS_DISPLAY_METRICS, DisPlayMetricsObject.class);
			if(Feature.IS_TABLET)
				MainApplication.sDisplayFactor = MainApplication.sDisPlayMetrics.widthPixels / 1920.0f;
			else
				MainApplication.sDisplayFactor = object.widthPixel / 1080.0f;
		}

		return value * MainApplication.sDisplayFactor;
	}

    /**
     * 1080 * 1920 기준으로 멀티 해상도의 픽셀을 계산한다. Tablet 은 1920 * 1200
     * @param value 1080 * 1920 의 픽셀
     * @return
     */
    public int getHeightPixel(int value)
    {
    	try
		{
			if (MainApplication.sDisplayFactor == 0.0f)
			{
				if(Feature.IS_TABLET)
					MainApplication.sDisplayFactor = MainApplication.sDisPlayMetrics.heightPixels / 1200.0f;
				else
					MainApplication.sDisplayFactor = MainApplication.sDisPlayMetrics.heightPixels / 1920.0f;
			}
		}
		catch (NullPointerException e)
		{
			DisPlayMetricsObject object = (DisPlayMetricsObject) getPreferenceObject(Common.PARAMS_DISPLAY_METRICS, DisPlayMetricsObject.class);
			if(Feature.IS_TABLET)
				MainApplication.sDisplayFactor = MainApplication.sDisPlayMetrics.heightPixels / 1200.0f;
			else
				MainApplication.sDisplayFactor = object.heightPixel / 1080.0f;
		}
		return (int)(value * MainApplication.sDisplayFactor);
    }

    /**
     * 1080 * 1920기준으로 멀티 해상도의 픽셀을 계산한다.
     * @param value 1080 * 1920 의 픽셀
     * @return
     */
    public float getHeightPixel(float value)
    {
		try
		{
			if (MainApplication.sDisplayFactor == 0.0f)
			{
				if(Feature.IS_TABLET)
					MainApplication.sDisplayFactor = MainApplication.sDisPlayMetrics.heightPixels / 1200.0f;
				else
					MainApplication.sDisplayFactor = MainApplication.sDisPlayMetrics.heightPixels / 1920.0f;
			}
		}
		catch (NullPointerException e)
		{
			DisPlayMetricsObject object = (DisPlayMetricsObject) getPreferenceObject(Common.PARAMS_DISPLAY_METRICS, DisPlayMetricsObject.class);
			if(Feature.IS_TABLET)
				MainApplication.sDisplayFactor = MainApplication.sDisPlayMetrics.heightPixels / 1200.0f;
			else
				MainApplication.sDisplayFactor = object.heightPixel / 1080.0f;
		}
		return value * MainApplication.sDisplayFactor;
    }
    
    /**
     * 최소 지원 해상도를 리턴
     * @return
     */
    public int getMinDisplayWidth()
    {
    	if(Feature.IS_TABLET)
    	{
    		return 1280;
    	}
    	else
    	{
    		return 1080;
    	}
    }
    
    public float getDisplayWidth()
    {
    	DisPlayMetricsObject object = (DisPlayMetricsObject) getPreferenceObject(Common.PARAMS_DISPLAY_METRICS, DisPlayMetricsObject.class);
    	if(object != null)
    	{
    		return object.widthPixel;
    	}
    	return 0f;
    }
    
    public float getDisplayHeight()
    {
    	DisPlayMetricsObject object = (DisPlayMetricsObject) getPreferenceObject(Common.PARAMS_DISPLAY_METRICS, DisPlayMetricsObject.class);
    	if(object != null)
    	{
    		return object.heightPixel;
    	}
    	return 0f;
    }

    public DisplayMetrics getDisPlayMetrics()
    {
        return MainApplication.sDisPlayMetrics;
    }

    /**
     * 저장한 프리퍼런스를 불러온다.
     * @param key  해당 값의 키값
     * @param type 데이터의 타입
     * @return
     */
    public Object getSharedPreference(String key, int type)
    {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(sContext);

        switch (type)
        {
            case Common.TYPE_PARAMS_BOOLEAN:
                return pref.getBoolean(key, false);
            case Common.TYPE_PARAMS_INTEGER:
                return pref.getInt(key, -1);
			case Common.TYPE_PARAMS_LONG:
				return pref.getLong(key, -1L);
            case Common.TYPE_PARAMS_STRING:
                return pref.getString(key, "");
        }

        return pref.getBoolean(key, false);
    }

    /**
     * 해당 프리퍼런스를 저장한다.
     * @param key 해당 값의 키값
     * @param object 저장할 데이터
     */
    public void setSharedPreference(String key, Object object)
    {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(sContext);
        SharedPreferences.Editor editor = pref.edit();

        if(object instanceof Boolean)
        {
            editor.putBoolean(key, (Boolean) object);
        }
        else if(object instanceof Integer)
        {
            editor.putInt(key, (Integer) object);
        }
        else if(object instanceof String)
        {
            editor.putString(key, (String) object);
        }
		else if(object instanceof Long)
		{
			editor.putLong(key, (Long) object);
		}

        editor.commit();

    }
    
    
    /**
     * 현재 디스플레이의 가로 해상도를 리턴
     * @return
     */
    public int getDisplayWidthPixel()
    {
    	if(MainApplication.sDisPlayMetrics == null)
    	{
    		return 0;
    	}
    	
    	return MainApplication.sDisPlayMetrics.widthPixels;
    }
    
    /**
     * 현재 디스플레이의 세로 해상도를 리턴
     * @return
     */
    public int getDisplayHeightPixel()
    {
    	if(MainApplication.sDisPlayMetrics == null)
    	{
    		return 0;
    	}
    	
    	return MainApplication.sDisPlayMetrics.heightPixels;
    }
    
    /**
     * 해상도가 특정 이하의 해상도인지 확인하는 메소드
     * @return TRUE : Minimum 보다 이하 , FALSE : Minimum 보다 이상
     */
	public  boolean isDisplayMinimumSize()
	{
		Log.i("CommonUtils.getDisplayWidthPixel(context) : "+getDisplayWidthPixel());
		if(getMinDisplayWidth() > getDisplayWidthPixel())
		{
			return true;
		}
		
		return false;
	}

    /**
     * 현재 모델이 타블릿인지 아닌지 확인
     * @return
     */
    public boolean isTablet()
    {
        int xlargeBit = 4; // Configuration.SCREENLAYOUT_SIZE_XLARGE;
        Configuration config = sContext.getResources().getConfiguration();
        return (config.screenLayout & xlargeBit) == xlargeBit;
    }


    /**
     * 패키지버젼코드 확인
     *
     * @return 패키지 버젼 코드
     */
    public int getPackageVersionCode()
    {
        int result = -1;
        try
        {
            PackageInfo pi = sContext.getPackageManager().getPackageInfo(Common.PACKAGE_NAME, 0);
            if (pi != null)
                result = pi.versionCode;
        }
        catch (Exception ex)
        {
            Log.f("getPackageVersionCode Error : "+ ex.getMessage());
        }
        return result;
    }

    /**
     * 패키지 버전 네임 확인
     * @return 패키지 버전 네임
     */
    public String getPackageVersionName(String packageName)
    {
        String result = "";
        try
        {
            PackageInfo pi = sContext.getPackageManager().getPackageInfo(packageName, 0);
            if (pi != null)
                result = pi.versionName;
        }
        catch (Exception ex)
        {
            Log.f("getPackageVersionName Error : "+ ex.getMessage());
        }
        return result;
    }

    /**
     * 인스톨 되어있나 검색한다.
     * @param packageName 해당 패키지 명
     * @return
     */
    public boolean isInstalledPackage(String packageName)
    {
        boolean result = true;
        try
        {
            Intent intent = sContext.getPackageManager().getLaunchIntentForPackage(packageName);
            if(intent == null)
            {
                result = false;
            }
        }catch(Exception e)
        {
            result = false;
        }

        return result;
    }

	/**
	 * Check the device to make sure it has the Google Play Services APK. If it doesn't, display a dialog that allows users to download the APK from the Google Play Store or enable it in the device's
	 * system settings.
	 */

	public static boolean checkPlayServices()
	{
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = googleApiAvailability.isGooglePlayServicesAvailable(sContext);
		switch (resultCode)
		{
		case ConnectionResult.SUCCESS:
			return true;
		case ConnectionResult.SERVICE_DISABLED:
		case ConnectionResult.SERVICE_INVALID:
		case ConnectionResult.SERVICE_MISSING:
		case ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED:
			Dialog dialog = googleApiAvailability.getErrorDialog((Activity) sContext, resultCode, 0);
			dialog.setOnCancelListener(new DialogInterface.OnCancelListener()
			{
				@Override
				public void onCancel(DialogInterface dialogInterface)
				{
					((Activity) sContext).finish();
				}
			});
			dialog.show();
		}
		return false;
	}


    /**
     * 맥 어드레스를 받아온다.
     * @return
     */
    public String getMacAddress()
    {
        WifiManager wifimanager = (WifiManager)sContext.getSystemService(Context.WIFI_SERVICE);
        return wifimanager.getConnectionInfo().getMacAddress();
    }

   

    /**
     * This method converts dp unit to equivalent pixels, depending on device density.
     *
     * @param dp A value in dp (density independent pixels) unit. Which we need to convert into pixels
     * @return A float value to represent px equivalent to dp depending on device density
     */
    public float convertDpToPixel(float dp){
        Resources resources = sContext.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return px;
    }

    /**
     * This method converts device specific pixels to density independent pixels.
     *
     * @param px A value in px (pixels) unit. Which we need to convert into db
     * @return A float value to represent dp equivalent to px value
     */
    public float convertPixelsToDp(float px){
        Resources resources = sContext.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / (metrics.densityDpi / 160f);
        return dp;
    }

    
    public long getAvailableStorageSize()
    {
        StatFs stat = new StatFs(Environment.getDataDirectory().getPath());
        long result;
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2)
        {
            result = stat.getAvailableBlocksLong() * stat.getBlockSizeLong();
        }
        else
        {
            result = (long)stat.getAvailableBlocks()* (long)stat.getBlockSize();
        }


        return result/(1024 * 1024);
    }

    /**
     * 비디오파일이 다운로드된 Internal Storage폴더의 사이즈를 리턴
     */
	public long getSizeVideoFileStorage()
	{
		long totalSize = 0;
		File appBaseFolder = new File(Common.PATH_APP_ROOT);
		for (File f : appBaseFolder.listFiles())
		{
                if (f.isDirectory())
                {
                    long dirSize = browseFiles(f);
                    totalSize += dirSize;
			}
			else
			{
				totalSize += f.length();
			}
		}
		Log.f("App uses " + totalSize + " total bytes");
		
		return totalSize/(1024 * 1024);
	}

	private long browseFiles(File dir)
	{
		long dirSize = 0;
		for (File f : dir.listFiles())
		{
			dirSize += f.length();
			if (f.isDirectory())
			{
				dirSize += browseFiles(f);
			}
		}
		return dirSize;
	}

    
    public Bitmap getBitmapFromDrawable(Drawable mDrawable, int width, int height)
	{
		Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

		Canvas canvas = new Canvas(bitmap);

		mDrawable.setBounds(0, 0, width, height);

		mDrawable.draw(canvas);

		return bitmap;

	}

	public Bitmap getBitmapFromFile(String filePath)
	{
		Bitmap bitmap = BitmapFactory.decodeFile(filePath);
		return bitmap;
	}


	public Bitmap getRoundedCornerBitmap(Bitmap bitmap, int roundPixel)
	{
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);

		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);
		final float roundPx = roundPixel;

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);

		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

		canvas.drawBitmap(bitmap, rect, rect, paint);

		return output;
	}

    public Bitmap getRoundedCornerRect(int width, int height, int color)
    {
        Bitmap output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);


        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, width, height);
        final RectF rectF = new RectF(rect);


        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, getPixel(20), getPixel(20), paint);

        return output;
    }

    public Drawable getDrawableFromBitmap(Bitmap mBitmap)
    {
        BitmapDrawable bitmapDrawable = new BitmapDrawable(sContext.getResources(), mBitmap);

        return bitmapDrawable;
    }

    public Drawable getScaledDrawable(int width, int height, int drawable)
    {
        Bitmap bitmap 	= BitmapFactory.decodeResource(sContext.getResources(), drawable);
        bitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);
        return getDrawableFromBitmap(bitmap);
    }


    /**
     * 앱버젼이 같은지 확인
     * @param appVersion 서버의 버젼
     * @return TRUE : 현재 로컬버젼과 같다. </p> FALSE : 현재 로컬버젼과 다르다.
     */
    public boolean isAppVersionEqual(String appVersion)
    {
        return appVersion.equals(getPackageVersionName(Common.PACKAGE_NAME));
    }


    
    /**
     * 오브젝트 클래스를 불러오는 프리퍼런스
     * @param key 키값
     * @param className 클래스 네임
     * @return
     */
    public Object getPreferenceObject(String key, Class className)
    {
    	Object result = null;
    	 SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(sContext);
    	 String loadObjectString = pref.getString(key, "");
    	 
    	 if(loadObjectString.equals("") == false)
    	 {
    		 result = new Gson().fromJson(loadObjectString, className);
    	 }
    	
    	 return result;
    }
    
    /**
     * 오브젝트 클래스를 저장하는 프리퍼런스
     * @param key 키값
     * @param object 저장할 오브젝트
     */
    public void setPreferenceObject(String key, Object object)
    {
    	String saveObjectString = "";
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(sContext);
        SharedPreferences.Editor editor = pref.edit();
        
        if(object != null)
        {
        	saveObjectString = new Gson().toJson(object);
        }
        
        editor.putString(key, saveObjectString);
        editor.commit();
    }


    public Animation getTranslateYAnimation(float fromYValue, float toYValue, int duration, int delay, Interpolator interpolator)
    {
        Animation anim = null;
        anim = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0, Animation.ABSOLUTE,fromYValue, Animation.ABSOLUTE, toYValue);
        anim.setDuration(duration);
        anim.setFillAfter(true);

        if(delay != 0 )
        {
            anim.setStartOffset(delay);
        }
        if(interpolator != null)
        {
            anim.setInterpolator(interpolator);
        }
        return anim;
    }

	public Animation getTranslateXAnimation(float fromXValue, float toXValue, int duration, int delay, Interpolator interpolator)
	{
		Animation anim = null;
		anim = new TranslateAnimation(Animation.ABSOLUTE, fromXValue, Animation.ABSOLUTE, toXValue, Animation.RELATIVE_TO_PARENT,0, Animation.RELATIVE_TO_PARENT, 0);
		anim.setDuration(duration);
		anim.setFillAfter(true);

		if(delay != 0 )
		{
			anim.setStartOffset(delay);
		}
		if(interpolator != null)
		{
			anim.setInterpolator(interpolator);
		}
		return anim;
	}

	
	public Animation getAlphaAnimation(int duration, float fromValue, float toValue)
	{
		Animation anim = null;
		anim = new AlphaAnimation(fromValue, toValue);
		anim.setDuration(duration);
		anim.setFillAfter(true);
		return anim;
	}
	
	public void setStatusBar(int color )
	{
		Window window = ((Activity) sContext).getWindow();
		window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
		window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
		window.setStatusBarColor(color);
	}
	
	public void showSnackMessage(CoordinatorLayout coordinatorLayout, String message, int color)
	{
		showSnackMessage(coordinatorLayout, message, color, -1);
	}
	
	public void showSnackMessage(CoordinatorLayout coordinatorLayout, String message, int color , int gravity)
	{
		Snackbar snackbar = Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_SHORT);
		View view = snackbar.getView();
		TextView textView = (TextView)view.findViewById(android.support.design.R.id.snackbar_text);
		textView.setTextColor(color);
		if(gravity != -1)
			textView.setGravity(gravity);
		snackbar.show();
	}
	
	public void showSnackMessage(CoordinatorLayout coordinatorLayout, String[] message, int[] color)
	{
		int beforeCount = 0;
		String messageText = "";
		SpannableStringBuilder spannableStringBuilder;
		for(String s : message)
		{
			messageText += s;
		}
		
		spannableStringBuilder = new SpannableStringBuilder(messageText);
		for(int i = 0; i < message.length; i++)
		{
			int currentCount = 0;
			for(int j = 0; j < i +1; j++)
			{
				currentCount += message[j].length();
			}
			spannableStringBuilder.setSpan(new ForegroundColorSpan(color[i]) ,beforeCount , currentCount , Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

			beforeCount = currentCount;
		}
		
		Snackbar snackbar = Snackbar.make(coordinatorLayout, messageText, Snackbar.LENGTH_SHORT);
		View view = snackbar.getView();
		TextView textView = (TextView)view.findViewById(android.support.design.R.id.snackbar_text);
		textView.setText(spannableStringBuilder);
		snackbar.show();
	}

	


	
	/**
	 *  30일이 지났는 지 체크
	 * @param payEndMiliseconds 해당 시간
	 * @return TRUE: 30일 이 넘음 , FALSE : 30일이 지나지 않음
	 */
	public boolean isOverPayDay(long payEndMiliseconds)
	{
		Log.f("Today : " + getDateClock(System.currentTimeMillis()));
		Log.f("Pay End Day : " + getDateClock(payEndMiliseconds));
		
		if(System.currentTimeMillis() >=  payEndMiliseconds)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	

	
	public int getCurrentMonth(long currentPaidMilliseconds)
	{
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(currentPaidMilliseconds);
		return calendar.get(Calendar.MONTH);
	}

	public String[] getSyncronizeTime(int time)
	{
		String[] result = new String[3];
		int hour = time / (60 * 60);
		int minute = (time % (60 * 60)) / 60;
		int second = (time % (60 * 60)) % 60;

		if(hour < 10)
		{
			result[0] = "0"+ hour;
		}
		else if(hour > 10)
		{
			result[0] = String.valueOf(hour);
		}

		if(minute < 10)
		{
			result[1] = "0"+ minute;
		}
		else if(minute > 10)
		{
			result[1] = String.valueOf(minute);
		}

		if(second < 10)
		{
			result[2] = "0"+ second;
		}
		else if(second > 10)
		{
			result[2] = String.valueOf(second);
		}

		return result;
	}

	
	public void startLinkMove(String link)
	{
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(Uri.parse(link));
		sContext.startActivity(intent);
	}
	
	/**
	 * 소프트 네비게이션 바가 있는지 체크
	 * @return
	 */
	public boolean isHaveNavigationBar()
	{
		 Point appUsableSize = getAppUsableScreenSize();
		 Point realScreenSize = getRealScreenSize();
		 
		 if (appUsableSize.y < realScreenSize.y) 
		 {
			 return true;
		 }
		 else
		 {
			 return false;
		 }
	}
	
	/**
	 * 네이게이션바 사이즈를 리턴한다.
	 * @return
	 */
	public Point getNavigationBarSize()
    {
	    Point appUsableSize = getAppUsableScreenSize();
	    Point realScreenSize = getRealScreenSize();

	    // navigation bar on the right
	    if (appUsableSize.x < realScreenSize.x) 
	    {
	        return new Point(realScreenSize.x - appUsableSize.x, appUsableSize.y);
	    }

	    // navigation bar at the bottom
	    if (appUsableSize.y < realScreenSize.y) 
	    {
	        return new Point(appUsableSize.x, realScreenSize.y - appUsableSize.y);
	    }

	    // navigation bar is not present
	    return new Point();
	}

	public Point getAppUsableScreenSize()
    {
	    WindowManager windowManager = (WindowManager) sContext.getSystemService(Context.WINDOW_SERVICE);
	    Display display = windowManager.getDefaultDisplay();
	    Point size = new Point();
	    display.getSize(size);
	    return size;
	}

	public Point getRealScreenSize() {
	    WindowManager windowManager = (WindowManager) sContext.getSystemService(Context.WINDOW_SERVICE);
	    Display display = windowManager.getDefaultDisplay();
	    Point size = new Point();

	    if (Build.VERSION.SDK_INT >= 17) {
	        display.getRealSize(size);
	    } else if (Build.VERSION.SDK_INT >= 14) {
	        try {
	            size.x = (Integer) Display.class.getMethod("getRawWidth").invoke(display);
	            size.y = (Integer) Display.class.getMethod("getRawHeight").invoke(display);
	        }
	        catch (IllegalAccessException e)
            {
                Log.f("getRealScreenSize Error : "+ e.getMessage());
            }
            catch (InvocationTargetException e)
            {
                Log.f("getRealScreenSize Error : "+ e.getMessage());
            }
            catch (NoSuchMethodException e)
            {
                Log.f("getRealScreenSize Error : "+ e.getMessage());
            }
	    }

	    return size;
	}
	

	public void inquireForDeveloper()
	{
		Intent i;
		i = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", Common.DEVELOPER_EMAIL, null));
		i.putExtra(Intent.EXTRA_SUBJECT, "");

		i.putExtra(Intent.EXTRA_TEXT, "[" + Build.BRAND.toString() + "]" + " Model: " + Build.MODEL + ", OS: " + Build.VERSION.RELEASE + ", Ver: "
				+ getPackageVersionName(Common.PACKAGE_NAME));
		String strTitle = sContext.getResources().getString(R.string.app_name);
		Uri uri = Uri.parse("file://"+ Log.getLogfilePath());
		i.putExtra(Intent.EXTRA_STREAM, uri);
		sContext.startActivity(Intent.createChooser(i, strTitle));
	}
	
	public void finishApplication()
	{
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2)
		{
			//((Activity)sContext).finishAffinity();
			((Activity)sContext).finish();
		}
		else
		{
			((Activity)sContext).finish();
		}
	}
	

	
	public String getDayNumberSuffixToEN(int day) {
	    if (day >= 11 && day <= 13) {
	        return "th";
	    }
	    switch (day % 10) {
	    case 1:
	        return "st";
	    case 2:
	        return "nd";
	    case 3:
	        return "rd";
	    default:
	        return "th";
	    }
	}
	
	public String[] getAvailableSelectYears()
	{
		final int MAX_TERM_YEAR = 100;
		String[] availableYears = new String[MAX_TERM_YEAR];
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		
		int startYear 	= calendar.get(Calendar.YEAR) - (MAX_TERM_YEAR -1);
		int endYear 	= calendar.get(Calendar.YEAR);
		Log.i("startYear : " + startYear+ " , Current Year : "+ calendar.get(Calendar.YEAR));
		
		int count = 0;
		for(int i = startYear  ; i <= endYear; i++)
		{
			availableYears[count] = String.valueOf(i);
			count++;
		}
		
		Log.i("size : " + availableYears.length);
		
		return availableYears;
	}

    /**
     * 배경색의 뷰를 원 모양의 애니메이션으로 나타나게 동작시킨다.
     * @param view 해당 뷰
     * @param color 배경색
     * @param positionX 시작 위치 X
     * @param positionY 시작 위치 Y
     * @param duration 애니메이션 시간
     */
    public void showAnimateReveal(final View view, int color, int positionX, int positionY, int duration)
    {
        showAnimateReveal(view, color, positionX, positionY, false, duration);
    }

    /**
     * 배경색의 뷰를 원 모양의 애니메이션으로 나타나게 동작시킨다.
     * @param view 해당 뷰
     * @param color 배경색
     * @param positionX 시작 위치 X
     * @param positionY 시작 위치 Y
     * @param isAlphaAnimation 알파 애니메이션을 적용할 것인지의 여부
     * @param duration 애니메이션 시간
     */
	public void showAnimateReveal(final View view, int color, int positionX, int positionY, boolean isAlphaAnimation, int duration)
    {
        float finalRadius = (float) Math.hypot(view.getWidth(), view.getHeight());
        AnimatorSet animaterSet = new AnimatorSet();

        Animator revealAnimation = ViewAnimationUtils.createCircularReveal(view, positionX, positionY, 0, finalRadius);
        view.setBackgroundColor(ContextCompat.getColor(sContext, color));

        if(isAlphaAnimation)
        {
            Animator alphaAnimation = ObjectAnimator.ofFloat(view, "alpha", 0, 1);
            animaterSet.playTogether(revealAnimation, alphaAnimation);
        }
        else
        {
            animaterSet.play(revealAnimation);
        }

        animaterSet.setDuration(duration);
        animaterSet.setInterpolator(new LinearInterpolator());
        animaterSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                view.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animator) {

            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        animaterSet.start();

    }

    /**
     * 배경색의 뷰를 원 모양의 애니메이션으로 사라지게 동작시킨다.
     * @param view 해당 뷰
     * @param color 배경색
     * @param positionX 시작 위치 X
     * @param positionY 시작 위치 Y
     * @param duration 애니메이션 시간
     */
    public void hideAnimateReveal(final View view, int color, int positionX, int positionY, int duration)
    {
        hideAnimateReveal(view, color, positionX, positionY, false, duration);
    }


    /**
     * 배경색의 뷰를 원 모양의 애니메이션으로 사라지게 동작시킨다.
     * @param view 해당 뷰
     * @param color 배경색
     * @param positionX 시작 위치 X
     * @param positionY 시작 위치 Y
     * @param isAlphaAnimation 알파 애니메이션을 적용할 것인지의 여부
     * @param duration 애니메이션 시간
     */
    public void hideAnimateReveal(final View view, int color, int positionX, int positionY, boolean isAlphaAnimation, int duration)
    {
        float initialRadius = (float) Math.hypot(view.getWidth(), view.getHeight());
        AnimatorSet animaterSet = new AnimatorSet();

        Animator revealAnimation = ViewAnimationUtils.createCircularReveal(view, positionX, positionY, initialRadius, 0);
        view.setBackgroundColor(ContextCompat.getColor(sContext, color));

        if(isAlphaAnimation)
        {
            Animator alphaAnimation = ObjectAnimator.ofFloat(view, "alpha", 1, 0);
            animaterSet.playTogether(revealAnimation, alphaAnimation);
        }
        else
        {
            animaterSet.play(revealAnimation);
        }

        animaterSet.setDuration(duration);
        animaterSet.setInterpolator(new LinearInterpolator());
        animaterSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                view.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        animaterSet.start();
    }

    public Rect getGlobalVisibleRect(View view)
    {
        Rect rect = new Rect();
        view.getGlobalVisibleRect(rect);

        return rect;
    }

    public Slide getSlideTransition(int gravity, int delay, int duration, int ...excludeTarget)
    {
        int[] excludeTargetList = null;

        Slide slide = new Slide();
        slide.setSlideEdge(gravity);
        slide.setDuration(duration);
		slide.setStartDelay(delay);

        if(excludeTarget != null)
        {
            for(int i = 0; i < excludeTarget.length ; i++)
            {
                slide.excludeTarget(excludeTarget[i], true);
            }
        }

        return slide;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void requestPermission(String[] permissionList, int requestCode)
    {
        ArrayList<String> unAuthorizeList = new ArrayList<String>();

        for(int i = 0 ; i < permissionList.length ; i++)
        {
            if(ContextCompat.checkSelfPermission(sContext,permissionList[i]) != PackageManager.PERMISSION_GRANTED)
            {
                unAuthorizeList.add(permissionList[i]);
            }
        }

        if(unAuthorizeList.size() > 0)
        {
            String[] unAuthorizePermissions = new String[unAuthorizeList.size()];
            unAuthorizePermissions = unAuthorizeList.toArray(unAuthorizePermissions);

            ((AppCompatActivity)sContext).requestPermissions(unAuthorizePermissions, requestCode);
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public ArrayList<String> getUnAuthorizePermissionList(String[] permissionList)
    {
        ArrayList<String> unAuthorizeList = new ArrayList<String>();

        for(int i = 0 ; i < permissionList.length ; i++)
        {
            if(sContext.checkSelfPermission(permissionList[i]) != PackageManager.PERMISSION_GRANTED)
            {
                unAuthorizeList.add(permissionList[i]);
            }
        }
        return unAuthorizeList;
    }

	public File getImageFile(Uri uri)
	{
		String[] projection = { MediaStore.Images.Media.DATA };

		if (uri == null)
		{
			return null;
		}

		Cursor cursor = sContext.getContentResolver().query(uri, projection, null, null,
				MediaStore.Images.Media.DATE_MODIFIED + " desc");

		if(cursor == null || cursor.getCount() < 1)
		{
			return null; // no cursor or no record
		}

		int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		String path = cursor.getString(column_index);

		if (cursor !=null )
		{
			cursor.close();
			cursor = null;
		}

		return new File(path);
	}

	/**
	 * 파일을 생성하여 URI를 전달 받는다.
	 * @param fileName 이미지 파일 생성 이름
	 * @return
	 */
	public Uri createImageFileUri(String fileName)
	{
        Log.f("fileName : "+fileName);
        Uri requestUri = null;
        requestUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), fileName+".jpg"));
		return requestUri;
	}



    public boolean saveJpegFile(Uri uri, String fileName)
	{
        Log.f("fileName : "+fileName);
		File folderPath = null;
		File filePath = null;

		boolean isSuccess = true;

		Bitmap bitmap = null;
		try {
			bitmap = MediaStore.Images.Media.getBitmap(sContext.getContentResolver(), uri);
			folderPath = new File(Common.PATH_IMAGE_ROOT);
			if(folderPath.isDirectory() == false)
			{
				folderPath.mkdirs();
			}
			filePath = new File(Common.PATH_IMAGE_ROOT+fileName);
			if(filePath.exists())
            {
                Log.f("exist file");
                FileUtils.deleteFile(filePath.getPath());
            }

            if(filePath.exists() == false)
            {
                Log.f("not exist file");

            }

			FileOutputStream out = new FileOutputStream(Common.PATH_IMAGE_ROOT+fileName);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
			out.close();
		}catch (Exception e)
		{
			Log.f("Error Message : "+ e.getMessage());
			isSuccess = false;
		}

		return isSuccess;
	}

	public  Bitmap getFontBitmap(String text, int color, float fontSizeSP, int viewWidth, int padding, String fontName, boolean isCenter) {
		float xOriginal = 0.0f;
		int fontSizePX = convertDiptoPix(fontSizeSP);
		int pad = convertDiptoPix(padding);
		Paint paint = new Paint();
		Typeface typeface = Typeface.createFromAsset(sContext.getAssets(), fontName);
		paint.setAntiAlias(true);
		paint.setTypeface(typeface);
		paint.setColor(color);
		paint.setTextSize(fontSizePX);


		int textWidth = (int) (convertDiptoPix(viewWidth));
		if(isCenter)
		{
			xOriginal = (int) ((textWidth - paint.measureText(text))/2);
		}
		else
		{
			xOriginal = pad/2;
		}

		int height = (int) (fontSizePX / 0.75);
		Bitmap bitmap = Bitmap.createBitmap(textWidth, height, Bitmap.Config.ARGB_4444);
		Canvas canvas = new Canvas(bitmap);


		canvas.drawText(text, xOriginal, fontSizePX, paint);
		return bitmap;
	}

	public int convertDiptoPix(float dip) {
		int value = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, sContext.getResources().getDisplayMetrics());
		return value;
	}

    public long getPhotoDateTime(Uri uri)
    {
        final int INDEX_DATE_TIME   = 0;
        String[] projection = {MediaStore.Images.Media.DATE_TAKEN};
        Cursor cursor = ((AppCompatActivity)sContext).getContentResolver().query(uri, projection, null, null, null);
        cursor.moveToFirst();
        if(cursor != null && cursor.getCount() != 0)
        {
            return Long.valueOf(cursor.getString(INDEX_DATE_TIME));
        }
        return System.currentTimeMillis();
    }

    public PhotoInformationObject getPhotoInformation(Uri uri, String keyID, String month, String comment)
    {
        final int INDEX_DATE_TIME   = 0;
        final int INDEX_LATITUDE    = 1;
        final int INDEX_LONGITUDE   = 2;
        PhotoInformationObject result = null;

        String[] projection = {MediaStore.Images.Media.DATE_TAKEN, MediaStore.Images.Media.LATITUDE, MediaStore.Images.Media.LONGITUDE};
        Cursor cursor = ((AppCompatActivity)sContext).getContentResolver().query(uri, projection, null, null, null);
        cursor.moveToFirst();

        if(cursor != null && cursor.getCount() != 0)
        {
            Log.f("cursor size : "+ cursor.getCount());

            long millisecond = Long.valueOf(cursor.getString(INDEX_DATE_TIME));
            Log.i("cursor DATE_TAKEN  : "+CommonUtils.getInstance(sContext).getPayInformationDate(false, millisecond));
            try
            {
                Log.i("cursor LATITUDE : "+cursor.getString(INDEX_LATITUDE));
                Log.i("cursor LONGITUDE : "+cursor.getString(INDEX_LONGITUDE));
            }catch(Exception e)
            {
                Log.i("Error Message : "+ e.getMessage());
            }

            result = new PhotoInformationObject(
                    keyID,
                    month,
                    Long.valueOf(cursor.getString(INDEX_DATE_TIME)),
                    cursor.getString(INDEX_LATITUDE) == null ? 0.0f : Float.valueOf(cursor.getString(INDEX_LATITUDE)),
                    cursor.getString(INDEX_LONGITUDE) == null ? 0.0f :Float.valueOf(cursor.getString(INDEX_LONGITUDE)),
                    comment
            );

        }
        cursor.close();
        return result;
    }

	public void updateWidget()
	{
		Log.i("");
		Intent intent = new Intent(sContext, PhotoFrameWidgetProvider.class);
		intent.setAction(Common.INTENT_WIDGET_UPDATE);
		sContext.sendBroadcast(intent);
	}

}
