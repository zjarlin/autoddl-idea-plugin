package com.addzero.addl.autoddlstarter.generator

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
import com.addzero.addl.autoddlstarter.generator.consts.DM
import com.addzero.addl.autoddlstarter.generator.consts.MYSQL
import com.addzero.addl.autoddlstarter.generator.consts.ORACLE
import com.addzero.addl.autoddlstarter.generator.consts.POSTGRESQL
import com.addzero.addl.autoddlstarter.generator.entity.FieldMapping
import com.addzero.addl.autoddlstarter.generator.entity.JavaFieldMetaInfo
import com.addzero.addl.autoddlstarter.generator.ex.DMSQLDDLGenerator
import com.addzero.addl.autoddlstarter.generator.ex.MysqlDDLGenerator
import com.addzero.addl.autoddlstarter.generator.ex.OracleDDLGenerator
import com.addzero.addl.autoddlstarter.generator.ex.PostgreSQLDDLGenerator
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.*

interface IDatabaseGenerator {
    /**
     * 依据mysql类型推导出各种sql类型
     * @param [mysqlType]
     * @return [String]
     */
    fun mapTypeByMysqlType(mysqlType: String): String

    /**
     * 依据java类型推导出各种sql类型
     * @param [javaFieldMetaInfo]
     * @return [String]
     */
    fun mapTypeByJavaType(javaFieldMetaInfo: JavaFieldMetaInfo): String


    companion object {

        fun getLength(javaFieldMetaInfo: JavaFieldMetaInfo): String {
            return fieldMappings.find { it.predi.test(javaFieldMetaInfo) }?.length!!
        }

        fun getDatabaseDDLGenerator(dbType: String): DatabaseDDLGenerator {
            return databaseType[dbType]!!
        }

        fun javaType2RefType(javaType: String): String {
            val javaClass = fieldMappings.find { it.javaClassSimple == javaType }!!.javaClassRef
            return javaClass
        }

        var javaTypesEnum: Array<String>
            get() = fieldMappings.map { it.javaClassSimple }.distinct().toTypedArray()
            set(value) = TODO()


        var fieldMappings: List<FieldMapping> = listOf(
            FieldMapping(::isStringType, "varchar", "varchar", "varchar2", "VARCHAR", "(255)", String::class.java),
            FieldMapping(::isCharType, "char", "character", "char", "VARCHAR", "(255)", String::class.java),
            FieldMapping(::isTextType, "text", "text", "clob", "CLOB", "", String::class.java),
            FieldMapping(
                ::isDateTimeType, "datetime", "timestamp", "timestamp", "TIMESTAMP", "", LocalDateTime::class.java
            ),
            FieldMapping(::isDateType, "date", "date", "date", "TIMESTAMP", "", Date::class.java),
            FieldMapping(::isTimeType, "time", "time", "timestamp", "TIMESTAMP", "", LocalTime::class.java),
            FieldMapping(::isIntType, "int", "integer", "number", "INT", "", Integer::class.java),
            FieldMapping(
                ::isDoubleType, "double", "double precision", "binary_double", "DOUBLE", "(6,2)", Double::class.java
            ),
            FieldMapping(::isBigDecimalType, "decimal", "numeric", "number", "NUMERIC", "(19,2)", BigDecimal::class.java),
            FieldMapping(::isLongType, "long", "bigint", "number", "BIGINT", "", Long::class.java),
            FieldMapping(::isBooleanType, "boolean", "boolean", "number", "INT", "", Boolean::class.java),
        ).onEach { mapping ->
            // 添加计算属性
            mapping.javaClassRef = mapping.classRef.name
            mapping.javaClassSimple = mapping.classRef.simpleName
        }
        var databaseType: HashMap<String, DatabaseDDLGenerator> = object : HashMap<String, DatabaseDDLGenerator>() {
            init {
                put(MYSQL, MysqlDDLGenerator())
                put(ORACLE, OracleDDLGenerator())
                put(POSTGRESQL, PostgreSQLDDLGenerator())
                put(DM, DMSQLDDLGenerator())
            }
        }

    }
}