package com.example.qrcode.functions.createFunction.input.strategies

import com.example.qrcode.R
import com.example.qrcode.functions.createFunction.CreateType
import com.example.qrcode.functions.createFunction.input.strategies.BaseAppStrategy

class FacebookStrategy: BaseAppStrategy(
    appName = "facebook",
    appIcon = R.mipmap.ic_facebook,
    appTabs = listOf("Facebook ID","URL"),
    appTabHints = mapOf("Facebook ID" to "Enter Facebook ID","URL" to "Enter Facebook URL"),
    openUrl = "https://www.facebook.com"
) {
    override val type: CreateType = CreateType.FaceBook
}