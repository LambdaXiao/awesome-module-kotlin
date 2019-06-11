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

    companion object {
        var mContext: Application? = null
        //获取全局Context
        fun getContext(): Application {
            return mContext!!
        }
    }

    override fun onCreate() {
        super.onCreate()
        mContext = this

        //初始化Arouter
        initARouter()
        //初始化其他第三方库
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    override fun onTerminate() {
        super.onTerminate()
        //清理Arouter注册表
        ARouter.getInstance().destroy()
    }

    private fun initARouter() {
        if (BuildConfig.DEBUG) {  // 这两行必须写在init之前，否则这些配置在init过程中将无效
            ARouter.openLog()    // 打印日志
            ARouter.openDebug()   // 开启调试模式(如果在InstantRun模式下运行，必须开启调试模式！线上版本需要关闭,否则有安全风险)
        }
        ARouter.init(this)// 尽可能早，推荐在Application中初始化
    }


}