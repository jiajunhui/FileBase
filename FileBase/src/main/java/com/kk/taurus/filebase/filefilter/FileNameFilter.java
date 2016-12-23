package com.kk.taurus.filebase.filefilter;

import java.io.File;
import java.io.FileFilter;

/**
 * Created by Taurus on 2016/12/23.
 */

public class FileNameFilter implements FileFilter {

    private String pattern;

    public FileNameFilter(String pattern){
        this.pattern = pattern;
    }

    @Override
    public boolean accept(File pathname) {
        return pathname.getName().matches(pattern);
    }

}
