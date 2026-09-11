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

import org.exbin.jaguif.options.api.OptionsStorage;
import org.exbin.jaguif.options.settings.api.SettingsOptions;
import org.jspecify.annotations.NullMarked;

import java.util.Locale;

/**
 * BinEd plugin integration options.
 */
@NullMarked
public class IntegrationOptions implements SettingsOptions {

    public static final String PREFERENCES_LOCALE_LANGUAGE = "integration.locale.language";
    public static final String PREFERENCES_LOCALE_COUNTRY = "integration.locale.country";
    public static final String PREFERENCES_LOCALE_VARIANT = "integration.locale.variant";
    public static final String PREFERENCES_LOCALE_TAG = "integration.locale.tag";
    public static final String PREFERENCES_ICONSET = "integration.iconset";
    public static final String PREFERENCES_REGISTER_OPEN_WITH_AS_BINARY = "integration.registerOpenWithAsBinary";
    public static final String PREFERENCES_REGISTER_DEBUG_VIEW_AS_BINARY = "integration.registerDebugVariablesAsBinary";
    public static final String PREFERENCES_REGISTER_BYTE_TO_BYTE_DIFF_TOOL = "integration.registerByteToByteDiffTool";
    public static final String PREFERENCES_REGISTER_DEFAULT_POPUP_MENU = "integration.registerDefaultPopupMenu";
    public static final String PREFERENCES_CHANGE_VISUAL_THEME = "integration.changeVisualTheme";
    public static final String PREFERENCES_VISUAL_THEME = "integration.visualTheme";

    private final OptionsStorage storage;

    public IntegrationOptions(OptionsStorage storage) {
        this.storage = storage;
    }

    public String getLocaleLanguage() {
        return storage.get(PREFERENCES_LOCALE_LANGUAGE, "");
    }

    public String getLocaleCountry() {
        return storage.get(PREFERENCES_LOCALE_COUNTRY, "");
    }

    public String getLocaleVariant() {
        return storage.get(PREFERENCES_LOCALE_VARIANT, "");
    }

    public String getLocaleTag() {
        return storage.get(PREFERENCES_LOCALE_TAG, "");
    }

    public Locale getLanguageLocale() {
        String localeTag = getLocaleTag();
        if (!localeTag.trim().isEmpty()) {
            try {
                return Locale.forLanguageTag(localeTag);
            } catch (SecurityException ex) {
                // Ignore it in java webstart
            }
        }

        String localeLanguage = getLocaleLanguage();
        String localeCountry = getLocaleCountry();
        String localeVariant = getLocaleVariant();
        try {
            return new Locale(localeLanguage, localeCountry, localeVariant);
        } catch (SecurityException ex) {
            // Ignore it in java webstart
        }

        return Locale.ROOT;
    }

    public void setLocaleLanguage(String language) {
        storage.put(PREFERENCES_LOCALE_LANGUAGE, language);
    }

    public void setLocaleCountry(String country) {
        storage.put(PREFERENCES_LOCALE_COUNTRY, country);
    }

    public void setLocaleVariant(String variant) {
        storage.put(PREFERENCES_LOCALE_VARIANT, variant);
    }

    public void setLocaleTag(String variant) {
        storage.put(PREFERENCES_LOCALE_TAG, variant);
    }

    public void setLanguageLocale(Locale locale) {
        setLocaleTag(locale.toLanguageTag());
        setLocaleLanguage(locale.getLanguage());
        setLocaleCountry(locale.getCountry());
        setLocaleVariant(locale.getVariant());
    }

    public String getIconSet() {
        return storage.get(PREFERENCES_ICONSET, "");
    }

    public void setIconSet(String iconSet) {
        storage.put(PREFERENCES_ICONSET, iconSet);
    }

    public boolean isRegisterOpenWithAsBinary() {
        return storage.getBoolean(PREFERENCES_REGISTER_OPEN_WITH_AS_BINARY, true);
    }

    public void setRegisterOpenWithAsBinary(boolean registerOpenWithAsBinary) {
        storage.putBoolean(PREFERENCES_REGISTER_OPEN_WITH_AS_BINARY, registerOpenWithAsBinary);
    }

    public boolean isRegisterDebugViewAsBinary() {
        return storage.getBoolean(PREFERENCES_REGISTER_DEBUG_VIEW_AS_BINARY, true);
    }

    public void setRegisterDebugViewAsBinary(boolean registerDebugViewAsBinary) {
        storage.putBoolean(PREFERENCES_REGISTER_DEBUG_VIEW_AS_BINARY, registerDebugViewAsBinary);
    }

    public boolean isRegisterByteToByteDiffTool() {
        return storage.getBoolean(PREFERENCES_REGISTER_BYTE_TO_BYTE_DIFF_TOOL, false);
    }

    public void setRegisterByteToByteDiffTool(boolean registerByteToByteDiffTool) {
        storage.putBoolean(PREFERENCES_REGISTER_BYTE_TO_BYTE_DIFF_TOOL, registerByteToByteDiffTool);
    }

    public boolean isChangeVisualTheme() {
        return storage.getBoolean(PREFERENCES_CHANGE_VISUAL_THEME, false);
    }

    public void setChangeVisualTheme(boolean changeVisualTheme) {
        storage.putBoolean(PREFERENCES_CHANGE_VISUAL_THEME, changeVisualTheme);
    }

    public String getVisualTheme() {
        return storage.get(PREFERENCES_VISUAL_THEME, "");
    }

    public void setVisualTheme(String visualTheme) {
        storage.put(PREFERENCES_VISUAL_THEME, visualTheme);
    }

    public boolean isRegisterDefaultPopupMenu() {
        return storage.getBoolean(PREFERENCES_REGISTER_DEFAULT_POPUP_MENU, false);
    }

    public void setRegisterDefaultPopupMenu(boolean registerDefaultPopupMenu) {
        storage.putBoolean(PREFERENCES_REGISTER_DEFAULT_POPUP_MENU, registerDefaultPopupMenu);
    }

    @Override
    public void copyTo(SettingsOptions options) {
        IntegrationOptions with = (IntegrationOptions) options;
        with.setLanguageLocale(getLanguageLocale());
        with.setChangeVisualTheme(isChangeVisualTheme());
        with.setVisualTheme(getVisualTheme());
        with.setIconSet(getIconSet());
        with.setRegisterOpenWithAsBinary(isRegisterOpenWithAsBinary());
        with.setRegisterDebugViewAsBinary(isRegisterDebugViewAsBinary());
        with.setRegisterByteToByteDiffTool(isRegisterByteToByteDiffTool());
        with.setRegisterDefaultPopupMenu(isRegisterDefaultPopupMenu());
    }
}
