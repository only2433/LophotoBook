package com.starbrunch.couple.photo.frame.main.common;

import com.littlefox.logmonitor.Log;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;

import java.io.File;

/**
 * Created by only340 on 2018-01-30.
 */

public class Compressor {

    public static boolean zip(String targetPath, String destinationFilePath)
    {
        return zip(targetPath, destinationFilePath ,"");
    }

    public static boolean zip(String targetPath, String destinationFilePath, String password)
    {
        try {
            ZipParameters parameters = new ZipParameters();
            parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
            parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);

            if (password.equals("") == false)
            {
                parameters.setEncryptFiles(true);
                parameters.setEncryptionMethod(Zip4jConstants.ENC_METHOD_AES);
                parameters.setAesKeyStrength(Zip4jConstants.AES_STRENGTH_256);
                parameters.setPassword(password);
            }

            File file = new File(destinationFilePath);
            File parentFile = new File(file.getParent());

            if(parentFile.exists() == false)
            {
                Log.i("");
                parentFile.mkdirs();
            }


            ZipFile zipFile = new ZipFile(destinationFilePath);

            File targetFile = new File(targetPath);

            if (targetFile.isFile())
            {
                zipFile.addFile(targetFile, parameters);
            }
            else if (targetFile.isDirectory())
            {
                zipFile.addFolder(targetPath, parameters);
            }

        } catch (Exception e)
        {
            Log.f("Exception : "+ e.getMessage());
            return false;
        }
        return true;
    }


    public static boolean unzip(String targetZipFilePath, String destinationFolderPath)
    {
       return unzip(targetZipFilePath, destinationFolderPath, "");
    }

    public static boolean unzip(String targetZipFilePath, String destinationFolderPath, String password)
    {
        Log.i("targetZipFilePath : "+targetZipFilePath);
        Log.i("destinationFolderPath : "+destinationFolderPath);
        Log.i("password : "+password);
        try {
            ZipFile zipFile = new ZipFile(targetZipFilePath);

            if(password.equals("") == false)
            {
                if (zipFile.isEncrypted())
                {
                    zipFile.setPassword(password);
                }
            }

            zipFile.extractAll(destinationFolderPath);

        } catch (Exception e)
        {
            Log.f("Exception : "+ e.getMessage());
            return false;
        }

        return true;
    }
}