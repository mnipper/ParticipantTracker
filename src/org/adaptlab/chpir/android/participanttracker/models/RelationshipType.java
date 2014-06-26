package org.adaptlab.chpir.android.participanttracker.models;

import org.adaptlab.chpir.android.activerecordcloudsync.ReceiveModel;
import org.json.JSONException;
import org.json.JSONObject;

import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import android.util.Log;

@Table(name = "RelationshipType")
public class RelationshipType extends ReceiveModel {
    private static final String TAG = "RelationshipType";

    @Column(name = "RemoteId", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    private Long mRemoteId;
    @Column(name = "Label")
    private String mLabel;
    @Column(name = "OwnerParticipantType")
    private ParticipantType mOwnerParticipantType;
    @Column(name = "RelatedParticipantType")
    private ParticipantType mRelatedParticipantType;

    public RelationshipType() {
        super();
    }
    
    public RelationshipType(String label, ParticipantType owner, ParticipantType related) {
        super();
        setLabel(label);
        setOwnerParticipantType(owner);
        setRelatedParticipantType(related);
    }
    
    @Override
    public void createObjectFromJSON(JSONObject jsonObject) {
        try {
            Long remoteId = jsonObject.getLong("id");
            RelationshipType relationshipType = RelationshipType.findByRemoteId(remoteId);
            if (relationshipType == null) {
                relationshipType = this;
            }
            
            Log.i(TAG, "Creating object from JSON Object: " + jsonObject);
            relationshipType.setRemoteId(remoteId);
            relationshipType.setOwnerParticipantType(ParticipantType.findByRemoteId(jsonObject.getLong("participant_type_owner_id")));
            relationshipType.setRelatedParticipantType(ParticipantType.findByRemoteId(jsonObject.getLong("participant_type_related_id")));
            relationshipType.setLabel(jsonObject.getString("label"));
            if (jsonObject.isNull("deleted_at")) {
                relationshipType.save();
            } else {
                RelationshipType rt = RelationshipType.findByRemoteId(remoteId);
                if (rt != null) {
                    rt.delete();
                }
            }
            
        } catch (JSONException je) {
            Log.e(TAG, "Error parsing object json", je);
        }  
    }
    
    public static RelationshipType findByRemoteId(Long id) {
        return new Select().from(RelationshipType.class).where("RemoteId = ?", id).executeSingle();
    }
    
    
    public Long getRemoteId() {
        return mRemoteId;
    }

    private void setRemoteId(Long remoteId) {
        mRemoteId = remoteId;
    }

    public String getLabel() {
        return mLabel;
    }

    private void setLabel(String label) {
        mLabel = label;
    }

    public ParticipantType getOwnerParticipantType() {
        return mOwnerParticipantType;
    }

    private void setOwnerParticipantType(ParticipantType ownerParticipantType) {
        mOwnerParticipantType = ownerParticipantType;
    }

    public ParticipantType getRelatedParticipantType() {
        return mRelatedParticipantType;
    }

    private void setRelatedParticipantType(ParticipantType relatedParticipantType) {
        mRelatedParticipantType = relatedParticipantType;
    }
}
