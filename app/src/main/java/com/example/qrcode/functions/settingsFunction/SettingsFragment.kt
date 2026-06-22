package com.example.qrcode.functions.settingsFunction

import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import com.example.qrcode.R
import com.example.qrcode.activity.BaseFragment
import com.example.qrcode.databinding.FragmentSettingsBinding
import com.example.qrcode.utils.ThemeHelper

class SettingsFragment: BaseFragment<FragmentSettingsBinding>(FragmentSettingsBinding::inflate) {

    override fun initData() {}

    //监听点击事件
    override fun initView() {
        //初始化黑暗模式选中状态
        binding.switchDarkMode.isChecked = ThemeHelper.isDarkMode(requireContext())

        //监听开关切换动作
        binding.switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            ThemeHelper.setDarkMode(requireContext(),isChecked)  //调用工具类，保存并应用新模式
        }

    }

}