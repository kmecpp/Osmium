package com.kmecpp.osmium.api.util;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.ParseException;
import com.kmecpp.osmium.api.GameProfile;

public class MojangUtil {

	public static final Pattern UUID_PATTERN = Pattern.compile("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})");

	public static Optional<GameProfile> getProfileRaw(UUID uuid) throws IOException {
		try {
			JsonArray response = WebUtil.get(new URL("https://api.mojang.com/user/profiles/" + String.valueOf(uuid) + "/names")).asArray();
			String name = response.get(response.size() - 1).asObject().get("name").asString();
			return Optional.of(new GameProfile(uuid, name));
		} catch (ParseException ex) {
			return Optional.empty(); //ParseException occurs when no data is returned from the server (user does not exist). IOException occurs when the server is down
		}
	}

	public static Optional<GameProfile> getProfileRaw(String name) throws IOException {
		try {
			JsonObject json = WebUtil.get(new URL("https://api.mojang.com/users/profiles/minecraft/" + name)).asObject();
			UUID uuid = UUID.fromString(UUID_PATTERN.matcher(json.get("id").asString()).replaceAll("$1-$2-$3-$4-$5"));;
			return Optional.of(new GameProfile(uuid, json.get("name").asString()));
		} catch (ParseException ex) {
			return Optional.empty(); //ParseException occurs when no data is returned from the server (user does not exist). IOException occurs when the server is down
		}
	}

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
