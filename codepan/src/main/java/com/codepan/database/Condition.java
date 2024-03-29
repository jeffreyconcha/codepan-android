package com.codepan.database;

public class Condition {

	private final int TRUE = 1;
	private final int FALSE = 0;

	public enum Operator {
		EQUALS,
		NOT_EQUALS,
		GREATER_THAN,
		LESS_THAN,
		GREATER_THAN_OR_EQUALS,
		LESS_THAN_OR_EQUALS,
		BETWEEN,
		IS_NULL,
		NOT_NULL,
		IS_EMPTY,
		NOT_EMPTY,
		LIKE,
		IN_QUERY,
	}

	public Operator operator = Operator.EQUALS;
	public String field, value, start, end, query;
	public SQLiteQuery subQuery;
	public Condition[] ors;

	public Condition(Condition... ors) {
		this.operator = null;
		this.ors = ors;
	}

	public Condition(FieldValue fieldValue) {
		this.field = fieldValue.field;
		this.value = fieldValue.value;
	}

	public Condition(String field, String value) {
		this.field = field;
		if(value != null) {
			this.value = "'" + value + "'";
		}
		else {
			this.operator = Operator.IS_NULL;
		}
	}

	public Condition(String field, long value) {
		this.value = String.valueOf(value);
		this.field = field;
	}

	public Condition(String field, int value) {
		this.value = String.valueOf(value);
		this.field = field;
	}

	public Condition(String field, double value) {
		this.value = String.valueOf(value);
		this.field = field;
	}

	public Condition(String field, float value) {
		this.value = String.valueOf(value);
		this.field = field;
	}

	public Condition(String field, boolean value) {
		this.field = field;
		if(value) {
			this.value = String.valueOf(TRUE);
		}
		else {
			this.value = String.valueOf(FALSE);
		}
	}

	public Condition(String field, Field value) {
		this.value = value.field;
		this.field = field;
	}

	public Condition(String field, SQLiteQuery subQuery) {
		this.field = field;
		this.operator = Operator.IN_QUERY;
		this.subQuery = subQuery;
	}

	public Condition(String field, String value, Table table) {
		this.field = table.as + "." + field;
		if(value != null) {
			this.value = "'" + value + "'";
		}
		else {
			this.operator = Operator.IS_NULL;
		}
	}

	public Condition(String field, long value, Table table) {
		this.field = table.as + "." + field;
		this.value = String.valueOf(value);
	}

	public Condition(String field, int value, Table table) {
		this.field = table.as + "." + field;
		this.value = String.valueOf(value);
	}

	public Condition(String field, double value, Table table) {
		this.field = table.as + "." + field;
		this.value = String.valueOf(value);
	}

	public Condition(String field, float value, Table table) {
		this.field = table.as + "." + field;
		this.value = String.valueOf(value);
	}

	public Condition(String field, boolean value, Table table) {
		this.field = table.as + "." + field;
		if(value) {
			this.value = String.valueOf(TRUE);
		}
		else {
			this.value = String.valueOf(FALSE);
		}
	}

	public Condition(String field, boolean value, Operator operator, Table table) {
		this.operator = operator;
		this.field = table.as + "." + field;
		if(value) {
			this.value = String.valueOf(TRUE);
		}
		else {
			this.value = String.valueOf(FALSE);
		}
	}

	public Condition(String field, Field value, Table table) {
		this.field = table.as + "." + field;
		this.value = value.field;
	}

	public Condition(FieldValue fieldValue, Operator operator) {
		this.field = fieldValue.field;
		this.value = fieldValue.value;
		this.operator = operator;
	}

	public Condition(String field, String value, Operator operator) {
		this.field = field;
		if(operator.equals(Operator.LIKE)) {
			this.value = "'%" + value + "%'";
			this.operator = operator;
		}
		else {
			if(value != null) {
				this.value = "'" + value + "'";
				this.operator = operator;
			}
			else {
				this.operator = Operator.IS_NULL;
			}
		}
	}

	public Condition(String field, long value, Operator operator) {
		this.value = String.valueOf(value);
		this.operator = operator;
		this.field = field;
	}

	public Condition(String field, int value, Operator operator) {
		this.value = String.valueOf(value);
		this.operator = operator;
		this.field = field;
	}

	public Condition(String field, double value, Operator operator) {
		this.value = String.valueOf(value);
		this.operator = operator;
		this.field = field;
	}

	public Condition(String field, float value, Operator operator) {
		this.value = String.valueOf(value);
		this.operator = operator;
		this.field = field;
	}

