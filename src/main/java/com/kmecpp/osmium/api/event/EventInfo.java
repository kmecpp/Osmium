package com.kmecpp.osmium.api.event;

import java.util.HashMap;

import com.kmecpp.osmium.api.event.events.BlockEvent;
import com.kmecpp.osmium.api.event.events.InventoryEvent;
import com.kmecpp.osmium.api.event.events.PlayerChatEvent;
import com.kmecpp.osmium.api.event.events.PlayerConnectionEvent;
import com.kmecpp.osmium.api.event.events.PlayerInteractEvent;
import com.kmecpp.osmium.api.event.events.PlayerMoveEvent;
import com.kmecpp.osmium.api.event.events.PlayerMovePositionEvent;
import com.kmecpp.osmium.api.event.events.PlayerTeleportEvent;
import com.kmecpp.osmium.api.event.events.ServerListPingEvent;
import com.kmecpp.osmium.api.logging.OsmiumLogger;
import com.kmecpp.osmium.api.platform.Platform;
import com.kmecpp.osmium.api.util.Reflection;
import com.kmecpp.osmium.platform.bukkit.event.events.BukkitBlockEvent.BukkitBlockBreakEvent;
import com.kmecpp.osmium.platform.bukkit.event.events.BukkitInventoryEvent.BukkitInventoryClickEvent;
import com.kmecpp.osmium.platform.bukkit.event.events.BukkitInventoryEvent.BukkitInventoryCloseEvent;
import com.kmecpp.osmium.platform.bukkit.event.events.BukkitInventoryEvent.BukkitInventoryDragEvent;
import com.kmecpp.osmium.platform.bukkit.event.events.BukkitInventoryEvent.BukkitInventoryInteractEvent;
import com.kmecpp.osmium.platform.bukkit.event.events.BukkitInventoryEvent.BukkitInventoryOpenEvent;
import com.kmecpp.osmium.platform.bukkit.event.events.BukkitPlayerChatEvent;
import com.kmecpp.osmium.platform.bukkit.event.events.BukkitPlayerConnectionEvent.BukkitPlayerAuthEvent;
import com.kmecpp.osmium.platform.bukkit.event.events.BukkitPlayerConnectionEvent.BukkitPlayerJoinEvent;
import com.kmecpp.osmium.platform.bukkit.event.events.BukkitPlayerConnectionEvent.BukkitPlayerLoginEvent;
import com.kmecpp.osmium.platform.bukkit.event.events.BukkitPlayerConnectionEvent.BukkitPlayerQuitEvent;
import com.kmecpp.osmium.platform.bukkit.event.events.BukkitPlayerInteractEvent.BukkitPlayerInteractBlockEvent;
import com.kmecpp.osmium.platform.bukkit.event.events.BukkitPlayerInteractEvent.BukkitPlayerInteractEntityEvent;
import com.kmecpp.osmium.platform.bukkit.event.events.BukkitPlayerInteractEvent.BukkitPlayerInteractItemEvent;
import com.kmecpp.osmium.platform.bukkit.event.events.BukkitPlayerInteractEvent.BukkitPlayerInteractPhysicalEvent;
import com.kmecpp.osmium.platform.bukkit.event.events.BukkitPlayerMoveEvent;
import com.kmecpp.osmium.platform.bukkit.event.events.BukkitPlayerTeleportEvent;
import com.kmecpp.osmium.platform.bukkit.event.events.BukkitServerListPingEvent;
import com.kmecpp.osmium.platform.osmium.OsmiumPlayerMovePositionEvent;
import com.kmecpp.osmium.platform.sponge.event.events.SpongeBlockEvent.SpongeBlockBreakEvent;
import com.kmecpp.osmium.platform.sponge.event.events.SpongeInventoryEvent.SpongeInventoryClickEvent;
import com.kmecpp.osmium.platform.sponge.event.events.SpongeInventoryEvent.SpongeInventoryCloseEvent;
import com.kmecpp.osmium.platform.sponge.event.events.SpongeInventoryEvent.SpongeInventoryDragEvent;
import com.kmecpp.osmium.platform.sponge.event.events.SpongeInventoryEvent.SpongeInventoryInteractEvent;
import com.kmecpp.osmium.platform.sponge.event.events.SpongeInventoryEvent.SpongeInventoryOpenEvent;
import com.kmecpp.osmium.platform.sponge.event.events.SpongePlayerChatEvent;
import com.kmecpp.osmium.platform.sponge.event.events.SpongePlayerConnectEvent.SpongePlayerAuthEvent;
import com.kmecpp.osmium.platform.sponge.event.events.SpongePlayerConnectEvent.SpongePlayerJoinEvent;
import com.kmecpp.osmium.platform.sponge.event.events.SpongePlayerConnectEvent.SpongePlayerLoginEvent;
import com.kmecpp.osmium.platform.sponge.event.events.SpongePlayerConnectEvent.SpongePlayerQuitEvent;
import com.kmecpp.osmium.platform.sponge.event.events.SpongePlayerInteractEvent.SpongePlayerInteractBlockEvent;
import com.kmecpp.osmium.platform.sponge.event.events.SpongePlayerInteractEvent.SpongePlayerInteractEntityEvent;
import com.kmecpp.osmium.platform.sponge.event.events.SpongePlayerInteractEvent.SpongePlayerInteractItemEvent;
import com.kmecpp.osmium.platform.sponge.event.events.SpongePlayerInteractEvent.SpongePlayerInteractPhysicalEvent;
import com.kmecpp.osmium.platform.sponge.event.events.SpongePlayerMoveEvent;
import com.kmecpp.osmium.platform.sponge.event.events.SpongePlayerTeleportEvent;
import com.kmecpp.osmium.platform.sponge.event.events.SpongeServerListPingEvent;

