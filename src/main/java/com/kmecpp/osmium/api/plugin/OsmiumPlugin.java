package com.kmecpp.osmium.api.plugin;

// @Plugin(id = "Id",
// name = "Name",
// version = "Version",
// description = "Description",
// authors = { "kmecpp" },
// dependencies = { @Dependency(id = "Depend", optional = true) },
// url = "Url")
public abstract class OsmiumPlugin {

	public static final String NAME = "";

	//Effectively final variables
	private static OsmiumPlugin plugin;
	private static Initializer initializer;

	public OsmiumPlugin() {
		plugin = this;
	}

	public static OsmiumPlugin getPlugin() {
		return plugin;
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

	//INITIALIZATION
	public abstract String getPluginName();

	public abstract ConfigurationSpec getConfigurationSpec();

}
