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
package org.exbin.bined.eclipse.debug.value;

import org.eclipse.debug.core.DebugException;
import org.eclipse.jdt.debug.core.IJavaArray;
import org.eclipse.jdt.debug.core.IJavaPrimitiveValue;
import org.eclipse.jdt.debug.core.IJavaValue;
import org.exbin.bined.eclipse.data.PageProvider;
import org.exbin.bined.eclipse.data.PageProviderBinaryData;
import org.exbin.framework.bined.gui.ValuesPanel;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.math.BigInteger;

/**
 * Long array data source for debugger view.
 *
 * @author ExBin Project (http://exbin.org)
 * @version 0.2.1 2022/06/01
 */
@ParametersAreNonnullByDefault
public class ValueLongArrayPageProvider implements PageProvider {

    private final IJavaArray arrayRef;

    public ValueLongArrayPageProvider(IJavaArray arrayRef) {
        this.arrayRef = arrayRef;
    }

    @Nonnull
    @Override
    public byte[] getPage(long pageIndex) {
        try {
	        int pageSize = PageProviderBinaryData.PAGE_SIZE / 8;
	        int startPos = (int) (pageIndex * pageSize);
	        int length = Math.min(arrayRef.getLength() - startPos, pageSize);
	        byte[] result = new byte[length * 8];
	        for (int i = 0; i < length; i++) {
	        	IJavaValue javaValue = arrayRef.getValue(startPos + i);
	            long value = ((IJavaPrimitiveValue) javaValue).getLongValue();
	
	            BigInteger bigInteger = BigInteger.valueOf(value);
	            for (int bit = 0; bit < 7; bit++) {
	                BigInteger nextByte = bigInteger.and(ValuesPanel.BIG_INTEGER_BYTE_MASK);
	                result[i * 8 + 7 - bit] = nextByte.byteValue();
	                bigInteger = bigInteger.shiftRight(8);
	            }
	        }
	
	        return result;
		} catch (DebugException e) {
			return new byte[0];
		}
    }

    @Override
    public long getDocumentSize() {
        try {
	        return arrayRef.getLength() * 8;
		} catch (DebugException e) {
			return 0;
		}
    }
}
