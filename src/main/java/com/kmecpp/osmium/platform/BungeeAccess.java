package com.kmecpp.osmium.platform;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.kmecpp.osmium.Osmium;
import com.kmecpp.osmium.api.command.Command;
import com.kmecpp.osmium.api.command.CommandSender;
import com.kmecpp.osmium.api.entity.Player;
import com.kmecpp.osmium.api.event.Order;
import com.kmecpp.osmium.api.logging.OsmiumLogger;
import com.kmecpp.osmium.api.plugin.OsmiumPlugin;
import com.kmecpp.osmium.cache.PlayerList;
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
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Event;
import net.md_5.bungee.api.plugin.Listener;
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
			BungeeCord.getInstance().getPluginManager().registerListener(plugin.getSource(), bungeeListener);

			//Note: can't create a Listener instance because bungeeEventClass is dynamic
		} catch (Exception e) {
			e.printStackTrace();
		}
		//		}
	}

}
