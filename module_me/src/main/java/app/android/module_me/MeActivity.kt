package app.android.module_me

import android.os.Bundle
import android.widget.Toast
import app.android.module_base.base.BaseActivity
import app.android.module_base.constant.ARouterPath
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter

@Route(path = ARouterPath.MODULEME_MEACTIVITY)
class MeActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_me)
        //注入传递的参数
        ARouter.getInstance().inject(this)
        Toast.makeText(applicationContext, intent.extras.getString("param"), Toast.LENGTH_LONG).show()
    }
}
