package com.addzero.addl.autoddlstarter.generator.ex

import cn.hutool.core.util.StrUtil
import com.addzero.addl.autoddlstarter.tools.JlStrUtil
import com.addzero.addl.autoddlstarter.generator.DatabaseDDLGenerator
import com.addzero.addl.autoddlstarter.generator.defaultconfig.BaseMetaInfoUtil
import com.addzero.addl.autoddlstarter.generator.entity.DDLContext
import com.addzero.addl.autoddlstarter.generator.entity.JavaFieldMetaInfo
import com.addzero.addl.autoddlstarter.generator.entity.RangeContext
import java.util.function.Consumer
import java.util.function.Function
import java.util.function.Predicate
import java.util.stream.Collectors

class OracleDDLGenerator : DatabaseDDLGenerator() {
    override fun generateCreateTableDDL(ddlContext: DDLContext): String {
        val sheetName: String = ddlContext.simpleClassName
        val tableEnglishName: String = ddlContext.tableEnglishName
        val dto: List<RangeContext> = ddlContext.dto

        val lineSeparator = System.lineSeparator()

        val simpleName = this.javaClass.simpleName
        println("-----------开始生成建表语句,依据:${simpleName}---------------当前读取的对象---$sheetName-----------------------")
        val createTable = "create table $tableEnglishName ($lineSeparator"

        val ddl = StringBuilder(createTable)

        val fieldsDDL = dto.stream()
            .map<String?>(Function<RangeContext, String?> { field: RangeContext ->
                val fieldName: String = field.fieldName
                val fieldType: String = field.fieldType
                val fieldLength: String = field.fieldLength
                val isPrimaryKey: String = field.isPrimaryKey
                val isSelfIncreasing: String = field.isSelfIncreasing
                val fieldComment = if (BaseMetaInfoUtil.isPrimaryKeyBoolean(fieldName)) {
                    "主键"
                } else {
                    field.fieldComment
                }
                val fieldCommentAppend = field.fieldCommentAppend
                val syndicationNotes = fieldComment ?: (fieldCommentAppend + fieldCommentAppend)

                // 根据字段类型映射关系映射为 PostgreSQL DDL
                var pgFieldType = mapTypeByMysqlType(fieldType)

                if (pgFieldType == null) {
                    // 如果映射关系中未定义，使用默认类型
                    pgFieldType = "text"
                }

                var fieldDDL = "\"$fieldName\" $pgFieldType"

                if ("0" != fieldLength) {
                    fieldDDL += "($fieldLength)"
                }

                if ("Y" == isPrimaryKey) {
                    fieldDDL += " primary key"
                }

                if ("Y" == isSelfIncreasing) {
                    fieldDDL += " serial"
                }
                fieldDDL
            })
            .collect(Collectors.toList<String?>())

        ddl.append(java.lang.String.join(",$lineSeparator", fieldsDDL))

        ddl.append(");$lineSeparator")
        //todo 添加表注释
        val tableComment: String = ddlContext.tableChineseName // 获取表注释
        if (StrUtil.isNotEmpty(tableComment)) {
            ddl.append("comment on table $tableEnglishName is '$tableComment';$lineSeparator")
        }

        //todo 添加字段注释
        dto.stream()
            .filter(Predicate<RangeContext> { rangeContext: RangeContext -> StrUtil.isNotEmpty(rangeContext.fieldComment) })
            .forEach(Consumer<RangeContext> { rangeContext: RangeContext ->
                var fieldName: String = rangeContext.fieldName
                fieldName = JlStrUtil.makeSurroundWith(fieldName, "\"")
                val fieldComment: String? = rangeContext.fieldComment // 获取字段注释
                ddl.append("comment on column $tableEnglishName.$fieldName is '$fieldComment';$lineSeparator")
            })

        ddl.append("-----------------------------------------------------------------------------------$lineSeparator")

        val string = ddl.toString()
        return string
    }

    override fun generateAddColDDL(ddlContext: DDLContext): String {
        val simpleName = this.javaClass.simpleName
        println(
            "#开始生成添加列语句,依据:${simpleName}--------------当前读取的对象：${ddlContext
                .simpleClassName}----------"
        )

        TODO()
    }

    override fun printChangeDML(ddlContext: DDLContext): String {
        TODO("Not yet implemented")
    }

    override fun mapTypeByMysqlType(mysqlType: String): String {
        return fieldMappings.find { it.mysqlType.equals(mysqlType, ignoreCase = true) }?.oracleType!!
    }


    override fun mapTypeByJavaType(javaFieldMetaInfo: JavaFieldMetaInfo): String {
        return fieldMappings.find { it.predi.test(javaFieldMetaInfo) }?.oracleType!!

    }

}