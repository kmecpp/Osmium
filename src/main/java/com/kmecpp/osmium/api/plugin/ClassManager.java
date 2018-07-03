package com.kmecpp.osmium.api.plugin;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.bukkit.Bukkit;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.event.EventPriority;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.source.CommandBlockSource;
import org.spongepowered.api.command.source.ConsoleSource;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;

import com.kmecpp.jlib.reflection.Reflection;
import com.kmecpp.osmium.OsmiumLogger;
import com.kmecpp.osmium.api.command.Command;
import com.kmecpp.osmium.api.command.CommandManager;
import com.kmecpp.osmium.api.command.CommandSender;
import com.kmecpp.osmium.api.command.OsmiumCommand;
import com.kmecpp.osmium.api.event.Event;
import com.kmecpp.osmium.api.event.Listener;
import com.kmecpp.osmium.api.event.events.EventInfo;
import com.kmecpp.osmium.api.platform.Platform;
import com.kmecpp.osmium.platform.bukkit.BukkitBlockCommandSender;
import com.kmecpp.osmium.platform.bukkit.BukkitConsoleCommandSender;
import com.kmecpp.osmium.platform.bukkit.BukkitPlayer;
import com.kmecpp.osmium.platform.bukkit.GenericBukkitCommandSender;
import com.kmecpp.osmium.platform.sponge.GenericSpongeCommandSender;
import com.kmecpp.osmium.platform.sponge.SpongeBlockCommandSender;
import com.kmecpp.osmium.platform.sponge.SpongeConsoleCommandSender;
import com.kmecpp.osmium.platform.sponge.SpongePlayer;

public class ClassManager {

	private final OsmiumPlugin plugin;
	private final Class<?> mainClass;
	private final Class<?> mainClassImpl;
	private final HashSet<Class<?>> pluginClasses = new HashSet<Class<?>>();

	private final HashMap<Class<?>, Object> listeners = new HashMap<>();
	private final HashMap<Class<?>, OsmiumCommand> commands = new HashMap<>();

