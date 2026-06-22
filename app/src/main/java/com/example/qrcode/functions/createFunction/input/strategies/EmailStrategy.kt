package com.example.qrcode.functions.createFunction.input.strategies

import com.example.qrcode.R
import com.example.qrcode.functions.createFunction.CreateType
import com.example.qrcode.functions.createFunction.input.FieldConfig
import com.example.qrcode.functions.createFunction.input.FieldType
import com.example.qrcode.functions.createFunction.input.ProtocolStrategy

//EMail二维码生成策略
class EmailStrategy: ProtocolStrategy {
    override val type: CreateType  = CreateType.EMail

    override var selectedTab: String  = ""

    //定义字段:收件人邮箱(必填),主题，正文(多行)
    override fun getFields(): List<FieldConfig> =listOf(
        FieldConfig("email","E-mail", iconRes = R.mipmap.ic_create_email, require = true),
        FieldConfig("subject","Please Enter something","Subject",type = FieldType.INPUT_NO_ICON, initiallyVisibility = false),
        FieldConfig(key = "content", hint ="Please Enter something", label = "Content", type = FieldType.MULTILINE, initiallyVisibility = false)
    )

    //按照mailto协议格式拼接:mailto:邮箱?subject = 主题&body=正文,采用简单换行分割
    override fun encode(
        values: Map<String, String>,
        switches: Map<String, Boolean>
    ): String {
        return listOf(
            "mailto:${values["email"] ?: ""}",
            "mailto:${values["subject"] ?: ""}",
            "mailto:${values["content"] ?: ""}",
        ).joinToString {"\n"}
    }
}