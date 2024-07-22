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