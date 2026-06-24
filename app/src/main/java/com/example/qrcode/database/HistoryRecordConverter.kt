package com.example.qrcode.database

import androidx.room.TypeConverter
import com.example.qrcode.functions.createFunction.CreateType

class HistoryRecordConverter {
    //将枚举类型转化为String类型存入数据库
    @TypeConverter
    fun typeToString(type: CreateType): String{
        return type.name
    }
    //将String类型转化为枚举类型，如果从数据库读取的字符串不合法则默认退回Text类型
    @TypeConverter
    fun stringToType(name: String) : CreateType{
        return try {
            CreateType.valueOf(name)
        }
        catch (e: Exception){
            CreateType.Text
        }
    }

}