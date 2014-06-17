package org.adaptlab.chpir.android.participanttracker.models;

import java.util.UUID;

import org.adaptlab.chpir.android.activerecordcloudsync.SendReceiveModel;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

@Table(name = "ParticipantProperty")
public class ParticipantProperty extends SendReceiveModel {
    private static final String TAG = "ParticipantProperty";
    
    @Column(name = "SentToRemote")
    private boolean mSent;
    @Column(name = "Participant")
    private Participant mParticipant;
    @Column(name = "Property")
    private Property mProperty;
    @Column(name = "Value")
    private String mValue;
    @Column(name = "UUID")
    private String mUUID;
    @Column(name = "RemoteId", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    private Long mRemoteId;
    
    public ParticipantProperty() {
        super();
    }
    
    public ParticipantProperty(Participant participant, Property property, String value) {
        super();
        mSent = false;
        mParticipant = participant;
        mProperty = property;
        mValue = value;
        mUUID = UUID.randomUUID().toString();
    }
    
    private void setParticipant(Participant participant) {
    	mParticipant = participant;
    }

    @Override
    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("participant_uuid", getParticipant().getUUID());
            jsonObject.put("property_id", getProperty().getRemoteId());
            jsonObject.put("value", getValue());
            jsonObject.put("uuid", getUUID());
            
            json.put("participant_property", jsonObject);
        } catch (JSONException je) {
            Log.e(TAG, "JSON exception", je);
        }
        return json;
    }
    
    public String getUUID() {
        return mUUID;
    }
    
    public void setUUID(String uuid) {
    	mUUID = uuid;
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
    
    private void setProperty(Property property) {
    	mProperty = property;
    }
    
    public String getValue() {
        return mValue;
    }
    
    private void setValue(String value) {
    	mValue = value;
    }
    
    public void setRemoteId(Long id) {
    	mRemoteId = id;
    }
    
    public Long getRemoteId() {
    	return mRemoteId;
    }
    
    public static ParticipantProperty findById(Long id) {
    	return new Select().from(ParticipantProperty.class).where("RemoteId = ?", id).executeSingle();
    }

	@Override
	public void createObjectFromJSON(JSONObject jsonObject) {
		try {
			Long remoteId = jsonObject.getLong("id");
			ParticipantProperty participantProperty = ParticipantProperty.findById(remoteId);
			if (participantProperty == null) {
				participantProperty = this;
			}
			participantProperty.setRemoteId(remoteId);
			participantProperty.setUUID(jsonObject.getString("uuid"));
			Participant participant = Participant.findByUUID(jsonObject.getString("participant_uuid"));
			if (participant != null) {
				participantProperty.setParticipant(participant);
			}
			Property property = Property.findByRemoteId(jsonObject.getLong("property_id"));
			if (property != null) {
				participantProperty.setProperty(property);
			}
			participantProperty.setValue(jsonObject.getString("value"));
			participantProperty.save();
			
		} catch(JSONException je) {
			Log.e(TAG, "Error parsing object json", je);
		}
	}
}
