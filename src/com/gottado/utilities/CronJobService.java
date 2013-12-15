package com.gottado.utilities;

import java.text.ParseException;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.gottado.R;
import com.gottado.dao.DAO;
import com.gottado.dao.LocalDAO;
import com.gottado.ui.MainActivity;

/**
 * A Service class that should show a notification when started,
 * notifying the user about the number of tasks he has due the current day
 * @author drakuwa
 *
 */
public class CronJobService extends Service {
	
	private NotificationManager mNM;
	
	// Unique Identification Number for the Notification.
    // We use it on Notification start, and to cancel it.
    private int NOTIFICATION = R.string.local_service_started;

	@Override
    public void onCreate() {
        mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        Log.i(Log.TAG, "Started");
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if(!prefs.getBoolean("isRunning", false)){
        	Editor e = prefs.edit();
        	e.putBoolean("isRunning", true);
        	e.commit();
        }
    }
	
	@Override
    public int onStartCommand(Intent intent, int flags, int startId) {
		// Display a notification about us starting.  We put an icon in the status bar.
        showNotification();
        //Log.i("LocalService", "Received start id " + startId + ": " + intent);
        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        // Cancel the persistent notification.
        mNM.cancel(NOTIFICATION);

        // Tell the user we stopped.
        Toast.makeText(this, R.string.local_service_stopped, Toast.LENGTH_SHORT).show();
        
        // remove
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if(prefs.getBoolean("isRunning", false)){
        	Editor e = prefs.edit();
        	e.putBoolean("isRunning", true);
        	e.commit();
        }
    }

    /**
     * Show a notification while this service is running.
     */
    @SuppressWarnings("deprecation")
	private void showNotification() {
    	// get a database instance
    	DAO db = LocalDAO.getInstance(this);
    	int tasksCount = 0;
    	try {
			tasksCount = db.getAllTasksDueToday().size();
		} catch (ParseException e) {
			Log.e(Log.TAG, e.getLocalizedMessage());
		}
        if(tasksCount != 0){
        	// show a notification if there are tasks due today
        	// Set the icon, scrolling text and timestamp
            Notification notification = new Notification(R.drawable.ic_launcher, getString(R.string.check_your_gottado_list_now_),
                    System.currentTimeMillis());

            // The PendingIntent to launch our activity if the user selects this notification
            PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                    new Intent(this, MainActivity.class), 0);

            // Set the info for the views that show in the notification panel.
            notification.setLatestEventInfo(this, "You have "+tasksCount+" tasks due today.", 
            		getString(R.string.click_here_to_see_them_), contentIntent);

            // Send the notification.
            mNM.notify(NOTIFICATION, notification);
        }
    }

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

}
