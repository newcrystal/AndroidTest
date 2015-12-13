package com.crystal.httpdebugger.proxy;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FilterInputStream;
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
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;

import com.crystal.httpdebugger.proxy.domain.HttpRequest;
import com.crystal.httpdebugger.proxy.domain.HttpResponse;
import com.crystal.httpdebugger.util.InputStreamUtils;

public class ProxyThread extends Thread {
    private Socket socket = null;
    private static final int BUFFER_SIZE = 1024*4;
    private static final int SOCKET_TIMEOUT = 3000;
    private HttpRequest httpRequest = new HttpRequest();
    private HttpResponse httpResponse = new HttpResponse();
    private InputStream realServerInputStream = null;
    
    public ProxyThread(Socket socket) {
        super("ProxyThread");
        this.socket = socket;
    }

    @Override
    public void run() {
    	long startTime = Calendar.getInstance().getTimeInMillis();
        DataOutputStream out = getDataOutputStream();
		BufferedReader in = getBufferedReader();


    	Socket realServerSocket = getRealServerSocket();
        PrintWriter writerToRealServerSocket = null;
        try {
        	writerToRealServerSocket = readRequestAndWriteOutputStream(in, realServerSocket);
        	realServerInputStream = realServerSocket.getInputStream();
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
       long endTime = Calendar.getInstance().getTimeInMillis();
       httpResponse.setResponseTime((int)(endTime-startTime));
   }

	private PrintWriter readRequestAndWriteOutputStream(BufferedReader in, Socket realServerSocket) throws IOException, UnknownHostException {
		String inputLine;
		boolean isFirstRow = true;
		PrintWriter writerToRealServerSocket = null;
		StringBuilder body = new StringBuilder();
		boolean isBodyStart = false;
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
		   
		   if (!isBodyStart && inputLine.isEmpty()) isBodyStart = true; 
		   if (isBodyStart) body.append(inputLine);
		   writerToRealServerSocket.println(inputLine); 
		   isFirstRow = false;
		}

		httpRequest.setBody(body.toString());
		return writerToRealServerSocket;
	}

	private void setAndWriteResponse(DataOutputStream out, HttpRequest request, Socket realServerSocket) throws IOException {
		InputStream inputStream = setResponseHeader(out, realServerSocket);
		FilterInputStream inputStreamWrapper = null;
		/*if (isGzipped()) {
			inputStreamWrapper = new GZIPInputStream(inputStream);
		} else if (isDeflate()) {
			inputStreamWrapper = new InflaterInputStream(inputStream);
		} else {*/
			inputStreamWrapper = new BufferedInputStream(inputStream);
		//}
		ByteArrayOutputStream responseStream = new ByteArrayOutputStream();
		byte chunck[] = new byte[ BUFFER_SIZE ];
		int index = inputStreamWrapper.read( chunck, 0, BUFFER_SIZE );
		while ( index != -1 ) {
			if (!isExceptSetBodyFileExtension() && !isNotModified()){
				responseStream.write(chunck, 0, index);
			}
			index = inputStreamWrapper.read(chunck, 0, BUFFER_SIZE);
		}
		out.write(responseStream.toByteArray());
		out.writeBytes("");
		httpResponse.setBody(new String(responseStream.toByteArray(), Charset.forName("UTF-8")));
	}

	private boolean isDeflate() {
		return httpResponse.getHeader("Content-Encoding") != null && httpResponse.getHeader("Content-Encoding").indexOf("deflate") >= 0;
	}

	private boolean isGzipped() {
		return httpResponse.getHeader("Content-Encoding") != null && httpResponse.getHeader("Content-Encoding").indexOf("gzip") >= 0;
	}

	private InputStream setResponseHeader(DataOutputStream out, Socket realServerSocket) throws IOException {
		String line = null;
		boolean isFirst = true;
		StringBuilder header = new StringBuilder();
		
		do {
			line = InputStreamUtils.readLine(realServerInputStream);
			if (line == null) {
				if (isFirst) out.writeBytes("");
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
		return realServerInputStream;
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
    	if (contentType != null && contentType.indexOf("image/") >= 0) return true;
    	return false;
    }

    private boolean isNotModified() {
    	return httpResponse.getStatusCode().equals("302");
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