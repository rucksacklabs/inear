package de.reneruck.inear;

import android.app.Application;


public class AppContext extends Application {

	private String currentAudiobook;

	public String getCurrentAudiobook() {
		return currentAudiobook;
	}

	public void setCurrentAudiobook(String currentAudiobook) {
		this.currentAudiobook = currentAudiobook;
	}
}
