package com.crystal.httpdebugger;

import java.io.IOException;
import java.net.ServerSocket;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.crystal.httpdebugger.db.DatabaseQuery;
import com.crystal.httpdebugger.proxy.ProxyThread;
import com.crystal.httpdebugger.proxy.domain.HttpRequest;
import com.crystal.httpdebugger.proxy.domain.HttpResponse;
import com.crystal.httpdebugger.proxy.domain.ProxyResult;
import com.crystal.httpdebugger.ui.list.UrlListButton;
import com.crystal.httpdebugger.ui.text.StatusCodeText;

public class CopyOfCopyOfCopyOfHTTPDebugActivity44 extends Activity {
	private LinearLayout urlList;
	private DatabaseQuery dbQuery;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_url_list);
		
		dbQuery = new DatabaseQuery(this);
		
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
	            	
	            	int id = dbQuery.insertRequest(thread.getHttpRequest());
					if (id > 0) {
						thread.getHttpRequest().setId(id);
						thread.getHttpResponse().setId(id);
						dbQuery.insertResponse(thread.getHttpResponse());
						
						ProxyResult result = new ProxyResult(thread.getHttpRequest(), thread.getHttpResponse());
		            	publishProgress(result);
					}
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
				String url = request.getUrl();
				if (url != null && !url.equals("")) {
					String statusCode = getStatusCode(response);
					LinearLayout row = createRow();

					TextView statusCodeView = new StatusCodeText(getBaseContext(), statusCode);
					row.addView(statusCodeView);
					
					UrlListButton urlButton = createUrlButton(request, response, url, statusCode);
					row.addView(urlButton);
					
					urlList.addView(row);
				}
			}
		}

		private UrlListButton createUrlButton(HttpRequest request, HttpResponse response, String url, String statusCode) {
			UrlListButton urlButton = new UrlListButton(getBaseContext(), statusCode);
			final int MAX_URL_LENGTH = 70;
			String slicedUrl = url.substring(0, Math.min(url.length(), MAX_URL_LENGTH));
			StringBuilder urlText = new StringBuilder().append(request.getMethod()).append(" ").append(slicedUrl);
			if (url.length() > MAX_URL_LENGTH) urlText.append("...");		
			urlButton.setText(urlText.toString());
			urlButton.setOnClickListener(new UrlOnClickListener(request.getId()));
			return urlButton;
		}

		private LinearLayout createRow() {
			LinearLayout row = new LinearLayout(getBaseContext());
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			params.setMargins(0, 0, 0, 5);
			row.setLayoutParams(params);
			row.setOrientation(LinearLayout.HORIZONTAL);
			return row;
		}

		private String getStatusCode(HttpResponse response) {
			final String UNKWON_STATUS = "???";
			String statusCode = (response == null) ? UNKWON_STATUS : response.getStatusCode();
			statusCode = (statusCode == null) ? UNKWON_STATUS : statusCode;
			return statusCode;
		}
	}

	public class UrlOnClickListener implements OnClickListener {
		private int id;
		public UrlOnClickListener(int id) {
			this.id = id;
		}
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(getApplicationContext(), HTTPDebugDetailActivity.class);
			intent.putExtra("id", id);
			startActivity(intent);
		}
	}	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.url_list_menu, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.deleteButton :
				new AlertDialog.Builder(this)
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setTitle("Delete")
				.setMessage("Do you really want to delete?")
				.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dbQuery.deleteAll();
						urlList.removeAllViews();
					}
				})
				.setNegativeButton("No", null)
				.show();
				return true;
			default :
				return super.onOptionsItemSelected(item);
		}
		
	}
}