	public Condition(String field, Operator operator) {
		this.operator = operator;
		this.field = field;
	}

	public Condition(String field, String start, String end, Operator operator) {
		this.start = "'" + start + "'";
		this.end = "'" + end + "'";
		this.operator = operator;
		this.field = field;
	}

	public Condition(String field, long start, long end, Operator operator) {
		this.start = String.valueOf(start);
		this.end = String.valueOf(end);
		this.operator = operator;
		this.field = field;
	}

	public Condition(String field, int start, int end, Operator operator) {
		this.start = String.valueOf(start);
		this.end = String.valueOf(end);
		this.operator = operator;
		this.field = field;
	}

	public Condition(String field, double start, double end, Operator operator) {
		this.start = String.valueOf(start);
		this.end = String.valueOf(end);
		this.operator = operator;
		this.field = field;
	}

	public Condition(String field, float start, float end, Operator operator) {
		this.start = String.valueOf(start);
		this.end = String.valueOf(end);
		this.operator = operator;
		this.field = field;
	}

	public Condition(String field, Operator operator, Table table) {
		this.field = table.as + "." + field;
		this.operator = operator;
	}

	public Condition(String field, SQLiteQuery subQuery, Table table) {
		this.field = table.as + "." + field;
		this.operator = Operator.IN_QUERY;
		this.subQuery = subQuery;
	}

	public Condition(String field, String start, String end, Operator operator, Table table) {
		this.field = table.as + "." + field;
		this.start = "'" + start + "'";
		this.end = "'" + end + "'";
		this.operator = operator;
	}

	public Condition(String field, long start, long end, Operator operator, Table table) {
		this.field = table.as + "." + field;
		this.start = String.valueOf(start);
		this.end = String.valueOf(end);
		this.operator = operator;
	}

	public Condition(String field, int start, int end, Operator operator, Table table) {
		this.field = table.as + "." + field;
		this.start = String.valueOf(start);
		this.end = String.valueOf(end);
		this.operator = operator;
	}

	public Condition(String field, double start, double end, Operator operator, Table table) {
		this.field = table.as + "." + field;
		this.start = String.valueOf(start);
		this.end = String.valueOf(end);
		this.operator = operator;
	}

	public Condition(String field, float start, float end, Operator operator, Table table) {
		this.field = table.as + "." + field;
		this.start = String.valueOf(start);
		this.end = String.valueOf(end);
		this.operator = operator;
	}

	public Condition(Field field, Operator operator) {
		this.field = field.field;
		this.operator = operator;
	}

	public Condition(Field field, String value) {
		this.field = field.field;
		if(value != null) {
			this.value = "'" + value + "'";
		}
		else {
			this.operator = Operator.IS_NULL;
		}
	}

	public Condition(Field field, long value) {
		this.value = String.valueOf(value);
		this.field = field.field;
	}

	public Condition(Field field, int value) {
		this.value = String.valueOf(value);
		this.field = field.field;
	}

	public Condition(Field field, double value) {
		this.value = String.valueOf(value);
		this.field = field.field;
	}

	public Condition(Field field, float value) {
		this.value = String.valueOf(value);
		this.field = field.field;
	}

	public Condition(Field field, boolean value) {
		this.field = field.field;
		if(value) {
			this.value = String.valueOf(TRUE);
		}
		else {
			this.value = String.valueOf(FALSE);
		}
	}

	public Condition(Field field, SQLiteQuery subQuery) {
		this.field = field.field;
		this.operator = Operator.IN_QUERY;
		this.subQuery = subQuery;
	}

	String complete() {
		switch(operator) {
			case EQUALS:
				return field + " = " + value;
			case NOT_EQUALS:
				return field + " != " + value;
			case GREATER_THAN:
				return field + " > " + value;
			case LESS_THAN:
				return field + " < " + value;
			case GREATER_THAN_OR_EQUALS:
				return field + " >= " + value;
			case LESS_THAN_OR_EQUALS:
				return field + " <= " + value;
			case BETWEEN:
				return field + " BETWEEN " + start + " AND " + end;
			case IS_NULL:
				return field + " IS NULL";
			case NOT_NULL:
				return field + " NOT NULL";
			case IS_EMPTY:
				return field + " = ''";
			case NOT_EMPTY:
				return field + " != ''";
			case LIKE:
				return field + " LIKE " + value;
			case IN_QUERY:
				return field + " IN (" + subQuery.select() + ")";
		}
		return null;
	}
}
