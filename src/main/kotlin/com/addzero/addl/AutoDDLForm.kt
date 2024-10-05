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
import com.intellij.ui.components.JBTabbedPane
import com.intellij.ui.table.JBTable
import quesDba
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.GridLayout
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.*

private const val s = "问问大模型"

class AutoDDLForm(project: Project?) : DialogWrapper(project) {
    private var mainPanel: JPanel? = null
    private var tableNameField: JTextField? = null
    private var tableEnglishNameField: JTextField? = null
    private var dbTypeComboBox: ComboBox<String>? = null
    private var dbNameField: JTextField? = null
    var fieldsTable: JBTable? = null
    private var fieldsTableModel: FieldsTableModel? = null
    private var llmPanel: JPanel? = null // 存放LLM相关内容的面板

    // 新增成员变量
    private lateinit var tabbedPane: JTabbedPane
    private lateinit var panelGenerateDDL: JPanel
    private lateinit var panelFunction1: JPanel
    private lateinit var panelFunction2: JPanel

    init {
        title = "Generate DDL"
        init()
    }

    override fun createCenterPanel(): JComponent? {
        mainPanel = JPanel(BorderLayout())
        tabbedPane = JBTabbedPane()

        // 创建标签页
        panelGenerateDDL = createGenerateDDLPanel()
//        panelFunction1 = createFunction1Panel()
//        panelFunction2 = createFunction2Panel()

        tabbedPane.addTab("根据元数据生成建表语句", panelGenerateDDL)
//        tabbedPane.addTab("根据实体生成建表语句", panelFunction1)
//        tabbedPane.addTab("根据Jimmer实体生成建表语句", panelFunction2)

        mainPanel!!.add(tabbedPane, BorderLayout.CENTER)

        return mainPanel
    }

    private fun createGenerateDDLPanel(): JPanel {
        val panel = JPanel(BorderLayout())

        // 表单信息区域
        val formPanel = JPanel(GridLayout(4, 2))
        formPanel.add(JLabel("*表中文名:"))
        tableNameField = JTextField()
        formPanel.add(tableNameField)

        formPanel.add(JLabel("*数据库类型:"))
        dbTypeComboBox = ComboBox(arrayOf(MYSQL, POSTGRESQL, DM, ORACLE))
        formPanel.add(dbTypeComboBox)


        formPanel.add(JLabel("表名(可空,为空默认表中文名转拼音):"))
        tableEnglishNameField = JTextField()
        formPanel.add(tableEnglishNameField)

        formPanel.add(JLabel("数据库名称 (可空):"))
        dbNameField = JTextField()
        formPanel.add(dbNameField)

        // 字段信息区域
        val tablePanel = fieldsJPanel()

        // 添加表单信息区域和字段信息区域
        panel.add(formPanel, BorderLayout.NORTH)
        panel.add(tablePanel, BorderLayout.CENTER)

        // 添加高级功能折叠菜单
        addAdvancedPanel(panel)
        // 添加使用说明
        val usageInstruction =
            JLabel("""<html><font color='orange'>
            删除选中行：按住Ctrl(跳跃选)或Shift(连续选)，点击要删除的行，然后点击“删除选中行”按钮。
            <br>
            问问大模型：您可以说:创建一张xx表，包含xxx字段。
            </font></html>""")


        panel.add(usageInstruction, BorderLayout.SOUTH)



        return panel
    }

//    private fun fieldsJPanel(): JPanel {
//        fieldsTableModel = FieldsTableModel()
//        fieldsTable = JBTable(fieldsTableModel)
//
//        // 设置 Java 类型下拉框
//        val javaTypesEnum = javaTypesEnum
//        val javaTypeComboBox = ComboBox(javaTypesEnum)
//        fieldsTable!!.columnModel.getColumn(0).cellEditor = DefaultCellEditor(javaTypeComboBox)
//
//        // 启用单元格点击编辑模式
//        fieldsTable!!.surrendersFocusOnKeystroke = true
//        fieldsTable!!.addMouseListener(object : MouseAdapter() {
//            override fun mousePressed(e: MouseEvent) {
//                val row = fieldsTable!!.rowAtPoint(e.point)
//                val column = fieldsTable!!.columnAtPoint(e.point)
//                if (row == -1) {
//                    // 没有行时，添加空行
//                    fieldsTableModel?.addField(FieldDTO("", "", ""))
//                } else {
//                    // 有行时，编辑当前单元格
//                    fieldsTable!!.editCellAt(row, column)
//                    fieldsTable!!.editorComponent?.requestFocus()
//                }
//            }
//        })
//
//        val tablePanel = JPanel(BorderLayout())
//        tablePanel.add(JScrollPane(fieldsTable), BorderLayout.CENTER)
//        tablePanel.preferredSize = Dimension(600, 200)
//        return tablePanel
//    }

    private fun fieldsJPanel(): JPanel {
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

        // 允许多行选择
        fieldsTable!!.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION)

        // 删除选中行的按钮
        val deleteButton = JButton("删除选中行")
        deleteButton.addActionListener {
            deleteSelectedRows()  // 调用删除选中行的函数
        }

        val buttonPanel = JPanel() // 用于放置按钮
        buttonPanel.add(deleteButton)

