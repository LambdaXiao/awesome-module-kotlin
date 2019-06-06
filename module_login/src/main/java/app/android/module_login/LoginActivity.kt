package app.android.module_login

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import app.android.module_base.base.BaseActivity
import kotlinx.android.synthetic.main.activity_main.*

class LoginActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        tv_login.setOnClickListener { v ->

        }
    }
}
