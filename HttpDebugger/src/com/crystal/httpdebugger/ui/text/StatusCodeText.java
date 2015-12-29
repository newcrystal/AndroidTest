package com.crystal.httpdebugger.ui.text;

import android.content.Context;
import android.view.Gravity;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;

import com.crystal.httpdebugger.R.color;

public class StatusCodeText extends TextView {
	public StatusCodeText(Context context, String statusCode) {
		super(context);
		setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));
		setTextSize(15);
			if ("200".equals(statusCode)) {
				setBackgroundColor(getResources().getColor(color.LIGHT_GREEN));
			} else if ("500".equals(statusCode)) {
				setBackgroundColor(getResources().getColor(color.RED));
			} else if ("304".equals(statusCode)) {
				setBackgroundColor(getResources().getColor(color.LIGHT_BLUE));
			} else {
				setBackgroundColor(getResources().getColor(color.BLACK));
			}
		setText(statusCode);
		setTextColor(getResources().getColor(color.WHITE));
		setGravity(Gravity.CENTER_HORIZONTAL|Gravity.START);
	}
}
