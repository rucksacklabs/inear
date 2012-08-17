package de.reneruck.inear;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

@SuppressLint({ "ParserError", "ParserError" })
public class AsyncStoreBookmark extends AsyncTask<Bookmark, Void, Void> {

	private DatabaseManager databaseManager;
	
	public AsyncStoreBookmark(DatabaseManager databaseManager) {
		this.databaseManager = databaseManager;
	}

	@Override
	protected Void doInBackground(Bookmark... params) {
		if(params != null && params.length > 1)
		{
			Bookmark bookmarkToStore = params[0];
			DatabaseHelper dbHelper = this.databaseManager.getDbHelper();
			SQLiteDatabase writableDatabase = dbHelper.getWritableDatabase();
			
			ContentValues values = new ContentValues(3);
			values.put(DbConfigs.FIELD_AUDIOBOOK_NAME, bookmarkToStore.getBookTitle());
			values.put(DbConfigs.FIELD_TRACK, bookmarkToStore.getTrackNumber());
			values.put(DbConfigs.FIELD_PLAYBACK_POS, bookmarkToStore.getPlaybackPosition());
			long result = writableDatabase.insertWithOnConflict(DbConfigs.TABLE_BOOKMARKS, null, values , SQLiteDatabase.CONFLICT_ROLLBACK);
			if(result == -1)
			{
				writableDatabase.updateWithOnConflict(DbConfigs.TABLE_BOOKMARKS, values, DbConfigs.FIELD_BOOKMARK_ID + "=" + bookmarkToStore.getId(), null,SQLiteDatabase.CONFLICT_ROLLBACK);
			}
		}
		return null;
	}

}
