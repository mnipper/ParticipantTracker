package org.adaptlab.chpir.android.participanttracker;

import java.util.List;
import java.util.Locale;

import org.adaptlab.chpir.android.participanttracker.models.Participant;
import org.adaptlab.chpir.android.participanttracker.models.ParticipantType;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.ListFragment;
import android.support.v4.view.ViewPager;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.TextView;
import android.widget.Toast;

public class ParticipantListActivity extends FragmentActivity implements
        ActionBar.TabListener {
    private static final String TAG = "ParticipantListActivity";
    public final static int DATA_CHANGED = 0;

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
     * will keep every loaded fragment in memory. If this becomes too memory
     * intensive, it may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;
    
   ActionBar mActionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppUtil.appInit(this);

        setContentView(R.layout.activity_participant_list);

        // Set up the action bar.
        mActionBar = getActionBar();
        mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the app.
        mSectionsPagerAdapter = new SectionsPagerAdapter(
                getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager
                .setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
                    @Override
                    public void onPageSelected(int position) {
                        mActionBar.setSelectedNavigationItem(position);
                    }
                });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            mActionBar.addTab(mActionBar.newTab()
                    .setText(mSectionsPagerAdapter.getPageTitle(i))     
                    .setTabListener(this));
        }
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.participant_list, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {	
    	case R.id.action_settings:
    		displayPassWordPrompt();
    		return true;
    	case R.id.menu_item_refresh:
    		authenticateUser();
    		return true;
    	default:
    		return super.onOptionsItemSelected(item);
    	}
    }

    private void authenticateUser() {
    	Intent i = new Intent(ParticipantListActivity.this, LoginActivity.class);
        startActivityForResult(i, DATA_CHANGED);		
	}
    
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == DATA_CHANGED) {
            if (resultCode == RESULT_OK) {
                mSectionsPagerAdapter.notifyDataSetChanged();
                refreshSectionTabs();
            }
        }
    }
    
    private void refreshSectionTabs() {
    	mActionBar.removeAllTabs();
    	for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            mActionBar.addTab(mActionBar.newTab()
                    .setText(mSectionsPagerAdapter.getPageTitle(i))     
                    .setTabListener(this));
        }
    }

	private void displayPassWordPrompt() {
    	final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        new AlertDialog.Builder(this)
        .setTitle(R.string.password_title)
        .setMessage(R.string.password_message)
        .setView(input)
        .setPositiveButton(R.string.okay, new DialogInterface.OnClickListener() { 
            public void onClick(DialogInterface dialog, int button) {
                if (AppUtil.checkAdminPassword(input.getText().toString())) {
                    Intent i = new Intent(ParticipantListActivity.this, AdminActivity.class);
                    startActivity(i);
                } else {
                    Toast.makeText(ParticipantListActivity.this, R.string.incorrect_password, Toast.LENGTH_LONG).show();
                }
            }
        }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int button) { }
        }).show();
    }

	@Override
    public void onTabSelected(ActionBar.Tab tab,
            FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab,
            FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab,
            FragmentTransaction fragmentTransaction) {
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = new ParticipantListFragment();
            Bundle args = new Bundle();
            args.putInt(ParticipantListFragment.ARG_SECTION_NUMBER, position);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public int getCount() {
            return ParticipantType.getCount();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            if (position >= 0 && position < ParticipantType.getCount()) {
                return ParticipantType.getAll().get(position).getLabel();
            }
            return null;
        }
    }

    public static class ParticipantListFragment extends ListFragment {
        private static final String TAG = "ParticipantListFragment";
        private static final int CREATE_NEW_PARTICIPANT = 0;
        
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        public static final String ARG_SECTION_NUMBER = "section_number";
        private Button mNewParticipantButton;
        private String currentQuery = null;

        final private OnQueryTextListener queryListener = new OnQueryTextListener() {       
            @Override
            public boolean onQueryTextChange(String newText) {
                if (TextUtils.isEmpty(newText)) {              
                    currentQuery = null;
                    setParticipantListAdapter(currentQuery);
                } else {
                    currentQuery = newText;
                }
                
                return false;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                setParticipantListAdapter(currentQuery);
                return false;
            }
        };
        
        public ParticipantListFragment() {          
        }
        
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }
        
        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            setHasOptionsMenu(true);
            setParticipantListAdapter(currentQuery);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_participant_list_dummy, container, false);
            
            mNewParticipantButton = (Button) rootView.findViewById(R.id.new_participant_button);            
            mNewParticipantButton.setText(getString(R.string.new_participant_prefix) + getParticipantType().getLabel());
            mNewParticipantButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent i = new Intent(getActivity(), NewParticipantActivity.class);
                    i.putExtra(NewParticipantFragment.EXTRA_PARTICIPANT_TYPE_ID, getParticipantType().getId());
                    startActivityForResult(i, CREATE_NEW_PARTICIPANT);
                }
            });

            return rootView;
        }
        
        @Override
        public void onListItemClick(ListView l, View v, int position, long id) {
            Participant participant = ((ParticipantAdapter) getListAdapter()).getItem(position);
            Intent i = new Intent(getActivity(), ParticipantDetailActivity.class);
            i.putExtra(ParticipantDetailFragment.EXTRA_PARTICIPANT_ID, participant.getId());
            startActivity(i);
        }   
        
        @Override 
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
                SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
                searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
                searchView.setIconifiedByDefault(true);
                searchView.setOnQueryTextListener(queryListener);
            }
        }
        
        @Override
        public void onResume() {
            super.onResume();
            setParticipantListAdapter(currentQuery);
        }
        
        private ParticipantType getParticipantType() {
            int participantTypeId = getArguments().getInt(ARG_SECTION_NUMBER, 0);
            ParticipantType participantType = ParticipantType.getAll().get(participantTypeId);
            return participantType;
        }
        
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);

            if (requestCode == CREATE_NEW_PARTICIPANT) {
                if (resultCode == RESULT_OK) {
                    setParticipantListAdapter(currentQuery);
                }
            }
        }
        
        private void setParticipantListAdapter(String query) {
            List<Participant> participants;
            if (query != null) {
                participants = Participant.getAllByParticipantType(getParticipantType(), query);   
            } else {
                participants = Participant.getAllByParticipantType(getParticipantType());
            }
            
            setListAdapter(new ParticipantAdapter(getActivity(), participants));
        }
    }
    
    private static class ParticipantAdapter extends ArrayAdapter<Participant> {
        private Context mContext;
        
        public ParticipantAdapter(Context context, List<Participant> participants) {
            super(context, 0, participants);
            mContext = context;
        }
               
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = ((Activity) mContext).getLayoutInflater().inflate(
                        R.layout.list_item_participant, null);
            }
            
            Participant participant = getItem(position);
            
            TextView titleTextView = (TextView) convertView.findViewById(R.id.participant_list_item_titleTextView);           
            titleTextView.setText(participant.getLabel());
            
            return convertView;
        }
    }
}
