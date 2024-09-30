package com.addzero.addl.autoddlstarter.generator.ex

import cn.hutool.core.util.StrUtil
import com.addzero.addl.autoddlstarter.generator.DatabaseDDLGenerator
import com.addzero.addl.autoddlstarter.generator.IDatabaseGenerator.Companion.fieldMappings
import com.addzero.addl.autoddlstarter.generator.entity.DDLContext
import com.addzero.addl.autoddlstarter.generator.entity.JavaFieldMetaInfo
import com.addzero.addl.ktututil.toUnderlineCase
import com.addzero.addl.util.JlStrUtil


class MysqlDDLGenerator : DatabaseDDLGenerator() {
    override fun generateCreateTableDDL(ddlContext: DDLContext): String {
        val tableEnglishName = ddlContext.tableEnglishName
        val tableChineseName = ddlContext.tableChineseName
        val dto = ddlContext.dto

        val createTableSQL = """
    CREATE TABLE `$tableEnglishName` (
        `id` VARCHAR(64) NOT NULL ,
        `created_by` VARCHAR(255) NOT NULL COMMENT '创建者',
        `updated_by` VARCHAR(255) NOT NULL COMMENT '更新者',
        `created_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
        `updated_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
        ${
            dto.joinToString(System.lineSeparator()) {
                val colLength =it.colLength
                """
                       `${it.colName.toUnderlineCase()}` ${it.colType}    $colLength    COMMENT '${it .colComment}' ,
                """.trimIndent()
            }
        }
        PRIMARY KEY (`id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
     COMMENT = '${tableChineseName}'; 
""".trimIndent()
        return createTableSQL
    }


    override fun generateAddColDDL(ddlContext: DDLContext): String {
        val (tableChineseName, tableEnglishName, databaseType, databaseName, dto) = ddlContext
        val dmls = dto.joinToString(System.lineSeparator()) {

            // 如果 databaseName 不为空，则拼接成 databaseName.tableEnglishName
            val tableRef = if (databaseName.isBlank()) {
                JlStrUtil.makeSurroundWith(tableEnglishName, "`")
            } else {
                "`$databaseName`.`$tableEnglishName`"
            }
            // 生成 ALTER 语句以及字段注释
            val toUnderlineCase = StrUtil.toUnderlineCase(it.colName)
            """
            ALTER TABLE $tableRef ADD COLUMN `$toUnderlineCase` ${it.colType}(${it.colLength}) ; 
            COMMENT ON COLUMN $tableRef.`$toUnderlineCase` IS '${it.colComment}';
        """.trimIndent()
        }

        return dmls
    }


    override fun mapTypeByMysqlType(mysqlType: String): String {
        return fieldMappings.find { it.mysqlType.equals(mysqlType, ignoreCase = true) }?.mysqlType!!
    }


    override fun mapTypeByJavaType(javaFieldMetaInfo: JavaFieldMetaInfo): String {
        return fieldMappings.find { it.predi.test(javaFieldMetaInfo) }?.mysqlType!!

    }
}