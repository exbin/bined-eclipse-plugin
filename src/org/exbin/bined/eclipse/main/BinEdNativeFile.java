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
package org.exbin.bined.eclipse.main;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Objects;
import java.util.Optional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.part.FileEditorInput;
import org.exbin.auxiliary.binary_data.BinaryData;
import org.exbin.auxiliary.binary_data.ByteArrayData;
import org.exbin.auxiliary.binary_data.EditableBinaryData;
import org.exbin.auxiliary.binary_data.paged.PagedData;
import org.exbin.auxiliary.binary_data.delta.DeltaDocument;
import org.exbin.auxiliary.binary_data.delta.FileDataSource;
import org.exbin.auxiliary.binary_data.delta.SegmentsRepository;
import org.exbin.bined.EditMode;
import org.exbin.bined.operation.swing.CodeAreaUndoHandler;
import org.exbin.bined.operation.undo.BinaryDataUndoHandler;
import org.exbin.bined.swing.extended.ExtCodeArea;
import org.exbin.framework.bined.BinEdEditorComponent;
import org.exbin.framework.bined.FileHandlingMode;
import org.exbin.framework.bined.gui.BinEdComponentFileApi;
import org.exbin.framework.bined.gui.BinEdComponentPanel;
import org.exbin.framework.editor.text.TextFontApi;
import org.exbin.framework.file.api.FileHandler;
import org.exbin.framework.file.api.FileType;

