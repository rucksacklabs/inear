package de.reneruck.inear;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import de.reneruck.inear.db.DatabaseManager;
import de.reneruck.inear.file.FileScanner;
import de.reneruck.inear.mediaservice.PlaybackService;


public class AppContext extends Application {

	private static final String TAG = "InEar - AppContext";

	private String audiobookBaseDir = "";

	private DatabaseManager databaseManager;
	private Settings settings;
	private CurrentAudiobook currentAudiobookBean;
	private AudiobookBeanFactory audiobookBeanFactory;

	@Override
	public void onCreate() {
		super.onCreate();
		PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
		this.databaseManager = new DatabaseManager(this);
		
		readSettings();
		initAudiobookBeanFactory();
	}
	
	private void initAudiobookBeanFactory() {
		this.audiobookBeanFactory = new AudiobookBeanFactory(this.audiobookBaseDir, this.databaseManager);
	}

	public void readSettings() {
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		this.audiobookBaseDir = sharedPref.getString("pref_base_dir", getString(R.string.pref_base_dir_default));
		this.settings = new Settings(sharedPref);
		runFilescanner();
	}
	
	private void runFilescanner() {
		FileScanner fileScanner = new FileScanner(this);
		fileScanner.doInBackground();
	}
	
	public void setCurrentAudiobook(String currentAudiobook) {
		if(this.currentAudiobookBean == null || this.currentAudiobookBean.getName().equals(currentAudiobook))
		{
			this.currentAudiobookBean = this.audiobookBeanFactory.getAudiobookBeanForName(currentAudiobook);
		} else {
			Log.d(TAG, currentAudiobook + " already loaded, nothing to do");
		}
	}
	
	public void setCurrentAudiobook(CurrentAudiobook currentAudiobook) {
		this.currentAudiobookBean = currentAudiobook;
	}

	public String getAudiobokkBaseDir() {
		return this.audiobookBaseDir;
	}

	public DatabaseManager getDatabaseManager() {
		return databaseManager;
	}

	public void setDatabaseManager(DatabaseManager databaseManager) {
		this.databaseManager = databaseManager;
	}
	public CurrentAudiobook getCurrentAudiobookBean() {
		return currentAudiobookBean;
	}

	public void setCurrentAudiobookBean(CurrentAudiobook currentAudiobookBean) {
		this.currentAudiobookBean = currentAudiobookBean;
	}

	public Settings getSettings() {
		return settings;
	}

	public void setSettings(Settings settings) {
		this.settings = settings;
	}

	public boolean isPlaybackServiceRunning(){
        final ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        final List<RunningServiceInfo> services = activityManager.getRunningServices(Integer.MAX_VALUE);

        for (RunningServiceInfo runningServiceInfo : services) {
            if (runningServiceInfo.service.getClassName().equals(PlaybackService.class.getName())){
                return true;
            }
        }
        return false;
     }
}
