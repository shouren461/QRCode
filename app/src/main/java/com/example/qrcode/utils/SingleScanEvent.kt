package com.example.qrcode.utils

import android.util.Log
import androidx.annotation.MainThread
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import java.util.concurrent.atomic.AtomicBoolean

object SingleScanEvent {
    class singleScanEvent<T>: MutableLiveData<T>(){
        private val mPending = AtomicBoolean(false) //使用原子布尔值，确保线程安全的读写
        @MainThread
        override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
            if (hasActiveObservers()){
                Log.w("TAG","多个观察者最后只通知一个")
            }
            super.observe(owner){status ->
                //如果状态是true则将状态修改为false,并返回true,确保扫描结果只扫描一次
                if (mPending.compareAndSet(true,false)){
                    observer.onChanged(status)
                }
            }
        }

        @MainThread
        override fun setValue(value: T?) {
            mPending.set(true)  //每次设置新数据，将状态设置为true,表示数据待消费
            super.setValue(value)
        }
        companion object{
            private const val TAG = "singleScanEvent"
        }
    }
}