package org.adaptlab.chpir.android.participanttracker.models;

import java.util.Locale;
import java.util.TimeZone;

import org.adaptlab.chpir.android.activerecordcloudsync.SendReceiveModel;
import org.adaptlab.chpir.android.participanttracker.AppUtil;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class DeviceSyncEntry extends SendReceiveModel {
    private static final String TAG = "DeviceSyncEntry";

    public DeviceSyncEntry() { }

    @Override
    public JSONObject toJSON() {
        Log.i(TAG, "Creating JSON for " + TAG);
        JSONObject json = new JSONObject();

        try {
            JSONObject jsonObject = new JSONObject();

            jsonObject.put("current_version", AppUtil.getVersionCode(AppUtil.getContext()));
            jsonObject.put("current_language", Locale.getDefault().getDisplayLanguage());
            jsonObject.put("device_uuid", AdminSettings.getInstance().getDeviceIdentifier());
            jsonObject.put("api_key", AdminSettings.getInstance().getApiKey());
            jsonObject.put("timezone", TimeZone.getDefault().getDisplayName() + " " + TimeZone.getDefault().getID());

            json.put("device_sync_entry", jsonObject);
        } catch (JSONException je) {
            Log.e(TAG, "JSON exception", je);
        }

        return json;
    }

    @Override
    public boolean isSent() { return false; }

    @Override
    public boolean readyToSend() { return true; }

    @Override
    public void setAsSent() { }

    @Override
    public boolean isPersistent() { return false; }

    @Override
    public void createObjectFromJSON(JSONObject jsonObject) { }

    @Override
    public Long getRemoteId() {
        return null;
    }
}
