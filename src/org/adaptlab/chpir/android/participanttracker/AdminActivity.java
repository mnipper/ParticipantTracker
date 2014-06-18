package org.adaptlab.chpir.android.participanttracker;

import android.support.v4.app.Fragment;

public class AdminActivity extends SingleFragmentActivity {

	@Override
	protected Fragment createFragment() {
		return new AdminFragment();
	}

}
