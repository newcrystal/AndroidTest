package com.crystal.httpdebugger.ui;

import android.content.Context;
import android.graphics.Color;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

public class WhiteTextView extends TextView {

	public WhiteTextView(Context context) {
		super(context);
		setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		setTextColor(Color.WHITE);
		setTextSize(15);
	}

}
