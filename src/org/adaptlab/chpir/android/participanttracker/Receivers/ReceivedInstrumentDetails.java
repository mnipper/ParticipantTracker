package org.adaptlab.chpir.android.participanttracker.Receivers;

import org.json.JSONException;
import org.json.JSONObject;

public class ReceivedInstrumentDetails {
    private static final String TAG = "ReceivedInstrumentDetails";
    private String mTitle;
    private long mId;
    private String mParticipantType;
    
    public String getTitle() {
        return mTitle;
    }
    
    public void setTitle(String title) {
        mTitle = title;
    }
    
    public long getId() {
        return mId;
    }
    
    public void setId(long id) {
        mId = id;
    }
    
    public String getParticipantType() {
        JSONObject json = null;
        try {
            json = new JSONObject(mParticipantType);
            return json.getString("participant_type");
        } catch (JSONException e) {
            return "";
        }
    }
    
    public void setParticipantType(String participantType) {
        mParticipantType = participantType;
    }
    
    public String toString() {
        return getTitle();
    }
}
