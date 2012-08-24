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

	private static final int START_ID = 666;
	private MediaPlayer mediaPlayer;
	private AppContext appContext;
	private CurrentAudiobook currentAudiobookBean;
	private boolean isPrepared = false;

	@Override
	public void onCreate() {
		super.onCreate();
		this.appContext = (AppContext) getApplicationContext();
		initializeMediaplayer();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		return START_ID;
	}

	public void loadCurrentAudiobook() {
		this.currentAudiobookBean = this.appContext.getAudiobookBeanFactory().getAudiobookBeanForName(this.appContext.getCurrentAudiobookName());
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

	public void setPrevTrack() {
		this.currentAudiobookBean.setPreviousTrack();
	}
	
	public void setNextTrack() {
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
	}

	public boolean isPlaying() {
		return mediaPlayer.isPlaying();
	}
	
	public void pausePlayback() {
		mediaPlayer.pause();
	}

	public void setPlaybackPosotion(int position) {
		mediaPlayer.seekTo(position);
	}

	public int getCurrentPlaybackPosition() {
		int currentPosition = 0;
		if(this.mediaPlayer != null) currentPosition = this.mediaPlayer.getCurrentPosition();
		return currentPosition;
	}

	public int getCurrentTrack() {
		return this.currentAudiobookBean.getCurrentTrack();
	}

	public int getDuration() {
		if(this.isPrepared)
		{
			return this.mediaPlayer.getDuration();
		} else {
			return 0;
		}
	}

	public String getCurrentTrackName() {
		return this.currentAudiobookBean.getCurrentTrackName();
	}

	public CurrentAudiobook getCurrentAudiobook() {
		return null;
	}
	
//	private void createOrUpdateBookmark() {
//		if(this.currentAudiobookBean.getBookmark() != null)
//		{
//			this.currentAudiobookBean.getBookmark().setTrackNumber(this.currentAudiobookBean.getCurrentTrack());
//			this.currentAudiobookBean.getBookmark().setPlaybackPosition(this.mediaPlayer.getCurrentPosition());
//		} else {
//			this.currentAudiobookBean.setBookmark(new Bookmark(this.currentAudiobookBean.getName(), this.currentAudiobookBean.getCurrentTrack(), this.mediaPlayer.getCurrentPosition()));
//		}
//		storeBookmark();
//	}
//
//	private void storeBookmark() {
//		if(this.databaseManager != null)
//		{
//			AsyncStoreBookmark storeBookmarkTask = new AsyncStoreBookmark(this.databaseManager);
//			storeBookmarkTask.doInBackground(this.currentAudiobookBean.getBookmark());
//		} else {
//			String string = getString(R.string.no_databasemanager);
//			Toast.makeText(getApplicationContext(), string, Toast.LENGTH_LONG).show();
//			Log.e(TAG, string);
//		}
//	}
}
