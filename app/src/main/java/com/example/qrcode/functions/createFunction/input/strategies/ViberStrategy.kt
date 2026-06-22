package com.example.qrcode.functions.createFunction.input.strategies

import com.example.qrcode.R
import com.example.qrcode.functions.createFunction.CreateType
import com.example.qrcode.functions.createFunction.input.strategies.BaseAppStrategy
import com.example.qrcode.functions.createFunction.input.FieldConfig
import com.example.qrcode.functions.createFunction.input.FieldType
import com.example.qrcode.functions.createFunction.input.ProtocolStrategy

class ViberStrategy: BaseAppStrategy(
    appName = "viber",
    appIcon = R.mipmap.ic_viber,
    appTabs = emptyList(),
    openUrl = ""
) {
    override val type: CreateType = CreateType.Viber

    override var selectedTab: String = ""
//定义字段:电话号码
    override fun getFields(): List<FieldConfig> = listOf(
    FieldConfig(key = "appInput", hint = "Phone number", iconRes = R.mipmap.ic_viber, type = FieldType.INPUT, require = true)
    )
//拼接协议:使用Viber特有的聊天启动协议
    override fun encode(
        values: Map<String, String>,
        switches: Map<String, Boolean>
    ): String {
        return "viber://chat?number=${values["appInput"] ?: ""}"
    }
}