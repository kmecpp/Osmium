package com.kmecpp.osmium.api.util;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import com.eclipsesource.json.JsonValue;

public abstract class JsonAPI {

	private final URL url;

	public JsonAPI(String url) {
		try {
			this.url = new URL(url);
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public URL getUrl() {
		return url;
	}

	public void post(JsonValue json) {
		try {
			WebUtil.post(url, json);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
