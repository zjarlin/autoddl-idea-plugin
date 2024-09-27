package com.addzero.addl

data class FormDTO(
    val tableName: String?,
    val tableEnglishName: String,
    val dbType: String,
    val dbName: String?,
    var fields: List<FieldDTO>?,
)

data class FieldDTO (
    var javaType: String,
    var fieldName: String?,
    var fieldChineseName: String
)