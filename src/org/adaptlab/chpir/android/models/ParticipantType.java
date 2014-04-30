package org.adaptlab.chpir.android.models;

import java.util.List;

import org.adaptlab.chpir.android.activerecordcloudsync.ReceiveModel;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

@Table(name = "ParticipantType")
public class ParticipantType extends ReceiveModel {
    private static final String TAG = "ParticipantType";
    
    @Column(name = "Label")
    private String mLabel;
    @Column(name = "LabelProperty")
    private Property mLabelProperty;
    @Column(name = "RemoveId", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    private Long mRemoteId;
    
    public ParticipantType() {
        super();
    }

    @Override
    public void createObjectFromJSON(JSONObject jsonObject) {
        try {
            Long remoteId = jsonObject.getLong("id");
            
            // If a ParticipantType already exists, update it from the remote
            ParticipantType participantType = ParticipantType.findByRemoteId(remoteId);
            if (participantType == null) {
                participantType = this;
            }
            
            Log.i(TAG, "Creating object from JSON Object: " + jsonObject);
            participantType.setLabel(jsonObject.getString("label"));
            participantType.setRemoteId(remoteId);
            participantType.save();
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
    
    public static int getCount() {
        return getAll().size();
    }
    
    /*
     * Getters / Setters
     */
    
    public String getLabel() {
        return mLabel;
    }
    
    public Long getRemoteId() {
        return mRemoteId;
    }
    
    @Override
    public String toString() {
        return getLabel();
    }
    
    public Property getLabelProperty() {
        return mLabelProperty;
    }
    
    public void setLabelProperty(Property property) {
        mLabelProperty = property;
    }
    
    public void setLabel(String label) {
        mLabel = label;
    }
    
    private void setRemoteId(Long remoteId) {
        mRemoteId = remoteId;
    }
}
