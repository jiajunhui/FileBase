package com.kk.taurus.filebase.comparators;

import java.io.File;
import java.util.Comparator;

/**
 * Created by Taurus on 2016/12/20.
 */

public class DirectoryFirstComparator implements Comparator<File> {

    private boolean asc = true;

    public DirectoryFirstComparator() {
    }

    public DirectoryFirstComparator(boolean asc){
        this.asc = asc;
    }

    @Override
    public int compare(File o1, File o2) {
        if(o1.isDirectory() && !o2.isDirectory()){
            return asc?-1:1;
        }
        if(!o1.isDirectory() && o2.isDirectory()){
            return asc?1:-1;
        }
        return 0;
    }
}