	protected ClassManager(OsmiumPlugin plugin, Class<?> mainClassImpl) {
		this.plugin = plugin;
		this.mainClass = plugin.getClass();
		this.mainClassImpl = mainClassImpl;

		try {
			ClassLoader classLoader = mainClassImpl.getClass().getClassLoader();
			JarFile jarFile = new JarFile(new File(mainClass.getProtectionDomain().getCodeSource().getLocation().toURI()));
			String packageName = mainClass.getPackage().getName();

			Enumeration<JarEntry> entry = jarFile.entries();
			while (entry.hasMoreElements()) {
				String name = entry.nextElement().getName().replace("/", ".");
				if (name.startsWith(packageName) && name.endsWith(".class")) {
					try {
						String className = name.substring(0, name.length() - 6);
						if (classLoader == null) {
							pluginClasses.add(Class.forName(className));
						} else {
							pluginClasses.add(classLoader.loadClass(className));
						}
					} catch (NoClassDefFoundError e) {
						//Ignore unloaded classes
					}
				}
			}
			jarFile.close();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	//	public HashMap<Class<?>, Object> getListeners() {
	//		return listeners;
	//	}

	public void enableEvents(Object listener) {
		listeners.put(listener.getClass(), listener);
	}

	protected void initializeHooks() {
		for (Class<?> cls : pluginClasses) {
			if (!Reflection.isConcrete(cls)) {
				return;
			}

			//COMMANDS

			if (cls.isAnnotationPresent(Command.class)) {
				if (!OsmiumCommand.class.isAssignableFrom(cls)) {
					OsmiumLogger.warn("Class is annotated with @" + Command.class.getSimpleName() + " but does not extend " + OsmiumCommand.class.getSimpleName() + ": " + cls);
					continue;
				}

				OsmiumCommand command;
				try {
					command = (OsmiumCommand) cls.newInstance();
				} catch (InstantiationException | IllegalAccessException e) {
					OsmiumLogger.warn("Cannot cannot be initialized! Class must have a default constructor!");
					continue;
				}

				Command properties = command.getProperties();
				commands.put(cls, command);

				if (properties.aliases().length == 0) {
					OsmiumLogger.warn("Command does not have any aliases and will not be registered: " + cls);
					continue;
				}

				if (Platform.isBukkit()) {
					try {
						String commandName = properties.aliases()[0];
						String[] aliases = new String[properties.aliases().length - 1];
						System.arraycopy(properties.aliases(), 1, aliases, 0, aliases.length);

						SimpleCommandMap commandMap = (SimpleCommandMap) Reflection.getFieldValue(Bukkit.getServer(), "commandMap");

						commandMap.register(commandName, new BukkitCommand(commandName, "", "", Arrays.asList(aliases)) { //Usage message cannot be null or else stuff will break

							@Override
							public boolean execute(org.bukkit.command.CommandSender bukkitSender, String label, String[] args) {
								CommandSender sender = bukkitSender instanceof org.bukkit.entity.Player ? new BukkitPlayer((org.bukkit.entity.Player) bukkitSender)
										: bukkitSender instanceof org.bukkit.command.ConsoleCommandSender ? new BukkitConsoleCommandSender((org.bukkit.command.ConsoleCommandSender) bukkitSender)
												: bukkitSender instanceof org.bukkit.command.BlockCommandSender ? new BukkitBlockCommandSender((org.bukkit.command.BlockCommandSender) bukkitSender)
														: new GenericBukkitCommandSender(bukkitSender);

								return CommandManager.invokeCommand(command, sender, label, args);
							}

						});

					} catch (Exception e) {
						e.printStackTrace();
					}
				} else if (Platform.isSponge()) {
					//TODO
					CommandSpec spec = CommandSpec.builder()
							.description(Text.of(properties.description()))
							.permission(properties.permission())
							.executor((src, args) -> {
								CommandSender sender = src instanceof org.bukkit.entity.Player ? new SpongePlayer((org.spongepowered.api.entity.living.player.Player) src)
										: src instanceof ConsoleSource ? new SpongeConsoleCommandSender((ConsoleSource) src)
												: src instanceof CommandBlockSource ? new SpongeBlockCommandSender((CommandBlockSource) src)
														: new GenericSpongeCommandSender(src);

								//								CommandManager.invokeCommand(command, sender, commandLabel, args);
								return CommandResult.success();
							})
							.build();
					Sponge.getCommandManager().register(plugin.asSpongePlugin(), spec, properties.aliases());
				}
			}

			//LISTENERS
			for (Method method : cls.getMethods()) {
				Listener annotation = method.getAnnotation(Listener.class);
				if (annotation != null) {
					if (method.getParameterCount() != 1) {
						plugin.error("Invalid listener method with signature: '" + method + "'");
					} else if (!Event.class.isAssignableFrom(method.getParameterTypes()[0])) {
						plugin.error("Invalid listener method with signature: '" + method + "'");
					} else {
						Class<Event> eventClass = Reflection.cast(method.getParameterTypes()[0]);
						EventInfo eventInfo = EventInfo.get(eventClass);

						Object listenerInstance;
						try {
							boolean contains = listeners.containsKey(cls);
							listenerInstance = contains ? listeners.get(cls) : cls.newInstance();
							if (!contains) {
								listeners.put(cls, listenerInstance);
							}
						} catch (Exception e) {
							OsmiumLogger.error("Cannot instantiate " + cls.getName() + "! Listener classes without a default constructor must be enabled with: plugin.enableEvents(listener)");
							e.printStackTrace();
							break;
						}

						if (Platform.isBukkit()) {
							Class<? extends org.bukkit.event.Event> bukkitEventClass = eventInfo.getBukkitClass();
							EventPriority priority = (EventPriority) annotation.order().getSource();
							try {
								Constructor<?> eventWrapper = eventInfo.getBukkitImplementation().getConstructor(bukkitEventClass);
								Bukkit.getPluginManager().registerEvent(bukkitEventClass, (org.bukkit.event.Listener) plugin.asBukkitPlugin(), priority, (l, e) -> {
									if (bukkitEventClass.isAssignableFrom(e.getClass())) {
										try {
											method.invoke(listenerInstance, eventWrapper.newInstance(e));
										} catch (Exception ex) {
											ex.printStackTrace();
										}
									}
								}, plugin.asBukkitPlugin(), true);
							} catch (Exception e) {
								e.printStackTrace();
								break;
							}
						} else if (Platform.isSponge()) {
							//TODO
						}
					}
				}
			}
		}
	}

	public Class<?> getMainClass() {
		return mainClass;
	}

	public Class<?> getMainClassImpl() {
		return mainClassImpl;
	}

	public HashSet<Class<?>> getPluginClasses() {
		return pluginClasses;
	}

}
