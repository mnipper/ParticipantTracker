package org.adaptlab.chpir.android.participanttracker;

import java.util.List;
import java.util.UUID;

import org.adaptlab.chpir.android.activerecordcloudsync.ActiveRecordCloudSync;
import org.adaptlab.chpir.android.participanttracker.models.AdminSettings;
import org.adaptlab.chpir.android.participanttracker.models.Participant;
import org.adaptlab.chpir.android.participanttracker.models.ParticipantProperty;
import org.adaptlab.chpir.android.participanttracker.models.ParticipantType;
import org.adaptlab.chpir.android.participanttracker.models.Property;
import org.adaptlab.chpir.android.participanttracker.models.Relationship;
import org.adaptlab.chpir.android.participanttracker.models.RelationshipType;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.AlertDialog;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

import com.activeandroid.ActiveAndroid;
import com.crashlytics.android.Crashlytics;

public class AppUtil {
    private final static boolean REQUIRE_SECURITY_CHECKS = false;
	private static final String TAG = "AppUtil";
	private static final boolean SEED_DB = false;
	public static String ADMIN_PASSWORD_HASH;
	public static String ACCESS_TOKEN;

	public static final void appInit(Context context) {
		
		if (AppUtil.REQUIRE_SECURITY_CHECKS) {
			if (!AppUtil.hasPassedDeviceSecurityChecks(context)) {
				return;
			}
		}
		
		ADMIN_PASSWORD_HASH = context.getResources().getString(R.string.admin_password_hash);
		ACCESS_TOKEN = AdminSettings.getInstance().getApiKey();
		
		if (!BuildConfig.DEBUG)
            Crashlytics.start(context);
		
		if (AdminSettings.getInstance().getDeviceIdentifier() == null) {
			AdminSettings.getInstance().setDeviceIdentifier(
					UUID.randomUUID().toString());
		}

		ActiveRecordCloudSync.setAccessToken(ACCESS_TOKEN);
		ActiveRecordCloudSync.setVersionCode(AppUtil.getVersionCode(context));
		ActiveRecordCloudSync.setEndPoint(getAdminSettingsInstanceApiUrl());
		addDataTables();
		seedDb();
	}
	
	public static String getAdminSettingsInstanceApiUrl() {
	    String domainName = AdminSettings.getInstance().getApiUrl();
		return domainName + "api/" + AdminSettings.getInstance().getApiVersion() + "/" ;
	}

	private static boolean hasPassedDeviceSecurityChecks(Context context) {
		DevicePolicyManager devicePolicyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
		if (devicePolicyManager.getStorageEncryptionStatus() != DevicePolicyManager.ENCRYPTION_STATUS_ACTIVE) {
			new AlertDialog.Builder(context)
            .setTitle(R.string.encryption_required_title)
            .setMessage(R.string.encryption_required_text)
            .setCancelable(false)
            .setPositiveButton(R.string.okay, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    int pid = android.os.Process.myPid(); 
                    android.os.Process.killProcess(pid);
                }
             })
             .show();
            return false;
		}
		return true;
	}

	private static void addDataTables() {
		ActiveRecordCloudSync.addReceiveTable("participant_types", ParticipantType.class);
		ActiveRecordCloudSync.addReceiveTable("properties", Property.class);
		ActiveRecordCloudSync.addReceiveTable("relationship_types", RelationshipType.class);
		ActiveRecordCloudSync.addSendReceiveTable("participants", Participant.class);
		ActiveRecordCloudSync.addSendReceiveTable("participant_properties", ParticipantProperty.class);
		ActiveRecordCloudSync.addSendReceiveTable("relationships", Relationship.class);
	}

	@SuppressLint("UseValueOf")
	public static void seedDb() {
		if (SEED_DB) {
			String[] dummyParticipantTypes = { "Child", "Caregiver", "Center" };
			for (String participantType : dummyParticipantTypes) {
				ActiveAndroid.beginTransaction();
				try {
					ParticipantType p = new ParticipantType();
					p.setLabel(participantType);
					p.save();
					ActiveAndroid.setTransactionSuccessful();
				} finally {
					ActiveAndroid.endTransaction();
				}
			}

			for (ParticipantType participantType : ParticipantType.getAll()) {
				Property nameProperty = new Property("Name",
						Property.PropertyType.STRING, true, participantType, "PARTICIPANT_ID");
				Property ageProperty = new Property("Age",
						Property.PropertyType.INTEGER, false, participantType, "PARTICIPANT_ID");
				Property dateProperty = new Property("Birthday",
						Property.PropertyType.DATE, false, participantType, "PARTICIPANT_ID");

				nameProperty.setUseAsLabel(true);

				nameProperty.save();
				ageProperty.save();
				dateProperty.save();
                
                if (participantType.getLabel().equals("Child")) {
                    Log.i(TAG, "Creating relationship");
                    RelationshipType relationshipType = new RelationshipType(
                            "Caregiver",
                            ParticipantType.findById(new Long(1)),
                            ParticipantType.findById(new Long(2))
                    );
                    relationshipType.save();
                }

				for (int i = 0; i < 10; i++) {
					Participant participant = new Participant(participantType);
					participant.save();

					ParticipantProperty participantProperty = new ParticipantProperty(
							participant, nameProperty, participantType + " " + i
					);
					participantProperty.save();

					participantProperty = new ParticipantProperty(participant,
							ageProperty, String.valueOf(i));
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
			PackageInfo pInfo = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0);
			return pInfo.versionCode;
		} catch (NameNotFoundException nnfe) {
			Log.e(TAG, "Error finding version code: " + nnfe);
		}
		return -1;
	}

    public static boolean checkForRunningProcess(Context context, String process) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningAppProcessInfo> procInfos = activityManager.getRunningAppProcesses();
        for (int i = 0; i < procInfos.size(); i++) {
            if (procInfos.get(i).processName.equals(process)) {
                return true;
            }
        }
        return false;
    }
	
}
