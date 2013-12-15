package com.gottado.utilities;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.gottado.R;
import com.gottado.ui.MainActivity;

public class AddTaskWidgetProvider extends AppWidgetProvider {

	@SuppressWarnings("unused")
	private static final String ACTION_CLICK = "ACTION_CLICK";

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {

		// Get all ids
		ComponentName thisWidget = new ComponentName(context,
				AddTaskWidgetProvider.class);
		int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
		for (int widgetId : allWidgetIds) {
			RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
					R.layout.widget_quick_add);
			
			Intent configIntent = new Intent(context, MainActivity.class);
			configIntent.putExtra("ACTION_ADD", true);
			configIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		    PendingIntent configPendingIntent = PendingIntent.getActivity(context, 0, configIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		    
		    // start the application
		    remoteViews.setOnClickPendingIntent(R.id.addTask, configPendingIntent);
			appWidgetManager.updateAppWidget(widgetId, remoteViews);
		}
	}
}