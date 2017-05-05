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

package com.kk.taurus.filebase.entity;

import java.io.Serializable;

/**
 * Created by Taurus on 2017/5/5.
 */

public class Storage implements Serializable {
    private String path;
    private long totalSize;
    private long availableSize;
    private long lowBytesLimit;
    private long fullBytesLimit;
    private boolean isUsbMassStorage;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(long totalSize) {
        this.totalSize = totalSize;
    }

    public long getAvailableSize() {
        return availableSize;
    }

    public void setAvailableSize(long availableSize) {
        this.availableSize = availableSize;
    }

    public long getLowBytesLimit() {
        return lowBytesLimit;
    }

    public void setLowBytesLimit(long lowBytesLimit) {
        this.lowBytesLimit = lowBytesLimit;
    }

    public long getFullBytesLimit() {
        return fullBytesLimit;
    }

    public void setFullBytesLimit(long fullBytesLimit) {
        this.fullBytesLimit = fullBytesLimit;
    }

    public boolean isUsbMassStorage() {
        return isUsbMassStorage;
    }

    public void setUsbMassStorage(boolean usbMassStorage) {
        isUsbMassStorage = usbMassStorage;
    }
}
