package com.example.qrcode.functions.createFunction.input.strategies

import com.example.qrcode.R
import com.example.qrcode.functions.createFunction.CreateType
import com.example.qrcode.functions.createFunction.input.FieldConfig
import com.example.qrcode.functions.createFunction.input.FieldType
import com.example.qrcode.functions.createFunction.input.ProtocolStrategy
//Wifi二维码生成策略
class WifiStrategy: ProtocolStrategy {
    override val type: CreateType = CreateType.Wifi
    override var selectedTab: String  = ""
    //Wifi需要三个字段，网络名称，密码，加密类型(WPA/WAP2,"WEP","NONE")
    override fun getFields(): List<FieldConfig>  = listOf(
        FieldConfig("wifiName","WiFi Name", iconRes = R.mipmap.ic_create_wifi, type = FieldType.INPUT,require = true),
        FieldConfig("security","Security", iconRes = R.mipmap.ic_create_security, type = FieldType.DROPDOWN,require = true, options = listOf("WPA/WPA2","WEP","NONE"), defaultValue = "WPA/WPA2"),
        FieldConfig("passWord","PassWord", iconRes = R.mipmap.ic_create_password, type = FieldType.INPUT,require = true)
    )

    //按照标准Wifi协议拼接，Wifi:T 加密类型 S:账号 P密码
    override fun encode(
        values: Map<String, String>,
        switches: Map<String, Boolean>
    ): String {
        val ssid = values["wifiName"] ?: ""
        val pass = values["password"] ?: ""
        val sec = values["security"] ?: "WPA"
        return "WIFI:T$sec;S:$ssid;P:$pass;;"
    }
}