package com.crystal.httpdebugger.proxy;

import java.io.IOException;
import java.net.ServerSocket;

import android.app.Activity;
import android.os.AsyncTask;

public class ProxyServer extends AsyncTask<Activity, Object, Object>{
    private int port = 10000;

	@Override
		protected Object doInBackground(Activity... activities) {
        ServerSocket serverSocket = null;
        boolean listening = true;
        
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Started on: " + port);
        } catch (IOException e) {
            System.err.println("Could not listen on port: " + port);
            System.exit(-1);
        }

        while (listening) {
            try {
				new ProxyThread(serverSocket.accept(), activities[0]).run();
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
        try {
			serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
        
		return null;
	}
}
