package com.example.qrcode.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

//所有Fragment的基类，，规范了生命周期和视图初始化
abstract class BaseFragment<VB: ViewBinding>(val inflate:(LayoutInflater, ViewGroup?, Boolean) ->VB): Fragment(){
    //定义ViewBing绑定视图
    private var _binding :VB ?= null
    val binding get() = _binding!!
    //创建基础视图
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = inflate(inflater,container,false)
        initView();
        initData();
        return binding.root;
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    //初始化布局资源
    abstract fun initData();
    abstract fun initView();


}