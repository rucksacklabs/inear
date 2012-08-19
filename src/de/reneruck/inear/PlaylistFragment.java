package de.reneruck.inear;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class PlaylistFragment extends Fragment implements PropertyChangeListener{
	
	private AppContext appContext;
	private List<String> currentPlaylist;
	private ListView playlistView;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.appContext = (AppContext) getActivity().getApplicationContext();
		this.appContext.getCurrentAudiobookBean().addPropertyChangeListener(this);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View inflated = inflater.inflate(R.layout.fragment_playlist, container);
		this.playlistView = (ListView) inflated.findViewById(R.id.playlist);
		this.currentPlaylist = this.appContext.getCurrentAudiobookBean().getPlaylist();
		PlaylistAdapter listAdapter = new PlaylistAdapter(this.appContext, android.R.layout.simple_list_item_1, currentPlaylist);
		this.playlistView.setAdapter(listAdapter);
		this.playlistView.setOnItemClickListener(this.onPlaylistItemListener);
		return inflated;
	}
	private OnItemClickListener onPlaylistItemListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> adapter, View view, int pos, long id) {
			appContext.getCurrentAudiobookBean().setCurrentTrack(pos);
		}
	};

	@Override
	public void propertyChange(PropertyChangeEvent event) {
		if("track".equals(event.getPropertyName()))
		{
			if(this.playlistView != null)
			{
				this.playlistView.invalidate();
			}
		}
	}
}
