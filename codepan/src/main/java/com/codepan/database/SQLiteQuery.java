package com.codepan.database;

import com.codepan.database.Condition.Operator;

import java.util.ArrayList;

public class SQLiteQuery {

	private ArrayList<FieldValue> fieldValueList;
	private ArrayList<Condition> conditionList;
	private ArrayList<Table> tableList;
	private ArrayList<Field> fieldList;
	private ArrayList<Field> orderList;
	private ArrayList<Field> groupList;
	private boolean ascending = true;
	private boolean ignoreCase = false;
	private int limit;

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public enum Constraint {
		PRIMARY_KEY,
		FOREIGN_KEY,
		DEFAULT,
		UNIQUE
	}

	public enum DataType {
		INTEGER,
		TEXT
	}

	public void setTableList(ArrayList<Table> tableList) {
		this.tableList = tableList;
	}

	public void setFieldList(ArrayList<Field> fieldList) {
		this.fieldList = fieldList;
	}

	public void setFieldValueList(ArrayList<FieldValue> fieldValueList) {
		this.fieldValueList = fieldValueList;
	}

	public void setConditionList(ArrayList<Condition> conditionList) {
		this.conditionList = conditionList;
	}

	public void setOrderList(ArrayList<Field> orderList) {
		this.orderList = orderList;
	}

	public void setGroupList(ArrayList<Field> groupList) {
		this.groupList = groupList;
	}

	public void add(Table table) {
		if(tableList == null) {
			tableList = new ArrayList<>();
		}
		tableList.add(table);
	}

	public void add(Field field) {
		if(fieldList == null) {
			fieldList = new ArrayList<>();
		}
		fieldList.add(field);
	}

	public void add(FieldValue fieldValue) {
		if(fieldValueList == null) {
			fieldValueList = new ArrayList<>();
		}
		fieldValueList.add(fieldValue);
	}

	public void add(Condition condition) {
		if(conditionList == null) {
			conditionList = new ArrayList<>();
		}
		conditionList.add(condition);
	}

	public void order(Field field) {
		if(orderList == null) {
			orderList = new ArrayList<>();
		}
		orderList.add(field);
	}

	public void group(Field field) {
		if(groupList == null) {
			groupList = new ArrayList<>();
		}
		groupList.add(field);
	}

	public void removeTable(int index) {
		if(tableList != null) {
			tableList.remove(index);
		}
	}

	public void removeField(int index) {
		if(fieldList != null) {
			fieldList.remove(index);
		}
	}

	public void removeFieldValue(int index) {
		if(fieldValueList != null) {
			fieldValueList.remove(index);
		}
	}

	public void removeCondition(int index) {
		if(conditionList != null) {
			conditionList.remove(index);
		}
	}

	public void removeOrder(int index) {
		if(orderList != null) {
			orderList.remove(index);
		}
	}

	public void removeGroup(int index) {
		if(groupList != null) {
			groupList.remove(index);
		}
	}

	public void clearTableList() {
		if(tableList != null) {
			tableList.clear();
		}
	}

	public void clearFieldList() {
		if(fieldList != null) {
			fieldList.clear();
		}
	}

	public void clearFieldValueList() {
		if(fieldValueList != null) {
			fieldValueList.clear();
		}
	}

	public void clearConditionList() {
		if(conditionList != null) {
			conditionList.clear();
		}
	}

	public void clearOrderList() {
		if(orderList != null) {
			orderList.clear();
		}
	}

	public void clearGroupList() {
		if(groupList != null) {
			groupList.clear();
		}
	}

	public void clearAll() {
		clearTableList();
		clearFieldList();
		clearOrderList();
		clearGroupList();
		clearFieldValueList();
		clearConditionList();
	}

	public boolean hasTables() {
		return tableList != null && !tableList.isEmpty();
	}

	public boolean hasFields() {
		return fieldList != null && !fieldList.isEmpty();
	}

	public boolean hasFieldValues() {
		return fieldValueList != null && !fieldValueList.isEmpty();
	}

	public boolean hasConditions() {
		return conditionList != null && !conditionList.isEmpty();
	}

	public boolean hasOrders() {
		return orderList != null && !orderList.isEmpty();
	}

	public boolean hasGroups() {
		return groupList != null && !groupList.isEmpty();
	}

	public void ascending(boolean ascending) {
		this.ascending = ascending;
	}

	public void ignoreCase(boolean ignoreCase) {
		this.ignoreCase = ignoreCase;
	}

