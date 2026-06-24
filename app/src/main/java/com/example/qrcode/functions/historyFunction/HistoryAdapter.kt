package com.example.qrcode.functions.historyFunction

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import com.example.qrcode.R
import androidx.recyclerview.widget.RecyclerView
import com.example.qrcode.functions.createFunction.CreateAdapter

//多类型适配器 ->根据不同的类型来加载 Header标题 或者 Record记录不同的布局文件，并且绑定数据
class HistoryAdapter(
    var itemList: List<DisplayItem> = emptyList(),
    private val onClick:(DisplayItem.Record) -> Unit,
    private val onLongClick:(DisplayItem.Record) -> Unit,
    private val onFavoriteClick:(DisplayItem.Record) -> Unit,
    private val onShowViewAllClick:() -> Unit
): RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    companion object{
        private const val TYPE_HEADER = 0
        private const val TYPE_RECORD = 1
    }
    //根据位置判断当前项的类型
    override fun getItemViewType(position: Int): Int {
        val displayItemType = itemList[position]
        return when(displayItemType){
            is DisplayItem.Header -> TYPE_HEADER
            is DisplayItem.Record -> TYPE_RECORD
        }
    }
    //分组标题Header的视图容器
    class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val tvHeaderTitle: TextView = view.findViewById(R.id.tv_header_title)
        val tvHeaderViewAll: TextView = view.findViewById(R.id.tv_header_view_all)
    }
    //历史记录Record的视图容器
    class RecordViewHolder(view: View): RecyclerView.ViewHolder(view){
        val ivRecordIcon: ImageView = view.findViewById(R.id.iv_history_record_icon)
        val tvRecordTitle: TextView = view.findViewById(R.id.tv_history_record_title)
        val tvRecordCategory: TextView = view.findViewById(R.id.tv_history_record_category)
        val tvRecordTime: TextView = view.findViewById(R.id.tv_history_record_time)
        val ivRecordFavIcon: ImageView = view.findViewById(R.id.iv_history_favorite)
        val ivRecordCheckbox: ImageView = view.findViewById(R.id.iv_history_checkbox)
    }
    //针对标题 布局和 记录布局创建不同的ViewHolder视图容器
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        //根据不同的viewType选择初始化不同的视图容器
        return if(viewType == TYPE_HEADER){
            HeaderViewHolder(inflater.inflate(R.layout.item_history_header,parent,false))
        }else{
            RecordViewHolder(inflater.inflate(R.layout.item_history_record,parent,false))
        }
    }
    //绑定数据到视图
    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int
    ) {
        val item = itemList[position]
        //情况一,处理标题项
        if (holder is HeaderViewHolder && item is DisplayItem.Header){
            holder.tvHeaderTitle.text = item.title
            holder.tvHeaderViewAll.isVisible = item.showALLView
            holder.tvHeaderViewAll.setOnClickListener {onShowViewAllClick()}
        }
        //情况二，处理记录项
        else if (holder is RecordViewHolder && item is DisplayItem.Record){
            val historyRecord = item.historyRecordItem  //定义单个历史记录类别
            //1,设置基本信息(图标，标题，类别，时间)
            holder.ivRecordIcon.setImageResource(CreateAdapter.getIconRes(historyRecord.category))
            holder.tvRecordTitle.text = historyRecord.title
            holder.tvRecordCategory.text = historyRecord.category.toString()
            holder.tvRecordTime.text = item.formattedTime
            //2，设计图标资源可见性。编辑模式下隐藏时间，收藏图标，显示复选框
            holder.tvRecordTime.isVisible = !item.isEditMode
            holder.ivRecordFavIcon.isVisible = !item.isEditMode
            holder.ivRecordCheckbox.isVisible = item.isEditMode
            //3,状态显示，包括收藏图标和复选框图标
            holder.ivRecordFavIcon.setImageResource(
                if (historyRecord.isFavorite){ //如果历史记录被收藏
                    R.mipmap.ic_favorites_selected
                }else{//如果未被收藏
                    R.mipmap.ic_favorites_unselected
                }
            )
            holder.ivRecordCheckbox.setImageResource(
                if (item.isSelected){//被选中
                    R.mipmap.ic_history_checkbox_selected
                }else{//未被选中
                    R.mipmap.ic_history_checkbox_unselected
                }
            )
            //4,设置点击事件
            holder.ivRecordFavIcon.setOnClickListener { onFavoriteClick(item) }
            holder.itemView.setOnClickListener { onClick(item) }
            holder.itemView.setOnLongClickListener {
                onLongClick(item)
                true
            }
        }
    }

    override fun getItemCount(): Int = itemList.size
    //更新列表数据
    fun updateRCView(newList:List<DisplayItem>){
        itemList = newList
        notifyDataSetChanged()  //刷新全部视图数据
    }
}