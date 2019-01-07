package com.kmecpp.osmium;

import java.lang.reflect.Method;
import java.util.Optional;
import java.util.function.Consumer;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandMapping;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.source.CommandBlockSource;
import org.spongepowered.api.command.source.ConsoleSource;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;

import com.kmecpp.osmium.api.Block;
import com.kmecpp.osmium.api.Chunk;
import com.kmecpp.osmium.api.World;
import com.kmecpp.osmium.api.command.Command;
import com.kmecpp.osmium.api.command.CommandManager;
import com.kmecpp.osmium.api.command.CommandSender;
import com.kmecpp.osmium.api.entity.Entity;
import com.kmecpp.osmium.api.entity.Player;
import com.kmecpp.osmium.api.event.EventInfo;
import com.kmecpp.osmium.api.event.Order;
import com.kmecpp.osmium.api.inventory.ItemStack;
import com.kmecpp.osmium.api.inventory.ItemType;
import com.kmecpp.osmium.api.location.Location;
import com.kmecpp.osmium.api.plugin.OsmiumPlugin;
import com.kmecpp.osmium.cache.PlayerList;
import com.kmecpp.osmium.cache.WorldList;
import com.kmecpp.osmium.platform.sponge.GenericSpongeCommandSender;
import com.kmecpp.osmium.platform.sponge.SpongeBlock;
import com.kmecpp.osmium.platform.sponge.SpongeBlockCommandSender;
import com.kmecpp.osmium.platform.sponge.SpongeChunk;
import com.kmecpp.osmium.platform.sponge.SpongeConsoleCommandSender;
import com.kmecpp.osmium.platform.sponge.SpongeEntity;
import com.kmecpp.osmium.platform.sponge.SpongeItemStack;

public class SpongeAccess {

	public static Text getText(String str) {
		return Text.of(str);
	}

	public static ItemStack getItemStack(org.spongepowered.api.item.inventory.ItemStack itemStack) {
		return new SpongeItemStack(itemStack);
	}

	public static ItemType getItemType(org.spongepowered.api.item.inventory.ItemStack itemStack) {
		return Osmium.getItemManager().getItemType(itemStack.getType().getKey().getValue());
	}

	public static Chunk getChunk(org.spongepowered.api.world.Chunk chunk) {
		return new SpongeChunk(chunk);
	}

	public static Entity getEntity(org.spongepowered.api.entity.Entity entity) {
		return new SpongeEntity(entity);
	}

	public static Player getPlayer(org.spongepowered.api.entity.living.player.Player player) {
		return PlayerList.getPlayer(player);
	}

	public static World getWorld(org.spongepowered.api.world.World world) {
		return WorldList.getWorld(world);
	}

	public static Location getLocation(org.spongepowered.api.world.Location<org.spongepowered.api.world.World> location) {
		return new Location(getWorld(location.getExtent()), location.getX(), location.getY(), location.getZ());
	}

	public static Block getBlock(org.spongepowered.api.world.Location<org.spongepowered.api.world.World> block) {
		return new SpongeBlock(block);
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

	public static void registerListener(OsmiumPlugin plugin, EventInfo eventInfo, Order order, Method method, Object listenerInstance, Consumer<Object> consumer) {
		Sponge.getEventManager().registerListener(plugin.getPluginImplementation(), eventInfo.getSource(), (org.spongepowered.api.event.Order) order.getSource(), false, (spongeEvent) -> {
			consumer.accept(spongeEvent);
		});
		//		Class<? extends org.spongepowered.api.event.Event> spongeEventClass = eventInfo.getSource();
		//		Constructor<? extends Event> eventWrapper = eventInfo.getImplementation().getConstructor(spongeEventClass);
		//
		//		Sponge.getEventManager().registerListener(plugin.getPluginImplementation(), spongeEventClass, (org.spongepowered.api.event.Order) order.getSource(), false, (spongeEvent) -> {
		//			if (spongeEventClass.isAssignableFrom(spongeEvent.getClass())) {
		//				try {
		//					Event event = eventWrapper.newInstance(spongeEvent);
		//					if (!event.shouldFire()) {
		//						return;
		//					}
		//
		//					try {
		//						method.invoke(listenerInstance, event);
		//					} catch (Exception ex) {
		//						ex.printStackTrace();
		//					}
		//				} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
		//					e.printStackTrace();
		//				}
		//			}
		//		});
	}
}
