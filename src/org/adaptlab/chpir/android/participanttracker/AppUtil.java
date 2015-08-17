package org.adaptlab.chpir.android.participanttracker;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

import com.activeandroid.ActiveAndroid;
import com.crashlytics.android.Crashlytics;

import org.adaptlab.chpir.android.activerecordcloudsync.ActiveRecordCloudSync;
import org.adaptlab.chpir.android.participanttracker.models.AdminSettings;
import org.adaptlab.chpir.android.participanttracker.models.DeviceSyncEntry;
import org.adaptlab.chpir.android.participanttracker.models.Participant;
import org.adaptlab.chpir.android.participanttracker.models.ParticipantProperty;
import org.adaptlab.chpir.android.participanttracker.models.ParticipantType;
import org.adaptlab.chpir.android.participanttracker.models.Property;
import org.adaptlab.chpir.android.participanttracker.models.Relationship;
import org.adaptlab.chpir.android.participanttracker.models.RelationshipType;
import org.adaptlab.chpir.android.vendor.BCrypt;

import java.util.UUID;

public class AppUtil {
    private final static boolean REQUIRE_SECURITY_CHECKS = !BuildConfig.DEBUG;
    private static final String TAG = "AppUtil";
    private static final boolean SEED_DB = false;
    public static String ADMIN_PASSWORD_HASH;
    public static String ACCESS_TOKEN;
    private static Context mContext;

    public static final void appInit(Context context) {
        if (AppUtil.REQUIRE_SECURITY_CHECKS) {
            if (!AppUtil.hasPassedDeviceSecurityChecks(context)) {
                return;
            }
        }

        mContext = context;

        ADMIN_PASSWORD_HASH = context.getResources().getString(R.string.admin_password_hash);
        ACCESS_TOKEN = AdminSettings.getInstance().getApiKey();

        if (AdminSettings.getInstance().getDeviceIdentifier() == null) {
            AdminSettings.getInstance().setDeviceIdentifier(
                    UUID.randomUUID().toString());
        }

        if (!BuildConfig.DEBUG) {
            Crashlytics.start(context);
            Crashlytics.setUserIdentifier(AdminSettings.getInstance().getDeviceIdentifier());
            Crashlytics.setString("device label", AdminSettings.getInstance().getDeviceLabel());
        }

        ActiveRecordCloudSync.setAccessToken(ACCESS_TOKEN);
        ActiveRecordCloudSync.setVersionCode(AppUtil.getVersionCode(context));
        ActiveRecordCloudSync.setEndPoint(getAdminSettingsInstanceApiUrl());
        addDataTables();
        seedDb();
    }

    public static String getAdminSettingsInstanceApiUrl() {
        String domainName = AdminSettings.getInstance().getApiUrl();
        return domainName + "api/" + AdminSettings.getInstance().getApiVersion() + "/";
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
        ActiveRecordCloudSync.addSendTable("device_sync_entries", DeviceSyncEntry.class);
    }

    @SuppressLint("UseValueOf")
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

    public static boolean checkAdminPassword(String password) {
        return BCrypt.checkpw(password, ADMIN_PASSWORD_HASH);
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

    public static Context getContext() {
        return mContext;
    }
}
