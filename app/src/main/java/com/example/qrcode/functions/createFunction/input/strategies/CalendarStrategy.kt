package com.example.qrcode.functions.createFunction.input.strategies

import com.example.qrcode.functions.createFunction.CreateType
import com.example.qrcode.functions.createFunction.input.FieldConfig
import com.example.qrcode.functions.createFunction.input.FieldType
import com.example.qrcode.functions.createFunction.input.ProtocolStrategy
import java.text.SimpleDateFormat
import java.util.Locale

//日历事件二维码生成策略()
class CalendarStrategy: ProtocolStrategy {

    override val type: CreateType = CreateType.Calendar

    override var selectedTab: String = ""
//定义字段：标题(必填)，地点，全天开关，开始时间，结束时间，事件描述
    override fun getFields(): List<FieldConfig>  = listOf(
    FieldConfig("title","Please Enter something", label = "Title", type = FieldType.INPUT_NO_ICON, require = true),
    FieldConfig("location","Please Enter something", label = "Location", type = FieldType.INPUT_NO_ICON,initiallyVisibility = false),
    FieldConfig(key = "allDay", label = "AllDay", type = FieldType.SWITCH,defaultValue = "false"),
    FieldConfig("startTime", label = "Start", type = FieldType.DATETIME, defaultValue = "Jan 8 9:30", require = true),
    FieldConfig("endTime", label = "End", type = FieldType.DATETIME, defaultValue = "11:30", require = true),
    FieldConfig(key = "description","Please Enter something","Description",type = FieldType.MULTILINE)
    )

//按照标准iCalendar格式(VEVENT)格式拼接
    override fun encode(
        values: Map<String, String>,
        switches: Map<String, Boolean>
    ): String {
        val event = values["event"] ?: ""
        val start = formatToEventTime(values["start"] ?: "")
        val end = formatToEventTime(values["end"] ?: "")
       return listOf(
           "BEGIN:VEVENT",
           "SUMMARY:${values["title"] ?: ""}",
           "LOCATION:${values["location"] ?: ""}",
           "DTSTART:${start}",
           "DTEND:${end}",
           "DESCRIPTION:${values["description"] ?: ""}",
           "ALL_DAY:${switches["allDay"] ?: false}",
           "END:VEVENT"
       ).joinToString("\n")
    }
    //将2026-06-22 14:00:00 转换成 20260622140000Z
    private fun formatToEventTime(raw: String): String{
        return  try {
            val date = SimpleDateFormat("yyyy-M-d HH:mm", Locale.getDefault()).parse(raw)
            SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'", Locale.getDefault()).format(date)
        }catch (e: Exception){
            ""
        }
    }
}