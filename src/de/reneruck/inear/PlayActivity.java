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

import android.app.Activity;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class PlayActivity extends Activity {

    private static final String TAG = "PlayActivity";
	private AppContext appContext;
	private String currentAudiobook;
	private List<String> currentPlaylist = new LinkedList<String>();
	private MediaPlayer mediaPlayer;
	private int currentTrackNumber = 0;
	private Bookmark currentBookmark;
	private DatabaseManager databaseManager;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        this.appContext = (AppContext) getApplicationContext();
        this.databaseManager = this.appContext.getDatabaseManager();
        
        initializePlayControl();

        getAudiobookToPlay();
        getPlaylistToPlay();
		initializeMediaplayer();
    }

	private void initializeMediaplayer() {
    	this.mediaPlayer = new MediaPlayer();
    	this.mediaPlayer.setOnCompletionListener(this.trackFinishedListener);
    	this.mediaPlayer.setOnPreparedListener(this.preparedListener);
    	this.mediaPlayer.setScreenOnWhilePlaying(true); 
    	setNewDataSource();
    	this.mediaPlayer.prepareAsync();
	}

    private void initializePlayControl() {
    	Button bottonNext = (Button) findViewById(R.id.button_next);
    	bottonNext.setOnClickListener(this.nextButtonClickListener);
    	
    	Button bottonPlay = (Button) findViewById(R.id.button_play);
    	bottonPlay.setOnClickListener(this.playButtonClickListener);
    	
    	Button bottonPrev = (Button) findViewById(R.id.button_prev);
    	bottonPrev.setOnClickListener(this.prevButtonClickListener);
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
	
	private OnPreparedListener preparedListener = new OnPreparedListener() {
		
		@Override
		public void onPrepared(MediaPlayer mp) {
			mediaPlayer.start();
		}
	};

    private OnCompletionListener trackFinishedListener = new OnCompletionListener() {
		
		@Override
		public void onCompletion(MediaPlayer mp) {
			setNextTrack();
		}

	};
	
	private void setNextTrack() {
		this.currentTrackNumber++;
		if(this.currentTrackNumber < this.currentPlaylist.size())
		{
			setNewDataSource();
			this.mediaPlayer.prepareAsync();
		}
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
				((Button)v).setText("Play");
			} else {
				mediaPlayer.start();
				((Button)v).setText("||");
			}
		}
	};
	
	private OnClickListener prevButtonClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			Toast.makeText(getApplicationContext(), R.string.no_way_back, Toast.LENGTH_SHORT).show();
		}
	};
	
    @Override
    protected void onStop() {
    	super.onStop();
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
}
