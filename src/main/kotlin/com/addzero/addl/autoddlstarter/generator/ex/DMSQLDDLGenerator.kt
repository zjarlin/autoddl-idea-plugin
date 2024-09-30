package com.addzero.addl.autoddlstarter.generator.ex

import cn.hutool.core.util.StrUtil
import com.addzero.addl.autoddlstarter.generator.DatabaseDDLGenerator
import com.addzero.addl.autoddlstarter.generator.IDatabaseGenerator.Companion.fieldMappings
import com.addzero.addl.autoddlstarter.generator.entity.DDLContext
import com.addzero.addl.autoddlstarter.generator.entity.JavaFieldMetaInfo
import com.addzero.addl.util.JlStrUtil

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
        var (tableChineseName, tableEnglishName, databaseType,
        databaseName, dto) = ddlContext


     tableEnglishName = tableEnglishName.uppercase() // 达梦数据库表名通常为大写

        val createTableSQL = """
    CREATE TABLE "$tableEnglishName" (
        "ID" VARCHAR(64) NOT NULL AUTO_INCREMENT,
        "CREATED_BY" VARCHAR(255) NOT NULL COMMENT '创建者',
        "UPDATED_BY" VARCHAR(255) NOT NULL COMMENT '更新者',
        "CREATED_TIME" DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
        "UPDATED_TIME" DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
        ${
            dto.joinToString(System.lineSeparator()) {
                """
                    "${it.colName.uppercase()}" ${it.colType}  COMMENT '${it.colComment}' 
                """.trimIndent()
            }
        },
        PRIMARY KEY ("ID")
    ) STORAGE(ON "MAIN", CLUSTERBTR)
    COMMENT = '$tableChineseName'; 
""".trimIndent()
        return createTableSQL
    }

    override fun generateAddColDDL(ddlContext: DDLContext): String {
        val (tableChineseName, tableEnglishName, databaseType, databaseName, dto) = ddlContext
        val dmls = dto.joinToString(System.lineSeparator()) {

            // 如果 databaseName 不为空，则拼接成 databaseName.tableEnglishName
            val tableRef = if (databaseName.isBlank()) {
                JlStrUtil.makeSurroundWith(tableEnglishName.uppercase(), "\"")
            } else {
                "\"$databaseName\".\"${tableEnglishName.uppercase()}\""
            }

            // 生成 ALTER 语句以及字段注释
            val upperCaseColName = StrUtil.toUnderlineCase(it.colName).uppercase()
            """
            ALTER TABLE $tableRef ADD COLUMN "$upperCaseColName" ${it.colType}(${it.colLength}) ; 
            COMMENT ON COLUMN $tableRef."$upperCaseColName" IS '${it.colComment}';
        """.trimIndent()
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