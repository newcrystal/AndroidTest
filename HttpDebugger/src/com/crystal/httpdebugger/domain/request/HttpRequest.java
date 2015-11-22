package com.crystal.httpdebugger.domain.request;

import java.util.HashMap;
import java.util.Map;

public class HttpRequest {
	private String url;//1.[1]]
	private String method; //1.[0]
	private int port = 80;
	private Map<String, String> elements = new HashMap<String, String>();
	
	public HttpRequest() {};
	
	public HttpRequest(String line) {
		String[] tokens = line.split(" ");
        method = tokens[0];
        url = tokens[1];
        extractPort();
	}

	private void extractPort() {
		if (url.replaceAll("http://", "").indexOf(":") >= 0) port = Integer.parseInt(url.replaceAll("http://", "").split(":")[1].split("/")[0]);
	}
	
	public void append(String name, String value) {
		elements.put(name, value);
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String get(String name) {
		return elements.get(name);
	}

	public void setElements(Map<String, String> elements) {
		this.elements = elements;
	}
}
