package com.kmecpp.osmium.api.config;

import com.kmecpp.osmium.api.config.ConfigFormatWriter.ConfigFormat;

public class ConfigFormats {

	public static final ConfigFormat HOCON = ConfigFormat.builder()
			.setTab("\t")
			.setComment("#")
			.setBlockOpen(" {")
			.setBlockClose("}")
			.setListOpen("[")
			.setListClose("]")
			.setListElementSuffix(",")
			.build();

	public static final ConfigFormat YAML = ConfigFormat.builder()
			.setTab("    ")
			.setComment("#")
			.setBlockOpen(":")
			.setListElementPrefix("- ")
			.build();

}
