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
import javax.swing.KeyStroke;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.part.FileEditorInput;
import org.exbin.bined.BasicCodeAreaZone;
import org.exbin.bined.CodeAreaCaretPosition;
import org.exbin.bined.EditationMode;
import org.exbin.bined.EditationOperation;
import org.exbin.bined.PositionCodeType;
import org.exbin.bined.delta.DeltaDocument;
import org.exbin.bined.delta.FileDataSource;
import org.exbin.bined.delta.SegmentsRepository;
import org.exbin.bined.eclipse.BinEdApplyOptions;
import org.exbin.bined.eclipse.FileHandlingMode;
import org.exbin.bined.eclipse.GoToPositionAction;
import org.exbin.bined.eclipse.SearchAction;
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
import org.exbin.bined.swing.extended.ExtCodeArea;
import org.exbin.framework.PreferencesWrapper;
import org.exbin.framework.bined.BinaryStatusApi;
import org.exbin.framework.bined.options.CodeAreaOptions;
import org.exbin.framework.bined.options.EditorOptions;
import org.exbin.framework.bined.options.StatusOptions;
import org.exbin.framework.bined.panel.BinaryStatusPanel;
import org.exbin.framework.bined.panel.ValuesPanel;
import org.exbin.framework.bined.preferences.BinaryEditorPreferences;
import org.exbin.framework.editor.text.EncodingsHandler;
import org.exbin.framework.editor.text.TextEncodingStatusApi;
import org.exbin.framework.gui.about.panel.AboutPanel;
import org.exbin.framework.gui.utils.WindowUtils;
import org.exbin.framework.gui.utils.WindowUtils.DialogWrapper;
import org.exbin.framework.gui.utils.handler.OptionsControlHandler;
import org.exbin.framework.gui.utils.panel.CloseControlPanel;
import org.exbin.framework.gui.utils.panel.OptionsControlPanel;
import org.exbin.utils.binary_data.BinaryData;
import org.exbin.utils.binary_data.ByteArrayData;
import org.exbin.utils.binary_data.ByteArrayEditableData;
import org.exbin.utils.binary_data.EditableBinaryData;
import org.exbin.utils.binary_data.PagedData;

public final class BinEdEditorSwing {

    private static final FileHandlingMode DEFAULT_FILE_HANDLING_MODE = FileHandlingMode.DELTA;

    private final BinaryEditorPreferences preferences;
    private static SegmentsRepository segmentsRepository = null;
    private final ExtCodeArea codeArea;
    private final CodeAreaUndoHandler undoHandler;
    private final int metaMask;

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

