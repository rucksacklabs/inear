package de.reneruck.inear;

import android.app.Application;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import de.reneruck.inear.db.DatabaseManager;
import de.reneruck.inear.file.FileScanner;
import de.reneruck.inear.mediaservice.PlaybackService;
import de.reneruck.inear.mediaservice.PlaybackServiceControl;


public class AppContext extends Application {

	private static final String TAG = "InEar - AppContext";

	private String audiobookBaseDir = "";

	private DatabaseManager databaseManager;
	private Settings settings;
	private CurrentAudiobook currentAudiobookBean;
	private boolean isBound;
	private PlaybackServiceControl playbackService;


	@Override
	public void onCreate() {
		super.onCreate();
		PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
		this.databaseManager = new DatabaseManager(this);
		readSettings();
		startService(new Intent(this, PlaybackService.class));
		bindToPlaybackService();
	}
	
	private void bindToPlaybackService() {
		bindService(new Intent(this, PlaybackService.class), this.serviceConnection, 0);
	}
	
	private ServiceConnection serviceConnection = new ServiceConnection() {
		

		@Override
		public void onServiceDisconnected(ComponentName name) {
			handleServiceUnbound();
		}

		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			handleServiceBound(service);
			getCurrentAudiobookFromService();
		}
	};
	
	private void getCurrentAudiobookFromService() {
		if(this.isBound)
		{
			this.currentAudiobookBean = this.playbackService.getCurrentAudiobookBean();
		}
	}
	
	private void handleServiceBound(IBinder service) {
		if(service instanceof PlaybackServiceControl) {
			this.playbackService = (PlaybackServiceControl) service;
			this.isBound = true;
		}
	}
	
	private void handleServiceUnbound() {
		this.isBound = false;
		this.playbackService = null;
	}

	public void readSettings() {
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		this.audiobookBaseDir = sharedPref.getString("pref_base_dir", getString(R.string.pref_base_dir_default));
		this.settings = new Settings(sharedPref);
		runFilescanner();
	}
	
	private void runFilescanner() {
		FileScanner fileScanner = new FileScanner(this);
		fileScanner.doInBackground();
	}
	
	public void setCurrentAudiobook(String currentAudiobook) {
		this.currentAudiobookName = currentAudiobook;
	}


	public String getAudiobokkBaseDir() {
		return this.audiobookBaseDir;
	}

	public DatabaseManager getDatabaseManager() {
		return databaseManager;
	}

	public void setDatabaseManager(DatabaseManager databaseManager) {
		this.databaseManager = databaseManager;
	}
	public CurrentAudiobook getCurrentAudiobookBean() {
		return currentAudiobookBean;
	}

	public void setCurrentAudiobookBean(CurrentAudiobook currentAudiobookBean) {
		this.currentAudiobookBean = currentAudiobookBean;
	}

	public Settings getSettings() {
		return settings;
	}

	public void setSettings(Settings settings) {
		this.settings = settings;
	}

	public String getCurrentAudiobookName() {
		return currentAudiobookName;
	}

	public void setCurrentAudiobookName(String currentAudiobookName) {
		this.currentAudiobookName = currentAudiobookName;
	}
}
