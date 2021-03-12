package com.kmecpp.osmium;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.kmecpp.osmium.api.command.Command;
import com.kmecpp.osmium.api.command.CommandManager;
import com.kmecpp.osmium.api.command.CommandSender;
import com.kmecpp.osmium.api.entity.Player;
import com.kmecpp.osmium.api.event.EventInfo;
import com.kmecpp.osmium.api.event.Order;
import com.kmecpp.osmium.api.logging.OsmiumLogger;
import com.kmecpp.osmium.api.plugin.OsmiumPlugin;
import com.kmecpp.osmium.cache.PlayerList;
import com.kmecpp.osmium.platform.bungee.BungeeGenericCommandSender;
import com.kmecpp.osmium.platform.bungee.BungeePlayer;
import com.kmecpp.osmium.platform.osmium.CommandRedirectSender;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.Annotation;
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
					CommandManager.invokeCommand(command, sender, command.getPrimaryAlias(), args);
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

	public static void registerOsmiumListener(OsmiumPlugin plugin, EventInfo eventInfo, Order order, Method method, Object listenerInstance, Consumer<Object> sourceEventConsumer) {
		String parameterString = Arrays.stream(method.getParameterTypes()).map(c -> c.getName()).collect(Collectors.joining(","));

		for (Class<? extends Event> bungeeEventClass : eventInfo.<Event> getSourceClasses()) {
			try {
				ClassPool pool = ClassPool.getDefault();

				String className = listenerInstance.getClass().getName() + "$$$OsmiumBungeeWrapper$" + method.getName() + "$" + parameterString;
				OsmiumLogger.debug("Generating listener class " + className);
				CtClass cc = pool.makeClass(className);
				ConstPool constPool = cc.getClassFile().getConstPool();

				cc.addInterface(pool.get(Listener.class.getName()));

				CtMethod ctMethod = new CtMethod(CtClass.voidType, method.getName(), new CtClass[] { pool.get(bungeeEventClass.getName()) }, cc);

				AnnotationsAttribute attribute = new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);
				attribute.addAnnotation(new Annotation(constPool, pool.get(EventHandler.class.getName())));
				ctMethod.getMethodInfo().addAttribute(attribute);
				//			m.

				Listener bungeeListener = (Listener) cc.toClass(listenerInstance.getClass().getClassLoader(), listenerInstance.getClass().getProtectionDomain()).newInstance();
				BungeeCord.getInstance().getPluginManager().registerListener(plugin.getSource(), bungeeListener);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
