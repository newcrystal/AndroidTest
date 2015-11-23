package com.crystal.httpdebugger;

import java.io.IOException;
import java.net.ServerSocket;

import android.app.Activity;
import android.os.Bundle;
import android.widget.LinearLayout;

import com.crystal.httpdebugger.proxy.ProxyServer;

public class HTTPDebugActivity extends Activity {
	private int port = 10000;
	private LinearLayout urlList;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_url_list);
		
		urlList = (LinearLayout) findViewById(R.id.urlListLayout);
		urlList.setVerticalScrollBarEnabled(true);
		
		ServerSocket serverSocket = null;

		try {
			serverSocket = new ServerSocket(port);
			System.out.println("Started on: " + port);
		} catch (IOException e) {
			System.err.println("Could not listen on port: " + port);
			System.exit(-1);
		}

		ProxyServer proxyServer = new ProxyServer();
		proxyServer.execute(this);

		try {
			serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
