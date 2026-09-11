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

import org.exbin.bined.eclipse.settings.IntegrationOptions;
import org.exbin.bined.jaguif.editor.settings.BinaryEditorOptions;
import org.exbin.bined.jaguif.inspector.settings.DataInspectorOptions;
import org.exbin.bined.jaguif.theme.settings.CodeAreaColorOptions;
import org.exbin.bined.jaguif.theme.settings.CodeAreaLayoutOptions;
import org.exbin.bined.jaguif.theme.settings.CodeAreaThemeOptions;
import org.exbin.bined.jaguif.viewer.settings.CodeAreaOptions;
import org.exbin.bined.jaguif.viewer.settings.CodeAreaStatusOptions;
import org.exbin.jaguif.text.encoding.settings.TextEncodingOptions;
import org.exbin.jaguif.text.font.settings.TextFontOptions;
import org.jspecify.annotations.NullMarked;

/**
 * Options for apply operation.
 */
@NullMarked
public interface BinEdApplyOptions {

    CodeAreaOptions getCodeAreaOptions();

    TextEncodingOptions getEncodingOptions();

    TextFontOptions getFontOptions();

    IntegrationOptions getIntegrationOptions();

    BinaryEditorOptions getEditorOptions();

    CodeAreaStatusOptions getStatusOptions();

    DataInspectorOptions getDataInspectorOptions();

    CodeAreaLayoutOptions getLayoutOptions();

    CodeAreaColorOptions getColorOptions();

    CodeAreaThemeOptions getThemeOptions();
}
