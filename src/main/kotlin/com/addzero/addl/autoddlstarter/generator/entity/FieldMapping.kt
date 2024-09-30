package com.addzero.addl.autoddlstarter.generator.entity

import java.util.function.Predicate

/**
 * @author zjarlin
 * @since 2023/11/4 09:24
 */
data class FieldMapping(
    val predi: Predicate<JavaFieldMetaInfo>,
    val mysqlType: String,
    val pgType: String,
    val oracleType: String,
    val dmType: String,
    val length: String,
    val classRef: Class<*>,
){
    var javaClassRef: String=classRef.name
    var javaClassSimple: String=classRef.simpleName
}