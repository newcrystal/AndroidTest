package com.crystal.httpdebugger.proxy.domain;

import java.util.HashMap;
import java.util.Map;

public class HttpResponse {
	private int id = 0;
	private String statusCode;
	private String message;
	private String protocol;
	private Map<String, String> header = new HashMap<String, String>();
	private String body;
	private int responseTime;
	
	public boolean isEmpty(){
		return (statusCode == null || statusCode.isEmpty())
				&& (message == null || message.isEmpty())
				&& (protocol == null || protocol.isEmpty());
	}
	
	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public Map<String, String> getHeader() {
		return header;
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
	public int getResponseTime() {
		return responseTime;
	}
	public void setResponseTime(int responseTime) {
		this.responseTime = responseTime;
	}
}
