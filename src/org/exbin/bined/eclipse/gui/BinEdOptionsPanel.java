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
package org.exbin.bined.eclipse.gui;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.event.ListSelectionEvent;

import org.exbin.bined.eclipse.BinEdApplyOptions;
import org.exbin.bined.swing.extended.color.ExtendedCodeAreaColorProfile;
import org.exbin.bined.swing.extended.layout.DefaultExtendedCodeAreaLayoutProfile;
import org.exbin.bined.swing.extended.theme.ExtendedCodeAreaThemeProfile;
import org.exbin.framework.bined.options.CodeAreaColorOptions;
import org.exbin.framework.bined.options.CodeAreaLayoutOptions;
import org.exbin.framework.bined.options.CodeAreaOptions;
import org.exbin.framework.bined.options.CodeAreaThemeOptions;
import org.exbin.framework.bined.options.EditorOptions;
import org.exbin.framework.bined.options.StatusOptions;
import org.exbin.framework.bined.options.impl.CodeAreaColorOptionsImpl;
import org.exbin.framework.bined.options.impl.CodeAreaLayoutOptionsImpl;
import org.exbin.framework.bined.options.impl.CodeAreaOptionsImpl;
import org.exbin.framework.bined.options.impl.CodeAreaThemeOptionsImpl;
import org.exbin.framework.bined.options.impl.EditorOptionsImpl;
import org.exbin.framework.bined.options.impl.StatusOptionsImpl;
import org.exbin.framework.bined.options.gui.CodeAreaOptionsPanel;
import org.exbin.framework.bined.options.gui.ColorProfilePanel;
import org.exbin.framework.bined.options.gui.ColorProfilesPanel;
import org.exbin.framework.bined.options.gui.ColorTemplatePanel;
import org.exbin.framework.bined.options.gui.EditorOptionsPanel;
import org.exbin.framework.bined.options.gui.LayoutProfilePanel;
import org.exbin.framework.bined.options.gui.LayoutProfilesPanel;
import org.exbin.framework.bined.options.gui.LayoutTemplatePanel;
import org.exbin.framework.bined.options.gui.NamedProfilePanel;
import org.exbin.framework.bined.options.gui.ProfileSelectionPanel;
import org.exbin.framework.bined.options.gui.StatusOptionsPanel;
import org.exbin.framework.bined.options.gui.ThemeProfilePanel;
import org.exbin.framework.bined.options.gui.ThemeProfilesPanel;
import org.exbin.framework.bined.options.gui.ThemeTemplatePanel;
import org.exbin.framework.bined.preferences.BinaryEditorPreferences;
import org.exbin.framework.editor.text.gui.AddEncodingPanel;
import org.exbin.framework.editor.text.gui.TextFontPanel;
import org.exbin.framework.editor.text.options.TextFontOptions;
import org.exbin.framework.editor.text.options.gui.TextEncodingPanel;
import org.exbin.framework.editor.text.options.gui.TextEncodingOptionsPanel;
import org.exbin.framework.editor.text.options.gui.TextFontOptionsPanel;
import org.exbin.framework.editor.text.options.impl.TextEncodingOptionsImpl;
import org.exbin.framework.editor.text.options.impl.TextFontOptionsImpl;
import org.exbin.framework.editor.text.service.TextFontService;
import org.exbin.framework.utils.LanguageUtils;
import org.exbin.framework.utils.WindowUtils;
import org.exbin.framework.utils.WindowUtils.DialogWrapper;
import org.exbin.framework.utils.gui.DefaultControlPanel;
import org.exbin.framework.utils.handler.DefaultControlHandler;

