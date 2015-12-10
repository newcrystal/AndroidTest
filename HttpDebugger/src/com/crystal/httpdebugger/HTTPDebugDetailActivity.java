package com.crystal.httpdebugger;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.crystal.httpdebugger.ui.RequestFragment;
import com.crystal.httpdebugger.ui.RequestTabListener;
import com.crystal.httpdebugger.ui.ResponseFragment;
import com.crystal.httpdebugger.ui.ResponseTabListener;

public class HTTPDebugDetailActivity extends FragmentActivity {
	private long id;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail);
		
		Intent receivedIntent = getIntent();
		id = receivedIntent.getLongExtra("id", 0);
		
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		
		Bundle bundle = new Bundle();
		bundle.putLong("id", id);
		
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
