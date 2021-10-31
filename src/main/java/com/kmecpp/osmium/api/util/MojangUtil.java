package com.kmecpp.osmium.api.util;

import java.io.IOException;
import java.net.URL;
import java.util.UUID;
import java.util.regex.Pattern;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;

public class MojangUtil {

	public static final Pattern UUID_PATTERN = Pattern.compile("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})");

	public static UUID getPlayerUUID(String name) throws IOException {
		return UUID.fromString(UUID_PATTERN
				.matcher(WebUtil.get(new URL("https://api.mojang.com/users/profiles/minecraft/" + name)).asObject().get("id").asString())
				.replaceAll("$1-$2-$3-$4-$5"));
	}

	public static String getPlayerName(UUID uuid) throws IOException {
		String responseString = IOUtil.readString(new URL("https://api.mojang.com/user/profiles/" + String.valueOf(uuid).replace("-", "") + "/names"));
		JsonArray result = Json.parse(responseString).asArray();
		return result.get(result.size() - 1).asObject().get("name").asString();
	}

}
