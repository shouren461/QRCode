package com.example.qrcode

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.FragmentTransaction
import com.example.qrcode.activity.BaseActivity
import com.example.qrcode.databinding.ActivityMainBinding
import com.example.qrcode.functions.createFunction.CreateFragment
import com.example.qrcode.functions.historyFunction.HistoryFragment
import com.example.qrcode.functions.scanFunction.ScanFragment
import com.example.qrcode.functions.settingsFunction.SettingsFragment

//应用的主界面Activity
// 采用单Activity和 多Fragment 的格式 负责管路底部导航栏和 "扫描"，"创建"，"历史记录"三个核心功能页面的切换
class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate) {
    //延迟初始化底部标签
    //定义底部的Tab的类型常量
    object Constant{
        //Intent传递参数:选中主页面的哪个Tab(1:扫描,2:历史,3:创建,4:设置)
        const val EXTRA_SELECT_TAB = "key_select_tab"
    }
    companion object {
        const val TAB_TYPE_SCAN: Int = 1 //扫描二维码Tab
        const val TAB_TYPE_HISTORY: Int = 2 //历史记录Tab
        const val TAB_TYPE_CREATE: Int = 3 //创建二维码Tab

        const val TAB_TYPE_SETTINGS: Int = 4  //设置记录Tab
    }

    //初始化布局控件
    //底部导航栏的UI控件引用(图标)
    private lateinit var scanTab: ImageView
    private lateinit var historyTab: ImageView
    private lateinit var createTab: ImageView
    private lateinit var settingsTab: ImageView

    //记录当前选中的Tab类型，默认显示"创建"页
    var cursorTab = TAB_TYPE_CREATE
    private var savedTabFromBundle: Int? = null

    //初始化Fragment资源
    private var createFragment: CreateFragment? = null
    private var historyFragment: HistoryFragment? = null
    private var scanFragment: ScanFragment? = null
    private var settingsFragment: SettingsFragment? = null

