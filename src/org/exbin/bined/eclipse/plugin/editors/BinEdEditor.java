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
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IPathEditorInput;
import org.eclipse.ui.IStorageEditorInput;
import org.eclipse.ui.IURIEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.editors.text.ILocationProvider;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.part.FileEditorInput;

/**
 * Implementation of the Eclipse editor. 
 *
 * @version 0.2.0 2019/05/19
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
		// TODO Auto-generated method stub
	}

	@Override
	public void doSaveAs() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
        setSite(site);
        if (!(input instanceof IPathEditorInput)
                && !(input instanceof ILocationProvider)
                && (!(input instanceof IURIEditorInput))
                && (!(input instanceof IStorageEditorInput))) {
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
		return true;
	}

	@Override
	public void createPartControl(Composite parent) {
		editor = new BinEdEditorSwing();

	    Composite wrapper = new Composite(parent, SWT.EMBEDDED); 
		java.awt.Frame frame = SWT_AWT.new_Frame(wrapper);
		frame.setLayout(new BorderLayout());

		frame.add(editor.getCodeAreaPanel(), java.awt.BorderLayout.CENTER);
		frame.add(editor.getStatusPanel(), BorderLayout.SOUTH);
		
		editor.openDataObject(getEditorInput());
	}

	@Override
	public void setFocus() {
//        editor.requestFocus();
	}

    @Override
    public void dispose() {
//      IPreferenceStore store = BinEdPlugin.getDefault().getPreferenceStore();
    	super.dispose();
    }
}
