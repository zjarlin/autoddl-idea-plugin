package com.addzero.addl.autoddlstarter.generator.defaultconfig

import cn.hutool.core.util.StrUtil
import com.addzero.addl.autoddlstarter.generator.DatabaseDDLGenerator
import com.addzero.addl.autoddlstarter.generator.DatabaseDDLGenerator.Companion.getDatabaseDDLGenerator
import com.addzero.addl.autoddlstarter.generator.defaultconfig.DefaultMetaInfoUtil.getTableChineseNameFun
import com.addzero.addl.autoddlstarter.generator.defaultconfig.DefaultMetaInfoUtil.getTableEnglishNameFun
import com.addzero.addl.autoddlstarter.generator.defaultconfig.DefaultMetaInfoUtil.mydbType
import com.addzero.addl.autoddlstarter.generator.entity.DDLContext
import com.addzero.addl.autoddlstarter.generator.entity.JavaFieldMetaInfo
import com.addzero.addl.autoddlstarter.generator.entity.RangeContext
import com.addzero.addl.autoddlstarter.tools.RefUtil
import com.addzero.addl.autoddlstarter.tools.RefUtil.extractClassMetaInfo
import com.addzero.addl.autoddlstarter.tools.RefUtil.extractInterfaceMetaInfo
import java.lang.reflect.ParameterizedType


object BaseMetaInfoUtil {
    fun isPrimaryKey(fieldName: String?): String {
        if (fieldName?.lowercase()!! == "id") {
            return "Y"
        }
        return ""
    }

    fun isPrimaryKeyBoolean(fieldName: String?): Boolean {
        return fieldName?.lowercase()!! == "id"
    }

    private fun isAutoIncrement(fieldName: String?): String {
        return isPrimaryKey(fieldName)
    }

    /**
     * 处理接口和类的字段元数据逻辑不一样所以需要单独处理
     * @param [clazz]
     * @return [List<JavaFieldMetaInfo>]
     */
    private fun javaFieldMetaInfos(clazz: Class<*>): List<JavaFieldMetaInfo> {
        if (clazz.isInterface) {
            return extractInterfaceMetaInfo(clazz)
        }
        return extractClassMetaInfo(clazz)
    }

    fun extractDDLContext(clazz: Class<*>): List<DDLContext> {
        //当前读取对象名称
        val sheetName = clazz.simpleName
        val tableEngName = getTableEnglishNameFun(clazz)
        val tableChineseName = getTableChineseNameFun(clazz)
        val fields = javaFieldMetaInfos(clazz)
        // 处理字段并生成 RangeContext
        val rangeContexts = fields.map {
            val name = it.name
            val description = if (isPrimaryKeyBoolean(name)) {
                "主键"
            } else {
                it.comment
            }
            val colName = StrUtil.toUnderlineCase(name)

            // 判断属性是否是实体类
            if (RefUtil.isT(it.type) && it.type != clazz) {
                // 如果是实体类，递归处理实体类中的字段
                extractDDLContext(it.type)
            }
            // 判断是否是集合类型的实体类
            if (Collection::class.java.isAssignableFrom(it.type)) {
                val genericType = (it.genericType as ParameterizedType).actualTypeArguments[0] as Class<*>
                val t1 = RefUtil.isT(genericType)
                if (t1 && genericType != clazz) {
                    extractDDLContext(genericType)
                }
            }
            // 生成 RangeContext
            val databaseDDLGenerator = getDatabaseDDLGenerator(mydbType())
            val fieldType = databaseDDLGenerator.mapTypeByJavaType(it)

            val rangeContext = RangeContext(
                colName,
                null,
                description,
                null,
                "",
                fieldType,
                databaseDDLGenerator.getLength(it)!!,
                isPrimaryKey(it.name),
                isAutoIncrement(it.name), it.type
            )
            rangeContext
        }.toList()

        // 生成并返回当前类的 DDLContext
        return listOf(DDLContext(sheetName, tableEngName, tableChineseName, mydbType(), rangeContexts, ""))
    }


}