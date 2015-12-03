package com.crystal.httpdebugger;

import java.io.IOException;
import java.net.ServerSocket;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.crystal.httpdebugger.proxy.ProxyThread;
import com.crystal.httpdebugger.proxy.domain.HttpRequest;
import com.crystal.httpdebugger.proxy.domain.HttpResponse;
import com.crystal.httpdebugger.proxy.domain.ProxyResult;
import com.crystal.httpdebugger.ui.StatusCodeText;
import com.crystal.httpdebugger.ui.UrlListButton;

public class HTTPDebugActivity extends Activity {
	private LinearLayout urlList;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_url_list);
		
		urlList = (LinearLayout) findViewById(R.id.urlListLayout);
		urlList.setVerticalScrollBarEnabled(true);

		ProxyServer proxyServer = new ProxyServer();
		proxyServer.execute();
	}
	
	public class ProxyServer extends AsyncTask<Object, ProxyResult, Object>{
	    private int port = 10000;

		@Override
		protected ProxyResult doInBackground(Object... objs) {
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
	            	ProxyThread thread = new ProxyThread(serverSocket.accept());
	            	thread.run();
	            	ProxyResult result = new ProxyResult(thread.getHttpRequest(), thread.getHttpResponse());
	            	publishProgress(result);
				} catch (IOException e) {
					e.printStackTrace();
				}
	            
	            try {
	            	Thread.sleep(100);
	            } catch(InterruptedException e) {
	            }
	        }
	        try {
				serverSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
	        
			return null;
		}

		@Override
		protected void onProgressUpdate(ProxyResult ... results) {
			if(results.length > 0) {
				HttpRequest request = results[0].getHttpRequest();
				HttpResponse response = results[0].getHttpResponse();
				String statusCode = (response == null) ? "???" : response.getStatusCode();
				UrlListButton urlButton = new UrlListButton(getBaseContext(), statusCode);
				String url = request.getUrl();
				if (url != null && !url.equals("")) {
					urlButton.setText(url);
					TextView statusCodeButton = new StatusCodeText(getBaseContext(), statusCode);
					LinearLayout urlObj = new LinearLayout(getBaseContext());
					urlObj.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
					urlObj.setOrientation(LinearLayout.HORIZONTAL);
					
					urlObj.addView(statusCodeButton);
					urlObj.addView(urlButton);
					urlList.addView(urlObj);
				}
			}
		}
	}
}
