package com.addzero.addl.autoddlstarter.generator.ex

import cn.hutool.core.util.StrUtil
import com.addzero.addl.autoddlstarter.tools.JlStrUtil
import com.addzero.addl.autoddlstarter.generator.DatabaseDDLGenerator
import com.addzero.addl.autoddlstarter.generator.defaultconfig.BaseMetaInfoUtil
import com.addzero.addl.autoddlstarter.generator.entity.DDLContext
import com.addzero.addl.autoddlstarter.generator.entity.JavaFieldMetaInfo
import com.addzero.addl.autoddlstarter.generator.entity.RangeContext
import java.util.*
import java.util.function.Consumer
import java.util.function.Function
import java.util.function.Predicate
import java.util.stream.Collectors

/**
 * 达梦
 *
 * @author zjarlin
 * @see DatabaseDDLGenerator
 *
 * @since 2024/01/16
 */
class DMSQLDDLGenerator : DatabaseDDLGenerator() {
    override fun generateCreateTableDDL(ddlContext: DDLContext): String {
        val sheetName: String = ddlContext.simpleClassName
        var tableEnglishName: String = ddlContext.tableEnglishName
        tableEnglishName = tableEnglishName.uppercase(Locale.getDefault())
        tableEnglishName = JlStrUtil.makeSurroundWith(tableEnglishName, "\"")
        val databaseName: String = ddlContext.databaseName

        val refName = if (StrUtil.isBlank(databaseName)) tableEnglishName else databaseName?.let {
            JlStrUtil.makeSurroundWith(
                it, "\""
            )
        } + "." + tableEnglishName

        val dto: List<RangeContext> = ddlContext.dto

        val lineSeparator = System.lineSeparator()
        val simpleName = this.javaClass.simpleName

        println("--开始生成建表语句,依据:,依据:${simpleName}------------当前读取的对象---$sheetName-----------------------")
        val createTable = "create table $refName ($lineSeparator"

        val ddl = StringBuilder(createTable)

        val fieldsDDL = dto.stream().map<String?>(Function<RangeContext, String?> { field: RangeContext ->
            var fieldName: String = field.fieldName
            fieldName = fieldName.uppercase(Locale.getDefault())
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

            val pgFieldType = mapTypeByMysqlType(fieldType)

            var fieldDDL = "\"$fieldName\" $pgFieldType"

            if ("0" != fieldLength) {
                fieldDDL += "($fieldLength)"
            }

            if ("Y" == isPrimaryKey) {
                fieldDDL += ""
            }

            if ("Y" == isSelfIncreasing) {
                fieldDDL += " serial"
            }
            fieldDDL
        }).collect(Collectors.toList<String?>())

        ddl.append(java.lang.String.join(",$lineSeparator", fieldsDDL))
        ddl.append("," + System.lineSeparator())
        val s = "NOT CLUSTER PRIMARY KEY(\"ID\")) STORAGE(ON \"MAIN\", CLUSTERBTR) ; "
        ddl.append(s + System.lineSeparator())


        //todo 添加表注释
        val tableComment: String = ddlContext.tableChineseName // 获取表注释
        if (StrUtil.isNotEmpty(tableComment)) {
            ddl.append("comment on table $refName is '$tableComment';$lineSeparator")
        }

        //todo 添加字段注释
        dto.stream()
            .filter(Predicate<RangeContext> { rangeContext: RangeContext -> StrUtil.isNotEmpty(rangeContext.fieldComment) })
            .forEach(Consumer<RangeContext> { rangeContext: RangeContext ->
                var fieldName: String = rangeContext.fieldName
                fieldName = fieldName.uppercase(Locale.getDefault())
                fieldName = JlStrUtil.makeSurroundWith(fieldName, "\"")
                val fieldComment: String = rangeContext.fieldComment ?: fieldName //
                // 获取字段注释
                ddl.append("comment on column $refName.$fieldName is '$fieldComment';$lineSeparator")
            })
        ddl.append("-----------------------------------------------------------------------------------$lineSeparator")
        return ddl.toString()
    }

    override fun generateAddColDDL(ddlContext: DDLContext): String {
        val simpleName = this.javaClass.simpleName
        println(
            "#开始生成添加列语句,依据:${simpleName}--------------当前读取的对象：${ddlContext .simpleClassName}----------"
        )

        val (sheetName, tableEnglishName, tableChineseName, databaseType, dto, databaseName) = ddlContext

        val dmls = dto.map {
            val fieldName = it.fieldName
            val upperCase = StrUtil.toUnderlineCase(fieldName)?.uppercase()
            val dmsql: String = """
                        alter table "$databaseName"."$tableEnglishName" add column ( "$upperCase" ${it.fieldType} ${it.fieldLength} );
                        ${System.lineSeparator()}
                        comment on column "$databaseName"."$tableEnglishName"."$upperCase" is '${it.fieldComment + it.fieldCommentAppend}';
        """.trimIndent()
            return@map dmsql
        }.joinToString(System.lineSeparator())
        return dmls
    }

    override fun printChangeDML(
        ddlContext: DDLContext,
    ): String {
        val tabName: String = ddlContext.tableEnglishName
        val joinToString = ddlContext.dto.joinToString(System.lineSeparator()) {
            val colComment = it.fieldComment
            val newColName = it.newColName
            val colType = it.fieldType
            val colLength = it.fieldLength
            val col: String = StrUtil.toUnderlineCase(it.fieldName)!!.uppercase()
            val newCol: String = StrUtil.toUnderlineCase(newColName)!!.uppercase()
            """
                """.trimIndent()
            TODO()
        }

        return joinToString
    }

    override fun mapTypeByMysqlType(mysqlType: String): String {
        return fieldMappings.find { it.mysqlType.equals(mysqlType, ignoreCase = true) }?.dmType!!
    }

    override fun mapTypeByJavaType(javaFieldMetaInfo: JavaFieldMetaInfo): String {
        return fieldMappings.find { it.predi.test(javaFieldMetaInfo) }?.dmType!!
    }

}