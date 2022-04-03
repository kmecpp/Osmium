package com.kmecpp.osmium.core;

import java.util.Calendar;

import com.kmecpp.osmium.Osmium;
import com.kmecpp.osmium.api.persistence.Persistent;
import com.kmecpp.osmium.api.util.TimeUtil;
import com.kmecpp.osmium.platform.osmium.OsmiumDayChangeEvent;
import com.kmecpp.osmium.platform.osmium.OsmiumMonthChangeEvent;
import com.kmecpp.osmium.platform.osmium.OsmiumWeekChangeEvent;

public class OsmiumData {

	@Persistent(id = "day_of_month")
	public static int dayOfMonth;

	public static void update() {
		int currentDay = TimeUtil.getCalendar().get(Calendar.DAY_OF_MONTH);

		if (dayOfMonth != currentDay) {
			Calendar old = TimeUtil.getCalendar();
			old.set(Calendar.DAY_OF_MONTH, dayOfMonth);
			if (currentDay == 1) {
				int prevMonth = old.get(Calendar.MONTH) - 1;
				old.set(Calendar.MONTH, prevMonth < 0 ? Calendar.DECEMBER : prevMonth);
			}

			Osmium.getEventManager().callEvent(new OsmiumDayChangeEvent(dayOfMonth, currentDay));

			Calendar current = TimeUtil.getCalendar();

			if (current.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
				Osmium.getEventManager().callEvent(new OsmiumWeekChangeEvent(old.get(Calendar.WEEK_OF_MONTH), current.get(Calendar.WEEK_OF_MONTH)));
			}

			if (currentDay == 1) {
				Osmium.getEventManager().callEvent(new OsmiumMonthChangeEvent(old.get(Calendar.MONTH), current.get(Calendar.MONTH)));
			}
			dayOfMonth = currentDay;

			OsmiumCore.getPlugin().savePersistentData();
		}
	}

}
