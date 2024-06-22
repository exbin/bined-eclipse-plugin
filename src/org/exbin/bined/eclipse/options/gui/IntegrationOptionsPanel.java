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
package org.exbin.bined.eclipse.options.gui;

import java.awt.Component;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JList;

import org.exbin.bined.eclipse.options.impl.IntegrationOptionsImpl;
import org.exbin.framework.options.model.LanguageRecord;
import org.exbin.framework.utils.LanguageUtils;
import org.exbin.framework.utils.WindowUtils;
import org.exbin.framework.options.api.OptionsComponent;
import org.exbin.framework.options.api.OptionsModifiedListener;

/**
 * Integration preference parameters panel.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class IntegrationOptionsPanel extends javax.swing.JPanel implements OptionsComponent<IntegrationOptionsImpl> {

    private final java.util.ResourceBundle resourceBundle = LanguageUtils.getResourceBundleByClass(IntegrationOptionsPanel.class);
    private OptionsModifiedListener optionsModifiedListener;
    private String defaultLocaleName = "";

    public IntegrationOptionsPanel() {
        initComponents();
    }

    @Nonnull
    @Override
    public ResourceBundle getResourceBundle() {
        return resourceBundle;
    }

    @Override
    public void saveToOptions(IntegrationOptionsImpl options) {
        options.setLanguageLocale(((LanguageRecord) languageComboBox.getSelectedItem()).getLocale());
        options.setRegisterOpenWithAsBinary(openWithAsBinaryCheckBox.isSelected());
        options.setRegisterDebugViewAsBinary(openAsBinaryInDebugViewCheckBox.isSelected());
        options.setRegisterByteToByteDiffTool(byteToByteDiffToolCheckBox.isSelected());
        options.setRegisterDefaultPopupMenu(registerDefaultPopupCheckBox.isSelected());
        options.setChangeVisualTheme(visualThemeCheckBox.isSelected());
        options.setVisualTheme((String) visualThemeComboBox.getSelectedItem());
    }

    @Override
    public void loadFromOptions(IntegrationOptionsImpl options) {
        Locale languageLocale = options.getLanguageLocale();
        ComboBoxModel<LanguageRecord> languageComboBoxModel = languageComboBox.getModel();
        for (int i = 0; i < languageComboBoxModel.getSize(); i++) {
            LanguageRecord languageRecord = languageComboBoxModel.getElementAt(i);
            if (languageLocale.equals(languageRecord.getLocale())) {
                languageComboBox.setSelectedIndex(i);
                break;
            }
        }
        openWithAsBinaryCheckBox.setSelected(options.isRegisterOpenWithAsBinary());
        openAsBinaryInDebugViewCheckBox.setSelected(options.isRegisterDebugViewAsBinary());
        byteToByteDiffToolCheckBox.setSelected(options.isRegisterByteToByteDiffTool());
        registerDefaultPopupCheckBox.setSelected(options.isRegisterDefaultPopupMenu());
        visualThemeCheckBox.setSelected(options.isChangeVisualTheme());
        visualThemeComboBox.setSelectedIndex(findMatchingElement(visualThemeComboBox.getModel(), options.getVisualTheme()));
    }

    public void setLanguageLocales(List<LanguageRecord> languageLocales) {
        DefaultComboBoxModel<LanguageRecord> languageComboBoxModel = new DefaultComboBoxModel<>();
        languageLocales.forEach(languageComboBoxModel::addElement);
        languageComboBox.setModel(languageComboBoxModel);
        languageComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                DefaultListCellRenderer renderer = (DefaultListCellRenderer) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                LanguageRecord record = (LanguageRecord) value;
                String languageText = record.getText();
                if ("".equals(languageText)) {
                    languageText = defaultLocaleName;
                }
                renderer.setText(languageText);
                ImageIcon flag = record.getFlag();
                if (flag != null) {
                    renderer.setIcon(flag);
                }
                return renderer;
            }
        });
    }

    public void setDefaultLocaleName(String defaultLocaleName) {
        this.defaultLocaleName = defaultLocaleName;
    }

    public void setThemes(List<String> themeKeys, List<String> themeNames) {
        DefaultComboBoxModel<String> themeComboBoxModel = new DefaultComboBoxModel<>();
        themeKeys.forEach((themeKey) -> {
            themeComboBoxModel.addElement(themeKey);
        });
        visualThemeComboBox.setModel(themeComboBoxModel);
        visualThemeComboBox.setRenderer(new DefaultListCellRenderer() {
            @Nonnull
            @Override
            public Component getListCellRendererComponent(JList<?> list, @Nullable Object value, int index, boolean isSelected, boolean cellHasFocus) {
                if (index >= 0) {
                    return super.getListCellRendererComponent(list, themeNames.get(index), index, isSelected, cellHasFocus);
                }
                int selectedIndex = visualThemeComboBox.getSelectedIndex();
                return super.getListCellRendererComponent(list, selectedIndex >= 0 ? themeNames.get(selectedIndex) : value, index, isSelected, cellHasFocus);
            }
        });
    }

    private static int findMatchingElement(ComboBoxModel<String> model, String value) {
        for (int i = 0; i < model.getSize(); i++) {
            if (value.equals(model.getElementAt(i))) {
                return i;
            }
        }
        return -1;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        languageLabel = new javax.swing.JLabel();
        languageComboBox = new javax.swing.JComboBox<>();
        openWithAsBinaryCheckBox = new javax.swing.JCheckBox();
        openAsBinaryInDebugViewCheckBox = new javax.swing.JCheckBox();
        byteToByteDiffToolCheckBox = new javax.swing.JCheckBox();
        visualThemeCheckBox = new javax.swing.JCheckBox();
        visualThemeLabel = new javax.swing.JLabel();
        visualThemeComboBox = new javax.swing.JComboBox<>();
        registerDefaultPopupCheckBox = new javax.swing.JCheckBox();

        languageLabel.setText(resourceBundle.getString("languageLabel.text") + " *"); // NOI18N

        languageComboBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                languageComboBoxItemStateChanged(evt);
            }
        });

        openWithAsBinaryCheckBox.setSelected(true);
        openWithAsBinaryCheckBox.setText(resourceBundle.getString("openWithAsBinaryCheckBox.text")); // NOI18N
        openWithAsBinaryCheckBox.setEnabled(false);
        openWithAsBinaryCheckBox.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                openWithAsBinaryCheckBoxStateChanged(evt);
            }
        });

        openAsBinaryInDebugViewCheckBox.setSelected(true);
        openAsBinaryInDebugViewCheckBox.setText(resourceBundle.getString("openAsBinaryInDebugViewCheckBox.text")); // NOI18N
        openAsBinaryInDebugViewCheckBox.setEnabled(false);
        openAsBinaryInDebugViewCheckBox.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                openAsBinaryInDebugViewCheckBoxStateChanged(evt);
            }
        });
        openAsBinaryInDebugViewCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openAsBinaryInDebugViewCheckBoxActionPerformed(evt);
            }
        });

        byteToByteDiffToolCheckBox.setText(resourceBundle.getString("byteToByteDiffToolCheckBox.text")); // NOI18N
        byteToByteDiffToolCheckBox.setEnabled(false);
        byteToByteDiffToolCheckBox.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                byteToByteDiffToolCheckBoxStateChanged(evt);
            }
        });
        byteToByteDiffToolCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                byteToByteDiffToolCheckBoxActionPerformed(evt);
            }
        });

        visualThemeCheckBox.setText(resourceBundle.getString("visualThemeCheckBox.text") + " *"); // NOI18N
        visualThemeCheckBox.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                visualThemeCheckBoxStateChanged(evt);
            }
        });

        visualThemeLabel.setText(resourceBundle.getString("visualThemeLabel.text")); // NOI18N

        visualThemeComboBox.setEnabled(false);
        visualThemeComboBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                visualThemeComboBoxjComboBoxItemStateChanged(evt);
            }
        });

        registerDefaultPopupCheckBox.setText(resourceBundle.getString("registerDefaultPopupCheckBox.text")); // NOI18N
        registerDefaultPopupCheckBox.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                registerDefaultPopupCheckBoxStateChanged(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(openWithAsBinaryCheckBox, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(openAsBinaryInDebugViewCheckBox, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(languageComboBox, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(byteToByteDiffToolCheckBox, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(registerDefaultPopupCheckBox, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(visualThemeCheckBox, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(visualThemeComboBox, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(languageLabel)
                            .addComponent(visualThemeLabel))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(languageLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(languageComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(openWithAsBinaryCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(openAsBinaryInDebugViewCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(byteToByteDiffToolCheckBox)
                .addGap(18, 18, 18)
                .addComponent(visualThemeCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(visualThemeLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(visualThemeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(registerDefaultPopupCheckBox)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void openWithAsBinaryCheckBoxStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_openWithAsBinaryCheckBoxStateChanged
        notifyModified();
    }//GEN-LAST:event_openWithAsBinaryCheckBoxStateChanged

    private void openAsBinaryInDebugViewCheckBoxStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_openAsBinaryInDebugViewCheckBoxStateChanged
        notifyModified();
    }//GEN-LAST:event_openAsBinaryInDebugViewCheckBoxStateChanged

    private void registerDefaultPopupCheckBoxStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_registerDefaultPopupCheckBoxStateChanged
        notifyModified();
    }//GEN-LAST:event_registerDefaultPopupCheckBoxStateChanged

    private void languageComboBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_languageComboBoxItemStateChanged
        notifyModified();
    }//GEN-LAST:event_languageComboBoxItemStateChanged

    private void openAsBinaryInDebugViewCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openAsBinaryInDebugViewCheckBoxActionPerformed
        notifyModified();
    }//GEN-LAST:event_openAsBinaryInDebugViewCheckBoxActionPerformed

    private void visualThemeComboBoxjComboBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_visualThemeComboBoxjComboBoxItemStateChanged
        notifyModified();
    }//GEN-LAST:event_visualThemeComboBoxjComboBoxItemStateChanged

    private void visualThemeCheckBoxStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_visualThemeCheckBoxStateChanged
        notifyModified();
        visualThemeComboBox.setEnabled(visualThemeCheckBox.isSelected());
    }//GEN-LAST:event_visualThemeCheckBoxStateChanged

    private void byteToByteDiffToolCheckBoxStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_byteToByteDiffToolCheckBoxStateChanged
        notifyModified();
    }//GEN-LAST:event_byteToByteDiffToolCheckBoxStateChanged

    private void byteToByteDiffToolCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_byteToByteDiffToolCheckBoxActionPerformed
        notifyModified();
    }//GEN-LAST:event_byteToByteDiffToolCheckBoxActionPerformed

    /**
     * Test method for this panel.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        WindowUtils.invokeDialog(new IntegrationOptionsPanel());
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox byteToByteDiffToolCheckBox;
    private javax.swing.JComboBox<LanguageRecord> languageComboBox;
    private javax.swing.JLabel languageLabel;
    private javax.swing.JCheckBox openAsBinaryInDebugViewCheckBox;
    private javax.swing.JCheckBox openWithAsBinaryCheckBox;
    private javax.swing.JCheckBox registerDefaultPopupCheckBox;
    private javax.swing.JCheckBox visualThemeCheckBox;
    private javax.swing.JComboBox<String> visualThemeComboBox;
    private javax.swing.JLabel visualThemeLabel;
    // End of variables declaration//GEN-END:variables

    private void notifyModified() {
        if (optionsModifiedListener != null) {
            optionsModifiedListener.wasModified();
        }
    }

    @Override
    public void setOptionsModifiedListener(OptionsModifiedListener listener) {
        optionsModifiedListener = listener;
    }
}
