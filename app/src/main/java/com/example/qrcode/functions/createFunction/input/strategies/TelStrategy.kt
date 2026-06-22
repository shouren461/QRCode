package com.example.qrcode.functions.createFunction.input.strategies

import com.example.qrcode.R
import com.example.qrcode.functions.createFunction.CreateType
import com.example.qrcode.functions.createFunction.input.FieldConfig
import com.example.qrcode.functions.createFunction.input.ProtocolStrategy
//电话(TEL)生成策略
class TelStrategy: ProtocolStrategy {
    override val type: CreateType  = CreateType.Tel
    override var selectedTab: String = ""

    //只需要一个手机号输入框
    override fun getFields(): List<FieldConfig>  = listOf(
        FieldConfig(
            key = "phone",
            hint =  "Phone Number",
            iconRes = R.mipmap.ic_create_phone,
            require = true
        )
    )
    //按照标准电话协议拼接 tel:手机号
    override fun encode(
        values: Map<String, String>,
        switches: Map<String, Boolean>
    ): String {
        return "tel:${values["phone"] ?: ""}"
    }

}