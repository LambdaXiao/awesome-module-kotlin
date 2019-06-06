package app.android.module_login

import android.os.Bundle
import android.widget.TextView
import app.android.module_base.base.BaseActivity
import app.android.module_base.constant.ARouterPath
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter

@Route(path = ARouterPath.MODULELOGIN_LOGINACTIVITY)
class LoginActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val tv_login :TextView
        tv_login = findViewById(R.id.tv_login)
        tv_login.setOnClickListener { v ->
            ARouter.getInstance().build(ARouterPath.MODULEME_MEACTIVITY)
                .withString("param", "我要到个人中心页面").navigation()
        }
    }
}
