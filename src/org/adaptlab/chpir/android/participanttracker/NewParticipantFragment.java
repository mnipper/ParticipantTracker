package org.adaptlab.chpir.android.participanttracker;

import org.adaptlab.chpir.android.models.Participant;
import org.adaptlab.chpir.android.models.ParticipantType;
import org.adaptlab.chpir.android.models.Property;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class NewParticipantFragment extends Fragment {
    private static final String TAG = "NewParticipantFragment";
    public final static String EXTRA_PARTICIPANT_TYPE_ID = 
            "org.adaptlab.chpir.participanttracker.newparticipantfragment.participant_type_id";
    
    private Participant mParticipant;
    private ParticipantType mParticipantType;
    private LinearLayout mParticipantPropertiesContainer;
    
    private TextView mParticipantTitle;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        if (savedInstanceState != null) {
            mParticipantType = ParticipantType.findById(savedInstanceState.getLong(EXTRA_PARTICIPANT_TYPE_ID));
        } else {
            Long participantTypeId = getActivity().getIntent().getLongExtra(EXTRA_PARTICIPANT_TYPE_ID, -1);
            if (participantTypeId == -1) return;

            mParticipantType = ParticipantType.findById(participantTypeId);
        }
    }
        
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
            Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_new_participant, parent,
                false);
        
        mParticipantTitle = (TextView) v.findViewById(R.id.new_participant_title);
        mParticipantTitle.setText(getString(R.string.new_participant_prefix) + mParticipantType.getLabel());
        
        mParticipantPropertiesContainer = (LinearLayout) v.findViewById(R.id.new_participant_properties_container);

        for (Property property : mParticipantType.getProperties()) {
            TextView textView = new TextView(getActivity());
            textView.setText(property.getLabel());
            EditText editText = new EditText(getActivity());
            mParticipantPropertiesContainer.addView(textView);
            mParticipantPropertiesContainer.addView(editText);
        }
        
        return v;
    }
}
