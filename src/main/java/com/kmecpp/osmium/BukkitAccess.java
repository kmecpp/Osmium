package com.kmecpp.osmium;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.event.EventPriority;

import com.kmecpp.osmium.api.Block;
import com.kmecpp.osmium.api.Chunk;
import com.kmecpp.osmium.api.World;
import com.kmecpp.osmium.api.command.Command;
import com.kmecpp.osmium.api.command.CommandManager;
import com.kmecpp.osmium.api.command.CommandSender;
import com.kmecpp.osmium.api.entity.Entity;
import com.kmecpp.osmium.api.entity.Player;
import com.kmecpp.osmium.api.event.Event;
import com.kmecpp.osmium.api.event.EventInfo;
import com.kmecpp.osmium.api.event.Order;
import com.kmecpp.osmium.api.location.Location;
import com.kmecpp.osmium.api.logging.OsmiumLogger;
import com.kmecpp.osmium.api.plugin.OsmiumPlugin;
import com.kmecpp.osmium.api.util.Reflection;
import com.kmecpp.osmium.cache.PlayerList;
import com.kmecpp.osmium.cache.WorldList;
import com.kmecpp.osmium.platform.bukkit.BukkitBlock;
import com.kmecpp.osmium.platform.bukkit.BukkitBlockCommandSender;
import com.kmecpp.osmium.platform.bukkit.BukkitChunk;
import com.kmecpp.osmium.platform.bukkit.BukkitConsoleCommandSender;
import com.kmecpp.osmium.platform.bukkit.BukkitEntity;
import com.kmecpp.osmium.platform.bukkit.BukkitWorld;
import com.kmecpp.osmium.platform.bukkit.GenericBukkitCommandSender;

public class BukkitAccess {

	public static Chunk getChunk(org.bukkit.Chunk chunk) {
		return new BukkitChunk(chunk);
	}

	public static Entity getEntity(org.bukkit.entity.Entity entity) {
		return new BukkitEntity(entity);
	}

	public static Player getPlayer(org.bukkit.entity.Player player) {
		return PlayerList.getPlayer(player.getName());
	}

	public static void processConsoleCommand(String command) {
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
	}

	public static void processCommand(org.bukkit.command.CommandSender sender, String command) {
		Bukkit.dispatchCommand(sender, command);
	}

	public static World getWorld(org.bukkit.World bukkitWorld) {
		World world = WorldList.getWorld(bukkitWorld.getName());
		if (world == null) { //This can happen with Bukkit
			world = new BukkitWorld(bukkitWorld);
			WorldList.addWorld(world);
		}
		return world;
	}

	public static Location getLocation(org.bukkit.Location location) {
		return new Location(getWorld(location.getWorld()), location.getX(), location.getY(), location.getZ());
	}

	public static Block getBlock(org.bukkit.block.Block block) {
		return new BukkitBlock(block);
	}

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

			commandMap.register(command.getPrimaryAlias(), new BukkitCommand(command.getPrimaryAlias(),
					command.getDescription(), command.getUsage(), aliases) { // Usage message cannot be null or else stuff will break

				@Override
				public boolean execute(org.bukkit.command.CommandSender bukkitSender, String label, String[] args) {
					CommandSender sender = bukkitSender instanceof org.bukkit.entity.Player
							? getPlayer((org.bukkit.entity.Player) bukkitSender)
							: bukkitSender instanceof org.bukkit.command.ConsoleCommandSender
									? new BukkitConsoleCommandSender(
											(org.bukkit.command.ConsoleCommandSender) bukkitSender)
									: bukkitSender instanceof org.bukkit.command.BlockCommandSender
											? new BukkitBlockCommandSender(
													(org.bukkit.command.BlockCommandSender) bukkitSender)
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
		Class<? extends org.bukkit.event.Event> bukkitEventClass = eventInfo.getSource();

		Constructor<? extends Event> eventWrapper = eventInfo.getImplementation().getConstructor(bukkitEventClass);
		Bukkit.getPluginManager().registerEvent(bukkitEventClass, plugin.getPluginImplementation(), (EventPriority) order.getSource(), (bukkitListener, bukkitEvent) -> {
			if (bukkitEventClass.isAssignableFrom(bukkitEvent.getClass())) {
				try {
					Event event = eventWrapper.newInstance(bukkitEvent);
					if (!event.shouldFire()) {
						return;
					}

					try {
						method.invoke(listenerInstance, event);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					e.printStackTrace();
				}
			}
		}, plugin.getPluginImplementation(), true);
	}

}
