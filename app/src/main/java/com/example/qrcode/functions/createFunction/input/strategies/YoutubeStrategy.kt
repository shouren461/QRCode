package com.example.qrcode.functions.createFunction.input.strategies

import com.example.qrcode.R
import com.example.qrcode.functions.createFunction.CreateType
import com.example.qrcode.functions.createFunction.input.strategies.BaseAppStrategy

class YoutubeStrategy: BaseAppStrategy(
    appName = "Youtube",
    appIcon = R.mipmap.ic_youtube,
    appTabs = listOf("URL","Video ID","Channel ID"),
    appTabHints = mapOf("URL" to "Enter Youtube URL","Video ID" to "Enter Youtube Video ID","Channel ID" to "Enter Youtube Channel ID"),
    openUrl = "https://www.youtube.com"
) {
    override val type: CreateType
        get() = CreateType.Youtube
}