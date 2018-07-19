package com.kmecpp.osmium;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.source.CommandBlockSource;
import org.spongepowered.api.command.source.ConsoleSource;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.event.EventListener;
import org.spongepowered.api.text.Text;

import com.kmecpp.osmium.api.command.Command;
import com.kmecpp.osmium.api.command.CommandManager;
import com.kmecpp.osmium.api.command.CommandSender;
import com.kmecpp.osmium.api.event.EventInfo;
import com.kmecpp.osmium.api.event.Order;
import com.kmecpp.osmium.api.logging.OsmiumLogger;
import com.kmecpp.osmium.api.plugin.OsmiumPlugin;
import com.kmecpp.osmium.platform.sponge.GenericSpongeCommandSender;
import com.kmecpp.osmium.platform.sponge.SpongeBlockCommandSender;
import com.kmecpp.osmium.platform.sponge.SpongeConsoleCommandSender;
import com.kmecpp.osmium.platform.sponge.SpongePlayer;

public class SpongeAccess {

	public static Text getText(String str) {
		return Text.of(str);
	}

	public static void registerCommand(OsmiumPlugin plugin, Command command) {
		CommandSpec spec = CommandSpec.builder()
				.description(SpongeAccess.getText(command.getDescription()))
				.permission(command.getPermission())
				.arguments(GenericArguments.remainingRawJoinedStrings(SpongeAccess.getText("args")))
				.executor((src, context) -> {
					CommandSender sender = src instanceof org.spongepowered.api.entity.living.player.Player ? new SpongePlayer((org.spongepowered.api.entity.living.player.Player) src)
							: src instanceof ConsoleSource ? new SpongeConsoleCommandSender((ConsoleSource) src)
									: src instanceof CommandBlockSource ? new SpongeBlockCommandSender((CommandBlockSource) src)
											: new GenericSpongeCommandSender(src);

					String[] args = context.<String> getOne("args").map((s) -> s.split(" ")).orElse(new String[0]);

					return CommandManager.invokeCommand(command, sender, command.getAliases()[0], args)
							? CommandResult.success()
							: CommandResult.empty();
				})
				.build();

		Sponge.getCommandManager().register(plugin.getPluginImplementation(), spec, command.getAliases());
		OsmiumLogger.debug("Registered Sponge command: /" + command.getPrimaryAlias());
	}

	public static void registerListener(OsmiumPlugin plugin, EventInfo eventInfo, Order order, Method method, Object listenerInstance) throws Exception {
		Class<? extends org.spongepowered.api.event.Event> spongeEventClass = eventInfo.getSpongeClass();
		Constructor<?> eventWrapper = eventInfo.getBukkitImplementation().getConstructor(spongeEventClass);

		Sponge.getEventManager().registerListener(plugin.getPluginImplementation(), spongeEventClass, (org.spongepowered.api.event.Order) order.getSource(), false,
				new EventListener<org.spongepowered.api.event.Event>() {

					@Override
					public void handle(org.spongepowered.api.event.Event spongeEvent) throws Exception {
						if (spongeEventClass.isAssignableFrom(spongeEvent.getClass())) {
							try {
								method.invoke(listenerInstance, eventWrapper.newInstance(spongeEvent));
							} catch (Exception ex) {
								ex.printStackTrace();
							}
						}
					}

				});
	}

}
