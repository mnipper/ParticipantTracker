package org.adaptlab.chpir.android.participanttracker.models;

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
    @Column(name = "ParticipantType", onUpdate = Column.ForeignKeyAction.CASCADE, onDelete = Column.ForeignKeyAction.CASCADE)
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
        return new Select().from(Participant.class).where("ParticipantType = ?", participantType.getId()).orderBy("Id DESC").execute();
    }
    
    public static List<Participant> getAllByParticipantType(ParticipantType participantType, String query) {
        return new Select("Participant.*")
                .distinct()
                .from(Participant.class)
                .innerJoin(ParticipantProperty.class)
                .on("Participant.Id = ParticipantProperty.Participant AND Participant.ParticipantType = ? AND ParticipantProperty.Value LIKE ?",
                        participantType.getId(),
                        "%" + query + "%")
                .orderBy("Participant.Id DESC")
                .execute();
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
    
    public static Participant findByRemoteId(Long remoteId) {
    	return new Select().from(Participant.class).where("RemoteId = ?", remoteId).executeSingle();
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
        return true; //TODO: For Testing...FIX - also in ParticipantProperty
    }

    @Override
    public void setAsSent() {
        mSent = true;
    }
    
    public List<Property> getProperties() {
        return Property.getAllByParticipantType(getParticipantType());
    }
    
    public String getLabel() {
        for (ParticipantProperty participantProperty : getParticipantProperties()) {
            if (participantProperty.getProperty().getUseAsLabel()) {
                return participantProperty.getValue();
            }
        } 
        
        return mUUID;
    }

	@Override
	public void createObjectFromJSON(JSONObject jsonObject) {
		try {
			String uuid = jsonObject.getString("uuid");
			Participant participant = Participant.findByUUID(uuid);
			if (participant == null) {
				participant = this;
			}
			participant.setUUID(uuid);
			Long remoteId = jsonObject.getLong("id");
			participant.setRemoteId(remoteId);
			Long participantTypeId = jsonObject.getLong("participant_type_id");
			ParticipantType participantType = ParticipantType.findByRemoteId(participantTypeId); 
			if (participantType != null) {
				participant.setParticipantType(participantType);
			}
			if (jsonObject.isNull("deleted_at")) {
				participant.save();
			} else {
				Log.i(TAG, "deleted participant: " + jsonObject.toString());
				Participant p = Participant.findByUUID(uuid);
				if (p != null) {
					p.delete();
				}
			}
		} catch (JSONException je) {
			Log.e(TAG, "Error parsing object json", je);
		}
		
	}
	
	private void setRemoteId(Long id) {
		mRemoteId = id;
	}
	
	@Override
	public Long getRemoteId() {
		return mRemoteId;
	}
	
	public String getMetadata() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("participant_uuid", getUUID());
        jsonObject.put("participant_type", getParticipantType().getLabel());
        return jsonObject.toString();
	}
}
