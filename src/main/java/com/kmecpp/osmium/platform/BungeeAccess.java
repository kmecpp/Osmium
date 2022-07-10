package com.kmecpp.osmium.platform;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Consumer;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Handler;
import java.util.stream.Collectors;

import org.yaml.snakeyaml.Yaml;

import com.kmecpp.osmium.Osmium;
import com.kmecpp.osmium.api.command.Command;
import com.kmecpp.osmium.api.command.CommandSender;
import com.kmecpp.osmium.api.entity.Player;
import com.kmecpp.osmium.api.event.Order;
import com.kmecpp.osmium.api.logging.OsmiumLogger;
import com.kmecpp.osmium.api.plugin.BungeePlugin;
import com.kmecpp.osmium.api.plugin.OsmiumPlugin;
import com.kmecpp.osmium.api.util.Reflection;
import com.kmecpp.osmium.cache.PlayerList;
import com.kmecpp.osmium.core.OsmiumCore;
import com.kmecpp.osmium.platform.bungee.BungeeGenericCommandSender;
import com.kmecpp.osmium.platform.bungee.BungeePlayer;
import com.kmecpp.osmium.platform.osmium.CommandRedirectSender;

import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMethod;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.ByteMemberValue;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Event;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginDescription;
import net.md_5.bungee.event.EventHandler;

public class BungeeAccess {

	public static Player getPlayer(ProxiedPlayer player) {
		return PlayerList.getPlayer(player);
	}

	public static void processConsoleCommand(String command) {
		BungeeCord.getInstance().getPluginManager().dispatchCommand(BungeeCord.getInstance().getConsole(), command);
	}

	public static void processConsoleCommand(CommandSender receiver, String command) {
		CommandRedirectSender sender = new CommandRedirectSender(new BungeeGenericCommandSender(BungeeCord.getInstance().getConsole()), receiver);
		Osmium.getCommandManager().processCommand(sender, command);
	}

	public static void processCommand(net.md_5.bungee.api.CommandSender sender, String command) {
		BungeeCord.getInstance().getPluginManager().dispatchCommand(sender, command);
	}

