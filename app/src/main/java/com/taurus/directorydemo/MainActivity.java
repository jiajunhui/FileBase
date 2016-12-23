package com.taurus.directorydemo;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.kk.taurus.filebase.comparators.NameComparator;
import com.kk.taurus.filebase.engine.AssetsEngine;
import com.kk.taurus.filebase.engine.FileEngine;
import com.kk.taurus.filebase.engine.StorageEngine;
import com.kk.taurus.filebase.filefilter.FileNameFilter;
import com.kk.taurus.filebase.tools.BytesTool;

import java.io.File;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        long totalSize = StorageEngine.storageTotalSize(this);
        long remainSize = StorageEngine.storageRemainSize(this);

        System.out.println("storage_info : total = " + BytesTool.formatBytes(totalSize,2) + " remain = " + BytesTool.formatBytes(remainSize,3));

        TestFileBase testFileBase = new TestFileBase(this);
        testFileBase.getImageDir();
        testFileBase.getImageCacheDir();
        testFileBase.getTempDir();
        testFileBase.getLogDir();

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
    }
}
