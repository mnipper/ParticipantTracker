package org.adaptlab.chpir.android.participanttracker;

import org.adaptlab.chpir.android.models.ParticipantType;

public class AppUtil {
    
    public static final void appInit() {
        seedDb();
    }
    
    public static void seedDb() {
        if (ParticipantType.getCount() == 0) {
            ParticipantType p = new ParticipantType();
            p.setLabel("Child");
            p.save();
            p = new ParticipantType();
            p.setLabel("Caregiver");
            p.save();
            p = new ParticipantType();
            p.setLabel("Center");
            p.save();
        }
    }
}
