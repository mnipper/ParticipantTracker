package org.adaptlab.chpir.android.participanttracker.Receivers;

import org.adaptlab.chpir.android.participanttracker.ParticipantDetailFragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class InstrumentListReceiver extends BroadcastReceiver {
    private static final String TAG = "InstrumentListReceiver";
    private static final String INSTRUMENT_TITLE_LIST = "org.adaptlab.chpir.android.survey.instrument_title_list";
    private static final String INSTRUMENT_ID_LIST = "org.adaptlab.chpir.android.survey.instrument_id_list";
    
    public static final String GET_INSTRUMENT_LIST = "org.adaptlab.chpir.android.survey.get_instrument_list";
    public static final String START_SURVEY = "org.adaptlab.chpir.android.survey.start_survey";
    public static final String START_SURVEY_INSTRUMENT_ID = "org.adaptlab.chpir.android.survey.instrument_id";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "Received broadcast with list of available instruments");
        String[] instrumentTitleList = intent.getStringArrayExtra(INSTRUMENT_TITLE_LIST);
        long[] instrumentIdList = intent.getLongArrayExtra(INSTRUMENT_ID_LIST);
        ParticipantDetailFragment.displayInstrumentPicker(instrumentTitleList, instrumentIdList);  
    }
}