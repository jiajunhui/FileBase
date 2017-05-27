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
        Long t_o1 = o1.lastModified();
        Long t_o2 = o2.lastModified();
        int result = t_o1.compareTo(t_o2);
        return asc?result:-result;
    }
}
