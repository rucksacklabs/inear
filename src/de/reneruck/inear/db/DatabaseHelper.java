package de.reneruck.inear.db;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

	private static final String CREATE_BOOKMARKS = "CREATE  TABLE IF NOT EXISTS `" + DbConfigs.TABLE_BOOKMARKS+ "`" +
			" (`" + DbConfigs.FIELD_BOOKMARK_ID + "` INTEGER  PRIMARY KEY AUTOINCREMENT ," +
			"`" + DbConfigs.FIELD_AUDIOBOOK_NAME + "` TEXT NULL ," +
			"`" + DbConfigs.FIELD_TRACK + "` INTEGER NULL ," +
			"`" + DbConfigs.FIELD_PLAYBACK_POS+ "` INTEGER NULL)";

	public DatabaseHelper(Context context, String name, CursorFactory factory, int version) {
		super(context, DbConfigs.databaseName, factory,  DbConfigs.databaseVersion);
	}

	

	@Override
	public void onCreate(SQLiteDatabase db) {
		try {
			db.execSQL(CREATE_BOOKMARKS);
		} catch (SQLException e) {
			System.err.println(e);
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
		/*
		 * TODO: Here should happen a data migration!!  
		 */
		db.execSQL("DROP TABLE IF EXISTS " + DbConfigs.TABLE_BOOKMARKS + "");
		onCreate(db);
	}
}
