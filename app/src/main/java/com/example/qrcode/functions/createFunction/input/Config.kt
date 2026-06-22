package com.example.qrcode.functions.createFunction.input

data class FieldConfig(
    val key: String,
    val hint: String = "",
    val label: String ?= null,
    val iconRes: Int = 0,
    val type: FieldType  = FieldType.INPUT,
    val require: Boolean = true,
    val options: List<String>  =emptyList(),
    val defaultValue: String = "",
    val initiallyVisibility: Boolean = true,
    val quickOptions: List<String> = listOf("www.",".com")
)
enum class FieldType{
    INPUT,INPUT_NO_ICON,MULTILINE,TEXT_COUNTER,DROPDOWN,SWITCH,DATE,DATETIME,QUICK_INPUT, QUICK_INPUT_NO_ICON
}

