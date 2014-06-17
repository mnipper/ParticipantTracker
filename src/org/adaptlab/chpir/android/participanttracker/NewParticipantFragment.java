package org.adaptlab.chpir.android.participanttracker;

import java.util.HashMap;

import org.adaptlab.chpir.android.participanttracker.models.Participant;
import org.adaptlab.chpir.android.participanttracker.models.ParticipantProperty;
import org.adaptlab.chpir.android.participanttracker.models.ParticipantType;
import org.adaptlab.chpir.android.participanttracker.models.Property;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class NewParticipantFragment extends Fragment {
    private static final String TAG = "NewParticipantFragment";
    public final static String EXTRA_PARTICIPANT_TYPE_ID = 
            "org.adaptlab.chpir.participanttracker.newparticipantfragment.participant_type_id";
    
    private ParticipantType mParticipantType;
    private HashMap<Property, EditText> mPropertyFields;
    
    private LinearLayout mParticipantPropertiesContainer;
    private TextView mParticipantTitle;
    private Button mSaveParticipantButton;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        if (savedInstanceState != null) {
            mParticipantType = ParticipantType.findById(savedInstanceState.getLong(EXTRA_PARTICIPANT_TYPE_ID));
        } else {
            Long participantTypeId = getActivity().getIntent().getLongExtra(EXTRA_PARTICIPANT_TYPE_ID, -1);
            if (participantTypeId == -1) return;

            mParticipantType = ParticipantType.findById(participantTypeId);
            mPropertyFields = new HashMap<Property, EditText>();
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
            final EditText editText = new EditText(getActivity());
            if (property.getTypeOf() == Property.PropertyType.INTEGER) {
                editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);
            }

            mPropertyFields.put(property, editText);
            mParticipantPropertiesContainer.addView(textView);
            mParticipantPropertiesContainer.addView(editText);
            
            attachRequiredLabel(property);
        }
        
        mSaveParticipantButton = (Button) v.findViewById(R.id.save_participant_button);
        mSaveParticipantButton.setText(getString(R.string.save_participant_prefix) + mParticipantType.getLabel());
        mSaveParticipantButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (isMissingRequiredValue()) {
                    return;
                }
                
                Participant participant = new Participant(mParticipantType);
                participant.save();
                
                for (Property property : mParticipantType.getProperties()) {
                    ParticipantProperty participantProperty = new ParticipantProperty(participant, property, mPropertyFields.get(property).getText().toString());
                    participantProperty.save();
                }
                
                getActivity().setResult(Activity.RESULT_OK);
                getActivity().finish();
            } 
        });
        
        return v;
    }
    
    private boolean isMissingRequiredValue() {
        for (Property property : mParticipantType.getProperties()) {
            if (mPropertyFields.get(property).getText().toString().equals("")) {
                Toast.makeText(getActivity(), property.getLabel() + getString(R.string.is_a_required_field), Toast.LENGTH_LONG).show();
                return true;
            }
        }
        
        return false;
    }
    
    private void attachRequiredLabel(Property property) {
        if (property.getRequired()) {
            TextView requiredTextView = new TextView(getActivity());
            requiredTextView.setText(getString(R.string.required_field));
            requiredTextView.setTextColor(Color.RED);
            mParticipantPropertiesContainer.addView(requiredTextView);
        }
    }
}
