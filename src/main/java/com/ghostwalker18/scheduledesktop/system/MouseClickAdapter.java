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

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * Этот класс служит прототипом обработчика нажатий мышью на элемент UI.
 *
 * @author Ипатов Никита
 * @since 3.0
 */
public abstract class MouseClickAdapter
        implements MouseListener {
    private static final long DELAY = 250;
    private long startTime;

    public abstract void onClick();

    public abstract void onLongClick();

    @Override
    public void mousePressed(MouseEvent e){
        startTime = e.getWhen();
    }

    @Override
    public void mouseReleased(MouseEvent e){
        long endTime = e.getWhen();
        if(endTime - startTime < DELAY)
            onClick();
        else
            onLongClick();
    }

    @Override
    public void mouseClicked(MouseEvent e){}

    @Override
    public void mouseEntered(MouseEvent e){}

    @Override
    public void mouseExited(MouseEvent e){}
}