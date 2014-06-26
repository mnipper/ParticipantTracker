package org.adaptlab.chpir.android.participanttracker.models;

import java.util.UUID;

import org.adaptlab.chpir.android.activerecordcloudsync.SendReceiveModel;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.activeandroid.annotation.Column;
import com.activeandroid.query.Select;

public class Relationship extends SendReceiveModel {
    private static final String TAG = "Relationship";
    
    @Column(name = "RemoteId", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    private Long mRemoteId;
    @Column(name = "SentToRemote")
    private boolean mSent;
    @Column(name = "ParticpantOwner")
    private Participant mParticipantOwner;
    @Column(name = "ParticipantRelated")
    private Participant mParticipantRelated;
    @Column(name = "UUID")
    private String mUUID;
    
    public Relationship() {
        super();
        mUUID = UUID.randomUUID().toString();
    }

    @Override
    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        
        try {
            JSONObject jsonObject = new JSONObject();
            if (getParticipantOwner() != null)
                jsonObject.put("participant_owner_uuid", getParticipantOwner().getUUID());
            if (getParticipantRelated() != null)
                jsonObject.put("participant_related_uuid", getParticipantRelated().getUUID());

            json.put("relationship", jsonObject);
        } catch (JSONException je) {
            Log.e(TAG, "JSON exception", je);
        }
        return json;
    }
    
    @Override
    public void createObjectFromJSON(JSONObject jsonObject) {
        try {
            String uuid = jsonObject.getString("uuid");
            Relationship relationship = Relationship.findByUUID(uuid);
            if (relationship == null) {
                relationship = this;
            }
            relationship.setUUID(uuid);
            Long remoteId = jsonObject.getLong("id");
            relationship.setRemoteId(remoteId);
            Participant participantOwner = Participant.findByUUID(jsonObject.getString("participant_owner_uuid"));
            if (participantOwner != null) {
                relationship.setParticipantOwner(participantOwner);
            }
            
            Participant participantRelated = Participant.findByUUID(jsonObject.getString("participant_related_uuid"));
            if (participantRelated != null) {
                relationship.setParticipantRelated(participantRelated);
            }

            if (jsonObject.isNull("deleted_at")) {
                relationship.save();
            } else {
                Relationship rs = Relationship.findByUUID(uuid);
                if (rs != null) {
                    rs.delete();
                }
            }
            
        } catch(JSONException je) {
            Log.e(TAG, "Error parsing object json", je);
        }
    }
    
    public static Relationship findByRemoteId(Long id) {
        return new Select().from(Relationship.class).where("RemoteId = ?", id).executeSingle();
    }
    
    public static Relationship findByUUID(String uuid) {
        return new Select().from(Relationship.class).where("UUID = ?", uuid).executeSingle();
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

    @Override
    public Long getRemoteId() {
        return mRemoteId;
    }
    
    private void setRemoteId(Long remoteId) {
        mRemoteId = remoteId;
    }
    
    public Participant getParticipantOwner() {
        return mParticipantOwner;
    }

    public void setParticipantOwner(Participant participantOwner) {
        mParticipantOwner = participantOwner;
    }

    public Participant getParticipantRelated() {
        return mParticipantRelated;
    }

    public void setParticipantRelated(Participant participantRelated) {
        mParticipantRelated = participantRelated;
    }

    public String getUUID() {
        return mUUID;
    }

    private void setUUID(String uUID) {
        mUUID = uUID;
    }
}
