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

import javax.annotation.Nonnull;

/**
 * Space placeholder segment.
 *
 * @version 0.2.0 2018/04/27
 * @author ExBin Project (https://exbin.org)
 */
public class SpaceSegment extends DataSegment {

    private long length;

    public SpaceSegment(long length) {
        this.length = length;
    }

    @Override
    public long getStartPosition() {
        return 0;
    }

    public void setStartPosition(long startPosition) {
        throw new IllegalStateException("Unable to set start position on space placeholder segment");
    }

    @Override
    public long getLength() {
        return length;
    }

    public void setLength(long length) {
        this.length = length;
    }

    public byte getByte(long position) {
        throw new IllegalStateException("Unable to read data from space placeholder segment");
    }

    @Nonnull
    @Override
    public DataSegment copy() {
        return new SpaceSegment(length);
    }
}
