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

package com.ghostwalker18.scheduledesktop.system;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class Toast
        extends JDialog {
    private int duration = 2000;

    public Toast(JComponent owner, String message){
        setUndecorated(true);
        setAlwaysOnTop(true);
        setFocusableWindowState(false);
        setLayout(new GridBagLayout());
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        panel.setBackground(new Color(160, 160, 160));
        JLabel toastLabel = new JLabel(message);
        toastLabel.setForeground(Color.WHITE);
        panel.add(toastLabel);
        add(panel);
        pack();
        
        Window window = SwingUtilities.getWindowAncestor(owner);
        int xcoord = window.getLocationOnScreen().x + window.getWidth() / 2 - getWidth() / 2;
        int ycoord = window.getLocationOnScreen().y + window.getHeight() / 2 - getHeight() / 2;
        setLocation(xcoord, ycoord);
        setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 25, 25));
    }

    public void setDuration(int millis){
        duration = millis;
    }

    public void display() {
        new Thread(() -> {
            try {
                setOpacity(1);
                setVisible(true);
                Thread.sleep(duration);

                //hide the toast message in slow motion
                for (double d = 1.0; d > 0; d -= 0.05) {
                    Thread.sleep(50);
                    setOpacity((float)d);
                }

                dispose();
            }
            catch (InterruptedException | ThreadDeath e){
                Thread.currentThread().interrupt();
            }
            catch (Exception e) {/*Not required*/}
        }).start();
    }
}