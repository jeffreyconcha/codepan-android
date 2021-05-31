package com.codepan.database

class IndexValue(val name: String, vararg columns: Field) {
    private val _columns = columns

    val fields: ArrayList<Field>
        get() {
            return arrayListOf(*_columns)
        }
}

class TableIndices(val table: String, vararg indices: IndexValue) {

    private val _indices = indices

    fun create(db: SQLiteAdapter) {
        val binder = SQLiteBinder(db);
        val query = SQLiteQuery();
        for (index in _indices) {
            query.clearAll()
            for (field in index.fields) {
                query.add(field)
            }
            binder.createIndex(index.name, table, query)
        }
        binder.finish();
    }
}