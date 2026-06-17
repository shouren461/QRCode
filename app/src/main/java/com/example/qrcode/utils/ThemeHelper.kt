package com.example.qrcode.utils

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate

object ThemeHelper {
    private const val PREFS_NAME = "theme_settings"
    private const val KEY_IS_DARK_MODE = "is_dark_mode"

    //1,应用主题设置 ->在BaseActivity中或启动时调用
    fun applyTheme(context: Context){
        val isDark = isDarkMode(context)
        if (isDark){//如果是黑夜模式
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }else{ //如果是白天模式
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }
    //2,保存并立即应用黑夜模式
    fun setDarkMode(context: Context,isDark: Boolean){
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_IS_DARK_MODE,isDark).apply()
        //立即改变当前模式，这会导致当前Activity自动重启已应用新主题
        if (isDark){//如果是黑夜模式
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }else{ //如果是白天模式
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }
    //3,读取当前保存的状态
    fun isDarkMode(context: Context): Boolean{
        val prefs = context.getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_IS_DARK_MODE,false)  //默认关闭黑夜模式
    }
}