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
package org.exbin.bined.eclipse.panel;

import java.awt.event.ActionListener;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.bined.CodeType;
import org.exbin.bined.operation.undo.BinaryDataUndoHandler;
import org.exbin.bined.swing.extended.ExtCodeArea;
import org.exbin.framework.bined.preferences.BinaryEditorPreferences;
import org.exbin.framework.gui.utils.LanguageUtils;

/**
 * Hexadecimal editor toolbar panel.
 *
 * @version 0.2.0 2019/08/10
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class BinEdToolbarPanel extends javax.swing.JPanel {

    private final java.util.ResourceBundle resourceBundle = LanguageUtils.getResourceBundleByClass(BinEdToolbarPanel.class);

    private final BinaryEditorPreferences preferences;
    private final ExtCodeArea codeArea;
    private final BinaryDataUndoHandler undoHandler;
    private ActionListener saveAction = null;

//    private JSplitButton codeTypeButton;
    public BinEdToolbarPanel(BinaryEditorPreferences preferences, ExtCodeArea codeArea, BinaryDataUndoHandler undoHandler) {
        this.preferences = preferences;
        this.codeArea = codeArea;
        this.undoHandler = undoHandler;
        initComponents();
        init();
    }

    private void init() {
//        codeTypeButton = new JSplitButton("HEX");
//        codeTypeButton.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                throw new UnsupportedOperationException("Not supported yet.");
//            }
//        });
//        controlToolBar.add(codeTypeButton);
    }

    public void applyFromCodeArea() {
        codeTypeComboBox.setSelectedIndex(codeArea.getCodeType().ordinal());
        showUnprintablesToggleButton.setSelected(codeArea.isShowUnprintables());
    }

    public void loadFromPreferences() {
        codeTypeComboBox.setSelectedIndex(preferences.getCodeAreaPreferences().getCodeType().ordinal());
        showUnprintablesToggleButton.setSelected(preferences.getCodeAreaPreferences().isShowUnprintables());
    }

    public void updateUndoState() {
        undoEditButton.setEnabled(undoHandler.canUndo());
        redoEditButton.setEnabled(undoHandler.canRedo());
    }

    public void updateModified(boolean modified) {
        saveFileButton.setEnabled(modified);
    }

    public void setSaveAction(ActionListener saveAction) {
        this.saveAction = saveAction;
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        controlToolBar = new javax.swing.JToolBar();
        saveFileButton = new javax.swing.JButton();
        separator1 = new javax.swing.JToolBar.Separator();
        undoEditButton = new javax.swing.JButton();
        redoEditButton = new javax.swing.JButton();
        separator2 = new javax.swing.JToolBar.Separator();
        showUnprintablesToggleButton = new javax.swing.JToggleButton();
        separator3 = new javax.swing.JToolBar.Separator();
        codeTypeComboBox = new javax.swing.JComboBox<>();

        controlToolBar.setBorder(null);
        controlToolBar.setFloatable(false);
        controlToolBar.setRollover(true);

        saveFileButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/exbin/bined/eclipse/resources/icons/document-save.png"))); // NOI18N
        saveFileButton.setToolTipText(resourceBundle.getString("saveFileButton.toolTipText")); // NOI18N
        saveFileButton.setEnabled(false);
        saveFileButton.setFocusable(false);
        saveFileButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        saveFileButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        saveFileButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveFileButtonActionPerformed(evt);
            }
        });
        controlToolBar.add(saveFileButton);
        controlToolBar.add(separator1);

        undoEditButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/exbin/bined/eclipse/resources/icons/edit-undo.png"))); // NOI18N
        undoEditButton.setToolTipText(resourceBundle.getString("undoEditButton.toolTipText")); // NOI18N
        undoEditButton.setFocusable(false);
        undoEditButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        undoEditButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        undoEditButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                undoEditButtonActionPerformed(evt);
            }
        });
        controlToolBar.add(undoEditButton);

        redoEditButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/exbin/bined/eclipse/resources/icons/edit-redo.png"))); // NOI18N
        redoEditButton.setToolTipText(resourceBundle.getString("redoEditButton.toolTipText")); // NOI18N
        redoEditButton.setFocusable(false);
        redoEditButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        redoEditButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        redoEditButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                redoEditButtonActionPerformed(evt);
            }
        });
        controlToolBar.add(redoEditButton);
        controlToolBar.add(separator2);

        showUnprintablesToggleButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/exbin/bined/eclipse/resources/icons/insert-pilcrow.png"))); // NOI18N
        showUnprintablesToggleButton.setToolTipText(resourceBundle.getString("showUnprintablesToggleButton.toolTipText")); // NOI18N
        showUnprintablesToggleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showUnprintablesToggleButtonActionPerformed(evt);
            }
        });
        controlToolBar.add(showUnprintablesToggleButton);
        controlToolBar.add(separator3);

        codeTypeComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "BIN", "OCT", "DEC", "HEX" }));
        codeTypeComboBox.setSelectedIndex(3);
        codeTypeComboBox.setToolTipText(resourceBundle.getString("codeTypeComboBox.toolTipText")); // NOI18N
        codeTypeComboBox.setMaximumSize(new java.awt.Dimension(58, 25));
        codeTypeComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                codeTypeComboBoxActionPerformed(evt);
            }
        });
        controlToolBar.add(codeTypeComboBox);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(controlToolBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 280, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(controlToolBar, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void showUnprintablesToggleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showUnprintablesToggleButtonActionPerformed
        codeArea.setShowUnprintables(showUnprintablesToggleButton.isSelected());
    }//GEN-LAST:event_showUnprintablesToggleButtonActionPerformed

    private void codeTypeComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_codeTypeComboBoxActionPerformed
        CodeType codeType = CodeType.values()[codeTypeComboBox.getSelectedIndex()];
        codeArea.setCodeType(codeType);
        preferences.getCodeAreaPreferences().setCodeType(codeType);
    }//GEN-LAST:event_codeTypeComboBoxActionPerformed

    private void saveFileButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveFileButtonActionPerformed
        if (saveAction != null) saveAction.actionPerformed(evt);
    }//GEN-LAST:event_saveFileButtonActionPerformed

    private void undoEditButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_undoEditButtonActionPerformed
        try {
            undoHandler.performUndo();
            codeArea.repaint();
            updateUndoState();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_undoEditButtonActionPerformed

    private void redoEditButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_redoEditButtonActionPerformed
        try {
            undoHandler.performRedo();
            codeArea.repaint();
            updateUndoState();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_redoEditButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> codeTypeComboBox;
    private javax.swing.JToolBar controlToolBar;
    private javax.swing.JButton saveFileButton;
    private javax.swing.JButton redoEditButton;
    private javax.swing.JButton undoEditButton;
    private javax.swing.JToolBar.Separator separator1;
    private javax.swing.JToolBar.Separator separator2;
    private javax.swing.JToolBar.Separator separator3;
    private javax.swing.JToggleButton showUnprintablesToggleButton;
    // End of variables declaration//GEN-END:variables

}
