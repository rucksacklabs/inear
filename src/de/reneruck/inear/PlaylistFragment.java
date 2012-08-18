package de.reneruck.inear;

import java.util.List;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class PlaylistFragment extends Fragment {
	
	private AppContext appContext;
	private List<String> currentPlaylist;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.appContext = (AppContext) getActivity().getApplicationContext();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View inflated = inflater.inflate(R.layout.fragment_playlist, container);
		ListView playlistView = (ListView) inflated.findViewById(R.id.playlist);
		this.currentPlaylist = this.appContext.getCurrentPlaylist();
		ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(this.appContext, android.R.layout.simple_list_item_1, currentPlaylist);
		playlistView.setAdapter(listAdapter);
		return inflated;
	}
}
