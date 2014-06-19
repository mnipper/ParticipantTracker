package org.adaptlab.chpir.android.participanttracker;

import org.adaptlab.chpir.android.activerecordcloudsync.ActiveRecordCloudSync;
import org.adaptlab.chpir.android.activerecordcloudsync.NetworkNotificationUtils;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class LoginFragment extends Fragment {

	protected static final String TAG = "LoginFragment";
	private EditText mEmailAddress;
	private EditText mPassword;
	private Button mLoginButton;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_login, parent, false);
		
		view.findViewById(R.id.login_label);
		mEmailAddress = (EditText) view.findViewById(R.id.txt_email);
		mEmailAddress.setHint("Enter Email");
		mPassword = (EditText) view.findViewById(R.id.txt_password);
		mPassword.setHint("Enter Password");
		
		mLoginButton = (Button) view.findViewById(R.id.remote_login_button);
		mLoginButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	if (mEmailAddress.getText().toString() == null || mPassword.getText().toString() == null) {
            		new AlertDialog.Builder(getActivity().getApplicationContext())
    				.setMessage(R.string.email_password_blank)
    				.setPositiveButton(R.string.okay, new DialogInterface.OnClickListener() { 
    					public void onClick(DialogInterface dialog, int button) {}
    				}).show();
            	} else {
            		new RemoteAuthenticationTask().execute();
	            	getActivity().finish();
            	}
            }
		});
		
		return view;
	}
	
	private class RemoteAuthenticationTask extends AsyncTask<Void, Void, Void> {
		private static final String TAG = "RemoteAuthenticationTask";

		@Override
		protected Void doInBackground(Void... params) {
			if (NetworkNotificationUtils.checkForNetworkErrors(getActivity())) {
				ActiveRecordCloudSync.authenticateUser(mEmailAddress.getText().toString(), mPassword.getText().toString());
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void param) {
			Log.i(TAG, "authenticated user and token is: " + ActiveRecordCloudSync.getAuthToken());
			if (ActiveRecordCloudSync.getAuthToken() != null) {
				new SyncTablesTask().execute();
			} else {
				AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
				dialog.setMessage(R.string.user_password_mismatch);
				dialog.setPositiveButton(R.string.okay, new DialogInterface.OnClickListener() { 
					public void onClick(DialogInterface dialog, int button) {}
				});
				dialog.create();
				dialog.show();
			}  
		}      
	}
	
	private class SyncTablesTask extends AsyncTask<Void, Void, Void> {		
        @Override
        protected Void doInBackground(Void... params) {
            if (NetworkNotificationUtils.checkForNetworkErrors(getActivity())) {
                ActiveRecordCloudSync.syncTables(getActivity());
            }
            return null;
        }      
    }
	
}
