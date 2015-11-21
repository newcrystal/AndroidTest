package com.crystal.httpdebugger.proxy;

import java.net.*;
import java.nio.charset.Charset;
import java.io.*;
import java.util.*;

public class ProxyThread extends Thread {
    private Socket socket = null;
    //private static final int BUFFER_SIZE = 1024*2*2;
    public ProxyThread(Socket socket) {
        super("ProxyThread");
        this.socket = socket;
    }

    @Override
    public void run() {
        //get input from user
        //send request to server
        //get response from server
        //send response to user

        try {
            DataOutputStream out =
		new DataOutputStream(socket.getOutputStream());
            BufferedReader in = new BufferedReader(
		new InputStreamReader(socket.getInputStream()));

            String inputLine, outputLine;
            int cnt = 0;
            String urlToCall = "";

        	///////////////////////////////////
            //begin get request from client
            Socket realServerSocket = new Socket();
            realServerSocket.setSoTimeout(10000);
            PrintWriter pw = null;
            while ((inputLine = in.readLine()) != null && !"".equals(inputLine)) {
                try {
                    StringTokenizer tok = new StringTokenizer(inputLine);
                    tok.nextToken();
                    //System.out.println(inputLine);
                    //parse the first line of the request to find the url
                    if (cnt == 0) {
                        String[] tokens = inputLine.split(" ");
                        urlToCall = tokens[1];
                        //can redirect this to output log
                        System.out.println("Request for : " + urlToCall);
                        
                        URL url = new URL(urlToCall);
                        URI uri;
        				uri = url.toURI();
        	            InetAddress address = InetAddress.getByName(uri.getHost());
        	            realServerSocket.connect(new InetSocketAddress(address, 80), 5000);
        	            pw = new PrintWriter(realServerSocket.getOutputStream());
                    }
                    pw.println(inputLine);
                } catch (Exception e) {
                    break;
                }

                cnt++;
            }
            if (pw != null) {
	            pw.println("");
	    		pw.flush();
            }
            
		    try {
		    	int buffersize = realServerSocket.getReceiveBufferSize();
            	int BUFFER_SIZE = 1024;
            	BufferedInputStream realServerResult = new BufferedInputStream(realServerSocket.getInputStream(), BUFFER_SIZE);
            	 
                /*byte by[] = new byte[ BUFFER_SIZE ];
                int index = realServerResult.read( by, 0, BUFFER_SIZE );
                StringBuilder sb = new StringBuilder();
                while ( index != -1 )
                {
                  sb.append(new String(by, 0, index, Charset.forName("UTF-8")));
                  out.write( by, 0, index );
                  index = realServerResult.read( by, 0, BUFFER_SIZE );
                }
                if (urlToCall.indexOf(".png") < 0 && urlToCall.indexOf(".gif") < 0 && urlToCall.indexOf(".jpg") < 0) {
                	System.out.println(sb.toString());
                }*/
            	byte buffer[] = new byte [BUFFER_SIZE];
            	int bytesRead = 0;
            	StringBuilder result = new StringBuilder();
            	
            	while ((bytesRead = realServerResult.read(buffer, 0, BUFFER_SIZE)) != -1) {
            		result.append(new String(buffer, 0, bytesRead));
            		System.out.println(result);
            		out.write(buffer, 0, bytesRead);
            		
            		if (bytesRead < BUFFER_SIZE) break;
            	}
            
            	
             } catch (Exception e) {
                System.err.println("Encountered exception: " + e);
                out.writeBytes("");
            }
            out.flush();
            
            if (realServerSocket != null) {
            	realServerSocket.close();
            }	
            if (out != null) {
                out.close();
            }
            if (in != null) {
                in.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}