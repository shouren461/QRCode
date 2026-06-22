package com.example.qrcode.functions.createFunction.input.strategies

import com.example.qrcode.R
import com.example.qrcode.functions.createFunction.CreateType
import com.example.qrcode.functions.createFunction.input.strategies.BaseAppStrategy
import com.example.qrcode.functions.createFunction.input.FieldConfig
import com.example.qrcode.functions.createFunction.input.FieldType
import com.example.qrcode.functions.createFunction.input.ProtocolStrategy
//Spotify二维码生成策略
class SpotifyStrategy: BaseAppStrategy(
    appName = "spotify",
    appIcon = R.mipmap.ic_spotify,
    appTabs = emptyList(),
    openUrl = "https://www.spotify.com" //
){
    override val type: CreateType = CreateType.Spotify

    override var selectedTab: String  = ""
//定义字段:艺人名称
    override fun getFields(): List<FieldConfig>  = listOf(
    FieldConfig(key = "appInput", hint = "Artist name", iconRes = R.mipmap.ic_spotify, type = FieldType.INPUT, require = true)
    )
//使用spotify自定义协议格式
    override fun encode(
        values: Map<String, String>,
        switches: Map<String, Boolean>
    ): String {
       return  "spotify:${values["appInput"] ?: ""}"
    }
}