public class EventInfo {

	private static final HashMap<Class<? extends EventAbstraction>, EventInfo> events = new HashMap<>(); //<Interface, EventInfo>

	private final Class<? extends EventAbstraction> event;
	private final Class<? extends EventAbstraction> osmiumImplementation;
	private final Class<?> source;
	private final boolean osmiumEvent;

	//	private final Class<? extends Event> bukkitImplementation;
	//	private final Class<? extends org.bukkit.event.Event> bukkitClass;
	//	private final Class<? extends Event> spongeImplementation;
	//	private final Class<? extends org.spongepowered.api.event.Event> spongeClass;

	//	public EventInfo(Class<? extends Event> implementation) {
	//		this(null, null, null, null, true);
	//	}
	//
	//	public EventInfo(Class<? extends Event> bukkitImplementation, Class<? extends org.bukkit.event.Event> bukkitClass, Class<? extends Event> spongeImplementation, Class<? extends org.spongepowered.api.event.Event> spongeClass, boolean osmiumEvent) {
	//		this.bukkitImplementation = bukkitImplementation;
	//		this.bukkitClass = bukkitClass;
	//		this.spongeImplementation = spongeImplementation;
	//		this.spongeClass = spongeClass;
	//		this.osmiumEvent = osmiumEvent;
	//	}

	public EventInfo(Class<? extends EventAbstraction> event, Class<? extends EventAbstraction> implementation, Class<?> source, boolean osmiumEvent) {
		this.event = event;
		this.osmiumImplementation = implementation;
		this.source = source;
		this.osmiumEvent = osmiumEvent;
	}

