package com.example.qrcode.functions.createFunction

import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.qrcode.functions.createFunction.input.ProtocolStrategy
import com.example.qrcode.functions.createFunction.input.StrategyFactory

//二维码创建ViewModel->持有界面数据，并处理核心业务(选择类型，生成协议内容等)
class CreateViewModel : ViewModel(){
    //当前正在使用的二维码策略->观察者模式，当策略改变时，UI自动重绘
    private val _strategy = MutableLiveData<ProtocolStrategy?> ()
    val strategy: LiveData<ProtocolStrategy?> = _strategy
    //生成的最终协议结果(用于跳转结果页)
    private val _createResult = MutableLiveData<String>()
    val createResult: LiveData<String> = _createResult
    //初始化或者切换当前要创建的二维码类型
    fun selectType(type: CreateType){
        _strategy.value = StrategyFactory.getStrategy(type)
    }
    //执行协议生成策略->调用当前策略的encode()方法,将界面收集到的values和switchs转换成协议字符串
    fun buildContent(values: Map<String, String> ,switches: Map<String, Boolean>){
        _strategy.value?.let {
            val content = it.encode(values, switches)
           _createResult.value =  content
        }
    }
}