package org.adaptlab.chpir.android.participanttracker.validators;

public class CenterIdValidator implements ValidationCallable {
    @Override
    public boolean validate(String value) {
        return value.matches("^\\d\\d\\d$");
    }

    @Override
    public String formatText(String text) {
        return text;
    }
}
