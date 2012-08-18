package de.reneruck.inear;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import de.reneruck.inear.db.DatabaseManager;
import de.reneruck.inear.file.FileScanner;


public class AppContext extends Application {

	private String currentAudiobook;
	private String audiobookBaseDir = "";
	private DatabaseManager databaseManager;

	@Override
	public void onCreate() {
		super.onCreate();
		PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
		this.databaseManager = new DatabaseManager(this);
	}
	
	public String getCurrentAudiobook() {
		return currentAudiobook;
	}

	public void setCurrentAudiobook(String currentAudiobook) {
		this.currentAudiobook = currentAudiobook;
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

	public void readSettings() {
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		this.audiobookBaseDir = sharedPref.getString("pref_base_dir", getString(R.string.pref_base_dir_default));
        runFilescanner();
	}
	
	private void runFilescanner() {
		FileScanner fileScanner = new FileScanner(this);
		fileScanner.doInBackground();
	}
}
