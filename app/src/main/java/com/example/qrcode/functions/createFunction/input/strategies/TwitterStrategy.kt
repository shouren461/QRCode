package com.example.qrcode.functions.createFunction.input.strategies

import com.example.qrcode.R
import com.example.qrcode.functions.createFunction.CreateType
import com.example.qrcode.functions.createFunction.input.strategies.BaseAppStrategy

class TwitterStrategy : BaseAppStrategy(
    appName = "twitter", appIcon = R.mipmap.ic_twitter,
    appTabs = listOf("Username","URL"),
    appTabHints = mapOf("UserName" to "Enter Twitter username","URL" to "Enter Twitter URL"),
    openUrl = "https://www.twitter.com"
){
    override val type: CreateType
        get() = CreateType.Twitter
}