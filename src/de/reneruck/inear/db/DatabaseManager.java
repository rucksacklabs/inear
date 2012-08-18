package de.reneruck.inear.db;

import android.content.Context;

public class DatabaseManager {

	private DatabaseHelper dbHelper;

	public DatabaseManager(Context context) {
		this.dbHelper = new DatabaseHelper(context, DbConfigs.databaseName, null, DbConfigs.databaseVersion);
	}

	public DatabaseHelper getDbHelper() {
		return dbHelper;
	}

	public void setDbHelper(DatabaseHelper dbHelper) {
		this.dbHelper = dbHelper;
	}
}

