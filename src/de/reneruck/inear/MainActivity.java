package de.reneruck.inear;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

public class MainActivity extends Activity {

	private File audioBooksBaseDir;
	private List<String> audioBookTitles;
	private AppContext appContext;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        this.appContext = (AppContext) getApplicationContext();
        this.audioBooksBaseDir = new File(this.appContext.getAudiobokkBaseDir());
        
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

    
}
