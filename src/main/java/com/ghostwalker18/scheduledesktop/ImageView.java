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
import java.awt.image.BufferedImage;
import java.util.ResourceBundle;

/**
 * Этот класс используется для отображения в графическом интерфейсе буферизированных изображений
 */
public class ImageView
        extends JPanel {
    private static final ResourceBundle platformStrings = ResourceBundle.getBundle("platform_strings",
            new XMLBundleControl());
    private JProgressBar placeholder;
    private JLabel imageLabel;

    public ImageView(){
        placeholder = new JProgressBar();
        placeholder.setString(platformStrings.getString("downloading"));
        imageLabel = new JLabel();
        placeholder.setIndeterminate(true);
        this.add(placeholder);
    }

    /**
     * Этот метод устанавливает изображение для отображения.
     * @param image изображение
     */
    public void setImage(BufferedImage image){
        this.remove(placeholder);
        imageLabel.setIcon(new ImageIcon(image));
        this.add(imageLabel);
    }
}