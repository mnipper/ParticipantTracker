package org.adaptlab.chpir.android.participanttracker;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class LoginFragment extends Fragment {

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
            	//Send email and password here;
            	getActivity().finish();
            }
		});
		
		return view;
	}
}
