/*
 * Copyright (C) ExBin Project, https://exbin.org
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
package org.exbin.bined.eclipse.settings;

import java.util.Optional;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.eclipse.jface.preference.IPreferenceStore;
import org.exbin.jaguif.options.api.OptionsStorage;

/**
 * Wrapper for preferences.
 */
@NullMarked
public class EclipseOptionsStorage implements OptionsStorage {

    private final IPreferenceStore preferenceStore;

    public EclipseOptionsStorage(IPreferenceStore preferences) {
        this.preferenceStore = preferences;
    }

    @Override
    public boolean exists(String key) {
        return preferenceStore.contains(key);
    }

    @Override
    public Optional<String> get(String key) {
        String value = preferenceStore.getString(key);
        return "".equals(value) ? Optional.empty() : Optional.of(value);
    }

    @Override
    public String get(String key, String def) {
    	if (def != null) preferenceStore.setDefault(key, def);
        String value = preferenceStore.getString(key);
        return "".equals(value) || value == null ? def : value;
    }

    @Override
    public void put(String key, @Nullable String value) {
        if (value == null) {
            preferenceStore.setValue(key, "");
        } else {
            preferenceStore.setValue(key, value);
        }
    }

    @Override
    public void remove(String key) {
    	preferenceStore.setValue(key, "");
    }

    @Override
    public void putInt(String key, int value) {
        preferenceStore.setValue(key, value);
    }

    @Override
    public int getInt(String key, int def) {
    	preferenceStore.setDefault(key, def);
        return preferenceStore.getInt(key);
    }

    @Override
    public void putLong(String key, long value) {
        preferenceStore.setValue(key, value);
    }

    @Override
    public long getLong(String key, long def) {
    	preferenceStore.setDefault(key, def);
        return preferenceStore.getLong(key);
    }

    @Override
    public void putBoolean(String key, boolean value) {
        preferenceStore.setValue(key, value);
    }

    @Override
    public boolean getBoolean(String key, boolean def) {
    	preferenceStore.setDefault(key, def);
        return preferenceStore.getBoolean(key);
    }

    @Override
    public void putFloat(String key, float value) {
        preferenceStore.setValue(key, value);
    }

    @Override
    public float getFloat(String key, float def) {
    	preferenceStore.setDefault(key, def);
        return preferenceStore.getFloat(key);
    }

    @Override
    public void putDouble(String key, double value) {
        preferenceStore.setValue(key, value);
    }

    @Override
    public double getDouble(String key, double def) {
    	preferenceStore.setDefault(key, def);
        return preferenceStore.getDouble(key);
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
