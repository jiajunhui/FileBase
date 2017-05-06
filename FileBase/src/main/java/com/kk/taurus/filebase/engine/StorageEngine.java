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
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;

import com.kk.taurus.filebase.entity.Storage;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Taurus on 2016/12/22.
 */

public class StorageEngine {

    private static void printStorageManagerMethods(){
        try {
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

    private static void printStorageVolumeMethods(Context context){
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

    public static List<Storage> getStorageList(Context context){
        List<Storage> result = new ArrayList<>();
        StorageVolume[] storageVolumes = getStorageVolumes(context);
        if(storageVolumes==null || storageVolumes.length<=0)
            return result;
        StorageManager storageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
        try {
            Storage storage;
            Method invokeMethod;
            for(int i=0;i<storageVolumes.length;i++){
                invokeMethod = storageVolumes[i].getClass().getMethod("getState");
                if(Environment.MEDIA_MOUNTED.equals(invokeMethod.invoke(storageVolumes[i]).toString())){
                    storage = new Storage();
                    //getDescriptionMethod = storageVolumes[i].getClass().getMethod("getDescription",Context.class);
                    //allowMassStorageMethod = storageVolumes[i].getClass().getMethod("allowMassStorage");
                    //isRemovableMethod = storageVolumes[i].getClass().getMethod("isRemovable");
                    invokeMethod = storageVolumes[i].getClass().getMethod("getPath");
                    String path = (String) invokeMethod.invoke(storageVolumes[i]);
                    //set storage path
                    storage.setPath(path);
                    //set storage description
                    invokeMethod = storageVolumes[i].getClass().getMethod("getDescription",Context.class);
                    storage.setDescription((String) invokeMethod.invoke(storageVolumes[i],context));
                    //set storage is removable
                    invokeMethod = storageVolumes[i].getClass().getMethod("isRemovable");
                    storage.setRemovableStorage((Boolean) invokeMethod.invoke(storageVolumes[i]));
                    //set storage is allowMassStorage
                    invokeMethod = storageVolumes[i].getClass().getMethod("allowMassStorage");
                    storage.setAllowMassStorage((boolean) invokeMethod.invoke(storageVolumes[i]));
                    //set storage is emulate
                    invokeMethod = storageVolumes[i].getClass().getMethod("isEmulated");
                    storage.setEmulated((boolean) invokeMethod.invoke(storageVolumes[i]));
                    //set storage is primary
                    invokeMethod = storageVolumes[i].getClass().getMethod("isPrimary");
                    storage.setPrimary((boolean) invokeMethod.invoke(storageVolumes[i]));
                    //set storage lowBytes limit
                    invokeMethod = StorageManager.class.getMethod("getStorageLowBytes",File.class);
                    storage.setLowBytesLimit((Long) invokeMethod.invoke(storageManager,new File(path)));
                    //set storage fullBytes limit
                    invokeMethod = StorageManager.class.getMethod("getStorageFullBytes",File.class);
                    storage.setFullBytesLimit((Long) invokeMethod.invoke(storageManager,new File(path)));
                    //set storage total size and available size
                    long[] sizeInfo = getStorageSizeInfo(path);
                    storage.setTotalSize(sizeInfo[0]);
                    storage.setAvailableSize(sizeInfo[1]);
                    result.add(storage);
                }

            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return result;
    }

    public static StorageVolume[] getStorageVolumes(Context context){
        StorageManager storageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
        try {
            Method method = StorageManager.class.getMethod("getVolumeList");
            return (StorageVolume[]) method.invoke(storageManager);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static List<String> getStoragePathList(Context context){
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
                if(checkStorageMountState(context,path)){
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

    private static boolean checkStorageMountState(Context context, String mountPoint) {
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

    /**
     * get storage size info by path.
     * @param path
     * @return storage[0] is total size , storage[1] is available size.
     */
    public static long[] getStorageSizeInfo(String path){
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

}
