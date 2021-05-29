package com.codepan.database;

import com.codepan.database.SQLiteQuery.Constraint;
import com.codepan.database.SQLiteQuery.DataType;

public class Field {

	public boolean hasDataType, hasConstraint;
	public String field, defText;
	public Constraint constraint;
	public DataType type;
	public Table table;
	public int defInt;

	public Field(String field) {
		this.field = field;
	}

	public Field(String field, Table table) {
		this.field = table.as + "." + field;
		this.table = table;
	}

	public Field(String field, DataType type) {
		this.hasDataType = true;
		this.field = field;
		this.type = type;
	}

	public Field(String field, String defText) {
		this.hasDataType = true;
		this.hasConstraint = true;
		this.constraint = Constraint.DEFAULT;
		this.type = DataType.TEXT;
		this.defText = defText;
		this.field = field;
	}

	public Field(String field, int defInt) {
		this.hasDataType = true;
		this.hasConstraint = true;
		this.constraint = Constraint.DEFAULT;
		this.field = field;
		this.type = DataType.INTEGER;
		this.defInt = defInt;
	}

	public Field(String field, Constraint constraint) {
		this.hasConstraint = true;
		this.constraint = constraint;
		this.type = DataType.INTEGER;
		this.hasDataType = true;
		this.field = field;
	}

	public Field foreignKey(Table table) {
		this.hasConstraint = true;
		this.hasDataType = true;
		this.type = DataType.INTEGER;
		this.constraint = Constraint.FOREIGN_KEY;
		this.table = table;
		return this;
	}

	public String getDataType() {
		String dataType = null;
		switch(type) {
			case INTEGER:
				dataType = "INTEGER";
				break;
			case TEXT:
				dataType = "TEXT";
				break;
		}
		return dataType;
	}

	public String getDefaultValue() {
		String defValue = null;
		switch(type) {
			case INTEGER:
				defValue = String.valueOf(defInt);
				break;
			case TEXT:
				defValue = defText;
				break;
		}
		return defValue;
	}
}
