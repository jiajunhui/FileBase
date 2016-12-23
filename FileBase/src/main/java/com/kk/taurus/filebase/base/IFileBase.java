package com.kk.taurus.filebase.base;

import android.content.Context;

import java.io.File;

/**
 * Created by Taurus on 2016/12/22.
 */

public interface IFileBase {

    /**
     * manage parent dir type . context.getFilesDir()
     */
    int MANAGE_PARENT_DIR_APP_FILES = 1 << 1;

    /**
     * manage parent dir type . context.getCacheDir()
     */
    int MANAGE_PARENT_DIR_APP_CACHE_FILES = 1 << 2;

    /**
     * manage parent dir type . context.getExternalCacheDir()
     */
    int MANAGE_PARENT_DIR_APP_EXTERNAL_CACHE_FILES = 1 << 3;

    /**
     * manage parent dir type . Environment.getExternalStorageDirectory()
     */
    int MANAGE_PARENT_DIR_APP_EXTERNAL_ROOT = 1 << 4;

    /**
     * Gets the root name of the application management
     * @return
     */
    String getManageRootDirName();

    /**
     * Gets the parent directory type of the root of the application manager
     * @return
     */
    int getRootParentDirType();

    /**
     * Gets the alternate parent directory type for the root of the application management
     * @return
     */
    int getRootParentSpareDirType();

    /**
     * Create root directory of application management
     * @param context
     * @param parentDirType
     * @return
     */
    boolean createAppManageRootDir(Context context, int parentDirType);

    /**
     * create directory
     * @param dirPath
     * @return
     */
    File createDir(String dirPath);

}
