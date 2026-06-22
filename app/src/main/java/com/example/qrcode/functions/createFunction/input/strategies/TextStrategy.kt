package com.example.qrcode.functions.createFunction.input.strategies

import com.example.qrcode.functions.createFunction.CreateType
import com.example.qrcode.functions.createFunction.input.FieldConfig
import com.example.qrcode.functions.createFunction.input.FieldType
import com.example.qrcode.functions.createFunction.input.ProtocolStrategy
//文本二维码生成策略
class TextStrategy: ProtocolStrategy {
    override val type: CreateType = CreateType.Text

    override var selectedTab: String = ""

    override fun getFields(): List<FieldConfig> = listOf(
        //只需要一个文本输入框，并启用字数统计功能
        FieldConfig(key = "text", hint = "Enter text", type = FieldType.TEXT_COUNTER, require = true)
    )
    //直接返回用户输入的原始文本
    override fun encode(values: Map<String, String>, switches: Map<String, Boolean>): String {
        return values["text"] ?:""
    }
}