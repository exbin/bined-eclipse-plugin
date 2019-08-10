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
package org.exbin.bined.eclipse.plugin.editors;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Nonnull;
import javax.swing.AbstractAction;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JViewport;
import javax.swing.KeyStroke;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.part.FileEditorInput;
import org.exbin.bined.BasicCodeAreaZone;
import org.exbin.bined.CodeAreaCaretPosition;
import org.exbin.bined.EditationMode;
import org.exbin.bined.EditationOperation;
import org.exbin.bined.PositionCodeType;
import org.exbin.bined.SelectionChangedListener;
import org.exbin.bined.SelectionRange;
import org.exbin.bined.capability.CharsetCapable;
import org.exbin.bined.delta.DeltaDocument;
import org.exbin.bined.delta.FileDataSource;
import org.exbin.bined.delta.SegmentsRepository;
import org.exbin.bined.eclipse.BinEdApplyOptions;
import org.exbin.bined.eclipse.GoToPositionAction;
import org.exbin.bined.eclipse.SearchAction;
import org.exbin.bined.eclipse.panel.BinEdOptionsPanel;
import org.exbin.bined.eclipse.panel.BinEdOptionsPanelBorder;
import org.exbin.bined.eclipse.panel.BinEdToolbarPanel;
import org.exbin.bined.eclipse.panel.BinarySearchPanel;
import org.exbin.bined.eclipse.plugin.BinEdPlugin;
import org.exbin.bined.extended.layout.ExtendedCodeAreaLayoutProfile;
import org.exbin.bined.highlight.swing.extended.ExtendedHighlightNonAsciiCodeAreaPainter;
import org.exbin.bined.operation.BinaryDataCommand;
import org.exbin.bined.operation.swing.CodeAreaOperationCommandHandler;
import org.exbin.bined.operation.swing.CodeAreaUndoHandler;
import org.exbin.bined.operation.undo.BinaryDataUndoUpdateListener;
import org.exbin.bined.swing.basic.color.CodeAreaColorsProfile;
import org.exbin.bined.swing.extended.ExtCodeArea;
import org.exbin.bined.swing.extended.theme.ExtendedCodeAreaThemeProfile;
import org.exbin.framework.bined.BinaryStatusApi;
import org.exbin.framework.bined.FileHandlingMode;
import org.exbin.framework.bined.options.CodeAreaColorOptions;
import org.exbin.framework.bined.options.CodeAreaLayoutOptions;
import org.exbin.framework.bined.options.CodeAreaOptions;
import org.exbin.framework.bined.options.CodeAreaThemeOptions;
import org.exbin.framework.bined.options.EditorOptions;
import org.exbin.framework.bined.options.StatusOptions;
import org.exbin.framework.bined.options.impl.CodeAreaOptionsImpl;
import org.exbin.framework.bined.panel.BinaryStatusPanel;
import org.exbin.framework.bined.panel.ValuesPanel;
import org.exbin.framework.bined.preferences.BinaryEditorPreferences;
import org.exbin.framework.editor.text.EncodingsHandler;
import org.exbin.framework.editor.text.TextEncodingStatusApi;
import org.exbin.framework.editor.text.options.TextEncodingOptions;
import org.exbin.framework.gui.about.panel.AboutPanel;
import org.exbin.framework.gui.utils.ActionUtils;
import org.exbin.framework.gui.utils.WindowUtils;
import org.exbin.framework.gui.utils.WindowUtils.DialogWrapper;
import org.exbin.framework.gui.utils.handler.OptionsControlHandler;
import org.exbin.framework.gui.utils.panel.CloseControlPanel;
import org.exbin.framework.gui.utils.panel.OptionsControlPanel;
import org.exbin.framework.preferences.PreferencesWrapper;
import org.exbin.utils.binary_data.BinaryData;
import org.exbin.utils.binary_data.ByteArrayData;
import org.exbin.utils.binary_data.ByteArrayEditableData;
import org.exbin.utils.binary_data.EditableBinaryData;
import org.exbin.utils.binary_data.PagedData;

/**
 * Binary editor editor component.
 *
 * @version 0.2.1 2019/08/07
 * @author ExBin Project (http://exbin.org)
 */
public final class BinEdEditorSwing {

    private static final FileHandlingMode DEFAULT_FILE_HANDLING_MODE = FileHandlingMode.DELTA;

    private final BinaryEditorPreferences preferences;
    private static SegmentsRepository segmentsRepository = null;
    private final ExtCodeArea codeArea;
    private final CodeAreaUndoHandler undoHandler;
    private final ExtendedCodeAreaLayoutProfile defaultLayoutProfile;
    private final ExtendedCodeAreaThemeProfile defaultThemeProfile;
    private final CodeAreaColorsProfile defaultColorProfile;

