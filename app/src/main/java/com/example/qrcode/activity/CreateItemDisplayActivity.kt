package com.example.qrcode.activity

import android.content.Intent
import android.content.res.Configuration
import android.graphics.Bitmap
import android.widget.Toast
import com.example.qrcode.R
import androidx.lifecycle.lifecycleScope
import com.example.qrcode.database.HistoryRecordDB
import com.example.qrcode.databinding.ActivityCreateItemDispalyBinding
import com.example.qrcode.functions.createFunction.CreateAdapter
import com.example.qrcode.functions.createFunction.CreateType
import com.example.qrcode.utils.PictureHelper
import com.example.qrcode.utils.QRHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

//二维码创建结果展示界面：负责接收协议内容，并将其转化为最终的二维码图片显示出来
class CreateItemDisplayActivity: BaseActivity<ActivityCreateItemDispalyBinding>(ActivityCreateItemDispalyBinding::inflate) {
    private lateinit var type: CreateType //二维码业务类型
    private var qrBitmap: Bitmap ?= null
    private var historyRecordId: Long = -1L
    private lateinit var content: String
    private var isMarkedFavorite: Boolean = false  //检查收藏状态

    override fun initData() {
        historyRecordId = intent.getLongExtra("EXTRA_HISTORY_RECORD_ID",-1L)
    }
    override fun initAction() {
        //返回按钮逻辑，销毁当前页面回到上一步
        binding.btnBackCreateResult.setOnClickListener { finish() }
        //保存图片按钮
        binding.btnSave.setOnClickListener {
            qrBitmap?.let { saveQRToAlbum(it) }
        }
        //分享给其他应用
        binding.btnShare.setOnClickListener { shareQRWithOthersAPP() }
        //收藏按钮
        binding.ivCreateResultsFavorites.setOnClickListener { toggleFavorites() }
    }

    override fun initView() {
        updateDarkModeIconStyle()
        val typeStr = intent.getStringExtra("EXTRA_CREATE_RESULT_TYPE") ?: ""
        //将字符串转换为枚举类型
        type = try { CreateType.valueOf(typeStr) }catch (e: Exception){ CreateType.Text //类型转换失败，默认是文本类型
        }
        binding.ivTitleImg.setImageResource(CreateAdapter.getIconRes(type))
        binding.tvTitle.text = typeStr
        createQR()
        //创建视图时检查收藏图标状态
        checkAndUpdateFavorite()
    }
    //生成并显示二维码图片
    private fun createQR() {
        content = intent.getStringExtra("EXTRA_CREATE_RESULT_CONTENT") ?: ""
        lifecycleScope.launch {
            qrBitmap = QRHelper.createQRBitmap(content) //生成二维码位图
            //将生成的位图设置给iv_qr控件
            binding.ivQr.setImageBitmap(qrBitmap)
        }
    }
    //保存图片到相册功能
    fun saveQRToAlbum(bitmap: Bitmap) {
        when(val result = PictureHelper.savePicture(this,bitmap)){
            is PictureHelper.SaveInfo.Success ->{
                Toast.makeText(this,getString(R.string.saved_picture), Toast.LENGTH_SHORT).show()
            }
            is PictureHelper.SaveInfo.PermissionRequired->{
                Toast.makeText(this,getString(R.string.permission_denied), Toast.LENGTH_SHORT).show()
            }
            is PictureHelper.SaveInfo.Error ->{
                Toast.makeText(this,getString(R.string.save_failed,result.message), Toast.LENGTH_SHORT).show()
            }
        }
    }

    //分享二维码到其他应用
    fun shareQRWithOthersAPP() {
        qrBitmap?.let { qrBitmap ->
            PictureHelper.shareWith(this,qrBitmap)?.let { intent ->
                startActivity(Intent.createChooser(intent,getString(R.string.share)))
            } ?: Toast.makeText(this,getString(R.string.error_save_unknown), Toast.LENGTH_SHORT).show()
        }
    }
    //跳转到创建界面时搜索查看收藏状态
    private fun checkAndUpdateFavorite() {
        if (historyRecordId == -1L) return
        lifecycleScope.launch(Dispatchers.IO) {
            val dao = HistoryRecordDB.getDatabase(application).historyRecordDao()
            val allHistoryRecord = dao.selectAllHistoryRecord()
            val resultHistoryRecord = allHistoryRecord.find { it.id == historyRecordId }
            //切换主线程进行查询UI更新操作
            withContext(Dispatchers.Main){
                isMarkedFavorite = resultHistoryRecord?.isFavorite ?: false
                updateFavoritesIcon()
            }
        }

    }
    //切换图标收藏状态
    fun toggleFavorites() {
        if (historyRecordId == -1L) return
        lifecycleScope.launch(Dispatchers.IO) {
            val dao = HistoryRecordDB.getDatabase(application).historyRecordDao()
            val newStatus = !isMarkedFavorite// 状态取反
            val newTime = if (newStatus) System.currentTimeMillis() else null
            dao.updateFavoriteHistoryRecord(historyRecordId,newStatus,newTime)
            //切换主线程，更换收藏状态和UI图标
            withContext(Dispatchers.Main){
                isMarkedFavorite = newStatus
                updateFavoritesIcon()
            }
        }
    }

    //切换选中样式图标
    fun updateFavoritesIcon() {
        binding.ivCreateResultsFavorites.setImageResource(
            if (isMarkedFavorite){
                R.mipmap.ic_favorites_selected
            }else{
                val isDarkMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
                if (isDarkMode){
                    R.mipmap.ic_results_favorites_dark_mode
                }else{
                    R.mipmap.ic_favorites_unselected
                }
            }
        )

    }

    //黑夜模式下更新创建结果页面 返回/收藏 图标
    fun updateDarkModeIconStyle(){
        val isDarkMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
        if (isDarkMode){
            binding.btnBackCreateResult.setImageResource(R.mipmap.ic_results_page_return_dark_mode)
            binding.ivCreateResultsFavorites.setImageResource(R.mipmap.ic_results_favorites_dark_mode)
        }else{
            binding.btnBackCreateResult.setImageResource(R.mipmap.ic_results_page_return)
            binding.ivCreateResultsFavorites.setImageResource(R.mipmap.ic_results_favorites)
        }
    }

}