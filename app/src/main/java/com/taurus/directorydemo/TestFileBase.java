package com.taurus.directorydemo;

import android.content.Context;
import com.kk.taurus.filebase.base.FileBase;
import java.io.File;

/**
 * Created by Taurus on 2016/12/23.
 */

public class TestFileBase extends FileBase {

    public static final String APP_MANAGE_ROOT_DIR_NAME             = "TestFileBase";
    public static final String APP_MANAGE_IMAGE_DIR_NAME            = "image";
    public static final String APP_MANAGE_IMAGE_CACHE_DIR_NAME      = "image/cache";
    public static final String APP_MANAGE_TEMP_DIR_NAME             = "temp";
    public static final String APP_MANAGE_ASSETS_COPY_DIR_NAME      = "assets";
    public static final String APP_MANAGE_LOG_DIR_NAME              = "log";

    public TestFileBase(Context context) {
        super(context);
    }

    public File getImageDir(){
        return createDir(APP_MANAGE_IMAGE_DIR_NAME);
    }

    public File getImageCacheDir(){
        return createDir(APP_MANAGE_IMAGE_CACHE_DIR_NAME);
    }

    public File getTempDir(){
        return createDir(APP_MANAGE_TEMP_DIR_NAME);
    }

    public File getAssetsDir(){
        return createDir(APP_MANAGE_ASSETS_COPY_DIR_NAME);
    }

    public File getLogDir(){
        return createDir(APP_MANAGE_LOG_DIR_NAME);
    }

    @Override
    public String getManageRootDirName() {
        return APP_MANAGE_ROOT_DIR_NAME;
    }

    @Override
    public int getRootParentDirType() {
        return MANAGE_PARENT_DIR_APP_EXTERNAL_ROOT;
    }

    @Override
    public int getRootParentSpareDirType() {
        return MANAGE_PARENT_DIR_APP_EXTERNAL_CACHE_FILES;
    }

}
