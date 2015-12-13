package com.crystal.httpdebugger.ui.text;

import android.content.Context;
import android.graphics.Color;
import android.widget.TableRow;
import android.widget.TextView;

public class TableRowText extends TextView {

	public TableRowText(Context context) {
		super(context);
		setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
		setTextColor(Color.WHITE);
		setTextSize(15);
		setPadding(5, 5, 10, 5);
	}

}
