package com.example.qrcode.functions.historyFunction

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import com.example.qrcode.database.HistoryRecord
import com.example.qrcode.database.HistoryRecordDB
import com.example.qrcode.functions.createFunction.CreateType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

//历史界面核心业务逻辑处理  ->历史列表的获取，筛选，排序，分组，以及模式切换(普通/编辑模式/收藏)，响应式设计,列表发生变化时UI 自动刷新
class HistoryViewModel(application: Application) : AndroidViewModel(application) {
    private val dao  = HistoryRecordDB.getDatabase(application).historyRecordDao()
    private val _rawList = MutableLiveData<List<HistoryRecord>>(emptyList())//从数据库获取的原始数据
    val filterTypes = MutableLiveData<Set<CreateType>>(emptySet()) //过滤器类型集合
    val isEditMode = MutableLiveData(false)
    val selectedIds = MutableLiveData<Set<Long>>(emptySet())//选中的id记录
    val isFavoritesMode = MutableLiveData(false) //是否处于收藏夹查看模式
    //响应式数据转换,只要任意数据发生变化，都需要重新刷新列表
    val displayList: LiveData<List<DisplayItem>>  = MediatorLiveData<List<DisplayItem>>().apply {
        //定义一个统一的刷新函数
        val updateAction = Observer<Any?> {
            val raw = _rawList.value ?:  emptyList()
            val filters = filterTypes.value ?:emptySet()
            val isEditMode = isEditMode.value ?: false
            val selections = selectedIds.value ?: emptySet()
            val isFavoritesMode = isFavoritesMode.value ?:false
            value = transformData(raw,filters,isEditMode,selections,isFavoritesMode)
        }
        //注册观察所有的数据源，由于MediatorLiveData需要特定的Observer,需要完成SAM转换
        addSource(_rawList){updateAction.onChanged(it) }
        addSource(filterTypes){updateAction.onChanged(it) }
        addSource(isEditMode){updateAction.onChanged(it) }
        addSource(selectedIds){updateAction.onChanged(it) }
        addSource(isFavoritesMode){updateAction.onChanged(it) }
    }
    //数据转化，将原数据转换成简单的列表序列
    private fun transformData(
        raw: List<HistoryRecord>,
        filters: Set<CreateType>,
        isEditMode: Boolean,
        selections: Set<Long>,
        isFavoritesMode: Boolean
    ): List<DisplayItem>? {
        val result = mutableListOf<DisplayItem>()
        //根据二维码类型进行筛选
        val filtered = if (filters.isEmpty()) raw else raw.filter { filters.contains(it.category)}
        //处理显示模式
        if (isFavoritesMode){
            //如果是收藏夹详情界面,只处理收藏的数据并按日期分组
            val fav = filtered.filter { it.isFavorite }
            groupAndAddToCollection(fav,result,isEditMode,selections)
        }else{
            //普通模式,收藏记录置顶3条记录
            val favorites = filtered.filter { it.isFavorite}.sortedByDescending { it.favoriteTime?: it.timeStamp }
            if (favorites.isNotEmpty()){
                result.add(DisplayItem.Header("Favorites",favorites.size > 3))
                favorites.take(3).forEach {
                    result.add(DisplayItem.Record(it,selections.contains(it.id),isEditMode,formatTime(it.timeStamp)))
                }
            }
            //常规历史记录，按日期分组展示
            groupAndAddToCollection(filtered,result,isEditMode,selections)
        }
        return result
    }

    //分组方法:将标题和历史记录按照日期分组并添加到对应的标题集合或者历史记录集合中
    private fun groupAndAddToCollection(
        list: List<HistoryRecord>,
        result: MutableList<DisplayItem>,
        isEditMode: Boolean,
        selections: Set<Long>
    ) {
        val grouped = list.groupBy { formatDate(it.timeStamp)}
        grouped.forEach { (date, historyRecordItems) ->
            result.add(DisplayItem.Header(date))
            historyRecordItems.forEach {
                result.add(DisplayItem.Record(it,selections.contains(it.id),isEditMode,formatTime(it.timeStamp)))
            }
        }
    }
    //格式化标题项日期时间  "Dec 27,2020"的格式
    private fun formatDate(time: Long) : String{
        val today = Calendar.getInstance()
        val target = Calendar.getInstance().apply { timeInMillis = time }
        return if(today.get(Calendar.YEAR) == target.get(Calendar.YEAR) && today.get(Calendar.DAY_OF_YEAR) == target.get(Calendar.DAY_OF_YEAR)) "Today"
        else SimpleDateFormat("MMM d,yyyy", Locale.US).format(time)
    }
    //格式化24小时制时间
    private fun formatTime(timeStamp: Long): String? {
        return SimpleDateFormat("HH:mm", Locale.US).format(timeStamp)
    }

    //从数据库刷新数据
    fun refreshData(){
        viewModelScope.launch{
            val leastData = withContext(Dispatchers.IO){ //查询数据库操作放到后台执行
                dao.selectAllHistoryRecord()
            }
            _rawList.value = leastData
        }
    }
    //切换某条记录的收藏状态
    fun toggleFavoriteState(historyRecord: HistoryRecord)
    {
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                val newState = !historyRecord.isFavorite //点击反选收藏状态
                dao.updateFavoriteHistoryRecord(historyRecord.id,newState, System.currentTimeMillis())
            }
            refreshData()
        }
    }
    //批量删除选中的记录
    fun deleteByBatch(){
        val ids = selectedIds.value ?.toList() ?: return
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                dao.deleteHistoryRecordByBatch(ids)
            }
            refreshData()
        }
    }
    //全选/取消全选逻辑
    fun toggleSelectAll(visibleIds: List<Long>){
        val currentSelectedIds = selectedIds.value ?: emptySet()
        if (currentSelectedIds.containsAll(visibleIds)){
            selectedIds.value = emptySet()   //如已经全选则执行反选逻辑
        }else{
            selectedIds.value = visibleIds.toSet()  //否则全选所有可见项
        }
    }
}