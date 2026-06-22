package com.example.qrcode.functions.createFunction.input.strategies

import com.example.qrcode.R
import com.example.qrcode.functions.createFunction.CreateType
import com.example.qrcode.functions.createFunction.input.FieldConfig
import com.example.qrcode.functions.createFunction.input.FieldType
import com.example.qrcode.functions.createFunction.input.ProtocolStrategy
//短信(SMS)二维码生成策略
class SMSStrategy : ProtocolStrategy {
    override val type: CreateType = CreateType.SMS
    override var selectedTab: String = ""
//定义字段：手机号(必填)，短信内容:(多行)
    override fun getFields(): List<FieldConfig>  = listOf(
    FieldConfig("phone","Phone number", iconRes = R.mipmap.ic_create_phone, require = true),
    FieldConfig(key = "message",hint = "Please Enter something", label = "Message",type = FieldType.MULTILINE)
    )

    //按照SMSTO 的协议格式拼接;->SMSTO:手机号:短信内容
    override fun encode(
        values: Map<String, String>,
        switches: Map<String, Boolean>
    ): String {
        return "SMSTO:${values["phone" ?: ""]}:${values["message"] ?: ""}"
    }
}