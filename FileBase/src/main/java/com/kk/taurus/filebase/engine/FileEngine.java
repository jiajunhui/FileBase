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
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;
import com.kk.taurus.filebase.filefilter.NullFilter;
import com.kk.taurus.filebase.tools.CopyTool;
import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Stack;

/**
 * Created by Taurus on 2016/12/22.
 */

public class FileEngine {

    private static final String TAG = "FileEngine";

    /**
     * list files by file filter and comparators
     * @param dir
     * @param fileFilter
     * @param comparators
     * @return
     */
    public static List<File> listFiles(File dir, FileFilter fileFilter, Comparator[] comparators){
        List<File> files = new ArrayList<>();
        if(fileFilter==null){
            fileFilter = new NullFilter();
        }
        if(dir!=null && dir.isDirectory()){
            File[] fileArray = dir.listFiles(fileFilter);
            if(fileArray!=null){
                files = new ArrayList<>(Arrays.asList(fileArray));
            }
        }
        Log.d(TAG,"files num : " + files.size());
        sortByComparators(files,comparators);
        return files;
    }

    /**
     * sort file list by comparators
     * @param files
     * @param comparators
     */
    public static void sortByComparators(List<File> files,Comparator[] comparators){
        if(files==null || comparators==null)
            return;
        if(comparators.length<1)
            return;
        if(files.size()<=1)
            return;
        for(Comparator comparator : comparators){
            Collections.sort(files,comparator);
        }
    }

    /**
     * delete file or directory.
     * @param file
     * @return delete failure number of files or directory.
     */
    public static int deleteFile(File file){
        if(file==null)
            return 0;
        if(!file.isDirectory()){
            boolean result = file.delete();
            return result?0:1;
        }
        return deleteDir(file);
    }

    public interface OnDeleteListener{
        void onDeleteProgress(int progress, int max);
        void onDeleteFinish(int failNumber);
    }

    public static int deleteFiles(List<File> targets, OnDeleteListener onDeleteListener){
        Stack<File> tempStack = new Stack<>();
        Stack<File> taskStack = new Stack<>();
        for(File f : targets){
            if(f.isDirectory()){
                tempStack.push(f);
            }
            taskStack.push(f);
        }
        while (!tempStack.isEmpty()){
            File dir = tempStack.pop();
            File[] files = dir.listFiles();
            for(File file : files){
                if(file.isDirectory()){
                    tempStack.push(file);
                }
                taskStack.push(file);
            }
        }
        int max = taskStack.size();
        int failNumber = 0;
        while (!taskStack.isEmpty()){
            File deleteTarget = taskStack.pop();
            failNumber += (deleteTarget.delete()?0:1);
            if(onDeleteListener!=null){
                int progress = max - taskStack.size();
                onDeleteListener.onDeleteProgress(progress,max);
            }
        }
        if(onDeleteListener!=null){
            onDeleteListener.onDeleteFinish(failNumber);
        }
        return failNumber;
    }

    private static int deleteDir(File root){
        Stack<File> tempStack = new Stack<>();
        Stack<File> taskStack = new Stack<>();
        tempStack.push(root);
        taskStack.push(root);
        while (!tempStack.isEmpty()){
            File dir = tempStack.pop();
            File[] files = dir.listFiles();
            for(File file : files){
                if(file.isDirectory()){
                    tempStack.push(file);
                }
                taskStack.push(file);
            }
        }
        int failNumber = 0;
        while (!taskStack.isEmpty()){
            File deleteTarget = taskStack.pop();
            failNumber += (deleteTarget.delete()?0:1);
        }
        return failNumber;
    }

    /**
     * copy directory
     * @param source
     * @param destination
     * @return
     */
    public static boolean copy(File source, File destination){
        try {
            CopyTool.copy(source, destination);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * get file name by file absolute path.
     * @param path
     * @return
     */
    public static String getFileNameByPath(String path){
        if(TextUtils.isEmpty(path))
            return null;
        int index = path.lastIndexOf("/");
        if(index!=-1){
            return path.substring(index,path.length());
        }
        return path;
    }

    /**
     * bitmap stream to file
     * @param bitmap
     * @param dir
     * @param fileName
     * @return
     */
    public static String bitmapToFile(Bitmap bitmap, File dir, String fileName){
        File f = new File(dir,fileName);
        if (f.exists())
            f.delete();
        FileOutputStream fOut;
        try {
            fOut = new FileOutputStream(f);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
            fOut.flush();
            fOut.close();
        } catch (Exception e) {
            return null;
        }
        return f.getAbsolutePath();
    }

    /**
     * you can set bitmap compress format to save file.
     * @param bitmap
     * @param dir
     * @param fileName
     * @param format
     * @return
     */
    public static String bitmapToFile(Bitmap bitmap, File dir, String fileName, Bitmap.CompressFormat format){
        File f = new File(dir,fileName);
        if (f.exists())
            f.delete();
        FileOutputStream fOut;
        try {
            fOut = new FileOutputStream(f);
            bitmap.compress(format, 100, fOut);
            fOut.flush();
            fOut.close();
        } catch (Exception e) {
            return null;
        }
        return f.getAbsolutePath();
    }

    /**
     * from apk file path get apk icon.
     * @param context
     * @param apkPath
     * @return
     */
    public static Drawable getApkDrawable(Context context, String apkPath) {
        PackageManager pm = context.getPackageManager();
        PackageInfo info = pm.getPackageArchiveInfo(apkPath,
                PackageManager.GET_ACTIVITIES);
        if (info != null) {
            ApplicationInfo appInfo = info.applicationInfo;
            appInfo.sourceDir = apkPath;
            appInfo.publicSourceDir = apkPath;
            try {
                return appInfo.loadIcon(pm);
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * from apk file get apk icon bitmap.
     * @param context
     * @param apkPath
     * @return
     */
    public static Bitmap getApkBitmap(Context context, String apkPath) {
        Drawable drawable = getApkDrawable(context, apkPath);
        if(drawable!=null){
            return drawableToBitmap(drawable);
        }
        return null;
    }

    /**
     * drawable to bitmap.
     * @param drawable
     * @return
     */
    public static Bitmap drawableToBitmap(Drawable drawable){
        Bitmap bitmap = Bitmap.createBitmap(
                drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(),
                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                        : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        //canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    /**
     * bitmap to drawable.
     * Create drawable from a bitmap, setting initial target density based on
     * the display metrics of the resources.
     * @param resources
     * @param bitmap
     * @return
     */
    public static Drawable bitmapToDrawable(Resources resources, Bitmap bitmap){
        return new BitmapDrawable(resources, bitmap);
    }

    /**
     * from file name get file extension name.
     * @param filename
     * @return
     */
    public static String getExtFromFilename(String filename) {
        int dotPosition = filename.lastIndexOf('.');
        if (dotPosition != -1) {
            return filename.substring(dotPosition + 1, filename.length());
        }
        return "";
    }

    /**
     * get file name (not include extension name).
     * @param filename
     * @return
     */
    public static String getNameFromFilename(String filename) {
        int dotPosition = filename.lastIndexOf('.');
        if (dotPosition != -1) {
            return filename.substring(0, dotPosition);
        }
        return "";
    }

    /**
     * get file name from file path.
     * @param filepath
     * @return
     */
    public static String getNameFromFilepath(String filepath) {
        int pos = filepath.lastIndexOf('/');
        if (pos != -1) {
            return filepath.substring(pos + 1);
        }
        return "";
    }

}
