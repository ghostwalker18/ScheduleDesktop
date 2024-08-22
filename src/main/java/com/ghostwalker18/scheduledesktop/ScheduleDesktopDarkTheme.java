package com.ghostwalker18.scheduledesktop;

import com.formdev.flatlaf.FlatDarkLaf;

public class ScheduleDesktopDarkTheme
        extends FlatDarkLaf {
    public static final String NAME = "ScheduleDesktopDarkTheme";

    public static boolean setup() {
        return setup( new ScheduleDesktopDarkTheme() );
    }

    public static void installLafInfo() {
        installLafInfo( NAME, ScheduleDesktopDarkTheme.class );
    }

    @Override
    public String getName() {
        return NAME;
    }
}