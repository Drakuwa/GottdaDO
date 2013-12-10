package com.gottado.ui;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.gottado.R;
import com.gottado.adapters.TaskAdapter;
import com.gottado.dao.DAO;
import com.gottado.dao.LocalDAO;
import com.gottado.dom.Task;
import com.gottado.utilities.Log;

public class MainActivity extends ActionBarActivity {
	
	private TextView noContent;
	private List<Task> tasks = new ArrayList<Task>();
	private TaskAdapter sta;
	private ListView lv;
	private DAO db;
	private String status = "All tasks";
	private TextView statusTextView, count;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// get the db instance
		db = LocalDAO.getInstance(this);
		noContent = (TextView) findViewById(R.id.taskNoContent);
		
		// GET THE LIST
		lv = (ListView) findViewById(R.id.tasksList);
		lv.setTextFilterEnabled(true);
		
		count = (TextView) findViewById(R.id.taskStatusCount);
		statusTextView = (TextView) findViewById(R.id.taskStatus);
		statusTextView.setText(status);
		
		initList();
	}
	
	private void initList(){
		// GET YOUR TASKS
		tasks.clear();
		try {
			if(status.equals("All tasks")){
				tasks = db.getAllTasks();
				if(tasks.size()==0){
					noContent.setText(R.string.no_content);
					noContent.setVisibility(View.VISIBLE);
				} else noContent.setVisibility(View.GONE);
			} else if(status.equals("Completed tasks")){
				tasks = db.getAllCompletedTasks(true);
				if(tasks.size()==0){
    				noContent.setText(R.string.no_completed);
    				noContent.setVisibility(View.VISIBLE);
    			} else noContent.setVisibility(View.GONE);
			} else if(status.equals("Pending tasks")){
				tasks = db.getAllCompletedTasks(false);
				if(tasks.size()==0){
    				noContent.setText(R.string.no_unfinished);
    				noContent.setVisibility(View.VISIBLE);
    			} else noContent.setVisibility(View.GONE);
			}
		} catch (ParseException e) {
			Log.e(Log.TAG, e.getLocalizedMessage());
		}
		// CREATE BASE ADAPTER
		sta = new TaskAdapter(MainActivity.this, tasks);
		count.setText(String.valueOf(tasks.size()));
		
		// SET AS CURRENT LIST
		lv.setAdapter(sta);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	        case R.id.action_add:
	        	AddOrModifyTaskDialog d = new AddOrModifyTaskDialog(MainActivity.this, null, getDialog());
	        	d.showDialog();
	            return true;
	        case R.id.action_completed:
	        	status = "Completed tasks";statusTextView.setText(status);
	        	initList();
	            return true;
	        case R.id.action_pending:
	        	status = "Pending tasks";statusTextView.setText(status);
	        	initList();
	            return true;
	        case R.id.action_all:
	        	status = "All tasks";statusTextView.setText(status);
	        	initList();
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	private Dialog getDialog(){
		final Dialog dialog = new Dialog(this);
    	dialog.setOnDismissListener(new OnDismissListener() {
		@Override public void onDismiss(DialogInterface dialog) {initList();}});
    	return dialog;
	}
	
	@Override
	protected void onResume() {
		initList();
		super.onResume();
	}
}
