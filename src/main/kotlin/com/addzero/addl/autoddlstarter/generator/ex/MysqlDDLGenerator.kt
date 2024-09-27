package com.addzero.addl.autoddlstarter.generator.ex

import cn.hutool.core.util.StrUtil
import com.addzero.addl.autoddlstarter.tools.JlStrUtil
import com.addzero.addl.autoddlstarter.generator.DatabaseDDLGenerator
import com.addzero.addl.autoddlstarter.generator.defaultconfig.BaseMetaInfoUtil
import com.addzero.addl.autoddlstarter.generator.entity.DDLContext
import com.addzero.addl.autoddlstarter.generator.entity.JavaFieldMetaInfo
import com.addzero.addl.autoddlstarter.generator.entity.RangeContext
import java.util.concurrent.atomic.AtomicBoolean
import java.util.stream.Collectors

class MysqlDDLGenerator : DatabaseDDLGenerator() {
    override fun generateCreateTableDDL(ddlContext: DDLContext): String {
        val contextName = ddlContext.simpleClassName
        val tableEnglishName = ddlContext.tableEnglishName
        val tableChineseName = ddlContext.tableChineseName
        val databaseType = ddlContext.databaseType
        val dto = ddlContext.dto

        val lineSeparator = System.lineSeparator()

        val simpleName = this.javaClass.simpleName

        println("#开始生成建表语句,依据:${simpleName}--------------当前读取的对象：$contextName--------------------------")
//        println("生成器为"+name)

        val join = StrUtil.join(" ", "CREATE TABLE", "`$tableEnglishName` (", lineSeparator)


        val finalDdl = arrayOf(StringBuilder())

        val autoIncrement = AtomicBoolean(true)
        val collect = dto.stream().map<String> { e: RangeContext ->
            val fieldName = e.fieldName
            val fieldComment = if (BaseMetaInfoUtil.isPrimaryKeyBoolean(fieldName)) {
                "主键"
            } else {
                e.fieldComment
            }
            val fieldCommentAppend = e.fieldCommentAppend
            val syndicationNotes = fieldComment ?: (fieldCommentAppend + fieldCommentAppend)
            val fieldType = e.fieldType
            val fieldLength = e.fieldLength
            val isPrimaryKey = e.isPrimaryKey
            val isSelfIncreasing = e.isSelfIncreasing


            autoIncrement.set("Y" == isSelfIncreasing)

            val s: String = JlStrUtil.makeSurroundWith(fieldName, "`")
            val s1 = if ("0" != fieldLength) "($fieldLength)" else ""
            val s2 = if ("Y" == isPrimaryKey) " PRIMARY KEY " else ""
            val s3 = if ("Y" == isSelfIncreasing) " AUTO_INCREMENT " else ""
            val s4 = "COMMENT '$syndicationNotes'"
            val join1 = StrUtil.join(" ", s, fieldType, s1, s2, s3, s4)
            join1
        }.collect(Collectors.joining("," + System.lineSeparator()))


        val stringBuilder = StringBuilder()
        val s = if ("" != tableChineseName) {
            val removeNotChinese = JlStrUtil.removeNotChinese(tableChineseName)
            ("COMMENT = '" + removeNotChinese + "'")

        } else ""
        stringBuilder.append("${System.lineSeparator()})  ENGINE=InnoDB ")
            .append(if (autoIncrement.get()) "AUTO_INCREMENT=1" else "").append(" DEFAULT CHARSET=utf8mb4 ").append(
                s
            ).append(";\r\n")
        stringBuilder.append("----------------------------------------------------------------------------------\r\n")

        return join + collect + stringBuilder.toString()
    }

    override fun generateAddColDDL(ddlContext: DDLContext): String {
        val simpleName = this.javaClass.simpleName
        println(
            "#开始生成添加列语句,依据:${simpleName}--------------当前读取的对象：${ ddlContext .simpleClassName
            }----------"
        )

        val (sheetName, tableEnglishName, tableChineseName, databaseType, dto, databaseName) = ddlContext
        val dmls = dto.map {
            val fieldName = it.fieldName
            val upperCase = StrUtil.toUnderlineCase(fieldName)
            val makeSurroundWith = JlStrUtil.makeSurroundWith(tableEnglishName, "`")
            val s = if (databaseName.isBlank()) {
                makeSurroundWith
            } else {
                "$databaseName.$tableEnglishName"
            }

            val dmsql: String = """
                        alter table $s add column  "$upperCase" ${it .fieldType} (${it.fieldLength}) ; comment on column $s.$upperCase" is '${it.fieldComment}';
        """.trimIndent()
            return@map dmsql
        }.joinToString(System.lineSeparator())
        return dmls
    }

    override fun printChangeDML(ddlContext: DDLContext): String {
        val (sheetName, tableEnglishName, tableChineseName, databaseType, dto, databaseName) = ddlContext
        val joinToString = dto.joinToString(System.lineSeparator()) {
            val col: String = StrUtil.toUnderlineCase(it.fieldName)
            val newCol: String = StrUtil.toUnderlineCase(it.newColName)
            """
                   alter table`${tableEnglishName}`  change `$col` `$newCol` ${it.fieldType}(${it.fieldLength}) null comment '${it.fieldComment}';
                """.trimIndent()
        }
        return joinToString
    }


    override fun mapTypeByMysqlType(mysqlType: String): String {
        return fieldMappings.find { it.mysqlType.equals(mysqlType, ignoreCase = true) }?.mysqlType!!
    }


    override fun mapTypeByJavaType(javaFieldMetaInfo: JavaFieldMetaInfo): String {
        return fieldMappings.find { it.predi.test(javaFieldMetaInfo) }?.mysqlType!!

    }
}