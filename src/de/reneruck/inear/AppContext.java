package de.reneruck.inear;

import de.reneruck.inear.db.DatabaseManager;
import android.app.Application;
import android.os.Environment;


public class AppContext extends Application {

	private String currentAudiobook;
	private String audiobookBaseDir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Audiobooks";
	private DatabaseManager databaseManager;

	@Override
	public void onCreate() {
		super.onCreate();
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
}
