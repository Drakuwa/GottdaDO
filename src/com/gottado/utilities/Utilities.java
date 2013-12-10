package com.gottado.utilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Comparator;
import java.util.Map;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

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
	 * Constructor of the Model class which initializes the activity context.
	 * 
	 * @param context
	 */
	public Utilities(Context context) {
		ctx = context;
	}
	
	/**
	 * Method that creates and shows an AlertDialog with a message passed with
	 * the txt parameter, and a PositiveButton "OK..."
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
	 * Method that creates and shows a GPS Disabled alert, and calls the
	 * showGpsOptions() method on positive click.
	 */
	public void createGpsDisabledAlert() {
		AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
		builder.setMessage(
				"Your GPS is disabled. Please edable it.")
				.setIcon(R.drawable.ic_launcher)
				.setTitle(R.string.app_name)
				.setCancelable(true)
				.setPositiveButton("Enable GPS",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								showGpsOptions();
							}
						});
		AlertDialog alert = builder.create();
		alert.show();
	}

	/**
	 * Method that calls the Location settings and allows the user to enable or
	 * disable the GPS
	 */
	private void showGpsOptions() {
		Intent gpsOptionsIntent = new Intent(
				android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
		ctx.startActivity(gpsOptionsIntent);
	}

	public void createInternetDisabledAlert() {
		AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
		builder.setMessage(
				"Your internet connection is disabled. Please enable it.")
				.setIcon(R.drawable.ic_launcher)
				.setTitle(R.string.app_name)
				.setCancelable(true)
				.setPositiveButton("Internet options",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								showNetOptions();
							}
						});
		AlertDialog alert = builder.create();
		alert.show();
	}

	public void showNetOptions() {
		Intent netOptionsIntent = new Intent(
				android.provider.Settings.ACTION_WIRELESS_SETTINGS);
		ctx.startActivity(netOptionsIntent);
	}

	public class MapComparator implements Comparator<Map<String, ?>> {
		private final String key;

		public MapComparator(String key) {
			this.key = key;
		}

		public int compare(Map<String, ?> first, Map<String, ?> second) {
			// Null checking, both for maps and values
			String firstValue = (String) first.get(key);
			String secondValue = (String) second.get(key);
			return firstValue.compareTo(secondValue);
		}
	}
	
	public static boolean HaveNetworkConnection(Context ctx) {
		boolean HaveConnectedWifi = false;
		boolean HaveConnectedMobile = false;

		ConnectivityManager cm = (ConnectivityManager) ctx
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo[] netInfo = cm.getAllNetworkInfo();
		for (NetworkInfo ni : netInfo) {
			if (ni.getTypeName().equalsIgnoreCase("WIFI"))
				if (ni.isConnected())
					HaveConnectedWifi = true;
			if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
				if (ni.isConnected())
					HaveConnectedMobile = true;
		}
		return HaveConnectedWifi || HaveConnectedMobile;
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
	
	/**
     * convert the given input stream into a string
     * @param is
     * @return
     */
    public static String convertStreamToString(InputStream is) {

    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
    StringBuilder sb = new StringBuilder();

    String line = null;
    try { while ((line = reader.readLine()) != null) { sb.append(line + "\n"); }
    } catch (IOException e) { e.printStackTrace(); }
    finally { try { is.close(); } catch (IOException e) { e.printStackTrace(); }}
    return sb.toString();
    }
    
    public static void clearExpiredTasksDialog(Context ctx) {
    	final DAO db = LocalDAO.getInstance(ctx);
		AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
		builder.setMessage(R.string.clear_expired)
				.setIcon(R.drawable.ic_launcher)
				.setTitle(R.string.app_name)
				.setPositiveButton(R.string.yes,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								db.clearExpiredTasks();
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
    
    public static void clearCompletedTasksDialog(Context ctx) {
    	final DAO db = LocalDAO.getInstance(ctx);
		AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
		builder.setMessage(R.string.clear_completed)
				.setIcon(R.drawable.ic_launcher)
				.setTitle(R.string.app_name)
				.setPositiveButton(R.string.yes,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								db.clearCompletedTasks();
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
}
