package com.example.qrcode.functions.createFunction.input.strategies

import com.example.qrcode.R
import com.example.qrcode.functions.createFunction.CreateType
import com.example.qrcode.functions.createFunction.input.strategies.BaseAppStrategy

class InstagramStrategy: BaseAppStrategy(
    appName = "instagram",
    appIcon = R.mipmap.ic_instagram,
    appTabs = listOf("Username","URL"),
    appTabHints = mapOf("Username" to "Enter Instagram username","URL" to "Enter Instagram URL"),
    openUrl = "https://www.instagram.com"
) {
    override val type: CreateType  = CreateType.Instagram
}