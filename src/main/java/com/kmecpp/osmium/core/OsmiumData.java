package com.kmecpp.osmium.core;

import java.io.IOException;
import java.util.Calendar;

import com.kmecpp.osmium.Osmium;
import com.kmecpp.osmium.api.config.ConfigProperties;
import com.kmecpp.osmium.api.config.Setting;
import com.kmecpp.osmium.api.util.TimeUtil;
import com.kmecpp.osmium.platform.osmium.OsmiumDayChangeEvent;
import com.kmecpp.osmium.platform.osmium.OsmiumMonthChangeEvent;
import com.kmecpp.osmium.platform.osmium.OsmiumWeekChangeEvent;

@ConfigProperties(path = "data.txt")
public class OsmiumData {

	@Setting
	public static int dayOfMonth;

	public static void updateDay() {
		int currentDay = TimeUtil.getCalendar().get(Calendar.DAY_OF_MONTH);

		if (dayOfMonth != currentDay) {
			Calendar logged = TimeUtil.getCalendar();
			logged.set(Calendar.DAY_OF_MONTH, dayOfMonth);

			Osmium.getEventManager().callEvent(new OsmiumDayChangeEvent(dayOfMonth, currentDay));

			Calendar current = TimeUtil.getCalendar();

			if (current.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
				Osmium.getEventManager().callEvent(new OsmiumWeekChangeEvent(logged.get(Calendar.WEEK_OF_MONTH), current.get(Calendar.WEEK_OF_MONTH)));
			}

			if (currentDay == 1) {
				Osmium.getEventManager().callEvent(new OsmiumMonthChangeEvent(logged.get(Calendar.MONTH), current.get(Calendar.MONTH)));
			}
			dayOfMonth = currentDay;

			try {
				Osmium.getConfigManager().save(OsmiumData.class);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
