package com.kmecpp.osmium.api.entity;

import org.spongepowered.api.entity.EntityTypes;

import com.kmecpp.osmium.api.platform.Platform;

public enum EntityType {

	//	DROPPED_ITEM("item", false),
	//	EXPERIENCE_ORB("experience_orb", 2),
	//	AREA_EFFECT_CLOUD("area_effect_cloud", 3),
	//	ELDER_GUARDIAN("elder_guardian", ElderGuardian.class, 4),
	//	WITHER_SKELETON("wither_skeleton", WitherSkeleton.class, 5),
	//	STRAY("stray", Stray.class, 6),
	//	EGG("egg", Egg.class, 7),
	//	LEASH_HITCH("leash_knot", LeashHitch.class, 8),
	//	PAINTING("painting", Painting.class, 9),
	//	ARROW("arrow", Arrow.class, 10),
	//	SNOWBALL("snowball", Snowball.class, 11),
	//	FIREBALL("fireball", LargeFireball.class, 12),
	//	SMALL_FIREBALL("small_fireball", SmallFireball.class, 13),
	//	ENDER_PEARL("ender_pearl", EnderPearl.class, 14),
	//	ENDER_SIGNAL("eye_of_ender", EnderSignal.class, 15),
	//	SPLASH_POTION("potion", SplashPotion.class, 16, false),
	//	THROWN_EXP_BOTTLE("experience_bottle", ThrownExpBottle.class, 17),
	//	ITEM_FRAME("item_frame", ItemFrame.class, 18),
	//	WITHER_SKULL("wither_skull", WitherSkull.class, 19),
	//	PRIMED_TNT("tnt", TNTPrimed.class, 20),
	//	FALLING_BLOCK("falling_block", FallingBlock.class, 21, false),
	//	FIREWORK("firework_rocket", Firework.class, 22, false),
	//	HUSK("husk", Husk.class, 23),
	//	SPECTRAL_ARROW("spectral_arrow", SpectralArrow.class, 24),
	//	SHULKER_BULLET("shulker_bullet", ShulkerBullet.class, 25),
	//	DRAGON_FIREBALL("dragon_fireball", DragonFireball.class, 26),
	//	ZOMBIE_VILLAGER("zombie_villager", ZombieVillager.class, 27),
	//	SKELETON_HORSE("skeleton_horse", SkeletonHorse.class, 28),
	//	ZOMBIE_HORSE("zombie_horse", ZombieHorse.class, 29),
	//	ARMOR_STAND("armor_stand", ArmorStand.class, 30),
	//	DONKEY("donkey", Donkey.class, 31),
	//	MULE("mule", Mule.class, 32),
	//	EVOKER_FANGS("evoker_fangs", EvokerFangs.class, 33),
	//	EVOKER("evoker", Evoker.class, 34),
	//	VEX("vex", Vex.class, 35),
	//	VINDICATOR("vindicator", Vindicator.class, 36),
	//	ILLUSIONER("illusioner", Illusioner.class, 37),
	//	MINECART_COMMAND("command_block_minecart", CommandMinecart.class, 40),
	//	BOAT("boat", Boat.class, 41),
	//	MINECART("minecart", RideableMinecart.class, 42),
	//	MINECART_CHEST("chest_minecart", StorageMinecart.class, 43),
	//	MINECART_FURNACE("furnace_minecart", PoweredMinecart.class, 44),
	//	MINECART_TNT("tnt_minecart", ExplosiveMinecart.class, 45),
	//	MINECART_HOPPER("hopper_minecart", HopperMinecart.class, 46),
	//	MINECART_MOB_SPAWNER("spawner_minecart", SpawnerMinecart.class, 47),
	//	CREEPER("creeper", Creeper.class, 50),
	//	SKELETON("skeleton", Skeleton.class, 51),
	//	SPIDER("spider", Spider.class, 52),
	//	GIANT("giant", Giant.class, 53),
	//	ZOMBIE("zombie", Zombie.class, 54),
	//	SLIME("slime", Slime.class, 55),
	//	GHAST("ghast", Ghast.class, 56),
	//	PIG_ZOMBIE("zombie_pigman", PigZombie.class, 57),
	//	ENDERMAN("enderman", Enderman.class, 58),
	//	CAVE_SPIDER("cave_spider", CaveSpider.class, 59),
	//	SILVERFISH("silverfish", Silverfish.class, 60),
	//	BLAZE("blaze", Blaze.class, 61),
	//	MAGMA_CUBE("magma_cube", MagmaCube.class, 62),
	//	ENDER_DRAGON("ender_dragon", EnderDragon.class, 63),
	//	WITHER("wither", Wither.class, 64),
	//	BAT("bat", Bat.class, 65),
	//	WITCH("witch", Witch.class, 66),
	//	ENDERMITE("endermite", Endermite.class, 67),
	//	GUARDIAN("guardian", Guardian.class, 68),
	//	SHULKER("shulker", Shulker.class, 69),
	//	PIG("pig", Pig.class, 90),
	//	SHEEP("sheep", Sheep.class, 91),
	//	COW("cow", Cow.class, 92),
	//	CHICKEN("chicken", Chicken.class, 93),
	//	SQUID("squid", Squid.class, 94),
	//	WOLF("wolf", Wolf.class, 95),
	//	MUSHROOM_COW("mooshroom", MushroomCow.class, 96),
	//	SNOWMAN("snow_golem", Snowman.class, 97),
	//	OCELOT("ocelot", Ocelot.class, 98),
	//	IRON_GOLEM("iron_golem", IronGolem.class, 99),
	//	HORSE("horse", Horse.class, 100),
	//	RABBIT("rabbit", Rabbit.class, 101),
	//	POLAR_BEAR("polar_bear", PolarBear.class, 102),
	//	LLAMA("llama", Llama.class, 103),
	//	LLAMA_SPIT("llama_spit", LlamaSpit.class, 104),
	//	PARROT("parrot", Parrot.class, 105),
	//	VILLAGER("villager", Villager.class, 120),
	//	ENDER_CRYSTAL("end_crystal", EnderCrystal.class, 200),
	//	TURTLE("turtle", Turtle.class, -1),
	//	PHANTOM("phantom", Phantom.class, -1),
	//	TRIDENT("trident", Trident.class, -1),
	//	COD("cod", Cod.class, -1),
	//	SALMON("salmon", Salmon.class, -1),
	//	PUFFERFISH("pufferfish", PufferFish.class, -1),
	//	TROPICAL_FISH("tropical_fish", TropicalFish.class, -1),
	//	DROWNED("drowned", Drowned.class, -1),
	//	DOLPHIN("dolphin", Dolphin.class, -1),
	//	LINGERING_POTION(null, LingeringPotion.class, -1, false),
	//	FISHING_HOOK("fishing_bobber", FishHook.class, -1, false),
	//	LIGHTNING("lightning_bolt", LightningStrike.class, -1, false),
	//	WEATHER(null, Weather.class, -1, false),
	//	PLAYER("player", Player.class, -1, false),
	//	COMPLEX_PART(null, ComplexEntityPart.class, -1, false),
	//	TIPPED_ARROW(null, TippedArrow.class, -1),
	//	UNKNOWN(null, null, -1, false);

	VILLAGER

	;

	private Object bukkitSource;
	private Object spongeSource;

	static {
		//@formatter:off
		set(VILLAGER,      org.bukkit.entity.EntityType.VILLAGER,      EntityTypes.VILLAGER);
		//@formatter:on
	}

	private static void set(EntityType type, Object bukkitSource, Object spongeSource) {
		type.bukkitSource = bukkitSource;
		type.spongeSource = spongeSource;
	}

	@SuppressWarnings("unchecked")
	public <T> T getImplementation() {
		if (Platform.isBukkit()) {
			return (T) bukkitSource;
		} else if (Platform.isSponge()) {
			return (T) spongeSource;
		}
		return null;
	}

	//	private final String name;
	//
	//	private EntityType(String name) {
	//		this.name = name;
	//	}
	//
	//	public String getName() {
	//		return name;
	//	}

}
