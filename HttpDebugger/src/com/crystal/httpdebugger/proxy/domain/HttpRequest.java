package com.crystal.httpdebugger.proxy.domain;

import android.annotation.SuppressLint;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HttpRequest {
	private long id = 0;
	private String url;
	private String method;
	private String protocol;
	private String protocolVersion;

	private int port = 80;
	private Map<String, String> header = new HashMap<String, String>();
	private String body;
	
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
        Pattern pattern = Pattern.compile("/[0-9]\\.?[0-9]");
        Matcher matcher = pattern.matcher(tokens[2]);
        
        while (matcher.find()) {
        	protocol = tokens[2].replace(matcher.group(), "");
        	protocolVersion = matcher.group().replace("/", "");	
        }
        
        
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

	public Map<String, String> getHeader() {
		return this.header;
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

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	public String getProtocol() {
		return protocol;
	}

	public String getProtocolVersion() {
		return protocolVersion;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public void setProtocolVersion(String protocolVersion) {
		this.protocolVersion = protocolVersion;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}
}
