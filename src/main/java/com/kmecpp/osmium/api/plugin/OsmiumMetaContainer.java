package com.kmecpp.osmium.api.plugin;

public class OsmiumMetaContainer {

	private final String sourceClass;

	private final String name;
	private final String version;
	private final String description;
	private final String url;
	private final String[] authors;
	private final String[] dependencies;

	public OsmiumMetaContainer(String sourceClass, String name, String version, String description, String url, String[] authors, String[] dependencies) {
		this.sourceClass = sourceClass;

		this.name = name;
		this.version = version;
		this.description = description;
		this.url = url;
		this.authors = authors;

		this.dependencies = new String[dependencies.length + 1];
		this.dependencies[0] = "Osmium";
		System.arraycopy(dependencies, 0, this.dependencies, 1, dependencies.length);
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

	public String[] getDependencies() {
		return dependencies;
	}

}
