package org.adaptlab.chpir.android.participanttracker;

import java.util.HashMap;
import java.util.List;

import org.adaptlab.chpir.android.participanttracker.models.Participant;
import org.adaptlab.chpir.android.participanttracker.models.ParticipantProperty;
import org.adaptlab.chpir.android.participanttracker.models.ParticipantType;
import org.adaptlab.chpir.android.participanttracker.models.Property;
import org.adaptlab.chpir.android.participanttracker.models.Relationship;
import org.adaptlab.chpir.android.participanttracker.models.RelationshipType;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
    private HashMap<RelationshipType, Participant> mRelationshipFields;
    
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
            mRelationshipFields = new HashMap<RelationshipType, Participant>();
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
                
        for (RelationshipType relationshipType : mParticipantType.getRelationshipTypes()) {
            attachSelectRelationshipButton(relationshipType);
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
            if (property.getRequired() && mPropertyFields.get(property).getText().toString().trim().equals("")) {
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
        } else if (property.getTypeOf() == Property.PropertyType.DATE) {
            editText.setInputType(InputType.TYPE_CLASS_DATETIME | InputType.TYPE_DATETIME_VARIATION_DATE);
        }

        mPropertyFields.put(property, editText);
        mParticipantPropertiesContainer.addView(editText);
    }
       
    private void attachSelectRelationshipButton(final RelationshipType relationshipType) {
        TextView textView = new TextView(getActivity());
        textView.setTextAppearance(getActivity(), R.style.sectionHeader);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 50, 0, 0);
        textView.setLayoutParams(layoutParams);
        mParticipantPropertiesContainer.addView(textView);
        textView.setText(relationshipType.getLabel());
        
        final Button button = new Button(getActivity());
        button.setText("Select " + relationshipType.getRelatedParticipantType().getLabel());
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                displayRelationshipPicker(relationshipType, button);
            }
        });
        mParticipantPropertiesContainer.addView(button);
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
        
        for (RelationshipType relationshipType : mRelationshipFields.keySet()) {
            Relationship relationship = new Relationship(relationshipType);
            relationship.setParticipantOwner(participant);
            relationship.setParticipantRelated(mRelationshipFields.get(relationshipType));
            relationship.save();
        }
        
        getActivity().setResult(Activity.RESULT_OK);
        getActivity().finish();
    }
    
    public void displayRelationshipPicker(final RelationshipType relationshipType, final Button button) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Choose " + relationshipType.getRelatedParticipantType().getLabel());
        final List<Participant> relationshipParticipants = Participant.getAllByParticipantType(relationshipType.getRelatedParticipantType());
        CharSequence[] relationshipParticipantLabels = new CharSequence[relationshipParticipants.size()];
        for (int i = 0; i < relationshipParticipants.size(); i++) {
            relationshipParticipantLabels[i] = relationshipParticipants.get(i).getLabel();
        }
        builder.setSingleChoiceItems(relationshipParticipantLabels, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mRelationshipFields.put(relationshipType, relationshipParticipants.get(which));
                button.setText(relationshipParticipants.get(which).getLabel());
                dialog.cancel();
            }
        });
        
        builder.show();
    }
}
