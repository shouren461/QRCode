package com.example.qrcode.functions.historyFunction

import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.example.qrcode.R
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Dao
import com.example.qrcode.activity.BaseFragment
import com.example.qrcode.activity.CreateItemDisplayActivity
import com.example.qrcode.databinding.FragmentHistoryBinding
import com.example.qrcode.functions.createFunction.CreateAdapter
import com.example.qrcode.functions.createFunction.CreateItem
import com.example.qrcode.functions.createFunction.CreateType

//历史记录Fragment ->负责历史界面的UI初始化，事件监听，以及观察ViewModel的状态来刷新界面
class HistoryFragment: BaseFragment<FragmentHistoryBinding>(FragmentHistoryBinding::inflate){
    //使用ViewModel管理状态，HistoryFragment不处理具体的业务逻辑
    private  val viewModel: HistoryViewModel by lazy { ViewModelProvider(this)[HistoryViewModel::class.java] }
    //引入列表适配器
    private lateinit var adapter: HistoryAdapter //Android提供的一个原生弹窗类
    //引入筛选弹窗视图
    private var filterDialog: PopupWindow ?= null

    override fun initView() {
        updateDarkModeIconStyle()  //更新黑暗模式下的图标
        initRCView()
        initClickListener()
        observeState()
    }
    override fun initData() {
        viewModel.refreshData()
    }
    //初始化滚动视图适配器
    private fun initRCView() {
        adapter = HistoryAdapter(
            //单击逻辑:如果是普通模式则执行跳转对应的创建结果页，如果是选择模式则切换选中状态
            onClick = {record ->
                if (viewModel.isEditMode.value == true){
                    toggleSelectedIdState(record.historyRecordItem.id)
                }else{
                    val intent = Intent(context, CreateItemDisplayActivity::class.java).apply {
                        putExtra("type",record.historyRecordItem.category.name)
                        putExtra("content",record.historyRecordItem.content)
                    }
                    startActivity(intent)
                }
            },
            //长按逻辑:普通模式下长按进入编辑模式
            onLongClick = {record ->
                if (viewModel.isEditMode.value == false){
                    viewModel.isEditMode.value = true
                    viewModel.selectedIds.value = setOf(record.historyRecordItem.id)
                }
            },
            //点击收藏按钮:切换收藏按钮选中状态
            onFavoriteClick = {record ->
                viewModel.toggleFavoriteState(record.historyRecordItem)
            },
            //点击"View all"进入收藏记录详情界面
            onShowViewAllClick = {
                viewModel.isFavoritesMode.value = true
                binding.rvHistory.scrollToPosition(0)  //滚动到视图顶部查看收藏界面数据
            })
            val layoutManager = LinearLayoutManager(requireContext())
        binding.rvHistory.adapter = adapter
        binding.rvHistory.layoutManager = layoutManager
    }
    //改变选中id的状态
    private fun toggleSelectedIdState(id: Long) {
        val currentSelectedIds = viewModel.selectedIds.value ?:emptySet()
        viewModel.selectedIds.value = if (currentSelectedIds.contains(id)) currentSelectedIds - id else currentSelectedIds + id
    }

    //初始化点击事件监听器
    private fun initClickListener() {
        //返回事件:如果是编辑模式则退出编辑模式，如果是收藏界面详情页面则默认退出
        binding.ivHistoryBack.setOnClickListener {
            if (viewModel.isEditMode.value == true){
                viewModel.isEditMode.value = false
                viewModel.selectedIds.value = emptySet()
            }else if (viewModel.isFavoritesMode.value == true){
                viewModel.isFavoritesMode.value = false
            }
        }
        //筛选按钮:弹出筛选事件弹窗
        binding.ivHistoryFilter.setOnClickListener {
            showSelectedFilterDialog()
        }
        //删除按钮:如果是编辑模式则直接弹出确认删除弹窗，否则进入编辑模式
        binding.ivHistoryDelete.setOnClickListener {
            if (viewModel.isEditMode.value == true){
                showDeleteConfirmDialog()
            }else{
                viewModel.isEditMode.value  = true
            }
        }
        //全选按钮:获取当前界面所有的可见记录id,执行全选/反选
        binding.ivHistorySelectAll.setOnClickListener {
            val visibleIds = adapter.itemList.filterIsInstance<DisplayItem.Record>().map { it.historyRecordItem.id }
            viewModel.toggleSelectAll(visibleIds)
        }
    }

