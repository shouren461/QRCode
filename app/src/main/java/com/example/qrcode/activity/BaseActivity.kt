package com.example.qrcode.activity

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.example.qrcode.utils.ThemeHelper

//所有Activity的基类 提供Activity的初始化操作
abstract class BaseActivity<VB: ViewBinding>(val inflate:(LayoutInflater) ->VB): AppCompatActivity() {
    protected lateinit var binding:VB
    override fun onCreate(savedInstanceState: Bundle?) {
        //在Activity创建之前就应用主题色
        ThemeHelper.applyTheme(this)
        super.onCreate(savedInstanceState)
        binding = inflate(layoutInflater)
        //设置设置初始化布局方式
        setContentView(binding.root)
        //按照规范顺序初始化
        initData()
        initView()
        initAction()
    }

    protected abstract fun initData()       //初始化表
    protected abstract fun initView()    //初始化视图控件
    protected abstract fun initAction()  //初始化点击事件

}