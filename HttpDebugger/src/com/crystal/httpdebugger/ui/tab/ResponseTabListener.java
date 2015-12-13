package com.crystal.httpdebugger.ui.tab;

import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;

public class ResponseTabListener<T extends Fragment> implements TabListener {
	private Activity activity;
	private Bundle bundle;
	private ResponseFragment fragment;
	private Class<T> clazz;
	
	public ResponseTabListener(Activity activity, Class<T> clazz, Bundle bundle) {
		this.activity = activity;
		this.clazz = clazz;
		this.bundle = bundle;
		//this.fragment = (ResponseFragment)fragment;
	}
	
	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		if (fragment == null) {
			fragment = (ResponseFragment) Fragment.instantiate(activity, clazz.getName(), bundle);
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
