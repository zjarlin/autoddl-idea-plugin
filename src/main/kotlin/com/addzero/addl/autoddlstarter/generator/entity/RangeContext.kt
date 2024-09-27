package com.addzero.addl.autoddlstarter.generator.entity

/**
 * @author zjarlin
 * @since 2023/11/3 16:46
 */
data class RangeContext(


    // 字段名
    var fieldName: String,

    var newColName: String?,

    // 字段注释
    var fieldComment: String?,

    // 追加注释
    var fieldCommentAppend: String?,

    //合体注释
    var syndicationNotes: String,

    var fieldType: String,

    // 字段长度
    var fieldLength: String,

    /** // 是否为主键  */
    var isPrimaryKey: String,

    // 是否自增
    var isSelfIncreasing: String,

    var javaType:Class<*>?,

    )