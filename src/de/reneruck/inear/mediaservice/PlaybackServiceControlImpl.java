package de.reneruck.inear.mediaservice;

import java.util.List;

import de.reneruck.inear.CurrentAudiobook;

import android.os.Binder;
import android.os.IInterface;

public class PlaybackServiceControlImpl extends Binder implements PlaybackServiceControl  {

	private PlaybackService playbackService;

	public PlaybackServiceControlImpl(PlaybackService playbackService) {
		this.playbackService = playbackService;
	}

	@Override
	public boolean isBinderAlive() {
		return this.playbackService != null;
	}

	@Override
	public IInterface queryLocalInterface(String descriptor) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean unlinkToDeath(DeathRecipient recipient, int flags) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void startPlayback() {
		handlePlayPause();
	}

	@Override
	public void pausePlayback() {
		handlePlayPause();
	}

	private void handlePlayPause() {
		if(this.playbackService.isPlaying())
		{
			this.playbackService.pausePlayback();
		} else {
			this.playbackService.resumePlayback();
		}
	}

	@Override
	public void nextTrack() {
		this.playbackService.setNextTrack();
	}

	@Override
	public void prevTrack() {
		this.playbackService.setPrevTrack();
	}

	@Override
	public int getCurrentPlaybackPosition() {
		return this.playbackService.getCurrentPlaybackPosition();
	}

	@Override
	public int getCurrentTrack() {
		return this.playbackService.getCurrentTrack();
	}

	@Override
	public List<String> getCurrentPlaylist() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void loadCurrentAudiobook() {
		this.playbackService.loadCurrentAudiobook();
	}

	@Override
	public void setTrack(int trackNr) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setPlaybackPosisition(int position) {
		this.playbackService.setPlaybackPosotion(position);
	}

	@Override
	public boolean isPlaying() {
		return this.playbackService.isPlaying();
	}

	@Override
	public void resumePlayback() {
		this.playbackService.resumePlayback();
	}

	@Override
	public int getDuration() {
		return this.playbackService.getDuration();
	}

	@Override
	public String getCurrentTrackName() {
		return this.playbackService.getCurrentTrackName();
	}

	@Override
	public CurrentAudiobook getCurrentAudiobookBean() {
		return this.playbackService.getCurrentAudiobook();
	}

}
