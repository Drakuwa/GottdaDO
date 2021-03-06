package com.gottado.dao;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.gottado.dom.Priority;
import com.gottado.dom.Task;
import com.gottado.utilities.MyDateFormatter;

/**
 * Implementation of the {@link DAO} interface
 * 
 */
public class LocalDAO extends SQLiteOpenHelper implements DAO {
	// Database Version
	private static final int DATABASE_VERSION = 1;
	// Database Name
	private static final String DATABASE_NAME = "tasksManager";
	// Machines table name
	private static final String TABLE_TASKS = "tasks";
	// Date formatters:
	SimpleDateFormat dateFormat = MyDateFormatter.getInstance().getDateFormat();
	// Singleton
	private static volatile LocalDAO instance = null;
	// Constructor
	private LocalDAO(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	// Singleton
	public static LocalDAO getInstance(Context context) {
		if (instance == null) {
			synchronized (LocalDAO.class) {
				if (instance == null) {
					instance = new LocalDAO(context);
				}
			}
		}
		return instance;
	}

	// Creating Tables
	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_MACHINES_TABLE = "CREATE TABLE " + TABLE_TASKS
				+ "(" + DatabaseHelper.KEY_ID + " INTEGER PRIMARY KEY,"
				+ DatabaseHelper.KEY_TITLE + " TEXT, "
				+ DatabaseHelper.KEY_DESCRIPTION + " TEXT, "
				+ DatabaseHelper.KEY_DATE + " DATETIME, "
				+ DatabaseHelper.KEY_PRIORITY + " INTEGER, "
				+ DatabaseHelper.KEY_COMPLETED + " BOOLEAN, "
				+ DatabaseHelper.KEY_CATEGORY + " TEXT )";
		db.execSQL(CREATE_MACHINES_TABLE);
	}

