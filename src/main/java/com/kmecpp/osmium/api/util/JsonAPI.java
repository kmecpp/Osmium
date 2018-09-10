package com.kmecpp.osmium.api.util;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import com.eclipsesource.json.JsonValue;

public abstract class JsonAPI {

	private final URL url;

	public JsonAPI() {
		try {
			//			this.url = new URL(url);
			this.url = new URL(getTarget());
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public abstract String getTarget();

	public URL getURL() {
		return url;
	}
	//	public URL getUrl() {
	//		return url;
	//	}

	public JsonValue get() throws IOException {
		return WebUtil.get(url);
	}

	public JsonValue post(JsonValue json) throws IOException {
		return WebUtil.post(url, json);
	}

}
