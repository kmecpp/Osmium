package com.kmecpp.osmium.api.plugin;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.EnumSet;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import com.kmecpp.osmium.Directory;
import com.kmecpp.osmium.Osmium;
import com.kmecpp.osmium.Platform;
import com.kmecpp.osmium.api.AutoRegister;
import com.kmecpp.osmium.api.HookClass;
import com.kmecpp.osmium.api.OnlinePlayerData;
import com.kmecpp.osmium.api.command.Command;
import com.kmecpp.osmium.api.config.ConfigClass;
import com.kmecpp.osmium.api.database.MultiplePlayerData;
import com.kmecpp.osmium.api.database.PlayerData;
import com.kmecpp.osmium.api.database.api.DBTable;
import com.kmecpp.osmium.api.database.api.DatabaseType;
import com.kmecpp.osmium.api.event.Event;
import com.kmecpp.osmium.api.event.EventAbstraction;
import com.kmecpp.osmium.api.event.EventInfo;
import com.kmecpp.osmium.api.event.Listener;
import com.kmecpp.osmium.api.logging.OsmiumLogger;
import com.kmecpp.osmium.api.persistence.Persistent;
import com.kmecpp.osmium.api.persistence.PersistentField;
import com.kmecpp.osmium.api.persistence.PersistentPluginData;
import com.kmecpp.osmium.api.tasks.Schedule;
import com.kmecpp.osmium.api.util.Reflection;
import com.kmecpp.osmium.platform.BukkitAccess;
import com.kmecpp.osmium.platform.BungeeAccess;
import com.kmecpp.osmium.platform.SpongeAccess;

import ninja.leaping.configurate.commented.CommentedConfigurationNode;

public class ClassProcessor {

	private final OsmiumPlugin plugin;
	private final Class<?> mainClass;
	private final Class<?> mainClassImpl;
	private final HashSet<Class<?>> pluginClasses = new HashSet<Class<?>>();
	private final HashSet<Class<?>> externalClasses = new HashSet<Class<?>>();

	private final HashMap<Class<?>, Object> classInstances = new HashMap<>();
	//	private final HashMap<Class<?>, Command> commands = new HashMap<>();
	private final HashSet<String> skipClasses = new HashSet<>();

	private final HashSet<Class<?>> databaseTables = new HashSet<>();
	//	private final HashSet<Class<?>> mysqlTables = new HashSet<>();

	protected ClassProcessor(OsmiumPlugin plugin, Object pluginImpl) {
		this.plugin = plugin;
		this.mainClass = plugin.getClass();
		this.mainClassImpl = pluginImpl.getClass();
	}

	public void loadAll() throws Exception {
		//		OsmiumClassLoader classLoader = new OsmiumClassLoader(mainClassImpl.getClassLoader());

		JarFile jarFile = Directory.getJarFile(mainClass);
		String packageName = Reflection.getPackageName(mainClass); //For some reason mainClass.getPackage() started returning null

		//		HashSet<String> staticLoadClasses = null;
		//		ZipEntry staticLoadFile = jarFile.getEntry("static-load-classes");
		//		if (staticLoadFile != null) {
		//			staticLoadClasses = new HashSet<>();
		//			InputStream is = jarFile.getInputStream(staticLoadFile);
		//			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		//
		//			int read;
		//			byte[] data = new byte[1024];
		//
		//			while ((read = is.read(data, 0, data.length)) != -1) {
		//				buffer.write(data, 0, read);
		//			}
		//
		//			String contents = new String(buffer.toByteArray());
		//			for (String line : contents.split("\n")) {
		//				staticLoadClasses.add(line);
		//				OsmiumLogger.debug("Adding static load class: " + line);
		//			}
		//		} else {
		//			OsmiumLogger.warn("LOAD FILE: " + staticLoadFile);
		//		}

		//		System.out.println(this.getClass().getClassLoader());
		//		URLClassLoader classLoader = new URLClassLoader(new URL[] { new File(jarFile.getName()).toURI().toURL() }, this.getClass().getClassLoader());
		ClassLoader classLoader = plugin.getSource().getClass().getClassLoader();

		Enumeration<JarEntry> entry = jarFile.entries(); //NOTE: THIS INCLUDES NESTED CLASSES
		while (entry.hasMoreElements()) {
			String elementName = entry.nextElement().getName().replace("/", ".");

			//Only search for elements below the parent package
			if (elementName.startsWith(packageName) && elementName.endsWith(".class")) {
				String className = elementName.substring(0, elementName.length() - 6);

				//				if (staticLoadClasses != null && !staticLoadClasses.contains(className)) {
				//					continue;
				//				}

				try {
					if (skipClasses.contains(className)) {
						continue;
					}
					OsmiumLogger.debug("Loading class: " + className);
					//					Class<?> cls = classLoader.loadClass(className, true);
					//					Class<?> cls = plugin.getSource().getClass().getClassLoader().loadClass(className);
					Class<?> cls = Class.forName(className, false, classLoader);

					if (cls.isAnnotationPresent(HookClass.class) || cls.isAnnotationPresent(SkipProcessing.class)) {
						skipClasses.add(cls.getName());
						continue;
					}
					//TODO: Only load static-load-classes
					cls.getDeclaredMethods(); //Verify that return types exist
					cls.getDeclaredFields(); //Verify that field types exist
					pluginClasses.add(cls); //NOTE: THIS ADDS NESTED CLASSES TOO
					//					Class<?> cls = Class.forName(className, false, classLoader);
					//					cls.getFields();
					onLoad(cls);
				} catch (ClassNotFoundException | NoClassDefFoundError ex) {
					String exceptionMessage = ex.getMessage().toLowerCase();
					if (exceptionMessage.contains("spongepowered") || exceptionMessage.contains("bukkit") || exceptionMessage.contains("bungee")) {
						OsmiumLogger.debug("SKIPPING: " + className + " (CLASS NOT FOUND: " + exceptionMessage + ")");
					} else {
						OsmiumLogger.warn("Could not load class: " + className);
						OsmiumLogger.warn(ex.getLocalizedMessage());
						//						ex.printStackTrace();
					}
					//Ignore classes depending on different platforms (TODO: THIS COULD EASILY BREAK STUFF??)
				} catch (Throwable t) {
					OsmiumLogger.error("Failed to load plugin class: " + className);
					t.printStackTrace();
				}
			}
		}
		jarFile.close();
		//		classLoader.close();
	}

