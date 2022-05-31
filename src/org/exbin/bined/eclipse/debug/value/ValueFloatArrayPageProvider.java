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

import org.eclipse.jdt.debug.core.IJavaArray;
import org.exbin.bined.eclipse.data.PageProvider;
import org.exbin.bined.eclipse.data.PageProviderBinaryData;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.nio.ByteBuffer;
import java.util.List;

/**
 * Float array data source for debugger view.
 *
 * @author ExBin Project (http://exbin.org)
 * @version 0.2.1 2022/05/31
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
/*        int pageSize = PageProviderBinaryData.PAGE_SIZE / 4;
        int startPos = (int) (pageIndex * pageSize);
        int length = Math.min(arrayRef.length() - startPos, pageSize);
        final List<Value> values = arrayRef.getValues(startPos, length);
        byte[] result = new byte[length * 4];
        for (int i = 0; i < values.size(); i++) {
            Value rawValue = values.get(i);
            if (rawValue instanceof ObjectReference) {
                Field field = ((ObjectReference) rawValue).referenceType().fieldByName("value");
                rawValue = ((ObjectReference) rawValue).getValue(field);
            }

            float value = rawValue instanceof FloatValue ? ((FloatValue) rawValue).value() : 0;

            byteBuffer.rewind();
            byteBuffer.putFloat(value);
            System.arraycopy(valuesCache, 0, result, i * 4, 4);
        }

        return result; */
    	return null;
    }

    @Override
    public long getDocumentSize() {
//        return arrayRef.length() * 4;
    	return 0;
    }
}
