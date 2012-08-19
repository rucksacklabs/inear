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
import de.reneruck.inear.db.AsyncGetBookmark;
import de.reneruck.inear.db.DatabaseManager;
import de.reneruck.inear.file.FileScanner;


public class AppContext extends Application {

	private String audiobookBaseDir = "";
	private boolean autoplay;
	
	private DatabaseManager databaseManager;
	private CurrentAudiobook currentAudiobookBean;

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
	
	private void readPlaylist(String playlistPath) {
		File playlistFile = new File(playlistPath);
		List<String> playlist = new ArrayList<String>();
		if (playlistPath != null && playlistFile.exists()) {
			try {
				FileInputStream fis = new FileInputStream(playlistFile);
				DataInputStream in = new DataInputStream(fis);
				BufferedReader br = new BufferedReader(new InputStreamReader(in));
				String line;
				while ((line = br.readLine()) != null) {
					if(!line.startsWith("#")){
						playlist.add(line);
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
		this.currentAudiobookBean.setPlaylist(playlist);
	}
	
	public void setCurrentAudiobook(String currentAudiobook) {
		readCurrentAduiobookValues(currentAudiobook);
	}

	private void readCurrentAduiobookValues(String currentAudiobook) {
		this.currentAudiobookBean = new CurrentAudiobook(this, currentAudiobook);
		readPlaylistForCurrentAudiobook();
		getStoredBookmark();
	}

	private void readPlaylistForCurrentAudiobook() {
    	String playlist = this.audiobookBaseDir + File.separator + this.currentAudiobookBean.getName() + File.separator + this.currentAudiobookBean.getName() + ".m3u";
		readPlaylist(playlist);
	}

	private void getStoredBookmark() {
		AsyncGetBookmark getBookmarkTask = new AsyncGetBookmark(this.databaseManager);
		Bookmark bookmark = getBookmarkTask.doInBackground(this.currentAudiobookBean.getName());
		if(bookmark != null)
		{
			this.currentAudiobookBean.setBookmark(bookmark);
			this.currentAudiobookBean.setCurrentTrack(bookmark.getTrackNumber());
		}
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

	public CurrentAudiobook getCurrentAudiobookBean() {
		return currentAudiobookBean;
	}

	public void setCurrentAudiobookBean(CurrentAudiobook currentAudiobookBean) {
		this.currentAudiobookBean = currentAudiobookBean;
	}
}
