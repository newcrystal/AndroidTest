package com.crystal.httpdebugger.ui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;

public class UrlListButton extends Button {

	GradientDrawable successStroke = new GradientDrawable();
	GradientDrawable errorStroke = new GradientDrawable();
	GradientDrawable authStroke = new GradientDrawable();
	GradientDrawable unkwonStroke = new GradientDrawable();

	public void initialize() {
		setRectangleStorke(successStroke, Color.GREEN);
		setRectangleStorke(errorStroke, Color.RED);
		setRectangleStorke(authStroke, Color.BLUE);
		setRectangleStorke(unkwonStroke, Color.BLACK);
	}

	public UrlListButton(Context context, String statusCode) {
		super(context);

		setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		setTextSize(15);
		if (statusCode == null) {
			setBackground(unkwonStroke);
		} else if (statusCode.equals("200") || statusCode.equals("302")) {
			setBackground(successStroke);
		} else if (statusCode.equals("403")) {
			setBackground(authStroke);
		} else if (statusCode.equals("401") || statusCode.equals("500")) {
			setBackground(errorStroke);
		} else {
			setBackground(unkwonStroke);
		}

		setTextColor(Color.WHITE);
		setGravity(Gravity.START);
	}

	public GradientDrawable setRectangleStorke(GradientDrawable drawable, int color) {
		drawable.setShape(GradientDrawable.RECTANGLE);
		drawable.setStroke(5, color);
		drawable.setCornerRadius(5);
		return drawable;
	}
}
