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
package org.exbin.bined.eclipse.action;

import org.exbin.bined.eclipse.gui.BinEdOptionsPanel;
import org.exbin.bined.eclipse.gui.BinEdOptionsPanelBorder;
import org.exbin.bined.eclipse.main.BinEdManager;
import org.exbin.bined.eclipse.plugin.BinEdPlugin;
import org.exbin.bined.swing.extended.ExtCodeArea;
import org.exbin.framework.bined.BinEdFileHandler;
import org.exbin.framework.bined.gui.BinEdComponentFileApi;
import org.exbin.framework.bined.gui.BinEdComponentPanel;
import org.exbin.framework.bined.preferences.BinaryEditorPreferences;
import org.exbin.framework.editor.text.TextFontApi;
import org.exbin.framework.editor.text.service.TextFontService;
import org.exbin.framework.file.api.FileHandler;
import org.exbin.framework.utils.WindowUtils;
import org.exbin.framework.utils.gui.OptionsControlPanel;
import org.exbin.framework.utils.handler.OptionsControlHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import javax.swing.JPanel;
import javax.swing.JTextField;

import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;

/**
 * Options action.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class OptionsAction extends AbstractAction {

    private final BinEdComponentPanel componentPanel;
    private final FileHandler fileHandler;
    private final BinaryEditorPreferences preferences;

    public OptionsAction(BinEdComponentPanel componentPanel, @Nullable FileHandler fileHandler, BinaryEditorPreferences preferences) {
        this.componentPanel = componentPanel;
        this.fileHandler = fileHandler;
        this.preferences = preferences;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        ExtCodeArea codeArea = componentPanel.getCodeArea();
        final BinEdOptionsPanelBorder optionsPanelWrapper = new BinEdOptionsPanelBorder();
        optionsPanelWrapper.setPreferredSize(new Dimension(700, 460));
        BinEdOptionsPanel optionsPanel = optionsPanelWrapper.getOptionsPanel();
        optionsPanel.setPreferences(preferences);
        optionsPanel.setTextFontService(new TextFontService() {
            @Nonnull
            @Override
            public Font getCurrentFont() {
                return codeArea.getCodeFont();
            }

            @Nonnull
            @Override
            public Font getDefaultFont() {
            	Font defaultFont = fileHandler == null ? new JTextField().getFont() : ((TextFontApi) fileHandler).getDefaultFont();
                if (defaultFont == null) {
                    defaultFont = new Font(Font.MONOSPACED, Font.PLAIN, 12);
                }
                return defaultFont;
            }

            @Override
            public void setCurrentFont(Font font) {
                codeArea.setCodeFont(font);
            }
        });
        optionsPanel.loadFromPreferences();
//        editorComponent.updateApplyOptions(optionsPanel);
        OptionsControlPanel optionsControlPanel = new OptionsControlPanel();
        JPanel dialogPanel = WindowUtils.createDialogPanel(optionsPanelWrapper, optionsControlPanel);
        WindowUtils.DialogWrapper dialog = WindowUtils.createDialog(dialogPanel, componentPanel, optionsPanel.getResourceBundle().getString("dialog.title"), Dialog.ModalityType.APPLICATION_MODAL);
        optionsControlPanel.setHandler((OptionsControlHandler.ControlActionType actionType) -> {
            if (actionType != OptionsControlHandler.ControlActionType.CANCEL) {
                optionsPanel.applyToOptions();
                if (actionType == OptionsControlHandler.ControlActionType.SAVE) {
                    optionsPanel.saveToPreferences();
                }
                BinEdManager binedManager = BinEdManager.getInstance();
                if (fileHandler != null) {
                    Font defaultFont = ((TextFontApi) fileHandler).getDefaultFont();
                    if (defaultFont == null) {
                        defaultFont = new Font(Font.MONOSPACED, Font.PLAIN, 12);
                    }
                    ((BinEdComponentFileApi) fileHandler).getEditorComponent()
                            .applyOptions(optionsPanel,
                                    binedManager.getEncodingsHandler(),
                                    defaultFont);
                    if (fileHandler instanceof BinEdFileHandler) {
                        ((BinEdFileHandler) fileHandler).switchFileHandlingMode(optionsPanel.getEditorOptions()
                                .getFileHandlingMode());
                    }
                }
                BinEdPlugin.applyIntegrationOptions(optionsPanel.getIntegrationOptions());
                codeArea.repaint();
            }

            dialog.close();
        });
        dialog.showCentered(componentPanel);
        dialog.dispose();
    }
}
