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
package org.exbin.bined.eclipse.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;

import org.exbin.bined.CodeType;
import org.exbin.bined.operation.undo.BinaryDataUndoHandler;
import org.exbin.framework.bined.preferences.BinaryEditorPreferences;
import org.exbin.framework.action.gui.DropDownButton;
import org.exbin.framework.utils.LanguageUtils;

/**
 * Binary editor toolbar panel.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class BinEdToolbarPanel extends javax.swing.JPanel {

    private final java.util.ResourceBundle resourceBundle = LanguageUtils.getResourceBundleByBundleName("org/exbin/framework/bined/resources/BinedModule");
    private final java.util.ResourceBundle fileResourceBundle = LanguageUtils.getResourceBundleByBundleName("org/exbin/framework/file/resources/FileModule");
    private final java.util.ResourceBundle optionsResourceBundle = LanguageUtils.getResourceBundleByBundleName("org/exbin/framework/options/resources/OptionsModule");
    private final java.util.ResourceBundle onlineHelpResourceBundle = LanguageUtils.getResourceBundleByBundleName("org/exbin/framework/help/online/action/resources/OnlineHelpAction");
    private final java.util.ResourceBundle operationUndoResourceBundle = LanguageUtils.getResourceBundleByBundleName("org/exbin/framework/operation/undo/resources/OperationUndoModule");

    private BinaryDataUndoHandler undoHandler;
    private ActionListener saveAction = null;

    private final Control codeAreaControl;
    private AbstractAction optionsAction;
    private AbstractAction onlineHelpAction;

    private final AbstractAction cycleCodeTypesAction;
    private final JRadioButtonMenuItem binaryCodeTypeMenuItem;
    private final JRadioButtonMenuItem octalCodeTypeMenuItem;
    private final JRadioButtonMenuItem decimalCodeTypeMenuItem;
    private final JRadioButtonMenuItem hexadecimalCodeTypeMenuItem;
    private final ButtonGroup codeTypeButtonGroup;
    private DropDownButton codeTypeDropDown;

//    private JSplitButton codeTypeButton;
    public BinEdToolbarPanel(JComponent targetComponent, Control codeAreaControl) {
        this.codeAreaControl = codeAreaControl;

        codeTypeButtonGroup = new ButtonGroup();
        Action binaryCodeTypeAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                codeAreaControl.setCodeType(CodeType.BINARY);
                updateCycleButtonState();
            }
        };
        binaryCodeTypeAction.putValue(Action.NAME, resourceBundle.getString("binaryCodeTypeAction.text"));
        binaryCodeTypeAction.putValue(Action.SHORT_DESCRIPTION, resourceBundle.getString("binaryCodeTypeAction.shortDescription"));
        binaryCodeTypeMenuItem = new JRadioButtonMenuItem(binaryCodeTypeAction);
        codeTypeButtonGroup.add(binaryCodeTypeMenuItem);
        Action octalCodeTypeAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                codeAreaControl.setCodeType(CodeType.OCTAL);
                updateCycleButtonState();
            }
        };
        octalCodeTypeAction.putValue(Action.NAME, resourceBundle.getString("octalCodeTypeAction.text"));
        octalCodeTypeAction.putValue(Action.SHORT_DESCRIPTION, resourceBundle.getString("octalCodeTypeAction.shortDescription"));
        octalCodeTypeMenuItem = new JRadioButtonMenuItem(octalCodeTypeAction);
        codeTypeButtonGroup.add(octalCodeTypeMenuItem);
        Action decimalCodeTypeAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                codeAreaControl.setCodeType(CodeType.DECIMAL);
                updateCycleButtonState();
            }
        };
        decimalCodeTypeAction.putValue(Action.NAME, resourceBundle.getString("decimalCodeTypeAction.text"));
        decimalCodeTypeAction.putValue(Action.SHORT_DESCRIPTION, resourceBundle.getString("decimalCodeTypeAction.shortDescription"));
        decimalCodeTypeMenuItem = new JRadioButtonMenuItem(decimalCodeTypeAction);
        codeTypeButtonGroup.add(decimalCodeTypeMenuItem);
        Action hexadecimalCodeTypeAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                codeAreaControl.setCodeType(CodeType.HEXADECIMAL);
                updateCycleButtonState();
            }
        };
        hexadecimalCodeTypeAction.putValue(Action.NAME, resourceBundle.getString("hexadecimalCodeTypeAction.text"));
        hexadecimalCodeTypeAction.putValue(Action.SHORT_DESCRIPTION, resourceBundle.getString("hexadecimalCodeTypeAction.shortDescription"));
        hexadecimalCodeTypeMenuItem = new JRadioButtonMenuItem(hexadecimalCodeTypeAction);
        codeTypeButtonGroup.add(hexadecimalCodeTypeMenuItem);
        cycleCodeTypesAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int codeTypePos = codeAreaControl.getCodeType().ordinal();
                CodeType[] values = CodeType.values();
                CodeType next = codeTypePos + 1 >= values.length ? values[0] : values[codeTypePos + 1];
                codeAreaControl.setCodeType(next);
                updateCycleButtonState();
            }
        };

        initComponents();
        init();
    }

    private void init() {
        cycleCodeTypesAction.putValue(Action.SHORT_DESCRIPTION, resourceBundle.getString("cycleCodeTypesAction.shortDescription"));
        JPopupMenu cycleCodeTypesPopupMenu = new JPopupMenu();
        cycleCodeTypesPopupMenu.add(binaryCodeTypeMenuItem);
        cycleCodeTypesPopupMenu.add(octalCodeTypeMenuItem);
        cycleCodeTypesPopupMenu.add(decimalCodeTypeMenuItem);
        cycleCodeTypesPopupMenu.add(hexadecimalCodeTypeMenuItem);
        codeTypeDropDown = new DropDownButton(cycleCodeTypesAction, cycleCodeTypesPopupMenu);
        updateCycleButtonState();
        controlToolBar.add(codeTypeDropDown);

        //        codeTypeButton = new JSplitButton("HEX");
//        codeTypeButton.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                throw new UnsupportedOperationException("Not supported yet.");
//            }
//        });
//        controlToolBar.add(codeTypeButton);

        controlToolBar.addSeparator();
        JButton optionsButton = new JButton();
        optionsButton.setAction(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (optionsAction != null) {
                    optionsAction.actionPerformed(e);
                }
            }
        });
        optionsButton.setToolTipText(optionsResourceBundle.getString("optionsAction.text"));
        optionsButton.setIcon(new ImageIcon(getClass().getResource("/org/exbin/framework/options/gui/resources/icons/Preferences16.gif")));
        controlToolBar.add(optionsButton);

        JButton onlineHelpButton = new JButton();
        onlineHelpButton.setAction(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (onlineHelpAction != null) {
                    onlineHelpAction.actionPerformed(e);
                }
            }
        });
        onlineHelpButton.setToolTipText(onlineHelpResourceBundle.getString("onlineHelpAction.text"));
        onlineHelpButton.setIcon(new ImageIcon(getClass().getResource("/org/exbin/framework/bined/resources/icons/open_icon_library/icons/png/16x16/actions/help.png")));
        controlToolBar.add(onlineHelpButton);
}

    public void setOptionsAction(AbstractAction optionsAction) {
        this.optionsAction = optionsAction;
    }

    public void setOnlineHelpAction(AbstractAction onlineHelpAction) {
        this.onlineHelpAction = onlineHelpAction;
    }

    private void updateCycleButtonState() {
        CodeType codeType = codeAreaControl.getCodeType();
        codeTypeDropDown.setActionText(codeType.name().substring(0, 3));
        switch (codeType) {
            case BINARY: {
                if (!binaryCodeTypeMenuItem.isSelected()) {
                    binaryCodeTypeMenuItem.setSelected(true);
                }
                break;
            }
            case OCTAL: {
                if (!octalCodeTypeMenuItem.isSelected()) {
                    octalCodeTypeMenuItem.setSelected(true);
                }
                break;
            }
            case DECIMAL: {
                if (!decimalCodeTypeMenuItem.isSelected()) {
                    decimalCodeTypeMenuItem.setSelected(true);
                }
                break;
            }
            case HEXADECIMAL: {
                if (!hexadecimalCodeTypeMenuItem.isSelected()) {
                    hexadecimalCodeTypeMenuItem.setSelected(true);
                }
                break;
            }
        }
    }

    public void applyFromCodeArea() {
        updateCycleButtonState();
        updateUnprintables();
    }

    public void loadFromPreferences(BinaryEditorPreferences preferences) {
        codeAreaControl.setCodeType(preferences.getCodeAreaPreferences().getCodeType());
        updateCycleButtonState();
        updateUnprintables();
    }

    public void setUndoHandler(BinaryDataUndoHandler undoHandler) {
        this.undoHandler = undoHandler;
    }

    public void updateUndoState() {
    	if (undoHandler == null) {
    		return;
    	}

    	undoEditButton.setEnabled(undoHandler.canUndo());
        redoEditButton.setEnabled(undoHandler.canRedo());

        if (saveAction != null) {
            boolean modified = undoHandler != null && undoHandler.getCommandPosition() != undoHandler.getSyncPoint();
            saveFileButton.setEnabled(modified);
        }
    }

    public void updateUnprintables() {
        boolean showUnprintables = codeAreaControl.isShowUnprintables();
        showUnprintablesToggleButton.setSelected(showUnprintables);
    }

    public void setSaveAction(ActionListener saveAction) {
        this.saveAction = saveAction;
        updateUndoState();
    }

    public void saveFile() {
        if (saveAction != null) {
            saveAction.actionPerformed(new ActionEvent(BinEdToolbarPanel.this, 0, ""));
        }
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

        controlToolBar.setBorder(null);
        controlToolBar.setFloatable(false);
        controlToolBar.setRollover(true);

        saveFileButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/exbin/bined/eclipse/resources/icons/document-save.png"))); // NOI18N
        saveFileButton.setToolTipText(fileResourceBundle.getString("saveFileAction.shortDescription")); // NOI18N
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
        undoEditButton.setToolTipText(operationUndoResourceBundle.getString("editUndoAction.shortDescription")); // NOI18N
        undoEditButton.setFocusable(false);
        undoEditButton.setEnabled(false);
        undoEditButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        undoEditButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        undoEditButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                undoEditButtonActionPerformed(evt);
            }
        });
        controlToolBar.add(undoEditButton);

        redoEditButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/exbin/bined/eclipse/resources/icons/edit-redo.png"))); // NOI18N
        redoEditButton.setToolTipText(operationUndoResourceBundle.getString("editRedoAction.shortDescription")); // NOI18N
        redoEditButton.setFocusable(false);
        redoEditButton.setEnabled(false);
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
        showUnprintablesToggleButton.setToolTipText(resourceBundle.getString("viewUnprintablesAction.shortDescription")); // NOI18N
        showUnprintablesToggleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showUnprintablesToggleButtonActionPerformed(evt);
            }
        });
        controlToolBar.add(showUnprintablesToggleButton);
        controlToolBar.add(separator3);

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
                .addComponent(controlToolBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 20, Short.MAX_VALUE))
        );
        controlToolBar.revalidate();
        controlToolBar.repaint();
    }// </editor-fold>//GEN-END:initComponents

    private void showUnprintablesToggleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showUnprintablesToggleButtonActionPerformed
        codeAreaControl.setShowUnprintables(showUnprintablesToggleButton.isSelected());
    }//GEN-LAST:event_showUnprintablesToggleButtonActionPerformed

    private void saveFileButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveFileButtonActionPerformed
        if (saveAction != null) saveAction.actionPerformed(evt);
    }//GEN-LAST:event_saveFileButtonActionPerformed

    private void undoEditButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_undoEditButtonActionPerformed
        try {
            undoHandler.performUndo();
            codeAreaControl.repaint();
            updateUndoState();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_undoEditButtonActionPerformed

    private void redoEditButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_redoEditButtonActionPerformed
        try {
            undoHandler.performRedo();
            codeAreaControl.repaint();
            updateUndoState();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_redoEditButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JToolBar controlToolBar;
    private javax.swing.JButton saveFileButton;
    private javax.swing.JButton redoEditButton;
    private javax.swing.JButton undoEditButton;
    private javax.swing.JToolBar.Separator separator1;
    private javax.swing.JToolBar.Separator separator2;
    private javax.swing.JToolBar.Separator separator3;
    private javax.swing.JToggleButton showUnprintablesToggleButton;
    // End of variables declaration//GEN-END:variables

    @ParametersAreNonnullByDefault
    public interface Control {

        @Nonnull
        CodeType getCodeType();

        void setCodeType(CodeType codeType);

        boolean isShowUnprintables();

        void setShowUnprintables(boolean showUnprintables);

        void repaint();
    }
}