	public void skip(Class<?> cls) {
		skipClasses.add(cls.getName());
	}

	//	public void process(Class<?> cls) {
	//		cls.getDeclaredMethods(); //Verify that return types exist
	//		onLoad(cls);
	//		pluginClasses.add(cls);
	//		onEnable(cls);
	//	}

	public Class<?> getMainClass() {
		return mainClass;
	}

	public Class<?> getMainClassImpl() {
		return mainClassImpl;
	}

	public HashSet<Class<?>> getPluginClasses() {
		return pluginClasses;
	}

	public HashSet<Class<?>> getExternalClasses() {
		return externalClasses;
	}

	public <T> T getInstance(Class<T> cls) {
		return Reflection.cast(classInstances.get(cls));
	}

	public void provideInstance(Object listener) {
		classInstances.put(listener.getClass(), listener);
	}

	public void addExternalClass(Class<?> cls) {
		externalClasses.add(cls);
		if (!cls.isAnnotationPresent(HookClass.class) && !cls.isAnnotationPresent(SkipProcessing.class)) {
			onLoad(cls);
		}
	}

	protected void initializeClasses() {
		initializeClasses(pluginClasses);
		initializeClasses(externalClasses);
	}

	protected void initializeClasses(HashSet<Class<?>> classes) {
		for (Class<?> cls : classes) {
			if (!Reflection.isConcrete(cls) || skipClasses.contains(cls.getName())) {
				continue;
			}

			try {
				onEnable(cls);
			} catch (Throwable t) {
				OsmiumLogger.error("Failed to enable class: " + cls.getName());
				t.printStackTrace();
			}
		}
	}

