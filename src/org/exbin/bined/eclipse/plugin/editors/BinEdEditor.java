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
package org.exbin.bined.eclipse.plugin.editors;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IPathEditorInput;
import org.eclipse.ui.IStorageEditorInput;
import org.eclipse.ui.IURIEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.editors.text.ILocationProvider;
import org.eclipse.ui.part.EditorPart;
import org.exbin.bined.eclipse.plugin.BinEdNativeFile;
import org.exbin.bined.eclipse.plugin.EclipseWindowModule;
import org.exbin.bined.operation.command.BinaryDataUndoRedo;
import org.jspecify.annotations.NullMarked;

/**
 * Implementation of the binary/hexadecimal editor.
 */
@NullMarked
public final class BinEdEditor extends EditorPart implements ISelectionProvider {

	private List<ISelectionChangedListener> selectionChangedListeners = new ArrayList<>();
	private BinEdNativeFile binaryFile;

    protected String displayName;
    private ActionsStateListener actionsStateListener;

    public BinEdEditor() {
		super();
	}

	@Override
	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		selectionChangedListeners.add(listener);
	}

	@Override
	public ISelection getSelection() {
		return new ISelection() {
			@Override
			public boolean isEmpty() {
				return binaryFile == null || binaryFile.getCodeArea().hasSelection();
			}
		};
	}

	@Override
	public void removeSelectionChangedListener(ISelectionChangedListener listener) {
		selectionChangedListeners.remove(listener);
	}

	@Override
	public void setSelection(ISelection selection) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		binaryFile.saveDocument();
		notifyChanged();
	}

	@Override
	public void doSaveAs() {
		Shell shell = getEditorSite().getShell();
		FileDialog dialog = new FileDialog(shell, SWT.SAVE);
	    dialog.setText("Save As");

		String filePath = dialog.open();
		if (filePath == null) {
		    return;
		}

		File file = new File(filePath);
		if (file.exists()) {
			MessageBox messageBox = new MessageBox(shell, SWT.ICON_WARNING | SWT.YES | SWT.NO);
			messageBox.setText("File already exists.");
			messageBox.setMessage("File " + file.getName() + " already exists. Overwrite?");
			int messageResult = messageBox.open();
			if (messageResult != SWT.YES)
				return;
	    }

		binaryFile.saveFile(file);
		notifyChanged();
	}

	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		setSite(site);
		if (!(input instanceof IPathEditorInput) && !(input instanceof ILocationProvider)
				&& (!(input instanceof IURIEditorInput)) && (!(input instanceof IStorageEditorInput))) {
			throw new PartInitException("Input '" + input.toString() + "' is not a file");
		}
		setInput(input);
		setPartName(input.getName());

		// site.getActionBarContributor().setActiveEditor(this);
		site.setSelectionProvider(this);
	}

	@Override
	public boolean isDirty() {
		return binaryFile != null && binaryFile.isModified();
	}

	@Override
	public boolean isSaveAsAllowed() {
		return binaryFile != null && binaryFile.isSaveSupported();
	}

	@Override
	public void createPartControl(Composite parent) {
		binaryFile = new BinEdNativeFile();
		binaryFile.setChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				notifyChanged();
			}
		});

		Composite wrapper = new Composite(parent, SWT.EMBEDDED);
		wrapper.addTraverseListener(new TraverseListener() {
			public void keyTraversed(TraverseEvent e) {
				if (e.detail == SWT.TRAVERSE_TAB_NEXT) {
					e.doit = false;
					e.detail = SWT.TRAVERSE_NONE;
				}
			}
		});

		java.awt.Frame frame = SWT_AWT.new_Frame(wrapper);
		Shell shell = parent.getShell();
		EclipseWindowModule.frameShells.put(frame, shell);
		shell.addDisposeListener((e) -> {
		    EclipseWindowModule.frameShells.remove(frame);
		});

		frame.add(binaryFile.getComponent());
		final org.eclipse.swt.graphics.Rectangle size = wrapper.getClientArea();
		SwingUtilities.invokeLater(() -> {
			frame.invalidate();
			frame.setSize(size.width, size.height);
		});

		Display.getDefault().asyncExec(() -> {
            binaryFile.openFile(getEditorInput());
            registerActionBars();
        });
	}

    private void registerActionBars() {
		IActionBars bars = getEditorSite().getActionBars();
		bars.setGlobalActionHandler(ActionFactory.UNDO.getId(), new Action() {
			@Override
			public void run() {
			    BinaryDataUndoRedo undoHandler = binaryFile.getUndoHandler();
				if (!undoHandler.canUndo())
					return;

				try {
					undoHandler.performUndo();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		bars.setGlobalActionHandler(ActionFactory.REDO.getId(), new Action() {
			@Override
			public void run() {
			    BinaryDataUndoRedo undoHandler = binaryFile.getUndoHandler();
				if (!undoHandler.canRedo())
					return;

				try {
					undoHandler.performRedo();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		bars.setGlobalActionHandler(ActionFactory.CUT.getId(), new Action() {
			@Override
			public void run() {
				binaryFile.getCodeArea().cut();
			}
		});
		bars.setGlobalActionHandler(ActionFactory.COPY.getId(), new Action() {
			@Override
			public void run() {
				binaryFile.getCodeArea().copy();
			}
		});
		bars.setGlobalActionHandler(ActionFactory.PASTE.getId(), new Action() {
			@Override
			public void run() {
				binaryFile.getCodeArea().paste();
			}
		});
		bars.setGlobalActionHandler(ActionFactory.DELETE.getId(), new Action() {
			@Override
			public void run() {
				binaryFile.getCodeArea().delete();
			}
		});
		bars.setGlobalActionHandler(ActionFactory.SELECT_ALL.getId(), new Action() {
			@Override
			public void run() {
				binaryFile.getCodeArea().selectAll();
			}
		});
		updateActionBars();
	}

	private void updateActionBars() {
		IActionBars bars = getEditorSite().getActionBars();
		IAction undoAction = bars.getGlobalActionHandler(ActionFactory.UNDO.getId());
		if (undoAction != null) {
		    BinaryDataUndoRedo undoHandler = binaryFile.getUndoHandler();
			undoAction.setEnabled(undoHandler.canUndo());
		}

		IAction redoAction = bars.getGlobalActionHandler(ActionFactory.REDO.getId());
		if (redoAction != null) {
		    BinaryDataUndoRedo undoHandler = binaryFile.getUndoHandler();
			redoAction.setEnabled(undoHandler.canRedo());
		}

		IAction cutAction = bars.getGlobalActionHandler(ActionFactory.CUT.getId());
		if (cutAction != null) {
			cutAction.setEnabled(binaryFile.getCodeArea().hasSelection());
		}

		IAction copyAction = bars.getGlobalActionHandler(ActionFactory.COPY.getId());
		if (copyAction != null) {
			copyAction.setEnabled(binaryFile.getCodeArea().hasSelection());
		}

		IAction pasteAction = bars.getGlobalActionHandler(ActionFactory.PASTE.getId());
		if (pasteAction != null) {
			pasteAction.setEnabled(binaryFile.getCodeArea().canPaste());
		}

		IAction deleteAction = bars.getGlobalActionHandler(ActionFactory.DELETE.getId());
		if (deleteAction != null) {
			deleteAction.setEnabled(binaryFile.getCodeArea().hasSelection());
		}

		IAction selectAllAction = bars.getGlobalActionHandler(ActionFactory.SELECT_ALL.getId());
		if (selectAllAction != null) {
			selectAllAction.setEnabled(true);
		}

		Display.getDefault().syncExec(new Runnable() {
			@Override
			public void run() {
				bars.updateActionBars();
			}
		});
	}

	@Override
	public void setFocus() {
		binaryFile.requestFocus();
	}

	@Override
	public void dispose() {
		super.dispose();
	}

	private void notifyChanged() {
		Display.getDefault().syncExec(new Runnable() {
			@Override
			public void run() {
				firePropertyChange(PROP_DIRTY);
				updateActionBars();
			}
		});
	}

    public static interface ActionsStateListener {
    	void changed();
    }
}
