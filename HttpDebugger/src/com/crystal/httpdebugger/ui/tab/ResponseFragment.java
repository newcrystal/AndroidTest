package com.crystal.httpdebugger.ui.tab;

import java.util.Map;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.crystal.httpdebugger.R;
import com.crystal.httpdebugger.db.DatabaseQuery;
import com.crystal.httpdebugger.proxy.domain.HttpResponse;
import com.crystal.httpdebugger.ui.text.TableRowText;
import com.crystal.httpdebugger.ui.text.TitleText;
import com.crystal.httpdebugger.ui.text.WhiteText;

public class ResponseFragment extends Fragment {
	private final static String BLANK = " ";
	private DatabaseQuery dbQuery;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		ScrollView fragmentView = (ScrollView)inflater.inflate(R.layout.fragment_response, container, false);
		Bundle bundle = this.getArguments();
		Integer id = bundle.getInt("id");
		
		dbQuery = new DatabaseQuery(getActivity());
		
		LinearLayout layout = new LinearLayout(getActivity());
		layout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		layout.setOrientation(LinearLayout.VERTICAL);
		
		if (id == 0) {
			Toast.makeText(getActivity(), "id is empty!!", Toast.LENGTH_SHORT).show();
		} else {
			HttpResponse response = dbQuery.getResponse(id);
			layout.addView(getResponseTextView(response));
			layout.addView(getResponseHeaderTable(response));
			if (response.getBody() != null) layout.addView(getResponseBodyTitleTextView());
			if (response.getBody() != null) layout.addView(getResponseBodyTextView(response));
			
			fragmentView.addView(layout);
		}
		return fragmentView;
	}
	
	public TextView getResponseTextView(HttpResponse HttpResponse) {
		TextView responseText = new TitleText(getActivity());
		
		StringBuilder text = new StringBuilder();
		if (text != null) text.append(HttpResponse.getStatusCode()).append(BLANK);
		if (text != null) text.append(HttpResponse.getMessage()).append(BLANK);
		if (text != null) text.append(HttpResponse.getProtocol()).append(BLANK);
		
		responseText.setText(text);
		return responseText;
	}
	
	public TableLayout getResponseHeaderTable(HttpResponse response) {
		TableLayout tableLayout = new TableLayout(getActivity());
		tableLayout.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
		//tableLayout.setColumnStretchable(1, true);

		for (Map.Entry<String, String> entry : response.getHeader().entrySet()) {
			TableRow row = new TableRow(getActivity());
			
			TextView headerName = new TableRowText(getActivity());
			headerName.setText(entry.getKey());
			headerName.setTextColor(Color.CYAN);
			row.addView(headerName);
			tableLayout.addView(row);
			
			row = new TableRow(getActivity());
			TextView headerValue = new TableRowText(getActivity());
			headerValue.setText(entry.getValue());
			row.addView(headerValue);
			
			tableLayout.addView(row);
		}
		return tableLayout;
	}

	public TextView getResponseBodyTitleTextView() {
		TextView bodyText = new TitleText(getActivity());
		bodyText.setText("BODY");
		return bodyText;
	}
	
	public TextView getResponseBodyTextView(HttpResponse response) {
		TextView bodyText = new WhiteText(getActivity());
		bodyText.setText(response.getBody());
		return bodyText;
	}
}
