package com.crystal.httpdebugger;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.crystal.httpdebugger.ui.tab.RequestFragment;
import com.crystal.httpdebugger.ui.tab.RequestTabListener;
import com.crystal.httpdebugger.ui.tab.ResponseFragment;
import com.crystal.httpdebugger.ui.tab.ResponseTabListener;

public class HTTPDebugDetailActivity extends FragmentActivity {
	private int id;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail);
		
		Intent receivedIntent = getIntent();
		id = receivedIntent.getIntExtra("id", 0);
		
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		
		Bundle bundle = new Bundle();
		bundle.putInt("id", id);
		
		Tab requestTab = actionBar.newTab();
		requestTab.setText("Request");
		requestTab.setTabListener(new RequestTabListener<RequestFragment>(this, RequestFragment.class, bundle));
		
		Tab responseTab = actionBar.newTab();
		responseTab.setText("Response");
		responseTab.setTabListener(new ResponseTabListener<ResponseFragment>(this, ResponseFragment.class, bundle));
		
		actionBar.addTab(requestTab);
		actionBar.addTab(responseTab);
	}
}
