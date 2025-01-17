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

/**
 * Этот класс представляет собой реализацию модели множественного выбора элементов списка JList
 * без необходимости использования клавиш Ctrl и Shift.
 *
 * @author Ипатов Никита
 * @since 3.0
 */
public class CustomListSelectionModel
        extends DefaultListSelectionModel {
    private int i0 = -1;
    private int i1 = -1;

    public void setSelectionInterval(int index0, int index1) {
        if(i0 == index0 && i1 == index1){
            if(getValueIsAdjusting()){
                setValueIsAdjusting(false);
                setSelection(index0, index1);
            }
        } else {
            i0 = index0;
            i1 = index1;
            setValueIsAdjusting(false);
            setSelection(index0, index1);
        }
    }
    private void setSelection(int index0, int index1){
        if(super.isSelectedIndex(index0)) {
            super.removeSelectionInterval(index0, index1);
        }else {
            super.addSelectionInterval(index0, index1);
        }
        fireValueChanged(index0, index1);
    }
}