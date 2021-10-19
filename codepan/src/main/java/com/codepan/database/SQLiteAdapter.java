package com.codepan.database;

import android.content.Context;
import android.util.Log;

import com.codepan.database.Callback.OnCreateDatabaseCallback;
import com.codepan.database.Callback.OnUpgradeDatabaseCallback;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteDatabase.CursorFactory;
import net.sqlcipher.database.SQLiteDatabaseHook;
import net.sqlcipher.database.SQLiteOpenHelper;
import net.sqlcipher.database.SQLiteStatement;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class SQLiteAdapter implements SQLiteDatabaseHook {

	private OnUpgradeDatabaseCallback upgradeDatabaseCallback;
	private OnCreateDatabaseCallback createDatabaseCallback;
	private SQLiteDatabase sqLiteDatabase;
	private final String TAG = "DB-Error";
	private String temp = "temp";
	private String database;
	private String password;
	private Context context;
	private int version;
	private String old;

	public SQLiteAdapter(Context context, String database, String password, String old, int version) {
		this.context = context;
		this.database = database;
		this.password = password;
		this.version = version;
		this.temp += database;
		this.old = old;
		this.init();
	}

	private void init() {
		SQLiteDatabase.loadLibs(context);
		File databaseFile = context.getDatabasePath(database);
		File dir = databaseFile.getParentFile();
		if(!dir.exists()) {
			dir.mkdir();
		}
		try {
			sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(
				databaseFile, password, null, this);
		}
		catch(Exception e) {
			Log.e(TAG, e.getMessage());
			sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(databaseFile, old, null);
			sqLiteDatabase.changePassword(password);
		}
		sqLiteDatabase.close();
	}

	public SQLiteAdapter openConnection() throws android.database.SQLException {
		if(!sqLiteDatabase.isOpen()) {
			SQLiteHelper helper = new SQLiteHelper(context, database, null, version);
			sqLiteDatabase = helper.getWritableDatabase(password);
		}
		return this;
	}

	public SQLiteAdapter getInstance(SQLiteDatabase sqLiteDatabase) {
		this.sqLiteDatabase = sqLiteDatabase;
		return this;
	}

	public void close() {
		sqLiteDatabase.close();
	}

	public int getVersion() {
		return sqLiteDatabase.getVersion();
	}

	public void setVersion(int version) {
		sqLiteDatabase.setVersion(version);
	}

	public boolean execQuery(String query) {
		try {
			sqLiteDatabase.execSQL(query);
			return true;
		}
		catch(Exception e) {
			Log.e(TAG, e.getMessage() + "\r sql:" + query);
			return false;
		}
	}

	public Cursor read(String query) {
		return sqLiteDatabase.rawQuery(query, null);
	}

	public String getString(String query) {
		String value = null;
		Cursor cursor = sqLiteDatabase.rawQuery(query, null);
		while(cursor.moveToNext()) {
			value = cursor.getString(0);
		}
		cursor.close();
		return value;
	}

	public String getString(String query, int index) {
		String value = null;
		Cursor cursor = sqLiteDatabase.rawQuery(query, null);
		while(cursor.moveToNext()) {
			value = cursor.getString(index);
		}
		cursor.close();
		return value;
	}

	public HashMap<String, String> getMap(String query) {
		HashMap<String, String> map = new HashMap<>();
		Cursor cursor = sqLiteDatabase.rawQuery(query, null);
		while(cursor.moveToNext()) {
			for(int i = 0; i < cursor.getColumnCount(); i++) {
				String key = cursor.getColumnName(i);
				String value = cursor.getString(i);
				map.put(key, value);
			}
		}
		cursor.close();
		return map;
	}

	public int getInt(String query) {
		int value = 0;
		Cursor cursor = sqLiteDatabase.rawQuery(query, null);
		while(cursor.moveToNext()) {
			value = cursor.getInt(0);
		}
		cursor.close();
		return value;
	}

	public int getInt(String query, int index) {
		int value = 0;
		Cursor cursor = sqLiteDatabase.rawQuery(query, null);
		while(cursor.moveToNext()) {
			value = cursor.getInt(index);
		}
		cursor.close();
		return value;
	}

	public float getFloat(String query) {
		float value = 0F;
		Cursor cursor = sqLiteDatabase.rawQuery(query, null);
		while(cursor.moveToNext()) {
			value = cursor.getFloat(0);
		}
		cursor.close();
		return value;
	}

	public float getFloat(String query, int index) {
		float value = 0F;
		Cursor cursor = sqLiteDatabase.rawQuery(query, null);
		while(cursor.moveToNext()) {
			value = cursor.getFloat(index);
		}
		cursor.close();
		return value;
	}

	public long getLong(String query) {
		long value = 0L;
		Cursor cursor = sqLiteDatabase.rawQuery(query, null);
		while(cursor.moveToNext()) {
			value = cursor.getLong(0);
		}
		cursor.close();
		return value;
	}

	public long getLong(String query, int index) {
		long value = 0L;
		Cursor cursor = sqLiteDatabase.rawQuery(query, null);
		while(cursor.moveToNext()) {
			value = cursor.getLong(index);
		}
		cursor.close();
		return value;
	}

	public double getDouble(String query) {
		double value = 0D;
		Cursor cursor = sqLiteDatabase.rawQuery(query, null);
		while(cursor.moveToNext()) {
			value = cursor.getDouble(0);
		}
		cursor.close();
		return value;
	}

	public double getDouble(String query, int index) {
		double value = 0D;
		Cursor cursor = sqLiteDatabase.rawQuery(query, null);
		while(cursor.moveToNext()) {
			value = cursor.getDouble(index);
		}
		cursor.close();
		return value;
	}

	public boolean isRecordExists(String query) {
		boolean result = false;
		Cursor cursor = sqLiteDatabase.rawQuery(query, null);
		if(cursor.getCount() > 0) {
			result = true;
		}
		cursor.close();
		return result;
	}

	public boolean isColumnExists(String table, String column) {
		boolean result = false;
		String query = "PRAGMA table_info(" + table + ")";
		Cursor cursor = read(query);
		while(cursor.moveToNext()) {
			if(column.equals(cursor.getString(1))) {
				result = true;
				break;
			}
		}
		cursor.close();
		return result;
	}

	public int getTableColumnCount(String table) {
		String query = "PRAGMA table_info(" + table + ")";
		Cursor cursor = read(query);
		int value = cursor.getCount();
		cursor.close();
		return value;
	}

	public int getIndexColumnCount(String index) {
		String query = "PRAGMA index_info(" + index + ")";
		Cursor cursor = read(query);
		int value = cursor.getCount();
		cursor.close();
		return value;
	}

	public ArrayList<String> getColumnList(String table) {
		ArrayList<String> columnList = new ArrayList<>();
		String query = "PRAGMA table_info(" + table + ")";
		Cursor cursor = read(query);
		while(cursor.moveToNext()) {
			String name = cursor.getString(1);
			columnList.add(name);
		}
		cursor.close();
		return columnList;
	}

	public boolean isTableExists(String table) {
		String query = "SELECT name FROM sqlite_master WHERE name = '" + table + "'";
		return isRecordExists(query);
	}

	public int recordCount(String query) {
		int value = 0;
		Cursor cursor = sqLiteDatabase.rawQuery(query, null);
		value = cursor.getCount();
		cursor.close();
		return value;
	}

	public void setTransactionSuccessful() {
		sqLiteDatabase.setTransactionSuccessful();
	}

	public void beginTransaction() {
		sqLiteDatabase.beginTransaction();
	}

	public void endTransaction() {
		sqLiteDatabase.endTransaction();
	}

	public boolean isDatabaseLocked() {
		return sqLiteDatabase.isDbLockedByOtherThreads();
	}

	public SQLiteStatement compileStatement(String sql) {
		return sqLiteDatabase.compileStatement(sql);
	}

	public void decryptDatabase(String fileName) {
		File unencryptedFile = context.getDatabasePath(fileName);
		String path = unencryptedFile.getAbsolutePath();
		unencryptedFile.delete();
		if(sqLiteDatabase.isOpen()) {
			sqLiteDatabase.rawExecSQL(String.format("ATTACH database '%s' as plaintext KEY '';", path));
			sqLiteDatabase.rawExecSQL("SELECT sqlcipher_export('plaintext');");
			sqLiteDatabase.rawExecSQL("DETACH database plaintext;");
			android.database.sqlite.SQLiteDatabase androidSQLiteDB = android.database.sqlite.SQLiteDatabase.openOrCreateDatabase(unencryptedFile, null);
			androidSQLiteDB.close();
		}
	}

	public void encryptDatabase(String fileName) {
		File databaseFile = context.getDatabasePath(fileName);
		if(!databaseFile.exists()) {
			return;
		}
		File tempFile = context.getDatabasePath(temp);
		String path = tempFile.getAbsolutePath();
		tempFile.delete();
		SQLiteDatabase sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(databaseFile, old, null);
		sqLiteDatabase.rawExecSQL(String.format("ATTACH database '%s' AS encrypted KEY '%s'", path, password));
		sqLiteDatabase.rawExecSQL("SELECT sqlcipher_export('encrypted');");
		sqLiteDatabase.rawExecSQL("DETACH database encrypted;");
		sqLiteDatabase.close();
		if(databaseFile.delete()) {
			tempFile.renameTo(databaseFile);
		}
	}

	public boolean deleteDatabase(String fileName) {
		boolean result = false;
		File unencryptedFile = context.getDatabasePath(fileName);
		if(unencryptedFile.exists()) {
			result = unencryptedFile.delete();
		}
		return result;
	}

	@Override
	public void preKey(SQLiteDatabase database) {
	}

	@Override
	public void postKey(SQLiteDatabase database) {
		database.rawExecSQL("PRAGMA key = '" + password + "'");
		database.rawExecSQL("PRAGMA cipher_migrate");
	}

	private class SQLiteHelper extends SQLiteOpenHelper {
		public SQLiteHelper(Context context, String name, CursorFactory factory, int version) {
			super(context, name, factory, version);
		}

		@Override
		public void onCreate(SQLiteDatabase sqLiteDatabase) {
			SQLiteAdapter db = getInstance(sqLiteDatabase);
			if(createDatabaseCallback != null) {
				createDatabaseCallback.onCreateDatabase(db);
			}
		}

		@Override
		public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
			SQLiteAdapter db = getInstance(sqLiteDatabase);
			if(upgradeDatabaseCallback != null) {
				upgradeDatabaseCallback.onUpgradeDatabase(db, oldVersion, newVersion);
			}
		}
	}

	/**
	 * @param createDatabaseCallback must be set before {@link #openConnection()}
	 */
	public void setOnCreateDatabaseCallback(OnCreateDatabaseCallback createDatabaseCallback) {
		this.createDatabaseCallback = createDatabaseCallback;
	}

	/**
	 * @param upgradeDatabaseCallback must be set before {@link #openConnection()}
	 */
	public void setOnUpgradeDatabaseCallback(OnUpgradeDatabaseCallback upgradeDatabaseCallback) {
		this.upgradeDatabaseCallback = upgradeDatabaseCallback;
	}

	public void vacuum() {
		if(!sqLiteDatabase.inTransaction()) {
			this.execQuery("VACUUM");
		}
	}

	public Context getContext() {
		return this.context;
	}

	public String getName() {
		return this.database;
	}
}