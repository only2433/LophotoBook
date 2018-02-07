package com.starbrunch.couple.photo.frame.main.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.littlefox.logmonitor.Log;
import com.starbrunch.couple.photo.frame.main.object.PhotoInformationObject;

import java.util.ArrayList;

/**
 * 사진 정보에 관련된 DataBase
 * Created by 정재현 on 2017-12-27.
 */

public class PhotoInformationDBHelper extends SQLiteOpenHelper
{
    private static PhotoInformationDBHelper sPlayedContentDBHelper = null;
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "photo_information_base";

    public static final String KEY_ID                  = "key_id";
    public static final String KEY_MONTH               = "key_month";
    public static final String KEY_FILE_NAME           = "key_file_name";
    public static final String KEY_DATE_MILLISECOND    = "key_date_millisecond";
    public static final String KEY_LATITUDE            = "key_latitude";
    public static final String KEY_LONGITUDE           = "key_longitude";
    public static final String KEY_COMMENTS            = "key_comments";

    private static final int INDEX_KEY_ID               = 0;
    private static final int INDEX_MONTH                = 1;
    private static final int INDEX_FILE_NAME            = 2;
    private static final int INDEX_DATE_MILLISECOND     = 3;
    private static final int INDEX_LATITUDE             = 4;
    private static final int INDEX_LONGITUDE            = 5;
    private static final int INDEX_COMMENTS             = 6;

    public static PhotoInformationDBHelper getInstance(Context context)
    {
        if(sPlayedContentDBHelper == null)
        {
            sPlayedContentDBHelper = new PhotoInformationDBHelper(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        return sPlayedContentDBHelper;
    }

    public PhotoInformationDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version)
    {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase)
    {
        String CREATE_DATABASE_TABLE = "CREATE TABLE "+ DATABASE_NAME +
                "("
                    + KEY_ID +" TEXT PRIMARY KEY, "
                    + KEY_MONTH + " TEXT,"
                    + KEY_FILE_NAME + " TEXT,"
                    + KEY_DATE_MILLISECOND + " TEXT,"
                    + KEY_LATITUDE + " TEXT,"
                    + KEY_LONGITUDE + " TEXT,"
                    + KEY_COMMENTS + " TEXT"+")";

        sqLiteDatabase.execSQL(CREATE_DATABASE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1)
    {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+ DATABASE_NAME);

        onCreate(sqLiteDatabase);
    }

    public void release()
    {
        if(sPlayedContentDBHelper != null)
        {
            sPlayedContentDBHelper.close();
        }
    }

    public void addPhotoInformationObject(PhotoInformationObject object)
    {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_ID,  object.getKeyID());
        values.put(KEY_MONTH, object.getMonth());
        values.put(KEY_FILE_NAME,  object.getFileName());
        values.put(KEY_DATE_MILLISECOND,  object.getDateTime());
        values.put(KEY_LATITUDE,  object.getLatitude());
        values.put(KEY_LONGITUDE,  object.getLongitude());
        values.put(KEY_COMMENTS,  object.getCommants());

        database.insert(DATABASE_NAME, null,values);
    }

    public PhotoInformationObject getPhotoInformationObject(String keyID)
    {
        SQLiteDatabase database = this.getReadableDatabase();
        PhotoInformationObject object = null;
        Cursor cursor;

        cursor = database.query(DATABASE_NAME,
                new String[] {
                        KEY_ID,
                        KEY_MONTH,
                        KEY_FILE_NAME,
                        KEY_DATE_MILLISECOND,
                        KEY_LATITUDE,
                        KEY_LONGITUDE,
                        KEY_COMMENTS}, KEY_ID+"=?", new String[]{keyID}, null, null, null, null);
        if(cursor == null || cursor.getCount() == 0)
        {
            Log.i("Value Not Have");
            return object;
        }

        if(cursor.moveToFirst())
        {
            object = new PhotoInformationObject(
                    cursor.getString(INDEX_KEY_ID),
                    cursor.getString(INDEX_MONTH),
                    Long.valueOf(cursor.getString(INDEX_DATE_MILLISECOND)),
                    Float.valueOf(cursor.getString(INDEX_LATITUDE)),
                    Float.valueOf(cursor.getString(INDEX_LONGITUDE)),
                    cursor.getString(INDEX_COMMENTS));
        }


        cursor.close();

        return object;
    }

