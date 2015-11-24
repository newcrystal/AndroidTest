package com.crystal.httpdebugger.proxy.domain;

import android.annotation.SuppressLint;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class HttpRequest {
	private String url;
	private String method;
	private String protocol;
	private int port = 80;
	private Map<String, String> header = new HashMap<String, String>();
	
	public HttpRequest() {};

	public void createHttpRequestData(String inputLine, boolean isFirstRow) {
		StringTokenizer tok = new StringTokenizer(inputLine);
		tok.nextToken();
		if (isFirstRow) initialize(inputLine);
		else addHeader(inputLine.split(": ")[0], inputLine.split(": ")[1]);
	}
	
	@SuppressLint("DefaultLocale")
	public void initialize(String line) {
		String[] tokens = line.split(" ");
        method = tokens[0];
        url = tokens[1];
    	protocol = tokens[2].split("/[0-9]\\.?[0-9]")[0];
        if (url.indexOf("http") < 0 && url.indexOf("HTTP") < 0){
        	url = new StringBuilder().append(protocol.toLowerCase()).append("://").append(url).toString();
        }
        extractPort();
        url = url.replaceAll(":"+port,"");
	}

	private void extractPort() {
		if (url.replaceAll("http://", "").indexOf(":") >= 0) port = Integer.parseInt(url.replaceAll("http://", "").split(":")[1].split("/")[0]);
	}
	
	public void addHeader(String name, String value) {
		header.put(name, value);
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
		return header.get(name);
	}
}
