package com.codepan.database

data class Index(
    val fields: ArrayList<Field>,
    val table: String,
    val name: String,
)