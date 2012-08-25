package de.reneruck.inear.mediaservice;

import java.io.IOException;

import de.reneruck.inear.CurrentAudiobook;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.util.Log;

public class MediaPlayerThread extends Thread {

	private static final String TAG = "InEar - MediaplayerThread";
	private MediaPlayer mediaPlayer;
	private CurrentAudiobook currentAudiobookBean;
	private boolean isPrepared;

	public MediaPlayerThread() {
		this.mediaPlayer = new MediaPlayer();
		this.mediaPlayer.setOnCompletionListener(this.trackFinishedListener);
	}
	
	@Override
	public void run() {
		this.mediaPlayer.start();
	}
	
	private OnCompletionListener trackFinishedListener = new OnCompletionListener() {

		@Override
		public void onCompletion(MediaPlayer mp) {
			setNextTrack();
		}

	};
	
	public void setPreviousTrack() {
		this.currentAudiobookBean.setPreviousTrack();
	}
	
	public void setNextTrack() {
		this.currentAudiobookBean.setNextTrack();
	}

	public boolean isPlaying() {
		return this.mediaPlayer.isPlaying();
	}

	public void pause() {
		this.mediaPlayer.pause();
	}

	public void seekTo(int position) {
		this.mediaPlayer.seekTo(position);
	}

	public int getCurrentPosition() {
		return this.mediaPlayer.getCurrentPosition();
	}

	public void setDatasourceToCurrentTrack() {
		try {
			this.mediaPlayer.reset();
			this.isPrepared = false;
			this.mediaPlayer.setDataSource(this.currentAudiobookBean.getPlaylist().get(this.currentAudiobookBean.getCurrentTrack()));
			this.mediaPlayer.setOnPreparedListener(this.onMediaplayerPrepareListener);
			this.mediaPlayer.prepareAsync();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public boolean isPrepared() {
		return isPrepared;
	}

	public void setPrepared(boolean isPrepared) {
		this.isPrepared = isPrepared;
	}

	private OnPreparedListener onMediaplayerPrepareListener = new OnPreparedListener() {
		
		@Override
		public void onPrepared(MediaPlayer mp) {
			Log.d(TAG, "mediaplayer prepared");
			isPrepared = true;
		}
	};

	public int getDuration() {
		if(this.isPrepared)
		{
			return this.getDuration();
		}
		return 0;
	}

	public CurrentAudiobook getCurrentAudiobookBean() {
		return currentAudiobookBean;
	}

	public void setCurrentAudiobookBean(CurrentAudiobook currentAudiobookBean) {
		this.currentAudiobookBean = currentAudiobookBean;
	}

	public void startPlayback() {
		if(this.isPrepared)
		{
			this.run();
		} else {
			Log.d(TAG, "Cannot resume playback, mediaplayer not yet prepared");
		}
	}
}
