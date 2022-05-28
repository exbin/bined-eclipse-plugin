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
package org.exbin.framework.bined.options;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.bined.swing.extended.color.ExtendedCodeAreaColorProfile;

/**
 * Code area color options.
 *
 * @version 0.2.1 2019/07/20
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public interface CodeAreaColorOptions {

    @Nonnull
    ExtendedCodeAreaColorProfile getColorsProfile(int index);

    int getSelectedProfile();

    void removeColorsProfile(int index);

    void setColorsProfile(int index, ExtendedCodeAreaColorProfile colorProfile);

    void setSelectedProfile(int profileIndex);

}
