package de.reneruck.inear.file;

import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import android.os.AsyncTask;
import de.reneruck.inear.AppContext;

public class FileScanner extends AsyncTask<Void, Void, Void> {

	private AppContext appContext;
	
	public FileScanner(AppContext appContext) {
		super();
		this.appContext = appContext;
	}

	@Override
	public Void doInBackground(Void... params) {
		File baseDir = new File(this.appContext.getAudiobokkBaseDir());
		if(baseDir != null && baseDir.exists())
		{
			List<File >audiobookDirs = Arrays.asList(baseDir.listFiles());
			if(audiobookDirs.size() > 0)
			{
				for (File audiobookDir : audiobookDirs) {
					if(!hasPlaylist(audiobookDir))
					{
						createCompletePlaylist(audiobookDir);
					}
				}
			}
		}
		return null;
	}

	private void createCompletePlaylist(File audiobookDir) {
		File[] listFiles = audiobookDir.listFiles(dirFilter);
		List<String> mediaFiles = new LinkedList<String>();
		mediaFiles.addAll(getMediafiles(audiobookDir));
		if(this.appContext.getSettings().isCreateNoMediaFile())createNoMediaFile(audiobookDir);
		
		for (File dir : listFiles) {
			mediaFiles.addAll(getMediafiles(dir));
			if(this.appContext.getSettings().isCreateNoMediaFile())createNoMediaFile(dir);
		}
		createAndWritePlaylist(audiobookDir, mediaFiles);
	}

	private void createNoMediaFile(File dir) {
		File noMediaFile = new File(dir.getAbsolutePath() + File.separator + ".nomedia");
		if(noMediaFile == null | !noMediaFile.exists()){
			try {
				noMediaFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void createAndWritePlaylist(File audiobookDir, List<String> mediaFiles) {
		File playlist = new File(audiobookDir.getAbsolutePath() + File.separator + audiobookDir.getName() + ".m3u");
		try {
			playlist.createNewFile();
			FileOutputStream outputStream = new FileOutputStream(playlist);
			for (String mediafile : mediaFiles) {
				writeEntryToPlaylist(outputStream, mediafile);
			}
			outputStream.flush();
			outputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	private void writeEntryToPlaylist(FileOutputStream outputStream, String currentFile) throws IOException {
		String entryText = currentFile + "\r\n";
		outputStream.write(entryText.getBytes(Charset.forName("UTF-8")));
	}
	
	private List<String> getMediafiles(File dir) {
		File[] medFiles = dir.listFiles(mediaFileFilter);
		List<String> result = new LinkedList<String>();
		for (int i = 0; i < medFiles.length; i++) {
			result.add(medFiles[i].getAbsolutePath());
		}
		return result;
	}

	private boolean hasPlaylist(File audiobookDir) {
		String[] list = audiobookDir.list(m3uFilter);
		return list != null && list.length > 0;
	}
	
	private static FilenameFilter m3uFilter = new FilenameFilter() {
		
		@Override
		public boolean accept(File dir, String filename) {
			return filename.endsWith(".m3u");
		}
	};
	
	private static FileFilter dirFilter = new FileFilter() {
		
		@Override
		public boolean accept(File file) {
			return file.isDirectory();
		}
	};
	
	private static FilenameFilter mediaFileFilter = new FilenameFilter() {
		
		@Override
		public boolean accept(File dir, String filename) {
			return isMediaFile(filename);
		}
	};
	
	private static boolean isMediaFile(String filename) {
		String[] suffixe = new String[] {".mp3",".ogg",".wav", ".mp4" ,".aac",".m4a", ".imy",".ota",".rtttl",".rtx",".mid",".xmf",".mxmf",".flac",".3gp"};
		for (int i = 0; i < suffixe.length; i++) {
			String lowerCase = filename.toLowerCase();
			String string = suffixe[i];
			boolean endsWith = lowerCase.endsWith(string);
			if(endsWith)
			{
				return true;
			}
		}
		return false;
	}
}
