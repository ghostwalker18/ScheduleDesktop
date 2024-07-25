/*
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

package com.ghostwalker18.scheduledesktop;

import javax.swing.*;
import java.awt.*;

public class DefaulTheme implements Theme{
    @Override
    public Color getPrimaryColor() {
        return new Color(102,161, 1);
    }

    @Override
    public Color getSecondaryColor() {
        return new Color(40,158,46);
    }

    @Override
    public Color getAccentColor() {
        return new Color(8,12,115);
    }

    @Override
    public Color getTextColor() {
        return Color.WHITE;
    }

    @Override
    public Color getBackgroundColor() {
        return Color.WHITE;
    }

    @Override
    public ImageIcon getThemeIcon() {
        return null;
    }
}