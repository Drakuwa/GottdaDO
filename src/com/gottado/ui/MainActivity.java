package com.gottado.ui;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.gottado.R;
import com.gottado.adapters.TaskAdapter;
import com.gottado.dao.DAO;
import com.gottado.dao.LocalDAO;
import com.gottado.dom.Task;
import com.gottado.utilities.CallBackListener;
import com.gottado.utilities.Log;
import com.gottado.utilities.Utilities;

public class MainActivity extends ActionBarActivity implements CallBackListener {
	
	private TextView noContent;
	private List<Task> tasks = new ArrayList<Task>();
	private TaskAdapter sta;
	private ListView lv;
	private DAO db;
	private Utilities u = new Utilities(this);
	public static String status = "All tasks";
	private TextView statusTextView;
	private static TextView count;
	
	private DrawerLayout			mDrawerLayout;
	private ListView				mDrawerList;
	private ActionBarDrawerToggle	mDrawerToggle;

	private CharSequence			mDrawerTitle;
	private CharSequence			mTitle;
	private String[]				mDrawerOptions;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// set the drawer
		initDrawer();
		
		// initialize the other views
		initViews();
		
		// get the db instance
		db = LocalDAO.getInstance(this);
		
		// set the callback listener to Utilities
		u.setListener(this);
		
