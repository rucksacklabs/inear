package de.reneruck.inear;

public class Bookmark {

	private int id;
	private String bookTitle;
	private int trackNumber;
	private int playbackPosition;
	
	public Bookmark(String bookTitle, int trackNumber, int playbackPosition) {
		super();
		this.bookTitle = bookTitle;
		this.trackNumber = trackNumber;
		this.playbackPosition = playbackPosition;
	}

	public String getBookTitle() {
		return bookTitle;
	}

	public void setBookTitle(String bookTitle) {
		this.bookTitle = bookTitle;
	}

	public int getTrackNumber() {
		return trackNumber;
	}

	public void setTrackNumber(int trackNumber) {
		this.trackNumber = trackNumber;
	}

	public int getPlaybackPosition() {
		return playbackPosition;
	}

	public void setPlaybackPosition(int playbackPosition) {
		this.playbackPosition = playbackPosition;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}
