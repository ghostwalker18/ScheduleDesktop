package com.ghostwalker18.scheduledesktop;

import com.formdev.flatlaf.FlatLightLaf;

public class ScheduleDesktopLightTheme
	extends FlatLightLaf
{
	public static final String NAME = "ScheduleDesktopLightTheme";

	public static boolean setup() {
		return setup( new ScheduleDesktopLightTheme() );
	}

	public static void installLafInfo() {
		installLafInfo( NAME, ScheduleDesktopLightTheme.class );
	}

	@Override
	public String getName() {
		return NAME;
	}
}
