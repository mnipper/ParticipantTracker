package org.adaptlab.chpir.android.participanttracker;

import android.content.Context;
import android.widget.DatePicker;

// Serialize and deserialize a DatePicker in the format Month-Day-Year

public class SerializableDatePicker extends DatePicker {

    public SerializableDatePicker(Context context) {
        super(context);
    }

    public String serialize() {
        return (this.getMonth() + 1) + "-" + this.getDayOfMonth() + "-" + this.getYear();
    }
    
    public void deserialize(String serializedDate) {
        if (serializedDate.equals("") || serializedDate == null) return;
        String[] dateComponents = serializedDate.split("-");
        int month, day, year;
        if (dateComponents.length == 3) {
            month = Integer.parseInt(dateComponents[0]) - 1;
            day = Integer.parseInt(dateComponents[1]);
            year = Integer.parseInt(dateComponents[2]);
            this.updateDate(year, month, day);
        }
    }
}
