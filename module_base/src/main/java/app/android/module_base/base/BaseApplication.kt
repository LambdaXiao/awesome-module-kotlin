package app.android.module_base.base

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import app.android.module_base.BuildConfig
import com.alibaba.android.arouter.launcher.ARouter

/**
 *描述：Application基类
 *
 */
class BaseApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        mContext = this

        if (BuildConfig.DEBUG) {           // 这两行必须写在init之前，否则这些配置在init过程中将无效
            ARouter.openLog()    // 打印日志
            ARouter.openDebug()   // 开启调试模式(如果在InstantRun模式下运行，必须开启调试模式！线上版本需要关闭,否则有安全风险)
        }
        ARouter.init(this)// 尽可能早，推荐在Application中初始化
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    companion object {
        lateinit var mContext: Context
    }
}