package de.reneruck.inear.db;

import de.reneruck.inear.Bookmark;
import de.reneruck.inear.DbConfigs;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

public class AsyncGetBookmark extends AsyncTask<String, Void, Bookmark> {

	private DatabaseManager databaseManager;
	
	public AsyncGetBookmark(DatabaseManager databaseManager) {
		this.databaseManager = databaseManager;
	}

	@Override
	public Bookmark doInBackground(String... params) {
		Bookmark bookmark = null;
		if(params != null && params.length > 0)
		{
			String bookTitle = params[0];
			DatabaseHelper dbHelper = this.databaseManager.getDbHelper();
			SQLiteDatabase readableDatabase = dbHelper.getReadableDatabase();
			
			Cursor query = readableDatabase.query(DbConfigs.TABLE_BOOKMARKS, new String[]{"*"}, DbConfigs.FIELD_AUDIOBOOK_NAME + " = '" + bookTitle + "'", null, null, null, null);
			if (query.getCount() > 0) {
				query.moveToFirst();
				
				int id = query.getInt(query.getColumnIndex(DbConfigs.FIELD_BOOKMARK_ID));
				int trackNr = query.getInt(query.getColumnIndex(DbConfigs.FIELD_TRACK));
				int playbackPos = query.getInt(query.getColumnIndex(DbConfigs.FIELD_PLAYBACK_POS));
				
				bookmark = new Bookmark(bookTitle, trackNr, playbackPos);
				bookmark.setId(id);
			}
			query.close();
		}
		return bookmark;
	}

}
