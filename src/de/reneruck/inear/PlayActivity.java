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
import android.view.Menu;

public class PlayActivity extends Activity {

    private AppContext appContext;
	private String currentAudiobook;
	private List<String> currentPlaylist = new LinkedList<String>();
	private MediaPlayer mediaPlayer;
	private int currentTrackNumber = 0;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        this.appContext = (AppContext) getApplicationContext();
        
        getAudiobookToPlay();
        getPlaylistToPlay();
		initializeMediaplayer();
    }

	private void initializeMediaplayer() {
    	this.mediaPlayer = new MediaPlayer();
    	this.mediaPlayer.setOnCompletionListener(this.trackFinishedListener);
    	setNewDataSource();
    	this.mediaPlayer.setOnPreparedListener(this.preparedListener);
    	this.mediaPlayer.prepareAsync();
	}

	private void setNewDataSource() {
		try {
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
			play();
		}
	};

	private void play() {
		this.mediaPlayer.start();
	}
    
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

    @Override
    protected void onStop() {
    	super.onStop();
    	createBookmark();
    	this.mediaPlayer.stop();
    	this.mediaPlayer.release();
    	this.mediaPlayer = null;
    }

	private void createBookmark() {
		this.mediaPlayer.getCurrentPosition();
	}
}
