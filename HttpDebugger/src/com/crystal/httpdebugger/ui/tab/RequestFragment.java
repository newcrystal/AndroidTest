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
import com.crystal.httpdebugger.proxy.domain.HttpRequest;
import com.crystal.httpdebugger.ui.text.TableRowText;
import com.crystal.httpdebugger.ui.text.TitleText;
import com.crystal.httpdebugger.ui.text.WhiteText;

public class RequestFragment extends Fragment {
	private final static String BLANK = " ";
	private DatabaseQuery dbQuery;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		ScrollView fragmentView = (ScrollView)inflater.inflate(R.layout.fragment_request, container, false);
		Bundle bundle = this.getArguments();
		Integer id = bundle.getInt("id");
		
		dbQuery = new DatabaseQuery(getActivity());
		
		LinearLayout layout = new LinearLayout(getActivity());
		layout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		layout.setOrientation(LinearLayout.VERTICAL);
		
		if (id == 0) {
			Toast.makeText(getActivity(), "id is empty!!", Toast.LENGTH_SHORT).show();
		} else {
			HttpRequest request = dbQuery.getRequest(id);
			layout.addView(getRequestTextView(request));
			layout.addView(getRequestHeaderTable(request));
			layout.addView(getRequestBodyTitleTextView());
			layout.addView(getRequestBodyTextView(request));
			
			fragmentView.addView(layout);
		}
		return fragmentView;
	}
	
	public TextView getRequestTextView(HttpRequest httpRequest) {
		TextView requestText = new TitleText(getActivity());
		
		StringBuilder text = new StringBuilder()
		.append(httpRequest.getMethod()).append(BLANK)
		.append(httpRequest.getUrl());
		
		if (httpRequest.getPort() == 80){
			text.append(BLANK);
		} else {
			text.append(":").append(httpRequest.getPort()).append(BLANK);
		}
		
		text.append(httpRequest.getProtocol()).append(BLANK)
		.append(httpRequest.getProtocolVersion());
		
		requestText.setText(text);
		return requestText;
	}
	
	public TableLayout getRequestHeaderTable(HttpRequest request) {
		TableLayout tableLayout = new TableLayout(getActivity());
		tableLayout.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
		//tableLayout.setColumnStretchable(1, true);

		for (Map.Entry<String, String> entry : request.getHeader().entrySet()) {
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

	public TextView getRequestBodyTitleTextView() {
		TextView bodyText = new TitleText(getActivity());
		bodyText.setText("BODY");
		return bodyText;
	}
	
	public TextView getRequestBodyTextView(HttpRequest request) {
		TextView bodyText = new WhiteText(getActivity());
		bodyText.setText(request.getBody());
		return bodyText;
	}
}
