package com.kmecpp.osmium.api.hibernate;

import org.hibernate.cfg.ImprovedNamingStrategy;

@SuppressWarnings("serial")
public class PluginPrefixNamingStrategy extends ImprovedNamingStrategy {

	@Override
	public String classToTableName(String className) {
		String defaultValue = super.classToTableName(className);
		return defaultValue;
	}

}
