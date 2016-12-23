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

package com.kk.taurus.filebase.base;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import java.io.File;

/**
 * Created by Taurus on 2016/12/22.
 */

public abstract class FileBase implements IFileBase{

    private final String TAG = "FileBase";
    private boolean hasTrySpare;
    private File mManageRootDir;

    public FileBase(Context context){
        boolean mkResult = createAppManageRootDir(context,getRootParentDirType());
        Log.d(TAG,"init ManageDir : " + mkResult);
    }

    @Override
    public abstract String getManageRootDirName();

    @Override
    public abstract int getRootParentDirType();

    @Override
    public abstract int getRootParentSpareDirType();

    @Override
    public boolean createAppManageRootDir(Context context, int parentDirType) {
        switch (parentDirType){
            case MANAGE_PARENT_DIR_APP_FILES:
                this.mManageRootDir = new File(context.getFilesDir(),getManageRootDirName());
                break;

            case MANAGE_PARENT_DIR_APP_CACHE_FILES:
                this.mManageRootDir = new File(context.getCacheDir(),getManageRootDirName());
                break;

            case MANAGE_PARENT_DIR_APP_EXTERNAL_CACHE_FILES:
                this.mManageRootDir = new File(context.getExternalCacheDir(),getManageRootDirName());
                break;

            case MANAGE_PARENT_DIR_APP_EXTERNAL_ROOT:
                this.mManageRootDir = new File(Environment.getExternalStorageDirectory(),getManageRootDirName());
                break;

        }
        if(this.mManageRootDir==null && !hasTrySpare){
            hasTrySpare = true;
            return createAppManageRootDir(context,getRootParentSpareDirType());
        }
        Log.d(TAG,"ManageRootDir : " + mManageRootDir.getAbsolutePath() + " exists = " + mManageRootDir.exists());
        if(mManageRootDir.exists())
            return true;
        boolean mkResult = this.mManageRootDir.mkdirs();
        if(!mkResult && !hasTrySpare){
            hasTrySpare = true;
            return createAppManageRootDir(context,getRootParentSpareDirType());
        }
        return mkResult;
    }

    public File getManageRootDir() {
        return mManageRootDir;
    }

    @Override
    public File createDir(String dirPath) {
        if(getManageRootDir()!=null){
            File createDir = new File(getManageRootDir(),dirPath);
            if(createDir.exists())
                return createDir;
            boolean createResult = createDir.mkdirs();
            Log.d(TAG,"createDir : " + dirPath + " " + createResult);
            return createDir;
        }
        return null;
    }
}
