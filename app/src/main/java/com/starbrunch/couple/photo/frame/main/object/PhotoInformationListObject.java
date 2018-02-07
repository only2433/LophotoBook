package com.starbrunch.couple.photo.frame.main.object;

import java.util.ArrayList;

/**
 * Created by only340 on 2018-02-07.
 */

public class PhotoInformationListObject
{
    private ArrayList<PhotoInformationObject> list = new ArrayList<PhotoInformationObject>();

    public PhotoInformationListObject()
    {
        list = new ArrayList<PhotoInformationObject>();
    }

    public PhotoInformationListObject(ArrayList<PhotoInformationObject> list)
    {
        this.list = list;
    }

    public void setPhotoInformationListObjectList(ArrayList<PhotoInformationObject> list)
    {
        this.list = list;
    }

    public ArrayList<PhotoInformationObject> getPhotoInformationListObjectList()
    {
        return list;
    }
}
