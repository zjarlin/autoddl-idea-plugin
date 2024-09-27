package com.addzero.addl

import cn.hutool.core.util.ClassUtil
import cn.hutool.extra.pinyin.PinyinUtil
import com.addzero.addl.autoddlstarter.generator.DatabaseDDLGenerator
import com.addzero.addl.autoddlstarter.generator.defaultconfig.BaseMetaInfoUtil
import com.addzero.addl.autoddlstarter.generator.entity.DDLContext
import com.addzero.addl.autoddlstarter.generator.entity.JavaFieldMetaInfo
import com.addzero.addl.autoddlstarter.generator.entity.RangeContext
import com.addzero.addl.autoddlstarter.tools.DuplicateSuffixAdder
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import javax.swing.JTextField

class AutoDDL : AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project

        // 显示表单弹窗
        val form = AutoDDLForm(project)
        if (form.showAndGet()) {

            // 确保表格的编辑内容已经保存
            if (form.fieldsTable!!.cellEditor != null) {
                form.fieldsTable!!.cellEditor.stopCellEditing()
            }

            // 表单被提交，生成DDL
            val formDTO = form.formDTO
            formDTO.fields = formDTO.fields?.filter { it.javaType.isNotBlank() }
            val ddlResult = genDDL(formDTO)
            // 使用 IntelliJ 内置的 SQL 编辑器显示 SQL 语句
            showDDLInSqlEditor(project, ddlResult)
        }
    }

    private fun genDDL(formDTO: FormDTO): String {
        val (tableName, tableEnglishName, dbType, dbName, fields) = formDTO
        //获取sql生成器
        val databaseDDLGenerator = DatabaseDDLGenerator.getDatabaseDDLGenerator(dbType)

        //获取元数据提取器
//        val ddlContexts: List<DDLContext> = BaseMetaInfoUtil.extractDDLContext(sysUserClass)
        val toList = fields!!.map {
            var (javaType, fieldName, fieldChineseName) = it
            val javaType2RefType = DatabaseDDLGenerator.javaType2RefType(javaType)
            val type = ClassUtil.loadClass<Any>(javaType2RefType)

            //转为sqlType
//            val type = Object::class.java


            val javaFieldMetaInfo = JavaFieldMetaInfo(fieldName!!, type, type, fieldChineseName)

            val mapTypeByJavaType = databaseDDLGenerator.mapTypeByJavaType(javaFieldMetaInfo)
            val length = databaseDDLGenerator.getLength(javaFieldMetaInfo)
            val primaryKey = BaseMetaInfoUtil.isPrimaryKey(fieldName)
            val rangecontext = RangeContext(
                fieldName, null, fieldChineseName, null, "", mapTypeByJavaType, length!!, primaryKey, primaryKey, type
            )
            rangecontext

        }.toList()

        val ddlContext = DDLContext(tableEnglishName, tableEnglishName, tableName!!, dbType, toList, dbName!!)

        //做一些自动处理字段名,表名操作
        if (ddlContext.tableEnglishName.isBlank()) {
//            ddlContext.tableEnglishName = PinyinUtil.getPinyin(tableName, "_")
            ddlContext.tableEnglishName = tableName
        }
        ddlContext.dto.onEach {
            if (it.fieldName.isBlank()) {
//                it.fieldName = PinyinUtil.getPinyin(it.fieldComment, "_")
                it.fieldName = it.fieldComment!!
            }
        }

        val kMutableProperty11 = RangeContext::fieldName
        val kMutableProperty1 = RangeContext::fieldName
        //对重复的列,进行后缀编号处理

        val handleDuplicatesWithStreamAndCustomRemap = DuplicateSuffixAdder.handleDuplicatesWithStreamAndCustomRemap(
            ddlContext.dto,
            { it.fieldName },
            { item, s -> item.fieldName = s!! })

        val generateDDL = databaseDDLGenerator.generateCreateTableDDL(ddlContext)

        //打印add Col DDL
        val ad = databaseDDLGenerator.generateAddColDDL(ddlContext)

        return listOf(
            generateDDL, "以下为添加列语句,用户可选择性执行", System.lineSeparator(), ad
        ).joinToString(System.lineSeparator())

    }

    private fun showDDLInSqlEditor(project: Project?, ddlResult: String) {
        // 创建一个文本域
        val textField = JTextField(ddlResult)

        // 调用showTextAreaDialog
        Messages.showTextAreaDialog(textField,                        // 第一个参数为JTextField
            "Generated DDL",                  // 窗口标题
            "SQL Output",                     // DimensionServiceKey
            { input -> input.split("\n") },   // parser: 将输入按行解析成List
            { lines -> lines.joinToString("\n") }  // lineJoiner: 将List连接成字符串
        )
    }


}

fun main() {
//    val chinese2English = TransUtil.chinese2English("测试")
    val pinyin = PinyinUtil.getPinyin("测试", "_")
//    println(pinyin)
//    println(chinese2English)
}