package org.adaptlab.chpir.android.models;

import java.util.List;

import org.adaptlab.chpir.android.activerecordcloudsync.ReceiveModel;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

@Table(name = "Property")
public class Property extends ReceiveModel {
    private static final String TAG = "Property";

    @Column(name = "RemoteId")
    private Long mRemoteId;
    @Column(name = "Label")
    private String mLabel;
    @Column(name = "TypeOf")
    private String mTypeOf;
    @Column(name = "Required")
    private boolean mRequired;
    @Column(name = "ParticipantType")
    private ParticipantType mParticipantType;
    
    @Override
    public void createObjectFromJSON(JSONObject jsonObject) {
        try {
            Long remoteId = jsonObject.getLong("id");
            
            // If a Property already exists, update it from the remote
            Property property = Property.findByRemoteId(remoteId);
            if (property == null) {
                property = this;
            }
            
            Log.i(TAG, "Creating object from JSON Object: " + jsonObject);
            property.setLabel(jsonObject.getString("label"));
            property.setRemoteId(remoteId);
            property.setTypeOf(jsonObject.getString("type_of"));
            property.setRequired(jsonObject.getBoolean("required"));
            property.setParticipantType(ParticipantType.findByRemoteId(jsonObject.getLong("participant_type_id")));
            property.save();
        } catch (JSONException je) {
            Log.e(TAG, "Error parsing object json", je);
        } 
    }
    
    /*
     * Finders
     */
    public static List<Property> getAll() {
        return new Select().from(Property.class).orderBy("Id ASC").execute();
    }
    
    public static Property findByRemoteId(Long id) {
        return new Select().from(Property.class).where("RemoteId = ?", id).executeSingle();
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
    
    public String getTypeOf() {
        return mTypeOf;
    }
    
    public boolean getRequired() {
        return mRequired;
    }
    
    public ParticipantType getParticipantType() {
        return mParticipantType;
    }
    
    private void setLabel(String label) {
        mLabel = label;
    }
    
    private void setRemoteId(Long remoteId) {
        mRemoteId = remoteId;
    }
    
    private void setTypeOf(String typeOf) {
        mTypeOf = typeOf;
    }
    
    private void setRequired(boolean required) {
        mRequired = required;
    }
    
    private void setParticipantType(ParticipantType participantType) {
        mParticipantType = participantType;
    }
}
