package com.crystal.httpdebugger.ui;

import android.app.Activity;
import android.widget.LinearLayout;

import com.crystal.httpdebugger.R;
import com.crystal.httpdebugger.proxy.domain.ProxyResult;

public class UrlList {
	private Activity activity;
	
	public UrlList(Activity activity) {
		super();
		this.activity = activity;
	}
	
	public void add(ProxyResult result) {
		UrlListButton button = new UrlListButton(activity);
		button.setText(result.getHttpRequest().getUrl());
		((LinearLayout)activity.findViewById(R.id.urlListLayout)).addView(button);
	}
}
