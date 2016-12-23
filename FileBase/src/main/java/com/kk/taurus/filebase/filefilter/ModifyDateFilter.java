package com.kk.taurus.filebase.filefilter;

import java.io.File;
import java.io.FileFilter;

/**
 * Created by Taurus on 2016/12/23.
 */

public class ModifyDateFilter implements FileFilter {

    private long start,end;

    public ModifyDateFilter(long start, long end){
        this.start = start;
        this.end = end;
    }

    @Override
    public boolean accept(File pathname) {
        long lastModify = pathname.lastModified();
        return lastModify>=start && lastModify<=end;
    }
}
