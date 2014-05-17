// Copyright 2007-2013 metaio GmbH. All rights reserved.
package com.metaio.ar;

import com.isee.R;
import android.view.View;

import com.metaio.sdk.ARELActivity;

public class ARELViewActivity extends ARELActivity {

	@Override
	protected int getGUILayout() {
		// Attaching layout to the activity
		return R.layout.activity_ar;
	}

	public void onButtonClick(View v) {
		finish();
	}

}
