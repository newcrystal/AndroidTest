package com.crystal.httpdebugger.proxy.domain;

import java.util.HashMap;
import java.util.Map;

public class HttpResponse {
	private long id = 0;
	private String statusCode;
	private String message;
	private String protocol;
	private Map<String, String> header = new HashMap<String, String>();
	private String body;
	private long responseTime;
	
	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getHeader(String name) {
		return header.get(name);
	}
	public String getStatusCode() {
		return this.statusCode;
	}
	public String getProtocol() {
		return protocol;
	}
	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}
	public void add(String name, String value) {
		header.put(name, value);
	}
	public void setBody(String body) {
		this.body = body;
	}
	public String getBody() {
		return this.body;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public long getResponseTime() {
		return responseTime;
	}
	public void setResponseTime(long responseTime) {
		this.responseTime = responseTime;
	}
}
