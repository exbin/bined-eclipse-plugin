/*
 * Copyright (C) ExBin Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.exbin.bined.delta;

import java.io.IOException;
import java.io.InputStream;
import javax.annotation.Nonnull;
import org.exbin.utils.binary_data.FinishableStream;
import org.exbin.utils.binary_data.SeekableStream;

/**
 * Delta document input stream.
 *
 * @version 0.2.0 2018/04/27
 * @author ExBin Project (https://exbin.org)
 */
public class DeltaDocumentInputStream extends InputStream implements SeekableStream, FinishableStream {

    @Nonnull
    private final DeltaDocumentWindow data;
    private long position = 0;

    public DeltaDocumentInputStream(@Nonnull DeltaDocument document) {
        this.data = new DeltaDocumentWindow(document);
    }

    @Override
    public int read() throws IOException {
        if (position >= data.getDataSize()) {
            return -1;
        }

        try {
            int value = data.getByte(position) & 0xFF;
            position++;
            return value;
        } catch (ArrayIndexOutOfBoundsException ex) {
            return -1;
        }
    }

    @Override
    public void close() throws IOException {
        finish();
    }

    @Override
    public int available() throws IOException {
        long available = data.getDataSize() - position;
        return (available > Integer.MAX_VALUE) ? Integer.MAX_VALUE : (int) available;
    }

    @Override
    public int read(@Nonnull byte[] output, int offset, int length) throws IOException {
        if (output.length == 0 || length == 0) {
            return 0;
        }

        long dataSize = data.getDataSize();
        if (position >= dataSize) {
            return -1;
        }

        if (position + length > dataSize) {
            length = (int) (dataSize - position);
        }

        data.copyToArray(position, output, offset, length);
        position += length;
        return length;
    }

    @Override
    public void seek(long position) throws IOException {
        this.position = position;
    }

    @Override
    public long finish() throws IOException {
        position = data.getDataSize();
        return position;
    }

    @Override
    public long getLength() {
        return position;
    }

    @Override
    public long getStreamSize() {
        return data.getDataSize();
    }
}
