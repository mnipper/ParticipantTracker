package org.adaptlab.chpir.android.participanttracker;

import java.util.UUID;

import org.adaptlab.chpir.android.activerecordcloudsync.ActiveRecordCloudSync;
import org.adaptlab.chpir.android.activerecordcloudsync.PollService;
import org.adaptlab.chpir.android.participanttracker.models.AdminSettings;
import org.adaptlab.chpir.android.participanttracker.models.Participant;
import org.adaptlab.chpir.android.participanttracker.models.ParticipantProperty;
import org.adaptlab.chpir.android.participanttracker.models.ParticipantType;
import org.adaptlab.chpir.android.participanttracker.models.Property;
import org.adaptlab.chpir.android.participanttracker.tasks.*;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

import com.activeandroid.ActiveAndroid;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

public class AppUtil {
    private static final String TAG = "AppUtil";
    private static final boolean SEED_DB = false;
	private static Context mContext;
    public static String ADMIN_PASSWORD_HASH;
    public static String ACCESS_TOKEN;

    public static final void appInit(Context context) {
        mContext = context;
        ADMIN_PASSWORD_HASH = context.getResources().getString(R.string.admin_password_hash);
        ACCESS_TOKEN = context.getResources().getString(R.string.backend_api_key);  

        if (AdminSettings.getInstance().getDeviceIdentifier() == null) {
            AdminSettings.getInstance().setDeviceIdentifier(UUID.randomUUID().toString());
        }
        
        ActiveRecordCloudSync.setAccessToken(ACCESS_TOKEN);
        ActiveRecordCloudSync.setVersionCode(AppUtil.getVersionCode(context));
        ActiveRecordCloudSync.setEndPoint(AdminSettings.getInstance().getApiUrl());
    	seedDb();
        addDataTables();
        syncData();
        PollService.setServiceAlarm(context.getApplicationContext(), true);

    }
    
    private static void addDataTables() {
		ActiveRecordCloudSync.addReceiveTable("participant_types", ParticipantType.class);
		ActiveRecordCloudSync.addReceiveTable("properties", Property.class);
		ActiveRecordCloudSync.addSendReceiveTable("participants", Participant.class);
		ActiveRecordCloudSync.addSendReceiveTable("participant_properties", ParticipantProperty.class);
	}
    
    private static void syncData() {
		new FetchDataTask(mContext).execute();
		new SendDataTask(mContext).execute();
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
                Property nameProperty = new Property("Name", Property.PropertyType.STRING, true, participantType);           
                Property ageProperty = new Property("Age", Property.PropertyType.INTEGER, false, participantType); 
                Property dateProperty = new Property("Birthday", Property.PropertyType.DATE, false, participantType);
                
                nameProperty.setUseAsLabel(true);

                nameProperty.save();
                ageProperty.save();
                dateProperty.save();

                for (int i = 0; i < 4; i++) {  
                    Participant participant = new Participant(participantType);
                    participant.save();
                    
                    ParticipantProperty participantProperty = new ParticipantProperty(participant, nameProperty, participantType + " " + i); 
                    participantProperty.save();
                } 
            } 
        }       
    }

	public static boolean checkAdminPassword(String string) {
		String hash = new String(Hex.encodeHex(DigestUtils.sha256(string)));
        return hash.equals(ADMIN_PASSWORD_HASH);
	}
	
	public static int getVersionCode(Context context) {
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return pInfo.versionCode;
        } catch (NameNotFoundException nnfe) {
            Log.e(TAG, "Error finding version code: " + nnfe);
        }
        return -1;
    }
}
