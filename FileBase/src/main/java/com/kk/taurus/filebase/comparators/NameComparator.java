package com.kk.taurus.filebase.comparators;

import java.io.File;
import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

/**
 * Created by Taurus on 2016/12/20.
 */

public class NameComparator implements Comparator<File> {

    private boolean asc = true;

    public NameComparator() {
    }

    public NameComparator(boolean asc){
        this.asc = asc;
    }

    @Override
    public int compare(File o1, File o2) {
        Collator collator = Collator.getInstance(Locale.CHINA);
        int result = collator.compare(o1.getName(),o2.getName());
        return asc?result:-result;
    }
}
