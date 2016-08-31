package com.kmecpp.osmium.api.plugin;

import ninja.leaping.configurate.commented.CommentedConfigurationNode;

public abstract class ConfigurationSpec {

	public abstract String getHeader();

	public abstract void populate(IConfigSpec root);

	@FunctionalInterface
	public static interface IConfigSpec {

		CommentedConfigurationNode setDefault(Object... path);

	}

}
