package com.addzero.addl.autoddlstarter.generator.ex

import cn.hutool.core.util.StrUtil
import com.addzero.addl.autoddlstarter.generator.DatabaseDDLGenerator
import com.addzero.addl.autoddlstarter.generator.IDatabaseGenerator.Companion.fieldMappings
import com.addzero.addl.autoddlstarter.generator.entity.DDLContext
import com.addzero.addl.autoddlstarter.generator.entity.JavaFieldMetaInfo
import com.addzero.addl.util.JlStrUtil

class DMSQLDDLGenerator : DatabaseDDLGenerator() {
    override fun generateCreateTableDDL(ddlContext: DDLContext): String {
        var (tableChineseName, tableEnglishName, databaseType, databaseName, dto) = ddlContext
        tableEnglishName = tableEnglishName.uppercase()

        val tableRef = if (databaseName.isBlank()) {
            JlStrUtil.makeSurroundWith(tableEnglishName.uppercase(), "\"")
        } else {
            "\"$databaseName\".\"${tableEnglishName.uppercase()}\""
        }

        val createTableSQL = """
    CREATE TABLE $tableRef (
        "ID" VARCHAR2(64) NOT NULL,
        "CREATE_BY" VARCHAR2(255) NOT NULL,
        "UPDATE_BY" VARCHAR2(255) NOT NULL,
        "CREATE_TIME" TIMESTAMP,
        "UPDATE_TIME" TIMESTAMP,
        ${
            dto.joinToString(System.lineSeparator()) {
                """
                    "${it.colName.uppercase()}" ${it.colType} ${it.colLength?.let { length -> "($length)" }} NOT NULL
                """.trimIndent()
            }
        },
        PRIMARY KEY ("ID")
    );

    COMMENT ON TABLE "$tableEnglishName" IS '$tableChineseName';
    """.trimIndent()

        // 添加字段注释
        val comments = dto.joinToString(System.lineSeparator()) {
            """
            COMMENT ON COLUMN $tableRef."${it.colName.uppercase()}" IS '${it.colComment}';
            """.trimIndent()
        }

        return "$createTableSQL\n$comments"
    }

    override fun generateAddColDDL(ddlContext: DDLContext): String {
        val (tableChineseName, tableEnglishName, databaseType, databaseName, dto) = ddlContext
        val dmls = dto.joinToString(System.lineSeparator()) {

            val tableRef = if (databaseName.isBlank()) {
                JlStrUtil.makeSurroundWith(tableEnglishName.uppercase(), "\"")
            } else {
                "\"$databaseName\".\"${tableEnglishName.uppercase()}\""
            }

            // 生成 ALTER 语句以及字段属性
            val upperCaseColName = StrUtil.toUnderlineCase(it.colName).uppercase()
            val addColumnDDL = """
            ALTER TABLE $tableRef ADD ("$upperCaseColName" ${it.colType}(${it.colLength})); 
            """.trimIndent()

            val commentDDL = """
            COMMENT ON COLUMN $tableRef."$upperCaseColName" IS '${it.colComment}';
            """.trimIndent()

            "$addColumnDDL\n$commentDDL"
        }

        return dmls
    }

    override fun mapTypeByMysqlType(mysqlType: String): String {
        return fieldMappings.find { it.mysqlType.equals(mysqlType, ignoreCase = true) }?.dmType!!
    }

    override fun mapTypeByJavaType(javaFieldMetaInfo: JavaFieldMetaInfo): String {
        return fieldMappings.find { it.predi.test(javaFieldMetaInfo) }?.dmType!!
    }
}