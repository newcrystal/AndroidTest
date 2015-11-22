package com.crystal.httpdebugger.test;

import java.io.IOException;
import java.net.ServerSocket;

import org.junit.Test;

import com.crystal.httpdebugger.proxy.ProxyThread;

public class ProxyServer {
    private int port = 10000;
    
	@Test
	public void test() {
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
	}
}
