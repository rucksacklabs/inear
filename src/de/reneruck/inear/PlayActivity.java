package de.reneruck.inear;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

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
import de.reneruck.inear.db.AsyncGetBookmark;
import de.reneruck.inear.db.AsyncStoreBookmark;
import de.reneruck.inear.db.DatabaseManager;

public class PlayActivity extends Activity {

    private static final String TAG = "PlayActivity";
	private AppContext appContext;
	private String currentAudiobook;
	private List<String> currentPlaylist = new LinkedList<String>();
	private MediaPlayer mediaPlayer;
	private int currentTrackNumber = 0;
	private Bookmark currentBookmark;
	private DatabaseManager databaseManager;
	private SeekBar seekbar;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        this.appContext = (AppContext) getApplicationContext();
        this.databaseManager = this.appContext.getDatabaseManager();
        
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        
        initializePlayControl();

        getAudiobookToPlay();
        getPlaylistToPlay();
    }

	@Override
	protected void onResume() {
		super.onResume();
		getStoredBookmark();
		applyBookmarkValues();
		setTextIndikator();
		initializeMediaplayer();
		this.mediaPlayer.setOnPreparedListener(this.resumePreparedListener);
		this.mediaPlayer.prepareAsync();
	}
	
	private void applyBookmarkValues() {
		if(this.currentBookmark != null)
		{
			this.currentTrackNumber = this.currentBookmark.getTrackNumber();
		}
	}

	
	private void initializeMediaplayer() {
    	this.mediaPlayer = new MediaPlayer();
    	this.mediaPlayer.setOnCompletionListener(this.trackFinishedListener);
    	this.mediaPlayer.setOnPreparedListener(this.nextTrackPreparedListener);
    	this.mediaPlayer.setScreenOnWhilePlaying(true);
    	setNewDataSource();
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
			this.mediaPlayer.setDataSource(this.currentPlaylist.get(this.currentTrackNumber));
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
	
	private OnPreparedListener resumePreparedListener = new OnPreparedListener() {
		
		@Override
		public void onPrepared(MediaPlayer mp) {
			resumeFromBookmark();
		}


	};
	
	private void resumeFromBookmark() {
		setSeekbar();
		if(this.currentBookmark != null)
		{
			this.seekbar.setProgress(this.currentBookmark.getPlaybackPosition());
			this.mediaPlayer.seekTo(this.currentBookmark.getPlaybackPosition());
		}
		if(this.appContext.isAutoplay()){
			this.mediaPlayer.start();
		}
	}
	
	private OnPreparedListener nextTrackPreparedListener = new OnPreparedListener() {
		
		@Override
		public void onPrepared(MediaPlayer mp) {
			setSeekbar();
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

		@Override
		public void onProgressChanged(SeekBar seekbar, int progress, boolean fromUser) {
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			mediaPlayer.pause();
		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			mediaPlayer.seekTo(seekBar.getProgress());
			mediaPlayer.start();
		}
		
	};

	
	private void setPreveriousTrack() {
		this.currentTrackNumber--;
		if(this.currentTrackNumber > 0)
		{
			setNewDataSource();
			this.mediaPlayer.prepareAsync();
		}
	}
	
	private void setNextTrack() {
		this.currentTrackNumber++;
		if(this.currentTrackNumber < this.currentPlaylist.size())
		{
			setNewDataSource();
			setTextIndikator();
			this.mediaPlayer.setOnPreparedListener(this.nextTrackPreparedListener);
			this.mediaPlayer.prepareAsync();
		}
	}

	private void setTextIndikator() {
		TextView text = (TextView) findViewById(R.id.text_currentTrack);
		text.setText(this.currentAudiobook + " - " + this.currentPlaylist.get(this.currentTrackNumber).replace(this.appContext.getAudiobokkBaseDir(), " ").trim());
	}

	private void getPlaylistToPlay() {
    	String playlist = this.appContext.getAudiobokkBaseDir() + File.separator + this.currentAudiobook + File.separator + this.currentAudiobook + ".m3u";
		readPlaylist(playlist);
	}

	private void readPlaylist(String playlist) {
		File playlistFile = new File(playlist);
		if (playlist != null && playlistFile.exists()) {
			try {
				FileInputStream fis = new FileInputStream(playlistFile);
				DataInputStream in = new DataInputStream(fis);
				BufferedReader br = new BufferedReader(new InputStreamReader(in));
				String line;
				while ((line = br.readLine()) != null) {
					if(!line.startsWith("#")){
						this.currentPlaylist.add(line);
					}
				}
				in.close();
				br.close();
				fis.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void getAudiobookToPlay() {
		this.currentAudiobook = this.appContext.getCurrentAudiobook();
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
			setNextTrack();
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
		if(this.currentBookmark != null)
		{
			this.currentBookmark.setTrackNumber(this.currentTrackNumber);
			this.currentBookmark.setPlaybackPosition(this.mediaPlayer.getCurrentPosition());
		} else {
			this.currentBookmark = new Bookmark(this.currentAudiobook, this.currentTrackNumber, this.mediaPlayer.getCurrentPosition());
		}
		storeBookmark();
	}

	private void storeBookmark() {
		if(this.databaseManager != null)
		{
			AsyncStoreBookmark storeBookmarkTask = new AsyncStoreBookmark(this.databaseManager);
			storeBookmarkTask.doInBackground(this.currentBookmark);
		} else {
			String string = getString(R.string.no_databasemanager);
			Toast.makeText(getApplicationContext(), string, Toast.LENGTH_LONG).show();
			Log.e(TAG, string);
		}
	}
	
	private void getStoredBookmark() {
		AsyncGetBookmark getBookmarkTask = new AsyncGetBookmark(this.databaseManager);
		this.currentBookmark = getBookmarkTask.doInBackground(this.currentAudiobook);
	}

}
