package com.example.qrcode.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.qrcode.functions.createFunction.CreateType

//历史记录实体类
@Entity(tableName = "HISTORY_RECORD_TABLE")
data class HistoryRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val content: String,
    val category: CreateType,
    val isFavorite: Boolean = false,
    val timeStamp: Long = System.currentTimeMillis(),
    val favoriteTime: Long? = null
)
