package com.kmecpp.osmium;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Optional;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandMapping;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.source.CommandBlockSource;
import org.spongepowered.api.command.source.ConsoleSource;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.event.EventListener;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.text.Text;

import com.kmecpp.osmium.api.World;
import com.kmecpp.osmium.api.command.Command;
import com.kmecpp.osmium.api.command.CommandManager;
import com.kmecpp.osmium.api.command.CommandSender;
import com.kmecpp.osmium.api.entity.Player;
import com.kmecpp.osmium.api.event.EventInfo;
import com.kmecpp.osmium.api.event.Order;
import com.kmecpp.osmium.api.plugin.OsmiumPlugin;
import com.kmecpp.osmium.cache.PlayerList;
import com.kmecpp.osmium.cache.WorldList;
import com.kmecpp.osmium.platform.sponge.GenericSpongeCommandSender;
import com.kmecpp.osmium.platform.sponge.SpongeBlockCommandSender;
import com.kmecpp.osmium.platform.sponge.SpongeConsoleCommandSender;

public class SpongeAccess {

	public static Text getText(String str) {
		return Text.of(str);
	}

	public static Player getPlayer(org.spongepowered.api.entity.living.player.Player player) {
		return PlayerList.getPlayer(player.getName());
	}

	public static World getWorld(org.spongepowered.api.world.World world) {
		return WorldList.getWorld(world.getName());
	}

	public static Location getLocation(org.spongepowered.api.world.Location<org.spongepowered.api.world.World> location) {
		return new Location(getWorld(location.getExtent()), location.getX(), location.getY(), location.getZ());
	}

	public static void processConsoleCommand(String command) {
		Sponge.getCommandManager().process(Sponge.getServer().getConsole(), command);
	}

	public static void processCommand(org.spongepowered.api.command.CommandSource sender, String command) {
		Sponge.getCommandManager().process(sender, command);
	}

	public static void registerCommand(OsmiumPlugin plugin, Command command) {
		CommandSpec spec = CommandSpec.builder()
				.description(SpongeAccess.getText(command.getDescription()))
				.permission(command.getPermission())
				.arguments(GenericArguments.remainingRawJoinedStrings(SpongeAccess.getText("args")))
				.executor((src, context) -> {
					CommandSender sender = src instanceof org.spongepowered.api.entity.living.player.Player ? getPlayer((org.spongepowered.api.entity.living.player.Player) src)
							: src instanceof ConsoleSource ? new SpongeConsoleCommandSender((ConsoleSource) src)
									: src instanceof CommandBlockSource ? new SpongeBlockCommandSender((CommandBlockSource) src)
											: new GenericSpongeCommandSender(src);

					String[] args = context.<String> getOne("args").map((s) -> s.split(" ")).orElse(new String[0]);

					return CommandManager.invokeCommand(command, sender, command.getAliases()[0], args)
							? CommandResult.success()
							: CommandResult.empty();
				})
				.build();

		Optional<CommandMapping> optionalMapping = Sponge.getCommandManager().register(plugin.getPluginImplementation(), spec, command.getAliases());
		if (optionalMapping.isPresent()) {
			CommandMapping mapping = optionalMapping.get();
			command.setPrimaryAlias(mapping.getPrimaryAlias());
			Osmium.getCommandManager().register(plugin, command);
		} else {
			CommandManager.sendFailedRegistrationMessage(plugin, command);
		}
	}

	public static void registerListener(OsmiumPlugin plugin, EventInfo eventInfo, Order order, Method method, Object listenerInstance) throws Exception {
		Class<? extends org.spongepowered.api.event.Event> spongeEventClass = eventInfo.getSpongeClass();
		Constructor<?> eventWrapper = eventInfo.getSpongeImplementation().getConstructor(spongeEventClass);

		Sponge.getEventManager()
				.registerListener(plugin.getPluginImplementation(), spongeEventClass, (org.spongepowered.api.event.Order) order.getSource(), false,
						new EventListener<org.spongepowered.api.event.Event>() {

							@Override
							public void handle(org.spongepowered.api.event.Event spongeEvent) throws Exception {
								if (spongeEventClass.isAssignableFrom(spongeEvent.getClass())) {
									if (spongeEvent instanceof MoveEntityEvent && !(((MoveEntityEvent) spongeEvent).getTargetEntity() instanceof Player)) {
										return; //Only run MoveEntityEvent if it's a player
									}

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
