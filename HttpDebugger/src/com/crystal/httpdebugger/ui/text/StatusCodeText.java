package com.crystal.httpdebugger.ui.text;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;

public class StatusCodeText extends TextView {
	public StatusCodeText(Context context, String statusCode) {
		super(context);
		setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		setTextSize(15);
			if ("200".equals(statusCode)) {
				setBackgroundColor(Color.GREEN);
			} else if ("500".equals(statusCode)) {
				setBackgroundColor(Color.RED);
			} else if ("403".equals(statusCode)) {
				setBackgroundColor(Color.BLUE);
			} else {
				setBackgroundColor(Color.BLACK);
			}
		setText(statusCode);
		setTextColor(Color.WHITE);
		setGravity(Gravity.START);
	}
}
