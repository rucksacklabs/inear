package de.reneruck.inear.mediaservice;

import java.util.List;

import android.os.IBinder;

public interface PlaybackServiceControl extends IBinder {

	public int getCurrentPlaybackPosition();
	public int getCurrentTrack();
	public List<String> getCurrentPlaylist();
	
	public void loadCurrentAudiobook();
	public void setTrack(int trackNr);
	public void setPlaybackPosisition(int position);
	
	public void startPlayback();
	public void pausePlayback();
	public void resumePlayback();
	public void nextTrack();
	public void prevTrack();
	
	public boolean isPlaying();
	public int getDuration();
}
