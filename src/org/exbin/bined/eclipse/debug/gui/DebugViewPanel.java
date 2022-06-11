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
package org.exbin.bined.eclipse.debug.gui;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;
import org.exbin.bined.EditMode;
import org.exbin.auxiliary.paged_data.BinaryData;
import org.exbin.bined.eclipse.debug.DebugViewDataProvider;
import org.exbin.bined.eclipse.gui.BinEdComponentFileApi;
import org.exbin.bined.eclipse.gui.BinEdComponentPanel;
import org.exbin.framework.bined.FileHandlingMode;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Panel to show debug view.
 *
 * @version 0.2.1 2022/05/29
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class DebugViewPanel extends javax.swing.JPanel {

    private final List<DebugViewDataProvider> providers = new ArrayList<>();
    private int selectedProvider = 0;

    private final BinEdComponentPanel componentPanel;

    public DebugViewPanel() {
        componentPanel = new BinEdComponentPanel();

        initComponents();
        init();
    }

    private void init() {
        componentPanel.getCodeArea().setEditMode(EditMode.READ_ONLY);
        componentPanel.setFileApi(new BinEdComponentFileApi() {
            @Override
            public boolean isSaveSupported() {
                return false;
            }

            @Override
            public void saveDocument() {
            }

            @Override
            public void switchFileHandlingMode(FileHandlingMode newHandlingMode) {
            }

            @Override
            public void closeData() {
            }
        });

        this.add(componentPanel, BorderLayout.CENTER);
        this.invalidate();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {

        providerComboBox = new javax.swing.JComboBox<>();

        setLayout(new java.awt.BorderLayout());

        providerComboBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                providerComboBoxItemStateChanged(evt);
            }
        });
        add(providerComboBox, java.awt.BorderLayout.PAGE_START);
    }// </editor-fold>

    private void providerComboBoxItemStateChanged(java.awt.event.ItemEvent evt) {
        int selectedIndex = providerComboBox.getSelectedIndex();
        if (selectedProvider != selectedIndex) {
            selectedProvider = selectedIndex;
            setContentData(providers.get(selectedProvider).getData());
        }
    }

    // Variables declaration - do not modify
    private javax.swing.JComboBox<String> providerComboBox;
    // End of variables declaration

    public void addProvider(DebugViewDataProvider provider) {
        if (providers.isEmpty()) {
            setContentData(provider.getData());
        }

        providers.add(provider);
        providerComboBox.addItem(provider.getName());
    }

    public void setContentData(@Nullable BinaryData data) {
        componentPanel.setContentData(data);
    }
}
