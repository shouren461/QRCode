package com.example.qrcode.functions.historyFunction
import com.example.qrcode.database.HistoryRecord

//UI数据模型，分为分组标题项 和 历史记录内容项
sealed class DisplayItem {
    //分组标题项
    data class Header(
        val title: String,
        val showALLView: Boolean  = false //是否显示View all 按钮
    ): DisplayItem()
    //具体的历史记录项，将数据库实体和UI状态(是否选中，是否处于选择模式)封装在一起
    data class Record(
        val historyRecordItem: HistoryRecord,
        val isSelected: Boolean = false,
        val isEditMode: Boolean = false,
        val formattedTime: String  ?= null  //格式化后的显示时间如"11:51"
    ): DisplayItem()
}