/**
 * File editor wrapper using BinEd editor component.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class BinEdNativeFile implements FileHandler, BinEdComponentFileApi, TextFontApi {

    private SegmentsRepository segmentsRepository;

    private final BinEdEditorComponent editorComponent;

    private IEditorInput dataObject;
    private Font defaultFont;
    private long documentOriginalSize;

    public BinEdNativeFile() {
        BinEdManager binEdManager = BinEdManager.getInstance();
        editorComponent = new BinEdEditorComponent();
        binEdManager.getFileManager().initComponentPanel(editorComponent.getComponentPanel());
        binEdManager.initFileHandler(this);

        ExtCodeArea codeArea = editorComponent.getCodeArea();
        CodeAreaUndoHandler undoHandler = new CodeAreaUndoHandler(codeArea);
        editorComponent.setUndoHandler(undoHandler);
/*
        ActionMap actionMap = componentPanel.getActionMap();
        actionMap.put(ACTION_CLIPBOARD_COPY, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                codeArea.copy();
            }
        });
        actionMap.put(ACTION_CLIPBOARD_CUT, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                codeArea.cut();
            }
        });
        actionMap.put(ACTION_CLIPBOARD_PASTE, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                codeArea.paste();
            }
        }); */
    }

    public boolean isModified() {
        return editorComponent.isModified();
    }

    public IEditorInput getContent() {
        return dataObject;
    }

    public void openFile(IEditorInput dataObject) {
        this.dataObject = dataObject;
		documentOriginalSize = 0; 
		if (dataObject instanceof FileEditorInput) {
			IFile file = ((FileEditorInput) dataObject).getFile();
			IPath path = file.getLocation();
			try {
				File documentFile = path.toFile();
				openDocument(documentFile, documentFile.canWrite());
				documentOriginalSize = documentFile.length();
		        getUndoHandler().clear();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
//	        displayName = dataObject.getPrimaryFile().getNameExt();
//	        setHtmlDisplayName(displayName);
//	        node.openFile(dataObject);
//	        savable.setDataObject(dataObject);
//	        opened = true;

//	        final Charset charset = Charset.forName(FileEncodingQuery.getEncoding(dataObject.getPrimaryFile()).name());
//	        if (charsetChangeListener != null) {
//	            charsetChangeListener.charsetChanged();
	//	        }
//	        codeArea.setCharset(charset);
		}
    }

    public void openDocument(File file, boolean editable) throws IOException {
        ExtCodeArea codeArea = editorComponent.getCodeArea();
        FileHandlingMode fileHandlingMode = getFileHandlingMode();

        BinaryData oldData = codeArea.getContentData();
        if (fileHandlingMode == FileHandlingMode.DELTA) {
            FileDataSource fileSource = segmentsRepository.openFileSource(file, editable ? FileDataSource.EditMode.READ_WRITE : FileDataSource.EditMode.READ_ONLY);
            DeltaDocument document = segmentsRepository.createDocument(fileSource);
            editorComponent.setContentData(document);
            if (oldData != null) {
                oldData.dispose();
            }
        } else {
            try (FileInputStream fileStream = new FileInputStream(file)) {
                BinaryData data = codeArea.getContentData();
                if (!(data instanceof PagedData)) {
                    data = new PagedData();
                    if (oldData != null) {
                        oldData.dispose();
                    }
                }
                ((EditableBinaryData) data).loadFromStream(fileStream);
                editorComponent.setContentData(data);
            }
        }
        codeArea.setEditMode(editable ? EditMode.EXPANDING : EditMode.READ_ONLY);
    }

    public void openDocument(InputStream stream, boolean editable) throws IOException {
        ExtCodeArea codeArea = editorComponent.getCodeArea();
        setNewData(getFileHandlingMode());
        EditableBinaryData data = Objects.requireNonNull((EditableBinaryData) codeArea.getContentData());
        data.loadFromStream(stream);
        codeArea.setEditMode(editable ? EditMode.EXPANDING : EditMode.READ_ONLY);
        editorComponent.setContentData(data);
    }

    public void saveFile() {
        if (dataObject instanceof FileEditorInput) {
			IFile file = ((FileEditorInput) dataObject).getFile();
			IPath path = file.getLocation();

	        FileHandlingMode fileHandlingMode = getFileHandlingMode();
	        ExtCodeArea codeArea = editorComponent.getCodeArea();

			try {
		        BinaryData data = codeArea.getContentData();
		        if (fileHandlingMode == FileHandlingMode.MEMORY) {
		            data.saveToStream(new FileOutputStream(path.toFile()));
		        } else {
		            DeltaDocument document = (DeltaDocument) data;
		            document.save();
		        }
		        getUndoHandler().setSyncPoint();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
//        setModified(false);
    }

    public void saveFile(File file) {
        FileHandlingMode fileHandlingMode = getFileHandlingMode();
        ExtCodeArea codeArea = editorComponent.getCodeArea();

		try {
	        BinaryData data = codeArea.getContentData();
	        if (fileHandlingMode == FileHandlingMode.MEMORY) {
	            data.saveToStream(new FileOutputStream(file));
	        } else {
	        	throw new UnsupportedOperationException("Not supported yet.");
//	            DeltaDocument document = (DeltaDocument) data;
//	            document.save();
	        }
	        getUndoHandler().setSyncPoint();
            IWorkspace workspace = ResourcesPlugin.getWorkspace();
    		dataObject = new FileEditorInput(workspace.getRoot().getFileForLocation(org.eclipse.core.runtime.Path.fromOSString(file.getAbsolutePath())));
		} catch (IOException ex) {
			ex.printStackTrace();
		}
    }

    public void reloadFile() {
        openFile(dataObject);
    }

    @Override
    public void closeData() {
        ExtCodeArea codeArea = editorComponent.getCodeArea();
        BinaryData data = codeArea.getContentData();
        editorComponent.setContentData(new ByteArrayData());
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

    @Override
    public void saveDocument() {
        if (dataObject == null) {
            return;
        }

        saveFile();
    }
    
    public BinaryDataUndoHandler getUndoHandler() {
    	return editorComponent.getComponentPanel().getUndoHandler().get();
    }

    @Nonnull
    public JComponent getComponent() {
        return editorComponent.getComponent();
    }

    @Nonnull
    @Override
    public BinEdEditorComponent getEditorComponent() {
        return editorComponent;
    }
    @Nonnull
    @Override
    public ExtCodeArea getCodeArea() {
    	return editorComponent.getCodeArea();
    }

    @Override
    public long getDocumentOriginalSize() {
        return documentOriginalSize;
    }

    @Override
    public int getId() {
        return -1;
    }

    @Nonnull
    @Override
    public Optional<URI> getFileUri() {
        return Optional.empty();
    }

    @Nonnull
    @Override
    public String getTitle() {
        return dataObject.getName();
    }

    @Nonnull
    @Override
    public Optional<FileType> getFileType() {
        return Optional.empty();
    }

    @Override
    public void setFileType(@Nullable FileType fileType) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clearFile() {

    }

    @Override
    public void switchFileHandlingMode(FileHandlingMode handlingMode) {
        FileHandlingMode oldFileHandlingMode = getFileHandlingMode();
        ExtCodeArea codeArea = editorComponent.getCodeArea();
        if (handlingMode != oldFileHandlingMode) {
            if (dataObject != null) {
                openFile(dataObject);
            } else {
                BinaryData oldData = codeArea.getContentData();
                if (oldData instanceof DeltaDocument) {
                    PagedData data = new PagedData();
                    data.insert(0, oldData);
                    editorComponent.setContentData(data);
                } else {
                    DeltaDocument document = segmentsRepository.createDocument();
                    document.insert(0, oldData);
                    editorComponent.setContentData(document);
                }

            	getUndoHandler().clear();

                oldData.dispose();
            }
        }
    }

    @Nonnull
    public FileHandlingMode getFileHandlingMode() {
        return getCodeArea().getContentData() instanceof DeltaDocument ? FileHandlingMode.DELTA : FileHandlingMode.MEMORY;
    }

    /*
    @Override
    public void switchFileHandlingMode(FileHandlingMode newHandlingMode) {
        FileHandlingMode fileHandlingMode = componentPanel.getFileHandlingMode();
        ExtCodeArea codeArea = componentPanel.getCodeArea();
        if (newHandlingMode != fileHandlingMode) {
            // Switch memory mode
            if (dataObject != null) {
                // If document is connected to file, attempt to release first if modified and then simply reload
                if (isModified()) {
                    if (releaseFile()) {
                        openFile(dataObject);
                        codeArea.clearSelection();
                        codeArea.setCaretPosition(0);
                        componentPanel.setFileHandlingMode(newHandlingMode);
                    }
                } else {
                    componentPanel.setFileHandlingMode(newHandlingMode);
                    openFile(dataObject);
                }
            } else {
                // If document unsaved in memory, switch data in code area
                if (codeArea.getContentData() instanceof DeltaDocument) {
                    BinaryData oldData = codeArea.getContentData();
                    PagedData data = new PagedData();
                    data.insert(0, codeArea.getContentData());
                    componentPanel.setContentData(data);
                    if (oldData != null) {
                        oldData.dispose();
                    }
                } else {
                    BinaryData oldData = codeArea.getContentData();
                    DeltaDocument document = segmentsRepository.createDocument();
                    if (oldData != null) {
                        document.insert(0, oldData);
                        oldData.dispose();
                    }
                    componentPanel.setContentData(document);
                }

                undoHandler.clear();
                componentPanel.setFileHandlingMode(newHandlingMode);
            }
        }
    } */

    @Override
    public boolean isSaveSupported() {
        return true;
    }

    public void setNewData(FileHandlingMode fileHandlingMode) {
        if (fileHandlingMode == FileHandlingMode.DELTA) {
            editorComponent.setContentData(segmentsRepository.createDocument());
        } else {
            editorComponent.setContentData(new PagedData());
        }
    }

    public void setSegmentsRepository(SegmentsRepository segmentsRepository) {
        this.segmentsRepository = segmentsRepository;
    }

    public void requestFocus() {
    	editorComponent.getCodeArea().requestFocus();
    }

    @Override
    public void loadFromFile(URI fileUri, @Nullable FileType fileType) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void saveToFile(URI fileUri, @Nullable FileType fileType) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setCurrentFont(Font font) {
        getCodeArea().setCodeFont(font);
    }

    @Nonnull
    @Override
    public Font getCurrentFont() {
        return getCodeArea().getCodeFont();
    }

    @Nonnull
    @Override
    public Font getDefaultFont() {
        return defaultFont;
    }
}
