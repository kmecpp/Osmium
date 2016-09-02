package com.kmecpp.osmium.api.plugin;

public class OsmiumMetaContainer {

	private String name;
	private String version;
	private String description;
	private String url;
	private String[] authors;
	private String[] dependencies;

	public OsmiumMetaContainer(Class<?> source, String name, String version, String description, String url, String[] authors, String[] dependencies) {
		this.name = name;
		this.version = version;
		this.description = description;
		this.url = url;
		this.authors = authors;
		this.dependencies = dependencies;
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
