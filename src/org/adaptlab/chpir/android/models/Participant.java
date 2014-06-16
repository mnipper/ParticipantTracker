package org.adaptlab.chpir.android.models;

import java.util.List;
import java.util.UUID;

import org.adaptlab.chpir.android.activerecordcloudsync.SendReceiveModel;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

@Table(name = "Participant")
public class Participant extends SendReceiveModel {
    private static final String TAG = "Participant";
    
    @Column(name = "SentToRemote")
    private boolean mSent;
    @Column(name = "ParticipantType")
    private ParticipantType mParticipantType;
    @Column(name = "UUID")
    private String mUUID;
    @Column(name = "RemoteId", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    private Long mRemoteId;
    
    public Participant() {
        super();
    }
    
    public Participant(ParticipantType participantType) {
        super();
        mParticipantType = participantType;
        mUUID = UUID.randomUUID().toString();
        mSent = false;
    }

    @Override
    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        
        try {
            JSONObject jsonObject = new JSONObject();
            // TODO: Change to participant id
            jsonObject.put("participant_type_id", getParticipantType().getRemoteId());
            jsonObject.put("uuid", getUUID());
            
            json.put("participant", jsonObject);
        } catch (JSONException je) {
            Log.e(TAG, "JSON exception", je);
        }
        return json;
    }
    
    public ParticipantType getParticipantType() {
        return mParticipantType;
    }
    
    public void setParticipantType(ParticipantType participantType) {
    	mParticipantType = participantType;
    }
    
    public String getUUID() {
        return mUUID;
    }
    
    public void setUUID(String uuid) {
    	mUUID = uuid;
    }
    
    public static List<Participant> getAll() {
        return new Select().from(Participant.class).orderBy("Id ASC").execute();
    }
    
    public static List<Participant> getAllByParticipantType(ParticipantType participantType) {
        return new Select().from(Participant.class).where("ParticipantType = ?", participantType.getId()).execute();
    }
    
    public static int getCount() {
        return getAll().size();
    }
    
    public static int getCountByParticipantType(ParticipantType participantType) {
        return getAllByParticipantType(participantType).size();
    }
    
    public static Participant findById(Long id) {
        return new Select().from(Participant.class).where("Id = ?", id).executeSingle();
    }
    
    public static Participant findByUUID(String uuid) {
    	return new Select().from(Participant.class).where("UUID = ?", uuid).executeSingle();
    }
    
    public List<ParticipantProperty> getParticipantProperties() {
        return new Select().from(ParticipantProperty.class).where("Participant = ?", getId()).execute();
    }

    @Override
    public boolean isSent() {
        return mSent;
    }

    @Override
    public boolean readyToSend() {
        return false;
    }

    @Override
    public void setAsSent() {
        mSent = true;
    }
    
    public String getLabel() {
        for (ParticipantProperty participantProperty : getParticipantProperties()) {
            if (participantProperty.getProperty() == getParticipantType().getLabelProperty()) {
                return participantProperty.getValue();
            }
        }
        
        return mUUID;
    }

	@Override
	public void createObjectFromJSON(JSONObject jsonObject) {
		try {
			Long remoteId = jsonObject.getLong("id");
			Participant participant = Participant.findById(remoteId);
			if (participant == null) {
				participant = this;
			}
			participant.setRemoteId(remoteId);
			participant.setUUID(jsonObject.getString("uuid"));
			Long participantTypeId = jsonObject.getLong("participant_type_id");
			ParticipantType participantType = ParticipantType.findById(participantTypeId);
			if (participantType != null) {
				participant.setParticipantType(participantType);
			}
			participant.save();
		} catch (JSONException je) {
			Log.e(TAG, "Error parsing object json", je);
		}
		
	}
	
	private void setRemoteId(Long id) {
		mRemoteId = id;
	}
	
	public Long getRemoteId() {
		return mRemoteId;
	}
	
}
