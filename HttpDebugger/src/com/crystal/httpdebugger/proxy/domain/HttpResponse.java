package com.crystal.httpdebugger.proxy.domain;

import java.util.Map;

public class HttpResponse {
	private String statusCode;
	private Map<String, String> elements;
	private String body;
	public void append(String name, String value) {
		elements.put(name, value);
	}
	public void setBody(String body) {
		this.body = body;
	}
	public String getBody() {
		return this.body;
	}
}
