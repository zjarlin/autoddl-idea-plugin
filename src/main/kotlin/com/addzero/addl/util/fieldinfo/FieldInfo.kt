package com.example.demo.modules.ai.util.fieldinfo

import java.lang.reflect.Field


fun FieldInfo.toSimple(): FieldInfoSimple {
    return FieldInfoSimple(
        fieldName = this.field.name,
        description = this.description
    )
}

fun FieldInfo.toSimpleWithChildren(): List<FieldInfoSimple> {
    val simpleFields = mutableListOf<FieldInfoSimple>()
    // 先将当前字段转换为简单形式
    simpleFields.add(this.toSimple())
    // 如果有子字段，递归处理子字段
    for (child in this.children) {
        simpleFields.addAll(child.toSimpleWithChildren())
    }

    return simpleFields
}

fun FieldInfo.toSimpleString(): String {
    val fieldInfo = "${field.name}: ${description ?: "No description"}"
    val childrenInfo = children.joinToString() { it.toSimpleString() }
    return if (childrenInfo.isNotEmpty()) {
        "$fieldInfo ,  $childrenInfo"
    } else {
        fieldInfo
    }
}


data class FieldInfoSimple(
    val fieldName: String,                   // 字段本身
    val description: String?,           // 字段的注释
)

data class FieldInfo(
    val declaringClass: Class<*>,       // 字段所在的类
    val field: Field,                   // 字段本身
    val description: String?,           // 字段的注释
    val fieldType: Class<*>,            // 字段的类型
    val isNestedObject: Boolean,        // 是否是嵌套对象
    val children: List<FieldInfo> = emptyList(), // 子字段列表
)