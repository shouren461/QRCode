package com.example.qrcode.activity

import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.qrcode.R
import com.example.qrcode.databinding.ActivityCreateInputBinding
import com.example.qrcode.functions.createFunction.CreateAdapter
import com.example.qrcode.functions.createFunction.CreateType
import com.example.qrcode.functions.createFunction.CreateViewModel
import com.example.qrcode.functions.createFunction.FormAdapter

//CreateInputActivity仅对外展示UI交互和点击事件，CreateViewModel负责业务处理
class CreateInputActivity : BaseActivity<ActivityCreateInputBinding>(ActivityCreateInputBinding::inflate) {
    private val  viewModel: CreateViewModel by viewModels()  //注入ViewModel
    private var adapter: FormAdapter?= null   //表单适配器

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        //从跳转Intent获取要创建的二维码类型
        val typeStr = intent.getStringExtra("type") ?: "Website"
        val type = CreateType.valueOf(typeStr)
        initView(type)
        observeViewModel()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun initView(type: CreateType) {
        binding.tvTitle.text = type.name
        binding.btnBackCreateResult.setOnClickListener { finish() }
        //通知ViewModel选择的哪一种二维码类型
        viewModel.selectType(type)
        binding.btnCreate.setOnClickListener {
            adapter?.let {
                //将适配器收集到的values传给viewModel去生成协议
                viewModel.buildContent(it.values,emptyMap())
            }
        }
    }
    //初始化Tab栏，适用于社交APP类的模式切换
    private fun initTabs(tabs: List<String>,selected: String){
        binding.layoutTabs.removeAllViews()
        if (tabs.isEmpty()){//如果tabs参数为空，Tabs不可见直接返回
            binding.layoutTabs.visibility = View.GONE
            return
        }
        binding.layoutTabs.visibility = View.VISIBLE
        tabs.forEach { tab->
            //动态加载Tab的TextView布局
            val tabView = layoutInflater.inflate(R.layout.view_tab_item,binding.layoutTabs,false) as TextView
            tabView.text = tab
            //确保新增的tab宽度与原始tab保持一致,权重为1
            val params = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT,1.0f)
            tabView.layoutParams = params

            updateTabStyle(tabView,tab == selected ) //根据选中状态更新颜色和背景

            tabView.setOnClickListener {
                viewModel.strategy.value ?.let { strategy ->
                    //切换策略中选中的Tab,并通知ViewModel自动刷新
                    strategy.selectedTab = tab
                    (viewModel.strategy as MutableLiveData).value = strategy
                }
            }
            binding.layoutTabs.addView(tabView)
        }
    }
//更新Tab选中的样式
    fun updateTabStyle(view: TextView, isSelected: Boolean) {
        if (isSelected){
            view.setBackgroundResource(R.drawable.bg_tabs_selected)
            view.setTextColor(getColor(R.color.black_text))
        }else{
            view.setBackgroundResource(R.color.transparent)
            view.setTextColor(getColor(R.color.grey_text))
        }
    }
    //订阅ViewModel中的数据变化
    private fun observeViewModel() {
        //当策略对象的类型或者切换tab 发生改变，重新构建表单
        viewModel.strategy.observe(this) { strategy ->
            if (strategy != null) {
                initTabs(strategy.tabs, strategy.selectedTab)//重新渲染Tab栏
                //判断当前界面是否需要显示图标
                if (strategy.showAppIcon){
                    binding.layoutOpenUrl.visibility = View.VISIBLE
                    binding.ivAppIcon.visibility = View.VISIBLE
                    binding.ivAppIcon.setImageResource(strategy.appIconRes)
                    //判断跳转网址布局是否可见
                    binding.tvOpenUrl.visibility = if (strategy.showOpenUrl) View.VISIBLE else View.GONE
                    //判断当前界面是否有跳转网页功能
                    if (strategy.showOpenUrl){
                        //设置网站跳转功能
                        binding.tvOpenUrl.setOnClickListener {
                            try {
                                val intent = Intent(Intent.ACTION_VIEW,Uri.parse(strategy.targetUrl))
                                startActivity(intent)
                            } catch (e: Exception){

                                Toast.makeText(this,getString(R.string.cannot_open_browser), Toast.LENGTH_SHORT).show()
                            }
                        }
                }else{//点击事件为空或者不可点击
                    binding.tvOpenUrl.setOnClickListener(null)
                    binding.tvContactByViber.visibility = View.VISIBLE
                    }
                }else{
                    binding.layoutOpenUrl.visibility = View.GONE
                }
                //根据strategy返回的getFields()重新构建RecycleView表单
                adapter = FormAdapter(strategy.getFields())
                binding.rvForm.layoutManager = LinearLayoutManager(this)
                binding.rvForm.adapter = adapter
            }
        }

        //当协议内容生成成功后，执行跳转到结果展示页
        viewModel.createResult.observe(this) { pair ->
            val content = pair.first
            val insertId = pair.second //从键值对获取对应的内容 和插入返回的id
            if (content.isNotEmpty()) {
                val intent = Intent(this, CreateItemDisplayActivity::class.java).apply {
                    putExtra("EXTRA_CREATE_RESULT_TYPE", viewModel.strategy.value?.type?.name)
                    putExtra("EXTRA_CREATE_RESULT_CONTENT", content)
                    putExtra("EXTRA_HISTORY_RECORD_ID",insertId)
                }
                startActivity(intent)
            }
        }
    }
    override fun initData() {}
    override fun initView() {}
    override fun initAction() {}
}