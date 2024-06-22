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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.event.ChangeListener;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.part.FileEditorInput;
import org.exbin.auxiliary.binary_data.BinaryData;
import org.exbin.auxiliary.binary_data.EditableBinaryData;
import org.exbin.auxiliary.binary_data.delta.DeltaDocument;
import org.exbin.auxiliary.binary_data.delta.FileDataSource;
import org.exbin.auxiliary.binary_data.paged.PagedData;
import org.exbin.bined.EditMode;
import org.exbin.bined.operation.swing.CodeAreaUndoHandler;
import org.exbin.bined.swing.extended.ExtCodeArea;
import org.exbin.framework.bined.BinEdFileHandler;
import org.exbin.framework.bined.FileHandlingMode;
import org.exbin.framework.bined.UndoHandlerWrapper;
import org.exbin.xbup.operation.Command;
import org.exbin.xbup.operation.undo.XBUndoUpdateListener;

/**
 * File editor wrapper using BinEd editor component.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class BinEdNativeFile extends BinEdFileHandler {

    private IEditorInput dataObject;
    private ChangeListener changeListener;

    public BinEdNativeFile() {
    	super();

    	ExtCodeArea codeArea = getCodeArea();
        CodeAreaUndoHandler undoHandler = new CodeAreaUndoHandler(codeArea);
        getEditorComponent().setUndoHandler(undoHandler);
        ((UndoHandlerWrapper) getUndoHandler()).setHandler(undoHandler);
		getUndoHandler().addUndoUpdateListener(new XBUndoUpdateListener() {
			
			@Override
			public void undoCommandPositionChanged() {
				notifyChanged();
			}
			
			@Override
			public void undoCommandAdded(Command command) {
				notifyChanged();
			}
		});
        BinEdManager binEdManager = BinEdManager.getInstance();
        binEdManager.getFileManager().initCommandHandler(getComponent());
    }

    public IEditorInput getContent() {
        return dataObject;
    }

    public void openFile(IEditorInput dataObject) {
        this.dataObject = dataObject;
		if (dataObject instanceof FileEditorInput) {
			IFile file = ((FileEditorInput) dataObject).getFile();
			IPath path = file.getLocation();
			try {
				File documentFile = path.toFile();
				openDocument(documentFile, documentFile.canWrite());
		        getUndoHandler().clear();
		        fileSync();
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
        ExtCodeArea codeArea = getCodeArea();
        FileHandlingMode fileHandlingMode = getFileHandlingMode();

        BinaryData oldData = codeArea.getContentData();
        if (fileHandlingMode == FileHandlingMode.DELTA) {
            FileDataSource fileSource = segmentsRepository.openFileSource(file, editable ? FileDataSource.EditMode.READ_WRITE : FileDataSource.EditMode.READ_ONLY);
            DeltaDocument document = segmentsRepository.createDocument(fileSource);
            getEditorComponent().setContentData(document);
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
                getEditorComponent().setContentData(data);
            }
        }
        fileSync();
        codeArea.setEditMode(editable ? EditMode.EXPANDING : EditMode.READ_ONLY);
    }

    public void openDocument(InputStream stream, boolean editable) throws IOException {
        ExtCodeArea codeArea = getCodeArea();
        setNewData(getFileHandlingMode());
        EditableBinaryData data = Objects.requireNonNull((EditableBinaryData) codeArea.getContentData());
        data.loadFromStream(stream);
        codeArea.setEditMode(editable ? EditMode.EXPANDING : EditMode.READ_ONLY);
        getEditorComponent().setContentData(data);
        fileSync();
    }

    public void saveFile() {
        if (dataObject instanceof FileEditorInput) {
			IFile file = ((FileEditorInput) dataObject).getFile();
			IPath path = file.getLocation();

	        FileHandlingMode fileHandlingMode = getFileHandlingMode();
	        ExtCodeArea codeArea = getCodeArea();

			try {
		        BinaryData data = codeArea.getContentData();
		        if (fileHandlingMode == FileHandlingMode.MEMORY) {
		            data.saveToStream(new FileOutputStream(path.toFile()));
		        } else {
		            DeltaDocument document = (DeltaDocument) data;
		            document.save();
		        }
		        fileSync();
		        notifyChanged();
		        getEditorComponent().getToolbarPanel().updateUndoState();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
//        setModified(false);
    }

    public void saveFile(File file) {
        FileHandlingMode fileHandlingMode = getFileHandlingMode();
        ExtCodeArea codeArea = getCodeArea();

		try {
	        BinaryData data = codeArea.getContentData();
	        if (fileHandlingMode == FileHandlingMode.MEMORY) {
	            data.saveToStream(new FileOutputStream(file));
	        } else {
	        	throw new UnsupportedOperationException("Not supported yet.");
//	            DeltaDocument document = (DeltaDocument) data;
//	            document.save();
	        }
	        fileSync();
	        notifyChanged();
	        getEditorComponent().getToolbarPanel().updateUndoState();
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
    public void saveDocument() {
        if (dataObject == null) {
            return;
        }

        saveFile();
    }
    
    public void setChangeListener(ChangeListener changeListener) {
    	this.changeListener = changeListener;
    }
    
    public void notifyChanged() {
    	if (changeListener != null) {
    		changeListener.stateChanged(null);
    	}
    }
    
    @Nonnull
    @Override
    public String getTitle() {
        return dataObject.getName();
    }
}
