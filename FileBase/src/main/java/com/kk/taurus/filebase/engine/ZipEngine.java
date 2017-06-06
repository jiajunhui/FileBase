package com.kk.taurus.filebase.engine;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * Created by Taurus on 2017/6/6.
 */

public class ZipEngine {

    public static void packageZip(List<File> files, File destinationDir, String zipName) throws FileNotFoundException {
        ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(new File(destinationDir,zipName)));
        List<ZipItem> zipItems = new ArrayList<>();
        if(!destinationDir.exists()){
            destinationDir.mkdirs();
        }
        for(File file : files){
            if(file.isFile()){
                zipItems.add(new ZipItem(new ZipEntry(file.getName()),file));
            }else{

            }
        }
    }

    private static List<PrepareZipItem> patchFiles(List<PrepareZipItem> container, PrepareZipItem prepareZipItem){

        return null;
    }

    private static class PrepareZipItem{
        public String path;
        public File file;
    }

    private static class ZipItem{
        public ZipEntry zipEntry;
        public File file;

        public ZipItem(ZipEntry zipEntry, File file) {
            this.zipEntry = zipEntry;
            this.file = file;
        }
    }

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
