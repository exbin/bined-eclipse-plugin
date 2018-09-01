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
package org.exbin.bined.swt.basic;

import javax.annotation.Nullable;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;

/**
 * Basic code area component dimensions.
 *
 * @version 0.2.0 2017/08/31
 * @author ExBin Project (https://exbin.org)
 */
public class BasicCodeAreaMetrics {

    @Nullable
    private FontMetrics fontMetrics;
    @Nullable
    private GC gc;
    private boolean monospaceFont;

    private int rowHeight;
    private int characterWidth;
    private int fontHeight;

    // TODO replace with computation
    private final int subFontSpace = 3;

    /**
     * GC is expected to have proper font set.
     *
     * @param gc graphics context
     */
    public void recomputeMetrics(@Nullable GC gc) {
        this.gc = gc;
        if (gc == null) {
            fontMetrics = null;
            characterWidth = 0;
            fontHeight = 0;
        } else {
            fontMetrics = gc.getFontMetrics();
            fontHeight = fontMetrics.getHeight();

            /**
             * Use small 'w' character to guess normal font width.
             */
            characterWidth = gc.textExtent("w").x;
            /**
             * Compare it to small 'i' to detect if font is monospaced.
             *
             * TODO: Is there better way?
             */
            monospaceFont = false; // TODO characterWidth == g.textExtent(" ").x && characterWidth == g.textExtent("i").x;
            int fontSize = fontMetrics.getHeight();
            rowHeight = fontSize + subFontSpace;
        }

    }

    public boolean isInitialized() {
        return rowHeight != 0 && characterWidth != 0;
    }

    @Nullable
    public FontMetrics getFontMetrics() {
        return fontMetrics;
    }

    public int getCharWidth(char value) {
        return gc.textExtent(String.valueOf(value)).x;
    }

    public boolean isMonospaceFont() {
        return monospaceFont;
    }

    public int getRowHeight() {
        return rowHeight;
    }

    public int getCharacterWidth() {
        return characterWidth;
    }

    public int getFontHeight() {
        return fontHeight;
    }

    public int getSubFontSpace() {
        return subFontSpace;
    }
}
