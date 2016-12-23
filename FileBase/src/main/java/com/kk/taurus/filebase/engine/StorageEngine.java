package com.kk.taurus.filebase.engine;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.os.storage.StorageManager;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Taurus on 2016/12/22.
 */

public class StorageEngine {

    private static SDCardInfo SDCARD_INTERNAL = null;
    private static SDCardInfo SDCARD_EXTERNAL = null;

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