	// Upgrading database
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older table if existed
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASKS);
		// Create tables again
		onCreate(db);
	}

	/**
	 * Get all tasks from database
	 */
	@Override
	public List<Task> getAllTasks() throws ParseException {
		List<Task> tasksList = new ArrayList<Task>();
		// Select All Query
		String selectQuery = "SELECT  * FROM " + TABLE_TASKS;

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				Task t = new Task(); 
				t.setId(cursor.getLong(DatabaseHelper.COLUMN_ORDER.KEY_ID.ordinal()));
				t.setTitle(cursor.getString(DatabaseHelper.COLUMN_ORDER.KEY_TITLE.ordinal()));
				t.setDescription(cursor.getString(DatabaseHelper.COLUMN_ORDER.KEY_DESCRIPTION.ordinal()));
				t.setDueDate(dateFormat.parse(cursor.getString(DatabaseHelper.COLUMN_ORDER.KEY_DATE.ordinal())));
				int typeOridnal = cursor.getInt(DatabaseHelper.COLUMN_ORDER.KEY_PRIORITY.ordinal());
				if(typeOridnal==Priority.HIGH.getCustomOrdinal()) {
					t.setPriority(Priority.HIGH);
				} else if(typeOridnal==Priority.NORMAL.getCustomOrdinal()) {
					t.setPriority(Priority.NORMAL);
				} else t.setPriority(Priority.LOW);
				String completed = cursor.getString(DatabaseHelper.COLUMN_ORDER.KEY_COMPLETED.ordinal());
				if(completed.equals("1"))t.setCompleted(true);
				else t.setCompleted(false);
				// t.setCompleted(Boolean.valueOf(cursor.getString(DatabaseHelper.COLUMN_ORDER.KEY_COMPLETED.ordinal())));
				t.setCategory(cursor.getString(DatabaseHelper.COLUMN_ORDER.KEY_CATEGORY.ordinal()));
				tasksList.add(t);
			} while (cursor.moveToNext());
		}
		db.close();
		return tasksList;
	}

	/**
	 * Add a single task to the database
	 */
	@Override
	public void addTask(Task t) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(DatabaseHelper.KEY_TITLE, t.getTitle());
		values.put(DatabaseHelper.KEY_DESCRIPTION, t.getDescription());
		if(null != t.getDueDate())
			values.put(DatabaseHelper.KEY_DATE, dateFormat.format(t.getDueDate()));
		else values.put(DatabaseHelper.KEY_DATE, dateFormat.format(new Date(Long.MAX_VALUE)));
		values.put(DatabaseHelper.KEY_PRIORITY, t.getPriority().getCustomOrdinal());
		values.put(DatabaseHelper.KEY_COMPLETED, t.isCompleted()); // should be false
		values.put(DatabaseHelper.KEY_CATEGORY, t.getCategory());
		db.insert(TABLE_TASKS, null, values);
		db.close(); // Closing database connection
	}

	/**
	 * Update the task with the given new Task object
	 */
	@Override
	public int updateTask(Task t) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(DatabaseHelper.KEY_TITLE, t.getTitle());
		values.put(DatabaseHelper.KEY_DESCRIPTION, t.getDescription());
		values.put(DatabaseHelper.KEY_DATE, dateFormat.format(t.getDueDate()));
		values.put(DatabaseHelper.KEY_PRIORITY, t.getPriority().getCustomOrdinal());
		values.put(DatabaseHelper.KEY_COMPLETED, t.isCompleted()); // should be false
		values.put(DatabaseHelper.KEY_CATEGORY, t.getCategory());
		return db.update(TABLE_TASKS, values, DatabaseHelper.KEY_ID + " = ?",
				new String[] { String.valueOf(t.getId()) });
	}

	/**
	 * Delete the particular task
	 */
	@Override
	public void deleteTask(Task t) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_TASKS, DatabaseHelper.KEY_ID + " = ?",
				new String[] { String.valueOf(t.getId()) });
		db.close();
	}

	/**
	 * Get all the tasks from the database, that are marked
	 * as completed
	 */
	@Override
	public List<Task> getAllCompletedTasks(boolean finished) throws ParseException {
		List<Task> tasksList = new ArrayList<Task>();
		String status = "";
		if(finished) status = "1";else status = "0";
		// Select All Query
		String selectQuery = "SELECT  * FROM " + TABLE_TASKS 
				+ " WHERE "+DatabaseHelper.KEY_COMPLETED
				+ " LIKE "+status;

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				Task t = new Task(); 
				t.setId(cursor.getLong(DatabaseHelper.COLUMN_ORDER.KEY_ID.ordinal()));
				t.setTitle(cursor.getString(DatabaseHelper.COLUMN_ORDER.KEY_TITLE.ordinal()));
				t.setDescription(cursor.getString(DatabaseHelper.COLUMN_ORDER.KEY_DESCRIPTION.ordinal()));
				t.setDueDate(dateFormat.parse(cursor.getString(DatabaseHelper.COLUMN_ORDER.KEY_DATE.ordinal())));
				int typeOridnal = cursor.getInt(DatabaseHelper.COLUMN_ORDER.KEY_PRIORITY.ordinal());
				if(typeOridnal==Priority.HIGH.getCustomOrdinal()) {
					t.setPriority(Priority.HIGH);
				} else if(typeOridnal==Priority.NORMAL.getCustomOrdinal()) {
					t.setPriority(Priority.NORMAL);
				} else t.setPriority(Priority.LOW);
				String completed = cursor.getString(DatabaseHelper.COLUMN_ORDER.KEY_COMPLETED.ordinal());
				if(completed.equals("1"))t.setCompleted(true);
				else t.setCompleted(false);
				// t.setCompleted(Boolean.valueOf(cursor.getString(DatabaseHelper.COLUMN_ORDER.KEY_COMPLETED.ordinal())));
				t.setCategory(cursor.getString(DatabaseHelper.COLUMN_ORDER.KEY_CATEGORY.ordinal()));
				tasksList.add(t);
			} while (cursor.moveToNext());
		}
		db.close();
		return tasksList;
	}

	/**
	 * Delete all tasks that are older then 24 hours
	 */
	@Override
	public void clearExpiredTasks() {
		SQLiteDatabase db = this.getWritableDatabase();		
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_YEAR, -1);
		Date now = cal.getTime();
		db.delete(TABLE_TASKS, "Datetime("+DatabaseHelper.KEY_DATE+") < Datetime('"+dateFormat.format(now)+"')",
				new String[] {});
		db.close();		
	}
	
	/**
	 * Delete all the tasks that are marked as completed
	 */
	@Override
	public void clearCompletedTasks() {
		SQLiteDatabase db = this.getWritableDatabase();		
		db.delete(TABLE_TASKS, DatabaseHelper.KEY_COMPLETED + " = ?",
				new String[] { "1" });
		db.close();		
	}

	/**
	 * Get all the tasks that have todays date
	 */
	@Override
	public List<Task> getAllTasksDueToday() throws ParseException {
		List<Task> tasksList = new ArrayList<Task>();
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
		Date today = cal.getTime();
		String minValue = sdf.format(today)+" 00:00:00"; 
		String maxValue = sdf.format(today)+" 23:59:59";
		// Select Query
		String selectQuery = "SELECT * FROM " + TABLE_TASKS 
			+ " WHERE Datetime(" + DatabaseHelper.KEY_DATE+") >= Datetime('" + minValue
				+ "') AND Datetime(" + DatabaseHelper.KEY_DATE+") <= Datetime('" + maxValue+ "')";

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				Task t = new Task(); 
				t.setId(cursor.getLong(DatabaseHelper.COLUMN_ORDER.KEY_ID.ordinal()));
				t.setTitle(cursor.getString(DatabaseHelper.COLUMN_ORDER.KEY_TITLE.ordinal()));
				t.setDescription(cursor.getString(DatabaseHelper.COLUMN_ORDER.KEY_DESCRIPTION.ordinal()));
				t.setDueDate(dateFormat.parse(cursor.getString(DatabaseHelper.COLUMN_ORDER.KEY_DATE.ordinal())));
				int typeOridnal = cursor.getInt(DatabaseHelper.COLUMN_ORDER.KEY_PRIORITY.ordinal());
				if(typeOridnal==Priority.HIGH.getCustomOrdinal()) {
					t.setPriority(Priority.HIGH);
				} else if(typeOridnal==Priority.NORMAL.getCustomOrdinal()) {
					t.setPriority(Priority.NORMAL);
				} else t.setPriority(Priority.LOW);
				String completed = cursor.getString(DatabaseHelper.COLUMN_ORDER.KEY_COMPLETED.ordinal());
				if(completed.equals("1"))t.setCompleted(true);
				else t.setCompleted(false);
				// t.setCompleted(Boolean.valueOf(cursor.getString(DatabaseHelper.COLUMN_ORDER.KEY_COMPLETED.ordinal())));
				t.setCategory(cursor.getString(DatabaseHelper.COLUMN_ORDER.KEY_CATEGORY.ordinal()));
				tasksList.add(t);
			} while (cursor.moveToNext());
		}
		db.close();
		return tasksList;
	}

	/**
	 * Get all tasks with the given priority
	 */
	@Override
	public List<Task> getAllTasksByPriority(String priority)
			throws ParseException {
		List<Task> tasksList = new ArrayList<Task>();

		// Select Query
		String selectQuery = "SELECT * FROM " + TABLE_TASKS 
			+ " WHERE "+DatabaseHelper.KEY_PRIORITY
				+" LIKE "+Priority.valueOf(priority).getCustomOrdinal();

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				Task t = new Task(); 
				t.setId(cursor.getLong(DatabaseHelper.COLUMN_ORDER.KEY_ID.ordinal()));
				t.setTitle(cursor.getString(DatabaseHelper.COLUMN_ORDER.KEY_TITLE.ordinal()));
				t.setDescription(cursor.getString(DatabaseHelper.COLUMN_ORDER.KEY_DESCRIPTION.ordinal()));
				t.setDueDate(dateFormat.parse(cursor.getString(DatabaseHelper.COLUMN_ORDER.KEY_DATE.ordinal())));
				int typeOridnal = cursor.getInt(DatabaseHelper.COLUMN_ORDER.KEY_PRIORITY.ordinal());
				if(typeOridnal==Priority.HIGH.getCustomOrdinal()) {
					t.setPriority(Priority.HIGH);
				} else if(typeOridnal==Priority.NORMAL.getCustomOrdinal()) {
					t.setPriority(Priority.NORMAL);
				} else t.setPriority(Priority.LOW);
				String completed = cursor.getString(DatabaseHelper.COLUMN_ORDER.KEY_COMPLETED.ordinal());
				if(completed.equals("1"))t.setCompleted(true);
				else t.setCompleted(false);
				t.setCategory(cursor.getString(DatabaseHelper.COLUMN_ORDER.KEY_CATEGORY.ordinal()));
				tasksList.add(t);
			} while (cursor.moveToNext());
		}
		db.close();
		return tasksList;
	}
}