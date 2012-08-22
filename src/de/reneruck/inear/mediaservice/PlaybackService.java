package de.reneruck.inear.mediaservice;

import java.io.IOException;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.IBinder;
import de.reneruck.inear.AppContext;
import de.reneruck.inear.Bookmark;
import de.reneruck.inear.CurrentAudiobook;

public class PlaybackService extends Service {

	private MediaPlayer mediaPlayer;
	private AppContext appContext;
	private CurrentAudiobook currentAudiobookBean;
	private boolean isPrepared = false;

	@Override
	public void onCreate() {
		this.appContext = (AppContext) getApplicationContext();
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		initializeMediaplayer();
		return super.onStartCommand(intent, flags, startId);
	}

	public void loadCurrentAudiobook() {
		this.currentAudiobookBean = this.appContext.getCurrentAudiobookBean();
		if(this.currentAudiobookBean != null && this.currentAudiobookBean.getBookmark() != null)
		{
			setMediaplayerToBookmarkedTrack();
		}
		setDatasourceToCurrentTrack();
	}

	private void setMediaplayerToBookmarkedTrack() {
		Bookmark bookmark = this.currentAudiobookBean.getBookmark();
		this.currentAudiobookBean.setCurrentTrack(bookmark.getTrackNumber());
	}

	@Override
	public IBinder onBind(Intent intent) {
		return new PlaybackServiceControlImpl(this);
	}

	private void initializeMediaplayer() {
		this.mediaPlayer = new MediaPlayer();
		this.mediaPlayer.setOnCompletionListener(this.trackFinishedListener);
	}

	private void setDatasourceToCurrentTrack() {
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
	
	private OnPreparedListener onMediaplayerPrepareListener = new OnPreparedListener() {
		
		@Override
		public void onPrepared(MediaPlayer mp) {
			isPrepared = true;
		}
	};

	private OnCompletionListener trackFinishedListener = new OnCompletionListener() {

		@Override
		public void onCompletion(MediaPlayer mp) {
			setNextTrack();
		}

	};

	private void setNextTrack() {
		this.currentAudiobookBean.setNextTrack();
	}
	
	public void startPlayback()	{
		resumePlayback();
	}
	
	public void resumePlayback() {
		if(this.isPrepared)
		{
			this.mediaPlayer.start();
		}
//		((ImageView)findViewById(R.id.button_play)).setImageResource(android.R.drawable.ic_media_pause);
	}

	public boolean isPlaying() {
		return mediaPlayer.isPlaying();
	}
	
	public void pausePlayback() {
		mediaPlayer.pause();
//		((ImageView)findViewById(R.id.button_play)).setImageResource(android.R.drawable.ic_media_play);
	}

	public void setPlaybackPosotion(int position) {
		// TODO Auto-generated method stub
		
	}
}
