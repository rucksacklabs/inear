package de.reneruck.inear.db;

import de.reneruck.inear.Bookmark;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

@SuppressLint({ "ParserError", "ParserError" })
public class AsyncStoreBookmark extends AsyncTask<Bookmark, Void, Void> {

	private DatabaseManager databaseManager;
	
	public AsyncStoreBookmark(DatabaseManager databaseManager) {
		this.databaseManager = databaseManager;
	}

	@Override
	public Void doInBackground(Bookmark... params) {
		if(params != null && params.length > 0)
		{
			Bookmark bookmarkToStore = params[0];
			DatabaseHelper dbHelper = this.databaseManager.getDbHelper();
			SQLiteDatabase writableDatabase = dbHelper.getWritableDatabase();
			
			ContentValues values = new ContentValues(3);
			values.put(DbConfigs.FIELD_AUDIOBOOK_NAME, bookmarkToStore.getBookTitle());
			values.put(DbConfigs.FIELD_TRACK, bookmarkToStore.getTrackNumber());
			values.put(DbConfigs.FIELD_PLAYBACK_POS, bookmarkToStore.getPlaybackPosition());
			if(!bookmarkExists(writableDatabase, bookmarkToStore.getBookTitle()))
			{
				long result = writableDatabase.insertWithOnConflict(DbConfigs.TABLE_BOOKMARKS, null, values , SQLiteDatabase.CONFLICT_ROLLBACK);
				
			} else {
				writableDatabase.updateWithOnConflict(DbConfigs.TABLE_BOOKMARKS, values, DbConfigs.FIELD_BOOKMARK_ID + "=" + bookmarkToStore.getId(), null,SQLiteDatabase.CONFLICT_ROLLBACK);
			}
			writableDatabase.close();
		}
		return null;
	}

	private boolean bookmarkExists(SQLiteDatabase db, String bookName) {
		Cursor c = db.query(DbConfigs.TABLE_BOOKMARKS, new String[]{"*"}, DbConfigs.FIELD_AUDIOBOOK_NAME + " like '" + bookName + "'", null, null, null,null);
		boolean result = c.getCount() > 0 ? true : false;
		c.close();
		return result;
	}

}
