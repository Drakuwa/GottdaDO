package com.gottado.utilities;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.gottado.R;
import com.gottado.dao.DAO;
import com.gottado.dao.LocalDAO;

/**
 * A model class that resolves some of the business logic in the application.
 * Also as a part of the MVC(Model View Controller) programming pattern. It
 * presents changes to the UI thread through the Controller classes.
 * 
 * @author drakuwa
 */
public class Utilities {
	
	private Context ctx;
	
	/**
	 * Constructor of the Utilities class which initializes the activity context.
	 * 
	 * @param context
	 */
	public Utilities(Context context) {
		ctx = context;
	}
	
	/**
	 * Method that creates and shows an AlertDialog with a message passed with
	 * the txt parameter, and a PositiveButton "OK"
	 * 
	 * @param txt
	 */
	public static void customDialog(final String txt, Context ctx) {
		AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
		builder.setMessage(txt)
				.setIcon(R.drawable.ic_launcher)
				.setTitle(R.string.app_name)
				.setCancelable(false)
				.setPositiveButton("OK",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
							}
						});
		AlertDialog alert = builder.create();
		try {
			alert.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Encode toHash string into SHA1 hash and return the result
	 * @param toHash
	 * @return
	 */
	public static String sha1Hash(String toHash){
		String hash = null;
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-1");
			byte[] bytes = toHash.getBytes("UTF-8");
			digest.update(bytes, 0, bytes.length);
			bytes = digest.digest();
			StringBuilder sb = new StringBuilder();
			for(byte b : bytes){
				sb.append(String.format("%02X", b));
			}
			hash = sb.toString();
		} catch (NoSuchAlgorithmException e) {
			Log.e(Log.TAG, e.getLocalizedMessage());
		} catch (UnsupportedEncodingException e) {
			Log.e(Log.TAG, e.getLocalizedMessage());
		}
		return hash;
	}

    // set the callback listener
    CallBackListener mListener;
    public void setListener(CallBackListener listener){
            mListener = listener;
    }
    
    /**
     * Show an alert dialog warning the user that he is about to
     * delete all expired tasks from the application. Expired tasks
     * are considered to be task which have passed their due date.
     * (24 hours)
     */
    public void clearExpiredTasksDialog() {
    	final DAO db = LocalDAO.getInstance(ctx);
		AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
		builder.setMessage(R.string.clear_expired)
				.setIcon(R.drawable.ic_launcher)
				.setTitle(R.string.app_name)
				.setPositiveButton(R.string.yes,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								db.clearExpiredTasks();
								// execute callback
		                        mListener.callback();
						}
				}).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
				}
		});
		AlertDialog alert = builder.create();
		try {
			alert.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
    
    /**
     * Show an alert dialog warning the user that he is about to
     * delete all completed tasks from the application.
     */
    public void clearCompletedTasksDialog() {
    	final DAO db = LocalDAO.getInstance(ctx);
		AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
		builder.setMessage(R.string.clear_completed)
				.setIcon(R.drawable.ic_launcher)
				.setTitle(R.string.app_name)
				.setPositiveButton(R.string.yes,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								db.clearCompletedTasks();
								// execute callback
		                        mListener.callback();
						}
				}).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
				}
		});
		AlertDialog alert = builder.create();
		try {
			alert.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
    
    /**
     * Show a dialog from which the user can choose the priority of
     * the task that he wants shown
     */
    public void showTasksByPriorityDialog(){
    	final DAO db = LocalDAO.getInstance(ctx);
    	final String [] priorities = ctx.getResources().getStringArray(R.array.priority_spinner);;
    	AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setTitle(R.string.show_by_priority)
        		.setIcon(R.drawable.ic_launcher)
               .setItems(priorities, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int which) {
                   // The 'which' argument contains the index position
                   // of the selected item
                	   
                	// execute callback
                	try {
                		mListener.callback(db.getAllTasksByPriority(priorities[which]), priorities[which]);
					} catch (ParseException e) {
						Log.e(Log.TAG, e.getLocalizedMessage());
					}
               }
        });
        AlertDialog alert = builder.create();
		try {
			alert.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
}
