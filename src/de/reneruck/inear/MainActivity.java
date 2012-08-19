package de.reneruck.inear;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import de.reneruck.inear.settings.SettingsActivity;

public class MainActivity extends Activity {

	private static final String LAST_PLAYED = "lastPlayed";
	private File audioBooksBaseDir;
	private List<String> audioBookTitles;
	private AppContext appContext;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.appContext = (AppContext) getApplicationContext();
    }

    @Override
    protected void onResume() {
    	super.onResume();
    	this.appContext.readSettings();
    	this.audioBooksBaseDir = new File(this.appContext.getAudiobokkBaseDir());
    	
    	if(this.audioBooksBaseDir != null && this.audioBooksBaseDir.exists())
    	{
    		getAllAudiobooks();
    		initializeAndshowLayout();
    	} else {
    		showNoEntriesFoundScreen();
    	}
    }

	private void showNoEntriesFoundScreen() {
		setContentView(R.layout.activity_main_no_entries);
		((TextView)findViewById(R.id.no_entries_path)).setText(this.appContext.getAudiobokkBaseDir());
	}

	private void initializeAndshowLayout() {
		setContentView(R.layout.activity_main);
		ListView audiobooksList = (ListView) findViewById(R.id.audiobooklist);
		ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, this.audioBookTitles);
		audiobooksList.setAdapter(listAdapter);
		audiobooksList.setOnItemClickListener(this.audiobookItemClickListener);
		audiobooksList.invalidate();
	}
    
	private OnItemClickListener audiobookItemClickListener = new OnItemClickListener() {
    	@Override
    	public void onItemClick(AdapterView<?> arg0, View view, int pos, long id) {
    		appContext.setCurrentAudiobook(audioBookTitles.get(pos));
    		Intent i = new Intent(getApplicationContext(), PlayActivity.class);
    		startActivity(i);
    	}
	};
	
    private void getAllAudiobooks() {
    	this.audioBookTitles = Arrays.asList(this.audioBooksBaseDir.list());
	}

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
        case android.R.id.home:
        	break;
        case R.id.menu_settings:
        	Intent i = new Intent(getApplicationContext(), SettingsActivity.class);
        	startActivity(i);
        	break;
		}
		return true;
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		SharedPreferences preferences = getPreferences(Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putString(LAST_PLAYED, this.appContext.getCurrentAudiobookBean().getName());
		editor.commit();
	}
    
}
