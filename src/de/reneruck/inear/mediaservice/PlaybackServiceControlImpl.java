package de.reneruck.inear.mediaservice;

import java.io.FileDescriptor;
import java.util.List;

import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public class PlaybackServiceControlImpl implements PlaybackServiceControl {

	private PlaybackService playbackService;

	public PlaybackServiceControlImpl(PlaybackService playbackService) {
		this.playbackService = playbackService;
	}

	@Override
	public void dump(FileDescriptor fd, String[] args) throws RemoteException {
		// TODO Auto-generated method stub

	}

	@Override
	public void dumpAsync(FileDescriptor fd, String[] args)
			throws RemoteException {
		// TODO Auto-generated method stub

	}

	@Override
	public String getInterfaceDescriptor() throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isBinderAlive() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void linkToDeath(DeathRecipient recipient, int flags)
			throws RemoteException {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean pingBinder() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public IInterface queryLocalInterface(String descriptor) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean transact(int code, Parcel data, Parcel reply, int flags)
			throws RemoteException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean unlinkToDeath(DeathRecipient recipient, int flags) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void startPlayback() {
		handlePlayPause();
	}

	@Override
	public void pausePlayback() {
		handlePlayPause();
	}

	private void handlePlayPause() {
		if(this.playbackService.isPlaying())
		{
			this.playbackService.pausePlayback();
		} else {
			this.playbackService.resumePlayback();
		}
	}

	@Override
	public void nextTrack() {
		// TODO Auto-generated method stub

	}

	@Override
	public void prevTrack() {
		// TODO Auto-generated method stub

	}

	@Override
	public int getCurrentPlaybackPosition() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getCurrentTrack() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<String> getCurrentPlaylist() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void loadCurrentAudiobook() {
		this.playbackService.loadCurrentAudiobook();
	}

	@Override
	public void setTrack(int trackNr) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setPlaybackPosisition(int position) {
		this.playbackService.setPlaybackPosotion(position);
	}

	@Override
	public boolean isPlaying() {
		return this.playbackService.isPlaying();
	}

	@Override
	public void resumePlayback() {
		this.playbackService.resumePlayback();
	}

	@Override
	public int getDuration() {
		return 0;
	}

}
