package com.example.qrcode.functions.createFunction.input.strategies

import com.example.qrcode.R
import com.example.qrcode.functions.createFunction.CreateType
import com.example.qrcode.functions.createFunction.input.FieldConfig
import com.example.qrcode.functions.createFunction.input.ProtocolStrategy

//联系人二维码生成策略
class ContactStrategy: ProtocolStrategy {
    override val type: CreateType = CreateType.Contact
    override var selectedTab: String = ""

    override fun getFields(): List<FieldConfig> = listOf(
        FieldConfig("name", "Name", iconRes = R.mipmap.ic_create_name, require = true),
        FieldConfig("phone1", "Phone Number", iconRes = R.mipmap.ic_create_phone, require = true),
        FieldConfig("phone2", "Phone Number2", iconRes = R.mipmap.ic_create_phone, require = true)
    )
    //按照标准格式vCard格式拼接
    /*
    BEGIN:"VCARD",
    FN:"姓名",
    "TEL":电话,
    END:"VCARD"
    * */
    override fun encode(
        values: Map<String, String>,
        switches: Map<String, Boolean>
    ): String {
        return listOf(
            "BEGIN:VCARD",
            "FN:${values["name"] ?: ""}",
            "TEL:${values["phone1"] ?: ""}",
            "TEL:${values["phone2"] ?: ""}",
            "END:VCARD"
        ).joinToString {"\n"}
    }
}