    public BinEdEditorSwing() {

		initComponents();

		preferences = new BinaryEditorPreferences(new PreferencesWrapper(BinEdPlugin.getDefault().getPreferenceStore()));

        codeArea = new ExtCodeArea();
        codeArea.setPainter(new ExtendedHighlightNonAsciiCodeAreaPainter(codeArea));
        codeArea.setCodeFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        codeArea.getCaret().setBlinkRate(300);

        toolbarPanel = new BinEdToolbarPanel(preferences, codeArea);
        statusPanel = new BinaryStatusPanel();
        codeAreaPanel.add(toolbarPanel, BorderLayout.NORTH);
        registerEncodingStatus(statusPanel);
        encodingsHandler = new EncodingsHandler(new TextEncodingStatusApi() {
            @Override
            public String getEncoding() {
                return encodingStatus.getEncoding();
            }

            @Override
            public void setEncoding(String encodingName) {
                codeArea.setCharset(Charset.forName(encodingName));
                encodingStatus.setEncoding(encodingName);
                preferences.getCodeAreaParameters().setSelectedEncoding(encodingName);
                charsetChangeListener.charsetChanged();
            }
        });

        undoHandler = new CodeAreaUndoHandler(codeArea);

        loadFromPreferences();

        getSegmentsRepository();
        setNewData();
        CodeAreaOperationCommandHandler commandHandler = new CodeAreaOperationCommandHandler(codeArea, undoHandler);
        codeArea.setCommandHandler(commandHandler);
        codeAreaPanel.add(codeArea, BorderLayout.CENTER);
        registerBinaryStatus(statusPanel);
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
                JPopupMenu popupMenu = createContextMenu(x, y);
                popupMenu.show(invoker, x, y);
            }
        });

        undoHandler.addUndoUpdateListener(new BinaryDataUndoUpdateListener() {
            @Override
            public void undoCommandPositionChanged() {
                codeArea.repaint();
                updateCurrentDocumentSize();
                updateModified();
            }

            @Override
            public void undoCommandAdded(final BinaryDataCommand command) {
                updateCurrentDocumentSize();
                updateModified();
            }
        });

        int metaMaskValue;
        try {
            metaMaskValue = java.awt.Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
        } catch (java.awt.HeadlessException ex) {
            metaMaskValue = java.awt.Event.CTRL_MASK;
        }
        metaMask = metaMaskValue;

        searchAction = new SearchAction(codeArea, codeAreaPanel, metaMask);
        codeArea.addDataChangedListener(() -> {
            searchAction.codeAreaDataChanged();
            updateCurrentDocumentSize();
        });

        toolbarPanel.applyFromCodeArea();

        codeArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent keyEvent) {
                if (keyEvent.getModifiers() == metaMask) {
                    int keyCode = keyEvent.getKeyCode();
                    switch (keyCode) {
                        case KeyEvent.VK_F: {
                            searchAction.actionPerformed(null);
                            searchAction.switchReplaceMode(BinarySearchPanel.SearchOperation.FIND);
                            break;
                        }
                        case KeyEvent.VK_G: {
                            goToRowAction.actionPerformed(null);
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
                goToRowAction.actionPerformed(null);
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
                    switchDeltaMemoryMode(newHandlingMode);
                    preferences.getEditorParameters().setFileHandlingMode(newHandlingMode.name());
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

    private void switchDeltaMemoryMode(FileHandlingMode newHandlingMode) {
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
                cutMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, metaMask));
                cutMenuItem.setEnabled(codeArea.hasSelection() && codeArea.isEditable());
                cutMenuItem.addActionListener((ActionEvent e) -> {
                    codeArea.cut();
                    result.setVisible(false);
                });
                result.add(cutMenuItem);

                final JMenuItem copyMenuItem = new JMenuItem("Copy");
                copyMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, metaMask));
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
                pasteMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, metaMask));
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
                selectAllMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, metaMask));
                selectAllMenuItem.addActionListener((ActionEvent e) -> {
                    codeArea.selectAll();
                    result.setVisible(false);
                });
                result.add(selectAllMenuItem);
                result.addSeparator();

                JMenuItem goToMenuItem = createGoToMenuItem();
                result.add(goToMenuItem);

                final JMenuItem findMenuItem = new JMenuItem("Find...");
                findMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, metaMask));
                findMenuItem.addActionListener((ActionEvent e) -> {
                    searchAction.actionPerformed(e);
                    searchAction.switchReplaceMode(BinarySearchPanel.SearchOperation.FIND);
                });
                result.add(findMenuItem);

                final JMenuItem replaceMenuItem = new JMenuItem("Replace...");
                replaceMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H, metaMask));
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
            final BinEdOptionsPanelBorder optionsPanel = new BinEdOptionsPanelBorder();
            optionsPanel.load();
            optionsPanel.setApplyOptions(getApplyOptions());
            OptionsControlPanel optionsControlPanel = new OptionsControlPanel();
            JPanel dialogPanel = WindowUtils.createDialogPanel(optionsPanel, optionsControlPanel);
            DialogWrapper dialog = WindowUtils.createDialog(dialogPanel, null, "Options", Dialog.ModalityType.MODELESS);
            optionsControlPanel.setHandler((OptionsControlHandler.ControlActionType actionType) -> {
                if (actionType == OptionsControlHandler.ControlActionType.SAVE) {
                    optionsPanel.store();
                }
                if (actionType != OptionsControlHandler.ControlActionType.CANCEL) {
                    setApplyOptions(optionsPanel.getApplyOptions());
                    encodingsHandler.setEncodings(optionsPanel.getApplyOptions().getCharsetOptions().getEncodings());
                    codeArea.repaint();
                }

                dialog.close();
            });
            WindowUtils.assignGlobalKeyListener(dialog.getWindow(), optionsControlPanel.createOkCancelListener());
            dialog.getWindow().setSize(650, 460);
            dialog.show();
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
                    DialogWrapper dialog = WindowUtils.createDialog(dialogPanel, null, "About Plugin", Dialog.ModalityType.APPLICATION_MODAL);
                    closeControlPanel.setHandler(() -> {
                        dialog.close();
                    });
                    WindowUtils.assignGlobalKeyListener(dialog.getWindow(), closeControlPanel.createOkCancelListener());
                    //            dialog.setSize(650, 460);
                    dialog.show();
                });
                result.add(aboutMenuItem);
            }
        }

        return result;
    }

    @Nonnull
    private JMenuItem createGoToMenuItem() {
        final JMenuItem goToMenuItem = new JMenuItem("Go To...");
        goToMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, metaMask));
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
                preferences.getCodeAreaParameters().setPositionCodeType(PositionCodeType.OCTAL);
            }
        });
        menu.add(octalCodeTypeMenuItem);

        final JRadioButtonMenuItem decimalCodeTypeMenuItem = new JRadioButtonMenuItem("Decimal");
        decimalCodeTypeMenuItem.setSelected(codeType == PositionCodeType.DECIMAL);
        decimalCodeTypeMenuItem.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                codeArea.setPositionCodeType(PositionCodeType.DECIMAL);
                preferences.getCodeAreaParameters().setPositionCodeType(PositionCodeType.DECIMAL);
            }
        });
        menu.add(decimalCodeTypeMenuItem);

        final JRadioButtonMenuItem hexadecimalCodeTypeMenuItem = new JRadioButtonMenuItem("Hexadecimal");
        hexadecimalCodeTypeMenuItem.setSelected(codeType == PositionCodeType.HEXADECIMAL);
        hexadecimalCodeTypeMenuItem.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                codeArea.setPositionCodeType(PositionCodeType.HEXADECIMAL);
                preferences.getCodeAreaParameters().setPositionCodeType(PositionCodeType.HEXADECIMAL);
            }
        });
        menu.add(hexadecimalCodeTypeMenuItem);

        return menu;
    }

    @Nonnull
    private BinEdApplyOptions getApplyOptions() {
        BinEdApplyOptions applyOptions = new BinEdApplyOptions();
        applyOptions.applyFromCodeArea(codeArea);
        EditorOptions editorOptions = applyOptions.getEditorOptions();
        editorOptions.setIsShowValuesPanel(valuesPanelVisible);
        editorOptions.setFileHandlingMode(fileHandlingMode.name());
        applyOptions.getStatusOptions().loadFromParameters(preferences.getStatusParameters());
        return applyOptions;
    }

    private void setApplyOptions(BinEdApplyOptions applyOptions) {
        applyOptions.applyToCodeArea(codeArea);
        EditorOptions editorOptions = applyOptions.getEditorOptions();
        switchShowValuesPanel(editorOptions.isIsShowValuesPanel());

        FileHandlingMode newFileHandlingMode;
        try {
            newFileHandlingMode = FileHandlingMode.valueOf(editorOptions.getFileHandlingMode());
        } catch (Exception ex) {
            newFileHandlingMode = DEFAULT_FILE_HANDLING_MODE;
        }
        switchDeltaMemoryMode(newFileHandlingMode);

        StatusOptions statusOptions = applyOptions.getStatusOptions();
        statusPanel.setStatusOptions(statusOptions);
        toolbarPanel.applyFromCodeArea();

        int selectedLayoutProfile = preferences.getLayoutParameters().getSelectedProfile();
        if (selectedLayoutProfile >= 0) {
            codeArea.setLayoutProfile(preferences.getLayoutParameters().getLayoutProfile(selectedLayoutProfile));
        }

        int selectedThemeProfile = preferences.getThemeParameters().getSelectedProfile();
        if (selectedThemeProfile >= 0) {
            codeArea.setThemeProfile(preferences.getThemeParameters().getThemeProfile(selectedThemeProfile));
        }

        int selectedColorProfile = preferences.getColorParameters().getSelectedProfile();
        if (selectedColorProfile >= 0) {
            codeArea.setColorsProfile(preferences.getColorParameters().getColorsProfile(selectedColorProfile));
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

    private void loadFromPreferences() {
        try {
            fileHandlingMode = FileHandlingMode.valueOf(preferences.getEditorParameters().getFileHandlingMode());
        } catch (Exception ex) {
            fileHandlingMode = DEFAULT_FILE_HANDLING_MODE;
        }
        CodeAreaOptions codeAreaOptions = new CodeAreaOptions();
        codeAreaOptions.loadFromParameters(preferences.getCodeAreaParameters());
        codeAreaOptions.applyToCodeArea(codeArea);
        String selectedEncoding = preferences.getCodeAreaParameters().getSelectedEncoding();
        statusPanel.setEncoding(selectedEncoding);
        statusPanel.loadFromPreferences(preferences.getStatusParameters());
        toolbarPanel.loadFromPreferences();

        codeArea.setCharset(Charset.forName(selectedEncoding));
        encodingsHandler.loadFromPreferences(preferences);

        int selectedLayoutProfile = preferences.getLayoutParameters().getSelectedProfile();
        if (selectedLayoutProfile >= 0) {
            codeArea.setLayoutProfile(preferences.getLayoutParameters().getLayoutProfile(selectedLayoutProfile));
        }

        int selectedThemeProfile = preferences.getThemeParameters().getSelectedProfile();
        if (selectedThemeProfile >= 0) {
            codeArea.setThemeProfile(preferences.getThemeParameters().getThemeProfile(selectedThemeProfile));
        }

        int selectedColorProfile = preferences.getColorParameters().getSelectedProfile();
        if (selectedColorProfile >= 0) {
            codeArea.setColorsProfile(preferences.getColorParameters().getColorsProfile(selectedColorProfile));
        }

        // Memory mode handled from outside by isDeltaMemoryMode() method, worth fixing?
        boolean showValuesPanel = preferences.getEditorParameters().isShowValuesPanel();
        if (showValuesPanel) {
            showValuesPanel();
        }
    }
    
    private void revalidate() {
    	// TODO repaint
    }
    
    public static interface CharsetChangeListener {

        public void charsetChanged();
    }

	public void requestFocus() {
		codeArea.requestFocus();
	}
}
