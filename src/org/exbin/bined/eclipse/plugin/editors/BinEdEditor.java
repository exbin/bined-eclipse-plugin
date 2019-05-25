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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
import org.exbin.bined.DataChangedListener;
import org.exbin.bined.operation.BinaryDataOperationException;
import org.exbin.bined.operation.swing.CodeAreaUndoHandler;
import org.exbin.bined.swing.extended.ExtCodeArea;

/**
 * Implementation of the Eclipse editor.
 *
 * @version 0.2.0 2019/05/20
 * @author ExBin Project (http://exbin.org)
 */
public final class BinEdEditor extends EditorPart implements ISelectionProvider {

	private List<ISelectionChangedListener> selectionChangedListeners = new ArrayList<>();
	private BinEdEditorSwing editor;

	public BinEdEditor() {
		super();
	}

	@Override
	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		selectionChangedListeners.add(listener);
	}

	@Override
	public ISelection getSelection() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void removeSelectionChangedListener(ISelectionChangedListener listener) {
		selectionChangedListeners.remove(listener);
	}

	@Override
	public void setSelection(ISelection selection) {
		// TODO Auto-generated method stub

	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		try {
			editor.save();
			notifyChanged();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void doSaveAs() {
		// TODO Auto-generated method stub

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
		return editor.isModified();
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	@Override
	public void createPartControl(Composite parent) {
		editor = new BinEdEditorSwing();

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
		frame.setLayout(new BorderLayout());

		frame.add(editor.getCodeAreaPanel(), java.awt.BorderLayout.CENTER);
		frame.add(editor.getStatusPanel(), BorderLayout.SOUTH);

		editor.getCodeArea().addDataChangedListener(new DataChangedListener() {

			@Override
			public void dataChanged() {
				notifyChanged();
			}
		});
		editor.openDataObject(getEditorInput());
		registerActionBars();
	}

	private void registerActionBars() {
		IActionBars bars = getEditorSite().getActionBars();
		bars.setGlobalActionHandler(ActionFactory.UNDO.getId(), new Action() {
			@Override
			public void run() {
				CodeAreaUndoHandler undoHandler = editor.getUndoHandler();
				if (!undoHandler.canUndo())
					return;

				try {
					undoHandler.performUndo();
				} catch (BinaryDataOperationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		bars.setGlobalActionHandler(ActionFactory.REDO.getId(), new Action() {
			@Override
			public void run() {
				CodeAreaUndoHandler undoHandler = editor.getUndoHandler();
				if (!undoHandler.canRedo())
					return;

				try {
					undoHandler.performRedo();
				} catch (BinaryDataOperationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		bars.setGlobalActionHandler(ActionFactory.CUT.getId(), new Action() {
			@Override
			public void run() {
				editor.getCodeArea().cut();
			}
		});
		bars.setGlobalActionHandler(ActionFactory.COPY.getId(), new Action() {
			@Override
			public void run() {
				editor.getCodeArea().copy();
			}
		});
		bars.setGlobalActionHandler(ActionFactory.PASTE.getId(), new Action() {
			@Override
			public void run() {
				editor.getCodeArea().paste();
			}
		});
		bars.setGlobalActionHandler(ActionFactory.DELETE.getId(), new Action() {
			@Override
			public void run() {
				editor.getCodeArea().delete();
			}
		});
		bars.setGlobalActionHandler(ActionFactory.SELECT_ALL.getId(), new Action() {
			@Override
			public void run() {
				editor.getCodeArea().selectAll();
			}
		});
		updateActionBars();
	}

	private void updateActionBars() {
		IActionBars bars = getEditorSite().getActionBars();
		IAction undoAction = bars.getGlobalActionHandler(ActionFactory.UNDO.getId());
		if (undoAction != null) {
			CodeAreaUndoHandler undoHandler = editor.getUndoHandler();
			undoAction.setEnabled(undoHandler.canUndo());
		}

		IAction redoAction = bars.getGlobalActionHandler(ActionFactory.REDO.getId());
		if (redoAction != null) {
			CodeAreaUndoHandler undoHandler = editor.getUndoHandler();
			redoAction.setEnabled(undoHandler.canUndo());
		}

		IAction cutAction = bars.getGlobalActionHandler(ActionFactory.CUT.getId());
		if (cutAction != null) {
			ExtCodeArea codeArea = editor.getCodeArea();
			cutAction.setEnabled(codeArea.hasSelection());
		}

		IAction copyAction = bars.getGlobalActionHandler(ActionFactory.COPY.getId());
		if (copyAction != null) {
			ExtCodeArea codeArea = editor.getCodeArea();
			copyAction.setEnabled(codeArea.hasSelection());
		}

		IAction pasteAction = bars.getGlobalActionHandler(ActionFactory.PASTE.getId());
		if (pasteAction != null) {
			ExtCodeArea codeArea = editor.getCodeArea();
			pasteAction.setEnabled(codeArea.canPaste());
		}

		IAction deleteAction = bars.getGlobalActionHandler(ActionFactory.DELETE.getId());
		if (deleteAction != null) {
			ExtCodeArea codeArea = editor.getCodeArea();
			deleteAction.setEnabled(codeArea.hasSelection());
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
		editor.requestFocus();
	}

	@Override
	public void dispose() {
//      IPreferenceStore store = BinEdPlugin.getDefault().getPreferenceStore();
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
}