	private String createFields() {
		String fields = "";
		if(fieldList != null) {
			for(Field field : fieldList) {
				if(field.hasDataType) {
					String dataType = field.getDataType();
					String name = field.field;
					fields += name + " " + dataType;
					if(field.hasConstraint) {
						switch(field.constraint) {
							case PRIMARY_KEY:
								fields += " PRIMARY KEY AUTOINCREMENT NOT NULL";
								break;
							case FOREIGN_KEY:
								Table table = field.table;
								fields += " REFERENCES " + table.name + "(" + Convention.id + ")";
								break;
							case UNIQUE:
								fields += " UNIQUE";
								break;
							case DEFAULT:
								String defValue = field.getDefaultValue();
								fields += " DEFAULT " + defValue;
								break;
						}
					}
				}
				else {
					fields += field.field;
				}
				if(fieldList.indexOf(field) < fieldList.size() - 1) {
					fields += ", ";
				}
			}
		}
		return fields;
	}

	public String getTables() {
		String tables = "";
		if(hasTables()) {
			tables = getTables(tableList);
		}
		return tables;
	}

	public ArrayList<Table> getTableList() {
		return this.tableList;
	}

	public String getFields() {
		String fields = "";
		if(hasFields()) {
			fields = getFields(fieldList);
		}
		return fields;
	}

	public ArrayList<Field> getFieldList() {
		return this.fieldList;
	}

	public String getFieldsValues() {
		String fieldsValues = "";
		if(hasFieldValues()) {
			fieldsValues = getFieldsValues(fieldValueList);
		}
		return fieldsValues;
	}

	public ArrayList<FieldValue> getFieldValueList() {
		return this.fieldValueList;
	}

	public String getConditions() {
		String conditions = "";
		if(hasConditions()) {
			conditions = getConditions(conditionList);
		}
		return conditions;
	}

	public ArrayList<Condition> getConditionList() {
		return this.conditionList;
	}

	private String getTables(ArrayList<Table> tableList) {
		String tables = "";
		if(tableList != null) {
			for(Table obj : tableList) {
				if(!obj.hasJoin()) {
					if(tableList.indexOf(obj) > 0) {
						tables += ", ";
					}
					tables += obj.table;
				}
				else {
					tables += " " + obj.table + getConditions(obj.conditionList);
				}
			}
		}
		return tables;
	}

	private String getOrders() {
		String orders = "";
		if(hasOrders()) {
			String collate = ignoreCase ? "COLLATE NOCASE " : "";
			String direction = ascending ? "ASC" : "DESC";
			orders = " ORDER BY " + getFields(orderList) + " " + collate + direction;
		}
		return orders;
	}

	private String getGroups() {
		String groups = "";
		if(hasGroups()) {
			groups = " GROUP BY " + getFields(groupList);
		}
		return groups;
	}

	private String getLimit() {
		String limitBy = "";
		if(limit != 0) {
			limitBy = " LIMIT " + limit;
		}
		return limitBy;
	}

	private String getFields(ArrayList<Field> fieldList) {
		String fields = "";
		if(fieldList != null) {
			for(Field obj : fieldList) {
				fields += obj.field;
				if(fieldList.indexOf(obj) < fieldList.size() - 1) {
					fields += ", ";
				}
			}
		}
		return fields;
	}

	private String getFieldsValues(ArrayList<FieldValue> fieldValueList) {
		String fieldsValues = "";
		if(hasFieldValues()) {
			for(FieldValue obj : fieldValueList) {
				if(obj.value != null) {
					fieldsValues += obj.field + " = " + obj.value;
				}
				else {
					fieldsValues += obj.field + " = NULL";
				}
				if(fieldValueList.indexOf(obj) < fieldValueList.size() - 1) {
					fieldsValues += ", ";
				}
			}
		}
		return fieldsValues;
	}

	private String getConditions(ArrayList<Condition> conditionList) {
		String condition = "";
		if(hasConditions()) {
			for(Condition obj : conditionList) {
				Operator operator = obj.operator;
				if(operator != null) {
					switch(operator) {
						case EQUALS:
							condition += obj.field + " = " + obj.value;
							break;
						case NOT_EQUALS:
							condition += obj.field + " != " + obj.value;
							break;
						case GREATER_THAN:
							condition += obj.field + " > " + obj.value;
							break;
						case LESS_THAN:
							condition += obj.field + " < " + obj.value;
							break;
						case GREATER_THAN_OR_EQUALS:
							condition += obj.field + " >= " + obj.value;
							break;
						case LESS_THAN_OR_EQUALS:
							condition += obj.field + " <= " + obj.value;
							break;
						case BETWEEN:
							condition += obj.field + " BETWEEN " + obj.start + " AND " + obj.end;
							break;
						case IS_NULL:
							condition += obj.field + " IS NULL";
							break;
						case NOT_NULL:
							condition += obj.field + " NOT NULL";
							break;
						case IS_EMPTY:
							condition += obj.field + " = ''";
							break;
						case NOT_EMPTY:
							condition += obj.field + " != ''";
							break;
						case LIKE:
							condition += obj.field + " LIKE " + obj.value;
							break;
					}
				}
				else {
					Condition[] ors = obj.ors;
					int length = ors.length;
					if(length > 0) {
						String c = "";
						for(int i = 0; i < length; i++) {
							Condition or = ors[i];
							c += or.field + " = " + or.value;
							if(i < length - 1) {
								c += " OR ";
							}
						}
						condition += "(" + c + ")";
					}
				}
				if(conditionList.indexOf(obj) < conditionList.size() - 1) {
					condition += " AND ";
				}
			}
		}
		return condition;
	}

