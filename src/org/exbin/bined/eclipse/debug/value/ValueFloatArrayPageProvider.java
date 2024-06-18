/*
 * Copyright (C) ExBin Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
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
import org.eclipse.jdt.debug.core.IJavaFieldVariable;
import org.eclipse.jdt.debug.core.IJavaObject;
import org.eclipse.jdt.debug.core.IJavaPrimitiveValue;
import org.eclipse.jdt.debug.core.IJavaValue;
import org.exbin.bined.eclipse.data.PageProvider;
import org.exbin.bined.eclipse.data.PageProviderBinaryData;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.nio.ByteBuffer;

/**
 * Float array data source for debugger view.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class ValueFloatArrayPageProvider implements PageProvider {

    private final byte[] valuesCache = new byte[4];
    private final ByteBuffer byteBuffer = ByteBuffer.wrap(valuesCache);

    private final IJavaArray arrayRef;

    public ValueFloatArrayPageProvider(IJavaArray arrayRef) {
        this.arrayRef = arrayRef;
    }

    @Nonnull
    @Override
    public byte[] getPage(long pageIndex) {
        try {
	        int pageSize = PageProviderBinaryData.PAGE_SIZE / 4;
	        int startPos = (int) (pageIndex * pageSize);
	        int length = Math.min(arrayRef.getLength() - startPos, pageSize);
	        byte[] result = new byte[length * 4];
	        for (int i = 0; i < length; i++) {
	        	IJavaValue javaValue = arrayRef.getValue(startPos + i);
	            float value;
	        	if (javaValue instanceof IJavaPrimitiveValue) {
	        		value = ((IJavaPrimitiveValue) javaValue).getFloatValue();
	        	} else {
	        		IJavaFieldVariable variable = ((IJavaObject) javaValue).getField(ValueNodeConverter.VALUE_VARIABLE, false);
	        		value = variable != null ? ((IJavaPrimitiveValue) variable.getValue()).getFloatValue() : 0;
	        	}

	            byteBuffer.rewind();
	            byteBuffer.putFloat(value);
	            System.arraycopy(valuesCache, 0, result, i * 4, 4);
	        }

	        return result;
		} catch (DebugException e) {
			return new byte[0];
		}
    }

    @Override
    public long getDocumentSize() {
        try {
        	return arrayRef.getLength() * 4;
		} catch (DebugException e) {
			return 0;
		}
    }
}