    /**
     * KEYID에 맞는 DB정보 중 해당 KEY의 Value를 수정하여 업데이트 한다.
     * @param keyID 수정될 DB 아이템의 KEYID
     * @param key 수정될 정보의 KEY
     * @param value 수정될 정보 value
     */
    public void updatePhotoInformationObject(String keyID, String key, String value)
    {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(key, value);

        database.update(DATABASE_NAME, values, KEY_ID + " =? ", new String[]{keyID});
    }

    /**
     * DB에 있는 정보중 해당 KEY ID 에 맞는 정보만 삭제.
     * @param keyID
     */
    public void deletePhotoInformationObject(String keyID)
    {
        SQLiteDatabase database = this.getWritableDatabase();
        database.delete(DATABASE_NAME,  KEY_ID + " =? ", new String[]{keyID});
    }

    /**
     * DB에 있는 모든 정보를 삭제 한다.
     */
    public void deletePhotoInformationAll()
    {
        SQLiteDatabase database = this.getWritableDatabase();
        database.delete(DATABASE_NAME,  null, null);
    }

    /**
     * 해당 월에 저장된 사진 정보 리스트를 DB에서 가져온다.
     * @param month 해당 월
     * @return 해당 월에 저장된 파일 정보를 리스트로 전달
     */
    public ArrayList<PhotoInformationObject> getPhotoInformationListByMonth(String month)
    {
        ArrayList<PhotoInformationObject> result = new ArrayList<PhotoInformationObject>();

        SQLiteDatabase database = this.getReadableDatabase();
        PhotoInformationObject object = null;
        Cursor cursor;

        cursor = database.query(DATABASE_NAME,
                new String[] {
                        KEY_ID,
                        KEY_MONTH,
                        KEY_FILE_NAME,
                        KEY_DATE_MILLISECOND,
                        KEY_LATITUDE,
                        KEY_LONGITUDE,
                        KEY_COMMENTS}, KEY_MONTH+"=?", new String[]{month}, null, null, null, null);

        if(cursor == null || cursor.getCount() == 0)
        {
            Log.f("VALUE NOT HAVE");
            return result;
        }

        if(cursor.moveToFirst())
        {
            do
            {
                result.add(new PhotoInformationObject(
                        cursor.getString(INDEX_KEY_ID),
                        cursor.getString(INDEX_MONTH),
                        Long.valueOf(cursor.getString(INDEX_DATE_MILLISECOND)),
                        Float.valueOf(cursor.getString(INDEX_LATITUDE)),
                        Float.valueOf(cursor.getString(INDEX_LONGITUDE)),
                        cursor.getString(INDEX_COMMENTS)));

            }while (cursor.moveToNext());
        }
        cursor.close();
        return result;
    }

    /**
     * DB에 저정된 파일 정보를 모두 가져온다.
     * @return
     */
    public ArrayList<PhotoInformationObject> getPhotoInformationList()
    {
        ArrayList<PhotoInformationObject> result = new ArrayList<PhotoInformationObject>();

        SQLiteDatabase database = this.getReadableDatabase();
        PhotoInformationObject object = null;
        Cursor cursor;

        cursor = database.query(DATABASE_NAME,
                new String[] {
                        KEY_ID,
                        KEY_MONTH,
                        KEY_FILE_NAME,
                        KEY_DATE_MILLISECOND,
                        KEY_LATITUDE,
                        KEY_LONGITUDE,
                        KEY_COMMENTS}, null, null, null, null, null, null);

        if(cursor == null || cursor.getCount() == 0)
        {
            Log.f("VALUE NOT HAVE");
            return result;
        }

        if(cursor.moveToFirst())
        {
            do
            {
                result.add(new PhotoInformationObject(
                        cursor.getString(INDEX_KEY_ID),
                        cursor.getString(INDEX_MONTH),
                        Long.valueOf(cursor.getString(INDEX_DATE_MILLISECOND)),
                        Float.valueOf(cursor.getString(INDEX_LATITUDE)),
                        Float.valueOf(cursor.getString(INDEX_LONGITUDE)),
                        cursor.getString(INDEX_COMMENTS)));

            }while (cursor.moveToNext());
        }
        cursor.close();
        return result;
    }
}
