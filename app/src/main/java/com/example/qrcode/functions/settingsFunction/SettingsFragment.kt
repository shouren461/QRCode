package com.example.qrcode.functions.settingsFunction

import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import com.example.qrcode.R
import com.example.qrcode.activity.BaseFragment
import com.example.qrcode.utils.ThemeHelper

class SettingsFragment: BaseFragment() {
    //定义黑暗模式转换控件
    private lateinit var switchDarkMode: SwitchCompat
    override fun getLayoutResId(): Int  =R.layout.fragment_settings

    override fun initData() {}

    //监听点击事件
    override fun initView(root: View?) {
        switchDarkMode = root!!.findViewById<SwitchCompat>(R.id.switch_dark_mode)
        //初始化黑暗模式选中状态
        switchDarkMode.isChecked = ThemeHelper.isDarkMode(requireContext())
        //监听开关切换动作
        switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            ThemeHelper.setDarkMode(requireContext(),isChecked)  //调用工具类，保存并应用新模式
        }

    }

}