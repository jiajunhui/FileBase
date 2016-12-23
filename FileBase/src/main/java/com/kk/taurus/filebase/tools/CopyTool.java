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

package com.kk.taurus.filebase.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Taurus on 2016/3/30.
 */
public class CopyTool {

    public static void copy(File sourceDir, File destDir)throws Exception {
        if(!sourceDir.isDirectory()){
            copyFile(sourceDir,destDir);
            return;
        }
        File sDir = sourceDir;
        File dDir = destDir;
        String name = sDir.getName();
        File newDir = new File(dDir,name);
        newDir.mkdir();//
        listAll(sDir,sDir,newDir);
    }

    public static void copyFile(File sourceFile, File destDir) throws Exception {
        InputStream fis = new FileInputStream(sourceFile);
        OutputStream fos = new FileOutputStream(new File(destDir,sourceFile.getName()));
        byte[] buf = new byte[1024*2];
        int num;
        while((num=fis.read(buf))!=-1){
            fos.write(buf,0,num);
            fos.flush();
        }
        fos.close();
        fis.close();
    }

    private static void listAll(File dir,File sDir, File newDir)throws IOException {
        File[] files = dir.listFiles();
        for(int x=0; x<files.length; x++){
            if(files[x].isDirectory()){
                createDir(files[x],sDir,newDir);
                listAll(files[x],sDir,newDir);
            }else{
                createFile(files[x],sDir,newDir);
            }
        }
    }

    private static void createDir(File dir,File sDir,File newDir){
        File d = replaceFile(dir,sDir,newDir);
        d.mkdir();
    }

    private static void createFile(File file,File sDir,File newDir)throws IOException {
        File newFile = replaceFile(file,sDir,newDir);
        FileInputStream fis = new FileInputStream(file);
        FileOutputStream fos = new FileOutputStream(newFile);
        byte[] buf = new byte[1024*2];
        int num;
        while((num=fis.read(buf))!=-1){
            fos.write(buf,0,num);
        }
        fos.close();
        fis.close();
    }

    private static File replaceFile(File f,File sDir,File newDir){
        String path = f.getAbsolutePath();
        String newPath = path.replace(sDir.getAbsolutePath(),newDir.getAbsolutePath());
        File newFile = new File(newPath);
        return newFile;
    }
}
