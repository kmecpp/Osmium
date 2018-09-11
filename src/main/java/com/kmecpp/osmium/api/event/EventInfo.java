package com.kmecpp.osmium.api.event;

import java.lang.reflect.Field;
import java.util.HashMap;

import com.kmecpp.osmium.api.event.events.PlayerJoinEvent;
import com.kmecpp.osmium.api.event.events.PlayerMoveEvent;
import com.kmecpp.osmium.api.event.events.PlayerQuitEvent;
import com.kmecpp.osmium.api.event.events.ServerListPingEvent;
import com.kmecpp.osmium.api.platform.Platform;
import com.kmecpp.osmium.api.util.Reflection;
import com.kmecpp.osmium.platform.bukkit.event.events.BukkitPlayerJoinEvent;
import com.kmecpp.osmium.platform.bukkit.event.events.BukkitPlayerMoveEvent;
import com.kmecpp.osmium.platform.bukkit.event.events.BukkitPlayerQuitEvent;
import com.kmecpp.osmium.platform.bukkit.event.events.BukkitServerListPingEvent;
import com.kmecpp.osmium.platform.sponge.event.events.SpongePlayerJoinEvent;
import com.kmecpp.osmium.platform.sponge.event.events.SpongePlayerMoveEvent;
import com.kmecpp.osmium.platform.sponge.event.events.SpongePlayerQuitEvent;
import com.kmecpp.osmium.platform.sponge.event.events.SpongeServerListPingEvent;

public class EventInfo {

	private static final HashMap<Class<? extends Event>, EventInfo> EVENTS = new HashMap<>();

	private final Class<? extends Event> bukkitImplementation;
	private final Class<? extends org.bukkit.event.Event> bukkitClass;
	//	private final Predicate<? extends org.bukkit.event.Event> bukkitCondition;
	private final Class<? extends Event> spongeImplementation;
	private final Class<? extends org.spongepowered.api.event.Event> spongeClass;
	//	private final Predicate<? extends org.spongepowered.api.event.Event> spongeCondition;

	public EventInfo(Class<? extends Event> bukkitImplementation, Class<? extends org.bukkit.event.Event> bukkitClass, Class<? extends Event> spongeImplementation, Class<? extends org.spongepowered.api.event.Event> spongeClass) {
		this.bukkitImplementation = bukkitImplementation;
		this.bukkitClass = bukkitClass;
		//		this.bukkitCondition = bukkitCondition;
		this.spongeImplementation = spongeImplementation;
		this.spongeClass = spongeClass;
		//		this.spongeCondition = spongeCondition;
	}

	/*
	 * EVENT REGISTRATION
	 */
	static {
		register(PlayerMoveEvent.class, BukkitPlayerMoveEvent.class, SpongePlayerMoveEvent.class);
		register(PlayerJoinEvent.class, BukkitPlayerJoinEvent.class, SpongePlayerJoinEvent.class);
		register(PlayerQuitEvent.class, BukkitPlayerQuitEvent.class, SpongePlayerQuitEvent.class);
		register(ServerListPingEvent.class, BukkitServerListPingEvent.class, SpongeServerListPingEvent.class);
	}

	public static void register(Class<? extends Event> event, Class<? extends Event> bukkitImplementation, Class<? extends Event> spongeImplementation) {
		//Don't exact source unless needed
		Class<? extends org.bukkit.event.Event> bukkitSource = Platform.isBukkit() ? extractSourceClass(bukkitImplementation) : null;
		Class<? extends org.spongepowered.api.event.Event> spongeSource = Platform.isSponge() ? extractSourceClass(spongeImplementation) : null;

		EVENTS.put(event, new EventInfo(bukkitImplementation, bukkitSource, spongeImplementation, spongeSource));
	}

	private static <T> Class<T> extractSourceClass(Class<?> osmiumClass) {
		Field field = osmiumClass.getDeclaredFields()[0];
		field.setAccessible(true);
		return Reflection.cast(field.getType());
	}

	public static EventInfo get(Class<? extends Event> event) {
		return EVENTS.get(event);
	}

	public <T> Class<T> getOsmiumImplementation() {
		return null;
	}

	public <T> Class<T> getSourceClass() {
		return null;
	}

	public Class<? extends org.bukkit.event.Event> getBukkitClass() {
		return bukkitClass;
	}

	public Class<? extends Event> getBukkitImplementation() {
		return bukkitImplementation;
	}

	public Class<? extends org.spongepowered.api.event.Event> getSpongeClass() {
		return spongeClass;
	}

	public Class<? extends Event> getSpongeImplementation() {
		return spongeImplementation;
	}

}
