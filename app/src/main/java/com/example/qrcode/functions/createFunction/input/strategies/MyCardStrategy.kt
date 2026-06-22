package com.example.qrcode.functions.createFunction.input.strategies

import com.example.qrcode.R
import com.example.qrcode.functions.createFunction.CreateType
import com.example.qrcode.functions.createFunction.input.FieldConfig
import com.example.qrcode.functions.createFunction.input.FieldType
import com.example.qrcode.functions.createFunction.input.ProtocolStrategy

//个人名片二维码生成策略(MECARD协议)
class MyCardStrategy: ProtocolStrategy {

    override val type: CreateType = CreateType.MyCard
    override var selectedTab: String  = ""

//定义字段:姓名(必填)，电话，邮件，地址，生日，组织，备注
    override fun getFields(): List<FieldConfig>  = listOf(
    FieldConfig("name","Name", iconRes = R.mipmap.ic_create_name, require = true),
    FieldConfig("phone","phone Number", iconRes = R.mipmap.ic_create_phone),
    FieldConfig("email","E-mail", iconRes = R.mipmap.ic_create_email),
    FieldConfig("address","Address", iconRes = R.mipmap.ic_create_address),
    FieldConfig("birthday", iconRes = R.mipmap.ic_create_birthday, label = "BirthDay", type = FieldType.DATE, defaultValue = "Jan 8"),
    FieldConfig("organization","Org", iconRes = R.mipmap.ic_create_org),
    FieldConfig("note","Please enter something", label = "Note",type = FieldType.MULTILINE)
    )
//按照MECARD标准格式拼接 ->MECARD:N:姓名;TEL:电话;...
    override fun encode(
        values: Map<String, String>,
        switches: Map<String, Boolean>
    ): String {
        return listOf(
            "MECARD:N:${values["name"] ?: ""}",
            "TEL:${values["phone"] ?: ""}",
            "EMAIL:${values["email"] ?: ""}",
            "ADR:${values["address"] ?: ""}",
            "BDAY:${values["birthday"] ?: ""}",
            "ORG:${values["organization"] ?: ""}",
            "NOTE:${values["note"] ?: ""}"
        ).joinToString("\n")
    }
}