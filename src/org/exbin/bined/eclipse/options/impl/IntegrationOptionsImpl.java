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
package org.exbin.bined.eclipse.options.impl;

import org.exbin.bined.eclipse.options.IntegrationOptions;
import org.exbin.bined.eclipse.preferences.IntegrationPreferences;
import org.exbin.framework.options.api.OptionsData;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import java.util.Locale;

/**
 * BinEd plugin integration preferences.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class IntegrationOptionsImpl implements OptionsData, IntegrationOptions {

    private Locale languageLocale;
    private boolean registerOpenWithAsBinary = true;
    private boolean registerDebugViewAsBinary = true;
    private boolean registerByteToByteDiffTool = false;
    private boolean changeVisualTheme = false;
    private String visualTheme = "";
    private boolean registerDefaultPopupMenu = false;

//    private boolean registerEditAsBinaryForDbColumn = true;

    @Nonnull
    @Override
    public Locale getLanguageLocale() {
        return languageLocale;
    }

    @Override
    public void setLanguageLocale(Locale languageLocale) {
        this.languageLocale = languageLocale;
    }

    @Override
    public boolean isRegisterOpenWithAsBinary() {
        return registerOpenWithAsBinary;
    }

    public void setRegisterOpenWithAsBinary(boolean registerOpenWithAsBinary) {
        this.registerOpenWithAsBinary = registerOpenWithAsBinary;
    }

    @Override
    public boolean isRegisterDebugViewAsBinary() {
        return registerDebugViewAsBinary;
    }

    public void setRegisterDebugViewAsBinary(boolean registerDebugViewAsBinary) {
        this.registerDebugViewAsBinary = registerDebugViewAsBinary;
    }

    public boolean isRegisterByteToByteDiffTool() {
        return registerByteToByteDiffTool;
    }

    public void setRegisterByteToByteDiffTool(boolean registerByteToByteDiffTool) {
        this.registerByteToByteDiffTool = registerByteToByteDiffTool;
    }

  /*  public void setRegisterEditAsBinaryForDbColumn(boolean registerEditAsBinaryForDbColumn) {
        this.registerEditAsBinaryForDbColumn = registerEditAsBinaryForDbColumn;
    } */

    @Override
    public boolean isRegisterDefaultPopupMenu() {
        return registerDefaultPopupMenu;
    }

    public void setRegisterDefaultPopupMenu(boolean registerDefaultPopupMenu) {
        this.registerDefaultPopupMenu = registerDefaultPopupMenu;
    }

    @Override
    public boolean isChangeVisualTheme() {
    	return changeVisualTheme;
    }

    public void setChangeVisualTheme(boolean changeVisualTheme) {
        this.changeVisualTheme = changeVisualTheme;
    }

    @Override
    public String getVisualTheme() {
    	return visualTheme;
    }

    public void setVisualTheme(String visualTheme) {
        this.visualTheme = visualTheme;
    }

    public void loadFromPreferences(IntegrationPreferences preferences) {
        languageLocale = preferences.getLanguageLocale();
        registerOpenWithAsBinary = preferences.isRegisterOpenWithAsBinary();
        registerDebugViewAsBinary = preferences.isRegisterDebugViewAsBinary();
        registerByteToByteDiffTool = preferences.isRegisterByteToByteDiffTool();
        registerDefaultPopupMenu = preferences.isRegisterDefaultPopupMenu();
        changeVisualTheme = preferences.isChangeVisualTheme();
        visualTheme = preferences.getVisualTheme();
    }

    public void saveToPreferences(IntegrationPreferences preferences) {
        preferences.setLanguageLocale(languageLocale);
        preferences.setRegisterOpenWithAsBinary(registerOpenWithAsBinary);
        preferences.setRegisterDebugViewAsBinary(registerDebugViewAsBinary);
        preferences.setRegisterByteToByteDiffTool(registerByteToByteDiffTool);
        preferences.setRegisterDefaultPopupMenu(registerDefaultPopupMenu);
        preferences.setChangeVisualTheme(changeVisualTheme);
        preferences.setVisualTheme(visualTheme);
    }

    public void setOptions(IntegrationOptionsImpl options) {
        languageLocale = options.getLanguageLocale();
        registerOpenWithAsBinary = options.isRegisterOpenWithAsBinary();
        registerDebugViewAsBinary = options.isRegisterDebugViewAsBinary();
        registerByteToByteDiffTool = options.isRegisterByteToByteDiffTool();
        registerDefaultPopupMenu = options.isRegisterDefaultPopupMenu();
        changeVisualTheme = options.isChangeVisualTheme();
        visualTheme = options.getVisualTheme();
    }
}
