package com.example.qrcode.functions.createFunction.input

import com.example.qrcode.functions.createFunction.CreateType
import com.example.qrcode.functions.createFunction.input.strategies.CalendarStrategy
import com.example.qrcode.functions.createFunction.input.strategies.ContactStrategy
import com.example.qrcode.functions.createFunction.input.strategies.EmailStrategy
import com.example.qrcode.functions.createFunction.input.strategies.FacebookStrategy
import com.example.qrcode.functions.createFunction.input.strategies.InstagramStrategy
import com.example.qrcode.functions.createFunction.input.strategies.MyCardStrategy
import com.example.qrcode.functions.createFunction.input.strategies.PaypalStrategy
import com.example.qrcode.functions.createFunction.input.strategies.SMSStrategy
import com.example.qrcode.functions.createFunction.input.strategies.SpotifyStrategy
import com.example.qrcode.functions.createFunction.input.strategies.TelStrategy
import com.example.qrcode.functions.createFunction.input.strategies.TextStrategy
import com.example.qrcode.functions.createFunction.input.strategies.TwitterStrategy
import com.example.qrcode.functions.createFunction.input.strategies.ViberStrategy
import com.example.qrcode.functions.createFunction.input.strategies.WebSiteStrategy
import com.example.qrcode.functions.createFunction.input.strategies.WhatsAppStrategy
import com.example.qrcode.functions.createFunction.input.strategies.WifiStrategy
import com.example.qrcode.functions.createFunction.input.strategies.YoutubeStrategy

//策略工厂->根据类型分配对应的策略对象，它是UI层和业务逻辑层之间的桥梁
object StrategyFactory {
    //存储所有已实现的二维码策略
    private val strategies = mapOf(
        CreateType.WebSite to WebSiteStrategy(),
        CreateType.Wifi  to WifiStrategy(),
        CreateType.Text to TextStrategy(),
        CreateType.Contact to ContactStrategy(),
        CreateType.Tel to TelStrategy(),
        CreateType.EMail to EmailStrategy(),
        CreateType.SMS to SMSStrategy(),
        CreateType.Calendar  to CalendarStrategy(),
        CreateType.MyCard to MyCardStrategy(),
        CreateType.FaceBook to FacebookStrategy(),
        CreateType.Instagram  to InstagramStrategy(),
        CreateType.WhatsApp to WhatsAppStrategy(),
        CreateType.Youtube to YoutubeStrategy(),
        CreateType.Twitter to TwitterStrategy(),
        CreateType.Spotify to SpotifyStrategy(),
        CreateType.PayPal to PaypalStrategy(),
        CreateType.Viber to ViberStrategy()
    )
    //根据传入的二维码获取具体的策略类
    fun getStrategy(type: CreateType): ProtocolStrategy? {
        return strategies[type]
    }
}