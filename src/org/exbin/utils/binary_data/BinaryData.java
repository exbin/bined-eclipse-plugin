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
package org.exbin.utils.binary_data;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Interface for binary data.
 *
 * @version 0.1.2 2017/01/01
 * @author ExBin Project (http://exbin.org)
 */
public interface BinaryData {

    /**
     * Returns true if data are empty.
     *
     * @return true if data empty
     */
    boolean isEmpty();

    /**
     * Returns size of data or -1 if size is not available.
     *
     * @return size of data in bytes
     */
    long getDataSize();

    /**
     * Returns particular byte from data.
     *
     * @param position position
     * @return byte on requested position
     */
    byte getByte(long position);

    /**
     * Creates copy of all data.
     *
     * @return copy of data
     */
    BinaryData copy();

    /**
     * Creates copy of given area.
     *
     * @param startFrom position to start copy from
     * @param length length of area
     * @return copy of data
     */
    BinaryData copy(long startFrom, long length);

    /**
     * Creates copy of given area into array of bytes.
     *
     * @param startFrom position to start copy from
     * @param target target byte array
     * @param offset offset position in target
     * @param length length of area to copy
     */
    void copyToArray(long startFrom, byte[] target, int offset, int length);

    /**
     * Saves data to given stream.
     *
     * @param outputStream output stream
     * @throws java.io.IOException if input/output error
     */
    void saveToStream(OutputStream outputStream) throws IOException;

    /**
     * Provides handler for input stream generation.
     *
     * @return new instance of input stream
     */
    InputStream getDataInputStream();

    /**
     * Disposes all allocated data if possible.
     */
    void dispose();
}
