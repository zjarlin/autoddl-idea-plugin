package com.addzero.addl.autoddlstarter.generator.defaultconfig

import com.addzero.addl.autoddlstarter.anno.Comment
import com.addzero.addl.autoddlstarter.generator.MYSQL
import com.addzero.addl.autoddlstarter.tools.JlStrUtil
import com.addzero.addl.autoddlstarter.tools.JlStrUtil.shortEng
//import io.swagger.v3.oas.annotations.media.Schema
//import org.babyfish.jimmer.sql.Table
import java.lang.reflect.AnnotatedElement

interface IMetaInfoUtil {
    /**
     * 数据库类型
     * @return [String]
     */
    fun mydbType(): String

    /**
     * 表名如何获取
     * @param [clazz]
     * @return [String]
     */
    fun getTableEnglishNameFun(clazz: Class<*>): String

    /**
     * 表注释如何获取
     * @param [clazz]
     * @return [String]
     */
    fun getTableChineseNameFun(clazz: Class<*>): String

    /**
     * 字段的注释如何获取
     * @param [method]
     * @return [String]
     */
    fun getCommentFun(element: AnnotatedElement): String?
}

/**
 * 元数据信息默认实现，用于获取表名、表注释等元数据信息
 * @author zjarlin
 * @date 2024/09/25
 */
object DefaultMetaInfoUtil : IMetaInfoUtil {
    override fun mydbType(): String {
        return MYSQL
    }

    override fun getTableEnglishNameFun(clazz: Class<*>): String {
        TODO()
//        val annotation = clazz.getAnnotation(Table::class.java) ?: return clazz.simpleName
//        val tableEnglishName = annotation.name
//        return shortEng(tableEnglishName, getTableChineseNameFun(clazz))
    }

    override fun getTableChineseNameFun(clazz: Class<*>): String {
        val annotation = clazz.getAnnotation(Comment::class.java) ?: return clazz.simpleName
        return JlStrUtil.removeNotChinese(annotation.value)
    }

    override fun getCommentFun(element: AnnotatedElement): String {
        TODO()
//        val annotation = element.getAnnotation(Schema::class.java) ?: return ""
//        return annotation.description
    }
}