    private BinEdToolbarPanel toolbarPanel;
    private BinaryStatusPanel statusPanel;
    private BinaryStatusApi binaryStatus;
    private TextEncodingStatusApi encodingStatus;
    private CharsetChangeListener charsetChangeListener = null;
    private GoToPositionAction goToRowAction;
    private javax.swing.AbstractAction showHeaderAction;
    private javax.swing.AbstractAction showRowNumbersAction;
    private EncodingsHandler encodingsHandler;
    private ValuesPanel valuesPanel = null;
    private JScrollPane valuesPanelScrollPane = null;
    private boolean valuesPanelVisible = false;
    private final SearchAction searchAction;

    private boolean opened = false;
    private boolean modified = false;
    private FileHandlingMode fileHandlingMode = DEFAULT_FILE_HANDLING_MODE;
    protected String displayName;
    private long documentOriginalSize;
    private IEditorInput dataObject;
    private ActionsStateListener actionsStateListener;

    public BinEdEditorSwing(Composite parent) {

		initComponents();

		preferences = new BinaryEditorPreferences(new PreferencesWrapper(BinEdPlugin.getDefault().getPreferenceStore()));

        codeArea = new ExtCodeArea();
        codeArea.setPainter(new ExtendedHighlightNonAsciiCodeAreaPainter(codeArea));
        codeArea.setCodeFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        codeArea.getCaret().setBlinkRate(300);
        defaultLayoutProfile = codeArea.getLayoutProfile();
        defaultThemeProfile = codeArea.getThemeProfile();
        defaultColorProfile = codeArea.getColorsProfile();

        undoHandler = new CodeAreaUndoHandler(codeArea);
        toolbarPanel = new BinEdToolbarPanel(preferences, codeArea, undoHandler);
        toolbarPanel.setSaveAction(this::saveFileButtonActionPerformed);
        statusPanel = new BinaryStatusPanel();
        codeAreaPanel.add(toolbarPanel, BorderLayout.NORTH);
        registerEncodingStatus(statusPanel);
        encodingsHandler = new EncodingsHandler();
        encodingsHandler.setParentComponent(codeAreaPanel);
        encodingsHandler.init();
        encodingsHandler.setTextEncodingStatus(new TextEncodingStatusApi() {
            @Override
            public String getEncoding() {
                return encodingStatus.getEncoding();
            }

            @Override
            public void setEncoding(String encodingName) {
                codeArea.setCharset(Charset.forName(encodingName));
                encodingStatus.setEncoding(encodingName);
                preferences.getEncodingPreferences().setSelectedEncoding(encodingName);
                charsetChangeListener.charsetChanged();
            }
        });

        getSegmentsRepository();
        setNewData();
        CodeAreaOperationCommandHandler commandHandler = new CodeAreaOperationCommandHandler(codeArea, undoHandler);
        codeArea.setCommandHandler(commandHandler);
        registerBinaryStatus(statusPanel);

        initialLoadFromPreferences();

        codeAreaPanel.add(codeArea, BorderLayout.CENTER);
        goToRowAction = new GoToPositionAction(codeArea);
        showHeaderAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ExtendedCodeAreaLayoutProfile layoutProfile = codeArea.getLayoutProfile();
                if (layoutProfile == null) {
                    throw new IllegalStateException();
                }
                boolean showHeader = layoutProfile.isShowHeader();
                layoutProfile.setShowHeader(!showHeader);
                codeArea.setLayoutProfile(layoutProfile);
            }
        };
        showRowNumbersAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ExtendedCodeAreaLayoutProfile layoutProfile = codeArea.getLayoutProfile();
                if (layoutProfile == null) {
                    throw new IllegalStateException();
                }
                boolean showRowPosition = layoutProfile.isShowRowPosition();
                layoutProfile.setShowRowPosition(!showRowPosition);
                codeArea.setLayoutProfile(layoutProfile);
            }
        };

        codeArea.setComponentPopupMenu(new JPopupMenu() {
            @Override
            public void show(Component invoker, int x, int y) {
            	int clickedX = x;
            	int clickedY = y;
            	if (invoker instanceof JViewport) {
            		// Workaround for different behavior of emulated Swing
            		clickedX += ((JViewport) invoker).getParent().getX();
            		clickedY += ((JViewport) invoker).getParent().getY();
            	}
                JPopupMenu popupMenu = createContextMenu(clickedX, clickedY);
                popupMenu.show(invoker, x, y);
            }
        });

        undoHandler.addUndoUpdateListener(new BinaryDataUndoUpdateListener() {
            @Override
            public void undoCommandPositionChanged() {
                codeArea.repaint();
                toolbarPanel.updateUndoState();
                updateCurrentDocumentSize();
                updateModified();
            }

            @Override
            public void undoCommandAdded(final BinaryDataCommand command) {
                toolbarPanel.updateUndoState();
                updateCurrentDocumentSize();
                updateModified();
            }
        });
        toolbarPanel.updateUndoState();

        searchAction = new SearchAction(codeArea, codeAreaPanel);
        codeArea.addDataChangedListener(() -> {
            searchAction.codeAreaDataChanged();
            updateCurrentDocumentSize();
        });

        toolbarPanel.applyFromCodeArea();

        codeArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent keyEvent) {
                if (keyEvent.getModifiers() == ActionUtils.getMetaMask()) {
                    int keyCode = keyEvent.getKeyCode();
                    switch (keyCode) {
                        case KeyEvent.VK_F: {
                            searchAction.actionPerformed(new ActionEvent(keyEvent.getSource(), keyEvent.getID(), ""));
                            searchAction.switchReplaceMode(BinarySearchPanel.SearchOperation.FIND);
                            break;
                        }
                        case KeyEvent.VK_G: {
                            goToRowAction.actionPerformed(new ActionEvent(keyEvent.getSource(), keyEvent.getID(), ""));
                            break;
                        }
                    }
                }
            }
        });
    }

    public JPanel getCodeAreaPanel()
    {
    	return codeAreaPanel;
    }
    
    public BinaryStatusPanel getStatusPanel()
    {
    	return statusPanel;
    }
    
    public void registerBinaryStatus(BinaryStatusApi binaryStatusApi) {
        this.binaryStatus = binaryStatusApi;
        codeArea.addCaretMovedListener((CodeAreaCaretPosition caretPosition) -> {
            binaryStatus.setCursorPosition(caretPosition);
        });

        codeArea.addEditationModeChangedListener(binaryStatus::setEditationMode);
        binaryStatus.setEditationMode(codeArea.getEditationMode(), codeArea.getActiveOperation());

        binaryStatus.setControlHandler(new BinaryStatusApi.StatusControlHandler() {
            @Override
            public void changeEditationOperation(EditationOperation editationOperation) {
                codeArea.setEditationOperation(editationOperation);
            }

            @Override
            public void changeCursorPosition() {
                goToRowAction.actionPerformed(new ActionEvent(codeAreaPanel, 0, ""));
            }

            @Override
            public void cycleEncodings() {
                if (encodingsHandler != null) {
                    encodingsHandler.cycleEncodings();
                }
            }

            @Override
            public void encodingsPopupEncodingsMenu(MouseEvent mouseEvent) {
                if (encodingsHandler != null) {
                    encodingsHandler.popupEncodingsMenu(mouseEvent);
                }
            }

            @Override
            public void changeMemoryMode(BinaryStatusApi.MemoryMode memoryMode) {
                FileHandlingMode newHandlingMode = memoryMode == BinaryStatusApi.MemoryMode.DELTA_MODE ? FileHandlingMode.DELTA : FileHandlingMode.MEMORY;
                if (newHandlingMode != fileHandlingMode) {
                    switchFileHandlingMode(newHandlingMode);
                    preferences.getEditorPreferences().setFileHandlingMode(newHandlingMode);
                }
            }
        });
    }

    private void switchShowValuesPanel(boolean showValuesPanel) {
        if (showValuesPanel) {
            showValuesPanel();
        } else {
            hideValuesPanel();
        }
    }

    private void switchFileHandlingMode(FileHandlingMode newHandlingMode) {
        if (newHandlingMode != fileHandlingMode) {
            // Switch memory mode
            if (dataObject != null) {
                // If document is connected to file, attempt to release first if modified and then simply reload
                if (isModified()) {
                    if (releaseFile()) {
                        fileHandlingMode = newHandlingMode;
                        openDataObject(dataObject);
                        codeArea.clearSelection();
                        codeArea.setCaretPosition(0);
                    }
                } else {
                    fileHandlingMode = newHandlingMode;
                    openDataObject(dataObject);
                }
            } else {
                // If document unsaved in memory, switch data in code area
                if (codeArea.getContentData() instanceof DeltaDocument) {
                    BinaryData oldData = codeArea.getContentData();
                    PagedData data = new PagedData();
                    data.insert(0, codeArea.getContentData());
                    codeArea.setContentData(data);
                    oldData.dispose();
                } else {
                    BinaryData oldData = codeArea.getContentData();
                    DeltaDocument document = segmentsRepository.createDocument();
                    document.insert(0, oldData);
                    codeArea.setContentData(document);
                    oldData.dispose();
                }
                undoHandler.clear();
                codeArea.notifyDataChanged();
                updateCurrentMemoryMode();
                fileHandlingMode = newHandlingMode;
            }
            fileHandlingMode = newHandlingMode;
        }
    }

    public void registerEncodingStatus(TextEncodingStatusApi encodingStatusApi) {
        this.encodingStatus = encodingStatusApi;
        setCharsetChangeListener(() -> {
            String selectedEncoding = codeArea.getCharset().name();
            encodingStatus.setEncoding(selectedEncoding);
        });
    }

    public void setCharsetChangeListener(CharsetChangeListener charsetChangeListener) {
        this.charsetChangeListener = charsetChangeListener;
    }

    public boolean isModified() {
        return undoHandler.getCommandPosition() != undoHandler.getSyncPoint();
    }

    void setModified(boolean modified) {
        this.modified = modified;
        toolbarPanel.updateUndoState();
        toolbarPanel.updateModified(modified);
        actionsStateListener.changed();
//        final String htmlDisplayName;
//        if (modified && opened) {
//            savable.activate();
//            content.add(savable);
//            htmlDisplayName = "<html><b>" + displayName + "</b></html>";
//        } else {
//            savable.deactivate();
//            content.remove(savable);
//            htmlDisplayName = displayName;
//        }
//
//        if (SwingUtilities.isEventDispatchThread()) {
//            setHtmlDisplayName(htmlDisplayName);
//        } else {
//            try {
//                SwingUtilities.invokeAndWait(() -> {
//                    setHtmlDisplayName(htmlDisplayName);
//                });
//            } catch (InterruptedException | InvocationTargetException ex) {
//                Exceptions.printStackTrace(ex);
//            }
//        }
    }

    private void setNewData() {
        if (fileHandlingMode == FileHandlingMode.DELTA) {
            codeArea.setContentData(segmentsRepository.createDocument());
        } else {
            codeArea.setContentData(new PagedData());
        }
    }
    
    public void registerActionsStateListener(ActionsStateListener listener) {
    	actionsStateListener = listener;
    	undoHandler.addUndoUpdateListener(new BinaryDataUndoUpdateListener() {
			@Override
			public void undoCommandPositionChanged() {
				actionsStateListener.changed();
			}
			
			@Override
			public void undoCommandAdded(BinaryDataCommand arg0) {
				actionsStateListener.changed();
			}
		});
    	codeArea.addSelectionChangedListener(new SelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionRange arg0) {
				actionsStateListener.changed();
			}
		});
    }

    /**
     * Attempts to release current file and warn if document was modified.
     *
     * @return true if successful
     */
    private boolean releaseFile() {

        if (dataObject == null) {
            return true;
        }

        while (isModified()) {
            Object[] options = {
                "Save",
                "Discard",
                "Cancel"
            };
            int result = JOptionPane.showOptionDialog(codeArea,
                    "Document was modified! Do you wish to save it?",
                    "Save File?",
                    JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null, options, options[0]);
            if (result == JOptionPane.NO_OPTION) {
                return true;
            }
            if (result == JOptionPane.CANCEL_OPTION || result == JOptionPane.CLOSED_OPTION) {
                return false;
            }

            try {
                saveDataObject(dataObject);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        return true;
    }

    public void openDataObject(IEditorInput dataObject) {
        this.dataObject = dataObject;
		if (dataObject instanceof FileEditorInput) {
			IFile file = ((FileEditorInput) dataObject).getFile();
			IPath path = file.getLocation();
			try {
				openFileInt(path.toFile());
			} catch (IOException ex) {
				ex.printStackTrace();
			}
//	        displayName = dataObject.getPrimaryFile().getNameExt();
//	        setHtmlDisplayName(displayName);
//	        node.openFile(dataObject);
//	        savable.setDataObject(dataObject);
	        opened = true;
	        documentOriginalSize = codeArea.getDataSize();
	        updateCurrentDocumentSize();
	        updateCurrentMemoryMode();

//	        final Charset charset = Charset.forName(FileEncodingQuery.getEncoding(dataObject.getPrimaryFile()).name());
//	        if (charsetChangeListener != null) {
//	            charsetChangeListener.charsetChanged();
	//	        }
//	        codeArea.setCharset(charset);
		}
    }

    public void saveDataObject(IEditorInput dataObject) throws IOException {
		if (dataObject instanceof FileEditorInput) {
			IFile file = ((FileEditorInput) dataObject).getFile();
			IPath path = file.getLocation();
			try {
				saveFileInt(path.toFile());
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
        undoHandler.setSyncPoint();
        setModified(false);
        documentOriginalSize = codeArea.getDataSize();
        updateCurrentDocumentSize();
        updateCurrentMemoryMode();
    }
    
    public void save() throws IOException {
    	saveDataObject(dataObject);
    }

	public void saveAsFile(File file) throws IOException {
		saveFileInt(file);		
	}

	private void openFileInt(File file) throws IOException {
        boolean editable = file.canWrite();
        BinaryData oldData = codeArea.getContentData();
        if (fileHandlingMode == FileHandlingMode.MEMORY) {
            EditableBinaryData data = new ByteArrayEditableData();
            data.loadFromStream(new FileInputStream(file));
            codeArea.setContentData(data);
            oldData.dispose();
        } else {
            FileDataSource fileSource = segmentsRepository.openFileSource(file, editable ? FileDataSource.EditationMode.READ_WRITE : FileDataSource.EditationMode.READ_ONLY);
            DeltaDocument document = segmentsRepository.createDocument(fileSource);
            codeArea.setContentData(document);
            oldData.dispose();
        }
        codeArea.setEditationMode(editable ? EditationMode.EXPANDING : EditationMode.READ_ONLY);
        opened = true;
        documentOriginalSize = codeArea.getDataSize();
        updateCurrentDocumentSize();
        updateCurrentMemoryMode();
    }

    private void saveFileInt(File file) throws IOException {
        BinaryData data = codeArea.getContentData();
        if (fileHandlingMode == FileHandlingMode.MEMORY) {
            data.saveToStream(new FileOutputStream(file));
        } else {
            DeltaDocument document = (DeltaDocument) data;
            document.save();
        }
    }

    private void updateCurrentDocumentSize() {
        long dataSize = codeArea.getContentData().getDataSize();
        binaryStatus.setCurrentDocumentSize(dataSize, documentOriginalSize);
    }

    @Nonnull
    public FileHandlingMode getFileHandlingMode() {
        return fileHandlingMode;
    }

    public void setFileHandlingMode(FileHandlingMode fileHandlingMode) {
        this.fileHandlingMode = fileHandlingMode;
    }

    private void updateCurrentMemoryMode() {
        BinaryStatusApi.MemoryMode memoryMode = BinaryStatusApi.MemoryMode.RAM_MEMORY;
        if (codeArea.getEditationMode() == EditationMode.READ_ONLY) {
            memoryMode = BinaryStatusApi.MemoryMode.READ_ONLY;
        } else if (codeArea.getContentData() instanceof DeltaDocument) {
            memoryMode = BinaryStatusApi.MemoryMode.DELTA_MODE;
        }

        if (binaryStatus != null) {
            binaryStatus.setMemoryMode(memoryMode);
        }
    }

    private void updateModified() {
        setModified(undoHandler.getSyncPoint() != undoHandler.getCommandPosition());
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
    private void initComponents() {
        codeAreaPanel = new javax.swing.JPanel();
        codeAreaPanel.setLayout(new java.awt.BorderLayout());
    }// </editor-fold>                        

    // Variables declaration - do not modify                     
    private javax.swing.JPanel codeAreaPanel;
    // End of variables declaration                   

    private void closeData() {
        BinaryData data = codeArea.getContentData();
        codeArea.setContentData(new ByteArrayData());
        if (data instanceof DeltaDocument) {
            FileDataSource fileSource = ((DeltaDocument) data).getFileSource();
            data.dispose();
            segmentsRepository.detachFileSource(fileSource);
            segmentsRepository.closeFileSource(fileSource);
        } else {
            if (data != null) {
                data.dispose();
            }
        }
    }

    public static synchronized SegmentsRepository getSegmentsRepository() {
        if (segmentsRepository == null) {
            segmentsRepository = new SegmentsRepository();
        }

        return segmentsRepository;
    }

    public CodeAreaUndoHandler getUndoHandler() {
    	return undoHandler;
    }

    @Nonnull
    private JPopupMenu createContextMenu(int x, int y) {
        final JPopupMenu result = new JPopupMenu();

        BasicCodeAreaZone positionZone = codeArea.getPositionZone(x, y);

        switch (positionZone) {
            case TOP_LEFT_CORNER:
            case HEADER: {
                result.add(createShowHeaderMenuItem());
                result.add(createPositionCodeTypeMenuItem());
                break;
            }
            case ROW_POSITIONS: {
                result.add(createShowRowPositionMenuItem());
                result.add(createPositionCodeTypeMenuItem());
                result.add(new JSeparator());
                result.add(createGoToMenuItem());

                break;
            }
            default: {
                final JMenuItem cutMenuItem = new JMenuItem("Cut");
                cutMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionUtils.getMetaMask()));
                cutMenuItem.setEnabled(codeArea.hasSelection() && codeArea.isEditable());
                cutMenuItem.addActionListener((ActionEvent e) -> {
                    codeArea.cut();
                    result.setVisible(false);
                });
                result.add(cutMenuItem);

                final JMenuItem copyMenuItem = new JMenuItem("Copy");
                copyMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionUtils.getMetaMask()));
                copyMenuItem.setEnabled(codeArea.hasSelection());
                copyMenuItem.addActionListener((ActionEvent e) -> {
                    codeArea.copy();
                    result.setVisible(false);
                });
                result.add(copyMenuItem);

                final JMenuItem copyAsCodeMenuItem = new JMenuItem("Copy as Code");
                copyAsCodeMenuItem.setEnabled(codeArea.hasSelection());
                copyAsCodeMenuItem.addActionListener((ActionEvent e) -> {
                    codeArea.copyAsCode();
                    result.setVisible(false);
                });
                result.add(copyAsCodeMenuItem);

                final JMenuItem pasteMenuItem = new JMenuItem("Paste");
                pasteMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, ActionUtils.getMetaMask()));
                pasteMenuItem.setEnabled(codeArea.canPaste() && codeArea.isEditable());
                pasteMenuItem.addActionListener((ActionEvent e) -> {
                    codeArea.paste();
                    result.setVisible(false);
                });
                result.add(pasteMenuItem);

                final JMenuItem pasteFromCodeMenuItem = new JMenuItem("Paste from Code");
                pasteFromCodeMenuItem.setEnabled(codeArea.canPaste() && codeArea.isEditable());
                pasteFromCodeMenuItem.addActionListener((ActionEvent e) -> {
                    try {
                        codeArea.pasteFromCode();
                    } catch (IllegalArgumentException ex) {
                        JOptionPane.showMessageDialog(codeArea, ex.getMessage(), "Unable to Paste Code", JOptionPane.ERROR_MESSAGE);
                    }
                    result.setVisible(false);
                });
                result.add(pasteFromCodeMenuItem);

                final JMenuItem deleteMenuItem = new JMenuItem("Delete");
                deleteMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
                deleteMenuItem.setEnabled(codeArea.hasSelection() && codeArea.isEditable());
                deleteMenuItem.addActionListener((ActionEvent e) -> {
                    codeArea.delete();
                    result.setVisible(false);
                });
                result.add(deleteMenuItem);
                result.addSeparator();

                final JMenuItem selectAllMenuItem = new JMenuItem("Select All");
                selectAllMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionUtils.getMetaMask()));
                selectAllMenuItem.addActionListener((ActionEvent e) -> {
                    codeArea.selectAll();
                    result.setVisible(false);
                });
                result.add(selectAllMenuItem);
                result.addSeparator();

                JMenuItem goToMenuItem = createGoToMenuItem();
                result.add(goToMenuItem);

                final JMenuItem findMenuItem = new JMenuItem("Find...");
                findMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, ActionUtils.getMetaMask()));
                findMenuItem.addActionListener((ActionEvent e) -> {
                    searchAction.actionPerformed(e);
                    searchAction.switchReplaceMode(BinarySearchPanel.SearchOperation.FIND);
                });
                result.add(findMenuItem);

                final JMenuItem replaceMenuItem = new JMenuItem("Replace...");
                replaceMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H, ActionUtils.getMetaMask()));
                replaceMenuItem.setEnabled(codeArea.isEditable());
                replaceMenuItem.addActionListener((ActionEvent e) -> {
                    searchAction.actionPerformed(e);
                    searchAction.switchReplaceMode(BinarySearchPanel.SearchOperation.REPLACE);
                });
                result.add(replaceMenuItem);
            }
        }

        result.addSeparator();

        switch (positionZone) {
            case TOP_LEFT_CORNER:
            case HEADER:
            case ROW_POSITIONS: {
                break;
            }
            default: {
                JMenu showMenu = new JMenu("Show");
                JMenuItem showHeader = createShowHeaderMenuItem();
                showMenu.add(showHeader);
                JMenuItem showRowPosition = createShowRowPositionMenuItem();
                showMenu.add(showRowPosition);
                result.add(showMenu);
            }
        }

        final JMenuItem optionsMenuItem = new JMenuItem("Options...");
        optionsMenuItem.addActionListener((ActionEvent e) -> {
            final BinEdOptionsPanelBorder optionsPanelWrapper = new BinEdOptionsPanelBorder();
            BinEdOptionsPanel optionsPanel = optionsPanelWrapper.getOptionsPanel();
            optionsPanel.setPreferences(preferences);
            optionsPanel.loadFromPreferences();
            updateApplyOptions(optionsPanel);
            OptionsControlPanel optionsControlPanel = new OptionsControlPanel();
            JPanel dialogPanel = WindowUtils.createDialogPanel(optionsPanelWrapper, optionsControlPanel);
            DialogWrapper dialog = WindowUtils.createDialog(dialogPanel, (Component) e.getSource(), "Options", Dialog.ModalityType.APPLICATION_MODAL);
            optionsControlPanel.setHandler((OptionsControlHandler.ControlActionType actionType) -> {
                if (actionType != OptionsControlHandler.ControlActionType.CANCEL) {
                    optionsPanel.applyToOptions();
                    if (actionType == OptionsControlHandler.ControlActionType.SAVE) {
                        optionsPanel.saveToPreferences();
                    }
                    applyOptions(optionsPanel);
                    codeArea.repaint();
                }

                dialog.close();
                dialog.dispose();
            });
            dialog.getWindow().setSize(650, 460);
            dialog.showCentered((Component) e.getSource());
        });
        result.add(optionsMenuItem);

        switch (positionZone) {
            case TOP_LEFT_CORNER:
            case HEADER:
            case ROW_POSITIONS: {
                break;
            }
            default: {
                result.addSeparator();
                final JMenuItem aboutMenuItem = new JMenuItem("About...");
                aboutMenuItem.addActionListener((ActionEvent e) -> {
                    AboutPanel aboutPanel = new AboutPanel();
                    aboutPanel.setupFields();
                    CloseControlPanel closeControlPanel = new CloseControlPanel();
                    JPanel dialogPanel = WindowUtils.createDialogPanel(aboutPanel, closeControlPanel);
                    DialogWrapper dialog = WindowUtils.createDialog(dialogPanel, (Component) e.getSource(), "About Plugin", Dialog.ModalityType.APPLICATION_MODAL);
                    closeControlPanel.setHandler(() -> {
                        dialog.close();
                        dialog.dispose();
                    });
                    //            dialog.setSize(650, 460);
                    dialog.showCentered((Component) e.getSource());
                });
                result.add(aboutMenuItem);
            }
        }

        return result;
    }

    @Nonnull
    private JMenuItem createGoToMenuItem() {
        final JMenuItem goToMenuItem = new JMenuItem("Go To...");
        goToMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, ActionUtils.getMetaMask()));
        goToMenuItem.addActionListener(goToRowAction);
        return goToMenuItem;
    }

    @Nonnull
    private JMenuItem createShowHeaderMenuItem() {
        final JCheckBoxMenuItem showHeader = new JCheckBoxMenuItem("Show Header");
        showHeader.setSelected(codeArea.getLayoutProfile().isShowHeader());
        showHeader.addActionListener(showHeaderAction);
        return showHeader;
    }

    @Nonnull
    private JMenuItem createShowRowPositionMenuItem() {
        final JCheckBoxMenuItem showRowPosition = new JCheckBoxMenuItem("Show Row Position");
        showRowPosition.setSelected(codeArea.getLayoutProfile().isShowRowPosition());
        showRowPosition.addActionListener(showRowNumbersAction);
        return showRowPosition;
    }

    @Nonnull
    private JMenuItem createPositionCodeTypeMenuItem() {
        JMenu menu = new JMenu("Position Code Type");
        PositionCodeType codeType = codeArea.getPositionCodeType();

        final JRadioButtonMenuItem octalCodeTypeMenuItem = new JRadioButtonMenuItem("Octal");
        octalCodeTypeMenuItem.setSelected(codeType == PositionCodeType.OCTAL);
        octalCodeTypeMenuItem.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                codeArea.setPositionCodeType(PositionCodeType.OCTAL);
                preferences.getCodeAreaPreferences().setPositionCodeType(PositionCodeType.OCTAL);
            }
        });
        menu.add(octalCodeTypeMenuItem);

        final JRadioButtonMenuItem decimalCodeTypeMenuItem = new JRadioButtonMenuItem("Decimal");
        decimalCodeTypeMenuItem.setSelected(codeType == PositionCodeType.DECIMAL);
        decimalCodeTypeMenuItem.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                codeArea.setPositionCodeType(PositionCodeType.DECIMAL);
                preferences.getCodeAreaPreferences().setPositionCodeType(PositionCodeType.DECIMAL);
            }
        });
        menu.add(decimalCodeTypeMenuItem);

        final JRadioButtonMenuItem hexadecimalCodeTypeMenuItem = new JRadioButtonMenuItem("Hexadecimal");
        hexadecimalCodeTypeMenuItem.setSelected(codeType == PositionCodeType.HEXADECIMAL);
        hexadecimalCodeTypeMenuItem.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                codeArea.setPositionCodeType(PositionCodeType.HEXADECIMAL);
                preferences.getCodeAreaPreferences().setPositionCodeType(PositionCodeType.HEXADECIMAL);
            }
        });
        menu.add(hexadecimalCodeTypeMenuItem);

        return menu;
    }

    private void updateApplyOptions(BinEdApplyOptions applyOptions) {
        CodeAreaOptionsImpl.applyFromCodeArea(applyOptions.getCodeAreaOptions(), codeArea);
        applyOptions.getEncodingOptions().setSelectedEncoding(((CharsetCapable) codeArea).getCharset().name());

        EditorOptions editorOptions = applyOptions.getEditorOptions();
        editorOptions.setShowValuesPanel(valuesPanelVisible);
        editorOptions.setFileHandlingMode(fileHandlingMode);
        editorOptions.setEnterKeyHandlingMode(((CodeAreaOperationCommandHandler) codeArea.getCommandHandler()).getEnterKeyHandlingMode());

        // TODO applyOptions.getStatusOptions().loadFromPreferences(preferences.getStatusPreferences());
    }

    private void applyOptions(BinEdApplyOptions applyOptions) {
        CodeAreaOptionsImpl.applyToCodeArea(applyOptions.getCodeAreaOptions(), codeArea);

        ((CharsetCapable) codeArea).setCharset(Charset.forName(applyOptions.getEncodingOptions().getSelectedEncoding()));
        encodingsHandler.setEncodings(applyOptions.getEncodingOptions().getEncodings());

        EditorOptions editorOptions = applyOptions.getEditorOptions();
        switchShowValuesPanel(editorOptions.isShowValuesPanel());
        ((CodeAreaOperationCommandHandler) codeArea.getCommandHandler()).setEnterKeyHandlingMode(editorOptions.getEnterKeyHandlingMode());
        switchFileHandlingMode(editorOptions.getFileHandlingMode());

        StatusOptions statusOptions = applyOptions.getStatusOptions();
        statusPanel.setStatusOptions(statusOptions);
        toolbarPanel.applyFromCodeArea();

        CodeAreaLayoutOptions layoutOptions = applyOptions.getLayoutOptions();
        int selectedLayoutProfile = layoutOptions.getSelectedProfile();
        if (selectedLayoutProfile >= 0) {
            codeArea.setLayoutProfile(layoutOptions.getLayoutProfile(selectedLayoutProfile));
        } else {
            codeArea.setLayoutProfile(defaultLayoutProfile);
        }

        CodeAreaThemeOptions themeOptions = applyOptions.getThemeOptions();
        int selectedThemeProfile = themeOptions.getSelectedProfile();
        if (selectedThemeProfile >= 0) {
            codeArea.setThemeProfile(themeOptions.getThemeProfile(selectedThemeProfile));
        } else {
            codeArea.setThemeProfile(defaultThemeProfile);
        }

        CodeAreaColorOptions colorOptions = applyOptions.getColorOptions();
        int selectedColorProfile = colorOptions.getSelectedProfile();
        if (selectedColorProfile >= 0) {
            codeArea.setColorsProfile(colorOptions.getColorsProfile(selectedColorProfile));
        } else {
            codeArea.setColorsProfile(defaultColorProfile);
        }
    }

    public void showValuesPanel() {
        if (!valuesPanelVisible) {
            valuesPanelVisible = true;
            if (valuesPanel == null) {
                valuesPanel = new ValuesPanel();
                valuesPanel.setCodeArea(codeArea, undoHandler);
                valuesPanelScrollPane = new JScrollPane(valuesPanel);
                valuesPanelScrollPane.setBorder(null);
            }
            codeAreaPanel.add(valuesPanelScrollPane, BorderLayout.EAST);
            valuesPanel.enableUpdate();
            valuesPanel.updateValues();
            codeAreaPanel.revalidate();
            revalidate();
        }
    }

    public void hideValuesPanel() {
        if (valuesPanelVisible) {
            valuesPanelVisible = false;
            valuesPanel.disableUpdate();
            codeAreaPanel.remove(valuesPanelScrollPane);
            codeAreaPanel.revalidate();
            revalidate();
        }
    }

    public ExtCodeArea getCodeArea() {
        return codeArea;
    }

    private void saveFileButtonActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            save();
        } catch (IOException ex) {
            Logger.getLogger(BinEdEditorSwing.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void initialLoadFromPreferences() {
        applyOptions(new BinEdApplyOptions() {
            @Override
            public CodeAreaOptions getCodeAreaOptions() {
                return preferences.getCodeAreaPreferences();
            }

            @Override
            public TextEncodingOptions getEncodingOptions() {
                return preferences.getEncodingPreferences();
            }

            @Override
            public EditorOptions getEditorOptions() {
                return preferences.getEditorPreferences();
            }

            @Override
            public StatusOptions getStatusOptions() {
                return preferences.getStatusPreferences();
            }

            @Override
            public CodeAreaLayoutOptions getLayoutOptions() {
                return preferences.getLayoutPreferences();
            }

            @Override
            public CodeAreaColorOptions getColorOptions() {
                return preferences.getColorPreferences();
            }

            @Override
            public CodeAreaThemeOptions getThemeOptions() {
                return preferences.getThemePreferences();
            }
        });

        encodingsHandler.loadFromPreferences(preferences.getEncodingPreferences());
        statusPanel.loadFromPreferences(preferences.getStatusPreferences());
        toolbarPanel.loadFromPreferences();

        fileHandlingMode = preferences.getEditorPreferences().getFileHandlingMode();
    }

    private void revalidate() {
    	// TODO repaint
    }
    
    public static interface CharsetChangeListener {

        void charsetChanged();
    }
    
    public static interface ActionsStateListener {
    	void changed();
    }

	public void requestFocus() {
		codeArea.requestFocus();
	}
}
