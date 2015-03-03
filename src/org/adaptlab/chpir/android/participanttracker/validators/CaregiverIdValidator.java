package org.adaptlab.chpir.android.participanttracker.validators;

public class CaregiverIdValidator extends ParticipantIdValidator {

    @Override
    public boolean validate(String value) {
        if (value.length() > 0) {
            return value.charAt(0) == 'E' && super.validate(value);
        }

        return false;
    }
}