	public void onLoad(Class<?> cls) {
		//CONFIGURATIONS
		ConfigClass configuration = cls.getAnnotation(ConfigClass.class);
		if (configuration != null) {
			Osmium.getConfigManager().register(plugin, cls);
			if (!configuration.manualLoad()) {
				Osmium.loadConfig(cls);
			}
		}

		//DATABASE TABLES
		//		DBTable table = cls.getAnnotation(DBTable.class);
		//		if (table != null) {
		//			OsmiumLogger.debug("Initializing database table: " + table.name());
		//			Osmium.getDatabase(plugin).createTable(cls);
		//			Database.isSerializable(cls);
		//		}

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
		if (Command.class.isAssignableFrom(cls) && !Modifier.isAbstract(cls.getModifiers())) {
			Command command;
			try {
				command = (Command) cls.newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				OsmiumLogger.warn("Command class: '" + cls.getName() + "' cannot be initialized! It must have a default constructor!");
				return;
			}

			//				commands.put(cls, command);

			if (command.getAliases().length == 0) {
				OsmiumLogger.warn("Command does not have any aliases and will not be registered: " + cls);
				return;
			}

			Osmium.getCommandManager().register(plugin, command);
		}

		if (cls.isAnnotationPresent(DBTable.class)) {
			databaseTables.add(cls);
		}
		//		if (cls.isAnnotationPresent(MySQLTable.class)) {
		//			mysqlTables.add(cls);
		//		}

		for (Method method : cls.getDeclaredMethods()) {
			Schedule scheduleAnnotation = method.getAnnotation(Schedule.class);
			Listener listenerAnnotation = method.getAnnotation(Listener.class);
			Initializer initializer = method.getAnnotation(Initializer.class);

			if (scheduleAnnotation == null && listenerAnnotation == null && initializer == null) {
				continue;
			}

			method.setAccessible(true);

			//Retrieve instance or create one if possible
			Object instance = safeGetInstance(cls);

			//STARTUP
			if (initializer != null) {
				try {
					method.invoke(instance);
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					OsmiumLogger.error("Method " + cls.getSimpleName() + "." + method.getName() + " annotated with @" + Initializer.class.getSimpleName()
							+ " cannot be executed. Does it require arguments?");
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
						OsmiumLogger.error("Osmium event class has no registered implementation: " + eventClass.getName());
						continue;
					} else if (eventInfo.getOsmiumImplementation() == null) {
						OsmiumLogger.error("Failed to register " + eventInfo.getEventWrapperClass().getSimpleName() + " listener with " + cls.getSimpleName() + ". "
								+ "The event is not implemented for this platform.");
						continue;
					}

					if (eventInfo.isOsmiumEvent()) {
						//Register implementation class for Osmium
						OsmiumLogger.debug("Registering listener for " + eventInfo.getEventWrapperClass().getSimpleName() + ": " + cls.getSimpleName() + "." + method.getName());
						Osmium.getEventManager().registerOsmiumEventListener(plugin, eventInfo.getOsmiumImplementation(), listenerAnnotation.order(), instance, method);

					} else {
						Osmium.getEventManager().registerListener(plugin, eventInfo, listenerAnnotation.order(), method, instance);
					}
				}
			}
		}

