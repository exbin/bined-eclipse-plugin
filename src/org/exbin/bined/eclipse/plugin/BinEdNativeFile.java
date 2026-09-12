/*
 * Copyright (C) ExBin Project, https://exbin.org
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
package org.exbin.bined.eclipse.plugin;

import java.awt.Component;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.swing.event.ChangeListener;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.part.FileEditorInput;
import org.exbin.auxiliary.binary_data.BinaryData;
import org.exbin.auxiliary.binary_data.delta.DeltaDocument;
import org.exbin.bined.EditMode;
import org.exbin.bined.capability.EditModeCapable;
import org.exbin.bined.eclipse.gui.BinEdFilePanel;
import org.exbin.bined.jaguif.component.BinEdDataComponent;
import org.exbin.bined.jaguif.document.BinEdFileManager;
import org.exbin.bined.jaguif.document.BinaryFileDocument;
import org.exbin.bined.jaguif.document.BinedDocumentModule;
import org.exbin.bined.jaguif.document.FileProcessingMode;
import org.exbin.bined.jaguif.document.settings.BinaryFileProcessingOptions;
import org.exbin.bined.jaguif.search.BinedSearchModule;
import org.exbin.bined.operation.BinaryDataUndoRedoChangeListener;
import org.exbin.bined.operation.command.BinaryDataUndoRedo;
import org.exbin.bined.swing.CodeAreaCommandHandler;
import org.exbin.bined.swing.section.SectCodeArea;
import org.exbin.jaguif.App;
import org.exbin.jaguif.document.api.StreamDocumentSource;
import org.exbin.jaguif.file.api.FileDocumentSource;
import org.exbin.jaguif.options.api.OptionsModuleApi;
import org.exbin.jaguif.options.api.OptionsStorage;
import org.exbin.jaguif.options.settings.api.OptionsSettingsManagement;
import org.exbin.jaguif.options.settings.api.OptionsSettingsModuleApi;
import org.exbin.jaguif.options.settings.api.SettingsOptionsProvider;
import org.jspecify.annotations.NullMarked;

/**
 * File editor wrapper using BinEd editor component.
 */
@NullMarked
public class BinEdNativeFile {

    protected final BinEdFilePanel filePanel = new BinEdFilePanel();
    protected final BinaryFileDocument fileDocument = new BinaryFileDocument();

    protected IEditorInput dataObject;
    protected ChangeListener changeListener;

    public BinEdNativeFile() {
        BinedDocumentModule binedDocumentModule = App.getModule(BinedDocumentModule.class);
        BinEdFileManager fileManager = binedDocumentModule.getFileManager();
        filePanel.setDocument(fileDocument);
        BinEdDataComponent dataComponent = fileDocument.getDataComponent();
        fileManager.initDataComponent(dataComponent);
        fileManager.initCommandHandler(dataComponent);
        BinedSearchModule searchModule = App.getModule(BinedSearchModule.class);
        dataComponent.setSearchController(searchModule.createBinarySearchController(dataComponent));
    	
        OptionsModuleApi optionsModule = App.getModule(OptionsModuleApi.class);
        OptionsStorage optionsStorage = optionsModule.getAppOptions();
        fileDocument.setInitialProcessingMode(new BinaryFileProcessingOptions(optionsStorage).getFileProcessingMode());

        getUndoHandler().addChangeListener(new BinaryDataUndoRedoChangeListener() {
			
			@Override
			public void undoChanged() {
				notifyChanged();
			}
		});
        filePanel.getToolbarPanel().setUndoHandler(getUndoHandler());
        filePanel.getToolbarPanel().setSaveAction(event -> {
            saveFile();
        });

        OptionsSettingsModuleApi optionsSettingsModule = App.getModule(OptionsSettingsModuleApi.class);
        OptionsSettingsManagement settingsManager = optionsSettingsModule.getMainSettingsManager();
        SettingsOptionsProvider settingsOptionsProvider = settingsManager.getSettingsOptionsProvider();
        fileDocument.applySettings(settingsOptionsProvider);
    }

    public IEditorInput getContent() {
        return dataObject;
    }
    
    public Component getComponent() {
        return filePanel;
    }
    
    public void tabPressed() {
        SectCodeArea codeArea = filePanel.getCodeArea();
        CodeAreaCommandHandler commandHandler = codeArea.getCommandHandler();
        commandHandler.tabPressed();
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
        fileDocument.loadFrom(new FileDocumentSource(file));
        if (!editable) {
            ((EditModeCapable) fileDocument.getCodeArea()).setEditMode(EditMode.READ_ONLY);
        }
        fileSync();
    }

    public void openDocument(InputStream stream, boolean editable) throws IOException {
        fileDocument.loadFrom(new StreamDocumentSource() {

            @Override
            public String getDocumentTitle() {
                // TODO Auto-generated method stub
                return "TEST";
            }

            @Override
            public InputStream openInputStream() {
                return stream;
            }

            @Override
            public OutputStream openOutputStream() {
                // TODO Auto-generated method stub
                return null;
            }
        });
        fileSync();
    }

    public void saveFile() {
        if (dataObject instanceof FileEditorInput) {
			IFile file = ((FileEditorInput) dataObject).getFile();
			IPath path = file.getLocation();

	        FileProcessingMode fileProcessingMode = fileDocument.getFileProcessingMode();
	        SectCodeArea codeArea = getCodeArea();

			try {
		        BinaryData data = codeArea.getContentData();
		        if (fileProcessingMode == FileProcessingMode.MEMORY) {
		            data.saveToStream(new FileOutputStream(path.toFile()));
		        } else {
		            DeltaDocument document = (DeltaDocument) data;
		            document.save();
		        }
		        fileSync();
		        notifyChanged();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
//        setModified(false);
    }

    public void saveFile(File file) {
        FileProcessingMode fileProcessingMode = fileDocument.getFileProcessingMode();
        SectCodeArea codeArea = getCodeArea();

		try {
	        BinaryData data = codeArea.getContentData();
	        if (fileProcessingMode == FileProcessingMode.MEMORY) {
	            data.saveToStream(new FileOutputStream(file));
	        } else {
	        	throw new UnsupportedOperationException("Not supported yet.");
//	            DeltaDocument document = (DeltaDocument) data;
//	            document.save();
	        }
	        fileSync();
	        notifyChanged();
            IWorkspace workspace = ResourcesPlugin.getWorkspace();
    		dataObject = new FileEditorInput(workspace.getRoot().getFileForLocation(org.eclipse.core.runtime.Path.fromOSString(file.getAbsolutePath())));
		} catch (IOException ex) {
			ex.printStackTrace();
		}
    }
    
    public void fileSync() {
        fileDocument.fileSync();
        filePanel.notifyFileSync();
    }

    public void reloadFile() {
        openFile(dataObject);
    }

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
        filePanel.getToolbarPanel().updateUndoState();
    	if (changeListener != null) {
    		changeListener.stateChanged(null);
    	}
    }
    
    public SectCodeArea getCodeArea() {
        return (SectCodeArea) filePanel.getCodeArea();
    }

    public BinaryDataUndoRedo getUndoHandler() {
        return fileDocument.getUndoHandler().get();
    }
    
    public void requestFocus() {
        filePanel.requestFocus();
    }
    
    public boolean isModified() {
        return fileDocument.isModified();
    }
    
    public boolean isSaveSupported() {
        return true;
    }
    
    public String getTitle() {
        return dataObject.getName();
    }
}
