package com.kmecpp.osmium;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.event.EventPriority;

import com.kmecpp.osmium.api.command.Command;
import com.kmecpp.osmium.api.command.CommandManager;
import com.kmecpp.osmium.api.command.CommandSender;
import com.kmecpp.osmium.api.event.EventInfo;
import com.kmecpp.osmium.api.event.Order;
import com.kmecpp.osmium.api.logging.OsmiumLogger;
import com.kmecpp.osmium.api.plugin.OsmiumPlugin;
import com.kmecpp.osmium.api.util.Reflection;
import com.kmecpp.osmium.platform.bukkit.BukkitBlockCommandSender;
import com.kmecpp.osmium.platform.bukkit.BukkitConsoleCommandSender;
import com.kmecpp.osmium.platform.bukkit.BukkitPlayer;
import com.kmecpp.osmium.platform.bukkit.GenericBukkitCommandSender;

public class BukkitAccess {

	public static void registerCommand(OsmiumPlugin plugin, Command command) {
		try {
			SimpleCommandMap commandMap = (SimpleCommandMap) Reflection.getFieldValue(Bukkit.getServer(), "commandMap");
			List<String> aliases = Arrays.asList(Arrays.copyOfRange(command.getAliases(), 1, command.getAliases().length));

			String name = command.getPrimaryAlias();
			if (commandMap.getCommand(name) != null) {
				for (int i = 0; i < command.getAliases().length; i++) {
					String alias = command.getAliases()[i];
					if (commandMap.getCommand(alias) == null) {
						OsmiumLogger.debug("Modified primary alias: " + name + " -> " + alias);
						command.setPrimaryAlias(alias);
						break;
					} else if (i == command.getAliases().length) {
						CommandManager.sendFailedRegistrationMessage(plugin, command);
					}
				}
			}

			commandMap.register(command.getPrimaryAlias(), new BukkitCommand(command.getPrimaryAlias(), command.getDescription(), command.getUsage(), aliases) { //Usage message cannot be null or else stuff will break

				@Override
				public boolean execute(org.bukkit.command.CommandSender bukkitSender, String label, String[] args) {
					CommandSender sender = bukkitSender instanceof org.bukkit.entity.Player ? new BukkitPlayer((org.bukkit.entity.Player) bukkitSender)
							: bukkitSender instanceof org.bukkit.command.ConsoleCommandSender ? new BukkitConsoleCommandSender((org.bukkit.command.ConsoleCommandSender) bukkitSender)
									: bukkitSender instanceof org.bukkit.command.BlockCommandSender ? new BukkitBlockCommandSender((org.bukkit.command.BlockCommandSender) bukkitSender)
											: new GenericBukkitCommandSender(bukkitSender);
					return CommandManager.invokeCommand(command, sender, label, args);
				}

			});
			Osmium.getCommandManager().register(plugin, command);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void registerListener(OsmiumPlugin plugin, EventInfo eventInfo, Order order, Method method, Object listenerInstance) throws Exception {
		Class<? extends org.bukkit.event.Event> bukkitEventClass = eventInfo.getBukkitClass();

		Constructor<?> eventWrapper = eventInfo.getBukkitImplementation().getConstructor(bukkitEventClass);
		Bukkit.getPluginManager().registerEvent(bukkitEventClass, plugin.getPluginImplementation(), (EventPriority) order.getSource(),
				(bukkitListener, bukkitEvent) -> {
					if (bukkitEventClass.isAssignableFrom(bukkitEvent.getClass())) {
						try {
							method.invoke(listenerInstance, eventWrapper.newInstance(bukkitEvent));
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}
				}, plugin.getPluginImplementation(), true);

	}

}
