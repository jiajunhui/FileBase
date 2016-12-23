package com.kk.taurus.filebase.comparators;

import java.io.File;
import java.util.Comparator;

/**
 * Created by Taurus on 2016/12/20.
 */

public class DateComparator implements Comparator<File> {

    private boolean asc = true;

    public DateComparator() {
    }

    public DateComparator(boolean asc){
        this.asc = asc;
    }

    @Override
    public int compare(File o1, File o2) {
        long t_o1 = o1.lastModified();
        long t_o2 = o2.lastModified();
        if(t_o1==t_o2)
            return 0;
        return t_o1>t_o2?-1:1;
    }
}
