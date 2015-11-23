package com.crystal.httpdebugger.ui;

import android.app.Activity;
import android.graphics.Color;
import android.view.Gravity;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;

public class UrlListButton extends Button {
	public UrlListButton(Activity activity) {
		super(activity.getBaseContext());
		
		setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		setTextSize(12);
		setPadding(10, 10, 10, 10);
		setBackgroundColor(Color.rgb(000, 153, 204));
		setTextColor(Color.WHITE);
		setGravity(Gravity.START);
	}
}
