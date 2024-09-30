package com.addzero.addl

import FieldsTableModel
import com.addzero.addl.autoddlstarter.generator.IDatabaseGenerator.Companion.javaTypesEnum
import com.addzero.addl.autoddlstarter.generator.consts.DM
import com.addzero.addl.autoddlstarter.generator.consts.MYSQL
import com.addzero.addl.autoddlstarter.generator.consts.ORACLE
import com.addzero.addl.autoddlstarter.generator.consts.POSTGRESQL
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.table.JBTable
import defaultdTO
import quesDba
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.GridLayout
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.*

class AutoDDLForm(project: Project?) : DialogWrapper(project) {
    private var mainPanel: JPanel? = null
    private var tableNameField: JTextField? = null
    private var tableEnglishNameField: JTextField? = null
    private var dbTypeComboBox: ComboBox<String>? = null
    private var dbNameField: JTextField? = null
    var fieldsTable: JBTable? = null
    private var fieldsTableModel: FieldsTableModel? = null
    private var llmPanel: JPanel? = null // 存放LLM相关内容的面板

    init {
        title = "Generate DDL"
        init()
    }

    override fun createCenterPanel(): JComponent? {
        mainPanel = JPanel(BorderLayout())
        val usageInstruction =
            JLabel("<html><font color='red'>请填写必要的表名、字段注释，并选择 Java 类型</font></html>")
        mainPanel!!.add(usageInstruction, BorderLayout.SOUTH)

        // 表单信息区域
        val formPanel = JPanel(GridLayout(4, 2))
        formPanel.add(JLabel("表中文名:"))
        tableNameField = JTextField()
        formPanel.add(tableNameField)

        formPanel.add(JLabel("表名(为空默认中文采用表名拼音):"))
        tableEnglishNameField = JTextField()
        formPanel.add(tableEnglishNameField)

        formPanel.add(JLabel("数据库类型:"))
        dbTypeComboBox = ComboBox(arrayOf(MYSQL, POSTGRESQL, DM, ORACLE))
        formPanel.add(dbTypeComboBox)

        formPanel.add(JLabel("数据库名称 (可空,达梦必须填):"))
        dbNameField = JTextField()
        formPanel.add(dbNameField)

        // 字段信息区域
        fieldsTableModel = FieldsTableModel()
        fieldsTable = JBTable(fieldsTableModel)

        // 设置 Java 类型下拉框
        val javaTypesEnum = javaTypesEnum
        val javaTypeComboBox = ComboBox(javaTypesEnum)
        fieldsTable!!.columnModel.getColumn(0).cellEditor = DefaultCellEditor(javaTypeComboBox)

        // 启用单元格点击编辑模式
        fieldsTable!!.surrendersFocusOnKeystroke = true
        fieldsTable!!.addMouseListener(object : MouseAdapter() {
            override fun mousePressed(e: MouseEvent) {
                val row = fieldsTable!!.rowAtPoint(e.point)
                val column = fieldsTable!!.columnAtPoint(e.point)
                if (row == -1) {
                    // 没有行时，添加空行
                    fieldsTableModel?.addField(FieldDTO("", "", ""))
                } else {
                    // 有行时，编辑当前单元格
                    fieldsTable!!.editCellAt(row, column)
                    fieldsTable!!.editorComponent?.requestFocus()
                }
            }
        })

        val tablePanel = JPanel(BorderLayout())
        tablePanel.add(JScrollPane(fieldsTable), BorderLayout.CENTER)
        tablePanel.preferredSize = Dimension(600, 200)

        // 添加表单信息区域和字段信息区域
        mainPanel!!.add(formPanel, BorderLayout.NORTH)
        mainPanel!!.add(tablePanel, BorderLayout.CENTER)

        // 添加高级功能折叠菜单
        addAdvancedPanel()

        return mainPanel
    }

    private fun addAdvancedPanel() {
        val advancedPanelContainer = JPanel(BorderLayout())
        val toggleButton = JToggleButton("调用LLM能力提供建表上下文(用户可二次编辑)", false)
        llmPanel = createLLMPanel() // 创建LLM面板

        // 折叠菜单的逻辑
        toggleButton.addActionListener {
            llmPanel?.isVisible = toggleButton.isSelected // 控制LLM面板显示与否
        }

        advancedPanelContainer.add(toggleButton, BorderLayout.NORTH)
        advancedPanelContainer.add(llmPanel!!, BorderLayout.CENTER) // 添加LLM面板

        // 添加到主面板
        mainPanel!!.add(advancedPanelContainer, BorderLayout.SOUTH)
    }

    private fun createLLMPanel(): JPanel {
        val llmPanel = JPanel(BorderLayout())
        val inputTextArea = JTextArea(5, 30) // 长文本框
        val submitButton = JButton("提交")

        submitButton.addActionListener {
            // 这里调用大模型接口，并获取表单实体对象
            val inputText = inputTextArea.text
            val formEntity = callLargeModelApi(inputText) // 调用你的大模型接口
            // 将表单数据回填到表单输入区域
            tableNameField!!.text = formEntity.tableName
            tableEnglishNameField!!.text = formEntity.tableEnglishName
            dbTypeComboBox!!.selectedItem = formEntity.dbType
            dbNameField!!.text = formEntity.dbName
            // 这里确保字段可以二次编辑
            fieldsTableModel!!.fields = (formEntity.fields?.toMutableList() ?: listOf()) as MutableList<FieldDTO>
            // 让用户可以编辑字段表格
            fieldsTableModel!!.fireTableDataChanged()
        }


        // 让用户可以编辑字段表格
        fieldsTableModel!!.fireTableDataChanged()

        llmPanel.add(inputTextArea, BorderLayout.CENTER)
        llmPanel.add(submitButton, BorderLayout.SOUTH)
        llmPanel.isVisible = false // 默认隐藏

        return llmPanel
    }

    // 模拟调用大模型接口的函数
    private fun callLargeModelApi(inputText: String): FormDTO {

        val quesDba = quesDba(inputText)
        // 这里实现你调用大模型的逻辑
        // 返回表单实体对象
        return quesDba!!
    }


    val formDTO: FormDTO
        // 获取表单数据
        get() {
            val tableName = tableNameField!!.text
            val tabEngName = tableEnglishNameField!!.text
            val dbType = dbTypeComboBox!!.selectedItem as String
            val dbName = dbNameField!!.text
            val fields = fieldsTableModel?.fields
            return FormDTO(tableName, tabEngName, dbType, dbName, fields!!)
        }

    // 右下角的按钮（确定、取消）
    override fun createSouthPanel(): JComponent {
        val buttons = JPanel()
        val okButton = JButton("OK")
        okButton.addActionListener { close(OK_EXIT_CODE) }
        buttons.add(okButton)
        val cancelButton = JButton("Cancel")
        cancelButton.addActionListener { close(CANCEL_EXIT_CODE) }
        buttons.add(cancelButton)
        return buttons
    }
}