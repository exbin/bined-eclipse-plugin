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
package org.exbin.framework.preferences;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import org.eclipse.jface.preference.IPreferenceStore;
import org.exbin.framework.api.Preferences;

/**
 * Wrapper for preferences.
 *
 * @version 0.2.0 2019/08/10
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class PreferencesWrapper implements Preferences {

    private final IPreferenceStore preferences;

    public PreferencesWrapper(IPreferenceStore preferences) {
        this.preferences = preferences;
    }

    @Override
    public boolean exists(String key) {
        return preferences.contains(key);
    }

    @Nullable
    @Override
    public String get(String key) {
        String value = preferences.getString(key);
        return "".equals(value) ? null : value;
    }

    @Override
    public String get(String key, @Nullable String def) {
    	if (def != null) preferences.setDefault(key, def);
        String value = preferences.getString(key);
        return "".equals(value) ? def : value;
    }

    @Override
    public void put(String key, @Nullable String value) {
        if (value == null) {
            preferences.setValue(key, "");
        } else {
            preferences.setValue(key, value);
        }
    }

    @Override
    public void remove(String key) {
    	preferences.setValue(key, "");
    }

    @Override
    public void putInt(String key, int value) {
        preferences.setValue(key, value);
    }

    @Override
    public int getInt(String key, int def) {
    	preferences.setDefault(key, def);
        return preferences.getInt(key);
    }

    @Override
    public void putLong(String key, long value) {
        preferences.setValue(key, value);
    }

    @Override
    public long getLong(String key, long def) {
    	preferences.setDefault(key, def);
        return preferences.getLong(key);
    }

    @Override
    public void putBoolean(String key, boolean value) {
        preferences.setValue(key, value);
    }

    @Override
    public boolean getBoolean(String key, boolean def) {
    	preferences.setDefault(key, def);
        return preferences.getBoolean(key);
    }

    @Override
    public void putFloat(String key, float value) {
        preferences.setValue(key, value);
    }

    @Override
    public float getFloat(String key, float def) {
    	preferences.setDefault(key, def);
        return preferences.getFloat(key);
    }

    @Override
    public void putDouble(String key, double value) {
        preferences.setValue(key, value);
    }

    @Override
    public double getDouble(String key, double def) {
    	preferences.setDefault(key, def);
        return preferences.getDouble(key);
    }

    @Override
    public void putByteArray(String key, byte[] value) {
        // TODO preferences.setValue(key, value);
    }

    @Override
    public byte[] getByteArray(String key, byte[] def) {
        return null; // TODO preferences.getByteArray(key, def);
    }

    @Override
    public void flush() {
//        try {
//            preferences.flush();
//        } catch (BackingStoreException ex) {
//            ex.printStackTrace();
//        }
    }

    @Override
    public void sync() {
//        try {
//            preferences.sync();
//        } catch (BackingStoreException ex) {
//            ex.printStackTrace();
//        }
    }
}
