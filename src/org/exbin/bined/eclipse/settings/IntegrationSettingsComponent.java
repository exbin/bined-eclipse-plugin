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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.ImageIcon;
import javax.swing.UIManager;

import org.exbin.bined.eclipse.settings.gui.IntegrationSettingsPanel;
import org.exbin.jaguif.App;
import org.exbin.jaguif.language.api.IconSetProvider;
import org.exbin.jaguif.language.api.LanguageModuleApi;
import org.exbin.jaguif.language.api.LanguageProvider;
import org.exbin.jaguif.options.settings.api.SettingsComponent;
import org.exbin.jaguif.options.settings.api.SettingsComponentProvider;
import org.exbin.jaguif.ui.model.LanguageRecord;
import org.exbin.jaguif.ui.settings.gui.LanguageSettingsPanel;
import org.exbin.jaguif.ui.theme.UiThemeModule;
import org.exbin.jaguif.ui.theme.api.UiThemeModuleApi;
import org.jspecify.annotations.NullMarked;

/**
 * Integration settings component provider.
 */
@NullMarked
public class IntegrationSettingsComponent implements SettingsComponentProvider {

    @Override
    public SettingsComponent createComponent() {
        IntegrationSettingsPanel panel = new IntegrationSettingsPanel();
        ResourceBundle languageResourceBundle = App.getModule(LanguageModuleApi.class).getBundle(
                LanguageSettingsPanel.class);
        panel.setDefaultLocaleName("<" + languageResourceBundle.getString("locale.defaultLanguage") + ">");
        List<LanguageRecord> languageLocales = new ArrayList<>();
        languageLocales.add(new LanguageRecord(Locale.ROOT, null));
        languageLocales.add(new LanguageRecord(Locale.forLanguageTag("en-US"), new ImageIcon(getClass().getResource(languageResourceBundle.getString("locale.englishFlag")))));

        List<LanguageRecord> languageRecords = new ArrayList<>();
        LanguageModuleApi languageModule = App.getModule(LanguageModuleApi.class);
        List<LanguageProvider> languagePlugins = languageModule.getLanguagePlugins();
        for (LanguageProvider languageProvider : languagePlugins) {
            languageRecords.add(new LanguageRecord(languageProvider.getLocale(), languageProvider.getFlag().orElse(null)));
        }
        languageLocales.addAll(languageRecords);

        ResourceBundle resourceBundle = App.getModule(LanguageModuleApi.class).getBundle(IntegrationSettingsPanel.class);
        List<String> themes = new ArrayList<>();
        themes.add("");
        boolean extraCrossPlatformLAF = !"javax.swing.plaf.metal.MetalLookAndFeel".equals(UIManager.getCrossPlatformLookAndFeelClassName());
        if (extraCrossPlatformLAF) {
            themes.add(UIManager.getCrossPlatformLookAndFeelClassName());
        }
        themes.add("javax.swing.plaf.metal.MetalLookAndFeel");
        themes.add("com.sun.java.swing.plaf.motif.MotifLookAndFeel");
        List<String> themeNames = new ArrayList<>();
        themeNames.add(resourceBundle.getString("theme.defaultTheme"));
        if (extraCrossPlatformLAF) {
            themeNames.add(resourceBundle.getString("theme.crossPlatformTheme"));
        }
        themeNames.add("Metal");
        themeNames.add("Motif");
        UIManager.LookAndFeelInfo[] infos = UIManager.getInstalledLookAndFeels();
        for (UIManager.LookAndFeelInfo lookAndFeelInfo : infos) {
            if (!themes.contains(lookAndFeelInfo.getClassName())) {
                themes.add(lookAndFeelInfo.getClassName());
                themeNames.add(lookAndFeelInfo.getName());
            }
        }
        panel.setThemes(themes, themeNames);

        List<String> iconSets = new ArrayList<>();
        iconSets.add("");
        List<String> iconSetNames = new ArrayList<>();
        UiThemeModule themeModule = (UiThemeModule) App.getModule(UiThemeModuleApi.class);
        ResourceBundle themeResourceBundle = themeModule.getResourceBundle();
        iconSetNames.add(themeResourceBundle.getString("iconset.defaultTheme"));
        List<IconSetProvider> providers = App.getModule(LanguageModuleApi.class).getIconSets();
        for (IconSetProvider provider : providers) {
            iconSets.add(provider.getId());
            iconSetNames.add(provider.getName());
        }

        panel.setLanguageLocales(languageLocales);
        panel.setIconSets(iconSets, iconSetNames);
        return panel;
    }
}
