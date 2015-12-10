package com.crystal.httpdebugger.ui;

import android.content.Context;
import android.graphics.Color;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class TitleTextView extends TextView {
	public TitleTextView(Context context) {
		super(context);
		setBackgroundColor(Color.WHITE);
		setTextColor(Color.BLACK);
		setTextSize(20);
		
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		params.setMargins(0, 20, 0, 20);
		setLayoutParams(params);
	}
}
