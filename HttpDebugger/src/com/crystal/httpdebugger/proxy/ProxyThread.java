package com.crystal.httpdebugger.proxy;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
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

import com.crystal.httpdebugger.proxy.domain.HttpRequest;
import com.crystal.httpdebugger.proxy.domain.HttpResponse;
import com.crystal.httpdebugger.util.InputStreamUtils;

public class ProxyThread extends Thread {
    private Socket socket = null;
    private static final int BUFFER_SIZE = 1024;
    private static final int SOCKET_TIMEOUT = 3000;
    private HttpRequest httpRequest = new HttpRequest();
    private HttpResponse httpResponse = new HttpResponse();
    
    public ProxyThread(Socket socket) {
        super("ProxyThread");
        this.socket = socket;
    }

    @Override
    public void run() {
        DataOutputStream out = getDataOutputStream();
		BufferedReader in = getBufferedReader();


    	Socket realServerSocket = getRealServerSocket();
        PrintWriter writerToRealServerSocket = null;
        try {
        	writerToRealServerSocket = readRequestAndWriteOutputStream(in, realServerSocket);
		} catch (IOException e) {
			e.printStackTrace();
		}
        
        flushWriterToRealServerSocket(writerToRealServerSocket);
        
        try {
		    try {
		    	if (writerToRealServerSocket != null){
		    		setAndWriteResponse(out, httpRequest, realServerSocket);
		    	}
             } catch (Exception e) {
                System.err.println("Encountered exception: " + e);
                System.err.println("Exception URL : " + httpRequest.getUrl());
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
            //urlList.add(new ProxyResult(getHttpRequest(), getHttpResponse()));
        } catch (Exception e) {
    	  e.printStackTrace(); 
       }
       
   }

	private PrintWriter readRequestAndWriteOutputStream(BufferedReader in, Socket realServerSocket) throws IOException, UnknownHostException {
		String inputLine;
		boolean isFirstRow = true;
		PrintWriter writerToRealServerSocket = null;
		
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
		return writerToRealServerSocket;
	}

	private void setAndWriteResponse(DataOutputStream out, HttpRequest request, Socket realServerSocket) throws IOException {
		InputStream inputStream = setResponseHeader(out, realServerSocket);
		
		BufferedInputStream realServerResult = new BufferedInputStream(inputStream);
		byte chunck[] = new byte[ BUFFER_SIZE ];
		int index = realServerResult.read( chunck, 0, BUFFER_SIZE );
		StringBuilder response = new StringBuilder();
		while ( index != -1 ) {
			if (!isExceptSetBodyFileExtension())response.append(new String(chunck, 0, index, Charset.forName("UTF-8")));
			out.write(chunck, 0, index);
			index = realServerResult.read(chunck, 0, BUFFER_SIZE);
		}
		httpResponse.setBody(response.toString());
	}

	private InputStream setResponseHeader(DataOutputStream out, Socket realServerSocket) throws IOException {
		InputStream inputStream = realServerSocket.getInputStream();
		String line = null;
		boolean isFirst = true;
		StringBuilder header = new StringBuilder();
		
		do {
			line = InputStreamUtils.readLine(inputStream);
			if (line == null) {
				out.writeBytes("");
				break;
			}
			if (isFirst) {
				String[] status = line.split(" ", 3);
				if (status.length == 3) {
					httpResponse.setProtocol(status[0]);
					httpResponse.setStatusCode(status[1]);
					httpResponse.setMessage(status[2]);
				}
				isFirst = false;
			} else {
				String[] headerLine = line.split(":");
				if (headerLine.length == 2) {
					httpResponse.add(headerLine[0], headerLine[1]);
				}
			}
			header.append(line).append("\n");
		} while (!line.isEmpty());
		out.writeBytes(header.toString());
		return inputStream;
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

    private boolean isExceptSetBodyFileExtension() {
    	String contentType = httpResponse.getHeader("Content-Type");
    	if (contentType != null && contentType.indexOf("image/gif") >= 0) return true;
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