package de.reneruck.inear;

import java.util.List;

public class CurrentAudiobook {

	private String name;
	private List<String> playlist;
	private int track = 0;
	private Bookmark bookmark;
	private AppContext appContext;
	
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
		this.track = track;
	}
	public Bookmark getBookmark() {
		return bookmark;
	}
	public void setBookmark(Bookmark bookmark) {
		this.bookmark = bookmark;
	}

	public boolean setPreviousTrack() {
		if(this.track -1 >= 0)
		{
			this.track--;
			return true;
		} else {
			return false;
		}
	}
	
	public boolean setNextTrack() {
		if(this.track +1 <= this.playlist.size())
		{
			this.track++;
			return true;
		} else {
			return false;
		}
	}

	public String getCurrentTrackName() {
		String string = this.playlist.get(this.track);
		return string.replace(this.appContext.getAudiobokkBaseDir(), " ").trim();
	}
}