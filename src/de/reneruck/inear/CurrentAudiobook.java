package de.reneruck.inear;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.List;

public class CurrentAudiobook {

	private String name;
	private List<String> playlist;
	private int track = 0;
	private Bookmark bookmark;
	private AppContext appContext;

	private PropertyChangeSupport changes = new PropertyChangeSupport(this);

	public CurrentAudiobook(AppContext context, String name) {
		this.appContext = context;
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<String> getPlaylist() {
		return playlist;
	}

	public void setPlaylist(List<String> playlist) {
		this.playlist = playlist;
	}

	public int getCurrentTrack() {
		return track;
	}

	public void setCurrentTrack(int track) {
		int oldTrack = this.track;
		this.track = track;
		this.changes.firePropertyChange("track", oldTrack, track);
	}

	public Bookmark getBookmark() {
		return bookmark;
	}

	public void setBookmark(Bookmark bookmark) {
		this.bookmark = bookmark;
	}

	public void setPreviousTrack() {
		if (this.track - 1 >= 0) {
			int oldtrack = this.track;
			this.track--;
			this.changes.firePropertyChange("track", oldtrack, this.track);
		}
	}

	public void setNextTrack() {
		if (this.track + 1 <= this.playlist.size()) {
			int oldtrack = this.track;
			this.track++;
			this.changes.firePropertyChange("track", oldtrack, this.track);
		}
	}

	public String getCurrentTrackName() {
		String string = this.playlist.get(this.track);
		return string.replace(this.appContext.getAudiobokkBaseDir(), " ").trim();
	}

	public void addPropertyChangeListener(PropertyChangeListener l) {
		this.changes.addPropertyChangeListener(l);
	}

	public void removePropertyChangeListener(PropertyChangeListener l) {
		this.changes.removePropertyChangeListener(l);
	}
}
