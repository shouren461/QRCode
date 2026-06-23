package com.example.qrcode.functions.createFunction

import android.app.Application
import androidx.annotation.StringRes
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.qrcode.database.HistoryRecord
import com.example.qrcode.database.HistoryRecordDB
import com.example.qrcode.database.HistoryRecordDao
import com.example.qrcode.functions.createFunction.input.ProtocolStrategy
import com.example.qrcode.functions.createFunction.input.StrategyFactory
import com.example.qrcode.functions.historyFunction.DisplayItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

//二维码创建ViewModel->持有界面数据，并处理核心业务(选择类型，生成协议内容等)
class CreateViewModel(application: Application) : AndroidViewModel(application) {
    //当前正在使用的二维码策略->观察者模式，当策略改变时，UI自动重绘
    private val _strategy = MutableLiveData<ProtocolStrategy?> ()
    val strategy: LiveData<ProtocolStrategy?> = _strategy
    private val historyRecordDao = HistoryRecordDB.getDatabase(application).historyRecordDao()
    //生成的最终协议结果(用于跳转结果页)
    private val _createResult = MutableLiveData<String>()
    val createResult: LiveData<String> = _createResult
    //初始化或者切换当前要创建的二维码类型
    fun selectType(type: CreateType){
        _strategy.value = StrategyFactory.getStrategy(type)
    }
    //执行协议生成策略->调用当前策略的encode()方法,将界面收集到的values和switchs转换成协议字符串
    fun buildContent(values: Map<String, String> ,switches: Map<String, Boolean>){
        val currentStrategy = _strategy.value ?: return
        val content = currentStrategy.encode(values, switches)
        if (content.isEmpty()){
            _createResult.value = ""
            return
        }

            viewModelScope.launch {
                val title  = generateHistoryTitle(values,content)
                val historyRecord  = HistoryRecord(
                    title = title,
                    content = content,
                    category = currentStrategy.type
                )
                withContext(Dispatchers.IO){
                    historyRecordDao.insertHistoryRecord(historyRecord)
                }
            }
        _createResult.value =  content
        }
    }
    //根据输入内容给较长的字符串截取一部分文字
    private fun generateHistoryTitle(values: Map<String, String>, content: String): String{
        return values["SSID"] ?: values["Text"] ?: values["URL"] ?: values["Name"] ?: content.take(20)
    }
