package com.addzero.addl.settings

import com.intellij.openapi.options.Configurable
import org.jetbrains.annotations.Nls
import javax.swing.JComponent

class MyPluginSettingsConfigurable : Configurable {
    private var settingsComponent: MyPluginSettingsComponent? = null

    @Nls(capitalization = Nls.Capitalization.Title)
    override fun getDisplayName(): String {
        return "AutoDDL设置"
    }

    override fun createComponent(): JComponent? {
        settingsComponent = MyPluginSettingsComponent()
        return settingsComponent!!.panel
    }

    override fun isModified(): Boolean {
        val settings: MyPluginSettings = MyPluginSettings.instance
        return settingsComponent!!.aliLingjiModelKey != settings.state!!.aliLingjiModelKey
    }

    override fun apply() {
        val settings: MyPluginSettings = MyPluginSettings.instance
        // 只更新阿里的灵积模型 Key 设置
        settings.state!!.aliLingjiModelKey = settingsComponent!!.aliLingjiModelKey!!
    }

    override fun reset() {
        val settings: MyPluginSettings = MyPluginSettings.instance
        // 只重置阿里的灵积模型 Key 设置
        settingsComponent?.aliLingjiModelKey = settings.state!!.aliLingjiModelKey
    }

    override fun disposeUIResources() {
        settingsComponent = null
    }
}