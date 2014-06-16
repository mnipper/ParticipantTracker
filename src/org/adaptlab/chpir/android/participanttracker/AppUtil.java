package org.adaptlab.chpir.android.participanttracker;

import org.adaptlab.chpir.android.models.Participant;
import org.adaptlab.chpir.android.models.ParticipantProperty;
import org.adaptlab.chpir.android.models.ParticipantType;
import org.adaptlab.chpir.android.models.Property;

import com.activeandroid.ActiveAndroid;

public class AppUtil {
    private static final String TAG = "AppUtil";
    private static final boolean SEED_DB = false;
    
    public static final void appInit() {
        seedDb();
    }
    
    public static void seedDb() {           
        if (SEED_DB) {
            String[] dummyParticipantTypes = {"Child", "Caregiver", "Center"};
            for (String participantType : dummyParticipantTypes) {
                ActiveAndroid.beginTransaction();
                try {
                    ParticipantType p = new ParticipantType();
                    p.setLabel(participantType);
                    p.save();
                    ActiveAndroid.setTransactionSuccessful();
                }
                finally {
                    ActiveAndroid.endTransaction();
                }
            }
            
            for (ParticipantType participantType : ParticipantType.getAll()) {
                Property nameProperty = new Property("name", Property.PropertyType.STRING, true, participantType);           
                Property ageProperty = new Property("age", Property.PropertyType.INTEGER, true, participantType);  
                
                participantType.setLabelProperty(nameProperty);
                
                participantType.save();
                nameProperty.save();
                ageProperty.save();
                participantType.save();
                
                for (int i = 0; i < 4; i++) {  
                    Participant participant = new Participant(participantType);
                    participant.save();
                    
                    ParticipantProperty participantProperty = new ParticipantProperty(participant, nameProperty, participantType + " " + i); 
                    participantProperty.save();
                } 
            } 
        }       
    }
}
