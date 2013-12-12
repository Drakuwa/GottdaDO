package com.gottado.adapters;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gottado.R;
import com.gottado.dao.DAO;
import com.gottado.dao.LocalDAO;
import com.gottado.dom.Priority;
import com.gottado.dom.Task;
import com.gottado.ui.AddOrModifyTaskDialog;
import com.gottado.ui.MainActivity;
import com.gottado.utilities.MyDateFormatter;

public class TaskAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private List<Task> items = new ArrayList<Task>();
	private Context ctx;
	private DAO db;
	private SimpleDateFormat dateFormat = MyDateFormatter.getInstance().getDateFormat();
	private Date now = null;
	public TaskAdapter(Context context, List<Task> items) {
		mInflater = LayoutInflater.from(context);
		this.items = items;
		this.ctx = context;
		db = LocalDAO.getInstance(context);
		Calendar cal = Calendar.getInstance();
		now = cal.getTime();
	}
	public int getCount() {
		return items.size();
	}
	public Task getItem(int position) {
		return items.get(position);
	}
	public long getItemId(int position) {
		return position;
	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		final Task t = items.get(position);
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.task_list_item, null);
			holder = new ViewHolder();
			holder.title = (TextView) convertView.findViewById(R.id.taskTitle);
			holder.description = (TextView) convertView.findViewById(R.id.taskDesctiption);
			holder.dueDate = (TextView) convertView.findViewById(R.id.taskDate);
			holder.priority = (TextView) convertView.findViewById(R.id.taskPriority);
			holder.isCompleted = (ImageView) convertView.findViewById(R.id.taskCompleted);
			holder.popup = (ImageView) convertView.findViewById(R.id.taskPopup);
			holder.rl = (RelativeLayout) convertView.findViewById(R.id.taskHolder);
			convertView.setTag(holder);
		} else { holder = (ViewHolder) convertView.getTag(); }
		String title = t.getTitle();
		if(null == title || title.length()==0)holder.title.setText(R.string.no_title);
		else holder.title.setText(t.getTitle());
		String description = t.getDescription();
		if(null == description || description.length()==0)holder.description.setText(R.string.no_description);
		else holder.description.setText(t.getDescription());
		// holder.category.setText(t.getCategory());
		StringBuilder sb = new StringBuilder();
		sb.append(ctx.getResources().getString(R.string.due_date_));
		
		// if the date has a MAX value, the user hasn't set an end date for the task
		if(Long.MAX_VALUE-t.getDueDate().getTime()<1000){
			sb.append("N/A");
		} else {
			sb.append(dateFormat.format(t.getDueDate()));
		}
		holder.dueDate.setText(sb.toString());
		holder.priority.setText(ctx.getResources().getString(R.string.priority_)+t.getPriority().name());
		if(t.getPriority()==Priority.LOW){
			holder.priority.setTextColor(ctx.getResources().getColor(R.color.yellow));
		} else if(t.getPriority()==Priority.MEDIUM){
			holder.priority.setTextColor(ctx.getResources().getColor(R.color.orange));
		} else if(t.getPriority()==Priority.HIGH){
			holder.priority.setTextColor(ctx.getResources().getColor(R.color.darkRed));
		}
		
		if(t.isCompleted())holder.isCompleted.setImageResource(R.drawable.check_done);
		else holder.isCompleted.setImageResource(R.drawable.check_not);
		holder.isCompleted.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(t.isCompleted()){
					holder.isCompleted.setImageResource(R.drawable.check_not);
					t.setCompleted(false);
					if(MainActivity.status.equals("Completed tasks")){
						items.remove(position);
						notifyDataSetChanged();
						MainActivity.reduceTaskCounter();
					}
				} else {
					holder.isCompleted.setImageResource(R.drawable.check_done);
					t.setCompleted(true);
					if(MainActivity.status.equals("Pending tasks")){
						items.remove(position);
						notifyDataSetChanged();
						MainActivity.reduceTaskCounter();
					}
				} db.updateTask(t); // update the task with the new status
			}
		});
		
		holder.popup.setOnClickListener(new OnClickListener() {
		@Override
		public void onClick(View v) {
			PopupMenu popup = new PopupMenu(ctx, holder.popup);
			popup.getMenuInflater().inflate(R.menu.card, popup.getMenu());
			popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener(){
				public boolean onMenuItemClick(MenuItem item) {
					if(item.getTitle().equals("Delete")){
						db.deleteTask(t);
						items.remove(position);
						notifyDataSetChanged();
						MainActivity.reduceTaskCounter();
					} else if(item.getTitle().equals("Modify")){
						AddOrModifyTaskDialog d = new AddOrModifyTaskDialog(ctx, t, getDialog());
						d.showDialog();
					}
					return true;
				}});
		    popup.show();
		}});
		
		//if(now.compareTo(t.getDueDate())>0){
		if(now.getTime()>t.getDueDate().getTime()){
			holder.rl.setBackgroundResource(R.drawable.bg_card_expired);
		} else holder.rl.setBackgroundResource(R.drawable.bg_card);
		
		AnimationSet set = new AnimationSet(true);
        Animation animation = new AlphaAnimation(0.0f, 1.0f);
        animation.setDuration(600);
        set.addAnimation(animation);
        animation = new TranslateAnimation(
            Animation.RELATIVE_TO_SELF, 0.0f,Animation.RELATIVE_TO_SELF, 0.0f,
            Animation.RELATIVE_TO_SELF, 1.0f,Animation.RELATIVE_TO_SELF, 0.0f
        );
        animation.setDuration(600);
        set.addAnimation(animation);
        convertView.startAnimation(set);
		return convertView;
	}
	
	private Dialog getDialog(){
		final Dialog dialog = new Dialog(ctx);
    	dialog.setOnDismissListener(new OnDismissListener() {
		@Override public void onDismiss(DialogInterface dialog) {notifyDataSetChanged();}});
    	return dialog;
	}

	/**
	 * The ViewHolder pattern
	 * @author drakuwa
	 */
	static class ViewHolder {
		TextView title;
		TextView description;
		TextView dueDate;
		TextView priority;
		ImageView isCompleted;
		ImageView popup;
		TextView category;
		RelativeLayout rl;
	}
}