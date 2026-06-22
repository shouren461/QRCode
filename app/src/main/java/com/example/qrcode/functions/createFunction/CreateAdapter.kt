package com.example.qrcode.functions.createFunction

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.example.qrcode.R
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.qrcode.activity.CreateInputActivity
import com.example.qrcode.functions.createFunction.CreateType.*

//创建页面适配器，点击图标后统一跳转到CreateInputActivity
class CreateAdapter(val context: FragmentActivity?,val itemList: List<CreateItem>): RecyclerView.Adapter<CreateAdapter.ViewHolder>(){
    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val itemName: TextView = view.findViewById(R.id.tv_createRcv)
        val itemImage: ImageView = view.findViewById(R.id.iv_createRcv)
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.create_rcv_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        val item = itemList[position]
        holder.itemName.text  = item.name
       holder.itemImage.setImageResource(getIconRes(item.type))
        //跳转到内容输入页面，渲染对应输入视图CreateInputActivity
        holder.itemView.setOnClickListener {
            val intent = Intent(context, CreateInputActivity::class.java).apply {
                putExtra("type",item.type.name)
            }
            context?.startActivity(intent)
        }
    }

    override fun getItemCount(): Int  = itemList.size
    //获取图标资源
    companion object{
        fun getIconRes(type: CreateType) : Int{
            return when(type){
                WebSite -> R.mipmap.ic_url
                Wifi -> R.mipmap.ic_wifi
                Text -> R.mipmap.ic_text
                Contact ->R.mipmap.ic_contact
                Tel ->R.mipmap.ic_tel
                EMail ->R.mipmap.ic_email
                SMS ->R.mipmap.ic_sms
                Calendar -> R.mipmap.ic_calendar
                MyCard -> R.mipmap.ic_mecard
                FaceBook ->R.mipmap.ic_facebook
                Instagram ->R.mipmap.ic_instagram
                WhatsApp ->R.mipmap.ic_whatsapp
                Youtube ->R.mipmap.ic_youtube
                Twitter ->R.mipmap.ic_twitter
                Spotify ->R.mipmap.ic_spotify
                PayPal ->R.mipmap.ic_paypal
                Viber -> R.mipmap.ic_viber
            }
        }
    }
}