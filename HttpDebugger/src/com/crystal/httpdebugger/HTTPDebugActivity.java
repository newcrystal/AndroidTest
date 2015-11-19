package com.crystal.httpdebugger;

import android.app.Activity;
import android.os.Bundle;

import com.crystal.httpdebugger.proxy.ProxyServer;

public class HTTPDebugActivity extends Activity{
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ProxyServer proxyServer = new ProxyServer();
		try {
			proxyServer.execute("");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
