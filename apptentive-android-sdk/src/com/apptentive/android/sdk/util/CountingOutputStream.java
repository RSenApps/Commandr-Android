/*
 * Copyright (c) 2013, Apptentive, Inc. All Rights Reserved.
 * Please refer to the LICENSE file for the terms and conditions
 * under which redistribution and use of this file is permitted.
 */

package com.apptentive.android.sdk.util;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * This bad boy just counts the number of bytes written to it, and sends them along to the wrapped OutputStream.
 *
 * @author Sky Kelsey
 */
public class CountingOutputStream extends BufferedOutputStream {

    private int bytesWritten;

    public CountingOutputStream(OutputStream os) {
        super(os);
        this.bytesWritten = 0;
    }

    @Override
    public void write(int i) throws IOException {
        bytesWritten++;
        super.write(i);
    }

    @Override
    public void write(byte[] buffer) throws IOException {
        bytesWritten += buffer.length;
        super.write(buffer);
    }

    @Override
    public void write(byte[] buffer, int offset, int count) throws IOException {
        bytesWritten += count;
        super.write(buffer, offset, count);
    }

    public int getBytesWritten() {
        return bytesWritten;
    }
}
