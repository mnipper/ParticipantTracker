package org.adaptlab.chpir.android.participanttracker;

import org.adaptlab.chpir.android.models.Participant;
import org.adaptlab.chpir.android.models.ParticipantProperty;
import org.adaptlab.chpir.android.participanttracker.Receivers.InstrumentListReceiver;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ParticipantDetailFragment extends Fragment {
    private static final String TAG = "ParticipantDetailFragment";
    public final static String EXTRA_PARTICIPANT_ID = 
            "org.adaptlab.chpir.participanttracker.participantdetailfragment.participant_id";
    public final static String EXTRA_PARTICIPANT_METADATA =
            "org.adaptlab.chpir.android.survey.metadata";
    
    private Participant mParticipant;
    private static String sParticipantUUID;
    private LinearLayout mParticipantPropertiesContainer;
    private Button mNewSurveyButton;
    private static Activity sActivity;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sActivity = getActivity();
        
        if (savedInstanceState != null) {
            mParticipant = Participant.findById(savedInstanceState.getLong(EXTRA_PARTICIPANT_ID));
            sParticipantUUID = mParticipant.getUUID();
        } else {
            Long participantId = getActivity().getIntent().getLongExtra(EXTRA_PARTICIPANT_ID, -1);
            if (participantId == -1) return;

            mParticipant = Participant.findById(participantId);
            sParticipantUUID = mParticipant.getUUID();
        }
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
            Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_participant_detail, parent,
                false);       
        
        mParticipantPropertiesContainer = (LinearLayout) v.findViewById(R.id.participant_properties_container);
        
        for (ParticipantProperty participantProperty : mParticipant.getParticipantProperties()) {
            TextView textView = new TextView(getActivity());
            textView.setText(participantProperty.getProperty().getLabel() + ": " + participantProperty.getValue());
            mParticipantPropertiesContainer.addView(textView);
        }
        
        mNewSurveyButton = (Button) v.findViewById(R.id.new_survey_button);
        mNewSurveyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                i.setAction(InstrumentListReceiver.GET_INSTRUMENT_LIST);
                getActivity().getApplicationContext().sendBroadcast(i);       
            }
        });
        
        return v;
    }
    
    public static void displayInstrumentPicker(String[] instrumentTitleList, final long[] instrumentIdList) {
        AlertDialog.Builder builder = new AlertDialog.Builder(sActivity);
        builder.setTitle("Choose something");
        builder.setSingleChoiceItems(instrumentTitleList, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {             
                Intent i = new Intent();
                i.setAction(InstrumentListReceiver.START_SURVEY);
                i.putExtra(InstrumentListReceiver.START_SURVEY_INSTRUMENT_ID, instrumentIdList[which]);
                i.putExtra(EXTRA_PARTICIPANT_METADATA, getParticipantMetadataJSON());
                sActivity.sendBroadcast(i);
                dialog.cancel();
                sActivity.finish();
            }
        }); 
        
        builder.show();
    }
    
    private static String getParticipantMetadataJSON() {
        return "{\"participant_uuid\": \"" + sParticipantUUID + "\"}";
    }
}
