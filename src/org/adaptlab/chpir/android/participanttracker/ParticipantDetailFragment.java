package org.adaptlab.chpir.android.participanttracker;

import org.adaptlab.chpir.android.participanttracker.Receivers.InstrumentListReceiver;
import org.adaptlab.chpir.android.participanttracker.models.Participant;
import org.adaptlab.chpir.android.participanttracker.models.ParticipantProperty;
import org.adaptlab.chpir.android.participanttracker.models.RelationshipType;
import org.json.JSONException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
    private static String sParticipantMetadata;
    private LinearLayout mParticipantPropertiesContainer;
    private Button mNewSurveyButton;
    private static Activity sActivity;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sActivity = getActivity();
        
        if (savedInstanceState != null) {
            mParticipant = Participant.findById(savedInstanceState.getLong(EXTRA_PARTICIPANT_ID));
        } else {
            Long participantId = getActivity().getIntent().getLongExtra(EXTRA_PARTICIPANT_ID, -1);
            if (participantId == -1) return;

            mParticipant = Participant.findById(participantId);
        }
        
        try {
            sParticipantMetadata = mParticipant.getMetadata();
        } catch (JSONException e) {
            Log.e(TAG, "Could not parse participant metadata for " + mParticipant.getId());
        }  
        
        getActivity().setTitle(mParticipant.getLabel());
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
            Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View v = inflater.inflate(R.layout.fragment_participant_detail, parent,
                false);       
        
        mParticipantPropertiesContainer = (LinearLayout) v.findViewById(R.id.participant_properties_container);

        for (ParticipantProperty participantProperty : mParticipant.getParticipantProperties()) {
            addKeyValueLabel(participantProperty.getProperty().getLabel(), participantProperty.getValue());
        }
        
        addKeyValueLabel("UUID", mParticipant.getUUID());
                
        return v;
    }
    
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.participant_detail, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_new:
                newSurvey();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    public static void displayInstrumentPicker(String[] instrumentTitleList, final long[] instrumentIdList) {
        AlertDialog.Builder builder = new AlertDialog.Builder(sActivity);
        builder.setTitle(sActivity.getString(R.string.choose_instrument));
        builder.setSingleChoiceItems(instrumentTitleList, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {             
                Intent i = new Intent();
                i.setAction(InstrumentListReceiver.START_SURVEY);
                i.putExtra(InstrumentListReceiver.START_SURVEY_INSTRUMENT_ID, instrumentIdList[which]);
                i.putExtra(EXTRA_PARTICIPANT_METADATA, sParticipantMetadata);
                sActivity.sendBroadcast(i);
                dialog.cancel();
                sActivity.finish();
            }
        }); 
        
        builder.show();
    }
    
    private void newSurvey() {
        Intent i = new Intent();
        i.setAction(InstrumentListReceiver.GET_INSTRUMENT_LIST);
        getActivity().getApplicationContext().sendBroadcast(i);
    }
    
    private void addKeyValueLabel(String key, String value) {
        TextView textView = new TextView(getActivity());
        textView.setTextAppearance(getActivity(), R.style.sectionHeader);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 25, 0, 0);
        textView.setLayoutParams(layoutParams);
        mParticipantPropertiesContainer.addView(textView);
        textView.setText(key);
        
        textView = new TextView(getActivity());
        SpannableString spanString = new SpannableString(value);
        spanString.setSpan(new StyleSpan(Typeface.BOLD), 0, spanString.length(), 0);
        textView.setText(spanString);
        mParticipantPropertiesContainer.addView(textView);
    }

}
