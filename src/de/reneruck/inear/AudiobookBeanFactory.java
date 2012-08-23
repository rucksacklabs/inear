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

import de.reneruck.inear.db.AsyncGetBookmark;
import de.reneruck.inear.db.DatabaseManager;

public class AudiobookBeanFactory {

	private String audiobookName;
	private String audiobookBaseDir;
	private CurrentAudiobook currentAudiobookBean;
	private DatabaseManager databaseManager;

	public AudiobookBeanFactory(String audibooksBaseDir, DatabaseManager databaseManager) {
		this.audiobookBaseDir = audibooksBaseDir;
		this.databaseManager = databaseManager;
	}
	
	public CurrentAudiobook getAudiobookBeanForName(String name)
	{
		this.audiobookName = name;
		readCurrentAduiobookValues(this.audiobookName);
		readPlaylistForCurrentAudiobook();
		getStoredBookmark();
		return this.currentAudiobookBean;
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
	
	private void readCurrentAduiobookValues(String currentAudiobook) {
		this.currentAudiobookBean = new CurrentAudiobook(currentAudiobook, this.audiobookBaseDir);
		readPlaylistForCurrentAudiobook();
		getStoredBookmark();
	}
	
	private void readPlaylistForCurrentAudiobook() {
    	String playlist = this.audiobookBaseDir + File.separator + this.audiobookName + File.separator + this.audiobookName + ".m3u";
		readPlaylist(playlist);
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
}
