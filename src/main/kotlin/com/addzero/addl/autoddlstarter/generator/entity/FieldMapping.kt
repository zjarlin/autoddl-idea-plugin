package com.addzero.addl.autoddlstarter.generator.entity

import java.util.function.Predicate


/**
 * @author zjarlin
 * @since 2023/11/4 09:24
 */
data class FieldMapping(
//    val clazz: Array<Class<out Any>>,

    val predi: Predicate<JavaFieldMetaInfo>,
    val mysqlType: String,
    val pgType: String,
    val oracleType: String,
    val dmType: String,
    val length: String,
    val classRef: Class<*>,
    var javaClass: String="",
    var javaClassSimple: String="",
)