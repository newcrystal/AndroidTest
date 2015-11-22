package com.crystal.httpdebugger.proxy;

import java.net.*;
import java.nio.charset.Charset;
import java.io.*;
import java.util.*;

import com.crystal.httpdebugger.domain.request.HttpRequest;
import com.crystal.httpdebugger.domain.response.HttpResponse;

public class ProxyThread extends Thread {
    private Socket socket = null;
    private static final int BUFFER_SIZE = 1024;
    private static final int SOCKET_TIMEOUT = 3000;
    public ProxyThread(Socket socket) {
        super("ProxyThread");
        this.socket = socket;
    }

    @Override
    public void run() {
        DataOutputStream out = getDataOutputStream();
		BufferedReader in = getBufferedReader();

        String inputLine;
        boolean isFirstRow = true;
        HttpRequest request = null;

    	Socket realServerSocket = getRealServerSocket();
        
        PrintWriter writerToRealServerSocket = null;
        try {
			while ((inputLine = in.readLine()) != null && !"".equals(inputLine)) {
			    try {
			        request = createHttpRequestData(inputLine, request, isFirstRow);
			        } catch (Exception e) {
			        break;
			    }
			        
			    if (writerToRealServerSocket == null) {
					try {
						writerToRealServerSocket = getRealServerSocketOutputStream(request, realServerSocket);
					} catch (URISyntaxException e) {
						e.printStackTrace();
					}
		        }
			   writerToRealServerSocket.println(inputLine); 
			   isFirstRow = false;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
        
        flushWriterToRealServerSocket(writerToRealServerSocket);
        
        try {
		    try {
		    	saveAndWriteResponse(out, request, realServerSocket);
             } catch (Exception e) {
                System.err.println("Encountered exception: " + e);
                out.writeBytes("");
            }
		    out.flush();
        } catch(Exception e) {
        	e.printStackTrace();
        }
        
        
        try {
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
        } catch (Exception e) {
    	  e.printStackTrace(); 
       }
   }

	private void saveAndWriteResponse(DataOutputStream out, HttpRequest request, Socket realServerSocket) throws IOException {
		BufferedInputStream realServerResult = new BufferedInputStream(realServerSocket.getInputStream());
		 
		byte chunck[] = new byte[ BUFFER_SIZE ];
		int index = realServerResult.read( chunck, 0, BUFFER_SIZE );
		StringBuilder response = new StringBuilder();
		HttpResponse httpResponse = new HttpResponse();
		while ( index != -1 ) {
			if (!isExceptSaveFileExtension(request))response.append(new String(chunck, 0, index, Charset.forName("UTF-8")));
			out.write(chunck, 0, index);
			index = realServerResult.read(chunck, 0, BUFFER_SIZE);
		}
		if (!isExceptSaveFileExtension(request)) {
			httpResponse.setBody(response.toString());
		}
	}

	private void flushWriterToRealServerSocket(
			PrintWriter writerToRealServerSocket) {
		if (writerToRealServerSocket != null) {
		    writerToRealServerSocket.println("");
			writerToRealServerSocket.flush();
		}
	}

	private HttpRequest createHttpRequestData(String inputLine, HttpRequest request, boolean isFirstRow) {
		StringTokenizer tok = new StringTokenizer(inputLine);
		tok.nextToken();
		if (isFirstRow) {
		    request = new HttpRequest(inputLine);
		} else if (request != null) {
			request.append(inputLine.split(": ")[0], inputLine.split(": ")[1]);
		}
		return request;
	}

	private PrintWriter getRealServerSocketOutputStream(HttpRequest request, Socket realServerSocket) throws UnknownHostException, IOException, URISyntaxException {                    	
        URL url = new URL(request.getUrl());
        URI uri = url.toURI();
		PrintWriter writerToRealServerSocket;
		InetAddress address = InetAddress.getByName(uri.getHost());
		realServerSocket.connect(new InetSocketAddress(address, request.getPort()), SOCKET_TIMEOUT);
		writerToRealServerSocket = new PrintWriter(realServerSocket.getOutputStream());
		return writerToRealServerSocket;
	}

	private Socket getRealServerSocket() {
		Socket realServerSocket = new Socket();
		try {
			realServerSocket.setSoTimeout(SOCKET_TIMEOUT);
		} catch (SocketException e) {
			e.printStackTrace();
		}
		return realServerSocket;
	}

	private BufferedReader getBufferedReader() {
		BufferedReader in = null;
		try {
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return in;
	}

	private DataOutputStream getDataOutputStream() {
		DataOutputStream out = null;
		try {
			out = new DataOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return out;
	}

    private boolean isExceptSaveFileExtension(HttpRequest request) {
    	if (request.get("Accept").indexOf("image") >= 0 || request.get("Accept").indexOf("css") >= 0) {
    		return true;
    	}
    	return false;
    }
}