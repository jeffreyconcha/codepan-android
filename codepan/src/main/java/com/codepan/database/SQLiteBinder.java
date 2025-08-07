package com.codepan.database;

import net.zetetic.database.sqlcipher.SQLiteStatement;

import java.util.ArrayList;

public class SQLiteBinder {

	private SQLiteAdapter db;

	public SQLiteBinder(SQLiteAdapter db) {
		this.db = db;
		this.db.beginTransaction();
	}

	public String insert(String table, ArrayList<FieldValue> fields) {
		SQLiteQuery query = new SQLiteQuery();
		query.setFieldValueList(fields);
		return insert(table, query);
	}

	public String insert(String table, SQLiteQuery query) {
		String sql = query.insert(table);
		SQLiteStatement insert = db.compileStatement(sql);
		long id = insert.executeInsert();
		insert.close();
		if(id == -1) {
			return null;
		}
		else {
			return String.valueOf(id);
		}
	}

	public String optInsert(String table, SQLiteQuery query) {
		String sql = query.optInsert(table);
		SQLiteStatement insert = db.compileStatement(sql);
		long id = insert.executeInsert();
		insert.close();
		if(id == -1) {
			return null;
		}
		else {
			return String.valueOf(id);
		}
	}

	public int update(String table, ArrayList<FieldValue> fields, String recID) {
		SQLiteQuery query = new SQLiteQuery();
		query.setFieldValueList(fields);
		return update(table, query, recID);
	}

	public int update(String table, ArrayList<FieldValue> fields, int recID) {
		SQLiteQuery query = new SQLiteQuery();
		query.setFieldValueList(fields);
		return update(table, query, recID);
	}

	public int update(String table, ArrayList<FieldValue> fields, ArrayList<Condition> conditions) {
		SQLiteQuery query = new SQLiteQuery();
		query.setFieldValueList(fields);
		query.setConditionList(conditions);
		return update(table, query);
	}

	public int update(String table, SQLiteQuery query) {
		String sql = query.update(table);
		SQLiteStatement update = db.compileStatement(sql);
		int result = update.executeUpdateDelete();
		update.close();
		return result;
	}

	public int update(String table, SQLiteQuery query, String recID) {
		String sql = query.update(table, recID);
		SQLiteStatement update = db.compileStatement(sql);
		int result = update.executeUpdateDelete();
		update.close();
		return result;
	}

	public int update(String table, SQLiteQuery query, int recID) {
		String sql = query.update(table, recID);
		SQLiteStatement update = db.compileStatement(sql);
		int result = update.executeUpdateDelete();
		update.close();
		return result;
	}

	public int delete(String table, ArrayList<Condition> conditions) {
		SQLiteQuery query = new SQLiteQuery();
		query.setConditionList(conditions);
		return delete(table, query);
	}

	public int delete(String table, SQLiteQuery query) {
		String sql = query.delete(table);
		SQLiteStatement delete = db.compileStatement(sql);
		int result = delete.executeUpdateDelete();
		delete.close();
		return result;
	}

	public void addColumn(String table, Field field) {
		SQLiteQuery query = new SQLiteQuery();
		String sql = query.addColumn(table, field);
		SQLiteStatement alter = db.compileStatement(sql);
		alter.execute();
		alter.close();
	}

	public void renameTable(String oldName, String newName) {
		SQLiteQuery query = new SQLiteQuery();
		String sql = query.renameTable(oldName, newName);
		SQLiteStatement alter = db.compileStatement(sql);
		alter.execute();
		alter.close();
	}

	public void dropTable(String table) {
		SQLiteQuery query = new SQLiteQuery();
		String sql = query.dropTable(table);
		SQLiteStatement drop = db.compileStatement(sql);
		drop.execute();
		drop.close();
	}

	public void resetTable(String table) {
		SQLiteQuery query = new SQLiteQuery();
		String sql = query.resetTable(table);
		SQLiteStatement reset = db.compileStatement(sql);
		reset.execute();
		reset.close();
	}

	public void createTable(String table, SQLiteQuery query) {
		String sql = query.createTable(table);
		SQLiteStatement create = db.compileStatement(sql);
		create.execute();
		create.close();
	}

	public void createIndex(String idx, String table, SQLiteQuery query) {
		String sql = query.createIndex(idx, table);
		SQLiteStatement create = db.compileStatement(sql);
		create.execute();
		create.close();
	}

	public void dropIndex(String idx) {
		SQLiteQuery query = new SQLiteQuery();
		String sql = query.dropIndex(idx);
		SQLiteStatement create = db.compileStatement(sql);
		create.execute();
		create.close();
	}

	public void execute(String sql) {
		SQLiteStatement statement = db.compileStatement(sql);
		statement.execute();
		statement.close();
	}

	/**
	 * Since SQLite does not support truncate the ID will not be reset.
	 * Must only be used if you're going to replace ID (primary key) to avoid
	 * incrementation of IDs when doing insert
	 *
	 * @param table - name of table to truncate
	 */
	public void truncate(String table) {
		SQLiteQuery query = new SQLiteQuery();
		String sql = query.delete(table);
		SQLiteStatement statement = db.compileStatement(sql);
		statement.execute();
		statement.close();
	}

	public boolean finish() {
		boolean result = false;
		try {
			db.setTransactionSuccessful();
			result = true;
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		finally {
			db.endTransaction();
		}
		return result;
	}

	public void rollback() {
		db.endTransaction();
	}

	public SQLiteAdapter getDatabase() {
		return this.db;
	}
}
