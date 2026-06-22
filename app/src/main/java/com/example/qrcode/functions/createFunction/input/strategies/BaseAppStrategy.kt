package com.example.qrcode.functions.createFunction.input.strategies

import com.example.qrcode.functions.createFunction.input.FieldConfig
import com.example.qrcode.functions.createFunction.input.FieldType
import com.example.qrcode.functions.createFunction.input.ProtocolStrategy

abstract class BaseAppStrategy(
    protected val appName: String,
    protected val appIcon: Int,
    protected val appTabs: List<String>  =listOf("UserName,URL"),
    protected val appTabHints:Map<String, String> = emptyMap(),
    //需要跳转的网页链接
    private val openUrl: String = ""
    ): ProtocolStrategy {
    //重写接口tabs文件告知Activity有tab切换组件要展示
    override val tabs: List<String> get() = appTabs

    override var selectedTab: String  = appTabs.firstOrNull() ?: ""
    //判断一下是否有友需要跳转的网页链接
    override val showOpenUrl: Boolean = openUrl.isNotEmpty()
    override val targetUrl: String = openUrl

    //判断一下是否需要单独显示图标资源
    override val showAppIcon: Boolean = true //默认需要显示图标资源
    override val appIconRes: Int  = appIcon

    override fun getFields(): List<FieldConfig> {
        val hint = appTabHints[selectedTab] ?:"Enter $selectedTab"
        return listOf(
            FieldConfig(
                key = "appInput",
                hint = hint,
                iconRes = appIcon,
                type = FieldType.INPUT,
                require = true
            )
        )
    }

    override fun encode(values: Map<String, String>, switches: Map<String, Boolean>): String {
        val input = values["appInput"] ?: ""
        return if (selectedTab == "URL" || input.startsWith("https")){
            input
        }else{
            "$appName:$input"
        }
    }
}