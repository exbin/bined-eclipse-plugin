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
package org.exbin.bined.eclipse.plugin.handlers;

import java.awt.Dialog;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JPanel;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.exbin.bined.eclipse.debug.gui.DebugViewPanel;
import org.exbin.framework.utils.WindowUtils;
import org.exbin.framework.utils.WindowUtils.DialogWrapper;
import org.exbin.framework.utils.gui.DefaultControlPanel;
import org.exbin.framework.utils.handler.DefaultControlHandler;

/**
 * Views variable as binary data.
 *
 * @version 0.2.1 2022/05/30
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class ViewAsBinaryVariableHandler extends AbstractHandler {

	@Nullable
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		DebugViewPanel debugViewPanel = new DebugViewPanel();
		DefaultControlPanel controlPanel = new DefaultControlPanel();
		JPanel dialogPanel = WindowUtils.createDialogPanel(debugViewPanel, controlPanel);
		final DialogWrapper dialog = WindowUtils.createDialog(dialogPanel, null, "View as Binary",
				Dialog.ModalityType.APPLICATION_MODAL);

//        debugViewPanel.initFocus();
		controlPanel.setHandler((DefaultControlHandler.ControlActionType actionType) -> {
			dialog.close();
			dialog.dispose();
		});
		dialog.showCentered(null);

		return null;
	}
}
