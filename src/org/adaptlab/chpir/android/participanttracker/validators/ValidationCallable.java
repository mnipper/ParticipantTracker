package org.adaptlab.chpir.android.participanttracker.validators;


public interface ValidationCallable {   
    public boolean validate(String value);
    public String formatText(String text);
}