/**
 * Binary editor options panel.
 *
 * @version 0.2.1 2020/01/31
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class BinEdOptionsPanel extends javax.swing.JPanel implements BinEdApplyOptions {

    private BinaryEditorPreferences preferences;
    private final java.util.ResourceBundle resourceBundle = LanguageUtils.getResourceBundleByClass(BinEdOptionsPanel.class);

    private DefaultListModel<CategoryItem> categoryModel = new DefaultListModel<>();
    private JPanel currentCategoryPanel = null;

    private final EditorOptionsImpl editorOptions = new EditorOptionsImpl();
    private final StatusOptionsImpl statusOptions = new StatusOptionsImpl();
    private final TextEncodingOptionsImpl encodingOptions = new TextEncodingOptionsImpl();
    private final TextFontOptionsImpl fontOptions = new TextFontOptionsImpl();
    private final CodeAreaOptionsImpl codeAreaOptions = new CodeAreaOptionsImpl();
    private final CodeAreaLayoutOptionsImpl layoutOptions = new CodeAreaLayoutOptionsImpl();
    private final CodeAreaColorOptionsImpl colorOptions = new CodeAreaColorOptionsImpl();
    private final CodeAreaThemeOptionsImpl themeOptions = new CodeAreaThemeOptionsImpl();

    private final EditorOptionsPanel editorOptionsPanel = new EditorOptionsPanel();
    private final StatusOptionsPanel statusOptionsPanel = new StatusOptionsPanel();
    private final CodeAreaOptionsPanel codeAreaOptionsPanel = new CodeAreaOptionsPanel();
    private final TextEncodingOptionsPanel encodingOptionsPanel = new TextEncodingOptionsPanel();
    private final TextFontOptionsPanel fontOptionsPanel = new TextFontOptionsPanel();
    private final LayoutProfilesPanel layoutProfilesPanel = new LayoutProfilesPanel();
    private final ProfileSelectionPanel layoutSelectionPanel = new ProfileSelectionPanel(layoutProfilesPanel);
    private final ThemeProfilesPanel themeProfilesPanel = new ThemeProfilesPanel();
    private final ProfileSelectionPanel themeSelectionPanel = new ProfileSelectionPanel(themeProfilesPanel);
    private final ColorProfilesPanel colorProfilesPanel = new ColorProfilesPanel();
    private final ProfileSelectionPanel colorSelectionPanel = new ProfileSelectionPanel(colorProfilesPanel);

    public BinEdOptionsPanel() {
        initComponents();

        categoryModel.addElement(new CategoryItem("Editor", editorOptionsPanel));
        categoryModel.addElement(new CategoryItem("Status Panel", statusOptionsPanel));
        categoryModel.addElement(new CategoryItem("Code Area", codeAreaOptionsPanel));
        categoryModel.addElement(new CategoryItem("Font", fontOptionsPanel));
        categoryModel.addElement(new CategoryItem("Encoding", encodingOptionsPanel));
        categoryModel.addElement(new CategoryItem("Layout Profiles", layoutSelectionPanel));
        categoryModel.addElement(new CategoryItem("Theme Profiles", themeSelectionPanel));
        categoryModel.addElement(new CategoryItem("Colors Profiles", colorSelectionPanel));
        categoriesList.setModel(categoryModel);

        categoriesList.addListSelectionListener((ListSelectionEvent e) -> {
            int selectedIndex = categoriesList.getSelectedIndex();
            if (selectedIndex >= 0) {
                CategoryItem categoryItem = categoryModel.get(selectedIndex);
                currentCategoryPanel = categoryItem.getCategoryPanel();
                mainPane.setViewportView(currentCategoryPanel);
                mainPane.invalidate();
                revalidate();
                mainPane.repaint();
            }
        });
        categoriesList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component component = super.getListCellRendererComponent(list, ((CategoryItem) value).categoryName, index, isSelected, cellHasFocus);
                return component;
            }
        });
        categoriesList.setSelectedIndex(0);

        encodingOptionsPanel.setAddEncodingsOperation((List<String> usedEncodings, TextEncodingPanel.AddEncodingsResultListener resultListener) -> {
            final AddEncodingPanel addEncodingPanel = new AddEncodingPanel();
            addEncodingPanel.setUsedEncodings(usedEncodings);
            DefaultControlPanel encodingsControlPanel = new DefaultControlPanel(addEncodingPanel.getResourceBundle());
            JPanel dialogPanel = WindowUtils.createDialogPanel(addEncodingPanel, encodingsControlPanel);
            final DialogWrapper addEncodingDialog = WindowUtils.createDialog(dialogPanel, this, "Add Encodings", java.awt.Dialog.ModalityType.APPLICATION_MODAL);
            encodingsControlPanel.setHandler((DefaultControlHandler.ControlActionType actionType) -> {
                if (actionType == DefaultControlHandler.ControlActionType.OK) {
                	resultListener.result(addEncodingPanel.getEncodings());
                }

                addEncodingDialog.close();
            });
            addEncodingDialog.show();
        });

        fontOptionsPanel.setFontChangeAction(new TextFontOptionsPanel.FontChangeAction() {
            @Override
            public void changeFont(Font currentFont, TextFontOptionsPanel.FontChangeResult result) {
                final TextFontPanel fontPanel = new TextFontPanel();
                fontPanel.setStoredFont(currentFont);
                DefaultControlPanel controlPanel = new DefaultControlPanel();
                JPanel dialogPanel = WindowUtils.createDialogPanel(fontPanel, controlPanel);
                final DialogWrapper dialog = WindowUtils.createDialog(dialogPanel, BinEdOptionsPanel.this, "Set font", java.awt.Dialog.ModalityType.APPLICATION_MODAL);
                controlPanel.setHandler((DefaultControlHandler.ControlActionType actionType) -> {
                    if (actionType != DefaultControlHandler.ControlActionType.CANCEL) {
                        result.result(fontPanel.getStoredFont());
                    }

                    dialog.close();
                    dialog.dispose();
                });
                dialog.showCentered(BinEdOptionsPanel.this);
            }
        });

        layoutProfilesPanel.setAddProfileOperation((JComponent parentComponent, String profileName, LayoutProfilesPanel.ResultListener resultListener) -> {
            LayoutProfilePanel layoutProfilePanel = new LayoutProfilePanel();
            layoutProfilePanel.setLayoutProfile(new DefaultExtendedCodeAreaLayoutProfile());
            NamedProfilePanel namedProfilePanel = new NamedProfilePanel(layoutProfilePanel);
            namedProfilePanel.setProfileName(profileName);
            DefaultControlPanel controlPanel = new DefaultControlPanel();
            JPanel dialogPanel = WindowUtils.createDialogPanel(namedProfilePanel, controlPanel);

            final DialogWrapper dialog = WindowUtils.createDialog(dialogPanel, parentComponent, "Add Layout Profile", Dialog.ModalityType.APPLICATION_MODAL);
            controlPanel.setHandler((DefaultControlHandler.ControlActionType actionType) -> {
                if (actionType != DefaultControlHandler.ControlActionType.CANCEL) {
                    if (!isValidProfileName(namedProfilePanel.getProfileName())) {
                        JOptionPane.showMessageDialog(parentComponent, "Invalid profile name", "Profile Editation Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    resultListener.result(new LayoutProfilesPanel.LayoutProfile(
                            namedProfilePanel.getProfileName(), layoutProfilePanel.getLayoutProfile()
                    ));
                }

                dialog.close();
                dialog.dispose();
            });
            dialog.showCentered(parentComponent);
        });
        layoutProfilesPanel.setEditProfileOperation((JComponent parentComponent, LayoutProfilesPanel.LayoutProfile profileRecord, LayoutProfilesPanel.ResultListener resultListener) -> {
            LayoutProfilePanel layoutProfilePanel = new LayoutProfilePanel();
            NamedProfilePanel namedProfilePanel = new NamedProfilePanel(layoutProfilePanel);
            DefaultControlPanel controlPanel = new DefaultControlPanel();
            JPanel dialogPanel = WindowUtils.createDialogPanel(namedProfilePanel, controlPanel);

            final DialogWrapper dialog = WindowUtils.createDialog(dialogPanel, parentComponent, "Edit Layout Profile", Dialog.ModalityType.APPLICATION_MODAL);
            namedProfilePanel.setProfileName(profileRecord.getProfileName());
            layoutProfilePanel.setLayoutProfile(profileRecord.getLayoutProfile());
            controlPanel.setHandler((DefaultControlHandler.ControlActionType actionType) -> {
                if (actionType != DefaultControlHandler.ControlActionType.CANCEL) {
                    if (!isValidProfileName(namedProfilePanel.getProfileName())) {
                        JOptionPane.showMessageDialog(parentComponent, "Invalid profile name", "Profile Editation Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    resultListener.result(new LayoutProfilesPanel.LayoutProfile(
                            namedProfilePanel.getProfileName(), layoutProfilePanel.getLayoutProfile()
                    ));
                }

                dialog.close();
                dialog.dispose();
            });
            dialog.showCentered(parentComponent);
        });
        layoutProfilesPanel.setCopyProfileOperation((JComponent parentComponent, LayoutProfilesPanel.LayoutProfile profileRecord, LayoutProfilesPanel.ResultListener resultListener) -> {
            LayoutProfilePanel layoutProfilePanel = new LayoutProfilePanel();
            layoutProfilePanel.setLayoutProfile(new DefaultExtendedCodeAreaLayoutProfile());
            NamedProfilePanel namedProfilePanel = new NamedProfilePanel(layoutProfilePanel);
            DefaultControlPanel controlPanel = new DefaultControlPanel();
            JPanel dialogPanel = WindowUtils.createDialogPanel(namedProfilePanel, controlPanel);

            final DialogWrapper dialog = WindowUtils.createDialog(dialogPanel, parentComponent, "Copy Layout Profile", Dialog.ModalityType.APPLICATION_MODAL);
            layoutProfilePanel.setLayoutProfile(profileRecord.getLayoutProfile());
            controlPanel.setHandler((DefaultControlHandler.ControlActionType actionType) -> {
                if (actionType != DefaultControlHandler.ControlActionType.CANCEL) {
                    if (!isValidProfileName(namedProfilePanel.getProfileName())) {
                        JOptionPane.showMessageDialog(parentComponent, "Invalid profile name", "Profile Editation Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    resultListener.result(new LayoutProfilesPanel.LayoutProfile(
                            namedProfilePanel.getProfileName(), layoutProfilePanel.getLayoutProfile()
                    ));
                }

                dialog.close();
                dialog.dispose();
            });
            dialog.showCentered(parentComponent);
        });
        layoutProfilesPanel.setTemplateProfileOperation((JComponent parentComponent, LayoutProfilesPanel.ResultListener resultListener) -> {
            LayoutTemplatePanel layoutTemplatePanel = new LayoutTemplatePanel();
            NamedProfilePanel namedProfilePanel = new NamedProfilePanel(layoutTemplatePanel);
            namedProfilePanel.setProfileName("");
            layoutTemplatePanel.addListSelectionListener((e) -> {
                LayoutTemplatePanel.LayoutProfile selectedTemplate = layoutTemplatePanel.getSelectedTemplate();
                namedProfilePanel.setProfileName(selectedTemplate != null ? selectedTemplate.getProfileName() : "");
            });
            DefaultControlPanel controlPanel = new DefaultControlPanel();
            JPanel dialogPanel = WindowUtils.createDialogPanel(namedProfilePanel, controlPanel);

            final DialogWrapper dialog = WindowUtils.createDialog(dialogPanel, parentComponent, "Add Layout Template", Dialog.ModalityType.APPLICATION_MODAL);
//            WindowUtils.addHeaderPanel(dialog.getWindow(), layoutTemplatePanel.getClass(), layoutTemplatePanel.getResourceBundle());
            controlPanel.setHandler((DefaultControlHandler.ControlActionType actionType) -> {
                if (actionType != DefaultControlHandler.ControlActionType.CANCEL) {
                    if (!isValidProfileName(namedProfilePanel.getProfileName())) {
                        JOptionPane.showMessageDialog(parentComponent, "Invalid profile name", "Profile Template Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    LayoutTemplatePanel.LayoutProfile selectedTemplate = layoutTemplatePanel.getSelectedTemplate();
                    if (selectedTemplate == null) {
                        JOptionPane.showMessageDialog(parentComponent, "No template selected", "Profile Template Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    resultListener.result(new LayoutProfilesPanel.LayoutProfile(
                            namedProfilePanel.getProfileName(), selectedTemplate.getLayoutProfile()
                    ));
                }

                dialog.close();
                dialog.dispose();
            });
            dialog.showCentered(parentComponent);
        });

        themeProfilesPanel.setAddProfileOperation((JComponent parentComponent, String profileName, ThemeProfilesPanel.ResultListener resultListener) -> {
            ThemeProfilePanel themeProfilePanel = new ThemeProfilePanel();
            themeProfilePanel.setThemeProfile(new ExtendedCodeAreaThemeProfile());
            NamedProfilePanel namedProfilePanel = new NamedProfilePanel(themeProfilePanel);
            namedProfilePanel.setProfileName(profileName);
            DefaultControlPanel controlPanel = new DefaultControlPanel();
            JPanel dialogPanel = WindowUtils.createDialogPanel(namedProfilePanel, controlPanel);

            final DialogWrapper dialog = WindowUtils.createDialog(dialogPanel, parentComponent, "Add Theme Profile", Dialog.ModalityType.APPLICATION_MODAL);
            controlPanel.setHandler((DefaultControlHandler.ControlActionType actionType) -> {
                if (actionType != DefaultControlHandler.ControlActionType.CANCEL) {
                    if (!isValidProfileName(namedProfilePanel.getProfileName())) {
                        JOptionPane.showMessageDialog(parentComponent, "Invalid profile name", "Profile Editation Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    resultListener.result(new ThemeProfilesPanel.ThemeProfile(
                            namedProfilePanel.getProfileName(), themeProfilePanel.getThemeProfile()
                    ));
                }

                dialog.close();
                dialog.dispose();
            });
            dialog.showCentered(parentComponent);
        });
        themeProfilesPanel.setEditProfileOperation((JComponent parentComponent, ThemeProfilesPanel.ThemeProfile profileRecord, ThemeProfilesPanel.ResultListener resultListener) -> {
            ThemeProfilePanel themeProfilePanel = new ThemeProfilePanel();
            NamedProfilePanel namedProfilePanel = new NamedProfilePanel(themeProfilePanel);
            DefaultControlPanel controlPanel = new DefaultControlPanel();
            JPanel dialogPanel = WindowUtils.createDialogPanel(namedProfilePanel, controlPanel);

            final DialogWrapper dialog = WindowUtils.createDialog(dialogPanel, parentComponent, "Edit Theme Profile", Dialog.ModalityType.APPLICATION_MODAL);
            namedProfilePanel.setProfileName(profileRecord.getProfileName());
            themeProfilePanel.setThemeProfile(profileRecord.getThemeProfile());
            controlPanel.setHandler((DefaultControlHandler.ControlActionType actionType) -> {
                if (actionType != DefaultControlHandler.ControlActionType.CANCEL) {
                    if (!isValidProfileName(namedProfilePanel.getProfileName())) {
                        JOptionPane.showMessageDialog(parentComponent, "Invalid profile name", "Profile Editation Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    resultListener.result(new ThemeProfilesPanel.ThemeProfile(
                            namedProfilePanel.getProfileName(), themeProfilePanel.getThemeProfile()
                    ));
                }

                dialog.close();
                dialog.dispose();
            });
            dialog.showCentered(parentComponent);
        });
        themeProfilesPanel.setCopyProfileOperation((JComponent parentComponent, ThemeProfilesPanel.ThemeProfile profileRecord, ThemeProfilesPanel.ResultListener resultListener) -> {
            ThemeProfilePanel themeProfilePanel = new ThemeProfilePanel();
            themeProfilePanel.setThemeProfile(new ExtendedCodeAreaThemeProfile());
            NamedProfilePanel namedProfilePanel = new NamedProfilePanel(themeProfilePanel);
            DefaultControlPanel controlPanel = new DefaultControlPanel();
            JPanel dialogPanel = WindowUtils.createDialogPanel(namedProfilePanel, controlPanel);

            final DialogWrapper dialog = WindowUtils.createDialog(dialogPanel, parentComponent, "Copy Theme Profile", Dialog.ModalityType.APPLICATION_MODAL);
            themeProfilePanel.setThemeProfile(profileRecord.getThemeProfile());
            controlPanel.setHandler((DefaultControlHandler.ControlActionType actionType) -> {
                if (actionType != DefaultControlHandler.ControlActionType.CANCEL) {
                    if (!isValidProfileName(namedProfilePanel.getProfileName())) {
                        JOptionPane.showMessageDialog(parentComponent, "Invalid profile name", "Profile Editation Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    resultListener.result(new ThemeProfilesPanel.ThemeProfile(
                            namedProfilePanel.getProfileName(), themeProfilePanel.getThemeProfile()
                    ));
                }

                dialog.close();
                dialog.dispose();
            });
            dialog.showCentered(parentComponent);
        });
        themeProfilesPanel.setTemplateProfileOperation((JComponent parentComponent, ThemeProfilesPanel.ResultListener resultListener) -> {
            ThemeTemplatePanel themeTemplatePanel = new ThemeTemplatePanel();
            NamedProfilePanel namedProfilePanel = new NamedProfilePanel(themeTemplatePanel);
            namedProfilePanel.setProfileName("");
            themeTemplatePanel.addListSelectionListener((e) -> {
                ThemeTemplatePanel.ThemeProfile selectedTemplate = themeTemplatePanel.getSelectedTemplate();
                namedProfilePanel.setProfileName(selectedTemplate != null ? selectedTemplate.getProfileName() : "");
            });
            DefaultControlPanel controlPanel = new DefaultControlPanel();
            JPanel dialogPanel = WindowUtils.createDialogPanel(namedProfilePanel, controlPanel);

            final DialogWrapper dialog = WindowUtils.createDialog(dialogPanel, parentComponent, "Add Theme Template", Dialog.ModalityType.APPLICATION_MODAL);
//            WindowUtils.addHeaderPanel(dialog.getWindow(), themeTemplatePanel.getClass(), themeTemplatePanel.getResourceBundle());
            controlPanel.setHandler((DefaultControlHandler.ControlActionType actionType) -> {
                if (actionType != DefaultControlHandler.ControlActionType.CANCEL) {
                    if (!isValidProfileName(namedProfilePanel.getProfileName())) {
                        JOptionPane.showMessageDialog(parentComponent, "Invalid profile name", "Profile Template Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    ThemeTemplatePanel.ThemeProfile selectedTemplate = themeTemplatePanel.getSelectedTemplate();
                    if (selectedTemplate == null) {
                        JOptionPane.showMessageDialog(parentComponent, "No template selected", "Profile Template Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    resultListener.result(new ThemeProfilesPanel.ThemeProfile(
                            namedProfilePanel.getProfileName(), selectedTemplate.getThemeProfile()
                    ));
                }

                dialog.close();
                dialog.dispose();
            });
            dialog.showCentered(parentComponent);
        });

        colorProfilesPanel.setAddProfileOperation((JComponent parentComponent, String profileName, ColorProfilesPanel.ResultListener resultListener) -> {
            ColorProfilePanel colorProfilePanel = new ColorProfilePanel();
            colorProfilePanel.setColorProfile(new ExtendedCodeAreaColorProfile());
            NamedProfilePanel namedProfilePanel = new NamedProfilePanel(colorProfilePanel);
            namedProfilePanel.setProfileName(profileName);
            DefaultControlPanel controlPanel = new DefaultControlPanel();
            JPanel dialogPanel = WindowUtils.createDialogPanel(namedProfilePanel, controlPanel);

            final DialogWrapper dialog = WindowUtils.createDialog(dialogPanel, parentComponent, "Add Colors Profile", Dialog.ModalityType.APPLICATION_MODAL);
            controlPanel.setHandler((DefaultControlHandler.ControlActionType actionType) -> {
                if (actionType != DefaultControlHandler.ControlActionType.CANCEL) {
                    if (!isValidProfileName(namedProfilePanel.getProfileName())) {
                        JOptionPane.showMessageDialog(parentComponent, "Invalid profile name", "Profile Editation Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    resultListener.result(new ColorProfilesPanel.ColorProfile(
                            namedProfilePanel.getProfileName(), colorProfilePanel.getColorProfile()
                    ));
                }

                dialog.close();
                dialog.dispose();
            });
            dialog.showCentered(parentComponent);
        });
        colorProfilesPanel.setEditProfileOperation((JComponent parentComponent, ColorProfilesPanel.ColorProfile profileRecord, ColorProfilesPanel.ResultListener resultListener) -> {
            ColorProfilePanel colorProfilePanel = new ColorProfilePanel();
            NamedProfilePanel namedProfilePanel = new NamedProfilePanel(colorProfilePanel);
            DefaultControlPanel controlPanel = new DefaultControlPanel();
            JPanel dialogPanel = WindowUtils.createDialogPanel(namedProfilePanel, controlPanel);

            final DialogWrapper dialog = WindowUtils.createDialog(dialogPanel, parentComponent, "Edit Colors Profile", Dialog.ModalityType.APPLICATION_MODAL);
            namedProfilePanel.setProfileName(profileRecord.getProfileName());
            colorProfilePanel.setColorProfile(profileRecord.getColorProfile());
            controlPanel.setHandler((DefaultControlHandler.ControlActionType actionType) -> {
                if (actionType != DefaultControlHandler.ControlActionType.CANCEL) {
                    if (!isValidProfileName(namedProfilePanel.getProfileName())) {
                        JOptionPane.showMessageDialog(parentComponent, "Invalid profile name", "Profile Editation Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    resultListener.result(new ColorProfilesPanel.ColorProfile(
                            namedProfilePanel.getProfileName(), colorProfilePanel.getColorProfile()
                    ));
                }

                dialog.close();
                dialog.dispose();
            });
            dialog.showCentered(parentComponent);
        });
        colorProfilesPanel.setCopyProfileOperation((JComponent parentComponent, ColorProfilesPanel.ColorProfile profileRecord, ColorProfilesPanel.ResultListener resultListener) -> {
            ColorProfilePanel colorProfilePanel = new ColorProfilePanel();
            colorProfilePanel.setColorProfile(new ExtendedCodeAreaColorProfile());
            NamedProfilePanel namedProfilePanel = new NamedProfilePanel(colorProfilePanel);
            DefaultControlPanel controlPanel = new DefaultControlPanel();
            JPanel dialogPanel = WindowUtils.createDialogPanel(namedProfilePanel, controlPanel);

            final DialogWrapper dialog = WindowUtils.createDialog(dialogPanel, parentComponent, "Copy Colors Profile", Dialog.ModalityType.APPLICATION_MODAL);
            colorProfilePanel.setColorProfile(profileRecord.getColorProfile());
            controlPanel.setHandler((DefaultControlHandler.ControlActionType actionType) -> {
                if (actionType != DefaultControlHandler.ControlActionType.CANCEL) {
                    if (!isValidProfileName(namedProfilePanel.getProfileName())) {
                        JOptionPane.showMessageDialog(parentComponent, "Invalid profile name", "Profile Editation Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    resultListener.result(new ColorProfilesPanel.ColorProfile(
                            namedProfilePanel.getProfileName(), colorProfilePanel.getColorProfile()
                    ));
                }

                dialog.close();
                dialog.dispose();
            });
            dialog.showCentered(parentComponent);
        });
        colorProfilesPanel.setTemplateProfileOperation((JComponent parentComponent, ColorProfilesPanel.ResultListener resultListener) -> {
            ColorTemplatePanel colorTemplatePanel = new ColorTemplatePanel();
            NamedProfilePanel namedProfilePanel = new NamedProfilePanel(colorTemplatePanel);
            namedProfilePanel.setProfileName("");
            colorTemplatePanel.addListSelectionListener((e) -> {
                ColorTemplatePanel.ColorProfile selectedTemplate = colorTemplatePanel.getSelectedTemplate();
                namedProfilePanel.setProfileName(selectedTemplate != null ? selectedTemplate.getProfileName() : "");
            });
            DefaultControlPanel controlPanel = new DefaultControlPanel();
            JPanel dialogPanel = WindowUtils.createDialogPanel(namedProfilePanel, controlPanel);

            final DialogWrapper dialog = WindowUtils.createDialog(dialogPanel, parentComponent, "Add Colors Template", Dialog.ModalityType.APPLICATION_MODAL);
//            WindowUtils.addHeaderPanel(dialog.getWindow(), colorTemplatePanel.getClass(), colorTemplatePanel.getResourceBundle());
            controlPanel.setHandler((DefaultControlHandler.ControlActionType actionType) -> {
                if (actionType != DefaultControlHandler.ControlActionType.CANCEL) {
                    if (!isValidProfileName(namedProfilePanel.getProfileName())) {
                        JOptionPane.showMessageDialog(parentComponent, "Invalid profile name", "Profile Template Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    ColorTemplatePanel.ColorProfile selectedTemplate = colorTemplatePanel.getSelectedTemplate();
                    if (selectedTemplate == null) {
                        JOptionPane.showMessageDialog(parentComponent, "No template selected", "Profile Template Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    resultListener.result(new ColorProfilesPanel.ColorProfile(
                            namedProfilePanel.getProfileName(), selectedTemplate.getColorProfile()
                    ));
                }

                dialog.close();
                dialog.dispose();
            });
            dialog.showCentered(parentComponent);
        });
    }

    public void setPreferences(BinaryEditorPreferences preferences) {
        this.preferences = preferences;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        categoriesLabel = new javax.swing.JLabel();
        categoriesScrollPane = new javax.swing.JScrollPane();
        categoriesList = new javax.swing.JList<>();
        mainPane = new javax.swing.JScrollPane();

        categoriesLabel.setText("Categories:");

        categoriesList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        categoriesScrollPane.setViewportView(categoriesList);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(categoriesLabel)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addComponent(categoriesScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(mainPane, javax.swing.GroupLayout.DEFAULT_SIZE, 20, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(categoriesLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(categoriesScrollPane)
                    .addComponent(mainPane)))
        );
        setPreferredSize(new Dimension(700, 500));
    }// </editor-fold>//GEN-END:initComponents

    public void loadFromPreferences() {
        editorOptions.loadFromPreferences(preferences.getEditorPreferences());
        statusOptions.loadFromPreferences(preferences.getStatusPreferences());
        codeAreaOptions.loadFromPreferences(preferences.getCodeAreaPreferences());
        encodingOptions.loadFromPreferences(preferences.getEncodingPreferences());
        fontOptions.loadFromPreferences(preferences.getFontPreferences());
        layoutOptions.loadFromPreferences(preferences.getLayoutPreferences());
        colorOptions.loadFromPreferences(preferences.getColorPreferences());
        themeOptions.loadFromPreferences(preferences.getThemePreferences());

        editorOptionsPanel.loadFromOptions(editorOptions);
        statusOptionsPanel.loadFromOptions(statusOptions);
        codeAreaOptionsPanel.loadFromOptions(codeAreaOptions);
        encodingOptionsPanel.loadFromOptions(encodingOptions);
        fontOptionsPanel.loadFromOptions(fontOptions);

        layoutProfilesPanel.loadFromOptions(layoutOptions);
        layoutSelectionPanel.setDefaultProfile(layoutOptions.getSelectedProfile());
        colorProfilesPanel.loadFromOptions(colorOptions);
        colorSelectionPanel.setDefaultProfile(colorOptions.getSelectedProfile());
        themeProfilesPanel.loadFromOptions(themeOptions);
        themeSelectionPanel.setDefaultProfile(themeOptions.getSelectedProfile());
    }

    public void saveToPreferences() {
        applyToOptions();

        editorOptions.saveToPreferences(preferences.getEditorPreferences());
        statusOptions.saveToPreferences(preferences.getStatusPreferences());
        codeAreaOptions.saveToPreferences(preferences.getCodeAreaPreferences());
        encodingOptions.saveToPreferences(preferences.getEncodingPreferences());
        fontOptions.saveToPreferences(preferences.getFontPreferences());

        layoutOptions.saveToPreferences(preferences.getLayoutPreferences());
        colorOptions.saveToPreferences(preferences.getColorPreferences());
        themeOptions.saveToPreferences(preferences.getThemePreferences());
    }

    public void applyToOptions() {
        editorOptionsPanel.saveToOptions(editorOptions);
        statusOptionsPanel.saveToOptions(statusOptions);
        codeAreaOptionsPanel.saveToOptions(codeAreaOptions);
        encodingOptionsPanel.saveToOptions(encodingOptions);
        fontOptionsPanel.saveToOptions(fontOptions);

        layoutProfilesPanel.saveToOptions(layoutOptions);
        layoutOptions.setSelectedProfile(layoutSelectionPanel.getDefaultProfile());
        colorProfilesPanel.saveToOptions(colorOptions);
        colorOptions.setSelectedProfile(colorSelectionPanel.getDefaultProfile());
        themeProfilesPanel.saveToOptions(themeOptions);
        themeOptions.setSelectedProfile(themeSelectionPanel.getDefaultProfile());
    }

    boolean valid() {
        // TODO check whether form is consistent and complete
        return true;
    }

    public void setTextFontService(TextFontService textFontService) {
        fontOptionsPanel.setTextFontService(textFontService);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel categoriesLabel;
    private javax.swing.JList<CategoryItem> categoriesList;
    private javax.swing.JScrollPane categoriesScrollPane;
    private javax.swing.JScrollPane mainPane;
    // End of variables declaration//GEN-END:variables

    @Nonnull
    @Override
    public CodeAreaOptions getCodeAreaOptions() {
        return codeAreaOptions;
    }

    @Nonnull
    @Override
    public TextEncodingOptionsImpl getEncodingOptions() {
        return encodingOptions;
    }

    @Nonnull
    @Override
    public EditorOptions getEditorOptions() {
        return editorOptions;
    }

    @Nonnull
    @Override
    public TextFontOptions getFontOptions() {
        return fontOptions;
    }

    @Nonnull
    @Override
    public StatusOptions getStatusOptions() {
        return statusOptions;
    }

    @Nonnull
    @Override
    public CodeAreaLayoutOptions getLayoutOptions() {
        return layoutOptions;
    }

    @Nonnull
    @Override
    public CodeAreaColorOptions getColorOptions() {
        return colorOptions;
    }

    @Nonnull
    @Override
    public CodeAreaThemeOptions getThemeOptions() {
        return themeOptions;
    }
    
    private boolean isValidProfileName(@Nullable String profileName) {
        return profileName != null && !"".equals(profileName.trim());
    }

    private static class CategoryItem {

        String categoryName;
        JPanel categoryPanel;

        public CategoryItem(String categoryName, JPanel categoryPanel) {
            this.categoryName = categoryName;
            this.categoryPanel = categoryPanel;
        }

        public String getCategoryName() {
            return categoryName;
        }

        public JPanel getCategoryPanel() {
            return categoryPanel;
        }
    }
}
