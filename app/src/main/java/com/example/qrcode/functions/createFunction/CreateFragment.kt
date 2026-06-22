package com.example.qrcode.functions.createFunction

import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.qrcode.R
import com.example.qrcode.activity.BaseFragment
import com.example.qrcode.databinding.FragmentCreateBinding

//创建界面Fragment入口，实现应用图标跳转功能,剪贴板功能，分享功能
class CreateFragment: BaseFragment<FragmentCreateBinding>(FragmentCreateBinding::inflate){
    //新建创建应用类型集合
    private val createItemList = mutableListOf<CreateItem>()

    override fun initData() {
        createItemList.clear()
        createItemList.add(CreateItem(getString(R.string.website), CreateType.WebSite))
        createItemList.add(CreateItem(getString(R.string.wifi), CreateType.Wifi))
        createItemList.add(CreateItem(getString(R.string.text), CreateType.Text))
        createItemList.add(CreateItem(getString(R.string.contact), CreateType.Contact))
        createItemList.add(CreateItem(getString(R.string.tel), CreateType.Tel))
        createItemList.add(CreateItem(getString(R.string.email), CreateType.EMail))
        createItemList.add(CreateItem(getString(R.string.sms), CreateType.SMS))
        createItemList.add(CreateItem(getString(R.string.calendar), CreateType.Calendar))
        createItemList.add(CreateItem(getString(R.string.mycard), CreateType.MyCard))
        createItemList.add(CreateItem(getString(R.string.facebook), CreateType.FaceBook))
        createItemList.add(CreateItem(getString(R.string.instagram), CreateType.Instagram))
        createItemList.add(CreateItem(getString(R.string.whatsapp), CreateType.WhatsApp))
        createItemList.add(CreateItem(getString(R.string.youtube), CreateType.Youtube))
        createItemList.add(CreateItem(getString(R.string.twitter), CreateType.Twitter))
        createItemList.add(CreateItem(getString(R.string.spotify), CreateType.Spotify))
        createItemList.add(CreateItem(getString(R.string.paypal), CreateType.PayPal))
        createItemList.add(CreateItem(getString(R.string.viber), CreateType.Viber))
    }

    override fun initView() {
        binding.createRCView.layoutManager = GridLayoutManager(activity,3)
        binding.createRCView.adapter = CreateAdapter(activity,createItemList)
    }

}