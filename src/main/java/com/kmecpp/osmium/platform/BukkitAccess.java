package com.kmecpp.osmium.platform;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.kmecpp.osmium.Osmium;
import com.kmecpp.osmium.OsmiumRegistry;
import com.kmecpp.osmium.api.Block;
import com.kmecpp.osmium.api.Chunk;
import com.kmecpp.osmium.api.World;
import com.kmecpp.osmium.api.command.Command;
import com.kmecpp.osmium.api.command.CommandManager;
import com.kmecpp.osmium.api.command.CommandSender;
import com.kmecpp.osmium.api.command.OverrideMode;
import com.kmecpp.osmium.api.entity.Entity;
import com.kmecpp.osmium.api.entity.EntityType;
import com.kmecpp.osmium.api.entity.Player;
import com.kmecpp.osmium.api.event.Order;
import com.kmecpp.osmium.api.inventory.Inventory;
import com.kmecpp.osmium.api.inventory.ItemStack;
import com.kmecpp.osmium.api.inventory.ItemType;
import com.kmecpp.osmium.api.location.Direction;
import com.kmecpp.osmium.api.location.Location;
import com.kmecpp.osmium.api.location.WorldPosition;
import com.kmecpp.osmium.api.logging.OsmiumLogger;
import com.kmecpp.osmium.api.plugin.OsmiumPlugin;
import com.kmecpp.osmium.api.util.ReflectField;
import com.kmecpp.osmium.api.util.Reflection;
import com.kmecpp.osmium.cache.PlayerList;
import com.kmecpp.osmium.cache.WorldList;
import com.kmecpp.osmium.core.OsmiumCore;
import com.kmecpp.osmium.platform.bukkit.BukkitBlock;
import com.kmecpp.osmium.platform.bukkit.BukkitBlockCommandSender;
import com.kmecpp.osmium.platform.bukkit.BukkitChunk;
import com.kmecpp.osmium.platform.bukkit.BukkitConsoleCommandRedirect;
import com.kmecpp.osmium.platform.bukkit.BukkitConsoleCommandSender;
import com.kmecpp.osmium.platform.bukkit.BukkitEntity;
import com.kmecpp.osmium.platform.bukkit.BukkitInventory;
import com.kmecpp.osmium.platform.bukkit.BukkitItemStack;
import com.kmecpp.osmium.platform.bukkit.GenericBukkitCommandSender;

public class BukkitAccess {

	public static EntityType getEntityType(org.bukkit.entity.EntityType type) {
		return OsmiumRegistry.fromSource(EntityType.class, type);
	}

	public static ItemStack getItemStack(org.bukkit.inventory.ItemStack itemStack) {
		return new BukkitItemStack(itemStack);
	}

	public static ItemType getItemType(org.bukkit.inventory.ItemStack itemStack) {
		return Osmium.getItemManager().getItemType(itemStack.getType());
	}

	public static Inventory getInventory(org.bukkit.inventory.Inventory inventory) {
		return new BukkitInventory(inventory);
	}

	public static Location getLocation(org.bukkit.Location location) {
		return new Location(getWorld(location.getWorld()), location.getX(), location.getY(), location.getZ());
	}

	public static Direction getDirection(org.bukkit.Location location) {
		return new Direction(location.getPitch(), location.getYaw());
	}

	public static WorldPosition getPosition(org.bukkit.Location location) {
		return new WorldPosition(getLocation(location), getDirection(location));
	}

	public static Chunk getChunk(org.bukkit.Chunk chunk) {
		return new BukkitChunk(chunk);
	}

	public static Entity getEntity(org.bukkit.entity.Entity entity) {
		return new BukkitEntity(entity);
	}

	public static Player getPlayer(org.bukkit.entity.Player player) {
		return PlayerList.getPlayer(player);
	}

	public static World getWorld(org.bukkit.World bukkitWorld) {
		return WorldList.getWorld(bukkitWorld);
		//		World world = WorldList.getWorld(bukkitWorld.getName());
		//		if (world == null) { //This can happen with Bukkit
		//			world = new BukkitWorld(bukkitWorld);
		//			WorldList.addWorld(world);
		//		}
		//		return world;
	}

	public static void processConsoleCommand(String command) {
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
	}

	public static void processConsoleCommand(CommandSender output, String command) {
		Bukkit.dispatchCommand(new BukkitConsoleCommandRedirect(output), command);
	}

	public static void processCommand(org.bukkit.command.CommandSender sender, String command) {
		Bukkit.dispatchCommand(sender, command);
	}

	public static Block getBlock(org.bukkit.block.Block block) {
		return new BukkitBlock(block);
	}

