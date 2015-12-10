package com.crystal.httpdebugger.ui;

import com.crystal.httpdebugger.R;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ResponseFragment extends Fragment {
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_response, container, false);
	}
}