	public String insert(String table) {
		String fields = "";
		String values = "";
		if(hasFieldValues()) {
			for(FieldValue obj : fieldValueList) {
				fields += obj.field;
				values += obj.value;
				if(fieldValueList.indexOf(obj) < fieldValueList.size() - 1) {
					fields += ", ";
					values += ", ";
				}
			}
		}
		return "INSERT INTO " + table + " (" + fields + ") VALUES (" + values + ")";
	}

	public String optInsert(String table) {
		String fields = "";
		String values = "";
		if(hasFieldValues()) {
			for(FieldValue obj : fieldValueList) {
				fields += obj.field;
				values += obj.value;
				if(fieldValueList.indexOf(obj) < fieldValueList.size() - 1) {
					fields += ", ";
					values += ", ";
				}
			}
		}
		return "INSERT OR REPLACE INTO " + table + " (" + fields + ") VALUES (" + values + ")";
	}

	public String update(String table, String recID) {
		return "UPDATE " + table + " SET " + getFieldsValues() + " WHERE ID = '" + recID + "'";
	}

	public String update(String table, int recID) {
		return "UPDATE " + table + " SET " + getFieldsValues() + " WHERE ID = '" + recID + "'";
	}

	public String update(String table) {
		String condition = "";
		if(conditionList != null && !conditionList.isEmpty()) {
			condition = " WHERE " + getConditions();
		}
		return "UPDATE " + table + " SET " + getFieldsValues() + condition;
	}

	public String delete(String table) {
		String condition = "";
		if(conditionList != null && !conditionList.isEmpty()) {
			condition = " WHERE " + getConditions();
		}
		return "DELETE FROM " + table + condition;
	}

	public String createTable(String table) {
		return "CREATE TABLE IF NOT EXISTS " + table + " (" + createFields() + ")";
	}

	public String createIndex(String idx, String table) {
		return "CREATE INDEX IF NOT EXISTS " + idx + " ON " + table + " (" + getFields() + ")";
	}

	public String vacuum(String database, String table) {
		return "VACUUM '" + database + "." + table + "'";
	}

	public String dropIndex(String idx) {
		return "DROP INDEX IF EXISTS " + idx;
	}

	public String select(String table) {
		String conditions = "";
		if(hasConditions()) {
			conditions = " WHERE " + getConditions();
		}
		return "SELECT " + getFields() + " FROM " + table + conditions
				+ getGroups() + getOrders() + getLimit();
	}

	public String select() {
		String conditions = "";
		if(hasConditions()) {
			conditions = " WHERE " + getConditions();
		}
		return "SELECT " + getFields() + " FROM " + getTables() + conditions
				+ getGroups() + getOrders() + getLimit();
	}

	public String selectCount(Field field) {
		String conditions = "";
		if(hasConditions()) {
			conditions = " WHERE " + getConditions();
		}
		return "SELECT COUNT(" + field.field + ") FROM " + getTables() + conditions
				+ getGroups() + getOrders();
	}

	public String addColumn(String table, Field field) {
		if(field != null) {
			if(field.hasDataType) {
				String dataType = field.getDataType();
				String name = field.field;
				String column = name + " " + dataType;
				if(field.hasConstraint) {
					switch(field.constraint) {
						case PRIMARY_KEY:
							column += " PRIMARY KEY AUTOINCREMENT NOT NULL";
							break;
						case FOREIGN_KEY:
							Table reference = field.table;
							column += " REFERENCES " + reference.name + "(" + Convention.id + ")";
							break;
						case UNIQUE:
							column += " UNIQUE";
							break;
						case DEFAULT:
							String defValue = field.getDefaultValue();
							column += " DEFAULT " + defValue;
							break;
					}
				}
				return "ALTER TABLE " + table + " ADD COLUMN " + column;
			}
		}
		return null;
	}


	public String dropTable(String table) {
		return "DROP TABLE IF EXISTS " + table;
	}

	public String renameTable(String oldName, String newName) {
		return "ALTER TABLE " + oldName + " RENAME TO " + newName;
	}

	public String resetTable(String table) {
		return "UPDATE SQLITE_SEQUENCE SET SEQ = 0 WHERE NAME ='" + table + "'";
	}
}
