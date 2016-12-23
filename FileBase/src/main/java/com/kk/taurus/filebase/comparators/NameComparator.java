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
