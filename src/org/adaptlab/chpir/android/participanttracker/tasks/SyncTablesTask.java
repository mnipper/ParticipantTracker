package org.adaptlab.chpir.android.participanttracker.tasks;

import org.adaptlab.chpir.android.activerecordcloudsync.ActiveRecordCloudSync;
import org.adaptlab.chpir.android.activerecordcloudsync.NetworkNotificationUtils;
import org.adaptlab.chpir.android.participanttracker.R;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

public class SyncTablesTask extends AsyncTask<Void, Void, Void> {		
	ProgressDialog mProgressDialog;
	private Context mContext;

	public SyncTablesTask(Context context) {
		mContext = context;
	}
	
	@Override
	protected void onPreExecute() {
		mProgressDialog = ProgressDialog.show(
				mContext, 
				mContext.getString(R.string.participants_loading_header), 
				mContext.getString(R.string.participants_loading_message)
		);
	}
	
	@Override
    protected Void doInBackground(Void... params) {
        if (NetworkNotificationUtils.checkForNetworkErrors(mContext)) {
            ActiveRecordCloudSync.syncTables(mContext);
        }
        return null;
    }
    
    @Override
	protected void onPostExecute(Void param) {
    	new LogoutUserTask(mContext).execute();
    	((Activity) mContext).setResult(Activity.RESULT_OK);
    	if (mProgressDialog.isShowing()) {
    	    mProgressDialog.dismiss();
    	}
    	((Activity) mContext).finish();
    }
}

class LogoutUserTask extends AsyncTask<Void, Void, Void> {		
	Context lcontext;
	
	public LogoutUserTask(Context context) {
		lcontext = context;
	}
	
	@Override
	protected Void doInBackground(Void... params) {
		if (NetworkNotificationUtils.checkForNetworkErrors(lcontext)) {
        	ActiveRecordCloudSync.logoutUser();
		}
		return null;
	}
}