package com.kmecpp.osmium.api.plugin;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import com.kmecpp.osmium.BukkitAccess;
import com.kmecpp.osmium.Directory;
import com.kmecpp.osmium.Osmium;
import com.kmecpp.osmium.OsmiumClassLoader;
import com.kmecpp.osmium.SpongeAccess;
import com.kmecpp.osmium.api.command.Command;
import com.kmecpp.osmium.api.config.ConfigProperties;
import com.kmecpp.osmium.api.database.DBTable;
import com.kmecpp.osmium.api.database.PlayerData;
import com.kmecpp.osmium.api.event.Event;
import com.kmecpp.osmium.api.event.EventAbstraction;
import com.kmecpp.osmium.api.event.EventInfo;
import com.kmecpp.osmium.api.event.Listener;
import com.kmecpp.osmium.api.logging.OsmiumLogger;
import com.kmecpp.osmium.api.persistence.Persistent;
import com.kmecpp.osmium.api.persistence.PersistentField;
import com.kmecpp.osmium.api.persistence.PersistentPluginData;
import com.kmecpp.osmium.api.platform.Platform;
import com.kmecpp.osmium.api.tasks.Schedule;
import com.kmecpp.osmium.api.util.Reflection;

import ninja.leaping.configurate.commented.CommentedConfigurationNode;

public class ClassProcessor {

	private final OsmiumPlugin plugin;
	private final Class<?> mainClass;
	private final Class<?> mainClassImpl;
	private final HashSet<Class<?>> pluginClasses = new HashSet<Class<?>>();

	private final HashMap<Class<?>, Object> classInstances = new HashMap<>();
	private final HashMap<Class<?>, Command> commands = new HashMap<>();

