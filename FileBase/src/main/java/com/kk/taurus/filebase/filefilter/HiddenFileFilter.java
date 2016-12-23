package com.kk.taurus.filebase.filefilter;

import java.io.File;
import java.io.FileFilter;

/**
 * Created by Taurus on 2016/12/21.
 */

public class HiddenFileFilter implements FileFilter {
    @Override
    public boolean accept(File pathname) {
        return !pathname.getName().startsWith(".");
    }
}
