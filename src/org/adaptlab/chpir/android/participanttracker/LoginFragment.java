package org.adaptlab.chpir.android.participanttracker;

import org.adaptlab.chpir.android.activerecordcloudsync.ActiveRecordCloudSync;
import org.adaptlab.chpir.android.activerecordcloudsync.NetworkNotificationUtils;
import org.adaptlab.chpir.android.participanttracker.tasks.ApkUpdateTask;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Patterns;
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
		mEmailAddress.setHint(R.string.enter_email);
		mPassword = (EditText) view.findViewById(R.id.txt_password);
		mPassword.setHint(R.string.enter_password);
		
		mLoginButton = (Button) view.findViewById(R.id.remote_login_button);
		mLoginButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	if (isValidEmail(mEmailAddress.getText()) && passwordExists(mPassword.getText())) {
            		new RemoteAuthenticationTask().execute();
            	} else {
               		new AlertDialog.Builder(getActivity())
    				.setMessage(R.string.invalid_email_or_password)
    				.setPositiveButton(R.string.okay, new DialogInterface.OnClickListener() { 
    					public void onClick(DialogInterface dialog, int button) {}
    				}).show();
            	} 
            }
		});
		
		return view;
	}
	
	private boolean isValidEmail(CharSequence target) {
	    if (target == null) 
	        return false;
	    return Patterns.EMAIL_ADDRESS.matcher(target).matches();
	}
	
	private boolean passwordExists(CharSequence password) {
		if (TextUtils.isEmpty(password))
			return false;
		return true;
	}
	
	private class RemoteAuthenticationTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			if (NetworkNotificationUtils.checkForNetworkErrors(getActivity())) {
				ActiveRecordCloudSync.authenticateUser(mEmailAddress.getText().toString(), mPassword.getText().toString());
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void param) {
			if (ActiveRecordCloudSync.getAuthToken() != null) {
				new ApkUpdateTask(getActivity()).execute();
			} else {
				new AlertDialog.Builder(getActivity())
				.setMessage(R.string.email_password_mismatch)
				.setPositiveButton(R.string.okay, new DialogInterface.OnClickListener() { 
					public void onClick(DialogInterface dialog, int button) {}
				}).show();
			}
		}      
	}
	
}