        val tablePanel = JPanel(BorderLayout())
        tablePanel.add(JScrollPane(fieldsTable), BorderLayout.CENTER)
        tablePanel.add(buttonPanel, BorderLayout.SOUTH) // 添加按钮面板

        tablePanel.preferredSize = Dimension(600, 200)
        return tablePanel
    }

    private fun deleteSelectedRows() {
        val selectedRows = fieldsTable!!.selectedRows
        if (selectedRows.isNotEmpty()) {
            // 按倒序删除选中行，以避免索引问题
            for (i in selectedRows.size - 1 downTo 0) {
                fieldsTableModel!!.fields.removeAt(selectedRows[i])
//                fieldsTableModel!!.removeRow(selectedRows[i])
            }
            fieldsTableModel!!.fireTableDataChanged() // 通知表格模型数据已经更改
        }
    }

    private fun createFunction1Panel(): JPanel {
        // 在这里实现功能1的面板
        val panel = JPanel()
        panel.add(JLabel("暂未开放"))
        return panel
    }

    private fun createFunction2Panel(): JPanel {
        // 在这里实现功能2的面板
        val panel = JPanel()
        panel.add(JLabel("暂未开放"))
        return panel
    }

    private fun addAdvancedPanel(panel: JPanel) {
        val advancedPanelContainer = JPanel(BorderLayout())
        val toggleButton = JToggleButton("问问大模型(再次点击收起)", false)
        llmPanel = createLLMPanel() // 创建LLM面板

        // 折叠菜单的逻辑
        toggleButton.addActionListener {
            llmPanel?.isVisible = toggleButton.isSelected // 控制LLM面板显示与否
        }

        advancedPanelContainer.add(toggleButton, BorderLayout.NORTH)
        advancedPanelContainer.add(llmPanel!!, BorderLayout.CENTER) // 添加LLM面板

        // 添加到主面板
        mainPanel!!.add(advancedPanelContainer, BorderLayout.NORTH)
    }

//    private fun createLLMPanel(): JPanel {
//        val llmPanel = JPanel(BorderLayout())
//        val inputTextArea = JTextArea(5, 30) // 长文本框
//        val submitButton = JButton("使用LLM建议回填表单(响应较慢,耐心等待即可)")
//
//        submitButton.addActionListener {
//            // 这里调用大模型接口，并获取表单实体对象
//            val inputText = inputTextArea.text
//            val formEntity = callLargeModelApi(inputText) // 调用你的大模型接口
//            // 将表单数据回填到表单输入区域
//            tableNameField!!.text = formEntity.tableName
//            tableEnglishNameField!!.text = formEntity.tableEnglishName
//            dbTypeComboBox!!.selectedItem = formEntity.dbType
//            dbNameField!!.text = formEntity.dbName
//            // 这里确保字段可以二次编辑
//            fieldsTableModel!!.fields = formEntity.fields?.toMutableList() as MutableList<FieldDTO>
//            // 让用户可以编辑字段表格
//            fieldsTableModel!!.fireTableDataChanged()
//        }
//
//        llmPanel.add(inputTextArea, BorderLayout.CENTER)
//        llmPanel.add(submitButton, BorderLayout.SOUTH)
//        llmPanel.isVisible = false // 默认隐藏
//
//        return llmPanel
//    }

    private fun createLLMPanel(): JPanel {
        val llmPanel = JPanel(BorderLayout())
        val inputTextArea = JTextArea(5, 30) // 长文本框
        val submitButton = JButton("使用LLM建议回填表单(响应较慢,耐心等待即可)")
        val loadingLabel = JLabel("正在加载，请稍候...") // 显示加载状态的Label
        loadingLabel.isVisible = false // 初始状态为不可见

        submitButton.addActionListener {
            // 禁用按钮并显示加载状态
            submitButton.isEnabled = false
            loadingLabel.isVisible = true

            // 创建并执行异步任务
            val task = object : SwingWorker<FormDTO, Void>() {
                override fun doInBackground(): FormDTO {
                    val inputText = inputTextArea.text
                    return callLargeModelApi(inputText) // 调用你的大模型接口
                }

                override fun done() {
                    try {
                        // 获取表单实体对象并回填到表单输入区域
                        val formEntity = get()
                        tableNameField!!.text = formEntity.tableName
                        tableEnglishNameField!!.text = formEntity.tableEnglishName
                        dbTypeComboBox!!.selectedItem = formEntity.dbType
                        dbNameField!!.text = formEntity.dbName

                        // 确保字段可以二次编辑
                        fieldsTableModel!!.fields = formEntity.fields?.toMutableList() as MutableList<FieldDTO>
                        fieldsTableModel!!.fireTableDataChanged()
                    } catch (ex: Exception) {
                        ex.printStackTrace()
                        JOptionPane.showMessageDialog(llmPanel, "出现错误: ${ex.message}")
                    } finally {
                        // 任务完成后恢复按钮状态并隐藏加载状态
                        submitButton.isEnabled = true
                        loadingLabel.isVisible = false
                    }
                }
            }
            task.execute() // 开始执行异步任务
        }

        llmPanel.add(inputTextArea, BorderLayout.CENTER)
        llmPanel.add(loadingLabel, BorderLayout.NORTH) // 在北部添加加载状态的Label
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