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
package org.exbin.framework.popup;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Interface for popup event trigger dispatcher.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public interface ComponentPopupEventDispatcher {

    /**
     * Processes event for popup trigger actions.
     *
     * @param mouseEvent mouse event
     * @return true if event was processed
     */
    boolean dispatchMouseEvent(MouseEvent mouseEvent);

    /**
     * Processes event for popup trigger actions.
     *
     * @param keyEvent key event
     * @return true if event was processed
     */
    boolean dispatchKeyEvent(KeyEvent keyEvent);
}
