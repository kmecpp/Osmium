package com.kmecpp.osmium.api.plugin;

import com.kmecpp.osmium.AppInfo;

public class OsmiumMetaContainer {

	private final String sourceClass;

	private final String name;
	private final String version;
	private final String description;
	private final String url;
	private final String[] authors;
	private final String[] bukkitDependencies;
	private final String[] spongeDependencies;

	public OsmiumMetaContainer(String sourceClass, String name, String version, String description, String url, String[] authors, String[] dependencies) {
		this.sourceClass = sourceClass;

		this.name = name;
		this.version = version;
		this.description = description;
		this.url = url;
		this.authors = authors;
		
		this.bukkitDependencies = new String[dependencies.length + 1];
		this.bukkitDependencies[0] = "Osmium";
		System.arraycopy(dependencies, 0, this.bukkitDependencies, 1, dependencies.length);
		
		this.spongeDependencies = new String[dependencies.length + 2];
		this.spongeDependencies[0] = "spongeapi@" + AppInfo.SPONGE_VERSION;
		this.spongeDependencies[1] = "osmium";
		System.arraycopy(dependencies, 0, this.spongeDependencies, 2, dependencies.length);
	}

	public String getSourceClass() {
		return sourceClass;
	}

	public String getName() {
		return name;
	}

	public String getVersion() {
		return version;
	}

	public String getDescription() {
		return description;
	}

	public String getUrl() {
		return url;
	}

	public String[] getAuthors() {
		return authors;
	}

	public String[] getBukkitDependencies() {
		return bukkitDependencies;
	}

	public String[] getSpongeDependencies() {
		return spongeDependencies;
	}

}
