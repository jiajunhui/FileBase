package com.taurus.directorydemo;

import android.os.Environment;
import android.os.storage.StorageVolume;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.kk.taurus.filebase.comparators.NameComparator;
import com.kk.taurus.filebase.engine.AssetsEngine;
import com.kk.taurus.filebase.engine.FileEngine;
import com.kk.taurus.filebase.engine.FileStore;
import com.kk.taurus.filebase.engine.StorageEngine;
import com.kk.taurus.filebase.entity.Storage;
import com.kk.taurus.filebase.filefilter.FileNameFilter;
import com.kk.taurus.filebase.tools.BytesTool;
import com.kk.taurus.filebase.tools.MD5Utils;

import java.io.File;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StorageEngine.getStorageVolumes(this);
        StorageEngine.printMethod(this);

        List<Storage> storages = StorageEngine.getStorages(this);
        for(Storage storage : storages){
            System.out.println("storage_info : path = " + storage.getPath() + "\t"
                    + "totalSize = " + BytesTool.formatBytes(storage.getTotalSize()) + "\t"
                    + "availableSize = " + BytesTool.formatBytes(storage.getAvailableSize()) + "\t"
                    + "lowBytes = " + BytesTool.formatBytes(storage.getLowBytesLimit()) + "\t"
                    + "fullBytes = " + BytesTool.formatBytes(storage.getFullBytesLimit()) + "\t"
                    + "isUSB = " + storage.isUsbMassStorage());
        }

        TestFileBase testFileBase = new TestFileBase(this);
        testFileBase.getImageDir();
        testFileBase.getImageCacheDir();
        testFileBase.getTempDir();
        testFileBase.getLogDir();

//        FileEngine.deleteFile(testFileBase.getAssetsDir());

//        FileStore.save(testFileBase.getTempDir(),"sdbizdnvozidnv","15010218761-1234567890\nsdivnsidvnosdnmv");

        String data = FileStore.get(testFileBase.getTempDir(),"sdbizdnvozidnv");
        System.out.println("store_data  : " + data);

        String content = AssetsEngine.getAssetsStringContent(this,"test.txt");
        System.out.println("assets_content  : " + content);

        String content1 = AssetsEngine.getAssetsStringContent(this,new String[]{"dir1"},"test1.txt");
        System.out.println("assets_content1  : " + content1);

        String content2 = AssetsEngine.getAssetsStringContent(this,new String[]{"dir1","dir2"},"test2.txt");
        System.out.println("assets_content2  : " + content2);

        AssetsEngine.copyAssetsToDir(this,new String[]{"dir1","dir2"},"test2.txt",testFileBase.getAssetsDir());

        AssetsEngine.copyAssetsToDir(this,"ikanweb.zip",testFileBase.getAssetsDir());

        List<File> files = FileEngine.listFiles(Environment.getExternalStorageDirectory(),new FileNameFilter("[a-dA-D][a-z]+"),new Comparator[]{new NameComparator()});
        if(files!=null){
            for(File file : files){
                System.out.println("file_fiter : " + file.getName());
            }
        }

        boolean unZipResult = AssetsEngine.unZipAssets(this,"ikanweb.zip",testFileBase.getAssetsDir(),true);
        System.out.println("unZipResult : " + unZipResult);

        if(unZipResult){
            boolean copyResult = FileEngine.copy(new File(testFileBase.getAssetsDir(),"ikanweb"),testFileBase.getTempDir());
            System.out.println("copyResult : " + copyResult);
        }

        System.out.println("md5 : " + MD5Utils.md5(new File(testFileBase.getAssetsDir(),"ikanweb.zip")));

        String[] dir1 = AssetsEngine.listAssets(this,"dir1");
        String[] dir1_dir2 = AssetsEngine.listAssets(this,"dir1/dir2");

        for(String d : dir1){
            System.out.println("list_assets : dir1 = " + d);
        }

        for(String d : dir1_dir2){
            System.out.println("list_assets : dir1/dir2 = " + d);
        }
    }
}
