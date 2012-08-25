package de.reneruck.inear.mediaservice;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;
import de.reneruck.inear.AppContext;
import de.reneruck.inear.Bookmark;
import de.reneruck.inear.CurrentAudiobook;
import de.reneruck.inear.R;
import de.reneruck.inear.db.AsyncStoreBookmark;
import de.reneruck.inear.db.DatabaseManager;

public class PlaybackService extends Service {

	private static final String TAG = "InEar - PlaybackService";
	
	private static final int START_ID = 666;
	private MediaPlayerThread mediaPlayer;
	private AppContext appContext;
	private CurrentAudiobook currentAudiobookBean;
	private DatabaseManager databaseManager;
	private boolean isPrepared = false;

	@Override
	public void onCreate() {
		super.onCreate();
		this.appContext = (AppContext) getApplicationContext();
		this.databaseManager = appContext.getDatabaseManager();
		initializeMediaplayer();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		loadCurrentAudiobook();
		return START_ID;
	}

	public void loadCurrentAudiobook() {
		this.currentAudiobookBean = this.appContext.getCurrentAudiobookBean();
		if(this.currentAudiobookBean != null && this.currentAudiobookBean.getBookmark() != null)
		{
			setMediaplayerToBookmarkedTrack();
		}
		this.mediaPlayer.setCurrentAudiobookBean(this.currentAudiobookBean);
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
		this.mediaPlayer = new MediaPlayerThread();
		this.mediaPlayer.start();
	}

	private void setDatasourceToCurrentTrack() {
		this.mediaPlayer.setDatasourceToCurrentTrack();
	}
	
	public void setPrevTrack() {
		this.mediaPlayer.setPreviousTrack();
	}
	
	public void setNextTrack() {
		this.mediaPlayer.setNextTrack();
	}
	
	public void startPlayback()	{
		resumePlayback();
	}
	
	public void resumePlayback() {
		this.mediaPlayer.startPlayback();
	}

	public boolean isPlaying() {
		return mediaPlayer.isPlaying();
	}
	
	public void pausePlayback() {
		mediaPlayer.pause();
		createOrUpdateBookmark();
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
		return this.mediaPlayer.getDuration();
	}

	public String getCurrentTrackName() {
		return this.currentAudiobookBean.getCurrentTrackName();
	}

	private void createOrUpdateBookmark() {
		if(this.currentAudiobookBean.getBookmark() != null)
		{
			this.currentAudiobookBean.getBookmark().setTrackNumber(this.currentAudiobookBean.getCurrentTrack());
			this.currentAudiobookBean.getBookmark().setPlaybackPosition(this.mediaPlayer.getCurrentPosition());
		} else {
			this.currentAudiobookBean.setBookmark(new Bookmark(this.currentAudiobookBean.getName(), this.currentAudiobookBean.getCurrentTrack(), this.mediaPlayer.getCurrentPosition()));
		}
		storeBookmark();
	}

	private void storeBookmark() {
		if(this.databaseManager != null)
		{
			AsyncStoreBookmark storeBookmarkTask = new AsyncStoreBookmark(this.databaseManager);
			storeBookmarkTask.doInBackground(this.currentAudiobookBean.getBookmark());
		} else {
			String string = getString(R.string.no_databasemanager);
			Toast.makeText(getApplicationContext(), string, Toast.LENGTH_LONG).show();
			Log.e(TAG, string);
		}
	}
}
