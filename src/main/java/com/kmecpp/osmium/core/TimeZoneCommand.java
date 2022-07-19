package com.kmecpp.osmium.core;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import com.kmecpp.osmium.Osmium;
import com.kmecpp.osmium.Platform;
import com.kmecpp.osmium.api.MilliTimeUnit;
import com.kmecpp.osmium.api.command.Chat;
import com.kmecpp.osmium.api.command.Command;
import com.kmecpp.osmium.api.entity.Player;

public class TimeZoneCommand extends Command {

	private static final ArrayList<String> searchList = ZoneId.getAvailableZoneIds().stream().collect(Collectors.toCollection(ArrayList::new));

	public TimeZoneCommand() {
		super(Platform.isProxy() ? "timezonebungee" : "timezone",
				Platform.isProxy() ? "tzb" : "tz");
		setTitle("Time Zone Commands");

		add("check", "info").setCooldown(3, MilliTimeUnit.SECOND).setExecutor(e -> {
			ZoneId timeZone = e.getPlayer().getTimeZone();

			e.sendTitle("Time Zone Configuration");
			e.sendMessage(Chat.GREEN + "Your time zone is currently set to: " + Chat.YELLOW + (timeZone != null ? timeZone.getId() : "DEFAULT"));
		});

		add("set").setUsage("<timezone>").setCooldown(3, MilliTimeUnit.SECOND).setExecutor(e -> {
			String requestedTimeZone = e.getString(0);
			try {
				ZoneId zoneId = ZoneId.of(requestedTimeZone);
				setTimeZone(e.getPlayer(), zoneId);
			} catch (Exception ex) {
				e.sendMessage(Chat.RED + "Unknown time zone: '" + requestedTimeZone + "'");
			}
		});

		add("reset").setCooldown(3, MilliTimeUnit.SECOND).setExecutor(e -> {
			setTimeZone(e.getPlayer(), null);
		});

		add("list").setUsage("[search]").setCooldown(3, MilliTimeUnit.SECOND).setExecutor(e -> {
			if (!e.hasString(0)) {
				e.sendTitle("Time Zone Search");
				e.sendMessage(Chat.GREEN + "List of Time Zones: " + Chat.YELLOW + "https://pastebin.com/raw/EdjXe4BC");
				e.sendMessage("");
				e.sendMessage(Chat.RED + "You can also use " + Chat.YELLOW + "/tz list <search-term>" + Chat.RED + " to find your time zone.");
				return;
			}

			String searchTerm = e.getString(0).toLowerCase();

			Osmium.getTask().setAsync(true).setExecutor(t -> {
				ArrayList<String> results = new ArrayList<>();

				for (String timeZone : searchList) {
					if (timeZone.toLowerCase().contains(searchTerm)) {
						results.add(timeZone);
						if (results.size() >= 10) {
							results.add("...");
							break;
						}
					}
				}

				if (results.isEmpty()) {
					fail("Your search for '" + searchTerm + "' did not return any results!");
				}
				e.sendTitle("Matching Time Zones");
				e.sendList(results);
			}).start();
		});
	}

	private void setTimeZone(Player player, @Nullable ZoneId zoneId) {
		Osmium.getTask().setAsync(true).setExecutor(t -> {
			String timeZoneString = zoneId != null ? ("'" + zoneId.getId() + "'") : "NULL";
			int result = OsmiumCoreConfig.Database.useMySql
					? OsmiumCore.getPlugin().getMySQLDatabase().update("update osmium_users set time_zone=" + timeZoneString + " where uuid='" + player.getUniqueId() + "'")
					: OsmiumCore.getPlugin().getSQLiteDatabase().update("UPDATE users SET time_zone=" + timeZoneString + " WHERE uuid='" + player.getUniqueId() + "'");

			if (result > 0) {
				OsmiumUserDataManager.getUserDataFromPlayer(player).setTimezone(zoneId);
				player.sendMessage(Chat.GREEN + (zoneId != null
						? "Successfully set your time zone to: " + Chat.YELLOW + zoneId.getId()
						: "Successfully reset your time zone settings."));
			} else {
				player.sendMessage(Chat.RED + "Unable to update your timezone! Please contact an admin.");
			}
		}).start();
	}

}
