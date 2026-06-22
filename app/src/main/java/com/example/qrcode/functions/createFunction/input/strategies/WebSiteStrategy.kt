package com.example.qrcode.functions.createFunction.input.strategies

import com.example.qrcode.R
import com.example.qrcode.functions.createFunction.CreateType
import com.example.qrcode.functions.createFunction.input.FieldConfig
import com.example.qrcode.functions.createFunction.input.FieldType
import com.example.qrcode.functions.createFunction.input.ProtocolStrategy

//网址二维码生成策略
class WebSiteStrategy: ProtocolStrategy {
    override val type: CreateType = CreateType.WebSite

    override var selectedTab: String  = ""
    //只需要一个URL输入框
    override fun getFields(): List<FieldConfig>  = listOf(
        FieldConfig(
            key = "url",
            hint = "Please enter something",
            iconRes = R.mipmap.ic_url_unselected,
            type = FieldType.QUICK_INPUT,
            require = true
        )
    )
    //直接返回用户输入的网址字符串即可
    override fun encode(
        values: Map<String, String>,
        switches: Map<String, Boolean>
    ): String {
        return values["url"] ?: ""
    }

}