package com.kk.taurus.filebase.engine;

import java.util.Iterator;
import java.util.LinkedList;

/**
 * Created by Taurus on 2017/6/6.
 */

public class PathQueue extends LinkedList<String> {

    @Override
    public synchronized String toString() {
        Iterator<String> stringIterator = this.descendingIterator();
        StringBuilder result = new StringBuilder();
        while (stringIterator.hasNext()){
            result.append(stringIterator.next());
        }
        return result.toString();
    }

}
