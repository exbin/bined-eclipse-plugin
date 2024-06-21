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
package org.exbin.framework.editor.text.options.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;

import org.exbin.framework.editor.text.options.impl.TextEncodingOptionsImpl;
import org.exbin.framework.utils.LanguageUtils;
import org.exbin.framework.utils.WindowUtils;
import org.exbin.framework.options.api.OptionsModifiedListener;
import org.exbin.framework.options.api.OptionsComponent;

/**
 * Text encoding selection panel.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class TextEncodingPanel extends javax.swing.JPanel implements OptionsComponent<TextEncodingOptionsImpl> {

    private OptionsModifiedListener optionsModifiedListener;
    private final ResourceBundle resourceBundle = LanguageUtils.getResourceBundleByClass(TextEncodingPanel.class);
    private AddEncodingsOperation addEncodingsOperation = null;

    public TextEncodingPanel() {
        initComponents();
        init();
    }

    private void init() {
        encodingsList.getModel().addListDataListener(new ListDataListener() {

            @Override
            public void intervalAdded(ListDataEvent e) {
                contentsChanged(e);
            }

            @Override
            public void intervalRemoved(ListDataEvent e) {
                contentsChanged(e);
            }

            @Override
            public void contentsChanged(ListDataEvent e) {
                selectAllButton.setEnabled(encodingsList.getModel().getSize() > 0);
            }
        });

        encodingsList.addListSelectionListener((ListSelectionEvent e) -> {
            if (!e.getValueIsAdjusting()) {
                boolean emptySelection = encodingsList.isSelectionEmpty();
                removeButton.setEnabled(!emptySelection);
                selectAllButton.setEnabled(encodingsList.getModel().getSize() > 0);
                if (!emptySelection) {
                    int[] indices = encodingsList.getSelectedIndices();
                    upButton.setEnabled(encodingsList.getMaxSelectionIndex() >= indices.length);
                    downButton.setEnabled(encodingsList.getMinSelectionIndex() + indices.length < encodingsList.getModel().getSize());
                } else {
                    upButton.setEnabled(false);
                    downButton.setEnabled(false);
                }
            }
        });
    }

    @Nonnull
    @Override
    public ResourceBundle getResourceBundle() {
        return resourceBundle;
    }

    @Override
    public void saveToOptions(TextEncodingOptionsImpl options) {
        options.setEncodings(getEncodingList());
    }

    @Override
    public void loadFromOptions(TextEncodingOptionsImpl options) {
        setEncodingList(options.getEncodings());
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        encodingsListScrollPane = new javax.swing.JScrollPane();
        encodingsList = new javax.swing.JList();
        encodingsControlPanel = new javax.swing.JPanel();
        addButton = new javax.swing.JButton();
        upButton = new javax.swing.JButton();
        downButton = new javax.swing.JButton();
        selectAllButton = new javax.swing.JButton();
        removeButton = new javax.swing.JButton();

        setName("Form"); // NOI18N

        encodingsListScrollPane.setName("encodingsListScrollPane"); // NOI18N

        encodingsList.setModel(new EncodingsListModel());
        encodingsList.setName("encodingsList"); // NOI18N
        encodingsListScrollPane.setViewportView(encodingsList);

        encodingsControlPanel.setName("encodingsControlPanel"); // NOI18N

        addButton.setText(resourceBundle.getString("addButton.text")); // NOI18N
        addButton.setName("addButton"); // NOI18N
        addButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addButtonActionPerformed(evt);
            }
        });

        upButton.setText(resourceBundle.getString("upButton.text")); // NOI18N
        upButton.setEnabled(false);
        upButton.setName("upButton"); // NOI18N
        upButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                upButtonActionPerformed(evt);
            }
        });

        downButton.setText(resourceBundle.getString("downButton.text")); // NOI18N
        downButton.setEnabled(false);
        downButton.setName("downButton"); // NOI18N
        downButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                downButtonActionPerformed(evt);
            }
        });

        selectAllButton.setText(resourceBundle.getString("selectAllButton.text")); // NOI18N
        selectAllButton.setEnabled(false);
        selectAllButton.setName("selectAllButton"); // NOI18N
        selectAllButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectAllButtonActionPerformed(evt);
            }
        });

        removeButton.setText(resourceBundle.getString("removeButton.text")); // NOI18N
        removeButton.setEnabled(false);
        removeButton.setName("removeButton"); // NOI18N
        removeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout encodingsControlPanelLayout = new javax.swing.GroupLayout(encodingsControlPanel);
        encodingsControlPanel.setLayout(encodingsControlPanelLayout);
        encodingsControlPanelLayout.setHorizontalGroup(
            encodingsControlPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(encodingsControlPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(encodingsControlPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(addButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 118, Short.MAX_VALUE)
                    .addComponent(removeButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 118, Short.MAX_VALUE)
                    .addComponent(selectAllButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 118, Short.MAX_VALUE)
                    .addComponent(downButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 118, Short.MAX_VALUE)
                    .addComponent(upButton, javax.swing.GroupLayout.DEFAULT_SIZE, 118, Short.MAX_VALUE))
                .addContainerGap())
        );
        encodingsControlPanelLayout.setVerticalGroup(
            encodingsControlPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, encodingsControlPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(addButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(upButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(downButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(selectAllButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(removeButton)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(encodingsListScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 20, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(encodingsControlPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(encodingsControlPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(encodingsListScrollPane)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addButtonActionPerformed
        if (addEncodingsOperation != null) {
            addEncodingsOperation.run(((EncodingsListModel) encodingsList.getModel()).getCharsets(), (List<String> encodings) -> {
                if (encodings != null) {
                    ((EncodingsListModel) encodingsList.getModel()).addAll(encodings, encodingsList.getSelectedIndex());
                    encodingsList.clearSelection();
                    wasModified();
                }
            });
        }
    }//GEN-LAST:event_addButtonActionPerformed

    private void removeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeButtonActionPerformed
        ((EncodingsListModel) encodingsList.getModel()).removeIndices(encodingsList.getSelectedIndices());
        encodingsList.clearSelection();
        wasModified();
    }//GEN-LAST:event_removeButtonActionPerformed

    private void upButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_upButtonActionPerformed
        int[] indices = encodingsList.getSelectedIndices();
        int last = 0;
        for (int i = 0; i < indices.length; i++) {
            int next = indices[i];
            if (last != next) {
                EncodingsListModel model = (EncodingsListModel) encodingsList.getModel();
                String item = (String) model.getElementAt(next);
                model.add(next - 1, item);
                encodingsList.getSelectionModel().addSelectionInterval(next - 1, next - 1);
                model.remove(next + 1);
            } else {
                last++;
            }
        }
        wasModified();
    }//GEN-LAST:event_upButtonActionPerformed

    private void downButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_downButtonActionPerformed
        int[] indices = encodingsList.getSelectedIndices();
        int last = encodingsList.getModel().getSize() - 1;
        for (int i = indices.length; i > 0; i--) {
            int next = indices[i - 1];
            if (last != next) {
                EncodingsListModel model = (EncodingsListModel) encodingsList.getModel();
                String item = (String) model.getElementAt(next);
                model.add(next + 2, item);
                encodingsList.getSelectionModel().addSelectionInterval(next + 2, next + 2);
                model.remove(next);
            } else {
                last--;
            }
        }
        wasModified();
    }//GEN-LAST:event_downButtonActionPerformed

    private void selectAllButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectAllButtonActionPerformed
        if (encodingsList.getSelectedIndices().length < encodingsList.getModel().getSize()) {
            encodingsList.setSelectionInterval(0, encodingsList.getModel().getSize() - 1);
        } else {
            encodingsList.clearSelection();
        }
    }//GEN-LAST:event_selectAllButtonActionPerformed

    public void setEncodingList(List<String> list) {
        ((EncodingsListModel) encodingsList.getModel()).setCharsets(list);
        encodingsList.repaint();
    }

    public List<String> getEncodingList() {
        return ((EncodingsListModel) encodingsList.getModel()).getCharsets();
    }

    /**
     * Test method for this panel.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        WindowUtils.invokeDialog(new TextEncodingPanel());
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addButton;
    private javax.swing.JButton downButton;
    private javax.swing.JPanel encodingsControlPanel;
    private javax.swing.JList encodingsList;
    private javax.swing.JScrollPane encodingsListScrollPane;
    private javax.swing.JButton removeButton;
    private javax.swing.JButton selectAllButton;
    private javax.swing.JButton upButton;
    // End of variables declaration//GEN-END:variables

    @Override
    public void setOptionsModifiedListener(OptionsModifiedListener optionsModifiedListener) {
        this.optionsModifiedListener = optionsModifiedListener;
    }

    public void wasModified() {
        if (optionsModifiedListener != null) {
            optionsModifiedListener.wasModified();
        }
    }

    public void setAddEncodingsOperation(AddEncodingsOperation addEncodingsOperation) {
        this.addEncodingsOperation = addEncodingsOperation;
    }

    public void addEncodings(List<String> encodings) {
        ((EncodingsListModel) encodingsList.getModel()).addAll(encodings, encodingsList.isSelectionEmpty() ? -1 : encodingsList.getSelectedIndex());
    }

    public static interface AddEncodingsOperation {

        void run(List<String> usedEncodings, EncodingsUpdate encodingsUpdate);
    }

    public static interface EncodingsUpdate {

        void update(List<String> encodings);
    }

    @ParametersAreNonnullByDefault
    private class EncodingsListModel extends AbstractListModel<String> {

        private final List<String> charsets = new ArrayList<>();

        @Override
        public int getSize() {
            return charsets.size();
        }

        @Override
        public String getElementAt(int index) {
            return charsets.get(index);
        }

        /**
         * @return the charsets
         */
        @Nonnull
        public List<String> getCharsets() {
            return charsets;
        }

        /**
         * @param charsets the charsets to set
         */
        public void setCharsets(@Nullable List<String> charsets) {
            this.charsets.clear();
            if (charsets != null) {
                this.charsets.addAll(charsets);
            }
            fireContentsChanged(this, 0, this.charsets.size());
        }

        public void addAll(List<String> list, int pos) {
            if (pos >= 0) {
                charsets.addAll(pos, list);
                fireIntervalAdded(this, pos, list.size() + pos);
            } else {
                charsets.addAll(list);
                fireIntervalAdded(this, charsets.size() - list.size(), charsets.size());
            }
        }

        public void removeIndices(int[] indices) {
            if (indices.length == 0) {
                return;
            }

            for (int i = indices.length - 1; i >= 0; i--) {
                charsets.remove(indices[i]);
                fireIntervalRemoved(this, indices[i], indices[i]);
            }
        }

        public void remove(int index) {
            charsets.remove(index);
            fireIntervalRemoved(this, index, index);
        }

        public void add(int index, String item) {
            charsets.add(index, item);
            fireIntervalAdded(this, index, index);
        }
    }
}