    //在onCreate()中提前捕获保存的状态
    override fun onCreate(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            savedTabFromBundle = savedInstanceState.getInt("save_tab", TAB_TYPE_CREATE)
        }
        super.onCreate(savedInstanceState)
    }


    override fun initData() {
        //初始化viewModel ,用于处理Activity的数据逻辑
    }
    //在重建前保存当前位置
    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt("save_tab",cursorTab)
        super.onSaveInstanceState(outState)
    }

    override fun initView() {
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //如果是从重建恢复的，则更新当前 Tab
        savedTabFromBundle?.let {
            cursorTab = it
        }

        //初始化视图控件
        initBottomTab();
        //从Intent中获取选中的Tab ,如果没有则选择初始值
        val intent = intent
        if (intent != null) {
            val selectTab = intent.getIntExtra(Constant.EXTRA_SELECT_TAB, cursorTab);
            when (selectTab) {
                1 -> cursorTab = TAB_TYPE_SCAN
                2 -> cursorTab = TAB_TYPE_HISTORY
                3 -> cursorTab = TAB_TYPE_CREATE
                4 -> cursorTab = TAB_TYPE_SETTINGS
            }
        }
        //切换页面
        switchFragment(cursorTab);
    }

    override fun initAction() {}

    //初始化底部导航栏事件
    //初始化底部导航栏
    private fun initBottomTab() {
        scanTab = findViewById(R.id.iv_bottom_scan)
        historyTab = findViewById(R.id.iv_bottom_history)
        createTab = findViewById(R.id.iv_bottom_create)
        settingsTab = findViewById(R.id.iv_bottom_settings)

        //扫描功能入口，点击监听
        findViewById<View>(R.id.bottom_scan).setOnClickListener {
            Toast.makeText(this,getString(R.string.toast_feature_is_not_available), Toast.LENGTH_SHORT).show()
        }
        //历史Tab,点击监听
        findViewById<View>(R.id.bottom_history).setOnClickListener {
            Toast.makeText(this,getString(R.string.toast_feature_is_not_available), Toast.LENGTH_SHORT).show()
        }

        //创建Tab,点击监听
        findViewById<View>(R.id.bottom_create).setOnClickListener {
           onBottomTabSelect(TAB_TYPE_CREATE)

        }
        //设置Tab,点击监听
        findViewById<View>(R.id.bottom_settings).setOnClickListener {
            onBottomTabSelect(TAB_TYPE_SETTINGS)
        }
        //初始化底栏状态:设置默认选中状态"创建"图标
        scanTab.setImageResource(R.mipmap.ic_scan_unselected)
        historyTab.setImageResource(R.mipmap.ic_history_unselected)
        createTab.setImageResource(R.mipmap.ic_create_selected)
        settingsTab.setImageResource(R.mipmap.ic_setting_unselected)
    }

    //处理底部Tab选中的UI更新逻辑
    fun onBottomTabSelect(type: Int) {
        if (type == cursorTab) {
            return   //如果是当前选中的Tab，直接返回
        } else {
            switchFragment(type) //否则切换Fragment
            when(type){
                TAB_TYPE_SCAN ->{
                    binding.layoutBottomBar.ivBottomScan.setImageResource(R.mipmap.ic_scan_selected)
                    binding.layoutBottomBar.tvBottomScan.setTextColor(getColor(R.color.bottom_tab_selected_color))
                    binding.layoutBottomBar.ivBottomHistory.setImageResource(R.mipmap.ic_history_unselected)
                    binding.layoutBottomBar.tvBottomHistory.setTextColor(getColor(R.color.bottom_tab_unselected_color))
                    binding.layoutBottomBar.ivBottomCreate.setImageResource(R.mipmap.ic_create_unselected)
                    binding.layoutBottomBar.tvBottomCreate.setTextColor(getColor(R.color.bottom_tab_unselected_color))
                    binding.layoutBottomBar.ivBottomSettings.setImageResource(R.mipmap.ic_setting_unselected)
                    binding.layoutBottomBar.tvBottomSettings.setTextColor(getColor(R.color.bottom_tab_unselected_color))
                }
                TAB_TYPE_HISTORY->{
                    binding.layoutBottomBar.ivBottomScan.setImageResource(R.mipmap.ic_scan_unselected)
                    binding.layoutBottomBar.tvBottomScan.setTextColor(getColor(R.color.bottom_tab_unselected_color))
                    binding.layoutBottomBar.ivBottomHistory.setImageResource(R.mipmap.ic_history_selected)
                    binding.layoutBottomBar.tvBottomHistory.setTextColor(getColor(R.color.bottom_tab_selected_color))
                    binding.layoutBottomBar.ivBottomCreate.setImageResource(R.mipmap.ic_create_unselected)
                    binding.layoutBottomBar.tvBottomCreate.setTextColor(getColor(R.color.bottom_tab_unselected_color))
                    binding.layoutBottomBar.ivBottomSettings.setImageResource(R.mipmap.ic_setting_unselected)
                    binding.layoutBottomBar.tvBottomSettings.setTextColor(getColor(R.color.bottom_tab_unselected_color))
                }
                TAB_TYPE_CREATE ->{
                    binding.layoutBottomBar.ivBottomScan.setImageResource(R.mipmap.ic_scan_unselected)
                    binding.layoutBottomBar.tvBottomScan.setTextColor(getColor(R.color.bottom_tab_unselected_color))
                    binding.layoutBottomBar.ivBottomHistory.setImageResource(R.mipmap.ic_history_unselected)
                    binding.layoutBottomBar.tvBottomHistory.setTextColor(getColor(R.color.bottom_tab_unselected_color))
                    binding.layoutBottomBar.ivBottomCreate.setImageResource(R.mipmap.ic_create_selected)
                    binding.layoutBottomBar.tvBottomCreate.setTextColor(getColor(R.color.bottom_tab_selected_color))
                    binding.layoutBottomBar.ivBottomSettings.setImageResource(R.mipmap.ic_setting_unselected)
                    binding.layoutBottomBar.tvBottomSettings.setTextColor(getColor(R.color.bottom_tab_unselected_color))
                }
                TAB_TYPE_SETTINGS ->{
                    binding.layoutBottomBar.ivBottomScan.setImageResource(R.mipmap.ic_scan_unselected)
                    binding.layoutBottomBar.tvBottomScan.setTextColor(getColor(R.color.bottom_tab_unselected_color))
                    binding.layoutBottomBar.ivBottomHistory.setImageResource(R.mipmap.ic_history_unselected)
                    binding.layoutBottomBar.tvBottomHistory.setTextColor(getColor(R.color.bottom_tab_unselected_color))
                    binding.layoutBottomBar.ivBottomCreate.setImageResource(R.mipmap.ic_create_unselected)
                    binding.layoutBottomBar.tvBottomCreate.setTextColor(getColor(R.color.bottom_tab_unselected_color))
                    binding.layoutBottomBar.ivBottomSettings.setImageResource(R.mipmap.ic_setting_selected)
                    binding.layoutBottomBar.tvBottomSettings.setTextColor(getColor(R.color.bottom_tab_selected_color))
                }
            }
        }
    }

    //切换页面
    //核心逻辑:切换Fragment  ->使用hide/show 方式，避免Fragment 重复创建并保持页面状态
    private fun switchFragment(type: Int) {
        cursorTab = type
        val fragmentTransaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        //1,检查Fragment是否已经存在(可能是SaveInstanceState恢复的)
        if (scanFragment == null) {
            val mayExistFragment = supportFragmentManager.findFragmentByTag("f1")
            if (mayExistFragment is ScanFragment) {
                scanFragment = mayExistFragment
            }
        }
        if (historyFragment == null) {
            val maybeExistFragment = supportFragmentManager.findFragmentByTag("f2")
            if (maybeExistFragment is HistoryFragment) {
                historyFragment = maybeExistFragment
            }
        }
        if (createFragment == null) {
            val mayExistFragment = supportFragmentManager.findFragmentByTag("f3")
            if (mayExistFragment is CreateFragment) {
                createFragment = mayExistFragment
            }
        }
        if (settingsFragment == null){
            val mayExistFragment = supportFragmentManager.findFragmentByTag("f4")
            if (mayExistFragment is SettingsFragment){
                settingsFragment = mayExistFragment
            }
        }

        //2,先隐藏所有已存在的Fragment
        historyFragment?.let { fragmentTransaction.hide(it) }
        createFragment?.let { fragmentTransaction.hide(it) }
        scanFragment?.let { fragmentTransaction.hide(it) }
        settingsFragment?.let { fragmentTransaction.hide(it) }
        //3,根据点击的类型显示对应的Fragment
        when (type) {
            TAB_TYPE_SCAN -> {
                if (scanFragment == null) {
                    scanFragment = ScanFragment()
                    fragmentTransaction.add(R.id.fl_fragment_holder, scanFragment!!, "f1")
                } else {
                    fragmentTransaction.show(scanFragment!!)
                }
            }

            TAB_TYPE_CREATE -> {
                if (createFragment == null) {
                    createFragment = CreateFragment()
                    fragmentTransaction.add(R.id.fl_fragment_holder, createFragment!!, "f2")
                } else {
                    fragmentTransaction.show(createFragment!!)
                }
            }

            TAB_TYPE_HISTORY -> {
                if (historyFragment == null) {
                    historyFragment = HistoryFragment()
                    fragmentTransaction.add(R.id.fl_fragment_holder, historyFragment!!, "f3")
                } else {
                    fragmentTransaction.show(historyFragment!!)
                }
            }
            TAB_TYPE_SETTINGS ->{
                if (settingsFragment == null){
                    settingsFragment = SettingsFragment()
                    fragmentTransaction.add(R.id.fl_fragment_holder,settingsFragment!!,"f4")
                }else{
                    fragmentTransaction.show(settingsFragment!!)
                }
            }


        }
        //4,提交事务，允许状态丢失(防止极端情况下的崩溃)
        fragmentTransaction.commitAllowingStateLoss()
    }
}