	public static void registerCommand(OsmiumPlugin plugin, Command command) {
		try {
			net.md_5.bungee.api.plugin.Command bungeeCommand = new net.md_5.bungee.api.plugin.Command(command.getPrimaryAlias(), command.getPermission(), command.getAliases()) {

				@Override
				public void execute(net.md_5.bungee.api.CommandSender bungeeSender, String[] args) {
					CommandSender sender = bungeeSender instanceof ProxiedPlayer ? new BungeePlayer((ProxiedPlayer) bungeeSender) : new BungeeGenericCommandSender(bungeeSender);
					Osmium.getCommandManager().invokeCommand(command, sender, command.getPrimaryAlias(), args);
				}

			};
			BungeeCord.getInstance().getPluginManager().registerCommand(plugin.getSource(), bungeeCommand);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void registerListener(OsmiumPlugin plugin, Listener listener) {
		BungeeCord.getInstance().getPluginManager().registerListener(plugin.getSource(), listener);
	}

	public static void registerOsmiumListener(OsmiumPlugin plugin, Class<? extends Event> bungeeEventClass, Order order, Method method, Object listenerInstance, Consumer<Event> osmiumSourceEventConsumer) {
		String parameterString = Arrays.stream(method.getParameterTypes()).map(c -> c.getSimpleName()).collect(Collectors.joining("|"));

		//		for (Class<? extends Event> bungeeEventClass : eventInfo.<Event> getSourceClasses()) {
		try {
			ClassPool pool = ClassPool.getDefault();
			pool.insertClassPath(new ClassClassPath(Listener.class));

			String className = listenerInstance.getClass().getName() + "##OsmiumBungeeEventHandlerWrapper#" + method.getName() + "#" + parameterString + "#" + bungeeEventClass.getSimpleName();

			OsmiumLogger.debug("Generating listener class " + className);
			CtClass generatedClass = pool.makeClass(className);
			ConstPool constPool = generatedClass.getClassFile().getConstPool();

			generatedClass.addInterface(pool.get(Listener.class.getName()));

			pool.insertClassPath(new ClassClassPath(Osmium.class));

			CtField ctField = CtField.make("private final java.util.function.Consumer consumer;", generatedClass);
			//				generatedClass.addField(ctField, Initializer.byExpr(Osmium.class.getName() + ".getEventManager().getOsmiumSourceEventConsumer(" + bungeeEventClass.getName() + ".class);"));
			generatedClass.addField(ctField);

			CtConstructor ctConstructor = new CtConstructor(new CtClass[] { pool.get("java.util.function.Consumer") }, generatedClass);
			ctConstructor.setBody("{ this.consumer = $1; }");
			generatedClass.addConstructor(ctConstructor);

			CtMethod ctMethod = new CtMethod(CtClass.voidType, method.getName(), new CtClass[] { pool.get(bungeeEventClass.getName()) }, generatedClass);
			ctMethod.setBody("{ consumer.accept($1); }");

			AnnotationsAttribute attribute = new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);
			Annotation annotation = new Annotation(constPool, pool.get(EventHandler.class.getName()));
			annotation.addMemberValue("priority", new ByteMemberValue((byte) order.getSource(), constPool));
			attribute.addAnnotation(annotation);
			ctMethod.getMethodInfo().addAttribute(attribute);

			generatedClass.addMethod(ctMethod);

			//			com.google.common.io.Files.write(generatedClass.toBytecode(), Paths.get(className + ".class").toFile());
			Class<?> cls = generatedClass.toClass(listenerInstance.getClass().getClassLoader(), listenerInstance.getClass().getProtectionDomain());
			Listener bungeeListener = (Listener) cls.getConstructor(Consumer.class).newInstance(osmiumSourceEventConsumer);

			//				Listener bungeeListener = (Listener) generatedClass.toClass(listenerInstance.getClass().getClassLoader(), listenerInstance.getClass().getProtectionDomain()).newInstance();
			//			BungeeCord.getInstance().getPluginManager().registerListener(plugin.getSource(), bungeeListener);
			BungeeCord.getInstance().getPluginManager().registerListener(OsmiumCore.getPlugin().getSource(), bungeeListener);

			//Note: can't create a Listener instance because bungeeEventClass is dynamic
		} catch (Exception e) {
			e.printStackTrace();
		}
		//		}
	}

	public static OsmiumPlugin loadPlugin(File pluginFile) {
		try {
			PluginDescription pluginDescription;

			try (JarFile jar = new JarFile(pluginFile, true)) {
				JarEntry pluginDescriptionJarEntry = jar.getJarEntry("bungee.yml");
				if (pluginDescriptionJarEntry == null) {
					pluginDescriptionJarEntry = jar.getJarEntry("plugin.yml");
				}

				try (InputStream in = jar.getInputStream(pluginDescriptionJarEntry)) {
					pluginDescription = new Yaml().loadAs(in, PluginDescription.class);
					pluginDescription.setFile(pluginFile);
				}
			}

			//			Object pluginManagerLibraryLoader = new ReflectField<>(PluginManager.class, "libraryLoader").get(ProxyServer.getInstance().getPluginManager());
			//			Object libraryLoader = pluginManagerLibraryLoader != null ? Reflection.invokeMethod(pluginManagerLibraryLoader, "createLoader", pluginDescription) : null;
			//			ClassLoader classLoader = Reflection.newInstance("net.md_5.bungee.api.plugin.PluginClassLoader", ProxyServer.getInstance(), pluginDescription, new File(pluginFile.getAbsolutePath()), libraryLoader);

			Constructor<ClassLoader> constructor = Reflection.getConstructor("net.md_5.bungee.api.plugin.PluginClassloader", ProxyServer.class, PluginDescription.class, File.class, ClassLoader.class);
			ClassLoader pluginClassLoader = constructor.newInstance(ProxyServer.getInstance(), pluginDescription, pluginDescription.getFile(), null);

			Class<?> main = pluginClassLoader.loadClass(pluginDescription.getMain());
			Plugin plugin = (Plugin) main.getDeclaredConstructor().newInstance(); //This will call PluginClassLoader.init(plugin)

			Reflection.<Map<String, Plugin>> getFieldValue(ProxyServer.getInstance().getPluginManager(), "plugins")
					.put(pluginDescription.getName(), plugin);

			plugin.onLoad();
			plugin.onEnable();

			if (plugin instanceof BungeePlugin) {
				return ((BungeePlugin) plugin).getOsmiumPlugin();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void unloadPlugin(OsmiumPlugin osmiumPlugin) {
		Plugin plugin = osmiumPlugin.getSource();
		plugin.getDescription();

		plugin.onDisable();
		for (Handler handler : plugin.getLogger().getHandlers()) {
			handler.close();
		}
		BungeeCord.getInstance().getScheduler().cancel(plugin);
		BungeeCord.getInstance().getPluginManager().unregisterListeners(plugin);
		BungeeCord.getInstance().getPluginManager().unregisterCommands(plugin);

		try {
			URLClassLoader pluginClassLoader = (URLClassLoader) plugin.getClass().getClassLoader();
			pluginClassLoader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
