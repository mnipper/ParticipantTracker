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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class NewParticipantFragment extends Fragment {
    private static final String TAG = "NewParticipantFragment";
    public final static String EXTRA_PARTICIPANT_TYPE_ID = 
            "org.adaptlab.chpir.participanttracker.newparticipantfragment.participant_type_id";
    
    private ParticipantType mParticipantType;
    private HashMap<Property, EditText> mPropertyFields;
    
    private LinearLayout mParticipantPropertiesContainer;
    
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
        
        
        getActivity().setTitle(getString(R.string.new_participant_prefix) + mParticipantType.getLabel());
    }
        
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
            Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        
        View v = inflater.inflate(R.layout.fragment_new_participant, parent,
                false);
        
        mParticipantPropertiesContainer = (LinearLayout) v.findViewById(R.id.new_participant_properties_container);

        for (Property property : mParticipantType.getProperties()) {
            attachLabelForProperty(property);
            attachFieldForProperty(property);
            attachRequiredLabel(property);
        }
        
        return v;
    }
    
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.new_participant, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                saveParticipant();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    private boolean isMissingRequiredValue() {
        boolean missingField = false;
        for (Property property : mParticipantType.getProperties()) {
            if (mPropertyFields.get(property).getText().toString().trim().equals("")) {
                mPropertyFields.get(property).setError(getString(R.string.required_field));
                missingField = true;
            }
        }
        
        return missingField;
    }
    
    private void attachRequiredLabel(Property property) {
        if (property.getRequired()) {
            TextView requiredTextView = new TextView(getActivity());
            requiredTextView.setText(getString(R.string.required_field));
            requiredTextView.setTextColor(Color.RED);
            mParticipantPropertiesContainer.addView(requiredTextView);
        }
    }
    
    private void attachLabelForProperty(Property property) {
        TextView textView = new TextView(getActivity());
        textView.setText(property.getLabel());
        textView.setTextAppearance(getActivity(), R.style.sectionHeader);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 50, 0, 0);
        textView.setLayoutParams(layoutParams);
        mParticipantPropertiesContainer.addView(textView);
    }
    
    private void attachFieldForProperty(Property property) {        
        final EditText editText = new EditText(getActivity());
        if (property.getTypeOf() == Property.PropertyType.INTEGER) {
            editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);
        }

        mPropertyFields.put(property, editText);
        mParticipantPropertiesContainer.addView(editText);
    }
    
    private void saveParticipant() {
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
}
