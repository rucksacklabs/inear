package de.reneruck.inear;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;

import android.app.ActionBar;
import android.app.Activity;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import de.reneruck.inear.db.AsyncStoreBookmark;
import de.reneruck.inear.db.DatabaseManager;

public class PlayActivity extends Activity {

    private static final String TAG = "PlayActivity";
	private AppContext appContext;
	private MediaPlayer mediaPlayer;
	private DatabaseManager databaseManager;
	private SeekBar seekbar;
	private Thread seekbarUpdateThread;
	private CurrentAudiobook currentAudiobookBean;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        this.appContext = (AppContext) getApplicationContext();
        this.databaseManager = this.appContext.getDatabaseManager();
        
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        
        initializePlayControl();
    }

	private void initializeSeekbarUpdateThread() {
		this.seekbarUpdateThread = new Thread(new Runnable() {
			
			private int lastPos = 0;

			@Override
			public void run() {
				while(mediaPlayer != null && seekbar!= null)
				{
					int currentPos = mediaPlayer.getCurrentPosition();
					if(currentPos != this.lastPos)
					{
						seekbar.setProgress(currentPos);
						this.lastPos = currentPos;
					}
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		});
		this.seekbarUpdateThread.start();
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		getCurrentAudiobookBean();
		registerForAudiobookBeanChanges();
		
		setCurrentTrackNameIndikator();
		initializeMediaplayer();
		setNewDataSource();
		initializeSeekbarUpdateThread();
		
		this.mediaPlayer.setOnPreparedListener(this.preparedListenerAfterResume);
		this.mediaPlayer.prepareAsync();
	}
	
	private void registerForAudiobookBeanChanges() {
		this.currentAudiobookBean.addPropertyChangeListener(this.currentAudiobookBeanChangeListener);
	}

	private void getCurrentAudiobookBean() {
		this.currentAudiobookBean = this.appContext.getCurrentAudiobookBean();
	}

	private void initializeMediaplayer() {
    	this.mediaPlayer = new MediaPlayer();
    	this.mediaPlayer.setOnCompletionListener(this.trackFinishedListener);
    	this.mediaPlayer.setOnPreparedListener(this.nextTrackPreparedListener);
    	this.mediaPlayer.setScreenOnWhilePlaying(true);
	}

    private void initializePlayControl() {
    	ImageView bottonNext = (ImageView) findViewById(R.id.button_next);
    	bottonNext.setOnClickListener(this.nextButtonClickListener);
    	
    	initializePlayPauseButton();
    	
    	ImageView bottonPrev = (ImageView) findViewById(R.id.button_prev);
    	bottonPrev.setOnClickListener(this.prevButtonClickListener);
    	
		this.seekbar = (SeekBar) findViewById(R.id.seekBar1);
		this.seekbar.setOnSeekBarChangeListener(this.onSeekbarDragListener);
	}
	
	private void initializePlayPauseButton() {
		ImageView bottonPlay = (ImageView) findViewById(R.id.button_play);
		bottonPlay.setOnClickListener(this.playButtonClickListener);
		if(this.appContext.isAutoplay()){
			((ImageView)bottonPlay).setImageResource(android.R.drawable.ic_media_pause);
		}
	}

	private void setNewDataSource() {
		try {
			this.mediaPlayer.reset();
			this.mediaPlayer.setDataSource(this.currentAudiobookBean.getPlaylist().get(this.currentAudiobookBean.getCurrentTrack()));
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
	
	private PropertyChangeListener currentAudiobookBeanChangeListener = new PropertyChangeListener() {
		
		@Override
		public void propertyChange(PropertyChangeEvent event) {
			if("track".equals(event.getPropertyName()) && mediaPlayer != null)
			{
				switchTrack();
			}
		}
	};
	
	private OnPreparedListener preparedListenerAfterResume = new OnPreparedListener() {
		
		@Override
		public void onPrepared(MediaPlayer mp) {
			setSeekbar();
			setDurationIndicator();
			setCurrentPlaytimeIndicator();
			setSeekbarToBookmarkPosition();
			setMediaplayerToBookmarkPosition();
			startPlayOnAutoplay();
		}

	};
	
	private void setCurrentPlaytimeIndicator() {
		setCurrentPlaytimeIndicator(this.mediaPlayer.getCurrentPosition());
	}
	
	private void setCurrentPlaytimeIndicator(int progress) {
		TextView currentTimeField = (TextView)findViewById(R.id.playback_current_time);
		int seconds = (int) (progress / 1000) % 60 ;
		int minutes = (int) ((progress / (1000*60)) % 60);	
		String secoundsString = seconds < 10 ? "0" + seconds : String.valueOf(seconds);
		String durationText = minutes + ":" + secoundsString;
		currentTimeField.setText(durationText);
	}
	
	private void setDurationIndicator() {
		int duration = this.mediaPlayer.getDuration();
		TextView durationField = (TextView)findViewById(R.id.playback_max_time);
		int seconds = (int) (duration / 1000) % 60 ;
		int minutes = (int) ((duration / (1000*60)) % 60);		
		String secoundsString = seconds < 10 ? "0" + seconds : String.valueOf(seconds);
		String durationText = minutes + ":" + secoundsString;
		durationField.setText(durationText);
	}
	
	private void setMediaplayerToBookmarkPosition() {
		if(this.currentAudiobookBean.getBookmark() != null)
		{
			this.mediaPlayer.seekTo(this.currentAudiobookBean.getBookmark().getPlaybackPosition());
		}
	}
	
	private void setSeekbarToBookmarkPosition() {
		if(this.currentAudiobookBean.getBookmark() != null)
		{
			this.seekbar.setProgress(this.currentAudiobookBean.getBookmark().getPlaybackPosition());
		}
	}

	private void startPlayOnAutoplay() {
		if(this.appContext.isAutoplay()){
			this.mediaPlayer.start();
		}
	}
	
	private OnPreparedListener nextTrackPreparedListener = new OnPreparedListener() {
		
		@Override
		public void onPrepared(MediaPlayer mp) {
			setSeekbar();
			setDurationIndicator();
			mediaPlayer.start();
		}

	};
	
	private void setSeekbar() {
		seekbar.setMax(mediaPlayer.getDuration());
		seekbar.setProgress(0);
	}

    private OnCompletionListener trackFinishedListener = new OnCompletionListener() {
		
		@Override
		public void onCompletion(MediaPlayer mp) {
			setNextTrack();
		}

	};
	
	private OnSeekBarChangeListener onSeekbarDragListener = new OnSeekBarChangeListener() {

		private boolean wasPlaying;

		@Override
		public void onProgressChanged(SeekBar seekbar, int progress, boolean fromUser) {
			seekbar.setProgress(progress);
			setCurrentPlaytimeIndicator(progress);
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			this.wasPlaying = mediaPlayer.isPlaying();
			mediaPlayer.pause();
		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			mediaPlayer.seekTo(seekBar.getProgress());
			if(this.wasPlaying) mediaPlayer.start();
		}
		
	};

	
	private void setPreveriousTrack() {
		if(this.currentAudiobookBean.setPreviousTrack())
		{
			switchTrack();
		}
	}
	
	private void setNextTrack() {
		if(this.currentAudiobookBean.setNextTrack())
		{
			switchTrack();
		}
	}

	private void switchTrack() {
		setNewDataSource();
		setCurrentTrackNameIndikator();
		this.mediaPlayer.setOnPreparedListener(this.nextTrackPreparedListener);
		this.mediaPlayer.prepareAsync();
	}

	private void setCurrentTrackNameIndikator() {
		TextView text = (TextView) findViewById(R.id.text_currentTrack);
		text.setText(this.currentAudiobookBean.getCurrentTrackName());
	}

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_play, menu);
        return true;
    }

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
        case android.R.id.home:
        	finish();
        	break;
        case R.id.menu_playlist:
        	
        	break;
		}
		return true;
	}
	
	private OnClickListener nextButtonClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			mediaPlayer.stop();
			setNextTrack();
		}
	};
	
	private OnClickListener playButtonClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if(mediaPlayer.isPlaying()){
				mediaPlayer.pause();
				((ImageView)v).setImageResource(android.R.drawable.ic_media_play);
			} else {
				mediaPlayer.start();
				((ImageView)v).setImageResource(android.R.drawable.ic_media_pause);
			}
		}
	};
	
	private OnClickListener prevButtonClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			mediaPlayer.stop();
			setPreveriousTrack();
		}
	};
	
    @Override
    protected void onPause() {
    	super.onPause();
    	createOrUpdateBookmark();
    	this.mediaPlayer.stop();
    	this.mediaPlayer.release();
    	this.mediaPlayer = null;
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
