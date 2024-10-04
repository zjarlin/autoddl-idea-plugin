package com.addzero.addl.settings

import com.intellij.openapi.components.Service
import java.awt.*
import javax.swing.*

@Service
class MyPluginSettingsComponent {
    val panel: JPanel = JPanel(GridBagLayout())
    private val aliLingjiModelKeyField: JTextField

    init {
        val constraints = GridBagConstraints().apply {
            fill = GridBagConstraints.HORIZONTAL
//            insets = Insets(10, 10, 10, 10) // 设置内边距
            weightx = 1.0 // 组件占据水平空间
        }

        // 阿里的灵积模型 Key 设置
        constraints.gridx = 0
        constraints.gridy = 0
        panel.add(JLabel("阿里的灵积模型 Key 设置:"), constraints)

        constraints.gridx = 1
        aliLingjiModelKeyField = JTextField(20) // 设置文本框宽度
        panel.add(aliLingjiModelKeyField, constraints)

        // 设置面板背景和边框
        panel.border = BorderFactory.createTitledBorder("插件设置")
    }

    var aliLingjiModelKey: String?
        get() = aliLingjiModelKeyField.text
        set(value) {
            aliLingjiModelKeyField.text = value
        }
}