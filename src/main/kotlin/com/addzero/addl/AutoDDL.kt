package com.addzero.addl

import cn.hutool.core.util.StrUtil
import com.addzero.addl.autoddlstarter.generator.IDatabaseGenerator.Companion.getDatabaseDDLGenerator
import com.addzero.addl.autoddlstarter.generator.entity.DDLRangeContextUserInput
import com.addzero.addl.autoddlstarter.generator.factory.DDLContextFactory4UserInputMetaInfo
import com.alibaba.fastjson2.JSON
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.ui.components.JBTextField

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
            formDTO.fields = formDTO.fields.filter {
                StrUtil.isAllNotBlank(it.javaType, it.fieldChineseName)
            }
            val ddlResult = genDDL(formDTO)
            // 使用 IntelliJ 内置的 SQL 编辑器显示 SQL 语句
            showDDLInSqlEditor(project, ddlResult)
        }
    }


    private fun showDDLInSqlEditor(project: Project?, ddlResult: String) {
        // 创建一个文本域
        val textField = JBTextField(ddlResult)

        // 调用showTextAreaDialog
        val delimiters = System.lineSeparator()
        Messages.showTextAreaDialog(textField,                        // 第一个参数为JTextField
            "Generated DDL",                  // 窗口标题
            "SQL Output",                     // DimensionServiceKey
            { input -> input.split(delimiters) },   // parser: 将输入按行解析成List
            { lines -> lines.joinToString(delimiters) }
//               lineJoiner: 将List连接成字符串
        )
    }


}

private fun genDDL(formDTO: FormDTO): String {
    val (tableName, tableEnglishName, dbType, dbName, fields) = formDTO
    val map = fields.map {

        DDLRangeContextUserInput(it.javaType, it.fieldName, it.fieldChineseName)
    }
    //用户输入元数据工厂构建方法
    val createDDLContext = DDLContextFactory4UserInputMetaInfo.createDDLContext(tableEnglishName, tableName, dbType, map)
    //未来加入实体元数据抽取工厂
    val databaseDDLGenerator = getDatabaseDDLGenerator(dbType)
    val generateCreateTableDDL = databaseDDLGenerator.generateCreateTableDDL(createDDLContext)
    return generateCreateTableDDL
}


fun main() {
    val trimIndent = """
       {
  "dbName" : "示例数据库名称",
  "dbType" : "mysql",
  "fields" : [ {
    "fieldChineseName" : "字段注释",
    "fieldName" : "字段名",
    "javaType" : "String"
  } ],
  "tableEnglishName" : "示例英文名",
  "tableName" : "示例表名"
} 
    """.trimIndent()
    val parseObject = JSON.parseObject(trimIndent, FormDTO::class.java)
    val genDDL = genDDL(parseObject)
    println(genDDL)
}