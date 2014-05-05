package org.adaptlab.chpir.android.participanttracker;

import org.adaptlab.chpir.android.models.Participant;
import org.adaptlab.chpir.android.models.ParticipantProperty;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ParticipantDetailFragment extends Fragment {
    private static final String TAG = "ParticipantDetailFragment";
    public final static String EXTRA_PARTICIPANT_ID = 
            "org.adaptlab.chpir.participanttracker.participantdetailfragment.participant_id";
    
    private Participant mParticipant;
    private LinearLayout mParticipantPropertiesContainer;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        if (savedInstanceState != null) {
            mParticipant = Participant.findById(savedInstanceState.getLong(EXTRA_PARTICIPANT_ID));
        } else {
            Long participantId = getActivity().getIntent().getLongExtra(EXTRA_PARTICIPANT_ID, -1);
            if (participantId == -1) return;

            mParticipant = Participant.findById(participantId);
            Log.i(TAG, String.valueOf(mParticipant));
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
        
        return v;
    }
}
