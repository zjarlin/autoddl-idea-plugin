package com.addzero.addl.autoddlstarter.generator.factory

import cn.hutool.core.util.ClassUtil
import cn.hutool.core.util.StrUtil
import com.addzero.addl.autoddlstarter.generator.IDatabaseGenerator.Companion.fieldMappings
import com.addzero.addl.autoddlstarter.generator.IDatabaseGenerator.Companion.getDatabaseDDLGenerator
import com.addzero.addl.autoddlstarter.generator.IDatabaseGenerator.Companion.getLength
import com.addzero.addl.autoddlstarter.generator.consts.MYSQL
import com.addzero.addl.autoddlstarter.generator.defaultconfig.BaseMetaInfoUtil
import com.addzero.addl.autoddlstarter.generator.entity.DDLContext
import com.addzero.addl.autoddlstarter.generator.entity.DDLRangeContextUserInput
import com.addzero.addl.autoddlstarter.generator.entity.DDlRangeContext
import com.addzero.addl.autoddlstarter.generator.entity.JavaFieldMetaInfo
import com.addzero.addl.util.PinYin4JUtils

object DDLContextFactory4UserInputMetaInfo {
    fun createDDLContext(
        tableEngLishName: String,
        tableChineseName: String,
        databaseType: String = MYSQL,
        ddlRangeContextUserInput: List<DDLRangeContextUserInput>,
    ): DDLContext {


        val hanziToPinyin = PinYin4JUtils.hanziToPinyin(tableChineseName, "_")
        return DDLContext(
            tableChineseName = tableChineseName,
            tableEnglishName = tableEngLishName.ifBlank {
                hanziToPinyin
            },
            databaseType = databaseType,
            dto = createRangeContext1(databaseType, ddlRangeContextUserInput),
        )
    }

    private fun createRangeContext1(
        databaseType: String,
        ddlRangeContextUserInput: List<DDLRangeContextUserInput>,
    ): List<DDlRangeContext> {
        val databaseDDLGenerator = getDatabaseDDLGenerator(databaseType)
        val toList = ddlRangeContextUserInput.map {
            val colComment = it.colComment
            var colName = it.colName

            if (colName.isBlank()) {
                colName = PinYin4JUtils.hanziToPinyin(colName, "_")
            }

            val toCamelCase = StrUtil.toCamelCase(colName)
            val javaType = it.javaType
            val firstNotNullOf = fieldMappings.find {
                it.javaClassSimple == javaType
            }?.javaClassRef

            val loadClass = ClassUtil.loadClass<Any>(firstNotNullOf)
            val javaFieldMetaInfo = JavaFieldMetaInfo(toCamelCase, loadClass, loadClass, colComment)
            val mapTypeByJavaType = databaseDDLGenerator.mapTypeByJavaType(javaFieldMetaInfo)
            val length = getLength(javaFieldMetaInfo)
            val primaryKey = BaseMetaInfoUtil.isPrimaryKey(toCamelCase)
            val autoIncrement = BaseMetaInfoUtil.isAutoIncrement(toCamelCase)
            DDlRangeContext(
                colName, mapTypeByJavaType, colComment, length,
                primaryKey, autoIncrement
            )
        }.toList()
        return toList
    }
}