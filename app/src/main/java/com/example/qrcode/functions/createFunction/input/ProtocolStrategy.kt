package com.example.qrcode.functions.createFunction.input

import android.content.Context
import com.example.qrcode.functions.createFunction.CreateType

//协议生成策略接口
interface ProtocolStrategy {
    val type: CreateType
    //关于社交类App的tab切换
    val tabs: List<String>get() = emptyList()
    var selectedTab: String
    val showOpenUrl: Boolean get() = false //默认不显示图标
    val targetUrl: String get() = ""  //需要跳转的网站链接

    val showAppIcon: Boolean get() = false //是否显示APP图标
    val appIconRes:Int get() = 0  //图标资源默认显为0

    //获取当前业务需要的字段配置列表
    fun getFields(): List<FieldConfig>
    //根据输入的数据Map生成协议字符串
    fun encode(values: Map<String, String>,switches: Map<String, Boolean>): String
}