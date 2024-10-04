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
    create table "$tableEnglishName" (
        "id" varchar(64) primary key,
        "create_by" varchar(255) ,
        "update_by" varchar(255) ,
        "create_time" timestamp ,
        "update_time" timestamp ,
        ${
            dto.joinToString(System.lineSeparator()) {
                """
                    "${it.colName}" ${it.colType} comment '${it.colComment}',
                """.trimIndent()
            }
        }
        ${
            """
            comment on column $tableEnglishName.id is '主键';
            comment on column $tableEnglishName.create_by is '创建者';
            comment on column $tableEnglishName.create_time is '创建时间';
            comment on column $tableEnglishName.update_by is '更新者';
            comment on column $tableEnglishName.update_time is '更新时间'; 
            """.trimIndent()
        }
    );
    comment on table "$tableEnglishName" is '$tableChineseName';
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
            alter table $tableRef add column "$upperCaseColName" ${it.colType}(${it.colLength});
            comment on column $tableRef."$upperCaseColName" is '${it.colComment}';
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