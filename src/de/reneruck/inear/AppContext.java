package de.reneruck.inear;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import de.reneruck.inear.db.AsyncGetBookmark;
import de.reneruck.inear.db.DatabaseManager;
import de.reneruck.inear.file.FileScanner;
import de.reneruck.inear.mediaservice.PlaybackService;


public class AppContext extends Application {

	private static final String TAG = "InEar - AppContext";

	private String audiobookBaseDir = "";

	private DatabaseManager databaseManager;
	private Settings settings;

	private String currentAudiobookName;

	private CurrentAudiobook currentAudiobookBean;

	@Override
	public void onCreate() {
		super.onCreate();
		PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
		this.databaseManager = new DatabaseManager(this);
		startService(new Intent(this, PlaybackService.class));
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
