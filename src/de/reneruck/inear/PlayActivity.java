package de.reneruck.inear;

import android.app.ActionBar;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import de.reneruck.inear.mediaservice.PlaybackService;
import de.reneruck.inear.mediaservice.PlaybackServiceControl;

public class PlayActivity extends Activity {

    private static final String TAG = "PlayActivity";
	private AppContext appContext;
	private SeekBar seekbar;
	private Thread seekbarUpdateThread;
	private CurrentAudiobook currentAudiobookBean;
	private PlaybackServiceControl playbackServiceBinder;
	private boolean isBound;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        this.appContext = (AppContext) getApplicationContext();
        
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        
        initializePlayControl();
    }

	@Override
	protected void onResume() {
		super.onResume();
		
		startSeekbarUpdateThread();
		bindToPlaybackService();
	}
	
	private void startSeekbarUpdateThread() {
		this.seekbarUpdateThread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				while(isBound && playbackServiceBinder.isPlaying())
				{
						seekbar.setProgress(playbackServiceBinder.getCurrentPlaybackPosition());
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

	
	private ServiceConnection serviceConnection = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
			isBound = false;
			playbackServiceBinder = null;
		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			if(service instanceof PlaybackServiceControl)
			{
				playbackServiceBinder = (PlaybackServiceControl)service;
				isBound = true;
				playbackServiceBinder.loadCurrentAudiobook();
				setCurrentTrackNameIndikator();
				setDurationIndicator();
			}
		}
	};
	
	private void bindToPlaybackService() {
		bindService(new Intent(this, PlaybackService.class), this.serviceConnection, Context.BIND_AUTO_CREATE);
	}

    private void initializePlayControl() {
    	ImageView bottonNext = (ImageView) findViewById(R.id.button_next);
    	bottonNext.setOnClickListener(this.nextButtonClickListener);
    	
    	ImageView bottonPlay = (ImageView) findViewById(R.id.button_play);
    	bottonPlay.setOnClickListener(this.playButtonClickListener);
    	
    	ImageView bottonPrev = (ImageView) findViewById(R.id.button_prev);
    	bottonPrev.setOnClickListener(this.prevButtonClickListener);
    	
		this.seekbar = (SeekBar) findViewById(R.id.seekBar1);
		this.seekbar.setOnSeekBarChangeListener(this.onSeekbarDragListener);
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
		if(isBound)
		{
			int duration = this.playbackServiceBinder.getDuration();
			TextView durationField = (TextView)findViewById(R.id.playback_max_time);
			int seconds = (int) (duration / 1000) % 60 ;
			int minutes = (int) ((duration / (1000*60)) % 60);		
			String secoundsString = seconds < 10 ? "0" + seconds : String.valueOf(seconds);
			String durationText = minutes + ":" + secoundsString;
			durationField.setText(durationText);
		}
	}
	
	private OnSeekBarChangeListener onSeekbarDragListener = new OnSeekBarChangeListener() {

		private boolean wasPlaying;

		@Override
		public void onProgressChanged(SeekBar seekbar, int progress, boolean fromUser) {
			seekbar.setProgress(progress);
			setCurrentPlaytimeIndicator(progress);
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			if(isBound)
			{
				this.wasPlaying = playbackServiceBinder.isPlaying();
				playbackServiceBinder.pausePlayback();
			}
		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			if(isBound)
			{
				playbackServiceBinder.setPlaybackPosisition(seekBar.getProgress());
				if(this.wasPlaying) playbackServiceBinder.resumePlayback();
			}
		}
		
	};

	
	private void setCurrentTrackNameIndikator() {
		if(this.isBound)
		{
			
			TextView text = (TextView) findViewById(R.id.text_currentTrack);
			text.setText(this.playbackServiceBinder.getCurrentTrackName());
		}
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
			if(isBound)
			{
				playbackServiceBinder.nextTrack();
			}
		}
	};
	
	private OnClickListener playButtonClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (isBound) {
				if (playbackServiceBinder.isPlaying()) {
					playbackServiceBinder.pausePlayback();
					((ImageView)findViewById(R.id.button_play)).setImageResource(android.R.drawable.ic_media_play);
				} else {
					playbackServiceBinder.resumePlayback();
					((ImageView)findViewById(R.id.button_play)).setImageResource(android.R.drawable.ic_media_pause);
				}
			}
		}
	};
	
	private OnClickListener prevButtonClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if(isBound)
			{
				playbackServiceBinder.prevTrack();
			}
		}
	};
	
    @Override
    protected void onPause() {
    	super.onPause();
    }

}