    //显示分类筛选弹窗
    private fun showSelectedFilterDialog() {
        val content = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            background = requireContext().getDrawable(R.drawable.bg_item_card)
            setPadding(0,15,0,15)
        }
        //动态遍历二维码类型生成筛选列表项
        CreateType.values().forEach { type ->
            val filterItem = LayoutInflater.from(requireContext()).inflate(R.layout.item_history_filter,content,false)
            filterItem.findViewById<ImageView>(R.id.iv_history_filter_icon).setImageResource(CreateAdapter.getIconRes(type))
            filterItem.findViewById<TextView>(R.id.tv_history_filter_type).text  = type.name
            val ivFilterCheckBox = filterItem.findViewById<ImageView>(R.id.iv_history_filter_checkbox)

            val currentFilters = viewModel.filterTypes.value ?: emptySet()
            ivFilterCheckBox.setImageResource(if (currentFilters.contains(type))R.mipmap.ic_history_checkbox_selected else R.mipmap.ic_history_checkbox_unselected)
            //设置点击切换筛选状态并通知列表刷新
            filterItem.setOnClickListener {
                val  newFilters = if (currentFilters.contains(type)) currentFilters - type else currentFilters + type
                viewModel.filterTypes.value = newFilters
                filterDialog?.dismiss()
                viewModel.refreshData()
            }
            content.addView(filterItem)
        }
        //设置filterDialog的视图大小
        filterDialog = PopupWindow(content,dpToMetrics(210), ViewGroup.LayoutParams.WRAP_CONTENT,true).apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            showAsDropDown(binding.ivHistoryFilter,-dpToMetrics(180),0)
        }

    }
    private fun dpToMetrics(v: Int) = (v * resources.displayMetrics.density).toInt()
    //弹出删除确认弹窗
    private fun showDeleteConfirmDialog() {
        val selectedItemCount = viewModel.selectedIds.value ?.size ?: 0
        if (selectedItemCount == 0 ) return
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.history_page_delete_confirm_title))
            .setMessage(getString(R.string.history_page_delete_confirm_message))
            .setPositiveButton(getString(R.string.history_page_delete_confirm_positive), { _, _ ->viewModel.deleteByBatch() })
            .setNegativeButton(getString(R.string.history_page_delete_confirm_negative),null)
            .show()
    }

    //观察ViewModel的状态，实现数据驱动UI
    private fun observeState() {
        //实时观察核心列表数据，只要数据一更新就刷新适配器
        viewModel.displayList.observe(viewLifecycleOwner){
            adapter.updateRCView(it)
            //更新"全选"图标的视图状态
            val visibleIds  = adapter.itemList.filterIsInstance<DisplayItem.Record>().map { it.historyRecordItem.id }
            val currentSelectedIds = viewModel.selectedIds.value ?: emptySet()
            val isSelectAll = visibleIds.isNotEmpty() && currentSelectedIds.containsAll(visibleIds)
            binding.ivHistorySelectAll.setImageResource(if(isSelectAll) R.mipmap.ic_history_checkbox_selected else R.mipmap.ic_history_checkbox_unselected)
        }
        //观察模式变化，实时动态刷新标题栏的图标状态
        viewModel.isEditMode.observe(viewLifecycleOwner){updateTopBarStatus()}
        viewModel.isFavoritesMode.observe(viewLifecycleOwner){updateTopBarStatus()}
    }
    //根据模式的变化，动态刷新顶部标题栏的图标
    private fun updateTopBarStatus() {
        val isEdit = viewModel.isEditMode.value ?: false
        val isFavorite = viewModel.isFavoritesMode.value ?: false
        //主标题仅在普通模式下可见
        binding.tvHistoryTitle.visibility =if (!isEdit && !isFavorite) View.VISIBLE else View.INVISIBLE
        //返回键在编辑或收藏详情界面可见
        binding.ivHistoryBack.visibility = if (isEdit || isFavorite) View.VISIBLE else View.GONE
        //筛选键在普通模式下可见
        binding.ivHistoryFilter.visibility = if (!isEdit && !isFavorite) View.VISIBLE else View.GONE
       //收藏界面标题仅在收藏详情模式下可见
        binding.tvHistoryHeaderCategory.visibility = if (isFavorite) View.VISIBLE else View.GONE
        //全选按钮仅在编辑模式下可见
        binding.ivHistorySelectAll.visibility = if(isEdit) View.VISIBLE else View.GONE
    }

    //更新黑暗模式下的返回键图标
    fun updateDarkModeIconStyle(){
        val isDarkMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
        if (isDarkMode) {
            binding.ivHistoryBack.setImageResource(R.mipmap.ic_results_page_return_dark_mode)
        } else {
            binding.ivHistoryBack.setImageResource(R.mipmap.ic_results_page_return)
        }
    }
    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        //如果页面不隐藏了，则刷新页面
        if (!hidden){
            viewModel.refreshData()
        }
    }

}