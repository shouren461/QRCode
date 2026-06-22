package com.example.qrcode.functions.createFunction.input.strategies

import com.example.qrcode.R
import com.example.qrcode.functions.createFunction.CreateType
import com.example.qrcode.functions.createFunction.input.strategies.BaseAppStrategy
import com.example.qrcode.functions.createFunction.input.FieldConfig
import com.example.qrcode.functions.createFunction.input.FieldType

class PaypalStrategy: BaseAppStrategy(
    appName = "paypal",
    appIcon = R.mipmap.ic_paypal,
    appTabs = listOf("Me Link","Me Username"),
    appTabHints = mapOf("Me Link" to "Enter Paypal me link","Me Username" to "Enter Paypal username"),
    openUrl = "https://www.paypal.com"
) {
    override val type: CreateType
        get() = CreateType.PayPal

    override fun getFields(): List<FieldConfig> {
        val hint = appTabHints[selectedTab] ?: "Enter $selectedTab"
        return listOf(
            FieldConfig(
                key = "appInput",
                hint = hint,
                type = FieldType.QUICK_INPUT_NO_ICON, //使用快捷输入无图标方式
                require = true
            )
        )
    }
    
//重写协议生成逻辑:统一跳转到paypal.me链接
    override fun encode(values: Map<String, String>, switches: Map<String, Boolean>): String {
        return "https://palpal.me/${values["appInput"] ?: ""}"
    }
}