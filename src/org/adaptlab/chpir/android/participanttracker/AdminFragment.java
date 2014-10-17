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
    private TextView mDeviceLabelTextView;
    private EditText mDeviceLabelEditText;
    private EditText mApiEndPointEditText;
    private TextView mLastUpdateTextView;
    private TextView mVersionCodeTextView;
    private EditText mApiVersionEditText;
    private EditText mApiKeyEditText;
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
        
        mDeviceLabelEditText = (EditText) view.findViewById(R.id.device_label_edit_text);
        mDeviceLabelEditText.setText(AdminSettings.getInstance().getDeviceLabel());
        
        mApiEndPointEditText = (EditText) view.findViewById(R.id.api_endpoint_edit_text);
        mApiEndPointEditText.setText(getAdminSettingsInstanceApiDomainName());
        
        mApiVersionEditText = (EditText) view.findViewById(R.id.api_version_text);
        mApiVersionEditText.setText(getAdminSettingsInstanceApiVersion());
        
        mApiKeyEditText = (EditText) view.findViewById(R.id.api_key_text);
        mApiKeyEditText.setText(getAdminSettingsInstanceApiKey());
        
        mLastUpdateTextView = (TextView) view.findViewById(R.id.last_update_label);
        mLastUpdateTextView.setText(mLastUpdateTextView.getText().toString() + getLastUpdateTime());
         
        mVersionCodeTextView = (TextView) view.findViewById(R.id.version_code_label);
        mVersionCodeTextView.setText(getString(R.string.version_code) + AppUtil.getVersionCode(getActivity()));
        
        mSaveButton = (Button) view.findViewById(R.id.save_admin_settings_button);
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                AdminSettings.getInstance().setDeviceIdentifier(mDeviceIdentifierEditText.getText().toString());              
                AdminSettings.getInstance().setApiUrl(setApiUrl(mApiEndPointEditText.getText().toString()));
                AdminSettings.getInstance().setApiVersion(mApiVersionEditText.getText().toString());
                AdminSettings.getInstance().setApiKey(mApiKeyEditText.getText().toString());
                AdminSettings.getInstance().setDeviceLabel(mDeviceLabelEditText.getText().toString());
                ActiveRecordCloudSync.setAccessToken(getAdminSettingsInstanceApiKey());
                ActiveRecordCloudSync.setEndPoint(getAdminSettingsInstanceApiUrl());
                getActivity().finish();
            }

			private String setApiUrl(String string) {
			    char lastChar = string.charAt(string.length() - 1);
			    if (lastChar != '/') string = string + "/";
			    return string;
			}
        });
		return view;
	}
	
	public String getAdminSettingsInstanceApiDomainName() {
		return AdminSettings.getInstance().getApiUrl();
	}

	public String getAdminSettingsInstanceApiKey() {
		return AdminSettings.getInstance().getApiKey();
	}

	public String getAdminSettingsInstanceDeviceId() {
		return AdminSettings.getInstance().getDeviceIdentifier();
	}
	
	public String getLastUpdateTime() {
		return AdminSettings.getInstance().getLastUpdateTime();
	}
	
	private String getAdminSettingsInstanceApiUrl() {
	    String domainName = AdminSettings.getInstance().getApiUrl();
		return domainName + "api/" + AdminSettings.getInstance().getApiVersion() + "/" ;
	}

	public String getAdminSettingsInstanceSyncInterval() {
		return String.valueOf(AdminSettings.getInstance().getSyncIntervalInMinutes());
	}
	
	public String getAdminSettingsInstanceApiVersion() {
		return AdminSettings.getInstance().getApiVersion();
	}
	
}
