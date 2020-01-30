package com.kmecpp.osmium.api.event;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

import com.kmecpp.osmium.Platform;
import com.kmecpp.osmium.api.event.events.BlockEvent;
import com.kmecpp.osmium.api.event.events.EntityDamageEvent;
import com.kmecpp.osmium.api.event.events.InventoryEvent;
import com.kmecpp.osmium.api.event.events.ItemDropEvent;
import com.kmecpp.osmium.api.event.events.PlayerChangedWorldEvent;
import com.kmecpp.osmium.api.event.events.PlayerChatEvent;
import com.kmecpp.osmium.api.event.events.PlayerConnectionEvent;
import com.kmecpp.osmium.api.event.events.PlayerInteractEvent;
import com.kmecpp.osmium.api.event.events.PlayerMoveEvent;
import com.kmecpp.osmium.api.event.events.PlayerTeleportEvent;
import com.kmecpp.osmium.api.event.events.ServerListPingEvent;
import com.kmecpp.osmium.api.event.events.osmium.DateChangeEvent;
import com.kmecpp.osmium.api.event.events.osmium.PlayerMovePositionEvent;
import com.kmecpp.osmium.api.event.events.osmium.PluginReloadEvent;
import com.kmecpp.osmium.api.logging.OsmiumLogger;
import com.kmecpp.osmium.api.util.Reflection;
import com.kmecpp.osmium.platform.bukkit.event.events.BukkitBlockEvent.BukkitBlockBreakEvent;
import com.kmecpp.osmium.platform.bukkit.event.events.BukkitBlockEvent.BukkitBlockPlaceEvent;
import com.kmecpp.osmium.platform.bukkit.event.events.BukkitBlockEvent.BukkitPlayerChangeBlockEvent;
import com.kmecpp.osmium.platform.bukkit.event.events.BukkitEntityDamageEvent;
import com.kmecpp.osmium.platform.bukkit.event.events.BukkitInventoryEvent.BukkitInventoryClickEvent;
import com.kmecpp.osmium.platform.bukkit.event.events.BukkitInventoryEvent.BukkitInventoryCloseEvent;
import com.kmecpp.osmium.platform.bukkit.event.events.BukkitInventoryEvent.BukkitInventoryDragEvent;
import com.kmecpp.osmium.platform.bukkit.event.events.BukkitInventoryEvent.BukkitInventoryInteractEvent;
import com.kmecpp.osmium.platform.bukkit.event.events.BukkitInventoryEvent.BukkitInventoryOpenEvent;
import com.kmecpp.osmium.platform.bukkit.event.events.BukkitItemDropEvent.BukkitItemDropPlayerEvent;
import com.kmecpp.osmium.platform.bukkit.event.events.BukkitPlayerChangedWorldEvent;
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
import com.kmecpp.osmium.platform.osmium.OsmiumDayChangeEvent;
import com.kmecpp.osmium.platform.osmium.OsmiumMonthChangeEvent;
import com.kmecpp.osmium.platform.osmium.OsmiumPlayerMovePositionEvent;
import com.kmecpp.osmium.platform.osmium.OsmiumPluginReloadEvent;
import com.kmecpp.osmium.platform.osmium.OsmiumWeekChangeEvent;
import com.kmecpp.osmium.platform.sponge.event.events.SpongeBlockEvent.SpongeBlockBreakEvent;
import com.kmecpp.osmium.platform.sponge.event.events.SpongeBlockEvent.SpongeBlockPlaceEvent;
import com.kmecpp.osmium.platform.sponge.event.events.SpongeBlockEvent.SpongePlayerChangeBlockEvent;
import com.kmecpp.osmium.platform.sponge.event.events.SpongeEntityDamageEvent;
import com.kmecpp.osmium.platform.sponge.event.events.SpongeInventoryEvent.SpongeInventoryClickEvent;
import com.kmecpp.osmium.platform.sponge.event.events.SpongeInventoryEvent.SpongeInventoryCloseEvent;
import com.kmecpp.osmium.platform.sponge.event.events.SpongeInventoryEvent.SpongeInventoryDragEvent;
import com.kmecpp.osmium.platform.sponge.event.events.SpongeInventoryEvent.SpongeInventoryInteractEvent;
import com.kmecpp.osmium.platform.sponge.event.events.SpongeInventoryEvent.SpongeInventoryOpenEvent;
import com.kmecpp.osmium.platform.sponge.event.events.SpongeItemDropEvent.SpongeItemDropPlayerEvent;
import com.kmecpp.osmium.platform.sponge.event.events.SpongePlayerChangedWorldEvent;
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

	private static final HashMap<Class<? extends Event>, EventInfo> events = new HashMap<>(); //<Interface, EventInfo>

	private final Class<? extends Event> event;
	private final Class<? extends Event> osmiumImplementation;
	private final ArrayList<Class<?>> sourceClasses;
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

	public EventInfo(Class<? extends Event> event, Class<? extends Event> implementation, ArrayList<Class<?>> sourceClasses, boolean osmiumEvent) {
		this.event = event;
		this.osmiumImplementation = implementation;
		this.sourceClasses = sourceClasses;
		this.osmiumEvent = osmiumEvent;
	}

	/*
	 * EVENT REGISTRATION
	 */
	static {
		//@formatter:off
		register(PluginReloadEvent.class,             OsmiumPluginReloadEvent.class);
		register(DateChangeEvent.Day.class,           OsmiumDayChangeEvent.class);
		register(DateChangeEvent.Week.class,          OsmiumWeekChangeEvent.class);
		register(DateChangeEvent.Month.class,         OsmiumMonthChangeEvent.class);
		register(PlayerMovePositionEvent.class,       OsmiumPlayerMovePositionEvent.class);
		
		register(EntityDamageEvent.class,             BukkitEntityDamageEvent.class,           SpongeEntityDamageEvent.class);
		register(ItemDropEvent.Player.class,          BukkitItemDropPlayerEvent.class,         SpongeItemDropPlayerEvent.class);
		
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
		register(PlayerChangedWorldEvent.class,       BukkitPlayerChangedWorldEvent.class,     SpongePlayerChangedWorldEvent.class);
		                    
		register(ServerListPingEvent.class,           BukkitServerListPingEvent.class,         SpongeServerListPingEvent.class);
		register(PlayerConnectionEvent.Auth.class,    BukkitPlayerAuthEvent.class,             SpongePlayerAuthEvent.class);
		register(PlayerConnectionEvent.Login.class,   BukkitPlayerLoginEvent.class,            SpongePlayerLoginEvent.class);
		register(PlayerConnectionEvent.Join.class,    BukkitPlayerJoinEvent.class,             SpongePlayerJoinEvent.class);
		register(PlayerConnectionEvent.Quit.class,    BukkitPlayerQuitEvent.class,             SpongePlayerQuitEvent.class);
		                                                                                       
		register(BlockEvent.Break.class,              BukkitBlockBreakEvent.class,             SpongeBlockBreakEvent.class);
		register(BlockEvent.Place.class,              BukkitBlockPlaceEvent.class,             SpongeBlockPlaceEvent.class);
		register(BlockEvent.PlayerChange.class,       BukkitPlayerChangeBlockEvent.class,      SpongePlayerChangeBlockEvent.class);
		//@formatter:on
	}

	public static HashMap<Class<? extends Event>, EventInfo> getEvents() {
		return events;
	}

	public static void register(Class<? extends Event> event, Class<? extends Event> osmiumImplementation) {
		events.put(event, new EventInfo(event, osmiumImplementation, null, true));
	}

	public static void register(Class<? extends EventAbstraction> event, Class<? extends EventAbstraction> bukkitImplementation, Class<? extends EventAbstraction> spongeImplementation) {
		//Don't exact source unless needed
		if (Platform.isBukkit()) {
			events.put(event, new EventInfo(event, bukkitImplementation, extractSourceClasses(bukkitImplementation), false));
		} else if (Platform.isSponge()) {
			events.put(event, new EventInfo(event, spongeImplementation, extractSourceClasses(spongeImplementation), false));
		}
		//		EVENTS.put(event, new EventInfo(bukkitImplementation, bukkitSource, spongeImplementation, spongeSource, false));

	}

	private static ArrayList<Class<?>> extractSourceClasses(Class<? extends EventAbstraction> osmiumClass) {
		//		Field field;
		//		if (BukkitEvent.class.isAssignableFrom(osmiumClass)) {
		//			field = osmiumClass.getSuperclass().getDeclaredFields()[0];
		//		} else {
		//			field = osmiumClass.getDeclaredFields()[0];
		//		}
		ArrayList<Class<?>> result = new ArrayList<>();
		try {
			//			return Reflection.cast();
			Field[] fields = osmiumClass.getDeclaredFields();
			for (Field field : fields) {
				if ((Platform.isBukkit() && org.bukkit.event.Event.class.isAssignableFrom(field.getType()))
						|| (Platform.isSponge() && org.spongepowered.api.event.Event.class.isAssignableFrom(field.getType()))) {
					result.add(field.getType());
				}
			}
			if (result.isEmpty()) {
				result.add(osmiumClass.getDeclaredConstructors()[0].getParameterTypes()[0]);
			}
			return result;
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

	public Class<? extends Event> getEvent() {
		return event;
	}

	public String getEventName() {
		//TODO: Define this recursively for arbitrarily many enclosing classes
		return event.getEnclosingClass() == null ? event.getSimpleName()
				: event.getEnclosingClass().getSimpleName() + "." + event.getSimpleName();
	}

	@SuppressWarnings("unchecked")
	public <T extends EventAbstraction> Class<T> getOsmiumImplementation() {
		return (Class<T>) osmiumImplementation;
	}

	public <T> ArrayList<Class<T>> getSourceClasses() {
		return Reflection.cast(sourceClasses);
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
