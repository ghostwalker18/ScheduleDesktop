package com.ghostwalker18.scheduledesktop.common;

import javax.swing.*;

/**
 * Этот класс является прототипом для кастомных элементов GUI со сложной внутренней логикой.
 *
 * @author Ипатов Никита
 * @since 3.0
 */
public abstract class Fragment
        extends JPanel
        implements ViewModelOwner {

    private final Form parentForm;
    private final ViewModelStore store = new ViewModelStore();

    public abstract void onCreateUI();

    protected Fragment(Form form){
        parentForm = form;
    }

    protected final Form getParentForm() {
        return parentForm;
    }
    @Override
    public ViewModelStore getViewModelStore(){
        return store;
    }

    public void onCreate(Bundle bundle){}

    public void onSetupLanguage(){}

    public void onCreatedUI(){}

    public static class FragmentFactory {
        public <T extends Fragment> T create(Form owner, Class<T> fragmentClass, Bundle bundle){
            try{
                T fragment = fragmentClass.getConstructor(Form.class).newInstance(owner);
                fragment.onCreate(bundle);
                fragment.onCreateUI();
                fragment.onSetupLanguage();
                fragment.onCreatedUI();
                return fragment;
            } catch (Exception e){
                return null;
            }
        }
    }
}