	public static void registerCommand(OsmiumPlugin plugin, Command command) {
		try {
			SimpleCommandMap commandMap = (SimpleCommandMap) Reflection.getFieldValue(Bukkit.getServer(), "commandMap");
			ReflectField<Map<String, org.bukkit.command.Command>> knownCommandsField = new ReflectField<>(SimpleCommandMap.class, "knownCommands");
			Map<String, org.bukkit.command.Command> bukkitInternalKnownCommands = knownCommandsField.get(commandMap);

			List<String> aliases = Arrays.asList(Arrays.copyOfRange(command.getAliases(), 1, command.getAliases().length));

			//If the primary alias is taken and we don't have a specified override method, change the primary alias to the first available alias
			if (command.getOverrideMode() == OverrideMode.NONE && commandMap.getCommand(command.getPrimaryAlias()) != null) {
				boolean success = false;
				for (String alias : command.getAliases()) {
					if (commandMap.getCommand(alias) == null) {
						String originalPrimaryAlias = command.getPrimaryAlias();
						command.setPrimaryAlias(alias);
						OsmiumLogger.debug("Remapped primary alias: " + originalPrimaryAlias + " -> " + alias);
						success = true;
						break;
					}
				}
				if (!success) {
					CommandManager.sendFailedRegistrationMessage(plugin, command);
				}
			}

			// Usage message cannot be null or else stuff will break
			Constructor<PluginCommand> constructor = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
			constructor.setAccessible(true);
			org.bukkit.command.PluginCommand bukkitCommand = constructor.newInstance(command.getPrimaryAlias(), plugin.getSource());

			bukkitCommand.setAliases(aliases);
			bukkitCommand.setUsage(command.getUsage());
			bukkitCommand.setDescription(command.getDescription());
			bukkitCommand.setExecutor(new CommandExecutor() {

				@Override
				public boolean onCommand(org.bukkit.command.CommandSender bukkitSender, org.bukkit.command.Command cmd, String label, String[] args) {
					try {
						CommandSender sender = bukkitSender instanceof org.bukkit.entity.Player
								? getPlayer((org.bukkit.entity.Player) bukkitSender)
								: bukkitSender instanceof org.bukkit.command.ConsoleCommandSender
										? new BukkitConsoleCommandSender((org.bukkit.command.ConsoleCommandSender) bukkitSender)
										: bukkitSender instanceof org.bukkit.command.BlockCommandSender
												? new BukkitBlockCommandSender((org.bukkit.command.BlockCommandSender) bukkitSender)
												: new GenericBukkitCommandSender(bukkitSender);
						Osmium.getCommandManager().invokeCommand(command, sender, label, args);
					} catch (Throwable t) {
						t.printStackTrace();
					}
					return true; //Always return true, otherwise Bukkit will print command usage
				}

			});

			commandMap.register(plugin.getName(), bukkitCommand);

			if (command.getOverrideMode() != OverrideMode.NONE) {
				//@formatter:off
				List<String> aliasesToOverride =
							  command.getOverrideMode() == OverrideMode.PRIMARY ? Arrays.asList(command.getPrimaryAlias())
							: command.getOverrideMode() == OverrideMode.SPECIFIC ? Arrays.asList(command.getOverrideAliases())
							: command.getOverrideMode() == OverrideMode.ALL ? Arrays.asList(command.getAliases())
							: Collections.emptyList();
				//@formatter:on

				for (String overrideAlias : aliasesToOverride) {
					org.bukkit.command.Command existing = bukkitInternalKnownCommands.put(overrideAlias.toLowerCase(), bukkitCommand);
					System.out.println("EXISTING COMMAND: " + existing);
					if (existing != bukkitCommand) {
						OsmiumLogger.info("Overriding command: " + existing);
					}
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void registerListener(OsmiumPlugin plugin, Listener listener) {
		Bukkit.getPluginManager().registerEvents(listener, plugin.getSource());
	}

	public static void registerOsmiumListener(OsmiumPlugin plugin, Class<? extends org.bukkit.event.Event> bukkitEventClass, Order order, Method method, Object listenerInstance, Consumer<Object> sourceEventConsumer) {
		//		for (Class<? extends org.bukkit.event.Event> bukkitEventClass : eventInfo.<org.bukkit.event.Event> getSourceClasses()) {

		Bukkit.getPluginManager().registerEvent(bukkitEventClass, OsmiumCore.getPlugin().getSource(), (EventPriority) order.getSource(), //NOTE: 
				(bukkitListener, bukkitEvent) -> sourceEventConsumer.accept(bukkitEvent),
				plugin.getSource(), false);
		//		}
	}

	public static void loadPlugin(File pluginFile) {
		try {
			Plugin pluginInstance = Bukkit.getPluginManager().loadPlugin(pluginFile);
			pluginInstance.onLoad(); //This is called by CraftServer separately after PluginManager.loadPlugin() in 1.7.10
			Bukkit.getPluginManager().enablePlugin(pluginInstance);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void unloadPlugin(OsmiumPlugin plugin) {
		new URLConnection(null) {

			@Override
			public void connect() throws IOException {
			}

		}.setDefaultUseCaches(false);

		Bukkit.getScheduler().cancelTasks(plugin.getSource());
		HandlerList.unregisterAll(plugin.<JavaPlugin> getSource());
		//TODO: Commands?

		Bukkit.getPluginManager().disablePlugin(plugin.getSource());
	}

}
