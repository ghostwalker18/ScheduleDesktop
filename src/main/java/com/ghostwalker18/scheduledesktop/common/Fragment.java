package com.ghostwalker18.scheduledesktop.common;

/**
 * Этот класс является прототипом для кастомных элементов GUI со сложной внутренней логикой.
 *
 * @author Ипатов Никита
 * @since 3.0
 */
public abstract class Fragment
        implements ViewModelOwner {

    private Form parentForm;

    public Form getParentForm() {
        return parentForm;
    }
}