package com.crystal.httpdebugger.ui.text;

import android.content.Context;
import android.graphics.Color;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

public class WhiteText extends TextView {

	public WhiteText(Context context) {
		super(context);
		setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		setTextColor(Color.WHITE);
		setTextSize(15);
	}

}
