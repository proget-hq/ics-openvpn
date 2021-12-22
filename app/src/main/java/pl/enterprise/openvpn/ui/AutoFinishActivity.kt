package pl.enterprise.openvpn.ui

import android.app.Activity
import android.os.Bundle

class AutoFinishActivity: Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        finish()
    }
}
