package org.adaptlab.chpir.android.participanttracker.models;

import android.util.Log;

import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import org.adaptlab.chpir.android.activerecordcloudsync.SendReceiveModel;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.UUID;

@Table(name = "ParticipantProperty")
public class ParticipantProperty extends SendReceiveModel {
    private static final String TAG = "ParticipantProperty";
    
    @Column(name = "SentToRemote")
    private boolean mSent;
    @Column(name = "Participant", onUpdate = Column.ForeignKeyAction.CASCADE, onDelete = Column.ForeignKeyAction.CASCADE)
    private Participant mParticipant;
    @Column(name = "Property", onUpdate = Column.ForeignKeyAction.CASCADE, onDelete = Column.ForeignKeyAction.CASCADE)
    private Property mProperty;
    @Column(name = "Value")
    private String mValue;
    @Column(name = "UUID")
    private String mUUID;
    @Column(name = "RemoteId", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    private Long mRemoteId;

    public ParticipantProperty() {
        super();
        mSent = false;
        mUUID = UUID.randomUUID().toString();
    }
    
    public ParticipantProperty(Participant participant, Property property, String value) {
        super();
        mSent = false;
        mParticipant = participant;
        mProperty = property;
        mValue = value;
        mUUID = UUID.randomUUID().toString();
    }
    
    @Override
    public boolean isPersistent() {
        return true;
    }
    
    private void setParticipant(Participant participant) {
    	mParticipant = participant;
    }

    @Override
    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        
        try {
            JSONObject jsonObject = new JSONObject();
            if (getParticipant() != null)
            	jsonObject.put("participant_uuid", getParticipant().getUUID());
            if (getProperty() != null)
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
        save();
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
    
    public void setValue(String value) {
    	mValue = value;
    }
    
    public void setRemoteId(Long id) {
    	mRemoteId = id;
    }
    
    @Override
    public Long getRemoteId() {
    	return mRemoteId;
    }
    
    public static ParticipantProperty findByRemoteId(Long id) {
    	return new Select().from(ParticipantProperty.class).where("RemoteId = ?", id).executeSingle();
    }
    
    public static ParticipantProperty findByUUID(String uuid) {
    	return new Select().from(ParticipantProperty.class).where("UUID = ?", uuid).executeSingle();
    }
    
    public static List<ParticipantProperty> getAllByParticipant(Participant participant) {
    	return new Select().from(ParticipantProperty.class).where("Participant = ?", participant.getId()).execute();
    }

	@Override
	public void createObjectFromJSON(JSONObject jsonObject) {
		try {
			String uuid = jsonObject.getString("uuid");
			ParticipantProperty participantProperty = ParticipantProperty.findByUUID(uuid);
			if (participantProperty == null) {
				participantProperty = this;
			}
			participantProperty.setUUID(uuid);
			Long remoteId = jsonObject.getLong("id");
			participantProperty.setRemoteId(remoteId);
			Participant participant = Participant.findByUUID(jsonObject.getString("participant_uuid"));
			if (participant != null) {
				participantProperty.setParticipant(participant);
			}
			
			Property property = Property.findByRemoteId(jsonObject.getLong("property_id"));
			
			if (property != null) {
				participantProperty.setProperty(property);
			}
			participantProperty.setValue(jsonObject.getString("value"));
			if (jsonObject.isNull("deleted_at")) {
				participantProperty.save();
			} else {
				ParticipantProperty pp = ParticipantProperty.findByUUID(uuid);
				if (pp != null) {
					pp.delete();
				}
			}
			
		} catch(JSONException je) {
			Log.e(TAG, "Error parsing object json", je);
		}
	}
}
