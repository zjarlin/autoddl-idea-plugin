package com.addzero.addl.settings

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage

@State(name = "MyPluginSettings", storages = [Storage("MyPluginSettings.xml")])
@Service
class MyPluginSettings : PersistentStateComponent<MyPluginSettings.State> {
    class State {
//        var tablePrefix: String = "biz_" // 表前缀默认值
        var aliLingjiModelKey: String = ""  // 阿里的灵积模型 Key 默认值
    }

    private var myState: State = State()

    override fun getState(): State {
        return myState
    }

    override fun loadState(state: State) {
        this.myState = state
    }

    companion object {
        val instance: MyPluginSettings
            get() = ApplicationManager.getApplication().getService(MyPluginSettings::class.java)
    }
}