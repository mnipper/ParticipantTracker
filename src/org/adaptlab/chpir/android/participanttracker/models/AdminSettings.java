package org.adaptlab.chpir.android.participanttracker.models;

import android.util.Log;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

@Table(name = "AdminSettings")
public class AdminSettings extends Model {
	@Column(name = "DeviceIdentifier")
    private String mDeviceIdentifier;
	@Column(name = "SyncInterval")
    private int mSyncInterval;
    @Column(name = "ApiUrl")
    private String mApiUrl;
    @Column(name="LastUpdateTime")
    private String mLastUpdateTime;
    @Column(name = "ApiVersion")
    private String mApiVersion;
    @Column(name = "ApiKey")
    private String mApiKey;
    @Column(name = "DeviceLabel")
    private String mDeviceLabel;
    
    private static final String TAG = "AdminSettings";
	private static AdminSettings adminSettings;

	public AdminSettings() {
        super();
	}
	
	public static AdminSettings getInstance() {
        adminSettings = new Select().from(AdminSettings.class).orderBy("Id asc").executeSingle();
        if (adminSettings == null) {
        	Log.i(TAG, "Creating new admin settings instance");
            adminSettings = new AdminSettings();
            adminSettings.save();
        }
        return adminSettings;
    }
	
   public void setDeviceIdentifier(String id) {
        mDeviceIdentifier = id;
        save();
    }
    
    public String getDeviceIdentifier() {
        return mDeviceIdentifier;
    }
    
    public int getSyncInterval() {
        return mSyncInterval;
    }
    
    public int getSyncIntervalInMinutes() {
        return mSyncInterval / (60 * 1000);
    }
    
    public void setSyncInterval(int interval) {
        Log.i(TAG, "Setting set interval: " + (interval * 1000 * 60));
        mSyncInterval = interval * 1000 * 60;
        save();
    }
    
    public void setApiUrl(String apiUrl) {
        Log.i(TAG, "Setting api endpoint: " + apiUrl);
        mApiUrl = apiUrl;
        save();
    }
    
    public String getApiUrl() {
        return mApiUrl;
    }
    
    public void setLastUpdateTime(String time) {
    	mLastUpdateTime = time;
    }
	
    public String getLastUpdateTime() {
    	return mLastUpdateTime;
    }
    
    public void setApiKey(String apiKey) {
    	mApiKey = apiKey;
    	save();
    }
    
    public String getApiKey() {
    	return mApiKey;
    }
    
    public void setApiVersion(String apiVersion) {
    	mApiVersion = apiVersion;
    	save();
    }
    
    public String getApiVersion() {
    	return mApiVersion;
    }
    
    public void setDeviceLabel(String deviceLabel) {
        mDeviceLabel = deviceLabel;
        save();
    }
    
    public String getDeviceLabel() {
        return mDeviceLabel;
    }
    
}
