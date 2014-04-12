package com.example.tunepulze;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import com.example.tunepulze.*;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
//import android.widget.ExpandableListAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.Toast;

public class MainActivity extends FragmentActivity implements
		ActionBar.TabListener {

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Set up the action bar.
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

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
						actionBar.setSelectedNavigationItem(position);
					}
				});

		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			// Create a tab with text corresponding to the page title defined by
			// the adapter. Also specify this Activity object, which implements
			// the TabListener interface, as the callback (listener) for when
			// this tab is selected.
			actionBar.addTab(actionBar.newTab()
					.setText(mSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
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
			// getItem is called to instantiate the fragment for the given page.
			// Return a DummySectionFragment (defined as a static inner class
			// below) with the page number as its lone argument.
			
			
			
			switch (position) {
            case 0:
            	  // The other sections of the app are dummy placeholders.
               
            	return  new LaunchMusic();
            case 1:
                // The first section of the app is the most interesting -- it offers
                // a launchpad into the other demonstrations in this example application.
//                return new HeartFragment();
            	return new  LaunchHeart();
            case 2:
                // The first section of the app is the most interesting -- it offers
                // a launchpad into the other demonstrations in this example application.
                return new LaunchSteps();
          
                default:
                	
                	  // The other sections of the app are dummy placeholders.
                    Fragment fragment = new DummySectionFragment();
                    Bundle args = new Bundle();
                    args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, position + 1);
                    fragment.setArguments(args);
                    return fragment;
        }
		}

		@Override
		public int getCount() {
			// Show 3 total pages.
			return 3;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.title_section1).toUpperCase(l);
			case 1:
				return getString(R.string.title_section2).toUpperCase(l);
			case 2:
				return getString(R.string.title_section3).toUpperCase(l);
			}
			return null;
		}
	}
	

	
	public static class LaunchMusic extends Fragment {
		
		 private View rootView ;
		 private ViewGroup mContainerView;
		 public List<String> PopURLs=new ArrayList<String>(); 
		 private List<String> CountryURLs=new ArrayList<String>();
		 private List<String> RockURLs=new ArrayList<String>();
		
		ExpandableListAdapter listAdapter;
	    ExpandableListView expListView;
	    List<String> listDataHeader;
	    HashMap<String, List<String>> listDataChild;
	    
	    
	    
		 @Override
	      public View onCreateView(LayoutInflater inflater, ViewGroup container,
	              Bundle savedInstanceState) {
	         rootView= inflater.inflate(R.layout.firstlaunch, container, false);
	         //mContainerView = (ViewGroup) rootView.findViewById(R.id.container);
	         expListView = (ExpandableListView) rootView.findViewById(R.id.lvExp);
	         rootView.findViewById(android.R.id.empty).setVisibility(View.GONE);
	         // preparing list data
	         prepareListData();
	         prepareURLs();
	         
	         listAdapter = new ExpandableListAdapter(getActivity(), listDataHeader, listDataChild);
	         
	         // setting list adapter
	         expListView.setAdapter(listAdapter);
	         
	      // Listview on child click listener
		        expListView.setOnChildClickListener(new OnChildClickListener() {
		 
		            @Override
		            public boolean onChildClick(ExpandableListView parent, View v,
		                    int groupPosition, int childPosition, long id) {
		            	
		            	if(listDataHeader.get(groupPosition)=="Pop")
		            	{
		            		
		            		BroadcastAppActivity.URL=PopURLs.get(childPosition);
		            		
		            	}
		            	
		                Toast.makeText(
		                        getActivity(),
		                        listDataHeader.get(groupPosition)
		                                + " : "
		                                + listDataChild.get(
		                                        listDataHeader.get(groupPosition)).get(
		                                        childPosition), Toast.LENGTH_SHORT)
		                        .show();
		                Intent intent = new Intent(getActivity(), BroadcastAppActivity.class);
                        startActivity(intent);
		                return false;
		            }
		        });
			 
	         
	          //mContainerView.addView(expListView, 0);
	         //expListView.addView(expListView,0);
	         Button demo_button = (Button) rootView.findViewById(R.id.demo_collection_button);
	         demo_button.setText("Listen music");
			
				  // Demonstration of a collection-browsing activity.
	            rootView.findViewById(R.id.demo_collection_button)
	                    .setOnClickListener(new View.OnClickListener() {
	                        @Override
	                        public void onClick(View view) {
	                            Intent intent = new Intent(getActivity(), BroadcastAppActivity.class);
	                            startActivity(intent);
	                        }
	                    });


	          return rootView;
	      }
		 
		 private void prepareURLs()
		 {
			 PopURLs.add("hHUbLv4ThOo");
			 PopURLs.add("hT_nvWreIhg");
			 PopURLs.add("y6Sxv-sUYtM");
			 PopURLs.add("mWRsgZuwf_8");
			 PopURLs.add("nlcIKh6sBtc");
			 PopURLs.add("-2U0Ivkn2Ds");
		 }
		 private void prepareListData() {
		        
			 
			 	listDataHeader = new ArrayList<String>();
		        listDataChild = new HashMap<String, List<String>>();
		 
		        // Adding child data
		        listDataHeader.add("Pop");
		        listDataHeader.add("Country");
		        listDataHeader.add("Rock");
		 
		        // Adding child data
		        List<String> pop = new ArrayList<String>();
		        pop.add("Timber");
		        pop.add("Counting Stars");
		        pop.add("Happy");
		        pop.add("Demons");
		        pop.add("Royals");
		        pop.add("Say Something");
		 
		        List<String> Country = new ArrayList<String>();
		        Country.add("The Conjuring");
		        Country.add("Despicable Me 2");
		        Country.add("Turbo");
		        Country.add("Grown Ups 2");
		        Country.add("Red 2");
		        Country.add("The Wolverine");
		 
		        List<String> Rock = new ArrayList<String>();
		        Rock.add("2 Guns");
		        Rock.add("The Smurfs 2");
		        Rock.add("The Spectacular Now");
		        Rock.add("The Canyons");
		        Rock.add("Europa Report");
		 
		        listDataChild.put(listDataHeader.get(0), pop); // Header, Child data
		        listDataChild.put(listDataHeader.get(1), Country);
		        listDataChild.put(listDataHeader.get(2), Rock);
		    }
		 
		
		
	}
	
	
	public static class LaunchHeart extends Fragment {
		
		private View rootView;
		private ViewGroup mContainerView;
		 @Override
	      public View onCreateView(LayoutInflater inflater, ViewGroup container,
	              Bundle savedInstanceState) {
	          rootView = inflater.inflate(R.layout.firstlaunch, container, false);

	           mContainerView = (ViewGroup) rootView.findViewById(R.id.container);	  
	           addItem();
	           addItem();
				
	           Button demo_button = (Button) rootView.findViewById(R.id.demo_collection_button);
	           demo_button.setText("Check HeartRate");
	           
				  // Demonstration of a collection-browsing activity.
	            rootView.findViewById(R.id.demo_collection_button)
	                    .setOnClickListener(new View.OnClickListener() {
	                        @Override
	                        public void onClick(View view) {
	                            Intent intent = new Intent(getActivity(), HeartRateActivity.class);
	                            startActivity(intent);
	                        }
	                    });
	            
	            
	          return rootView;
	      }
		 private void addItem() {
			 rootView.findViewById(android.R.id.empty).setVisibility(View.GONE);
		        // Instantiate a new "row" view.in
			 final ViewGroup newView = (ViewGroup) LayoutInflater.from(getActivity()).inflate(
		                R.layout.list_item_example, mContainerView, false);
			 
		     //   final ViewGroup newView = (ViewGroup) LayoutInflater.from(this).inflate(
		       //         R.layout.list_item_example, mContainerView, false);

		        // Set the text in the new row to a random country.
		        ((TextView) newView.findViewById(android.R.id.text1)).setText(
		               "helloworld");
		      

		        // Because mContainerView has android:animateLayoutChanges set to true,
		        // adding this view is automatically animated.
		       mContainerView.addView(newView, 0);
		       
		    }
	}

	
	 
	
	  /**
   * A fragment that launches other parts of the demo application.
   */
  public static class LaunchSteps extends Fragment {

	  private View rootView;
		private ViewGroup mContainerView;
	  
      @Override
      public View onCreateView(LayoutInflater inflater, ViewGroup container,
              Bundle savedInstanceState) {
           rootView = inflater.inflate(R.layout.firstlaunch, container, false);

          
           mContainerView = (ViewGroup) rootView.findViewById(R.id.container);	  
        
			
           Button demo_button = (Button) rootView.findViewById(R.id.demo_collection_button);
           demo_button.setText("Check Calories burnt");
			
			  // Demonstration of a collection-browsing activity.
            rootView.findViewById(R.id.demo_collection_button)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(getActivity(), Pedometer.class);
                            startActivity(intent);
                        }
                    });


          return rootView;
      }
  }

  
	/**
	 * A dummy fragment representing a section of the app, but that simply
	 * displays dummy text.
	 */
	public static class DummySectionFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		public static final String ARG_SECTION_NUMBER = "section_number";

		public DummySectionFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main_dummy,
					container, false);
			TextView dummyTextView = (TextView) rootView
					.findViewById(R.id.section_label);
			dummyTextView.setText(Integer.toString(getArguments().getInt(
					ARG_SECTION_NUMBER)));
			return rootView;
		}



	}

}
