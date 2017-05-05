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
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.os.UserHandle;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.text.TextUtils;

import com.kk.taurus.filebase.entity.Storage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Taurus on 2016/12/22.
 */

public class StorageEngine {

    private static SDCardInfo SDCARD_INTERNAL = null;
    private static SDCardInfo SDCARD_EXTERNAL = null;

    public static void printMethod(Context context){
        try {
            StorageManager storageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
            Method[] methods = StorageManager.class.getMethods();
            for(Method method: methods){
                System.out.println("storage_method : methodName = " + method.getName()
                        + " methodParams = " + getParams(method.getParameterTypes())
                        + " returnType = " + method.getReturnType().getSimpleName());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static String getParams(Class<?>[] cls){
        StringBuilder sb = new StringBuilder();
        for(Class clz : cls){
            sb.append(clz.getSimpleName()).append(",");
        }
        return sb.toString();
    }

    public static void getStorageVolumes(Context context){
        try {
            StorageManager storageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
            Method method = StorageManager.class.getMethod("getVolumeList");
            StorageVolume[] storageVolumes = (StorageVolume[]) method.invoke(storageManager);
            System.out.println("storageVolumes : " + storageVolumes.length);
            Method[] methods = storageVolumes[0].getClass().getMethods();
            for(Method m : methods){
                System.out.println("storageVolumes : methodName = " + m.getName() + " methodParams = " + getParams(m.getParameterTypes()) + " returnType = " + m.getReturnType().getSimpleName());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static Storage getStorageByPath(Context context, String path){
        Storage storage = new Storage();
        storage.setPath(path);
        StorageManager storageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
        long[] sizes = getStorageInfo(path);
        storage.setTotalSize(sizes[0]);
        storage.setAvailableSize(sizes[1]);
        try {
            Method lowBytesMethod = StorageManager.class.getMethod("getStorageLowBytes",File.class);
            Method fullBytesMethod = StorageManager.class.getMethod("getStorageFullBytes",File.class);

            File file = new File(path);
            long lowBytes = (long) lowBytesMethod.invoke(storageManager,file);
            storage.setLowBytesLimit(lowBytes);
            long fullBytes = (long) fullBytesMethod.invoke(storageManager,file);
            storage.setFullBytesLimit(fullBytes);

            Method method = StorageManager.class.getMethod("getVolumeList");
            StorageVolume[] storageVolumes = (StorageVolume[]) method.invoke(storageManager);
            System.out.println("storageVolumes : " + storageVolumes.length);
            Method getDescriptionMethod = storageVolumes[3].getClass().getMethod("getDescription",Context.class);
            Method allowMassStorageMethod = storageVolumes[3].getClass().getMethod("allowMassStorage");
            Method isRemovableMethod = storageVolumes[3].getClass().getMethod("isRemovable");
            getDescriptionMethod.setAccessible(true);
            allowMassStorageMethod.setAccessible(true);
            isRemovableMethod.setAccessible(true);
            String description = (String) getDescriptionMethod.invoke(storageVolumes[3],context);
            boolean allowMassStorage = (boolean) allowMassStorageMethod.invoke(storageVolumes[0]);
            boolean isRemovable = (boolean) isRemovableMethod.invoke(storageVolumes[0]);
            if(allowMassStorage && isRemovable){
                storage.setUsbMassStorage(true);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return  storage;
    }

    public static List<Storage> getStorages(Context context){
        List<Storage> storageList = new ArrayList<>();
        List<String> storagePaths = getStorageInfo(context);
        for(String path : storagePaths){
            storageList.add(getStorageByPath(context,path));
        }
        return storageList;
    }

    public static List<String> getStorageInfo(Context context){
        StorageManager storageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
        List<String> result = new ArrayList<>();
        try {
            Class<?>[] paramClasses = {};
            Method getVolumePathsMethod = StorageManager.class.getMethod("getVolumePaths", paramClasses);
            getVolumePathsMethod.setAccessible(true);
            Object[] params = {};
            Object invoke = getVolumePathsMethod.invoke(storageManager, params);
            for (int i = 0; i < ((String[])invoke).length; i++) {
                String path = ((String[])invoke)[i];
                if(checkSDCardMount14(context,path)){
                    result.add(path);
                }
            }
        } catch (NoSuchMethodException e1) {
            e1.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return result;
    }

    private static void checkSDCardInfoBelow14() {
        BufferedReader bufferedReader = null;
        List<String> dev_mountStrs = null;
        try {
            bufferedReader = new BufferedReader(new FileReader(Environment
                    .getRootDirectory().getAbsoluteFile()
                    + File.separator
                    + "etc" + File.separator + "vold.fstab"));
            dev_mountStrs = new ArrayList<String>();
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                if (line.startsWith("dev_mount")) {
                    dev_mountStrs.add(line);
                }
            }
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (int i = 0; dev_mountStrs != null && i < dev_mountStrs.size(); i++) {
            SDCardInfo sdCardInfo = new SDCardInfo();
            String[] infoStr = dev_mountStrs.get(i).split(" ");
            sdCardInfo.label = infoStr[1];
            sdCardInfo.mountPoint = infoStr[2];
            if (sdCardInfo.mountPoint.equals(Environment
                    .getExternalStorageDirectory().getAbsolutePath())) {
                sdCardInfo.mounted = Environment.getExternalStorageState()
                        .equals(Environment.MEDIA_MOUNTED);
                SDCARD_INTERNAL = sdCardInfo;
            } else if (sdCardInfo.mountPoint.startsWith("/mnt")
                    && !sdCardInfo.mountPoint.equals(Environment
                    .getExternalStorageDirectory().getAbsolutePath())) {
                File file = new File(sdCardInfo.mountPoint + File.separator
                        + "temp");
                if (file.exists()) {
                    sdCardInfo.mounted = true;
                } else {
                    if (file.mkdir()) {
                        file.delete();
                        sdCardInfo.mounted = true;
                    } else {
                        sdCardInfo.mounted = true;
                    }
                }
                SDCARD_EXTERNAL = sdCardInfo;
            }
        }
    }

    private static void checkSDCardInfo(Context context) {
        String[] storagePathList = null;
        try {
            StorageManager storageManager = (StorageManager) context
                    .getSystemService(Context.STORAGE_SERVICE);
            Method getVolumePaths = storageManager.getClass().getMethod(
                    "getVolumePaths");
            storagePathList = (String[]) getVolumePaths.invoke(storageManager);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (storagePathList != null && storagePathList.length > 0) {
            String mSDCardPath = storagePathList[0];
            SDCardInfo internalDevInfo = new SDCardInfo();
            internalDevInfo.mountPoint = mSDCardPath;
            internalDevInfo.mounted = checkSDCardMount14(context, mSDCardPath);
            SDCARD_INTERNAL = internalDevInfo;
            if (storagePathList.length >= 2) {
                String externalDevPath = storagePathList[1];
                SDCardInfo externalDevInfo = new SDCardInfo();
                externalDevInfo.mountPoint = storagePathList[1];
                externalDevInfo.mounted = checkSDCardMount14(context,
                        externalDevPath);
                SDCARD_EXTERNAL = externalDevInfo;
            }
        }
    }

    private static boolean checkSDCardMount14(Context context,
                                                String mountPoint) {
        if (mountPoint == null) {
            return false;
        }
        StorageManager storageManager = (StorageManager) context
                .getSystemService(Context.STORAGE_SERVICE);
        try {
            Method getVolumeState = storageManager.getClass().getMethod(
                    "getVolumeState", String.class);
            String state = (String) getVolumeState.invoke(storageManager,
                    mountPoint);
            return Environment.MEDIA_MOUNTED.equals(state);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private static void checkSDCardStatus(Context context) {
        if (android.os.Build.VERSION.SDK_INT >= 14) {
            checkSDCardInfo(context);
        } else {
            checkSDCardInfoBelow14();
        }
    }

    /**
     * get storaget total size.
     * @param context
     * @return
     */
    public static long storageTotalSize(Context context) {
        android.os.StatFs statfs = null;
        if (SDCARD_EXTERNAL == null || SDCARD_INTERNAL == null) {
            checkSDCardStatus(context);
        }
        if (SDCARD_INTERNAL != null && SDCARD_INTERNAL.mounted) {
            File pathFile = Environment
                    .getExternalStorageDirectory();
            statfs = new android.os.StatFs(pathFile.getPath());
        } else if (SDCARD_EXTERNAL != null && SDCARD_EXTERNAL.mounted) {
            String esDir = SDCARD_EXTERNAL.mountPoint;
            statfs = new android.os.StatFs(esDir);
        }
        if (statfs != null) {
            long nTotalBlocks = getBlockCount(statfs);
            long nBlocSize = getBlockSize(statfs);
            return nTotalBlocks * nBlocSize;
        }
        return 0;
    }

    public static long[] getStorageInfo(String path){
        long[] result = new long[2];
        try{
            android.os.StatFs statfs = new android.os.StatFs(path);
            if (statfs != null) {
                long nTotalBlocks = getBlockCount(statfs);
                long nBlocSize = getBlockSize(statfs);
                result[0] =  nTotalBlocks * nBlocSize;

                long nAvailableBlock = getAvailableBlocksCount(statfs);
                result[1] = nAvailableBlock*nBlocSize;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }

    /**
     * get storage remain available size.
     * @param context
     * @return
     */
    public static long storageRemainSize(Context context) {
        android.os.StatFs statfs = null;
        if (SDCARD_EXTERNAL == null || SDCARD_INTERNAL == null) {
            checkSDCardStatus(context);
        }
        if (SDCARD_INTERNAL != null && SDCARD_INTERNAL.mounted) {
            File pathFile = Environment
                    .getExternalStorageDirectory();
            statfs = new android.os.StatFs(pathFile.getPath());
        } else if (SDCARD_EXTERNAL != null && SDCARD_EXTERNAL.mounted) {
            String esDir = SDCARD_EXTERNAL.mountPoint;
            statfs = new android.os.StatFs(esDir);
        }
        if (statfs != null) {
            long nBlocSize = getBlockSize(statfs);
            long nAvailableBlock = getAvailableBlocksCount(statfs);
            return nAvailableBlock * nBlocSize;
        }
        return 0;
    }

    private static long getBlockSize(StatFs statFs){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2){
            return statFs.getBlockSizeLong();
        }
        return statFs.getBlockSize();
    }

    private static long getBlockCount(StatFs statFs){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2){
            return statFs.getBlockCountLong();
        }
        return statFs.getBlockCount();
    }

    private static long getAvailableBlocksCount(StatFs statFs){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2){
            return statFs.getAvailableBlocksLong();
        }
        return statFs.getAvailableBlocks();
    }

    private static class SDCardInfo {
        public String label;
        private String mountPoint;
        private boolean mounted;

    }
}
