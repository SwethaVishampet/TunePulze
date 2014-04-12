package com.example.tunepulze;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * 
 */
public class MusicFragment extends Fragment {

	public MusicFragment() {
		// Required empty public constructor
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
//		TextView textView = new TextView(getActivity());
//		textView.setText(R.string.hello_blank_fragment);
//		return textView;
		
		return inflater.inflate(R.layout.music, container, false);
	}

}
