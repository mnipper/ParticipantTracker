package org.adaptlab.chpir.android.participanttracker.models;

import android.util.Log;

import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import org.adaptlab.chpir.android.activerecordcloudsync.ReceiveModel;
import org.adaptlab.chpir.android.participanttracker.validators.CaregiverIdValidator;
import org.adaptlab.chpir.android.participanttracker.validators.CenterIdValidator;
import org.adaptlab.chpir.android.participanttracker.validators.ChildIdValidator;
import org.adaptlab.chpir.android.participanttracker.validators.ParticipantIdValidator;
import org.adaptlab.chpir.android.participanttracker.validators.ValidationCallable;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

@Table(name = "Property")
public class Property extends ReceiveModel {
    private static final String TAG = "Property";
    
    public static enum PropertyType {STRING, DATE, INTEGER};
    public static enum Validator {PARTICIPANT_ID, CENTER_ID, CHILD_ID, CAREGIVER_ID};

    @Column(name = "RemoteId", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    private Long mRemoteId;
    @Column(name = "Label")
    private String mLabel;
    @Column(name = "TypeOf")
    private PropertyType mTypeOf;
    @Column(name = "Required")
    private boolean mRequired;
    @Column(name = "ParticipantType", onUpdate = Column.ForeignKeyAction.CASCADE, onDelete = Column.ForeignKeyAction.CASCADE)
    private ParticipantType mParticipantType;
    @Column(name = "UseAsLabel")
    private boolean mUseAsLabel;
    @Column(name = "Validator")
    private Validator mValidator;
    @Column(name = "IncludeInMetadata")
    private boolean mIncludeInMetadata;
    
    public Property() {
        super();
        mIncludeInMetadata = false;
    }
    
    public Property(String label, PropertyType typeOf, boolean required, ParticipantType participantType, String validator) {
        super();
        setLabel(label);
        setTypeOf(typeOf);
        setRequired(required);
        setParticipantType(participantType);
        setValidator(validator);
    }
    
    @Override
    public void createObjectFromJSON(JSONObject jsonObject) {
        try {
            Long remoteId = jsonObject.getLong("id");
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
            property.setUseAsLabel(jsonObject.getBoolean("use_as_label"));
            property.setValidator(jsonObject.getString("validator"));
            property.setIncludeInMetadata(jsonObject.getBoolean("include_in_metadata"));
            if (jsonObject.isNull("deleted_at")) {
            	property.save();
            } else {
            	Property py = Property.findByRemoteId(remoteId);
            	if (py != null) {
            		py.delete();
            	}
            }
            
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
    
    public static List<Property> getAllByParticipantType(ParticipantType participantType) {
        return new Select().from(Property.class).where("ParticipantType = ?", participantType.getId()).execute();
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
    
    public PropertyType getTypeOf() {
        return mTypeOf;
    }
    
    public boolean getRequired() {
        return mRequired;
    }
    
    public ParticipantType getParticipantType() {
        return mParticipantType;
    }
    
    public boolean getUseAsLabel() {
        return mUseAsLabel;
    }
    
    public void setUseAsLabel(boolean useAsLabel) {
        mUseAsLabel = useAsLabel;
    }
    
    public ValidationCallable getValidationCallable() {
        if (mValidator == Validator.PARTICIPANT_ID) {
            return new ParticipantIdValidator();
        } else if (mValidator == Validator.CENTER_ID) {
            return new CenterIdValidator();
        } else if (mValidator == Validator.CHILD_ID) {
            return new ChildIdValidator();
        } else if (mValidator == Validator.CAREGIVER_ID) {
            return new CaregiverIdValidator();
        } else {
            return null;
        }
    }
    
    public boolean hasValidator() {
        return getValidationCallable() != null;
    }
    
    public boolean isIncludedInMetadata() {
        return mIncludeInMetadata;
    }
    
    private void setLabel(String label) {
        mLabel = label;
    }
    
    private void setRemoteId(Long remoteId) {
        mRemoteId = remoteId;
    }
    
    private void setTypeOf(String typeOf) {
        mTypeOf = PropertyType.valueOf(typeOf);
    }
    
    private void setTypeOf(PropertyType propertyType) {
        mTypeOf = propertyType;
    }
    
    private void setRequired(boolean required) {
        mRequired = required;
    }
    
    private void setParticipantType(ParticipantType participantType) {
        mParticipantType = participantType;
    }
    
    private void setValidator(String validator) {
        for (Validator v: Validator.values()) {
            if (v.name().equals(validator)) {
                mValidator = Validator.valueOf(validator);
                return;
            }
        }
    }
    
    private void setIncludeInMetadata(boolean include) {
        mIncludeInMetadata = include;
    }
}
