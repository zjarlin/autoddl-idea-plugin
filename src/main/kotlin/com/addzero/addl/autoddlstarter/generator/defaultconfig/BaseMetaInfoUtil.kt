package com.addzero.addl.autoddlstarter.generator.defaultconfig

import com.addzero.addl.autoddlstarter.generator.entity.JavaFieldMetaInfo
import com.addzero.addl.util.RefUtil.extractClassMetaInfo
import com.addzero.addl.util.RefUtil.extractInterfaceMetaInfo


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

    fun isAutoIncrement(fieldName: String?): String {
        return isPrimaryKey(fieldName)
    }

    fun isAutoIncrementBoolean(fieldName: String?): Boolean {
        return isPrimaryKeyBoolean(fieldName)
    }

    /**
     * 处理接口和类的字段元数据逻辑不一样所以需要单独处理
     * @param [clazz]
     * @return [List<JavaFieldMetaInfo>]
     */
    fun javaFieldMetaInfos(clazz: Class<*>): List<JavaFieldMetaInfo> {
        if (clazz.isInterface) {
            return extractInterfaceMetaInfo(clazz)
        }
        return extractClassMetaInfo(clazz)
    }


}