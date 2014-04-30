package org.adaptlab.chpir.android.participanttracker;

import org.adaptlab.chpir.android.models.Participant;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

public class ParticipantDetailFragment extends Fragment {
    private static final String TAG = "ParticipantDetailFragment";
    public final static String EXTRA_PARTICIPANT_ID = 
            "org.adaptlab.chpir.participanttracker.participantdetailfragment.participant_id";
    
    private Participant mParticipant;
    
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
}