	/*
	 * EVENT REGISTRATION
	 */
	static {
		//@formatter:off
		register(PlayerMovePositionEvent.class,     OsmiumPlayerMovePositionEvent.class);

		register(InventoryEvent.Open.class,           BukkitInventoryOpenEvent.class,          SpongeInventoryOpenEvent.class);
		register(InventoryEvent.Close.class,          BukkitInventoryCloseEvent.class,         SpongeInventoryCloseEvent.class);
		register(InventoryEvent.Click.class,          BukkitInventoryClickEvent.class,         SpongeInventoryClickEvent.class);
		register(InventoryEvent.Drag.class,           BukkitInventoryDragEvent.class,          SpongeInventoryDragEvent.class);
		register(InventoryEvent.Interact.class,       BukkitInventoryInteractEvent.class,      SpongeInventoryInteractEvent.class);
		                                                                                       
		register(PlayerInteractEvent.Item.class,      BukkitPlayerInteractItemEvent.class,     SpongePlayerInteractItemEvent.class);
		register(PlayerInteractEvent.Block.class,     BukkitPlayerInteractBlockEvent.class,    SpongePlayerInteractBlockEvent.class);
		register(PlayerInteractEvent.Entity.class,    BukkitPlayerInteractEntityEvent.class,   SpongePlayerInteractEntityEvent.class);
		register(PlayerInteractEvent.Physical.class,  BukkitPlayerInteractPhysicalEvent.class, SpongePlayerInteractPhysicalEvent.class);
                                                                                       
		register(PlayerChatEvent.class,               BukkitPlayerChatEvent.class,             SpongePlayerChatEvent.class);
		register(PlayerMoveEvent.class,               BukkitPlayerMoveEvent.class,             SpongePlayerMoveEvent.class);
		register(PlayerTeleportEvent.class,           BukkitPlayerTeleportEvent.class,         SpongePlayerTeleportEvent.class);
		                                                                                       
		register(PlayerConnectionEvent.Auth.class,    BukkitPlayerAuthEvent.class,             SpongePlayerAuthEvent.class);
		register(PlayerConnectionEvent.Login.class,   BukkitPlayerLoginEvent.class,            SpongePlayerLoginEvent.class);
		register(PlayerConnectionEvent.Join.class,    BukkitPlayerJoinEvent.class,             SpongePlayerJoinEvent.class);
		register(PlayerConnectionEvent.Quit.class,    BukkitPlayerQuitEvent.class,             SpongePlayerQuitEvent.class);
		                                                                                       
		register(ServerListPingEvent.class,           BukkitServerListPingEvent.class,         SpongeServerListPingEvent.class);
		register(BlockEvent.Break.class,              BukkitBlockBreakEvent.class,             SpongeBlockBreakEvent.class);
		//@formatter:on
	}

	public static HashMap<Class<? extends EventAbstraction>, EventInfo> getEvents() {
		return events;
	}

	public static void register(Class<? extends EventAbstraction> event, Class<? extends EventAbstraction> osmiumImplementation) {
		events.put(event, new EventInfo(event, osmiumImplementation, null, true));
	}

	public static void register(Class<? extends EventAbstraction> event, Class<? extends EventAbstraction> bukkitImplementation, Class<? extends EventAbstraction> spongeImplementation) {
		//Don't exact source unless needed
		if (Platform.isBukkit()) {
			events.put(event, new EventInfo(event, bukkitImplementation, extractSourceClass(bukkitImplementation), false));
		} else if (Platform.isSponge()) {
			events.put(event, new EventInfo(event, spongeImplementation, extractSourceClass(spongeImplementation), false));
		}
		//		EVENTS.put(event, new EventInfo(bukkitImplementation, bukkitSource, spongeImplementation, spongeSource, false));

	}

	private static <T> Class<T> extractSourceClass(Class<? extends EventAbstraction> osmiumClass) {
		//		Field field;
		//		if (BukkitEvent.class.isAssignableFrom(osmiumClass)) {
		//			field = osmiumClass.getSuperclass().getDeclaredFields()[0];
		//		} else {
		//			field = osmiumClass.getDeclaredFields()[0];
		//		}
		try {
			//			Field field = osmiumClass.getDeclaredFields()[0];
			//			field.setAccessible(true);
			//			return Reflection.cast(field.getType());
			return Reflection.cast(osmiumClass.getDeclaredConstructors()[0].getParameterTypes()[0]);
		} catch (Exception e) {
			OsmiumLogger.error("Failed to extract source class for " + osmiumClass.getName());
			throw new RuntimeException(e);
		}
	}

	public static EventInfo get(Class<? extends EventAbstraction> event) {
		return events.get(event);
	}

	public boolean isOsmiumEvent() {
		return osmiumEvent;
	}

	public Class<? extends EventAbstraction> getEvent() {
		return event;
	}

	@SuppressWarnings("unchecked")
	public <T extends EventAbstraction> Class<T> getOsmiumImplementation() {
		return (Class<T>) osmiumImplementation;
	}

	@SuppressWarnings("unchecked")
	public <T> Class<T> getSource() {
		return (Class<T>) source;
	}

	//	public Class<? extends org.bukkit.event.Event> getBukkitClass() {
	//		return bukkitClass;
	//	}
	//
	//	public Class<? extends Event> getBukkitImplementation() {
	//		return bukkitImplementation;
	//	}
	//
	//	public Class<? extends org.spongepowered.api.event.Event> getSpongeClass() {
	//		return spongeClass;
	//	}
	//
	//	public Class<? extends Event> getSpongeImplementation() {
	//		return spongeImplementation;
	//	}

}
