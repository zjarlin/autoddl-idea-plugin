package com.addzero.addl.autoddlstarter.generator.entity


data class DDLContext(
    val simpleClassName: String,
    var tableEnglishName: String,
    val tableChineseName: String,
    val databaseType: String,
    val dto: List<RangeContext>,
    val databaseName: String // 添加到构造函数中
)