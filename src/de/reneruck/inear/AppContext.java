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
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import de.reneruck.inear.db.DatabaseManager;
import de.reneruck.inear.file.FileScanner;


public class AppContext extends Application {

	private String currentAudiobook;
	private String audiobookBaseDir = "";
	private DatabaseManager databaseManager;
	private List<String> currentPlaylist = new ArrayList<String>();
	private boolean autoplay;

	@Override
	public void onCreate() {
		super.onCreate();
		PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
		this.databaseManager = new DatabaseManager(this);
	}
	
	public void readSettings() {
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		this.audiobookBaseDir = sharedPref.getString("pref_base_dir", getString(R.string.pref_base_dir_default));
		this.autoplay = sharedPref.getBoolean("pref_autoplay", true);
		runFilescanner();
	}
	
	private void runFilescanner() {
		FileScanner fileScanner = new FileScanner(this);
		fileScanner.doInBackground();
	}
	
	public List<String> getCurrentPlaylist() {
		return this.currentPlaylist;
	}

	private void readPlaylist(String playlist) {
		File playlistFile = new File(playlist);
		if (playlist != null && playlistFile.exists()) {
			try {
				FileInputStream fis = new FileInputStream(playlistFile);
				DataInputStream in = new DataInputStream(fis);
				BufferedReader br = new BufferedReader(new InputStreamReader(in));
				String line;
				while ((line = br.readLine()) != null) {
					if(!line.startsWith("#")){
						this.currentPlaylist.add(line);
					}
				}
				in.close();
				br.close();
				fis.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public String getCurrentAudiobook() {
		return currentAudiobook;
	}

	public void setCurrentAudiobook(String currentAudiobook) {
		this.currentAudiobook = currentAudiobook;
		readPlaylistForCurrentAudiobook();
	}

	private void readPlaylistForCurrentAudiobook() {
    	String playlist = this.audiobookBaseDir + File.separator + this.currentAudiobook + File.separator + this.currentAudiobook + ".m3u";
    	this.currentPlaylist.clear();
		readPlaylist(playlist);
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


	public boolean isAutoplay() {
		return autoplay;
	}

	public void setAutoplay(boolean autoplay) {
		this.autoplay = autoplay;
	}
}
