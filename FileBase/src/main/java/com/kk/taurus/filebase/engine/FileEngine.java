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

import android.text.TextUtils;
import android.util.Log;
import com.kk.taurus.filebase.filefilter.NullFilter;
import com.kk.taurus.filebase.tools.CopyTool;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

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
     */
    public static void deleteFile(File file){
        if(file==null)
            return;
        if(!file.isDirectory()){
            file.delete();
            return;
        }
        deleteDirs(file);
        file.delete();
    }

    private static void deleteDirs(File file){
        File[] files = file.listFiles();
        if(files==null)
            return;
        for(File f : files){
            if(f.isDirectory()){
                deleteDirs(f);
            }
            f.delete();
        }
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
     * unzip zip package
     * @param sourceFile
     * @param destinationDir
     * @param rewrite
     * @return
     */
    public static boolean unZip(File sourceFile,File destinationDir, boolean rewrite){
        try {
            InputStream inputStream = new FileInputStream(sourceFile);
            unZip(inputStream,destinationDir.getAbsolutePath(),rewrite);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * unzip zip package
     * @param outputDirectory
     * @param isReWrite
     * @throws IOException
     */
    public static void unZip(InputStream inputStream, String outputDirectory, boolean isReWrite) throws IOException {
        // create destination root directory.
        File file = new File(outputDirectory);
        // if not exists , create it.
        if (!file.exists()) {
            file.mkdirs();
        }
        ZipInputStream zipInputStream = new ZipInputStream(inputStream);
        // read a zip entry
        ZipEntry zipEntry = zipInputStream.getNextEntry();
        byte[] buffer = new byte[1024 * 1024];
        int count = 0;
        while (zipEntry != null) {
            if (zipEntry.isDirectory()) {
                file = new File(outputDirectory + File.separator + zipEntry.getName());
                if (isReWrite || !file.exists()) {
                    file.mkdir();
                }
            } else {
                file = new File(outputDirectory + File.separator + zipEntry.getName());
                if (isReWrite || !file.exists()) {
                    file.createNewFile();
                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                    while ((count = zipInputStream.read(buffer)) > 0) {
                        fileOutputStream.write(buffer, 0, count);
                    }
                    fileOutputStream.close();
                }
            }
            zipEntry = zipInputStream.getNextEntry();
        }
        zipInputStream.close();
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

}