		// initialize the list view
		initListView();
		updateList();
	}
	
	private void initViews(){
		count = (TextView) findViewById(R.id.taskStatusCount);
		noContent = (TextView) findViewById(R.id.taskNoContent);
		statusTextView = (TextView) findViewById(R.id.taskStatus);
		statusTextView.setText(status);
	}
	
	private void initListView(){
		// GET THE LIST
		lv = (ListView) findViewById(R.id.tasksList);
		lv.setTextFilterEnabled(true);
		
		// CREATE BASE ADAPTER
		sta = new TaskAdapter(MainActivity.this, tasks);
		
		// SET AS CURRENT LIST
		lv.setAdapter(sta);
	}
	
	private void updateList(){
		// GET YOUR TASKS
		tasks.clear();
		try {
			if(status.equals("All tasks")){
				tasks.addAll(db.getAllTasks());
				if(tasks.size()==0){
					noContent.setText(R.string.no_content);
					noContent.setVisibility(View.VISIBLE);
				} else noContent.setVisibility(View.GONE);
			} else if(status.equals("Completed tasks")){
				tasks.addAll(db.getAllCompletedTasks(true));
				if(tasks.size()==0){
    				noContent.setText(R.string.no_completed);
    				noContent.setVisibility(View.VISIBLE);
    			} else noContent.setVisibility(View.GONE);
			} else if(status.equals("Pending tasks")){
				tasks.addAll(db.getAllCompletedTasks(false));
				if(tasks.size()==0){
    				noContent.setText(R.string.no_unfinished);
    				noContent.setVisibility(View.VISIBLE);
    			} else noContent.setVisibility(View.GONE);
			} else if(status.equals("Todays tasks")){
				tasks.addAll(db.getAllTasksDueToday());
				if(tasks.size()==0){
    				noContent.setText(R.string.no_today);
    				noContent.setVisibility(View.VISIBLE);
    			} else noContent.setVisibility(View.GONE);
			} else if(status.equals("Priority filtered tasks")){
				// if we have priority filtered view, and add a task, show all after
				status = "All tasks";statusTextView.setText(status);
				tasks.addAll(db.getAllTasks());
				if(tasks.size()==0){
					noContent.setText(R.string.no_content);
					noContent.setVisibility(View.VISIBLE);
				} else noContent.setVisibility(View.GONE);
			}
		} catch (ParseException e) {
			Log.e(Log.TAG, e.getLocalizedMessage());
		}
		sta.notifyDataSetChanged();
		count.setText(String.valueOf(tasks.size()));	
	}
	
	private void initDrawer(){
		mTitle = mDrawerTitle = getTitle();
		mDrawerOptions = getResources().getStringArray(R.array.drawer_options);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);
		
		// set a custom shadow that overlays the main content when the drawer
		// opens
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
		// set up the drawer's list view with items and click listener
		mDrawerList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mDrawerOptions));
		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
		
		// enable ActionBar app icon to behave as action to toggle nav drawer
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		
		// ActionBarDrawerToggle ties together the the proper interactions
		// between the sliding drawer and the action bar app icon
		mDrawerToggle = new ActionBarDrawerToggle(this, /* host Activity */
			mDrawerLayout, /* DrawerLayout object */
			R.drawable.ic_drawer, /* nav drawer image to replace 'Up' caret */
			R.string.drawer_open, /* "open drawer" description for accessibility */
			R.string.drawer_close /* "close drawer" description for accessibility */
		){
			public void onDrawerClosed(View view){
				getSupportActionBar().setTitle(mTitle);
				// invalidateOptionsMenu(); // creates call to
				// onPrepareOptionsMenu()
			}
			public void onDrawerOpened(View drawerView)	{
				getSupportActionBar().setTitle(mDrawerTitle);
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		// The action bar home/up action should open or close the drawer.
		// ActionBarDrawerToggle will take care of this.
		if (mDrawerToggle.onOptionsItemSelected(item)){return true;}
		
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	        case R.id.action_add:
	        	AddOrModifyTaskDialog d = new AddOrModifyTaskDialog(MainActivity.this, null, getDialog());
	        	d.showDialog();
	            return true;
	        case R.id.action_sort_priority:
	        	Collections.sort(tasks, new Comparator<Task>() {
	                public int compare(Task o1, Task o2) {
	                    //Sorts by 'Priority' property
	                    return o1.getPriority().getCustomOrdinal()>o2.getPriority().getCustomOrdinal()?-1
	                    		:o1.getPriority().getCustomOrdinal()<o2.getPriority().getCustomOrdinal()?1
	                    				:doSecodaryOrderSort(o1,o2);
	                }

	                //If 'Priority' property is equal sorts by 'Id' property
	                public int doSecodaryOrderSort(Task o1,Task o2) {
	                    return o1.getId()>o2.getId()?-1
	                    		:o1.getId()<o2.getId()?1:0;
	                }
	            });
	        	sta.notifyDataSetChanged();
	            return true;
	        case R.id.action_sort_date:
	        	Collections.sort(tasks, new Comparator<Task>() {
	                public int compare(Task o1, Task o2) {
	                    //Sorts by 'Due date' property
	                    return o1.getDueDate().getTime()<o2.getDueDate().getTime()?-1
	                    		:o1.getDueDate().getTime()>o2.getDueDate().getTime()?1
	                    				:doSecodaryOrderSort(o1,o2);
	                }
	                //If 'dueDate' property is equal sorts by 'Id' property
	                public int doSecodaryOrderSort(Task o1,Task o2) {
	                    return o1.getId()<o2.getId()?-1
	                    		:o1.getId()>o2.getId()?1:0;
	                }
	            });
	        	sta.notifyDataSetChanged();
	            return true;
	        case R.id.action_sort_clear:
	        	Collections.sort(tasks, new Comparator<Task>() {
	                public int compare(Task o1, Task o2) {
	                    //Sorts by 'Id' property
	                    return o1.getId()<o2.getId()?-1
	                    		:o1.getId()>o2.getId()?1
	                    				:doSecodaryOrderSort(o1,o2);
	                }
	                public int doSecodaryOrderSort(Task o1,Task o2) {
	                    return o1.getId()<o2.getId()?-1
	                    		:o1.getId()>o2.getId()?1:0;
	                }
	            });
	        	sta.notifyDataSetChanged();
	            return true;
	        case R.id.action_clear_completed:
	        	u.clearCompletedTasksDialog();
	            return true;
	        case R.id.action_clear_expired:
	        	u.clearExpiredTasksDialog();
	            return true;
	        case R.id.action_exit:
	        	finish();
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	/**
	 * If we delete a task, reduce the counter by 1
	 */
	public static void reduceTaskCounter(){
		int currentCount = Integer.parseInt(count.getText().toString());
		currentCount--;
		count.setText(String.valueOf(currentCount));
	}
	
	private Dialog getDialog(){
		final Dialog dialog = new Dialog(this);
    	dialog.setOnDismissListener(new OnDismissListener() {
		@Override public void onDismiss(DialogInterface dialog) {updateList();}});
    	return dialog;
	}
	
	/* The click listner for ListView in the navigation drawer */
	private class DrawerItemClickListener implements ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id){
            if(mDrawerOptions[position].equals("Show all")){
            	status = "All tasks";statusTextView.setText(status);
            	updateList();
            } else if(mDrawerOptions[position].equals("Due today")){
            	status = "Todays tasks";statusTextView.setText(status);
            	updateList();
            } else if(mDrawerOptions[position].equals("Show unfinished")){
            	status = "Pending tasks";statusTextView.setText(status);
            	updateList();
            } else if(mDrawerOptions[position].equals("Show by priority")){
            	status = "Priority filtered tasks";statusTextView.setText(status);
        		u.showTasksByPriorityDialog();
            } else if(mDrawerOptions[position].equals("Show completed")){
            	status = "Completed tasks";statusTextView.setText(status);
            	updateList();
            }
			
			// update selected item and title, then close the drawer
			mDrawerList.setItemChecked(position, true);
			setTitle(mDrawerOptions[position]);
			mDrawerLayout.closeDrawer(mDrawerList);
		}
	}
	
	@Override
	protected void onPostCreate(Bundle savedInstanceState){
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig){
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggls
		mDrawerToggle.onConfigurationChanged(newConfig);
	}
    
    @Override
	protected void onResume() {
    	updateList();
		super.onResume();
	}

	@Override
	public void callback(List<Task> tasks, String priority) {
		this.tasks.clear();
		this.tasks.addAll(tasks);
		if(tasks.size()==0){
			noContent.setText(getResources().getString(R.string.no_priority)+" "+priority+" priority.");
			noContent.setVisibility(View.VISIBLE);
		} else noContent.setVisibility(View.GONE);
		sta.notifyDataSetChanged();
		count.setText(String.valueOf(tasks.size()));
	}

	@Override
	public void callback() {
		updateList();		
	}
}
