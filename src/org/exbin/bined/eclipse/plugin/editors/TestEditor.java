package org.exbin.bined.eclipse.plugin.editors;

import org.eclipse.ui.editors.text.TextEditor;

public class TestEditor extends TextEditor {

	private ColorManager colorManager;

	public TestEditor() {
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
