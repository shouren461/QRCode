package com.example.qrcode.functions.createFunction.input.strategies

import com.example.qrcode.R
import com.example.qrcode.functions.createFunction.CreateType
import com.example.qrcode.functions.createFunction.input.strategies.BaseAppStrategy
import com.example.qrcode.functions.createFunction.input.FieldConfig
import com.example.qrcode.functions.createFunction.input.FieldType
import com.example.qrcode.functions.createFunction.input.ProtocolStrategy

class WhatsAppStrategy: BaseAppStrategy(
    appName = "whatsapp",
    appIcon = R.mipmap.ic_whatsapp,
    appTabs = emptyList(),
    openUrl = ""  //不显示跳转按钮
){
    override val type: CreateType  = CreateType.WhatsApp

    override var selectedTab: String  = ""
//只需要一个电话号码输入框
    override fun getFields(): List<FieldConfig>  = listOf(
    FieldConfig("appInput","Phone Number", iconRes = R.mipmap.ic_whatsapp, type = FieldType.INPUT, require = true)
    )
//使用标准的WhatsApp短链接格式
    override fun encode(
        values: Map<String, String>,
        switches: Map<String, Boolean>
    ): String {
        return "https://wa.me/${values["appInput"] ?: ""}"
    }
}