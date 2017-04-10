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

import com.kk.taurus.filebase.tools.MD5Utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by Taurus on 2017/4/10.
 */

public class FileStore {

    public static boolean save(File dir, String key, String data){
        String fileName = MD5Utils.md5(key);
        return writeToFile(dir, fileName, data);
    }

    public static String get(File dir,String key){
        String fileName = MD5Utils.md5(key);
        File file = new File(dir,fileName);
        if(!file.exists())
            return null;
        return readFile(dir,fileName);
    }

    public static boolean save(Context context, String key, String data){
        String fileName = MD5Utils.md5(key);
        return writeToFile(context.getCacheDir(), fileName, data);
    }

    public static String get(Context context,String key){
        String fileName = MD5Utils.md5(key);
        File file = new File(context.getCacheDir(),fileName);
        if(!file.exists())
            return null;
        return readFile(context.getCacheDir(),fileName);
    }

    private static String readFile(File dir,String fileName){
        FileReader fileReader = null;
        BufferedReader bufferedReader = null;
        try {
            StringBuffer sb = new StringBuffer();
            fileReader = new FileReader(new File(dir,fileName));
            bufferedReader = new BufferedReader(fileReader);
            String read;
            while ((read = bufferedReader.readLine())!=null){
                sb.append(read);
            }
            return sb.toString();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(fileReader!=null){
                try {
                    fileReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(bufferedReader!=null){
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    private static boolean writeToFile(File dir, String fileName, String data){
        FileWriter fileWriter = null;
        BufferedWriter bufferedWriter = null;
        try {
            if(!dir.exists())
                dir.mkdirs();
            fileWriter = new FileWriter(new File(dir,fileName));
            bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(data,0,data.length());
            bufferedWriter.flush();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(fileWriter!=null){
                try {
                    fileWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(bufferedWriter!=null){
                try {
                    bufferedWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }
}
