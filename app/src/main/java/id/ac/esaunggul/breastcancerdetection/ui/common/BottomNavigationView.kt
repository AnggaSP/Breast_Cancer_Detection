package id.ac.esaunggul.breastcancerdetection.ui.common

import android.content.Context
import android.util.AttributeSet
import com.google.android.material.bottomnavigation.BottomNavigationView

/**
 * Material [BottomNavigationView] without the built-in insets listener.
 *
 * @see [BottomNavigationView] for the full explanation of what this class does.
 */
class BottomNavigationView : BottomNavigationView {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    init {
        setOnApplyWindowInsetsListener(null)
    }
}