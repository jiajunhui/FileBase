/*
 * Copyright 2016 jiajunhui
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.kk.taurus.filebase.engine;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Taurus on 2016/12/22.
 */

public class AssetsEngine {

    private static final String TAG = "AssetsEngine";

    /**
     * get assets file in the assets dir root.
     * @param context
     * @param fileName
     * @return
     */
    public static InputStream getAssets(Context context, String fileName){
        try {
            return context.getAssets().open(fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * list assets files or dirs Under the specified path
     * @param context
     * @param dirName
     * @return
     */
    public static String[] listAssets(Context context, String dirName){
        try {
            return context.getAssets().list(dirName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getAssetsStringContent(Context context,String[] dirLevel, String fileName){
        return getAssetsStringContent(context, getAssetsFilePath(dirLevel, fileName));
    }

    /**
     * get string content from assets files
     * @param context
     * @param fileName
     * @return
     */
    public static String getAssetsStringContent(Context context, String fileName){
        InputStream inputStream = null;
        ByteArrayOutputStream outputStream = null;
        try {
            inputStream = context.getAssets().open(fileName);
            outputStream = new ByteArrayOutputStream();
            if(inputStream!=null){
                byte[] bytes = new byte[1024];
                int len;
                while((len = inputStream.read(bytes))!=-1){
                    outputStream.write(bytes,0,len);
                }
                outputStream.flush();
                return new String(outputStream.toByteArray(),"UTF-8");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if(inputStream!=null){
                    inputStream.close();
                }
                if(outputStream!=null){
                    outputStream.close();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * get assets file in the assets some dirs.
     * @param context
     * @param dirLevel
     * @param fileName
     * @return
     */
    public static InputStream getAssets(Context context,String[] dirLevel, String fileName){
        return getAssets(context,getAssetsFilePath(dirLevel, fileName));
    }

    /**
     * copy assets file int root dir to destination.
     * @param context
     * @param fileName
     * @param toDir
     * @return
     */
    public static boolean copyAssetsToDir(Context context, String fileName, File toDir) {
        InputStream is = null;
        OutputStream os = null;
        try {
            is = getAssets(context, fileName);
            if(is==null)
                return false;
            if (!toDir.exists()) {
                toDir.mkdirs();
            }
            fileName = FileEngine.getFileNameByPath(fileName);
            if(TextUtils.isEmpty(fileName))
                fileName = "temp";
            os = new FileOutputStream(new File(toDir,fileName));
            int byteCount;
            byte[] bytes = new byte[1024];
            while ((byteCount = is.read(bytes)) != -1) {
                os.write(bytes, 0, byteCount);
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if(os!=null){
                    os.close();
                }
                if(is!=null){
                    is.close();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * copy assets file int assets some dirs to destination.
     * @param context
     * @param dirLevel
     * @param fileName
     * @param toDir
     * @return
     */
    public static boolean copyAssetsToDir(Context context, String[] dirLevel, String fileName, File toDir) {
        return copyAssetsToDir(context, getAssetsFilePath(dirLevel, fileName), toDir);
    }

    /**
     * unzip assets zip package
     * @param context
     * @param fileName
     * @param destinationDir
     * @param rewrite
     * @return
     */
    public static boolean unZipAssets(Context context,String fileName,File destinationDir, boolean rewrite){
        InputStream inputStream = AssetsEngine.getAssets(context, fileName);
        if(inputStream!=null){
            try {
                ZipEngine.unZip(inputStream,destinationDir,rewrite);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * unzip zip package in assets dirs.
     * @param context
     * @param dirLevel
     * @param fileName
     * @param destinationDir
     * @param rewrite
     * @return
     */
    public static boolean unZipAssets(Context context,String[] dirLevel, String fileName,File destinationDir, boolean rewrite){
        return unZipAssets(context, getAssetsFilePath(dirLevel, fileName), destinationDir, rewrite);
    }

    private static String getAssetsFilePath(String[] dirLevel, String fileName){
        String dirPath = getPathByDirLevel(dirLevel);
        String filePath = TextUtils.isEmpty(dirPath)?fileName:(dirPath + fileName);
        Log.d(TAG,"dirPath : " + dirPath + " filePath : " + filePath);
        return filePath;
    }

    private static String getPathByDirLevel(String[] dirLevel){
        if(dirLevel==null)
            return "";
        if(dirLevel.length<=0)
            return "";
        StringBuilder sb = new StringBuilder();
        for(String dir : dirLevel){
            sb.append(dir + File.separator);
        }
        return sb.toString();
    }

}
