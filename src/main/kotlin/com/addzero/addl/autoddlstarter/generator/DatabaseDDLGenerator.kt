package com.addzero.addl.autoddlstarter.generator

import cn.hutool.core.util.ClassUtil
import com.addzero.addl.autoddlstarter.generator.FieldPredicateUtil.isBigDecimalType
import com.addzero.addl.autoddlstarter.generator.FieldPredicateUtil.isBooleanType
import com.addzero.addl.autoddlstarter.generator.FieldPredicateUtil.isCharType
import com.addzero.addl.autoddlstarter.generator.FieldPredicateUtil.isDateTimeType
import com.addzero.addl.autoddlstarter.generator.FieldPredicateUtil.isDateType
import com.addzero.addl.autoddlstarter.generator.FieldPredicateUtil.isDoubleType
import com.addzero.addl.autoddlstarter.generator.FieldPredicateUtil.isIntType
import com.addzero.addl.autoddlstarter.generator.FieldPredicateUtil.isLongType
import com.addzero.addl.autoddlstarter.generator.FieldPredicateUtil.isStringType
import com.addzero.addl.autoddlstarter.generator.FieldPredicateUtil.isTextType
import com.addzero.addl.autoddlstarter.generator.FieldPredicateUtil.isTimeType
import com.addzero.addl.autoddlstarter.generator.entity.DDLContext
import com.addzero.addl.autoddlstarter.generator.entity.FieldMapping
import com.addzero.addl.autoddlstarter.generator.entity.JavaFieldMetaInfo
import com.addzero.addl.autoddlstarter.generator.ex.DMSQLDDLGenerator
import com.addzero.addl.autoddlstarter.generator.ex.MysqlDDLGenerator
import com.addzero.addl.autoddlstarter.generator.ex.OracleDDLGenerator
import com.addzero.addl.autoddlstarter.generator.ex.PostgreSQLDDLGenerator
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import java.util.*
import kotlin.collections.HashMap

fun main() {
    val qualifiedName = Integer::class.java.name
    val kj = ClassUtil.loadClass<Any>(qualifiedName)
    println(kj)
}

abstract class DatabaseDDLGenerator {
    /**建表语句
     * @param [ddlContext]
     * @return [String]
     */
    abstract fun generateCreateTableDDL(ddlContext: DDLContext): String

    /**
     * 加列语句
     * @param [ddlContext]
     * @return [String]
     */
    abstract fun generateAddColDDL(ddlContext: DDLContext): String

    /**
     * 改列语句
     * @param [ddlContext]
     * @return [String]
     */
    abstract fun printChangeDML(ddlContext: DDLContext): String

    /**
     * 依据mysql类型推导出各种sql类型
     * @param [mysqlType]
     * @return [String]
     */
    abstract fun mapTypeByMysqlType(mysqlType: String): String

    /**
     * 依据java类型推导出各种sql类型
     * @param [javaFieldMetaInfo]
     * @return [String]
     */
    abstract fun mapTypeByJavaType(javaFieldMetaInfo: JavaFieldMetaInfo): String


    fun getLength(javaFieldMetaInfo: JavaFieldMetaInfo): String? {
        return fieldMappings.find { it.predi.test(javaFieldMetaInfo) }?.length
    }



    companion object {
        fun getDatabaseDDLGenerator(dbType: String): DatabaseDDLGenerator {
            return databaseType[dbType]!!
        }

        fun javaType2RefType(javaType: String): String? {
            val javaClass = fieldMappings.find { it.javaClassSimple == javaType }!!.javaClass
            return javaClass
        }
//        var fieldMappings: List<FieldMapping> = listOf(
//            FieldMapping(::isIntType, "int", "integer", "number", "INT", "",Int::class.java,"a"),
//            FieldMapping(::isLongType, "long", "bigint", "number", "BIGINT", ""),
//            FieldMapping(::isStringType, "varchar", "varchar", "varchar2", "VARCHAR", "255"),
//            FieldMapping(::isCharType, "char", "character", "char", "VARCHAR", "255"),
//            FieldMapping(::isTextType, "text", "text", "clob", "CLOB", ""),
//            FieldMapping(::isBooleanType, "boolean", "boolean", "number", "INT", ""),
//            FieldMapping(::isDateType, "date", "date", "date", "TIMESTAMP", ""),
//            FieldMapping(::isTimeType, "time", "time", "timestamp", "TIMESTAMP", ""),
//            FieldMapping(::isDateTimeType, "datetime", "timestamp", "timestamp", "TIMESTAMP", ""),
//            FieldMapping(::isBigDecimalType, "decimal", "numeric", "number", "NUMERIC", "19,2"),
//            FieldMapping(::isDoubleType, "double", "double precision", "binary_double", "DOUBLE", "6,2")
//        )

        var javaTypesEnum: Array<String>
            get() = fieldMappings.map { it.javaClassSimple }.distinct().toTypedArray()
            set(value) = TODO()







        var fieldMappings: List<FieldMapping> = listOf(
            FieldMapping(::isIntType, "int", "integer", "number", "INT", "", Integer::class.java),
            FieldMapping(::isLongType, "long", "bigint", "number", "BIGINT", "", Long::class.java),
            FieldMapping(::isStringType, "varchar", "varchar", "varchar2", "VARCHAR", "255", String::class.java),
            FieldMapping(::isCharType, "char", "character", "char", "VARCHAR", "255", String::class.java),
            FieldMapping(::isTextType, "text", "text", "clob", "CLOB", "", String::class.java),
            FieldMapping(::isBooleanType, "boolean", "boolean", "number", "INT", "", Boolean::class.java),
            FieldMapping(::isDateType, "date", "date", "date", "TIMESTAMP", "", Date::class.java),
            FieldMapping(::isTimeType, "time", "time", "timestamp", "TIMESTAMP", "", LocalTime::class.java),
            FieldMapping(::isDateTimeType, "datetime", "timestamp", "timestamp", "TIMESTAMP", "", LocalDateTime::class.java),
            FieldMapping(::isBigDecimalType, "decimal", "numeric", "number", "NUMERIC", "19,2", java.math.BigDecimal::class.java),
            FieldMapping(::isDoubleType, "double", "double precision", "binary_double", "DOUBLE", "6,2", Double::class.java)
        )
            .onEach { mapping ->
                // 添加计算属性
                mapping.javaClass = mapping.classRef.name
                mapping.javaClassSimple = mapping.classRef.simpleName
            }

        public var databaseType: HashMap<String, DatabaseDDLGenerator> = object : HashMap<String, DatabaseDDLGenerator>() {
            init {
                put(MYSQL, MysqlDDLGenerator())
                put(ORACLE, OracleDDLGenerator())
                put(POSTGRESQL, PostgreSQLDDLGenerator())
                put(DM, DMSQLDDLGenerator())
            }
        }

    }


}