package org.exbin.bined.eclipse.editors;

import org.eclipse.ui.editors.text.TextEditor;

public class BinEdEditor extends TextEditor {

	private ColorManager colorManager;

	public BinEdEditor() {
		super();
		colorManager = new ColorManager();
		setSourceViewerConfiguration(new XMLConfiguration(colorManager));
		setDocumentProvider(new XMLDocumentProvider());
	}

	public void dispose() {
		colorManager.dispose();
		super.dispose();
	}

}
