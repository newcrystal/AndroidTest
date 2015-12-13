package com.crystal.httpdebugger.ui.text;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;

public class HeaderNameText extends TextView {

	public HeaderNameText(Context context) {
		super(context);
		setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		setTextSize(15);
		setTextColor(Color.WHITE);
		setGravity(Gravity.START);
		setBackgroundColor(Color.BLUE);
	}

}
