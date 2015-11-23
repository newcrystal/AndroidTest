package com.crystal.httpdebugger.proxy;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.charset.Charset;

import android.app.Activity;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;

import com.crystal.httpdebugger.R;
import com.crystal.httpdebugger.proxy.domain.HttpRequest;
import com.crystal.httpdebugger.proxy.domain.HttpResponse;
import com.crystal.httpdebugger.proxy.domain.ProxyResult;
import com.crystal.httpdebugger.ui.UrlList;

public class ProxyThread extends Thread {
    private Socket socket = null;
    private static final int BUFFER_SIZE = 1024;
    private static final int SOCKET_TIMEOUT = 3000;
    private HttpRequest httpRequest = new HttpRequest();
    private HttpResponse httpResponse = new HttpResponse();
    private Activity activity;
    private UrlList urlList;
    
    public ProxyThread(Socket socket, Activity activity) {
        super("ProxyThread");
        this.socket = socket;
        this.activity = activity;
        this.urlList = new UrlList(activity);
    }

    @Override
    public void run() {
        DataOutputStream out = getDataOutputStream();
		BufferedReader in = getBufferedReader();


    	Socket realServerSocket = getRealServerSocket();
        PrintWriter writerToRealServerSocket = null;
        try {
            readRequestAndWriteOutputStream(in, realServerSocket, writerToRealServerSocket);
		} catch (IOException e) {
			e.printStackTrace();
		}
        
        flushWriterToRealServerSocket(writerToRealServerSocket);
        
        try {
		    try {
		    	saveAndWriteResponse(out, httpRequest, realServerSocket);
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
            urlList.add(new ProxyResult(getHttpRequest(), getHttpResponse()));
        } catch (Exception e) {
    	  e.printStackTrace(); 
       }
       
   }

	private void readRequestAndWriteOutputStream(BufferedReader in, Socket realServerSocket, PrintWriter writerToRealServerSocket) throws IOException, UnknownHostException {
		String inputLine;
		boolean isFirstRow = true;
		
		while ((inputLine = in.readLine()) != null && !"".equals(inputLine)) {
		    try {
		        httpRequest.createHttpRequestData(inputLine, isFirstRow);
		        } catch (Exception e) {
		        break;
		    }
		        
		    if (writerToRealServerSocket == null) {
				try {
					writerToRealServerSocket = getRealServerSocketOutputStream(httpRequest, realServerSocket);
				} catch (URISyntaxException e) {
					e.printStackTrace();
				}
		    }
		   writerToRealServerSocket.println(inputLine); 
		   isFirstRow = false;
		}
	}

	private void saveAndWriteResponse(DataOutputStream out, HttpRequest request, Socket realServerSocket) throws IOException {
		BufferedInputStream realServerResult = new BufferedInputStream(realServerSocket.getInputStream());
		 
		byte chunck[] = new byte[ BUFFER_SIZE ];
		int index = realServerResult.read( chunck, 0, BUFFER_SIZE );
		StringBuilder response = new StringBuilder();
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