package org.adaptlab.chpir.android.models;

import org.adaptlab.chpir.android.activerecordcloudsync.SendModel;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

@Table(name = "ParticipantProperty")
public class ParticipantProperty extends SendModel {
    private static final String TAG = "ParticipantProperty";
    
    @Column(name = "SentToRemote")
    private boolean mSent;
    @Column(name = "Participant")
    private Participant mParticipant;
    @Column(name = "Property")
    private Property mProperty;
    @Column(name = "Value")
    private String mValue;
    
    public ParticipantProperty(Participant participant, Property property, String value) {
        super();
        mSent = false;
        mParticipant = participant;
        mProperty = property;
        mValue = value;
    }

    @Override
    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        
        try {
            JSONObject jsonObject = new JSONObject();
            // TODO: Change to participant id
            jsonObject.put("participant_id", getParticipant());
            jsonObject.put("property_id", getProperty().getRemoteId());
            jsonObject.put("value", getValue());
            
            json.put("participant_property", jsonObject);
        } catch (JSONException je) {
            Log.e(TAG, "JSON exception", je);
        }
        return json;
    }

    @Override
    public boolean isSent() {
        return mSent;
    }

    @Override
    public boolean readyToSend() {
        return true;
    }

    @Override
    public void setAsSent() {
        mSent = true;
    }
    
    public Participant getParticipant() {
        return mParticipant;
    }
    
    public Property getProperty() {
        return mProperty;
    }
    
    public String getValue() {
        return mValue;
    }
}
