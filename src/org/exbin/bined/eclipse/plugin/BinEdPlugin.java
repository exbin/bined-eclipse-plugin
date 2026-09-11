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
package org.exbin.bined.eclipse.plugin;

import java.awt.AWTEvent;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.exbin.bined.eclipse.settings.EclipseOptionsStorage;
import org.exbin.bined.eclipse.settings.IntegrationOptions;
import org.exbin.bined.eclipse.settings.IntegrationSettingsComponent;
import org.exbin.bined.eclipse.settings.gui.IntegrationSettingsPanel;
import org.exbin.bined.jaguif.bookmarks.BinedBookmarksModule;
import org.exbin.bined.jaguif.compare.BinedCompareModule;
import org.exbin.bined.jaguif.compare.action.CompareFilesAction;
import org.exbin.bined.jaguif.component.BinedComponentModule;
import org.exbin.bined.jaguif.document.BinedDocumentModule;
import org.exbin.bined.jaguif.editor.BinedEditorModule;
import org.exbin.bined.jaguif.inspector.BinedInspectorModule;
import org.exbin.bined.jaguif.inspector.settings.DataInspectorFontContextInference;
import org.exbin.bined.jaguif.inspector.settings.DataInspectorFontInference;
import org.exbin.bined.jaguif.macro.BinedMacroModule;
import org.exbin.bined.jaguif.objectdata.BinedObjectDataModule;
import org.exbin.bined.jaguif.operation.bouncycastle.BinedOperationBouncycastleModule;
import org.exbin.bined.jaguif.operation.code.BinedOperationCodeModule;
import org.exbin.bined.jaguif.operation.method.BinedOperationMethodModule;
import org.exbin.bined.jaguif.search.BinedSearchModule;
import org.exbin.bined.jaguif.theme.BinedThemeModule;
import org.exbin.bined.jaguif.tool.content.BinedToolContentModule;
import org.exbin.bined.jaguif.tool.content.action.ClipboardContentAction;
import org.exbin.bined.jaguif.tool.content.action.DragDropContentAction;
import org.exbin.bined.jaguif.viewer.BinedViewerModule;
import org.exbin.jaguif.App;
import org.exbin.jaguif.Module;
import org.exbin.jaguif.ModuleProvider;
import org.exbin.jaguif.action.ActionModule;
import org.exbin.jaguif.action.api.ActionModuleApi;
import org.exbin.jaguif.action.api.DialogParentComponent;
import org.exbin.jaguif.component.ComponentModule;
import org.exbin.jaguif.component.api.ComponentModuleApi;
import org.exbin.jaguif.context.ContextModule;
import org.exbin.jaguif.context.api.ContextModuleApi;
import org.exbin.jaguif.context.api.ContextStateManagement;
import org.exbin.jaguif.contribution.ContributionModule;
import org.exbin.jaguif.contribution.api.ContributionModuleApi;
import org.exbin.jaguif.contribution.api.GroupSequenceContributionRule;
import org.exbin.jaguif.contribution.api.PositionSequenceContributionRule;
import org.exbin.jaguif.contribution.api.RelativeSequenceContributionRule;
import org.exbin.jaguif.contribution.api.SeparationSequenceContributionRule;
import org.exbin.jaguif.contribution.api.SequenceContribution;
import org.exbin.jaguif.docking.DockingModule;
import org.exbin.jaguif.docking.api.ContextDocking;
import org.exbin.jaguif.docking.api.DockingModuleApi;
import org.exbin.jaguif.document.DocumentModule;
import org.exbin.jaguif.document.api.DocumentModuleApi;
import org.exbin.jaguif.file.FileModule;
import org.exbin.jaguif.file.api.FileModuleApi;
import org.exbin.jaguif.frame.FrameModule;
import org.exbin.jaguif.frame.api.FrameModuleApi;
import org.exbin.jaguif.help.HelpModule;
import org.exbin.jaguif.help.api.HelpModuleApi;
import org.exbin.jaguif.help.online.HelpOnlineModule;
import org.exbin.jaguif.help.online.action.OnlineHelpAction;
import org.exbin.jaguif.language.LanguageModule;
import org.exbin.jaguif.language.api.LanguageModuleApi;
import org.exbin.jaguif.language.api.LanguageProvider;
import org.exbin.jaguif.license.LicenseModule;
import org.exbin.jaguif.license.action.AboutAction;
import org.exbin.jaguif.license.api.LicenseModuleApi;
import org.exbin.jaguif.menu.MenuModule;
import org.exbin.jaguif.menu.api.DefaultActionMenuContribution;
import org.exbin.jaguif.menu.api.MenuDefinitionManagement;
import org.exbin.jaguif.menu.api.MenuModuleApi;
import org.exbin.jaguif.menu.popup.DefaultPopupMenu;
import org.exbin.jaguif.menu.popup.MenuPopupModule;
import org.exbin.jaguif.menu.popup.api.MenuPopupModuleApi;
import org.exbin.jaguif.operation.undo.OperationUndoModule;
import org.exbin.jaguif.operation.undo.api.OperationUndoModuleApi;
import org.exbin.jaguif.options.OptionsModule;
import org.exbin.jaguif.options.api.OptionsModuleApi;
import org.exbin.jaguif.options.api.OptionsStorage;
import org.exbin.jaguif.options.settings.OptionsSettingsModule;
import org.exbin.jaguif.options.settings.api.OptionsSettingsManagement;
import org.exbin.jaguif.options.settings.api.OptionsSettingsModuleApi;
import org.exbin.jaguif.options.settings.api.SettingsPageContribution;
import org.exbin.jaguif.options.settings.api.SettingsPanelType;
import org.exbin.jaguif.options.settings.contribution.SettingsContribution;
import org.exbin.jaguif.plugin.language.ja_JP.LanguageJaJpModule;
import org.exbin.jaguif.plugin.language.ko_KR.LanguageKoKrModule;
import org.exbin.jaguif.plugin.language.zh_Hans.LanguageZhHansModule;
import org.exbin.jaguif.plugins.iconset.material.IconSetMaterialModule;
import org.exbin.jaguif.search.SearchModule;
import org.exbin.jaguif.search.api.SearchModuleApi;
import org.exbin.jaguif.statusbar.StatusBarModule;
import org.exbin.jaguif.statusbar.api.StatusBarModuleApi;
import org.exbin.jaguif.tabpages.TabPagesModule;
import org.exbin.jaguif.tabpages.api.TabPagesModuleApi;
import org.exbin.jaguif.text.encoding.settings.TextEncodingContextInference;
import org.exbin.jaguif.text.encoding.settings.TextEncodingInference;
import org.exbin.jaguif.text.encoding.settings.TextEncodingsContextInference;
import org.exbin.jaguif.text.encoding.settings.TextEncodingsInference;
import org.exbin.jaguif.text.font.settings.TextFontContextInference;
import org.exbin.jaguif.text.font.settings.TextFontInference;
import org.exbin.jaguif.toolbar.ToolBarModule;
import org.exbin.jaguif.toolbar.api.ToolBarModuleApi;
import org.exbin.jaguif.ui.UiModule;
import org.exbin.jaguif.ui.api.UiModuleApi;
import org.exbin.jaguif.ui.theme.UiThemeModule;
import org.exbin.jaguif.ui.theme.api.UiThemeModuleApi;
import org.exbin.jaguif.window.api.WindowModuleApi;
import org.jspecify.annotations.NullMarked;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle.
 */
