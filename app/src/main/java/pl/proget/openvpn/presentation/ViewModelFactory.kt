package pl.proget.openvpn.presentation

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import pl.proget.openvpn.OpenVpnApplication

class ViewModelFactory(
    private val creators: Map<Class<out ViewModel>, () -> ViewModel>,
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val creator = creators[modelClass]
            ?: throw IllegalArgumentException("Unknown ViewModel class $modelClass")
        return creator() as T
    }
}

val Context.viewModelFactory: ViewModelFactory
    get() = (applicationContext as OpenVpnApplication).viewModelFactory
