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
import org.eclipse.jdt.debug.core.IJavaObject;
import org.eclipse.jdt.debug.core.IJavaPrimitiveValue;
import org.eclipse.jdt.debug.core.IJavaValue;
import org.exbin.bined.eclipse.data.PageProvider;
import org.exbin.bined.eclipse.data.PageProviderBinaryData;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Byte array data source for debugger view.
 *
 * @author ExBin Project (http://exbin.org)
 * @version 0.2.1 2022/06/01
 */
@ParametersAreNonnullByDefault
public class ValueByteArrayPageProvider implements PageProvider {

    private final IJavaArray arrayRef;

    public ValueByteArrayPageProvider(IJavaArray arrayRef) {
        this.arrayRef = arrayRef;
    }

    @Nonnull
    @Override
    public byte[] getPage(long pageIndex) {
        try {
	        int startPos = (int) (pageIndex * PageProviderBinaryData.PAGE_SIZE);
	        int length = Math.min(arrayRef.getLength() - startPos, PageProviderBinaryData.PAGE_SIZE);
	        byte[] result = new byte[length];
	        for (int i = 0; i < length; i++) {
	        	IJavaValue javaValue = arrayRef.getValue(startPos + i);
	        	byte value;
	        	if (javaValue instanceof IJavaPrimitiveValue) {
	        		value = ((IJavaPrimitiveValue) javaValue).getByteValue();
	        	} else {
	        		// TODO
//	        		((IJavaObject) javaValue).getField(name, superField)Variables();
	        		value = 0;
	        	}
	            result[i] = value;
	        }
	
	        return result;
		} catch (DebugException e) {
			return new byte[0];
		}
    }

    @Override
    public long getDocumentSize() {
        try {
			return arrayRef.getLength();
		} catch (DebugException e) {
			return 0;
		}
    }
}
