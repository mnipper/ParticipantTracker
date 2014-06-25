package org.adaptlab.chpir.android.participanttracker;

import org.adaptlab.chpir.android.activerecordcloudsync.ActiveRecordCloudSync;
import org.adaptlab.chpir.android.participanttracker.models.AdminSettings;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class AdminFragment extends Fragment {
	private EditText mDeviceIdentifierEditText;
    private EditText mApiEndPointEditText;
    private TextView mLastUpdateTextView;
    private TextView mBackendApiKeyTextView;
    private TextView mVersionCodeTextView;
    private Button mSaveButton;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_admin_settings, parent, false);
		mDeviceIdentifierEditText = (EditText) view.findViewById(R.id.device_identifier_edit_text);
        mDeviceIdentifierEditText.setText(getAdminSettingsInstanceDeviceId());
        
        mApiEndPointEditText = (EditText) view.findViewById(R.id.api_endpoint_edit_text);
        mApiEndPointEditText.setText(getAdminSettingsInstanceApiUrl());
        
        mLastUpdateTextView = (TextView) view.findViewById(R.id.last_update_label);
        mLastUpdateTextView.setText(mLastUpdateTextView.getText().toString() + getLastUpdateTime());
        
        mBackendApiKeyTextView = (TextView) view.findViewById(R.id.backend_api_key_label);
        mBackendApiKeyTextView.setText(getString(R.string.api_key_label) + getString(R.string.backend_api_key));
        
        mVersionCodeTextView = (TextView) view.findViewById(R.id.version_code_label);
        mVersionCodeTextView.setText(getString(R.string.version_code) + AppUtil.getVersionCode(getActivity()));
        
        mSaveButton = (Button) view.findViewById(R.id.save_admin_settings_button);
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                AdminSettings.getInstance().setDeviceIdentifier(mDeviceIdentifierEditText.getText().toString());              
                AdminSettings.getInstance().setApiUrl(mApiEndPointEditText.getText().toString());
                ActiveRecordCloudSync.setEndPoint(getAdminSettingsInstanceApiUrl());
                getActivity().finish();
            }
        });
		return view;
	}
	
	public String getAdminSettingsInstanceDeviceId() {
		return AdminSettings.getInstance().getDeviceIdentifier();
	}
	
	public String getLastUpdateTime() {
		return AdminSettings.getInstance().getLastUpdateTime();
	}
	
	public String getAdminSettingsInstanceApiUrl() {
		return AdminSettings.getInstance().getApiUrl();
	}

	public String getAdminSettingsInstanceSyncInterval() {
		return String.valueOf(AdminSettings.getInstance().getSyncIntervalInMinutes());
	}
	
}
