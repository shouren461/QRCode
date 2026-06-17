package com.example.qrcode.activity

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.qrcode.utils.ThemeHelper

//所有Activity的基类 提供Activity的初始化操作
abstract class BaseActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        //在Activity创建之前就应用主题色
        ThemeHelper.applyTheme(this)
        super.onCreate(savedInstanceState)
        //设置设置初始化布局方式
        setContentView()
        //按照规范顺序初始化
        initData()
        initView()
        initAction()
    }

    //设置布局，子类可重写
    protected open fun setContentView() {
        setContentView(this.getLayout())
    }
    protected abstract fun getLayout(): Int //返回布局Id
    protected abstract fun initData()       //初始化表
    protected abstract fun initView()    //初始化视图控件
    protected abstract fun initAction()  //初始化点击事件

}