@NullMarked
public class BinEdPlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.exbin.bined.eclipse"; //$NON-NLS-1$
	// Internal prefix
    public static final String PLUGIN_PREFIX = "BinEdPlugin.";

	// The shared instance
	private static BinEdPlugin plugin;

    private static final List<IntegrationOptionsListener> INTEGRATION_OPTIONS_LISTENERS = new ArrayList<>();
    private static IntegrationOptions initialIntegrationOptions = null;
    private static PopupEventQueue popupEventQueue = null;
    private static boolean initialized = false;

    public BinEdPlugin() {
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.
	 * BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		
		initialize();

//		IContentTypeManager contentTypeManager = Platform.getContentTypeManager();
//		IContentType[] contentTypes = contentTypeManager.getAllContentTypes();
//		for (int i = 0; i < contentTypes.length; i++) {
//			IContentType contentType = contentTypes[i];
//			if (contentType.getBaseType() == null) {
//				contentType
//				// contentTypeManager.addContentType();
//			}
//		}
	}

    public static void initialize() {
        if (!initialized) {
            initialized = true;

            // Invoke Swing UI look&feel change
            initialIntegrationOptions = new IntegrationOptions(
                new EclipseOptionsStorage(BinEdPlugin.getDefault().getPreferenceStore())
            );
            if (initialIntegrationOptions.isChangeVisualTheme() && false) {
                String laf = initialIntegrationOptions.getVisualTheme();
                try {
                    if (laf.isEmpty()) {
                        String osName = System.getProperty("os.name").toLowerCase();
                        if (!osName.startsWith("windows") && !osName.startsWith("mac")) {
                            // Try "GTK+" on linux
                            try {
                                UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
                            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
                                laf = UIManager.getSystemLookAndFeelClassName();
                            }
                        } else {
                            laf = UIManager.getSystemLookAndFeelClassName();
                        }
                    }

                    if (laf != null && !laf.isEmpty()) {
//                        LookAndFeelApplier applier = lafPlugins.get(laf);
//                        if (applier != null) {
//                            applier.applyLookAndFeel(laf);
//                        } else {
                        UIManager.setLookAndFeel(laf);
//                        }
                    }
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
                    Logger.getLogger(BinEdPlugin.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
            AppModuleProvider appModuleProvider = new AppModuleProvider();
            appModuleProvider.createModules();
            App.setModuleProvider(appModuleProvider);
            appModuleProvider.init();
        }
    }

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);

        uninstallIntegration();
	}

	/**
	 * Returns the shared instance.
	 *
	 * @return the shared instance
	 */
	public static BinEdPlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given plug-in relative
	 * path.
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}

    public static void addIntegrationOptionsListener(IntegrationOptionsListener integrationOptionsListener) {
        INTEGRATION_OPTIONS_LISTENERS.add(integrationOptionsListener);
        if (initialIntegrationOptions != null) {
            integrationOptionsListener.integrationInit(initialIntegrationOptions);
        }
    }

    public static void applyIntegrationOptions(IntegrationOptions integrationOptions) {
        LanguageModuleApi languageModule = App.getModule(LanguageModuleApi.class);
        Locale languageLocale = integrationOptions.getLanguageLocale();
        if (languageLocale.equals(Locale.ROOT)) {
            // Try to match to IDE locale
            Locale ideLocale = Locale.getDefault();
            List<Locale> locales = new ArrayList<>();
            for (LanguageProvider languagePlugin : languageModule.getLanguagePlugins()) {
                locales.add(languagePlugin.getLocale());
            }
            List<Locale.LanguageRange> localeRange = new ArrayList<>();
            String languageTag = ideLocale.toLanguageTag();
            if ("zh-CN".equals(languageTag)) {
                // TODO detect match to zh_Hans somehow
                languageTag = "zh";
            }
            localeRange.add(new Locale.LanguageRange(languageTag));
            List<Locale> match = Locale.filter(localeRange, locales);
            if (!match.isEmpty()) {
                Locale firstMatchLlocale = match.get(0);
                languageModule.switchToLanguage(firstMatchLlocale);
                // TODO BinEdPlugin.setLocale(firstMatchLlocale);
            } else {
                languageModule.switchToLanguage(Locale.US);
                // TODO BinEdPlugin.setLocale(Locale.ROOT);
            }
        } else {
            languageModule.switchToLanguage(languageLocale);
            // TODO BinEdPlugin.setLocale("en-US".equals(languageLocale.toLanguageTag()) ? Locale.ROOT : languageLocale);
        }
        for (IntegrationOptionsListener listener : INTEGRATION_OPTIONS_LISTENERS) {
            listener.integrationInit(integrationOptions);
        }
        
        String iconSet = integrationOptions.getIconSet();
        if (!iconSet.isEmpty()) {
            languageModule.switchToIconSet(iconSet);
        }
        
        registerDefaultPopupMenu(integrationOptions.isRegisterDefaultPopupMenu());
    }

    private static void uninstallIntegration() {
        for (IntegrationOptionsListener listener : INTEGRATION_OPTIONS_LISTENERS) {
            listener.uninstallIntegration();
        }
    }

    @NullMarked
    public interface IntegrationOptionsListener {

        void integrationInit(IntegrationOptions integrationOptions);

        void uninstallIntegration();
    }

    @NullMarked
    private static class AppModuleProvider implements ModuleProvider {

        private final Map<Class<?>, Module> modules = new HashMap<>();

        private void createModules() {
            // Jaguif framework modules
            modules.put(LanguageModuleApi.class, new LanguageModule());
            modules.put(ContributionModuleApi.class, new ContributionModule());
            modules.put(ContextModuleApi.class, new ContextModule());
            modules.put(ActionModuleApi.class, new ActionModule());
            modules.put(OperationUndoModuleApi.class, new OperationUndoModule());
            modules.put(OptionsModuleApi.class, new org.exbin.jaguif.options.OptionsModule());
            modules.put(OptionsSettingsModuleApi.class, new OptionsSettingsModule());
            modules.put(UiModuleApi.class, new UiModule());
            modules.put(UiThemeModuleApi.class, new UiThemeModule());
            modules.put(HelpModuleApi.class, new HelpModule());
            modules.put(MenuModuleApi.class, new MenuModule());
            modules.put(MenuPopupModuleApi.class, new MenuPopupModule());
            modules.put(ToolBarModuleApi.class, new ToolBarModule());
            modules.put(StatusBarModuleApi.class, new StatusBarModule());
            modules.put(ComponentModuleApi.class, new ComponentModule());
            modules.put(WindowModuleApi.class, new EclipseWindowModule()); // Use modified module
            modules.put(FrameModuleApi.class, new FrameModule());
            modules.put(TabPagesModuleApi.class, new TabPagesModule());
            modules.put(LicenseModuleApi.class, new LicenseModule());
            modules.put(DocumentModuleApi.class, new DocumentModule());
            modules.put(FileModuleApi.class, new FileModule());
            modules.put(DockingModuleApi.class, new DockingModule());
            modules.put(HelpOnlineModule.class, new HelpOnlineModule());
            modules.put(SearchModuleApi.class, new SearchModule());

            // BinEd modules
            modules.put(BinedComponentModule.class, new BinedComponentModule());
            modules.put(BinedViewerModule.class, new BinedViewerModule());
            modules.put(BinedEditorModule.class, new BinedEditorModule());
            modules.put(BinedDocumentModule.class, new BinedDocumentModule());
            modules.put(BinedThemeModule.class, new BinedThemeModule());
            modules.put(BinedSearchModule.class, new BinedSearchModule());
            modules.put(BinedOperationMethodModule.class, new BinedOperationMethodModule());
            modules.put(BinedOperationCodeModule.class, new BinedOperationCodeModule());
            modules.put(BinedOperationBouncycastleModule.class, new BinedOperationBouncycastleModule());
            modules.put(BinedObjectDataModule.class, new BinedObjectDataModule());
            modules.put(BinedToolContentModule.class, new BinedToolContentModule());
            modules.put(BinedCompareModule.class, new BinedCompareModule());
            modules.put(BinedInspectorModule.class, new BinedInspectorModule());
            modules.put(BinedBookmarksModule.class, new BinedBookmarksModule());
            modules.put(BinedMacroModule.class, new BinedMacroModule());

            // Language plugins
            modules.put(LanguageJaJpModule.class, new LanguageJaJpModule());
            modules.put(LanguageKoKrModule.class, new LanguageKoKrModule());
            modules.put(LanguageZhHansModule.class, new LanguageZhHansModule());

            // Iconset plugins
            modules.put(IconSetMaterialModule.class, new IconSetMaterialModule());
        }

        private void init() {
            App.setAppBundle(ResourceBundle.getBundle("org.exbin.bined.eclipse.plugin.resources.BinEdPlugin",
                    Locale.ROOT));

            OptionsModule optionsModule = (OptionsModule) App.getModule(OptionsModuleApi.class);
            optionsModule.setAppOptions(new EclipseOptionsStorage(BinEdPlugin.getDefault().getPreferenceStore()));

            OptionsStorage preferences = optionsModule.getAppOptions();

            App.getModule(LanguageJaJpModule.class).register();
            App.getModule(LanguageKoKrModule.class).register();
            App.getModule(LanguageZhHansModule.class).register();

            App.getModule(IconSetMaterialModule.class).register();

            BinedBookmarksModule binedBookmarksModule = App.getModule(BinedBookmarksModule.class);
            binedBookmarksModule.register();
            BinedMacroModule binedMacroModule = App.getModule(BinedMacroModule.class);
            binedMacroModule.register();
            BinedOperationCodeModule binedOperationCodeModule = App.getModule(BinedOperationCodeModule.class);
            binedOperationCodeModule.register();
            BinedOperationBouncycastleModule binedOperationBouncycastleModule =
                    App.getModule(BinedOperationBouncycastleModule.class);
            binedOperationBouncycastleModule.register();

            LanguageModuleApi languageModule = App.getModule(LanguageModuleApi.class);
            ResourceBundle bundle = languageModule.getBundle(BinEdPlugin.class);
            languageModule.setAppBundle(bundle);

            applyIntegrationOptions(initialIntegrationOptions);
            
            UiModuleApi uiModule = App.getModule(UiModuleApi.class);
            uiModule.executePostInitActions();
            FrameModuleApi frameModule = App.getModule(FrameModuleApi.class);
            frameModule.init();

            FileModuleApi fileModule = App.getModule(FileModuleApi.class);
            fileModule.registerFileProviders();
            ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);

            WindowModuleApi windowModule = App.getModule(WindowModuleApi.class);
            windowModule.setHideHeaderPanels(true);
            SearchModuleApi searchModule = App.getModule(SearchModuleApi.class);

            LicenseModuleApi licenseModule = App.getModule(LicenseModuleApi.class);
            licenseModule.registerBasicPages();
            OptionsSettingsModuleApi optionsSettingsModule = App.getModule(OptionsSettingsModuleApi.class);
            optionsSettingsModule.setSettingsPanelType(SettingsPanelType.LIST);
            optionsSettingsModule.setOptionsRootCaption(App.getModule(LanguageModuleApi.class).getBundle(
                    IntegrationSettingsPanel.class).getString("options.caption"));
            // TODO Is currently stealing options action on macOS
            // optionsModule.registerMenuAction();

            HelpOnlineModule helpOnlineModule = App.getModule(HelpOnlineModule.class);
            try {
                helpOnlineModule.setOnlineHelpUrl(new URI(bundle.getString("online_help_url")).toURL());
                helpOnlineModule.registerOpeningHandler();
            } catch (MalformedURLException | URISyntaxException ex) {
                Logger.getLogger(BinEdPlugin.class.getName()).log(Level.SEVERE, null, ex);
            }

            BinEdEclipseDocking docking = new BinEdEclipseDocking();
            DocumentModule documentModule = (DocumentModule) App.getModule(DocumentModuleApi.class);
            BinedComponentModule binedComponentModule = App.getModule(BinedComponentModule.class);
            BinedViewerModule binedViewerModule = App.getModule(BinedViewerModule.class);
            BinedEditorModule binedEditorModule = App.getModule(BinedEditorModule.class);
            BinedDocumentModule binedDocumentModule = App.getModule(BinedDocumentModule.class);
            BinedThemeModule binedThemeModule = App.getModule(BinedThemeModule.class);
            BinedSearchModule binedSearchModule = App.getModule(BinedSearchModule.class);
            binedSearchModule.registerSearchComponent();

            BinedOperationMethodModule binedOperationModule = App.getModule(BinedOperationMethodModule.class);
            binedOperationModule.addBasicMethods();

            BinedToolContentModule binedToolContentModule = App.getModule(BinedToolContentModule.class);

            BinedInspectorModule binedInspectorModule = App.getModule(BinedInspectorModule.class);
            binedInspectorModule.registerBasicInspector();
            binedInspectorModule.registerShowParsingPanelMenuActions();
            binedInspectorModule.registerShowParsingPanelPopupMenuActions();

            BinedCompareModule binedCompareModule = App.getModule(BinedCompareModule.class);
            binedCompareModule.registerToolsOptionsMenuActions();

            OptionsSettingsManagement settingsManager = optionsSettingsModule.getMainSettingsManager();
            settingsManager.registerSettingsOptions(IntegrationOptions.class, IntegrationOptions::new);
            settingsManager.registerComponent("integration", new IntegrationSettingsComponent());
            SettingsPageContribution pageContribution =
                    new SettingsPageContribution("document", documentModule.getResourceBundle());
            settingsManager.registerPage(pageContribution);

            binedComponentModule.registerCodeAreaPopupMenu();
            binedViewerModule.registerCodeAreaPopupMenu();
            binedEditorModule.registerCodeAreaPopupMenu();
            binedDocumentModule.registerDocument();
            binedDocumentModule.registerStatusBar();
            binedViewerModule.registerCursorPositionStatusMenu();
            binedViewerModule.registerDataSizeStatusMenu();
            binedViewerModule.registerBinaryEncodingStatusMenu();
            binedEditorModule.registerEditModeStatusMenu();
            binedDocumentModule.registerProcessingModeStatusMenu();
            binedDocumentModule.registerEncodings();
            binedViewerModule.registerViewModeMenu();
            binedViewerModule.registerCodeTypeMenu();
            binedViewerModule.registerPositionCodeTypeMenu();
            binedViewerModule.registerHexCharactersCaseHandlerMenu();
            binedViewerModule.registerLayoutMenu();
            binedViewerModule.registerSettings();
            binedEditorModule.registerSettings();
            binedDocumentModule.registerSettings();
            binedThemeModule.registerSettings();
            searchModule.registerEditFindPopupMenuActions(BinedComponentModule.CODE_AREA_POPUP_MENU_ID);
            binedOperationModule.registerBlockEditPopupMenuActions();
            binedToolContentModule.registerClipboardContentMenu();
            binedToolContentModule.registerDragDropContentMenu();
            binedInspectorModule.registerSettings();
            binedViewerModule.registerFrameStatusBar();
            fileModule.registerSettings();

            FrameModuleApi frameModuleApi = App.getModule(FrameModuleApi.class);
            ContextStateManagement contextManagement = frameModuleApi.getFrameStateManager();
            settingsManager.registerInferenceOptions(TextEncodingInference.class, new TextEncodingContextInference(contextManagement));
            settingsManager.registerInferenceOptions(TextEncodingsInference.class, new TextEncodingsContextInference(contextManagement));
            settingsManager.registerInferenceOptions(TextFontInference.class, new TextFontContextInference((contextManagement)));
            settingsManager.registerInferenceOptions(DataInspectorFontInference.class, new DataInspectorFontContextInference(contextManagement));

            String toolsSubMenuId = BinEdPlugin.PLUGIN_PREFIX + "toolsMenu";
            MenuModuleApi menuModule = App.getModule(MenuModuleApi.class);
            MenuDefinitionManagement menuManagement = menuModule.getMainMenuDefinition(BinedComponentModule.CODE_AREA_POPUP_MENU_ID, BinedComponentModule.MODULE_ID);
            SequenceContribution contribution = new SettingsContribution();

            menuManagement.registerMenuContribution(contribution);
            menuManagement.registerMenuRule(contribution, new SeparationSequenceContributionRule(SeparationSequenceContributionRule.SeparationMode.AROUND));
            menuManagement.registerMenuRule(contribution, new RelativeSequenceContributionRule(RelativeSequenceContributionRule.NextToMode.AFTER, "binarySearchReplace"));

            Action toolsSubMenuAction = new AbstractAction(frameModule.getResourceBundle().getString("toolsMenu.text")) {
                @Override
                public void actionPerformed(ActionEvent e) {
                }
            };
            // toolsSubMenuAction.putValue(Action.SHORT_DESCRIPTION, ((FrameModule) frameModule).getResourceBundle().getString("toolsMenu.shortDescription"));
            contribution = menuManagement.registerMenuItem(toolsSubMenuId, toolsSubMenuAction);
            menuManagement.registerMenuRule(contribution, new PositionSequenceContributionRule(PositionSequenceContributionRule.PositionMode.BOTTOM_LAST));
            MenuDefinitionManagement subMenu = menuManagement.getSubMenu(toolsSubMenuId);
            contribution = new DefaultActionMenuContribution(CompareFilesAction.ACTION_ID, binedCompareModule::createCompareFilesAction);
            subMenu.registerMenuContribution(contribution);
            menuManagement.registerMenuRule(contribution, new PositionSequenceContributionRule(PositionSequenceContributionRule.PositionMode.TOP));
            contribution = new DefaultActionMenuContribution(ClipboardContentAction.ACTION_ID, binedToolContentModule::createClipboardContentAction);
            subMenu.registerMenuContribution(contribution);
            menuManagement.registerMenuRule(contribution, new PositionSequenceContributionRule(PositionSequenceContributionRule.PositionMode.TOP));
            contribution = new DefaultActionMenuContribution(DragDropContentAction.ACTION_ID, binedToolContentModule::createDragDropContentAction);
            subMenu.registerMenuContribution(contribution);
            menuManagement.registerMenuRule(contribution, new PositionSequenceContributionRule(PositionSequenceContributionRule.PositionMode.TOP));

            String aboutMenuGroup = BinEdPlugin.PLUGIN_PREFIX + "helpAboutMenuGroup";
            contribution = menuManagement.registerMenuGroup(aboutMenuGroup);
            menuManagement.registerMenuRule(contribution, new PositionSequenceContributionRule(PositionSequenceContributionRule.PositionMode.BOTTOM_LAST));
            menuManagement.registerMenuRule(contribution, new SeparationSequenceContributionRule(SeparationSequenceContributionRule.SeparationMode.ABOVE));
            contribution = new DefaultActionMenuContribution(OnlineHelpAction.ACTION_ID, helpOnlineModule::createOnlineHelpAction);
            menuManagement.registerMenuContribution(contribution);
            menuManagement.registerMenuRule(contribution, new GroupSequenceContributionRule(aboutMenuGroup));
            contribution = new DefaultActionMenuContribution(AboutAction.ACTION_ID, licenseModule::createAboutAction);
            menuManagement.registerMenuContribution(contribution);
            menuManagement.registerMenuRule(contribution, new GroupSequenceContributionRule(aboutMenuGroup));

            ContextStateManagement contextManager = frameModule.getFrameStateManager();
            contextManager.changeActiveState(ContextDocking.class, docking);
            contextManager.changeActiveState(DialogParentComponent.class, () -> frameModule.getFrame());

            optionsSettingsModule.initialLoadFromPreferences();
        }

        @Override
        public Class getManifestClass() {
            return BinEdPlugin.class;
        }

        @Override
        public void launch(Runnable runnable) {
        }

        @Override
        public void launch(String launchModuleId, String[] args) {
        }

        @Override
        public <T extends Module> T getModule(Class<T> moduleClass) {
            return (T) modules.get(moduleClass);
        }
    }

    
    /**
     * Registers default popup menu to AWT.
     */
    public static void registerDefaultPopupMenu(boolean register) {
        if (popupEventQueue == null) {
            if (register) {
                popupEventQueue = new PopupEventQueue();  
                popupEventQueue.registerToEventQueue();
            }
        } else {
            popupEventQueue.setEnabled(register);
        }
    }

    @NullMarked
    public static class PopupEventQueue extends EventQueue {
        
        SwingDefaultPopupMenu defaultPopupMenu;
        boolean enabled = true;

        private PopupEventQueue() {
            defaultPopupMenu = new SwingDefaultPopupMenu();
            defaultPopupMenu.initDefaultPopupMenu();
        }
        @Override
        protected void dispatchEvent(AWTEvent event) {
            super.dispatchEvent(event);

            if (enabled) {
                defaultPopupMenu.processAWTEvent(event);
            }
        }
        
        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public void registerToEventQueue() {
            Toolkit.getDefaultToolkit().getSystemEventQueue().push(this);
        }
    }

    @NullMarked
    public static class SwingDefaultPopupMenu extends DefaultPopupMenu {
        
        public void initDefaultPopupMenu() {
            ResourceBundle resourceBundle = App.getModule(LanguageModuleApi.class).getBundle(DefaultPopupMenu.class);
            initDefaultPopupMenu(resourceBundle, this.getClass());
        }
        
        public void processAWTEvent(AWTEvent event) {
            super.processAWTEvent(event);
        }
    }
}
