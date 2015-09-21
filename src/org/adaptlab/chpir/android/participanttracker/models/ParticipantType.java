package org.adaptlab.chpir.android.participanttracker.models;

import android.util.Log;

import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import org.adaptlab.chpir.android.activerecordcloudsync.ReceiveModel;
import org.adaptlab.chpir.android.participanttracker.AppUtil;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

@Table(name = "ParticipantType")
public class ParticipantType extends ReceiveModel {
    private static final String TAG = "ParticipantType";
    
    @Column(name = "Label")
    private String mLabel;
    @Column(name = "RemoteId", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    private Long mRemoteId;
    
    public ParticipantType() {
        super();
    }

    @Override
    public void createObjectFromJSON(JSONObject jsonObject) {
        try {
            Long remoteId = jsonObject.getLong("id");            
            ParticipantType participantType = ParticipantType.findByRemoteId(remoteId);
            if (participantType == null) {
                participantType = this;
            }
            
            Log.i(TAG, "Creating object from JSON Object: " + jsonObject);
            participantType.setLabel(jsonObject.getString("label"));
            participantType.setRemoteId(remoteId);
            if (jsonObject.isNull("deleted_at")) {
            	participantType.save();
            } else {
            	ParticipantType pt = ParticipantType.findByRemoteId(remoteId);
            	if (pt != null) {
            		pt.delete();
            	}
            }
        } catch (JSONException je) {
            Log.e(TAG, "Error parsing object json", je);
        }    
    }
    
    /*
     * Finders
     */
    public static List<ParticipantType> getAll() {
        return new Select().from(ParticipantType.class).orderBy("Id ASC").execute();
    }
    
    public static ParticipantType findByRemoteId(Long id) {
        return new Select().from(ParticipantType.class).where("RemoteId = ?", id).executeSingle();
    }
    
    public List<RelationshipType> getRelationshipTypes() {
        return new Select().from(RelationshipType.class).where("OwnerParticipantType = ?", getId()).execute();        
    }
    
    public static int getCount() {
       return getAll().size();
    }
    
    public static ParticipantType findById(Long id) {
        return new Select().from(ParticipantType.class).where("Id = ?", id).executeSingle();
    }
    
    public List<Property> getProperties() {
        return Property.getAllByParticipantType(this);
    }
    
    /*
     * Getters / Setters
     */
    
    public String getLabel() {
        if (mLabel == null) return "";
        int labelId = AppUtil.getContext().getResources().getIdentifier(mLabel.toLowerCase(), "string", AppUtil.getContext().getPackageName());
        if (labelId == 0)
            return mLabel;
        else
            return AppUtil.getContext().getResources().getString(labelId);
    }
    
    public Long getRemoteId() {
        return mRemoteId;
    }
    
    @Override
    public String toString() {
        return getLabel();
    }
    
    public void setLabel(String label) {
        mLabel = label;
    }
    
    public static List<String> getTypeLabels() {
        List<String> types = new ArrayList<String>();
        
        for (ParticipantType participantType : ParticipantType.getAll()) {
            types.add(participantType.getLabel());
        }
        
        return types;
    }
    
    private void setRemoteId(Long remoteId) {
        mRemoteId = remoteId;
    }
}
