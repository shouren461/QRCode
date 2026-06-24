package com.example.qrcode.functions.scanFunction

import android.app.Application
import androidx.camera.core.AspectRatio
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.qrcode.database.HistoryRecord
import com.example.qrcode.database.HistoryRecordDB
import com.example.qrcode.functions.createFunction.CreateType
import com.example.qrcode.utils.SingleScanEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

//ScanViewModel:主要负责扫描界面的逻辑处理,包括UI层的(闪光灯/缩放),数据跳转/入库
class ScanViewModel(application: Application): AndroidViewModel(application) {
    //获取数据库访问实例Dao
    private val historyRecordDao = HistoryRecordDB.getDatabase(application).historyRecordDao()
    //定义闪光灯，缩放比例，是否跳转到结果页，以及是否还继续扫描状态
    private val _flashState = MutableLiveData(false)
    val flashState: LiveData<Boolean>  =_flashState
    private val  _zoomRaio = MutableLiveData(0f)
    val zoomRatio: LiveData<Float> = _zoomRaio
    private val _navigateToResult = SingleScanEvent.singleScanEvent<Pair<HistoryRecord, Long>>()
    val navigateToResult: LiveData<Pair<HistoryRecord, Long>> = _navigateToResult

    var isProcessing: Boolean ?= false //判断当前帧线程池是否还有待处理的资源

    //切换闪光灯状态
     fun toggleFlashState(){
         _flashState.value = !(_flashState.value ?: false)
     }
    //更新缩放比例:范围限定在0-1倍
    fun updateZoomState(ratio: Float){
        _zoomRaio.value =ratio.coerceIn(0f,1f)
    }
    //处理扫描结果
    fun processScanResult(rawText: String){
        //如果是正在处理，则直接拦截
        if (isProcessing == true) return
        isProcessing = true
        //关于异步处理数据
        val qrType = judgeTypeByText(rawText)
        val historyRecordItem = HistoryRecord(
            title = "Scan Result:${rawText.take(15)}",
            content = rawText,
            category = qrType
        )
        //将扫描数据异步存入数据库，并获取这条记录的id
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                val historyRecordId =  historyRecordDao.insertHistoryRecord(historyRecordItem)
                //在非主线程中修改LiveData的值必须使用PostValue
                _navigateToResult.postValue(historyRecordItem to historyRecordId)  //跳转到对应结果页
            }
        }


    }
    fun judgeTypeByText(text: String): CreateType{
        return when{
            text.startsWith("http://") -> CreateType.WebSite
            text.startsWith("WIFI:") -> CreateType.Wifi
            text.startsWith("TEL:") -> CreateType.Tel
            text.startsWith("SMSTO:") -> CreateType.SMS
            else -> CreateType.Text
        }
    }

    //重置处理锁 ，允许进行下一次扫描
    fun resetProcessing(){
        isProcessing = false
    }

}