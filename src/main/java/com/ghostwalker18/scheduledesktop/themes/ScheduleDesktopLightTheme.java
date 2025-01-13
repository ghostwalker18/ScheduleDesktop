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

package com.ghostwalker18.scheduledesktop.themes;

import com.formdev.flatlaf.FlatLightLaf;

/**
 * Этот класс используется для задания темной темы приложения
 *
 * @author Ипатов Никита
 */
public class ScheduleDesktopLightTheme
	extends FlatLightLaf {
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