package pl.proget.openvpn.ui

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import pl.proget.openvpn.R

class Marble(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : View(context, attributeSet, defStyleAttr, defStyleRes) {

    constructor(context: Context) : this(context, null)
    constructor(
        context: Context,
        attributeSet: AttributeSet?
    ) : this(context, attributeSet, 0)

    constructor(
        context: Context,
        attributeSet: AttributeSet?,
        defStyleAttr: Int
    ) : this(context, attributeSet, defStyleAttr, 0)

    init {
        setRed()
    }

    fun setGreen() {
        background = AppCompatResources.getDrawable(context, R.drawable.green_dot)
    }

    fun setRed() {
        background = AppCompatResources.getDrawable(context, R.drawable.red_dot)
    }

    fun setOrange() {
        background = AppCompatResources.getDrawable(context, R.drawable.orange_dot)
    }
}
