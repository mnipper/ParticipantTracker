package org.adaptlab.chpir.android.participanttracker;

import org.adaptlab.chpir.android.activerecordcloudsync.ActiveRecordCloudSync;
import org.adaptlab.chpir.android.models.Participant;
import org.adaptlab.chpir.android.models.ParticipantProperty;
import org.adaptlab.chpir.android.models.ParticipantType;
import org.adaptlab.chpir.android.models.Property;
import org.adaptlab.chpir.android.participanttracker.tasks.*;

import com.activeandroid.ActiveAndroid;

import android.content.Context;
import android.util.Log;

public class AppUtil {
    private static final String TAG = "AppUtil";
    private static final boolean SEED_DB = false;
	private static final String ENDPOINT = "http://10.0.3.2:3000/api/v1/";
	private static Context mContext;
	
    public static final void appInit(Context context) {
        mContext = context;
    	seedDb();
        addDataTables();
        syncData();
    }
    
    private static void addDataTables() {
		ActiveRecordCloudSync.setEndPoint(ENDPOINT);
		ActiveRecordCloudSync.addReceiveTable("participant_types", ParticipantType.class);
		ActiveRecordCloudSync.addReceiveTable("properties", Property.class);
		ActiveRecordCloudSync.addSendTable("participants", Participant.class);
		ActiveRecordCloudSync.addSendTable("participant_properties", ParticipantProperty.class);
	}
    
    private static void syncData() {
		new FetchDataTask(mContext).execute();
		//new SendDataTask(mContext).execute();
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
