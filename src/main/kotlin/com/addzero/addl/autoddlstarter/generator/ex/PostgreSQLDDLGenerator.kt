package com.addzero.addl.autoddlstarter.generator.ex

import cn.hutool.core.util.StrUtil
import com.addzero.addl.autoddlstarter.generator.DatabaseDDLGenerator
import com.addzero.addl.autoddlstarter.generator.IDatabaseGenerator.Companion.fieldMappings
import com.addzero.addl.autoddlstarter.generator.entity.DDLContext
import com.addzero.addl.autoddlstarter.generator.entity.JavaFieldMetaInfo
import com.addzero.addl.util.JlStrUtil

class PostgreSQLDDLGenerator : DatabaseDDLGenerator() {

  override fun generateCreateTableDDL(ddlContext: DDLContext): String {
        val tableEnglishName = ddlContext.tableEnglishName
        val tableChineseName = ddlContext.tableChineseName
        val dto = ddlContext.dto

        val createTableSQL = """
    CREATE TABLE "$tableEnglishName" (
        "ID" SERIAL PRIMARY KEY,
        "CREATE_BY" VARCHAR(255) NOT NULL COMMENT '创建者',
        "UPDATE_BY" VARCHAR(255) NOT NULL COMMENT '更新者',
        "CREATE_TIME" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 
        '创建时间',
        "UPDATE_TIME" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE 
        CURRENT_TIMESTAMP COMMENT '更新时间',
        ${
            dto.joinToString(System.lineSeparator()) {
                """
                    "${it.colName}" ${it.colType} COMMENT '${it.colComment}',
                """.trimIndent()
            }
        }
    );
    COMMENT ON TABLE "$tableEnglishName" IS '$tableChineseName';
""".trimIndent()

        return createTableSQL
    }

    override fun generateAddColDDL(ddlContext: DDLContext): String {
        val (tableChineseName, tableEnglishName, databaseType, databaseName, dto) = ddlContext
        val dmls = dto.joinToString(System.lineSeparator()) {

            // 如果 databaseName 不为空，则拼接成 databaseName.tableEnglishName
            val tableRef = if (databaseName.isBlank()) {
                JlStrUtil.makeSurroundWith(tableEnglishName, "\"")
            } else {
                "\"$databaseName\".\"$tableEnglishName\""
            }

            // 生成 ALTER 语句以及字段注释
            val upperCaseColName = StrUtil.toUnderlineCase(it.colName).uppercase()
            """
            ALTER TABLE $tableRef ADD COLUMN "$upperCaseColName" ${it.colType}(${it.colLength});
            COMMENT ON COLUMN $tableRef."$upperCaseColName" IS '${it.colComment}';
        """.trimIndent()
        }

        return dmls
    }
    override fun mapTypeByMysqlType(mysqlType: String): String {
        return fieldMappings.find { it.mysqlType.equals(mysqlType, ignoreCase = true) }?.pgType!!
    }

    override fun mapTypeByJavaType(javaFieldMetaInfo: JavaFieldMetaInfo): String {
        return fieldMappings.find { it.predi.test(javaFieldMetaInfo) }?.pgType!!

    }

}