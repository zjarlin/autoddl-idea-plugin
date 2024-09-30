package com.addzero.addl.util.fieldinfo

import com.example.demo.modules.ai.util.fieldinfo.FieldInfo
import com.example.demo.modules.ai.util.fieldinfo.toSimpleString
import com.fasterxml.jackson.annotation.JsonPropertyDescription
import java.lang.reflect.Field
import java.lang.reflect.ParameterizedType

fun getFieldInfos(clazz: Class<*>): List<FieldInfo> {
    return getFieldInfosRecursive(clazz)
}

fun getFieldInfosRecursive(clazz: Class<*>): List<FieldInfo> {
    val fieldInfoList = mutableListOf<FieldInfo>()
    // 获取当前类的所有字段，包括父类的字段
    val fields = clazz.declaredFields
    for (field in fields) {
        field.isAccessible = true

        // 获取字段上的注释
        val description = field.getAnnotation(JsonPropertyDescription::class.java)?.value

        // 判断字段是否是嵌套对象
        val isNestedObject = isCustomObject(field.type)

        // 如果字段是嵌套对象类型，递归获取其子字段
        val children = if (isNestedObject) {
            getFieldInfosRecursive(field.type)
        } else if (isList(field)) {
            // 如果字段是一个集合类型，递归获取其泛型类型的子字段
            val genericType = (field.genericType as? ParameterizedType)?.actualTypeArguments?.get(0)
            if (genericType is Class<*> && isCustomObject(genericType)) {
                getFieldInfosRecursive(genericType)
            } else {
                emptyList()
            }
        } else {
            emptyList()
        }

        // 添加当前字段的信息到列表中，包括其子字段
        fieldInfoList.add(
            FieldInfo(
                declaringClass = clazz,
                field = field,
                description = description,
                fieldType = field.type,
                isNestedObject = isNestedObject,
                children = children // 子字段
            )
        )
    }
    return fieldInfoList
}
fun getSimpleFieldInfoStr(clazz: Class<*>): String {
    val joinToString = getFieldInfosRecursive(clazz).joinToString { fieldInfo ->
        fieldInfo.toSimpleString()
    }
    return joinToString;

}


// 判断一个字段是否是自定义对象
fun isCustomObject(clazz: Class<*>): Boolean {
    return !(clazz.isPrimitive || clazz == String::class.java || Number::class.java.isAssignableFrom(clazz) || clazz.isEnum || clazz == List::class.java)
}

// 判断字段是否是 List 类型
fun isList(field: Field): Boolean {
    return List::class.java.isAssignableFrom(field.type)
}