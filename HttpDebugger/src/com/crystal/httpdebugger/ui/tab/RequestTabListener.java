package com.crystal.httpdebugger.ui.tab;

import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;

public class RequestTabListener <T extends Fragment> implements TabListener {
	private Activity activity;
	private Bundle bundle;
	private RequestFragment fragment;
	private Class<T> clazz;
	
	public RequestTabListener(Activity activity, Class<T> clazz, Bundle bundle) {
		this.activity = activity;
		this.clazz = clazz;
		this.bundle = bundle;
	}
	
	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		if (fragment == null) {
			fragment = (RequestFragment) Fragment.instantiate(activity, clazz.getName(), bundle);
			ft.add(android.R.id.content, fragment);
		} else {
			ft.attach(fragment);
		}
	}
	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		if (fragment != null) {
			ft.detach(fragment);
		}
	}
	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
		
	}
}
