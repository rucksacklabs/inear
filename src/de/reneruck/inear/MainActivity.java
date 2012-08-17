package de.reneruck.inear;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import de.reneruck.inear.file.FileScanner;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.Preference;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends Activity {

	private static final String LAST_PLAYED = "lastPlayed";
	private File audioBooksBaseDir;
	private List<String> audioBookTitles;
	private AppContext appContext;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        this.appContext = (AppContext) getApplicationContext();
        this.audioBooksBaseDir = new File(this.appContext.getAudiobokkBaseDir());
        
        initFileScanner();
        
        if(this.audioBooksBaseDir != null && this.audioBooksBaseDir.exists())
        {
        	this.audioBookTitles = getAllAudiobooks();
        	ListView audiobooksList = (ListView) findViewById(R.id.audiobooklist);
        	ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, this.audioBookTitles);
        	audiobooksList.setAdapter(listAdapter);
        	audiobooksList.setOnItemClickListener(this.audiobookItemClickListener);
        	audiobooksList.invalidate();
        }
    }

	private void initFileScanner() {
		FileScanner fileScanner = new FileScanner(this.appContext);
		fileScanner.doInBackground();
	}

	private OnItemClickListener audiobookItemClickListener = new OnItemClickListener() {
    	@Override
    	public void onItemClick(AdapterView<?> arg0, View view, int pos, long id) {
    		appContext.setCurrentAudiobook(audioBookTitles.get(pos));
    		Intent i = new Intent(getApplicationContext(), PlayActivity.class);
    		startActivity(i);
    	}
	};
	
    private List<String> getAllAudiobooks() {
    	return Arrays.asList(this.audioBooksBaseDir.list());
	}

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

	@Override
	protected void onStop() {
		super.onStop();
		SharedPreferences preferences = getPreferences(Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putString(LAST_PLAYED, this.appContext.getCurrentAudiobook());
		editor.commit();
	}
    
}