		for (Field field : cls.getDeclaredFields()) {
			OnlinePlayerData onlinePlayerDataAnnotation = field.getDeclaredAnnotation(OnlinePlayerData.class);
			if (onlinePlayerDataAnnotation != null) {
				try {
					Class<?> fieldType = field.getType();
					field.setAccessible(true);

					if (!Modifier.isStatic(field.getModifiers())) {
						OsmiumLogger.warn("Fields annotated with @" + OnlinePlayerData.class.getSimpleName() + " must be static!");
					} else if (!Modifier.isFinal(field.getModifiers())) {
						OsmiumLogger.warn("Fields annotated with @" + OnlinePlayerData.class.getSimpleName() + " must be final!");
					} else if (field.get(null) == null) {
						OsmiumLogger.warn("Fields annotated with @" + OnlinePlayerData.class.getSimpleName() + " must not be null!");
					} else if (!Map.class.isAssignableFrom(fieldType) && !Set.class.isAssignableFrom(fieldType)) {
						OsmiumLogger.warn("Fields annotated with @" + OnlinePlayerData.class.getSimpleName() + " must have type Map<UUID, ?> or Set<UUID>");
					} else {
						Osmium.getPlayerDataManager().registerOnlinePlayerDataField(field, onlinePlayerDataAnnotation);
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}

		AutoRegister autoRegister = cls.getDeclaredAnnotation(AutoRegister.class);
		if (autoRegister != null) {
			OsmiumLogger.warn("Auto registering: " + cls.getName());
			Object instance = safeGetInstance(cls);
			if (Platform.isBukkit()) {
				BukkitAccess.registerListener(plugin, (org.bukkit.event.Listener) instance);
			} else if (Platform.isBungeeCord()) {
				BungeeAccess.registerListener(plugin, (net.md_5.bungee.api.plugin.Listener) instance);
			} else if (Platform.isSponge()) {
				SpongeAccess.registerListener(plugin, instance);
			}
		}
	}

	private Object safeGetInstance(Class<?> cls) {
		Object instance = null;
		try {
			Class.forName(cls.getName()); //Initialize class. This hack allows classes to register themselves in a static initializer

			//THE FOLLOWING CODE IS DONE THIS WAY BECAUSE THE LISTENER INSTANCE MUST BE FINAL
			Object temp = classInstances.get(cls);
			if (temp != null) {
				instance = temp;
			} else {
				instance = cls.newInstance(); //WE DON'T INSTANTIATE THE CLASS UNTIL DOWN HERE BECAUSE WE ONLY WANT TO INSTANTIATE IF IT HAS AN ANNOTATION 
				classInstances.put(cls, instance);
			}
		} catch (IllegalAccessException | InstantiationException | ExceptionInInitializerError | SecurityException e) {
			OsmiumLogger.error("Cannot instantiate " + cls.getName() + "! Task and listener classes without a default constructor must be enabled with: plugin.provideInstance(obj)");
			e.printStackTrace();
		} catch (Exception e) {
			OsmiumLogger.error("Caught exception while trying to instantiate task/listener class: " + cls.getName());
			e.printStackTrace();
		}
		return instance;
	}

	/*
	 * This is called on startup and full reloads
	 */
	public void createDatabaseTables() {
		Boolean mysqlConnected = null;
		Boolean sqliteConnected = null;

		for (Class<?> cls : databaseTables) {
			try {
				DBTable table = cls.getAnnotation(DBTable.class);

				if (!table.autoCreate()) {
					continue;
				}

				EnumSet<DatabaseType> seen = EnumSet.noneOf(DatabaseType.class);
				for (DatabaseType type : table.type()) {
					if (seen.contains(type)) {
						continue; //Skip duplicates
					}
					seen.add(type);

					if (type == DatabaseType.MYSQL) {
						if (mysqlConnected == null) {
							try {
								if (!plugin.getMySQLDatabase().isConnected()) {
									plugin.getMySQLDatabase().initialize(); //Mark this database as active for Osmium
									plugin.getMySQLDatabase().start();
								}
							} catch (Throwable t) {
								OsmiumLogger.error("Failed to connect to " + plugin.getName() + "'s MySQL database!");
								t.printStackTrace();
							}
							mysqlConnected = plugin.getMySQLDatabase().isConnected();
						}

						plugin.getMySQLDatabase().registerTable(cls); //Should always register the table, even if we can't connect to the database. Otherwise, we might think there was a registration problem
						if (mysqlConnected) {
							OsmiumLogger.debug("Initializing MySQL database table: " + table.name());

							plugin.getMySQLDatabase().createTable(cls);

							if (PlayerData.class.isAssignableFrom(cls) || MultiplePlayerData.class.isAssignableFrom(cls)) {
								Osmium.getPlayerDataManager().registerPlayerDataType(plugin, Reflection.cast(cls));
							}
						} else {
							OsmiumLogger.warn("Missing database connection! Cannot create " + plugin.getName() + "'s MySQL database table: " + table.name());
						}
					} else if (type == DatabaseType.SQLITE) {
						if (sqliteConnected == null) {
							try {
								if (!plugin.getSQLiteDatabase().isConnected()) {
									plugin.getSQLiteDatabase().initialize(); //Mark this database as active for Osmium
									plugin.getSQLiteDatabase().start();
								}
							} catch (Throwable t) {
								OsmiumLogger.error("Failed to connect to " + plugin.getName() + "'s SQLite database!");
								t.printStackTrace();
							}
							sqliteConnected = plugin.getSQLiteDatabase().isConnected();
						}

						plugin.getSQLiteDatabase().registerTable(cls); //Should always register the table, even if we can't connect to the database. Otherwise, we might think there was a registration problem
						if (sqliteConnected) {
							OsmiumLogger.debug("Initializing SQLite database table: " + table.name());
							plugin.getSQLiteDatabase().createTable(cls);

							if (PlayerData.class.isAssignableFrom(cls) || MultiplePlayerData.class.isAssignableFrom(cls)) {
								Osmium.getPlayerDataManager().registerPlayerDataType(plugin, Reflection.cast(cls));
							}
						} else {
							OsmiumLogger.warn("Missing database connection! Cannot create " + plugin.getName() + "'s SQLite database table: " + table.name());
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
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

		//		for (Class<?> cls : mysqlTables) {
		//			MySQLTable mysqlTable = cls.getAnnotation(MySQLTable.class);
		//			//			System.out.println("MYSQL TABLE : " + cls);
		//			if (mysqlTable != null && mysqlTable.autoCreate()) {
		//				try {
		//					OsmiumLogger.debug("Initializing MySQL database table: " + mysqlTable.name());
		//					plugin.getMySQLDatabase().createTable(cls);
		//
		//					if (PlayerData.class.isAssignableFrom(cls) || MultiplePlayerData.class.isAssignableFrom(cls)) {
		//						Osmium.getPlayerDataManager().registerPlayerDataType(plugin, Reflection.cast(cls));
		//					}
		//				} catch (Exception e) {
		//					e.printStackTrace();
		//				}
		//			}
		//		}
	}

}
