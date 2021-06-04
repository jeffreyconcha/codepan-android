package com.codepan.database;

public class Callback {
	public interface OnCreateDatabaseCallback {
		void onCreateDatabase(SQLiteAdapter db);
	}

	public interface OnUpgradeDatabaseCallback {
		void onUpgradeDatabase(SQLiteAdapter db, int oldVersion, int newVersion);
	}
}
