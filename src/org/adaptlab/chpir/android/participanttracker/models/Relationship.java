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

@Table(name = "Relationship")
public class Relationship extends SendReceiveModel {
    private static final String TAG = "Relationship";
    
    @Column(name = "RemoteId", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    private Long mRemoteId;
    @Column(name = "SentToRemote")
    private boolean mSent;
    @Column(name = "ParticpantOwner", onUpdate = Column.ForeignKeyAction.CASCADE, onDelete = Column.ForeignKeyAction.CASCADE)
    private Participant mParticipantOwner;
    @Column(name = "ParticipantRelated", onUpdate = Column.ForeignKeyAction.CASCADE, onDelete = Column.ForeignKeyAction.CASCADE)
    private Participant mParticipantRelated;
    @Column(name = "UUID")
    private String mUUID;
    @Column(name = "RelationshipType")
    private RelationshipType mRelationshipType;

    public Relationship() {
        super();
    }
    
    public Relationship(RelationshipType relationshipType) {
        super();
        mUUID = UUID.randomUUID().toString();
        mRelationshipType = relationshipType;
    }
    
    @Override
    public boolean isPersistent() {
        return true;
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
            jsonObject.put("relationship_type_id", getRelationshipType().getRemoteId());
            jsonObject.put("uuid", getUUID());

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
            
            relationship.setRelationshipType(RelationshipType.findByRemoteId(jsonObject.getLong("relationship_type_id")));

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
    
    public static List<Relationship> getAll() {
        return new Select().from(Relationship.class).orderBy("Id ASC").execute();
    }
    
    public static List<Relationship> getAllByParticipant(Participant participant) {
    	return new Select().from(Relationship.class).where("ParticpantOwner = ? OR ParticipantRelated = ?", participant.getId(), participant.getId()).execute();
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

    public RelationshipType getRelationshipType() {
        return mRelationshipType;
    }

    public void setRelationshipType(RelationshipType relationshipType) {
        mRelationshipType = relationshipType;
    }

}