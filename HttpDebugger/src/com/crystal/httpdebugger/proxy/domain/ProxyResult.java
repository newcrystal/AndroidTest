package com.crystal.httpdebugger.proxy.domain;

public class ProxyResult {
	private HttpRequest httpRequest;
	private HttpResponse httpResponse;
	
	public ProxyResult (HttpRequest httpRequest, HttpResponse httpResponse) {
		this.httpRequest = httpRequest;
		this.httpResponse = httpResponse;
	}
	
	public HttpRequest getHttpRequest() {
		return httpRequest;
	}
	public HttpResponse getHttpResponse() {
		return httpResponse;
	}
	public void setHttpRequest(HttpRequest httpRequest) {
		this.httpRequest = httpRequest;
	}
	public void setHttpResponse(HttpResponse httpResponse) {
		this.httpResponse = httpResponse;
	}
}
