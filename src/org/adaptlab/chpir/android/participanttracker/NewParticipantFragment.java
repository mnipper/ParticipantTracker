package org.adaptlab.chpir.android.participanttracker;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
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
import android.widget.Toast;

import org.adaptlab.chpir.android.participanttracker.models.Participant;
import org.adaptlab.chpir.android.participanttracker.models.ParticipantProperty;
import org.adaptlab.chpir.android.participanttracker.models.ParticipantType;
import org.adaptlab.chpir.android.participanttracker.models.Property;
import org.adaptlab.chpir.android.participanttracker.models.Relationship;
import org.adaptlab.chpir.android.participanttracker.models.RelationshipType;

import java.util.HashMap;
import java.util.List;

public class NewParticipantFragment extends Fragment {
    private static final String TAG = "NewParticipantFragment";
    public final static String EXTRA_PARTICIPANT_TYPE_ID =
            "org.adaptlab.chpir.participanttracker.newparticipantfragment.participant_type_id";
    public final static String EXTRA_PARTICIPANT_ID =
            "org.adaptlab.chpir.participanttracker.newparticipantfragment.participant_id";

    private ParticipantType mParticipantType;
    private Participant mParticipant;
    private HashMap<Long, View> mPropertyFields;
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
            mPropertyFields = new HashMap<Long, View>();
            mRelationshipFields = new HashMap<RelationshipType, Participant>();
        }

        loadOrCreateParticipant();
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
        super.onCreateOptionsMenu(menu, inflater);
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
            if (property.getRequired() && getValueForProperty(property.getRemoteId()).trim().equals("")) {
                if (mPropertyFields.get(property.getRemoteId()) instanceof EditText) {
                    ((EditText) mPropertyFields.get(property.getRemoteId())).setError(getString(R.string.required_field));
                }
                missingField = true;
            }
        }

        return missingField;
    }

    private boolean hasInvalidValidator() {
        boolean invalid = false;
        for (Property property : mParticipantType.getProperties()) {
            if (property.hasValidator() && !property.getValidationCallable().validate(getValueForProperty(property.getRemoteId()))) {
                if (mPropertyFields.get(property.getRemoteId()) instanceof EditText) {
                    ((EditText) mPropertyFields.get(property.getRemoteId())).setError(getString(R.string.invalid_validator));
                }

                Toast.makeText(getActivity(),
                        property.getLabel() + " " + getString(R.string.invalid_validator_toast),
                        Toast.LENGTH_SHORT).show();

                invalid = true;
            }
        }

        return invalid;
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

    private void attachFieldForProperty(final Property property) {
        String propertyValue = "";
        if (mParticipant.hasParticipantProperty(property)) {
            propertyValue = mParticipant.getParticipantProperty(property).getValue();
        }

        View propertyView = null;

        if (property.getTypeOf() == Property.PropertyType.INTEGER) {
            propertyView = new EditText(getActivity());
            ((EditText) propertyView).setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);
            ((EditText) propertyView).setText(propertyValue);
            attachValidator(property, (EditText) propertyView);
        } else if (property.getTypeOf() == Property.PropertyType.DATE) {
            propertyView = new SerializableDatePicker(getActivity());
            ((SerializableDatePicker) propertyView).setCalendarViewShown(false);
            ((SerializableDatePicker) propertyView).deserialize(propertyValue);
        } else {
            propertyView = new EditText(getActivity());
            ((EditText) propertyView).setText(propertyValue);
            attachValidator(property, (EditText) propertyView);
        }

        mPropertyFields.put(property.getRemoteId(), propertyView);
        mParticipantPropertiesContainer.addView(propertyView);
    }

    private void attachValidator(final Property property, final EditText propertyView) {
        propertyView.addTextChangedListener(new TextWatcher() {
            private boolean backspacing = false;

            public void afterTextChanged(Editable s) {
                if (!property.hasValidator()) return;

                if (!backspacing) {
                    propertyView.removeTextChangedListener(this);
                    propertyView.setText(property.getValidationCallable().formatText(s.toString()));
                    propertyView.setSelection(propertyView.getText().length());
                    propertyView.addTextChangedListener(this);
                }

                if (!property.getValidationCallable().validate(propertyView.getText().toString())) {
                    propertyView.setError(getString(R.string.invalid_validator));
                    propertyView.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);
                } else {
                    propertyView.setError(null);
                    propertyView.getBackground().setColorFilter(Color.TRANSPARENT, PorterDuff.Mode.SRC_ATOP);
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                backspacing = before > count;
            }
        });
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

        if (mParticipant.hasRelationshipByRelationshipType(relationshipType)) {
            Participant relatedParticipant = mParticipant.getRelationshipByRelationshipType(relationshipType).getParticipantRelated();
            button.setText(relatedParticipant.getLabel());
            mRelationshipFields.put(relationshipType, relatedParticipant);
        } else {
            button.setText("Select " + relationshipType.getRelatedParticipantType().getLabel());
        }

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                displayRelationshipPicker(relationshipType, button);
            }
        });
        mParticipantPropertiesContainer.addView(button);
    }

    private void saveParticipant() {
        if (isMissingRequiredValue() || hasInvalidValidator()) {
            return;
        }

        mParticipant.save();

        for (Property property : mParticipantType.getProperties()) {
            ParticipantProperty participantProperty = mParticipant.getParticipantProperty(property);
            participantProperty.setValue(getValueForProperty(property.getRemoteId()));
            participantProperty.save();
        }

        for (RelationshipType relationshipType : mRelationshipFields.keySet()) {
            Relationship relationship = mParticipant.getRelationshipByRelationshipType(relationshipType);
            relationship.setParticipantOwner(mParticipant);
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

        builder.setSingleChoiceItems(relationshipParticipantLabels, -1, null);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                int selectedPosition = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                if (selectedPosition == -1) return;
                mRelationshipFields.put(relationshipType, relationshipParticipants.get(selectedPosition));
                button.setText(relationshipParticipants.get(selectedPosition).getLabel());
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void loadOrCreateParticipant() {
        Long participantId = getActivity().getIntent().getLongExtra(EXTRA_PARTICIPANT_ID, -1);
        if (participantId == -1) {
            mParticipant = new Participant(mParticipantType);
            getActivity().setTitle(getString(R.string.new_participant_prefix) + mParticipantType.getLabel());
        } else {
            mParticipant = Participant.findById(participantId);
            getActivity().setTitle(mParticipant.getLabel());
        }
    }

    private String getValueForProperty(Long property) {
        if (mPropertyFields.get(property) instanceof EditText)
            return ((EditText) mPropertyFields.get(property)).getText().toString();
        else if (mPropertyFields.get(property) instanceof SerializableDatePicker)
            return ((SerializableDatePicker) mPropertyFields.get(property)).serialize();
        return "";
    }
}
