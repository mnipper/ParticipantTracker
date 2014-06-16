package org.adaptlab.chpir.android.participanttracker.Receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class InstrumentListReceiver extends BroadcastReceiver {
    private static final String TAG = "InstrumentListReceiver";
    private static final String INSTRUMENT_TITLE_LIST = "org.adaptlab.chpir.android.survey.instrument_title_list";
    private static final String INSTRUMENT_ID_LIST = "org.adaptlab.chpir.android.survey.instrument_id_list";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "Received broadcast with list of available instruments");
        String[] instrumentTitleList = intent.getStringArrayExtra(INSTRUMENT_TITLE_LIST);
        long[] instrumentIdList = intent.getLongArrayExtra(INSTRUMENT_ID_LIST);
        
        for (int i = 0; i < instrumentTitleList.length; i++) {
            Log.i(TAG, instrumentTitleList[i] + " id is " + instrumentIdList[i]); 
        } 
        
        /*
        Intent i = new Intent();
        i.setAction("org.adaptlab.chpir.android.survey.start_survey");
        i.putExtra("org.adaptlab.chpir.android.survey.instrument_id", instrumentIdList[0]);
        context.sendBroadcast(i);
        */
    }
}