package org.adaptlab.chpir.android.participanttracker;

import org.adaptlab.chpir.android.models.Participant;
import org.adaptlab.chpir.android.models.ParticipantProperty;
import org.adaptlab.chpir.android.models.ParticipantType;
import org.adaptlab.chpir.android.models.Property;

import android.util.Log;

public class AppUtil {
    private static final String TAG = "AppUtil";
    
    public static final void appInit() {
        seedDb();
    }
    
    public static void seedDb() {
        if (ParticipantType.getCount() == 0) {
            String[] dummyParticipantTypes = {"Child", "Caregiver", "Center"};
            for (String participantType : dummyParticipantTypes) {
                ParticipantType p = new ParticipantType();
                p.setLabel(participantType);
                p.save();
            }
        }
        
        if (Participant.getCount() == 0) {
            for (ParticipantType participantType : ParticipantType.getAll()) {
                Property property = new Property("name", Property.PropertyType.STRING, true, participantType);
                for (int i = 0; i < 4; i++) {
                    Participant participant = new Participant(participantType);
                    participant.save();
                    ParticipantProperty participantProperty = new ParticipantProperty(participant, property, participantType + " " + i); 
                    participantProperty.save();
                }
            }
        }
    }
}
