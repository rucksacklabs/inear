package de.reneruck.inear;

import android.app.Application;


public class AppContext extends Application {

	private String currentAudiobook;
	private String audiobookBaseDir = "/sdcard/Audiobooks";

	public String getCurrentAudiobook() {
		return currentAudiobook;
	}

	public void setCurrentAudiobook(String currentAudiobook) {
		this.currentAudiobook = currentAudiobook;
	}

	public String getAudiobokkBaseDir() {
		return this.audiobookBaseDir;
	}
}
