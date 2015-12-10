package com.crystal.httpdebugger.ui;

import android.content.Context;
import android.graphics.Color;
import android.widget.TableRow;
import android.widget.TextView;

public class TableRowTextView extends TextView {

	public TableRowTextView(Context context) {
		super(context);
		setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
		setTextColor(Color.WHITE);
		setTextSize(15);
		setPadding(5, 5, 10, 5);
	}

}
