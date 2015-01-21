package org.adaptlab.chpir.android.participanttracker.validators;


public class ParticipantIdValidator implements ValidationCallable {
    
    @Override
    public boolean validate(String value) {
        VerhoeffErrorDetection verhoeff = new VerhoeffErrorDetection();
        return verhoeff.performCheck(value);
    }
}
