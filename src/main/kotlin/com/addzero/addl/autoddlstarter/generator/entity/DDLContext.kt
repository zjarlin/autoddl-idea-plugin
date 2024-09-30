package com.addzero.addl.autoddlstarter.generator.entity

data class DDLContext(
    val tableChineseName: String,
    var tableEnglishName: String,
    val databaseType: String,
    val databaseName: String = "",
    val dto: List<DDlRangeContext>,
)


data class DDlRangeContext(
    var colName: String,
    val colType: String,
    val colComment: String,
    val colLength: String,
    val isPrimaryKey: String,
    val isSelfIncreasing: String,
//    val autoIncrement: String,
)

data class DDLRangeContextUserInput(
    val javaType: String,
    val colName: String,
    val colComment: String,
)