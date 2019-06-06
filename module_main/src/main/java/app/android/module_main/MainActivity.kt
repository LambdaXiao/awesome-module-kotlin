package app.android.module_main

import android.os.Bundle
import app.android.module_base.base.BaseActivity
import app.android.module_base.constant.ARouterPath
import com.alibaba.android.arouter.launcher.ARouter
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        tv_main.setOnClickListener { v ->
            ARouter.getInstance().build(ARouterPath.MODULELOGIN_LOGINACTIVITY).navigation()
        }
    }
}
