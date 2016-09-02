package com.kmecpp.osmium.api.plugin;

import com.kmecpp.jlib.Validate;

public abstract class OsmiumPlugin {

	public static final String NAME = "";

	//Effectively final variables
	private static OsmiumPlugin plugin;
	private static OsmiumMeta meta;
	private static Initializer initializer;

	public OsmiumPlugin() {
		plugin = this;
		meta = this.getClass().getAnnotation(OsmiumMeta.class);
		Validate.notNull(meta, "Osmium plugins must be annotated with @OsmiumMeta");
	}

	public static OsmiumPlugin getPlugin() {
		return plugin;
	}

	public static final String getName() {
		return meta.name();
	}

	public static final String getVersion() {
		return meta.version();
	}

	public static final String getDescription() {
		return meta.description();
	}

	public static final String getUrl() {
		return meta.url();
	}

	public static final String[] getAuthors() {
		return meta.authors();
	}

	public static final String[] getDependencies() {
		return meta.dependencies();
	}

	public void preInit() {
		initializer.preInit();
	}

	public void init() {
		initializer.init();
	}

	public void postInit() {
		initializer.postInit();
	}

	public final void setInitializer(Initializer initializer) {
		OsmiumPlugin.initializer = initializer;
	}

	public static final Initializer getInitializer() {
		return initializer;
	}

	//INITIALIZATION

	//	public abstract ConfigurationSpec getConfigurationSpec();

}
