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
import org.eclipse.debug.core.model.IValue;
import org.eclipse.jdt.debug.core.IJavaArray;
import org.eclipse.jdt.debug.core.IJavaFieldVariable;
import org.eclipse.jdt.debug.core.IJavaObject;
import org.eclipse.jdt.debug.core.IJavaPrimitiveValue;
import org.exbin.auxiliary.binary_data.BinaryData;
import org.exbin.auxiliary.binary_data.ByteArrayData;
import org.exbin.bined.eclipse.data.PageProviderBinaryData;
import org.exbin.bined.eclipse.debug.DebugViewDataProvider;
import org.exbin.bined.eclipse.debug.DefaultDebugViewDataProvider;
import org.exbin.framework.bined.inspector.gui.BasicValuesPanel;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Debug values converter.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class ValueNodeConverter {

	public static final String VALUE_VARIABLE = "value";
    private final byte[] valuesCache = new byte[8];
    private final ByteBuffer byteBuffer = ByteBuffer.wrap(valuesCache);

    public ValueNodeConverter() {
    }

    @Nonnull
    public List<DebugViewDataProvider> identifyAvailableProviders(IValue value) {

        List<DebugViewDataProvider> providers = new ArrayList<>();

        if (value instanceof IJavaArray) {
            BinaryData data = processArrayData((IJavaArray) value);
            if (data != null)
                providers.add(new DefaultDebugViewDataProvider("binary sequence from array", data));
        } else {
            BinaryData data = processSimpleValue(value);
            if (data != null) {
                providers.add(new DefaultDebugViewDataProvider("binary value", data));
            }
        }

        try {
        	final String valueString = value.getValueString();
            providers.add(new DebugViewDataProvider() {
                @Nonnull
                @Override
                public String getName() {
                    return "toString()";
                }

                @Nonnull
                @Override
                public BinaryData getData() {
                    return new ByteArrayData(valueString.getBytes(Charset.defaultCharset()));
                }
            });
    	} catch (DebugException ex) {
    		// ignore
    	}

        return providers;
    }

    @Nullable
    private static BinaryData processArrayData(IJavaArray arrayRef) {
    	try {
	        final String componentType = arrayRef.getJavaType().getName();
	        switch (componentType) {
	            case "java.lang.Boolean[]":
	            case "boolean[]": {
	                return new PageProviderBinaryData(new ValueBooleanArrayPageProvider(arrayRef));
	            }
	            case "java.lang.Byte[]":
	            case "byte[]": {
	                return new PageProviderBinaryData(new ValueByteArrayPageProvider(arrayRef));
	            }
	            case "java.lang.Short[]":
	            case "short[]": {
	                return new PageProviderBinaryData(new ValueShortArrayPageProvider(arrayRef));
	            }
	            case "java.lang.Integer[]":
	            case "int[]": {
	                return new PageProviderBinaryData(new ValueIntegerArrayPageProvider(arrayRef));
	            }
	            case "java.lang.Long[]":
	            case "long[]": {
	                return new PageProviderBinaryData(new ValueLongArrayPageProvider(arrayRef));
	            }
	            case "java.lang.Float[]":
	            case "float[]": {
	                return new PageProviderBinaryData(new ValueFloatArrayPageProvider(arrayRef));
	            }
	            case "java.lang.Double[]":
	            case "double[]": {
	                return new PageProviderBinaryData(new ValueDoubleArrayPageProvider(arrayRef));
	            }
	            case "java.lang.Character[]":
	            case "char[]": {
	                return new PageProviderBinaryData(new ValueCharArrayPageProvider(arrayRef));
	            }
	        }
    	} catch (DebugException ex) {
    		// ignore
    	}

        return null;
    }

    @Nullable
    private BinaryData processSimpleValue(IValue javaValue) {
        try {
        	String typeString = javaValue.getReferenceTypeName();

	        switch (typeString) {
	            case "java.lang.Byte":
	            case "byte": {
	                byte[] byteArray = new byte[1];
		        	byte value;
		        	if (javaValue instanceof IJavaPrimitiveValue) {
		        		value = ((IJavaPrimitiveValue) javaValue).getByteValue();
		        	} else {
		        		IJavaFieldVariable variable = ((IJavaObject) javaValue).getField(ValueNodeConverter.VALUE_VARIABLE, false);
		        		IJavaPrimitiveValue variableValue = (IJavaPrimitiveValue) variable.getValue();
		        		value = variableValue != null ? variableValue.getByteValue() : 0;
		        	}
	                byteArray[0] = value;
	                return new ByteArrayData(byteArray);
	            }
	            case "java.lang.Short":
	            case "short": {
	                byte[] byteArray = new byte[2];
		            short value;
		        	if (javaValue instanceof IJavaPrimitiveValue) {
		        		value = ((IJavaPrimitiveValue) javaValue).getShortValue();
		        	} else {
		        		IJavaFieldVariable variable = ((IJavaObject) javaValue).getField(ValueNodeConverter.VALUE_VARIABLE, false);
		        		IJavaPrimitiveValue variableValue = (IJavaPrimitiveValue) variable.getValue();
		        		value = variableValue != null ? variableValue.getShortValue() : 0;
		        	}
	                byteArray[0] = (byte) (value >> 8);
	                byteArray[1] = (byte) (value & 0xff);
	                return new ByteArrayData(byteArray);
	            }
	            case "java.lang.Integer":
	            case "int": {
	                byte[] byteArray = new byte[4];
		            int value;
		        	if (javaValue instanceof IJavaPrimitiveValue) {
		        		value = ((IJavaPrimitiveValue) javaValue).getIntValue();
		        	} else {
		        		IJavaFieldVariable variable = ((IJavaObject) javaValue).getField(ValueNodeConverter.VALUE_VARIABLE, false);
		        		IJavaPrimitiveValue variableValue = (IJavaPrimitiveValue) variable.getValue();
		        		value = variableValue != null ? variableValue.getIntValue() : 0;
		        	}
	                byteArray[0] = (byte) (value >> 24);
	                byteArray[1] = (byte) ((value >> 16) & 0xff);
	                byteArray[2] = (byte) ((value >> 8) & 0xff);
	                byteArray[3] = (byte) (value & 0xff);
	                return new ByteArrayData(byteArray);
	            }
	            case "java.lang.Long":
	            case "long": {
	                byte[] byteArray = new byte[8];
		        	long value;
		        	if (javaValue instanceof IJavaPrimitiveValue) {
		        		value = ((IJavaPrimitiveValue) javaValue).getLongValue();
		        	} else {
		        		IJavaFieldVariable variable = ((IJavaObject) javaValue).getField(ValueNodeConverter.VALUE_VARIABLE, false);
		        		IJavaPrimitiveValue variableValue = (IJavaPrimitiveValue) variable.getValue();
		        		value = variableValue != null ? variableValue.getLongValue() : 0;
		        	}
	                BigInteger bigInteger = BigInteger.valueOf(value);
	                for (int bit = 0; bit < 7; bit++) {
	                    BigInteger nextByte = bigInteger.and(BasicValuesPanel.BIG_INTEGER_BYTE_MASK);
	                    byteArray[7 - bit] = nextByte.byteValue();
	                    bigInteger = bigInteger.shiftRight(8);
	                }
	                return new ByteArrayData(byteArray);
	            }
	            case "java.lang.Float":
	            case "float": {
	                byte[] byteArray = new byte[4];
		            float value;
		        	if (javaValue instanceof IJavaPrimitiveValue) {
		        		value = ((IJavaPrimitiveValue) javaValue).getFloatValue();
		        	} else {
		        		IJavaFieldVariable variable = ((IJavaObject) javaValue).getField(ValueNodeConverter.VALUE_VARIABLE, false);
		        		IJavaPrimitiveValue variableValue = (IJavaPrimitiveValue) variable.getValue();
		        		value = variableValue != null ? variableValue.getFloatValue() : 0;
		        	}
	                byteBuffer.rewind();
	                byteBuffer.putFloat(value);
	                System.arraycopy(valuesCache, 0, byteArray, 0, 4);
	                return new ByteArrayData(byteArray);
	            }
	            case "java.lang.Double":
	            case "double": {
	                byte[] byteArray = new byte[8];
		            double value;
		        	if (javaValue instanceof IJavaPrimitiveValue) {
		        		value = ((IJavaPrimitiveValue) javaValue).getDoubleValue();
		        	} else {
		        		IJavaFieldVariable variable = ((IJavaObject) javaValue).getField(ValueNodeConverter.VALUE_VARIABLE, false);
		        		IJavaPrimitiveValue variableValue = (IJavaPrimitiveValue) variable.getValue();
		        		value = variableValue != null ? variableValue.getDoubleValue() : 0;
		        	}
	                byteBuffer.rewind();
	                byteBuffer.putDouble(value);
	                System.arraycopy(valuesCache, 0, byteArray, 0, 8);
	                return new ByteArrayData(byteArray);
	            }
	            case "java.lang.Character":
	            case "char": {
	                byte[] byteArray = new byte[2];
		            char value;
		        	if (javaValue instanceof IJavaPrimitiveValue) {
		        		value = ((IJavaPrimitiveValue) javaValue).getCharValue();
		        	} else {
		        		IJavaFieldVariable variable = ((IJavaObject) javaValue).getField(ValueNodeConverter.VALUE_VARIABLE, false);
		        		IJavaPrimitiveValue variableValue = (IJavaPrimitiveValue) variable.getValue();
		        		value = variableValue != null ? variableValue.getCharValue() : 0;
		        	}
	                byteBuffer.rewind();
	                byteBuffer.putChar(value);
	                System.arraycopy(valuesCache, 0, byteArray, 0, 2);
	                return new ByteArrayData(byteArray);
	            }
	        }

	        return null;
		} catch (DebugException e) {
			return null;
		}
    }
}
