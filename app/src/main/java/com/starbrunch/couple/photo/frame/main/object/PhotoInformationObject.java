package com.starbrunch.couple.photo.frame.main.object;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 포토 정보에 관련된 객체
 * Created by 정재현 on 2017-12-27.
 */

public class PhotoInformationObject implements Parcelable
{
    private String  keyID       = "";
    private String  month       = "";
    private String  fileName    = "";
    private long    dateTime    = 0L;
    private float   latitude    = 0.0f;
    private float   longitude   = 0.0f;
    private String  commants    = "";

    public PhotoInformationObject(String keyID, String month,long dateTime)
    {
        new PhotoInformationObject(keyID,month, dateTime, 0.0f, 0.0f , "");
    }

    public PhotoInformationObject(String keyID, String month, long dateTime, float latitude, float longitude)
    {
        new PhotoInformationObject(keyID, month, dateTime, latitude , longitude, "");
    }

    public PhotoInformationObject(String keyID, String month, long dateTime, float latitude, float longitude, String commants)
    {
        this.keyID      = keyID;
        this.month      = month;
        this.fileName   = keyID+".jpg";
        this.dateTime   = dateTime;
        this.latitude   = latitude;
        this.longitude  = longitude;
        this.commants   = commants;
    }

    public String getKeyID()
    {
        return keyID;
    }

    public String getFileName()
    {
        return fileName;
    }

    public long getDateTime()
    {
        return dateTime;
    }

    public float getLatitude()
    {
        return latitude;
    }

    public float getLongitude()
    {
        return longitude;
    }

    public String getCommants()
    {
        return commants;
    }

    public String getMonth()
    {
        return month;
    }

    @Override
    public int describeContents()
    {
        return 0;
    }


    protected PhotoInformationObject(Parcel in)
    {
        keyID = in.readString();
        month = in.readString();
        fileName = in.readString();
        dateTime = in.readLong();
        latitude = in.readFloat();
        longitude = in.readFloat();
        commants = in.readString();
    }

    @Override
    public void writeToParcel(Parcel parcel, int i)
    {
        parcel.writeString(keyID);
        parcel.writeString(month);
        parcel.writeString(fileName);
        parcel.writeLong(dateTime);
        parcel.writeFloat(latitude);
        parcel.writeFloat(longitude);
        parcel.writeString(commants);
    }

    public static final Creator<PhotoInformationObject> CREATOR = new Creator<PhotoInformationObject>() {
        @Override
        public PhotoInformationObject createFromParcel(Parcel in) {
            return new PhotoInformationObject(in);
        }

        @Override
        public PhotoInformationObject[] newArray(int size) {
            return new PhotoInformationObject[size];
        }
    };


}
