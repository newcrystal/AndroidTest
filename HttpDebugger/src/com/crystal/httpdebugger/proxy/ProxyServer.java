package com.crystal.httpdebugger.proxy;

import java.net.*;
import java.io.*;

import android.os.AsyncTask;

public class ProxyServer extends AsyncTask<String, Object, Object>{
    private int port = 10000;

	@Override
		protected Object doInBackground(String... arg) {
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
				new ProxyThread(serverSocket.accept()).run();
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