	protected ClassProcessor(OsmiumPlugin plugin, Object pluginImpl) throws Exception {
		this.plugin = plugin;
		this.mainClass = plugin.getClass();
		this.mainClassImpl = pluginImpl.getClass();

		OsmiumClassLoader classLoader = new OsmiumClassLoader(mainClassImpl.getClassLoader());

		JarFile jarFile = Directory.getJarFile(mainClass);
		String packageName = mainClass.getPackage().getName();

		Enumeration<JarEntry> entry = jarFile.entries();
		while (entry.hasMoreElements()) {
			String elementName = entry.nextElement().getName().replace("/", ".");

			//Only search for elements below the parent package
			if (elementName.startsWith(packageName) && elementName.endsWith(".class")) {
				String className = elementName.substring(0, elementName.length() - 6);
				try {
					OsmiumLogger.debug("Loading class: " + className);
					Class<?> cls = classLoader.loadClass(className, true);
					//					Class<?> cls = Class.forName(className, false, classLoader);
					cls.getDeclaredMethods(); //Verify that return types exist
					//					cls.getFields();
					onLoad(cls);
					pluginClasses.add(cls);
				} catch (ClassNotFoundException | NoClassDefFoundError e) {
					if (e.getMessage().contains("org/spongepowered") || e.getMessage().contains("org/bukkit")) {
						OsmiumLogger.debug("SKIPPING: " + className);
					} else {
						OsmiumLogger.error("Could not load class: " + className);
						e.printStackTrace();
					}
					//Ignore classes depending on different platforms (TODO: THIS COULD EASILY BREAK STUFF??)
				} catch (Throwable t) {
					OsmiumLogger.error("Failed to load plugin class: " + className);
					t.printStackTrace();
				}
			}
		}
		jarFile.close();
		classLoader.close();

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

	public HashMap<Class<?>, Object> getClassInstances() {
		return classInstances;
	}

	public void provideInstance(Object listener) {
		classInstances.put(listener.getClass(), listener);
	}

	protected void initializeClasses() {
		for (Class<?> cls : pluginClasses) {
			if (!Reflection.isConcrete(cls)) {
				continue;
			}
			try {
				onEnable(cls);
			} catch (Exception e) {
				OsmiumLogger.error("Failed to enable class: " + cls.getName());
				e.printStackTrace();
			}
		}
	}

	public void onLoad(Class<?> cls) {
		//CONFIGURATIONS
		ConfigProperties configuration = cls.getAnnotation(ConfigProperties.class);
		if (configuration != null) {
			Osmium.getConfigManager().initialize(cls);
			if (!configuration.loadLate()) {
				Osmium.reloadConfig(cls);
			}
		}

		//DATABASE TABLES
		//		DBTable table = cls.getAnnotation(DBTable.class);
		//		if (table != null) {
		//			OsmiumLogger.debug("Initializing database table: " + table.name());
		//			Osmium.getDatabase(plugin).createTable(cls);
		//			Database.isSerializable(cls);
		//		}
		DBTable entity = cls.getAnnotation(DBTable.class);
		if (entity != null) {
			OsmiumLogger.debug("Initializing database table: " + entity.name());
			plugin.getDatabase().createTable(cls);

			if (PlayerData.class.isAssignableFrom(cls)) {
				Osmium.getPlayerDataManager().registerType(plugin, cls);
			}
			//			try {
			//				ClassPool.getDefault().insertClassPath(new ClassClassPath(cls));
			//				CtClass c = ClassPool.getDefault().getAndRename(cls.getName(), cls.getName() + "_REMAPPED");
			//				for (CtField field : c.getDeclaredFields()) {
			//					if (!field.hasAnnotation(Transient.class)) {
			//						String customType = plugin.getDatabase().getTypeKey(field.getType().getClass());
			//						if (customType != null) {
			//							ConstPool cp = c.getClassFile().getConstPool();
			//							Annotation annotation = new Annotation(Type.class.getName(), cp);
			//							annotation.addMemberValue("type", new StringMemberValue(customType, cp));
			//							field.getFieldInfo().addAttribute(new AnnotationsAttribute(cp, null));
			//						}
			//					}
			//				}
			//				plugin.getDatabase().addTable(c.toClass());
			//			} catch (CannotCompileException | NotFoundException e) {
			//				OsmiumLogger.error("Failed to register database table: " + cls.getName());
			//				e.printStackTrace();
			//			}
		}

		//PERSISTENT FIELDS
		for (Field field : cls.getDeclaredFields()) {
			Persistent persistentAnnotation = field.getAnnotation(Persistent.class);
			if (persistentAnnotation != null) {
				if (!Modifier.isStatic(field.getModifiers())) {
					OsmiumLogger.error("Fields annotated with @" + Persistent.class.getSimpleName() + " must be static! Incorrect: " + field);
					continue;
				}

				PersistentPluginData data = plugin.getPersistentData();
				field.setAccessible(true);
				PersistentField persistentField = new PersistentField(persistentAnnotation, field);

				try {
					CommentedConfigurationNode node = data.load(persistentField);
					if (node.isVirtual()) {
						return; //Node doesn't exist anymore use the default
					}

					Object value = node.getValue();
					if (value == null && field.getType().isPrimitive()) {
						return; //Data field set to null but this is incorrect. Use default
					}

					field.set(null, value); //Update the value of the field
				} catch (IllegalArgumentException | IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void onEnable(Class<?> cls) {
		OsmiumLogger.debug("Initializing class: " + cls.getName());

		//COMMANDS
		if (Command.class.isAssignableFrom(cls)) {
			Command command;
			try {
				command = (Command) cls.newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				OsmiumLogger.warn("Cannot cannot be initialized! Class must have a default constructor!");
				return;
			}

			commands.put(cls, command);

			if (command.getAliases().length == 0) {
				OsmiumLogger.warn("Command does not have any aliases and will not be registered: " + cls);
				return;
			}

			if (Platform.isBukkit()) {
				BukkitAccess.registerCommand(plugin, command);
			} else if (Platform.isSponge()) {
				SpongeAccess.registerCommand(plugin, command);
			}
		}

		for (Method method : cls.getDeclaredMethods()) {
			Schedule scheduleAnnotation = method.getAnnotation(Schedule.class);
			Listener listenerAnnotation = method.getAnnotation(Listener.class);
			Startup startup = method.getAnnotation(Startup.class);

			if (scheduleAnnotation == null && listenerAnnotation == null && startup == null) {
				continue;
			}

			method.setAccessible(true);

			//Retrieve instance or create one if possible
			final Object instance;
			try {
				Class.forName(cls.getName()); //Initialize class. This hack allows classes to register themselves in a static initializer

				//THE FOLLOWING CODE IS DONE THIS WAY BECAUSE THE LISTENER INSTANCE MUST BE FINAL
				Object temp = classInstances.get(cls);
				if (temp != null) {
					instance = temp;
				} else {
					instance = cls.newInstance();
					classInstances.put(cls, instance);
				}
			} catch (IllegalAccessException | InstantiationException | ExceptionInInitializerError | SecurityException e) {
				OsmiumLogger.error("Cannot instantiate " + cls.getName() + "! Task and listener classes without a default constructor must be enabled with: plugin.provideInstance(obj)");
				e.printStackTrace();
				break;
			} catch (Exception e) {
				OsmiumLogger.error("Caught exception while trying to instantiate task/listener class: " + cls.getName());
				e.printStackTrace();
				break;
			}

			//STARTUP
			if (startup != null) {
				try {
					method.invoke(instance);
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					OsmiumLogger.error("Method " + cls.getSimpleName() + "." + method.getName() + " annotated with @" + Startup.class.getSimpleName()
							+ " cannot be executed because it contains parameters!");
					e.printStackTrace();
				}
			}

			//TASKS
			if (scheduleAnnotation != null) {
				plugin.getTask()
						.setAsync(scheduleAnnotation.async())
						.setDelay(scheduleAnnotation.delay(), scheduleAnnotation.unit())
						.setInterval(scheduleAnnotation.interval(), scheduleAnnotation.unit())
						.setExecutor((t) -> {
							try {
								method.invoke(instance);
							} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
								e.printStackTrace();
							}
						})
						.start();
			}

			//LISTENERS
			if (listenerAnnotation != null) {
				if (method.getParameterCount() != 1 || !Event.class.isAssignableFrom(method.getParameterTypes()[0])) {
					plugin.error("Invalid listener method with signature: '" + method + "'");
				} else {
					Class<? extends EventAbstraction> eventClass = Reflection.cast(method.getParameterTypes()[0]);
					EventInfo eventInfo = EventInfo.get(eventClass);

					if (eventInfo == null) {
						OsmiumLogger.error("Osmium event class has no registration: " + eventClass);
						continue;
					}

					if (eventInfo.isOsmiumEvent()) {
						//Register implementation class for Osmium
						OsmiumLogger.debug("Registering listener for " + eventInfo.getEvent() + ": " + cls.getSimpleName() + method.getName());
						Osmium.getEventManager()
								.registerListener(eventInfo.getOsmiumImplementation(), listenerAnnotation.order(), instance, method);

					} else {
						Osmium.getEventManager().registerListener(plugin, eventInfo, listenerAnnotation.order(), method, instance);
					}
				}
			}
		}
	}

}
