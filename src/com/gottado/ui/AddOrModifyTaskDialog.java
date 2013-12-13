package com.gottado.ui;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.gottado.R;
import com.gottado.dao.DAO;
import com.gottado.dao.LocalDAO;
import com.gottado.dom.Priority;
import com.gottado.dom.Task;
import com.gottado.utilities.Log;
import com.gottado.utilities.MyDateFormatter;

/**
 * a dialog for crating or modifying a task
 * @author drakuwa
 *
 */
public class AddOrModifyTaskDialog {
	
	private Context context;
	private Dialog dialog;
	private EditText title, description;
	private Button chooseDate, save;
	private static TextView dateTV;
	public static SimpleDateFormat dateFormat = MyDateFormatter.getInstance().getDateFormat();
	private Spinner priority;
	private DAO db;
	public static Task task;
	private boolean isEditing = false;

	/**
	 * constructor that receives context, a Task object if we want to modify
	 * and a dialog instance
	 * @param context
	 * @param t
	 * @param dialog
	 */
	public AddOrModifyTaskDialog(Context context, Task t, Dialog dialog) {
		this.context = context;
		// dialog = new Dialog(this.context);
		this.dialog = dialog;
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.dialog_add_task);
		db = LocalDAO.getInstance(context);
		if(null == t)
			task = new Task();
		else {
			task = t;
			isEditing = true;
		}
		initViews();
	}

	// initialize the views of the dialog
	private void initViews() {
		
		title = (EditText) dialog.findViewById(R.id.taskTitle);
		description = (EditText) dialog.findViewById(R.id.taskDescription);
		dateTV = (TextView) dialog.findViewById(R.id.taskDateTV);
		priority = (Spinner) dialog.findViewById(R.id.priority);
		String[] priorities = context.getResources().getStringArray(R.array.priority_spinner);
		ArrayAdapter<CharSequence> priorityAdapter = new ArrayAdapter<CharSequence>(
				context,android.R.layout.simple_spinner_item, priorities);
		// Specify the layout to use when the list of choices appears
		priorityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		priority.setAdapter(priorityAdapter);
		if(isEditing){	
			for(int i=0; i<priorities.length; i++){
				String current = task.getPriority().name();
				if(current.equals(priorities[i]))
					priority.setSelection(i);
			}
		}
		priority.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
				if(parent.getItemAtPosition(pos).toString().equals("LOW")) {
					task.setPriority(Priority.LOW);
				} else if(parent.getItemAtPosition(pos).toString().equals("NORMAL")) {
					task.setPriority(Priority.NORMAL);
				} else if(parent.getItemAtPosition(pos).toString().equals("HIGH")) {
					task.setPriority(Priority.HIGH);
				} else Log.e(Log.TAG, "Error choosing priority");
			}
			@Override public void onNothingSelected(AdapterView<?> arg0){}
		});
		chooseDate = (Button) dialog.findViewById(R.id.taskChooseDate);
		chooseDate.setOnClickListener(new OnClickListener() {
		@Override public void onClick(View v) {
			showDatePickerDialog();
		}});
		save = (Button) dialog.findViewById(R.id.taskSave);
		save.setOnClickListener(new OnClickListener() {
		@Override public void onClick(View v) {
			String titleStr = title.getText().toString().trim();
			if(titleStr.length()==0)titleStr="No title";
			String descriptionStr = description.getText().toString().trim();
			if(descriptionStr.length()==0)descriptionStr="No description";
			task.setTitle(titleStr);
			task.setDescription(descriptionStr);
			if(dateTV.getText().toString().equals("N/A"))task.setDueDate(null);
			if(isEditing) db.updateTask(task);
			else db.addTask(task);
			dialog.dismiss();
		}});
		
		if(isEditing){
			title.setText(task.getTitle());
			description.setText(task.getDescription());
			if(null != task.getDueDate())
				dateTV.setText(dateFormat.format(task.getDueDate()));
			else dateTV.setText(R.string.n_a);
		}
		// showDialog();
	}
	
	/**
	 * show the dialog
	 */
	public void showDialog(){
		dialog.show();
	}

	/**
	 * show the date picker dialog
	 */
	public void showDatePickerDialog() {
		DialogFragment newFragment = new DatePickerFragment();
		newFragment.show(((FragmentActivity) context).getSupportFragmentManager(), "datePicker");
	}

	/**
	 * a DatePicker fragment
	 * @author drakuwa
	 *
	 */
	public static class DatePickerFragment extends DialogFragment implements
			DatePickerDialog.OnDateSetListener {

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Use the current date as the default date in the picker
			final Calendar c = Calendar.getInstance();
			int year = c.get(Calendar.YEAR);
			int month = c.get(Calendar.MONTH);
			int day = c.get(Calendar.DAY_OF_MONTH);

			// Create a new instance of DatePickerDialog and return it
			return new DatePickerDialog(getActivity(), this, year, month, day);
		}

		@Override
		public void onDateSet(DatePicker view, int year, int month, int day) {
			// Do something with the date chosen by the user
			Calendar calendar = Calendar.getInstance();
		    calendar.set(year, month, day);
		    task.setDueDate(calendar.getTime());
		    dateTV.setText(dateFormat.format(calendar.getTime()));
		}
	}
}