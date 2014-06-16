package org.adaptlab.chpir.android.participanttracker.tasks;

import org.adaptlab.chpir.android.activerecordcloudsync.ActiveRecordCloudSync;
import org.adaptlab.chpir.android.activerecordcloudsync.NetworkNotificationUtils;

import android.content.Context;
import android.os.AsyncTask;

public class FetchDataTask extends AsyncTask<Void, Void, Void> {
	
	private Context mContext;

	public FetchDataTask(Context context) {
		mContext = context;
	}

	@Override
	protected Void doInBackground(Void... params) {
		if (NetworkNotificationUtils.checkForNetworkErrors(mContext)) {
			ActiveRecordCloudSync.syncReceiveTables(mContext);
			ActiveRecordCloudSync.syncFetchSendReceiveTables(mContext);
		}
		return null;
	}

}
