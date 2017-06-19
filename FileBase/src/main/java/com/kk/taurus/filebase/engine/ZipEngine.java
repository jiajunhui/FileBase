package com.kk.taurus.filebase.engine;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

/**
 * Created by Taurus on 2017/6/6.
 */

public class ZipEngine {

    public static void unZip(File zipSource, File destination, boolean isReWrite) throws IOException {
        ZipFile zipFile = new ZipFile(zipSource);
        Enumeration<? extends ZipEntry> entries = zipFile.entries();
        byte[] bytes = new byte[1024];
        while (entries.hasMoreElements()){
            ZipEntry zipEntry = entries.nextElement();
            File file = new File(destination + File.separator + zipEntry.getName()) ;
            if(zipEntry.isDirectory()){
                if(isReWrite || !file.exists()){
                    file.mkdirs();
                }
            }else{
                File parent = file.getParentFile();
                if(!parent.exists() || isReWrite){
                    parent.mkdirs();
                }
                InputStream input = zipFile.getInputStream(zipEntry);
                OutputStream output = new FileOutputStream(file);
                int len;
                while((len=input.read(bytes))!=-1){
                    output.write(bytes,0,len);
                }
                output.flush();
                input.close();
                output.close();
            }
        }
    }

    public static void unZip(InputStream inputStream, File destination, boolean isReWrite) throws IOException {
        // if not exists , create it.
        if (!destination.exists()) {
            destination.mkdirs();
        }
        String destinationPath = destination.getAbsolutePath();
        ZipInputStream zipInputStream = new ZipInputStream(inputStream);
        // read a zip entry
        ZipEntry zipEntry = zipInputStream.getNextEntry();
        byte[] buffer = new byte[1024 * 1024];
        int count;
        while (zipEntry != null) {
            if (zipEntry.isDirectory()) {
                destination = new File(destinationPath + File.separator + zipEntry.getName());
                if (isReWrite || !destination.exists()) {
                    destination.mkdir();
                }
            } else {
                destination = new File(destinationPath + File.separator + zipEntry.getName());
                if (isReWrite || !destination.exists()) {
                    destination.createNewFile();
                    FileOutputStream fileOutputStream = new FileOutputStream(destination);
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

}
