package com.kmecpp.osmium.api.event;

import java.lang.reflect.Field;
import java.util.HashMap;

import com.kmecpp.osmium.api.event.events.BlockEvent;
import com.kmecpp.osmium.api.event.events.PlayerConnectionEvent;
import com.kmecpp.osmium.api.event.events.PlayerMoveEvent;
import com.kmecpp.osmium.api.event.events.PlayerMovePositionEvent;
import com.kmecpp.osmium.api.event.events.PlayerTeleportEvent;
import com.kmecpp.osmium.api.event.events.ServerListPingEvent;
import com.kmecpp.osmium.api.logging.OsmiumLogger;
import com.kmecpp.osmium.api.platform.Platform;
import com.kmecpp.osmium.api.util.Reflection;
import com.kmecpp.osmium.platform.bukkit.event.events.BukkitBlockEvent.BukkitBlockBreakEvent;
import com.kmecpp.osmium.platform.bukkit.event.events.BukkitPlayerConnectEvent.BukkitPlayerAuthEvent;
import com.kmecpp.osmium.platform.bukkit.event.events.BukkitPlayerConnectEvent.BukkitPlayerJoinEvent;
import com.kmecpp.osmium.platform.bukkit.event.events.BukkitPlayerConnectEvent.BukkitPlayerLoginEvent;
import com.kmecpp.osmium.platform.bukkit.event.events.BukkitPlayerConnectEvent.BukkitPlayerQuitEvent;
import com.kmecpp.osmium.platform.bukkit.event.events.BukkitPlayerMoveEvent;
import com.kmecpp.osmium.platform.bukkit.event.events.BukkitPlayerTeleportEvent;
import com.kmecpp.osmium.platform.bukkit.event.events.BukkitServerListPingEvent;
import com.kmecpp.osmium.platform.osmium.OsmiumPlayerMovePositionEvent;
import com.kmecpp.osmium.platform.sponge.event.events.SpongeBlockEvent.SpongeBlockBreakEvent;
import com.kmecpp.osmium.platform.sponge.event.events.SpongePlayerConnectEvent.SpongePlayerAuthEvent;
import com.kmecpp.osmium.platform.sponge.event.events.SpongePlayerConnectEvent.SpongePlayerJoinEvent;
import com.kmecpp.osmium.platform.sponge.event.events.SpongePlayerConnectEvent.SpongePlayerLoginEvent;
import com.kmecpp.osmium.platform.sponge.event.events.SpongePlayerConnectEvent.SpongePlayerQuitEvent;
import com.kmecpp.osmium.platform.sponge.event.events.SpongePlayerMoveEvent;
import com.kmecpp.osmium.platform.sponge.event.events.SpongePlayerTeleportEvent;
import com.kmecpp.osmium.platform.sponge.event.events.SpongeServerListPingEvent;

public class EventInfo {

	private static final HashMap<Class<? extends Event>, EventInfo> events = new HashMap<>(); //<Interface, EventInfo>

	private final Class<? extends Event> event;
	private final Class<? extends Event> implementation;
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

	public EventInfo(Class<? extends Event> event, Class<? extends Event> implementation, Class<?> source, boolean osmiumEvent) {
		this.event = event;
		this.implementation = implementation;
		this.source = source;
		this.osmiumEvent = osmiumEvent;
	}

	/*
	 * EVENT REGISTRATION
	 */
	static {
		//@formatter:off
		register(PlayerMovePositionEvent.class,     OsmiumPlayerMovePositionEvent.class);

		register(PlayerMoveEvent.class,             BukkitPlayerMoveEvent.class,     SpongePlayerMoveEvent.class);
		register(PlayerTeleportEvent.class,         BukkitPlayerTeleportEvent.class, SpongePlayerTeleportEvent.class);
		register(PlayerConnectionEvent.Auth.class,  BukkitPlayerAuthEvent.class,     SpongePlayerAuthEvent.class);
		register(PlayerConnectionEvent.Login.class, BukkitPlayerLoginEvent.class,    SpongePlayerLoginEvent.class);
		register(PlayerConnectionEvent.Join.class,  BukkitPlayerJoinEvent.class,     SpongePlayerJoinEvent.class);
		register(PlayerConnectionEvent.Quit.class,  BukkitPlayerQuitEvent.class,     SpongePlayerQuitEvent.class);
		register(ServerListPingEvent.class,         BukkitServerListPingEvent.class, SpongeServerListPingEvent.class);
		register(BlockEvent.Break.class,            BukkitBlockBreakEvent.class,     SpongeBlockBreakEvent.class);
		//@formatter:on
	}

	public static void register(Class<? extends Event> event, Class<? extends Event> osmiumImplementation) {
		events.put(event, new EventInfo(event, osmiumImplementation, null, true));
	}

	public static void register(Class<? extends Event> event, Class<? extends Event> bukkitImplementation, Class<? extends Event> spongeImplementation) {
		//Don't exact source unless needed
		if (Platform.isBukkit()) {
			events.put(event, new EventInfo(event, bukkitImplementation, extractSourceClass(bukkitImplementation), false));
		} else if (Platform.isSponge()) {
			events.put(event, new EventInfo(event, spongeImplementation, extractSourceClass(spongeImplementation), false));
		}
		//		EVENTS.put(event, new EventInfo(bukkitImplementation, bukkitSource, spongeImplementation, spongeSource, false));

	}

	private static <T> Class<T> extractSourceClass(Class<? extends Event> osmiumClass) {
		//		Field field;
		//		if (BukkitEvent.class.isAssignableFrom(osmiumClass)) {
		//			field = osmiumClass.getSuperclass().getDeclaredFields()[0];
		//		} else {
		//			field = osmiumClass.getDeclaredFields()[0];
		//		}
		try {
			Field field = osmiumClass.getDeclaredFields()[0];
			field.setAccessible(true);
			return Reflection.cast(field.getType());
		} catch (Exception e) {
			OsmiumLogger.error("Failed to extract source class for " + osmiumClass.getName());
			throw new RuntimeException(e);
		}
	}

	public static EventInfo get(Class<? extends Event> event) {
		return events.get(event);
	}

	public boolean isOsmiumEvent() {
		return osmiumEvent;
	}

	public Class<? extends Event> getEvent() {
		return event;
	}

	@SuppressWarnings("unchecked")
	public <T extends Event> Class<T> getImplementation() {
		return (Class<T>) implementation;
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
