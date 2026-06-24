package com.example.qrcode.activity

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.qrcode.R
import com.example.qrcode.database.HistoryRecordDB
import com.example.qrcode.databinding.ActivityScanItemDisplayBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

//扫描结果展示页面:负责展示二维码对应的文本信息 并提供 返回/复制/分享/收藏 功能
class ScanItemDisplayActivity : BaseActivity<ActivityScanItemDisplayBinding>(
    ActivityScanItemDisplayBinding::inflate) {
    //定义变量
    private var scanItemContent: String = ""
    private var historyRecordId: Long = -1
    private var isMarkedFavorite: Boolean = false
    override fun initData() {
        scanItemContent = intent.getStringExtra("EXTRA_SCAN_CONTENT").orEmpty()
        historyRecordId = intent.getLongExtra("EXTRA_SCAN_ID",-1L)
    }

    override fun initView() {
        binding.tvScanResultContent.text = scanItemContent
        checkFavoritesStatus()  //检查收藏图标选中状态
    }
    //绑定监听事件
    override fun initAction() {
        binding.btnScanResultBack.setOnClickListener { finish() } //返回事件
        binding.ivScanResultFavorites.setOnClickListener { toggleFavorite() } //切换收藏状态
        binding.ivScanResultCopy.setOnClickListener {
            val clipBoardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager //获取系统剪贴板服务
            clipBoardManager.setPrimaryClip(ClipData.newPlainText("Scan",scanItemContent))  //设置刚复制的内容在剪贴板置顶
            Toast.makeText(this,getString(R.string.scan_result_copy_hint), Toast.LENGTH_SHORT).show()
        }
        binding.ivScanResultShare.setOnClickListener {  //调用系统分享功能分享文本信息
           val shareScanResultIntent  = Intent(Intent.ACTION_SEND).apply {
               type = "text/plain"
                putExtra(Intent.EXTRA_TEXT,scanItemContent)
            }
            startActivity(Intent.createChooser(shareScanResultIntent,"Share_Result"))
        }
    }
    //查询数据库获取当前记录的收藏状态
    private fun checkFavoritesStatus() {
       if (historyRecordId == -1L) return
        lifecycleScope.launch(Dispatchers.IO) {
                val dao = HistoryRecordDB.getDatabase(application).historyRecordDao()
                val allHistoryRecords = dao.selectAllHistoryRecord()
                val result = allHistoryRecords.find { it.id == historyRecordId }
                //切回主线程更新UI图标
                withContext(Dispatchers.Main){
                    isMarkedFavorite = result?.isFavorite ?: false
                    updateFavoritesIcon()
                }
        }
    }
    //切换收藏状态
    fun toggleFavorite(){
        if (historyRecordId == -1L) return
        lifecycleScope.launch(Dispatchers.IO) {
           val dao = HistoryRecordDB.getDatabase(application).historyRecordDao()
            val newStatus = !isMarkedFavorite //状态取反
            val newTime =  if (newStatus) System.currentTimeMillis() else null
            //调用dao层接口直接更新数据
            dao.updateFavoriteHistoryRecord(historyRecordId,newStatus,newTime)
            //切回主线程更新状态和图标
            withContext(Dispatchers.Main){
                isMarkedFavorite = newStatus
                updateFavoritesIcon()
            }
        }
    }
    //根据收藏状态更新UI图标状态
    fun updateFavoritesIcon() {
        binding.ivScanResultFavorites.setImageResource(
            if (isMarkedFavorite) R.mipmap.ic_favorites_selected else R.mipmap.ic_favorites_unselected
        )
    }


}