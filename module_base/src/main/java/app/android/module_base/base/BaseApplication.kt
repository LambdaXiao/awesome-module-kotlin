package app.android.module_base.base

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex

/**
 *描述：Application基类
 *
 */
class BaseApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        mContext = this
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    companion object {
        lateinit var mContext: Context
    }
}